package com.example.dsfinal.service;

import java.util.HashMap;
import java.util.Map;

public class KeywordWeights {
    private Map<String, Double> keywordWeights;

    public KeywordWeights() {
        keywordWeights = new HashMap<>();
        // 設定查詢關鍵字的權重
        keywordWeights.put("政大", 1.0);
        keywordWeights.put("nccu", 1.0);
        keywordWeights.put("NCCU", 1.0);
        keywordWeights.put("鬼故事", 1.0);
        keywordWeights.put("靈異", 1.0);
        keywordWeights.put("校園", 1.0);

        // 設定隱藏式字典關鍵字的權重
        keywordWeights.put("恐怖", 0.5);
        keywordWeights.put("陰森", 0.5);
        keywordWeights.put("驚悚", 0.5);
        keywordWeights.put("嚇人", 0.5);
        keywordWeights.put("屍水", 1.0);  // 這實在是太噁心了

        // 可以根據需求添加更多關鍵字及其權重
        keywordWeights.put("宿舍", 0.8);
        keywordWeights.put("八卦樓", 0.8);
        keywordWeights.put("八角亭", 0.8);
        keywordWeights.put("綜院", 0.8);
        
        keywordWeights.put("學長", 0.3);
        keywordWeights.put("學姊", 0.3);
        keywordWeights.put("學姐", 0.3);
        keywordWeights.put("外籍生", 0.3);
        keywordWeights.put("上吊", 0.3);
        keywordWeights.put("自殺", 0.3);
    }

    public Double getWeight(String keyword) {
        return keywordWeights.getOrDefault(keyword, 0.01); // 預設權重為0.1
    }

    public void addKeyword(String keyword, Double weight) {
        keywordWeights.put(keyword, weight);
    }
}
