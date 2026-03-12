package com.heimhel.notes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterNode {
    public String op; // "and" | "or" | null (if single filter)
    public List<FilterNode> filters;
    public String field;   // if leaf:  "address.city"
    public String operator; // if leaf: "eq","ne","gt","lt","gte","lte","like","in"
    public Object value;
}
