package com.example.dsfinal.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebTree{
	public WebNode root;
	public ArrayList<Keyword> keywords;
	public KeywordWeights keywordWeights;

	public WebTree(WebPage rootPage){
		this.root = new WebNode(rootPage);
		this.keywords = new ArrayList<>(); // 初始化關鍵字列表
		this.keywordWeights = new KeywordWeights();  // 初始化關鍵字權重
	}

	public void setPostOrderScore() throws IOException{
		setPostOrderScore(root);
	}

	private void setPostOrderScore(WebNode startNode) throws IOException{
		// YOUR TURN
		// 3. compute the score of children nodes via post-order, then setNodeScore for
		// startNode
		for(WebNode child : startNode.children) {
			setPostOrderScore(child);
		}
		startNode.setNodeScore(keywords, keywordWeights);

	}

	public void buildTreeFromRoot(String rootUrl) throws IOException {
    // 抓取根節點的子網頁
    List<String> subPages = new GoogleQueryService().fetchSubPages(rootUrl);

    // 為每個子網頁建立 WebNode 並加入根節點
    for (String subPageUrl : subPages) {
        WebPage subPage = new WebPage(subPageUrl, subPageUrl); // 假設名稱與 URL 相同
        WebNode subNode = new WebNode(subPage);
        root.addChild(subNode);
    }
}


	public void eularPrintTree(){
		eularPrintTree(root);
	}

	private void eularPrintTree(WebNode startNode){
		int nodeDepth = startNode.getDepth();

		if (nodeDepth > 1)
			System.out.print("\n" + repeat("\t", nodeDepth - 1));

		System.out.print("(");
		System.out.print(startNode.webPage.name + "," + startNode.nodeScore);
		
		// YOUR TURN
		// 4. print child via pre-order
		for(WebNode child : startNode.children) {
			eularPrintTree(child);
		}
		System.out.print(")");

		if (startNode.isTheLastChild())
			System.out.print("\n" + repeat("\t", nodeDepth - 2));
	}

	private String repeat(String str, int repeat){
		String retVal = "";
		for (int i = 0; i < repeat; i++){
			retVal += str;
		}
		return retVal;
	}
}
