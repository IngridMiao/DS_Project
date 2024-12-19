package com.example.bonus2.service;

import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.HashMap;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleQueryService {

    @Autowired
    private SearchBetterService searchBetterService;
    public HashMap<String, String> query(String searchKeyword) throws IOException {
        String url;
        try {
            String encodeKeyword = java.net.URLEncoder.encode(searchKeyword, "utf-8");
            url = "https://www.google.com/search?q=" + encodeKeyword + "&oe=utf8&num=20";
        } catch (Exception e) {
            throw new IOException("Error encoding keyword", e);
        }

        String content = fetchContent(url); 
        HashMap<String, String> retVal = new HashMap<>();
        Document doc = Jsoup.parse(content);
        Elements lis = doc.select("div").select(".kCrYT");

        for (Element li : lis) {
            try {
                String citeUrl = li.select("a").get(0).attr("href").replace("/url?q=", "");
                String title = li.select("a").get(0).select(".vvjwJb").text();
                if (title.equals("")) {
                    continue;
                }
                retVal.put(title, citeUrl);
            } catch (IndexOutOfBoundsException e) {
                // Handle exception
            }
        }
        //return retVal;
        //使用搜尋優化服務來優化搜尋結果
        return searchBetterService.betterSearchResults(retVal);
    }

    private String fetchContent(String url) throws IOException {
        StringBuilder retVal = new StringBuilder();
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");
        try (InputStream in = conn.getInputStream();
             InputStreamReader inReader = new InputStreamReader(in, "utf-8");
             BufferedReader bufReader = new BufferedReader(inReader)) {
            String line;
            while ((line = bufReader.readLine()) != null) {
                retVal.append(line);
            }
        }
        return retVal.toString();
    }
}

