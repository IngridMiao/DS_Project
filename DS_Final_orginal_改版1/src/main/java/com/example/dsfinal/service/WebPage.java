package com.example.dsfinal.service;

import java.io.IOException;
import java.util.ArrayList;

public class WebPage{
	public String url;
	public String name;
	public WordCounter counter;
	public double score;

	public WebPage(String url, String name){
		this.url = url;
		this.name = name;
		this.counter = new WordCounter(url);
	}

	public void setScore(ArrayList<Keyword> keywords, KeywordWeights keywordWeights) throws IOException{
		score = 0;
		// YOUR TURN
		// 1. calculate the score of this webPage
		for(Keyword keyword : keywords) {
			int num = counter.countKeyword(keyword.name);
			double weight = keywordWeights.getWeight(keyword.name);
			score = score + num * weight;
		}
		
	}
}

