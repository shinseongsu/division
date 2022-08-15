package com.dayone.dividend.web;

import com.dayone.dividend.model.ScrapedResult;
import com.dayone.dividend.service.FinanceService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    private final ObjectMapper objectMapper;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        ScrapedResult result = objectMapper.convertValue(this.financeService.getDividendByCompanyName(companyName), new TypeReference<ScrapedResult>() {});
        return ResponseEntity.ok(result);
    }

}
