package com.github.diorgejorge.controller;

import com.github.diorgejorge.crawler.CrawlerController;
import com.github.diorgejorge.crawler.CrawlerDefinition;
import com.github.diorgejorge.utils.DAOFirebase;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Diorge Jorge on 29/03/2018.
 */
public class Main {
    private static final String DATABASE_URL ="https://crawler-67104.firebaseio.com/surrenderAt20";
    public static void main(String[] args) {
        CrawlerDefinition crawlerDefinitionSurrenderAt20 = new CrawlerDefinition();
        crawlerDefinitionSurrenderAt20.setRulesRegexToNotVisit(".*\\.(bmp|gif|jpg|png)$","^(https:\\/\\/www\\.|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$");
        CrawlerController crawlerController = new CrawlerController(new DAOFirebase(DATABASE_URL),crawlerDefinitionSurrenderAt20);
        crawlerController.setMatchingWords("AVAILABLE");
        crawlerController.setMatchingWords("REWORK");
        crawlerController.setMatchingWords("RED POST");
        crawlerController.setMatchingWords("CHAMPION UPDATE");
        crawlerController.setMatchingWords("PATCH");
        crawlerController.setVisitingDeepness(10);
        try {
            crawlerController.setUrls(new URI("http://www.surrenderat20.net/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        crawlerController.start();
    }
}
