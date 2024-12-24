package com.example.dsfinal.service;

import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.URL;
 import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 import org.springframework.stereotype.Service;

@Service
public class GoogleQueryService {

    //@Autowired
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
        return retVal;
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

    public List<WebNode> sortResults(HashMap<String, String> searchResults) throws IOException {
        // 創建 WebPage 列表
        List<WebPage> webPages = new ArrayList<>();
        for (Map.Entry<String, String> entry : searchResults.entrySet()) {
            WebPage page = new WebPage(entry.getValue(), entry.getKey());
            webPages.add(page);
        }

        // 創建 WebTree 並計算節點分數
        WebTree webTree = new WebTree(webPages.get(0));
        for (int i = 1; i < webPages.size(); i++) {
            webTree.root.addChild(new WebNode(webPages.get(i)));
        }
        webTree.setPostOrderScore();

        // 使用 WebPageHeap 進行排序
        WebPageHeap heap = new WebPageHeap();
        heap.buildHeap(webTree);

        // 返回排序好的節點
        return heap.getSortedNodes();
    }
}

