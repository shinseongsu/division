package com.dayone.dividend.scheduler;

import com.dayone.dividend.model.Company;
import com.dayone.dividend.model.ScrapedResult;
import com.dayone.dividend.model.constants.CacheKey;
import com.dayone.dividend.persist.CompanyRepository;
import com.dayone.dividend.persist.DividendRepository;
import com.dayone.dividend.persist.entity.CompanyEntity;
import com.dayone.dividend.persist.entity.DividendEntity;
import com.dayone.dividend.scraper.Scraper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableCaching
@RequiredArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping schedule is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for (var company : companies) {
            log.info("scraping scheduler is started -> " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(new Company(company.getName(), company.getTicker()));

            // 스크래핑한 배당금 중 데이터베이스에 없는 값 저장
            scrapedResult.getDividndEntities().stream()
                // 디비든 모델을 디비든 엔테티로 매핑
                .map(e -> new DividendEntity(company.getId(), e))
                // 엘리먼트를 하나씨 디비든 레파지토리에 삽입
                .forEach(e -> {
                    boolean exist = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                    if (!exist) {
                        this.dividendRepository.save(e);
                        log.info("insert new dividen -> " + e.toString());
                    }
                });

            // 연속적인 스크래핑 대상 사이트 서버에 요청을 날리지 않도로 일시정지
            try {
                Thread.sleep(3000); // 3초 정지
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
