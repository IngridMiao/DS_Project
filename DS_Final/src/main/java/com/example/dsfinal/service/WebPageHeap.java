package com.example.dsfinal.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

public class WebPageHeap {
    private PriorityQueue<WebNode> heap;

    public WebPageHeap(double scoreThreshold) {
        // 判斷使用 min-heap 還是 max-heap
        if (scoreThreshold >= 2.8) {
            // 使用 min-heap
            heap = new PriorityQueue<>(Comparator.comparingDouble(WebNode::getNodeScore));
        } else {
            // 使用 max-heap
            heap = new PriorityQueue<>(Comparator.comparingDouble(WebNode::getNodeScore).reversed());
        }
    }

    public void addWebNode(WebNode node) {
        heap.add(node);
    }

    public WebNode getTopNode() {
        return heap.poll();
    }

    public void buildHeap(WebTree tree) throws IOException {
        // 假設這裡有一個方法 setPostOrderScore 計算所有節點的分數
        tree.setPostOrderScore();
        traverseAndAdd(tree.root);
    }

    private void traverseAndAdd(WebNode node) {
        if (node == null) return;
        addWebNode(node);
        for (WebNode child : node.children) {
            traverseAndAdd(child);
        }
    }
}
