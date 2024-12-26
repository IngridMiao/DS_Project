package com.example.dsfinal.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class WebPageHeap {
    private PriorityQueue<WebNode> minHeap;
    private PriorityQueue<WebNode> maxHeap;

    public WebPageHeap() {
        // 使用 min-heap 和 max-heap
        minHeap = new PriorityQueue<>(Comparator.comparingDouble(WebNode::getNodeScore));
        maxHeap = new PriorityQueue<>(Comparator.comparingDouble(WebNode::getNodeScore).reversed());
    }

    public void addWebNode(WebNode node) {
        if (node.getNodeScore() >= 2.8) {
            minHeap.add(node);
        } else {
            maxHeap.add(node);
        }
    }

    public List<WebNode> getSortedNodes() {
        List<WebNode> sortedNodes = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            sortedNodes.add(minHeap.poll());
        }
        while (!maxHeap.isEmpty()) {
            sortedNodes.add(maxHeap.poll());
        }
        return sortedNodes;
    }

    public void buildHeap(WebTree tree) throws IOException {
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

