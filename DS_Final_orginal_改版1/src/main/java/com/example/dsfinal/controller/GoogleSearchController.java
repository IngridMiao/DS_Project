package com.example.dsfinal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.dsfinal.service.GoogleQueryService;
import com.example.dsfinal.service.WebNode;


@Controller
public class GoogleSearchController {
    @Autowired
    private GoogleQueryService queryService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        try {
            HashMap<String, String> results = queryService.query(query);
            List<WebNode> sortedResults = queryService.sortResults(results);
            model.addAttribute("results", sortedResults);  // 添加查詢結果到模型中
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "查詢過程中出現錯誤");
        }
        return "result";  // 返回結果頁面
    }

    // API 端點，返回 JSON 格式的查詢結果
    @GetMapping("/api/boogle")
    @ResponseBody
    public HashMap<String, String> apiSearch(@RequestParam("query") String query) {
        try {
            return queryService.query(query);  // 返回查詢結果作為 JSON 格式
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

