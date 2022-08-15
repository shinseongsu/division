package com.dayone.dividend.service;

import com.dayone.dividend.exception.impl.NoCompanyException;
import com.dayone.dividend.model.Company;
import com.dayone.dividend.model.Dividend;
import com.dayone.dividend.model.ScrapedResult;
import com.dayone.dividend.model.constants.CacheKey;
import com.dayone.dividend.persist.CompanyRepository;
import com.dayone.dividend.persist.DividendRepository;
import com.dayone.dividend.persist.entity.CompanyEntity;
import com.dayone.dividend.persist.entity.DividendEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는가?
    // 자주 변경되는 데이터 인가?
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public Object getDividendByCompanyName(String companyName) {
        log.info("search company -> {}", companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
            .orElseThrow(() -> new NoCompanyException());

        // 2. 조회된 회사 ID로 배당금 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(
            company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
            .map(e -> new Dividend(e.getDate(), e.getDividend()))
            .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName()),
            dividends);
    }
}
