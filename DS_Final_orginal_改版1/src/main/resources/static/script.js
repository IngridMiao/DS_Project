function performSearch() {
    const query = document.getElementById("queryInput").value;
    const resultsContainer = document.getElementById("results");
    resultsContainer.innerHTML = "";  // 清空舊結果

    if (!query) {
        resultsContainer.innerHTML = "<p>請輸入查詢關鍵字</p>";
        return;
    }

    fetch(`/api/boogle?query=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
            if (Object.keys(data).length === 0) {
                resultsContainer.innerHTML = "<p>查無結果</p>";
                return;
            }
            
            // 將查詢結果動態加入頁面
            for (const [title, url] of Object.entries(data)) {
                const resultItem = document.createElement("div");
                resultItem.className = "result-item";
                resultItem.innerHTML = `<a href="${url}" target="_blank">${title}</a>`;
                resultsContainer.appendChild(resultItem);
            }
        })
        .catch(error => {
            console.error("Error fetching search results:", error);
            resultsContainer.innerHTML = "<p>查詢過程中出現錯誤，請稍後再試</p>";
        });
}