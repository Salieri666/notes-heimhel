package com.heimhel.notes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heimhel.notes.model.dto.DynamicQueryRequestDTO;
import com.heimhel.notes.model.dto.FilterNode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicQueryService {

    private final EntityManager em;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> query(DynamicQueryRequestDTO req) {
        Class<?> entityClass = resolveEntityClass(req.entity);
        Assert.notNull(entityClass, "Unknown entity: " + req.entity);

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // --- data query ---
        CriteriaQuery<?> cq = cb.createQuery(entityClass);
        Root<?> root = cq.from(entityClass);
        List<Predicate> predicates = new ArrayList<>();

        if (req.filter != null && !req.filter.isEmpty()) {
            for (FilterNode node : req.filter) {
                predicates.add(buildPredicate(node, root, cb, entityClass));
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // сортировка
        if (req.sort != null && !req.sort.isEmpty()) {
            List<Order> orders = req.sort.stream()
                    .map(s -> {
                        Path<?> path = getPath(root, s.field);
                        return "desc".equalsIgnoreCase(s.dir) ? cb.desc(path) : cb.asc(path);
                    }).collect(Collectors.toList());
            cq.orderBy(orders);
        }

        TypedQuery<?> tq = em.createQuery(cq);

        if (req.offset != null && req.offset >= 0) tq.setFirstResult(req.offset);
        if (req.limit != null && req.limit > 0) tq.setMaxResults(req.limit);

        List<?> resultList = tq.getResultList();

        // --- count query (для total) ---
        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<?> countRoot = countQ.from(entityClass);
        countQ.select(cb.count(countRoot));
        if (req.filter != null && !req.filter.isEmpty()) {
            List<Predicate> countPreds = new ArrayList<>();
            for (FilterNode node : req.filter) {
                countPreds.add(buildPredicate(node, countRoot, cb, entityClass));
            }
            countQ.where(cb.and(countPreds.toArray(new Predicate[0])));
        }
        Long total = em.createQuery(countQ).getSingleResult();

        // конвертируем сущности в Map<String,Object> (удобно для фронта)
        List<Map<String,Object>> rows = resultList.stream()
                .map(o -> objectMapper.convertValue(o, new TypeReference<Map<String,Object>>(){}))
                .collect(Collectors.toList());

        Map<String,Object> resp = new HashMap<>();
        resp.put("total", total);
        resp.put("rows", rows);
        return resp;
    }

    // Рекурсивное построение предиката из FilterNode
    private Predicate buildPredicate(FilterNode node, From<?,?> root, CriteriaBuilder cb, Class<?> entityClass) {
        if (node == null) return cb.conjunction();

        // логический узел (and/or)
        if (node.op != null && (node.filters != null && !node.filters.isEmpty())) {
            List<Predicate> child = node.filters.stream()
                    .map(f -> buildPredicate(f, root, cb, entityClass))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if ("or".equalsIgnoreCase(node.op)) {
                return cb.or(child.toArray(new Predicate[0]));
            } else { // default and
                return cb.and(child.toArray(new Predicate[0]));
            }
        }

        // leaf: поле + оператор + значение
        if (node.field == null || node.operator == null) return cb.conjunction();

        Path<?> path = getPath(root, node.field);
        // защита: убедимся, что путь действительно существует - getPath бросит исключение, если нет

        String op = node.operator.toLowerCase(Locale.ROOT);
        Object val = node.value;

        switch (op) {
            case "eq":
                return val == null ? cb.isNull(path) : cb.equal(path, castValue(path, val));
            case "ne":
                return val == null ? cb.isNotNull(path) : cb.notEqual(path, castValue(path, val));
            case "gt":
                return cb.greaterThan((Expression)path, (Comparable)castValue(path, val));
            case "gte":
                return cb.greaterThanOrEqualTo((Expression)path, (Comparable)castValue(path, val));
            case "lt":
                return cb.lessThan((Expression)path, (Comparable)castValue(path, val));
            case "lte":
                return cb.lessThanOrEqualTo((Expression)path, (Comparable)castValue(path, val));
            case "like":
                return cb.like(cb.lower((Expression<String>)path), "%" + val.toString().toLowerCase() + "%");
            case "in":
                if (val instanceof Collection) {
                    CriteriaBuilder.In<Object> in = cb.in(path);
                    for (Object v : (Collection<?>)val) in.value(castValue(path, v));
                    return in;
                } else {
                    return cb.equal(path, castValue(path, val));
                }
            default:
                throw new IllegalArgumentException("Unknown operator: " + node.operator);
        }
    }

    // Получение Path, поддерживает вложенные поля через точку (relation.field)
    private Path<?> getPath(From<?,?> root, String fieldPath) {
        Assert.hasText(fieldPath, "fieldPath required");

        String[] parts = fieldPath.split("\\.");
        Path<?> path = root;
        From<?,?> current = root;
        for (String part : parts) {
            // проверим через метамодель, что атрибут существует — защитит от инъекций через имя поля
            Metamodel mm = em.getMetamodel();
            EntityType<?> et = mm.entity(current.getJavaType());
            boolean ok = et.getAttributes().stream().anyMatch(a -> a.getName().equals(part));
            if (!ok) {
                throw new IllegalArgumentException("Unknown attribute '" + part + "' on " + current.getJavaType().getSimpleName());
            }
            path = path.get(part);
            // если это association и нужен join — можно преобразовать в Join, но для простоты используем path.get
            // (если понадобится join для fetch — расширим)
        }
        return path;
    }

    // Попытаться привести value к типу поля Path (примитивы, enum, UUID, Date, Integer, Long,...)
    private Object castValue(Path<?> path, Object value) {
        // простая (но полезная) реализация: приведём с помощью targetClass через ObjectMapper
        Class<?> target = path.getJavaType();
        if (value == null) return null;
        // если уже того же типа
        if (target.isInstance(value)) return value;
        try {
            return objectMapper.convertValue(value, target);
        } catch (Exception ex) {
            // fallback: строковое представление
            return value;
        }
    }

    // Разрешить имя сущности в Class<?> (по имени JPA entity name или простому имени класса)
    private Class<?> resolveEntityClass(String entityName) {
        if (entityName == null) return null;

        Metamodel mm = em.getMetamodel();
        for (EntityType<?> e : mm.getEntities()) {
            if (entityName.equals(e.getName()) || entityName.equals(e.getJavaType().getSimpleName())) {
                return e.getJavaType();
            }
        }
        return null;
    }
}
