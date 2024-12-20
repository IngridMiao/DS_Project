package com.example.dsfinal.controller;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.dsfinal.service.GoogleQueryService;


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
        //使用GoogleQueryService進行查詢
        //queryService = new GoogleQueryService(query);
        try{
            HashMap<String, String> results = queryService.query(query);
            model.addAttribute("results", results);  //添加查詢結果到模型中
        }catch(IOException e){
            e.printStackTrace();
            model.addAttribute("error","查詢過程中出現錯誤");
        }
        
        return "result";  //返回結果頁面
    }
    
    //API端點，返回JSON格式的查詢結果
    @GetMapping("/api/boogle")
    @ResponseBody
    public HashMap<String, String> apiSearch(@RequestParam("query") String query){
        //queryService = new GoogleQueryService(query);
        try{
            return queryService.query(query); //返回查詢結果作為JSON格式
        }catch(IOException e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
}
