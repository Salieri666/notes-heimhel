package com.heimhel.notes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicQueryRequestDTO {

    public String entity;
    public List<FilterNode> filter;
    public List<SortSpec> sort;
    public Integer limit;
    public Integer offset;

}
