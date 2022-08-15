package com.dayone.dividend.scraper;

import com.dayone.dividend.model.Company;
import com.dayone.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
