package com.example.dsfinal.service;

import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
 import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    //抓取子頁面連結
    public List<String> fetchSubPages(String url) throws IOException{
        List<String> subPages = new ArrayList<>();
        try{
            String content = fetchContent(url);
            Document doc = Jsoup.parse(content);
            Elements links = doc.select("a[href]");
            for(Element link:links){
                String subPageUrl = link.attr("abs:href");
                if(subPageUrl.startsWith(url) && isValidUrl(subPageUrl)){   //確保是子網頁
                    subPages.add(subPageUrl);
                }
            }
        }catch(IOException e){
            System.out.println("Error fetching sub-pages for URL: "+url);
        }
        return subPages;
    }

    //@Autowired
    public HashMap<String, String> query(String searchKeyword) throws IOException {
        String url;
        try {
            String encodeKeyword = java.net.URLEncoder.encode(searchKeyword, "utf-8");
            encodeKeyword = encodeKeyword + "鬼故事";
            
            url = "https://www.google.com/search?q=" + encodeKeyword + "&oe=utf8&num=20";
        } catch (Exception e) {
            throw new IOException("Error encoding keyword", e);
        }

        String content = fetchContent(url); 
        HashMap<String, String> retVal = new HashMap<>();
        //HashMap<String, String> searchResults = new HashMap<>();
        Document doc = Jsoup.parse(content);
        Elements lis = doc.select("div").select(".kCrYT");

        //創建根節點的WebTree
        WebPage rootPage = new WebPage(url, "Search Root");
        WebTree webTree = new WebTree(rootPage);

        for (Element li : lis) {
            try {
                String citeUrl = li.select("a").get(0).attr("href").replace("/url?q=", "");
                //citeUrl = handleSpecialCharacters(citeUrl);
                String title = li.select("a").get(0).select(".vvjwJb").text();
                if (title.equals("")) {
                    continue;
                }
                // 創建主頁面的節點
                WebPage webPage = new WebPage(citeUrl, title);
                WebNode node = new WebNode(webPage);

                // 抓取子網頁並添加到樹中
                List<String> subPages = fetchSubPages(citeUrl);
                for (String subPageUrl : subPages) {
                    if(isValidUrl(subPageUrl)){
                        WebPage subPage = new WebPage(subPageUrl, subPageUrl); // 假設名稱與 URL 相同
                        WebNode subNode = new WebNode(subPage);
                        node.addChild(subNode); // 將子節點加入到對應的主節點
                    }

                }

                // 將主節點加入到樹的根節點
                webTree.root.addChild(node);
                retVal.put(title, citeUrl);
                //searchResults.put(title, citeUrl);
            } catch (IndexOutOfBoundsException e) {
                // Handle exception
            }

            //打印樹結構以便檢查
            //webTree.eularPrintTree();
        }
        /*
         *         //抓取推薦的相關關鍵字
        List<String> relatedKeywords = new ArrayList<>();
        Elements suggestions = doc.select(".s75CSd a");
        for(Element suggestion:suggestions){
            if (!suggestion.text().isEmpty()) {
                relatedKeywords.add(suggestion.text().trim());
            }
        }

        retVal.put("searchResults", searchResults);
        retVal.put("relatedKeywords", relatedKeywords);
        
         */
        retVal = filterInvalidLinks(retVal);
        return retVal;
    }

    // 添加过滤无效链接的方法
    private HashMap<String, String> filterInvalidLinks(HashMap<String, String> results) {
        HashMap<String, String> validResults = new HashMap<>();
        for (Map.Entry<String, String> entry : results.entrySet()) {
            if (isValidUrl(entry.getValue())) {
                validResults.put(entry.getKey(), entry.getValue());
            }
        }
        return validResults;
    }

    // 检查链接是否有效
    private boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            URL u = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000); // 设置超时时间
            connection.connect();
            int responseCode = connection.getResponseCode();
            
            return responseCode >= 200 && responseCode < 400;

        } catch (Exception e) {
            return false;
        }
    }


    private String fetchContent(String urlString) throws IOException {
        StringBuilder retVal = new StringBuilder();
        try{
            URI uri = new URI(urlString);
            URL u = uri.toURL();
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
        }catch(URISyntaxException e){
            throw new IOException("Error constructing URI from URL: "+urlString);
        }

        return retVal.toString();
    }

    

    public String handleSpecialCharacters(String url){
        try{
            URI uri = new URI(url);
            return uri.toASCIIString();
        }catch(URISyntaxException e){
            try{
                return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
            }catch(Exception ex){
                return url;
            }
        }

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

