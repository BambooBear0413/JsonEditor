# 0.1.0
## 新功能
- 現在可以透過插件添加第三方Java Swing Look and Feel。
    - 目前僅支援資料夾插件。

## 新功能（實驗性功能）
- 現在可以透過`lookAndFeel`設定選擇Look and Feel。

## 更改
- 程式現在需要至少**Java 21**才能運行。
- **GSON**更新至**2.13.0**版本。
- 修改編輯區的UI。
- 程式現在支援JSON空值（`null`）。
- 翻譯中的佔位符現在只允許`%s`或`%n$s`（`n`是正整數）這兩種形式。
    - 這不影響任何已有的翻譯，因為其餘形式的佔位符在之前從未被使用過。
- 將`en_us.json`中的`json_editor.settings.restart`從「*Need to Restart*」 更改為 「*Requires Restart*」。

## 修復
- 語言設定沒有顯示語言ID。
- 禁用實驗性功能時，實驗性設定會被重設為預設值。
- 插件描述欄位可編輯。
