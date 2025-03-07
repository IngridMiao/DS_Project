package com.example.dsfinal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WordCounter
{
	private String urlStr;
	private String content;

	public WordCounter(String urlStr)
	{
		this.urlStr = urlStr;
	}

	private String fetchContent() throws IOException 
	{ 
		URL url = new URL(this.urlStr);
		URLConnection conn = url.openConnection();
		StringBuilder retVal = new StringBuilder();

		try (InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
				String line;
				while ((line = br.readLine()) != null){
					retVal.append(line).append("\n"); 
				} 
		}
		return retVal.toString();
	}

	public int countKeyword(String keyword) throws IOException
	{
		if (content == null)
		{
			content = fetchContent();
		}

		// To do a case-insensitive search, we turn the whole content and keyword into
		// upper-case:
		content = content.toUpperCase();
		keyword = keyword.toUpperCase();

		int retVal = 0;
		int fromIdx = 0;
		int found = -1;

		while ((found = content.indexOf(keyword, fromIdx)) != -1)
		{
			retVal++;
			fromIdx = found + keyword.length();
		}

		return retVal;
	}
}
