package com.heimhel.notes.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortSpec {
    public String field;
    public String dir; // "asc" | "desc"
}