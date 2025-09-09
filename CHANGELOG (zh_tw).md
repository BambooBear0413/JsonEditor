# 0.1.0
## 新功能
### 翻譯鍵重映射
- 目前僅「JSON編輯器」可使用。
- 允許開發者僅透過編輯`lang/key_mappings.json`即可「更新」翻譯鍵。
- 此功能的目的在於實現向後相容──開發者仍應盡可能更新語言檔案與原始碼中的翻譯鍵，而非只更新`lang/key-mappings.json`。

## 更改
### 翻譯
- 佔位符現在只允許`%s`或`%n$s`兩種形式，其中`n`是正整數。
  - 此更新不影響現存翻譯，因為其他格式的佔位符在此前從未使用過。
- 將`en_us.json`中的`json_editor.settings.restart`從「*Need to Restart*」 更改為 「*Requires Restart*」。
- 更新語言檔案的格式。
  - 現在支援2種格式：
    1. 包含`"translations"`（JSON物件，翻譯鍵值對）及其他元資料（可選）的JSON物件。
       - 目前支援的元資料僅有`"alternative_languages"`──包含零個或數個「語言ID」的JSON陣列。
       - 如果當前語言中不包含某翻譯鍵對應的翻譯，「JSON編輯器」會先於替代語言（由`"alternative_languages"`定義）中尋找，再尋找預設語言。
       - 此格式此前是JSON陣列。
    2. 格式一中`"translations"`的值。
  - 如果語言檔案的根元素是包含`"translations"`的JSON物件，且`"translations"`的值是JSON物件，則此元素會被視為格式一。
- 更新翻譯鍵：
  - `json_editor.error.invalid_root_element.title` → `json_editor.warning.invalid_root_element.title`
  - `json_editor.error.invalid_root_element` → `json_editor.warning.invalid_root_element`

### JSON支援
- 現在支援JSON空值（`null`）。

### 錯誤報告
- 錯誤報告的產生現在是執行緒安全的。
- 改進錯誤報告的命名邏輯，以避免覆寫現存檔案。

### 檔案載入
- 當錯誤（I/O錯誤、JSON格式錯誤、記憶體不足......）發生時，將會通知使用者，並在需要時產生錯誤報告。
- 當使用者嘗試開啟根元素非JSON物件或陣列的JSON檔案時：
  - 不再關閉當前檔案。
  - 顯示「警告」對話框，替代原本的「錯誤」對話框。

### 載入對話框
- 現在會保持在最上層，避免失去焦點後消失。
- 當對話框在主視窗顯示前關閉，會一併結束程式（退出碼`-1`）。

### 「關於」視窗
- 添加連結到[GitHub倉儲](https://github.com/BambooBear0413/JsonEditor)的按鈕。
- 於最底部新增一行字：「Copyright 2025 Bamboo Bear」。

### 檔案命名
- 現在僅當選擇的檔案「不存在」且其名稱不含附檔名時，會添加`.json`副檔名。
  - 先前只要檔名不以`.json`結尾，就會添加`.json`。

### 一般
- 現在需要至少**Java 21**才能執行。
- 更新**GSON**至**2.13.0**。
- 編輯區的UI已更改。

## 移除
### 翻譯
- 移除未使用的翻譯鍵：
  - `json_editor.color_picker.title`

### 「關於」視窗
- 移除連結到HTML文件的按鈕及其容器。

### 一般
- 移除插件系統。
  - 計畫在未來版本中引入更強大的插件系統。

## 修復
- 語言設定沒有顯示語言ID。
- 禁用實驗性功能時，實驗性設定會被重設為預設值。
- 產生錯誤報告的過程中發生錯誤時，顯示的對話框訊息中的拼字錯誤：
  - contant → content
  - imcomplete → incomplete
- 編輯欄位的過程中按下Ctrl+Z（復原）或Ctrl+Y（重做）可能導致不正確的行為。
