package com.dayone.dividend.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ScrapedResult {

    private Company company;

    private List<Dividend> dividndEntities;

    public ScrapedResult() {
        dividndEntities = new ArrayList<>();
    }

    public ScrapedResult(Company company, List<Dividend> dividndEntities) {
        this.company = company;
        this.dividndEntities = dividndEntities;
    }
}
