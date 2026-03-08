package com.heimhel.notes.model.entity;

import com.heimhel.notes.model.enums.TemplateType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
@Table(name = "templates")
@FieldNameConstants
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TemplateEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    private TemplateType templateType;

    @Column(name = "content")
    private String content;

}
