package com.cashcoach.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FilterDTO {

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    private String keyword;

    private String sortField; // responsible for adding fields like date, amount, name etc.

    private String sortOrder; // asc or desc

}
