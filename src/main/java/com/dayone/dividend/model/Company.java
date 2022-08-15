package com.dayone.dividend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Company {

    private String ticker;
    private String name;

    public Company(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
    }
}
