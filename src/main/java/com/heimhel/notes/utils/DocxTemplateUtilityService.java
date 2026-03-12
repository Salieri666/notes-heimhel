package com.heimhel.notes.utils;

import jakarta.xml.bind.JAXBElement;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.finders.RangeFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.ComplexFieldLocator;
import org.docx4j.model.fields.FieldRef;
import org.docx4j.model.fields.FieldsPreprocessor;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.vml.CTTextbox;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocxTemplateUtilityService {

    public void fillTablesInTemplate(WordprocessingMLPackage wordMLPackage, Map<String, Object> placeholders) {
        try {
            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

            placeholders.forEach((paramName, paramValue) -> {
                if (paramValue instanceof List) {
                    try {
                        List<Map<String, String>> params =
                            ((List<?>) paramValue).stream()
                                .map(item -> {
                                        Map<String, String> mapa = new HashMap<>();
                                        ((Map) item).forEach((key, value) ->
                                            mapa.put(key.toString(), value.toString())
                                        );
                                        return mapa;
                                    }
                                ).toList();
                        boolean success = BookmarkTableMerge.builder()
                            .bookmarkName(paramName)
                            .dataList(params)
                            .build()
                            .merge(documentPart);

                        if (!success) {
                            log.warn("Table merge was not successful for parameter {}",
                                paramName);
                        }
                    } catch (Exception e) {
                        log.error("Error processing table for parameter {}: {}", paramName, e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            log.error("Error filling tables in template: {}", e.getMessage());
            throw new RuntimeException("Table filling failed", e);
        }
    }

    public void fillTemplate(WordprocessingMLPackage wordMLPackage, Map<String, Object> placeholders) {
        try {
            MailMerger.setMERGEFIELDInOutput(MailMerger.OutputField.DEFAULT);
            Map<DataFieldName, String> simplePlaceholders = new HashMap<>();
            placeholders.forEach((key, value) -> simplePlaceholders.put(new DataFieldName(key), value.toString()));

            MailMerger.performMerge(wordMLPackage, simplePlaceholders, true);

        } catch (Exception e) {
            log.error("Error filling template with placeholders: {}", e.getMessage());
            throw new RuntimeException("Template filling failed", e);
        }
    }

    public WordprocessingMLPackage loadTemplateFromSource(InputStream inputStream) throws Docx4JException {
       return WordprocessingMLPackage.load(inputStream);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Slf4j
    private static class BookmarkTableMerge {
        private String bookmarkName;
        private List<Map<String, String>> dataList;

        public boolean merge(MainDocumentPart documentPart) throws Exception {
            // Находим все закладки в документе
            RangeFinder bf = new RangeFinder();
            new TraversalUtil(documentPart.getContent(), bf);

            // Находим нужные закладки
            // Имя закладки должно быть уникальным
            // данные по таблице должны быть в списке dataList
            CTBookmark startBM = bf.getStarts().stream()
                .filter(i -> i.getName().equals(bookmarkName))
                .findFirst()
                .orElse(null);

            if (startBM == null) {
                log.warn("Start bookmark {} not found", bookmarkName);
                return false;
            }

            // Находим родительскую таблицу
            Tbl table = (Tbl) startBM.getParent();
            if (table == null) {
                log.warn("Parent table not found for bookmark {}", bookmarkName);
                return false;
            }

            Tr templateRow = null;
            //В контексте таблицы ищем последнюю строку, в ней находятся данные для заполнения
            for (int i = table.getContent().size() - 1; i >= 0; i--) {
                if (table.getContent().get(i) instanceof Tr) {
                    templateRow = (Tr) table.getContent().get(i);
                    break;
                }
            }

            if (templateRow == null) {
                log.warn("Template row not found for bookmark {}", bookmarkName);
                return false;
            }

            // Получаем индекс шаблонной строки
            // для вставки последующих строк
            int templateRowIndex = table.getContent().indexOf(templateRow);

            try {
                // Для каждого набора данных создаем новую строку
                for (Map<String, String> rowData : dataList) {
                    // Клонируем шаблонную строку
                    Tr newRow = XmlUtils.deepCopy(templateRow);

                    // Заменяем MERGEFIELD в каждой ячейке новой строки
                    for (Object tc : newRow.getContent()) {
                        if (((JAXBElement) tc).getValue() instanceof Tc) {
                            Tc cell = (Tc) ((JAXBElement) tc).getValue();
                            for (Object p : cell.getContent()) {

                                Body shell = Context.getWmlObjectFactory().createBody();
                                shell.getContent().addAll(cell.getContent());
                                Body shellClone = XmlUtils.deepCopy(shell);

                                // find fields
                                ComplexFieldLocator fl = new ComplexFieldLocator();
                                new TraversalUtil(shellClone, fl);

                                // canonicalise and setup fieldRefs
                                List<FieldRef> fieldRefs = new ArrayList<>();
                                canonicaliseStarts(fl, fieldRefs);
                                for (FieldRef fr : fieldRefs) {
                                    String instr = extractInstr(fr.getInstructions());
                                    if (instr == null) {
                                        log.warn("No instructions found in this field");
                                        continue;
                                    }
                                    if (fr.getFldName().equals("MERGEFIELD")) {
                                        String datafieldName = getDatafieldNameFromInstr(instr);
                                        String val = rowData.get(datafieldName);

                                        //Если значения нет, то поля не удаляем.
                                        if (!StringUtils.isBlank(val)) {
                                            fr.setResult(val);
                                            fr.getParent().getContent().remove(fr.getBeginRun());
                                            fr.getParent().getContent().remove(fr.getEndRun());
                                        }
                                    }
                                }
                                //очищаем ячейку от старых данных и добавляем новые без MERGEFIELD
                                cell.getContent().clear();
                                cell.getContent().addAll(shellClone.getContent());
                            }
                        }
                    }
                    //в таблицу добавляем новую строку.
                    table.getContent().add(templateRowIndex + 1, newRow);
                    templateRowIndex++;
                }

                // Удаляем шаблонную строку (она была первой в таблице и больше не нужна)
                table.getContent().remove(templateRow);

                return true;
            } catch (Exception e) {
                log.error("Error while merging table: {}", e.getMessage());
                return false;
            }
        }

        private String extractInstr(List<Object> instructions) {
            if (instructions.size() != 1) {
                log.warn("MERGEFIELD field contained complex instruction; attempting to process");
                StringBuffer sb = new StringBuffer();
                for (Object i : instructions) {
                    i = XmlUtils.unwrap(i);
                    if (i instanceof Text) {
                        String t = ((Text) i).getValue();
                        log.debug(t);
                        sb.append(t);
                    } else {
                        log.warn("Failed: non Text object encountered.");
                        log.debug(XmlUtils.marshaltoString(i, true, true));
                        return null;
                    }
                }
                return sb.toString();
            }

            Object o = XmlUtils.unwrap(instructions.get(0));
            if (o instanceof Text) {
                return ((Text) o).getValue();
            } else {
                if (log.isErrorEnabled()) {
                    log.error("TODO: extract field name from " + o.getClass().getName());
                    log.error(XmlUtils.marshaltoString(instructions.get(0), true, true));
                }
                return null;
            }
        }

        private String getDatafieldNameFromInstr(String instr) {
            String tmp = instr.substring(instr.indexOf("MERGEFIELD") + 10);
            tmp = tmp.trim();
            String datafieldName = null;

            if (tmp.startsWith("\"")) {
                if (tmp.indexOf("\"", 1) > -1) {
                    datafieldName = tmp.substring(1, tmp.indexOf("\"", 1));
                } else {
                    log.warn("Quote mismatch in " + instr);
                    datafieldName = tmp.indexOf(" ") > -1 ? tmp.substring(1, tmp.indexOf(" ")) : tmp.substring(1);
                }
            } else {
                datafieldName = tmp.indexOf(" ") > -1 ? tmp.substring(0, tmp.indexOf(" ")) : tmp;
            }
            log.info("Key: '" + datafieldName + "'");

            return datafieldName;

        }

        private void canonicaliseStarts(ComplexFieldLocator fl,
                                        List<FieldRef> fieldRefs) throws Docx4JException {
            for (P p : fl.getStarts()) {
                int index;
                if (p.getParent() instanceof ContentAccessor) {
                    // 2.8.1
                    index = ((ContentAccessor) p.getParent()).getContent().indexOf(p);
                    P newP = FieldsPreprocessor.canonicalise(p, fieldRefs);
                    newP.setParent(p.getParent());
                    if (log.isDebugEnabled()) {
                        log.debug("Canonicalised: " + XmlUtils.marshaltoString(newP, true, true));
                    }
                    ((ContentAccessor) p.getParent()).getContent().set(index, newP);
                } else if (p.getParent() instanceof List) {
                    // 3.0
                    index = ((List) p.getParent()).indexOf(p);
                    P newP = FieldsPreprocessor.canonicalise(p, fieldRefs);
                    newP.setParent(p.getParent());
                    log.debug("NewP length: " + newP.getContent().size());
                    ((List) p.getParent()).set(index, newP);
                } else if (p.getParent() instanceof CTTextbox) {
                    // 3.0.1
                    index = ((CTTextbox) p.getParent()).getTxbxContent().getContent().indexOf(p);
                    P newP = FieldsPreprocessor.canonicalise(p, fieldRefs);
                    newP.setParent(p.getParent());
                    if (log.isDebugEnabled()) {
                        log.debug("Canonicalised: " + XmlUtils.marshaltoString(newP, true, true));
                    }
                    ((CTTextbox) p.getParent()).getTxbxContent().getContent().set(index, newP);
                } else {
                    throw new Docx4JException("Unexpected parent: " + p.getParent().getClass().getName());
                }
            }
        }
    }

}
