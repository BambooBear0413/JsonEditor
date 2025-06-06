# 0.1.0
## 新功能
- 现在可以透过插件添加第三方Java Swing Look and Feel。
    - 目前仅支援目录插件。

## 新功能（实验性内容）
- 现在可以透过`lookAndFeel`设置选择Look and Feel。

## 变更
- 程序现在需要至少**Java 21**才能运行。
- **GSON**更新至**2.13.0**版本。
- 修改编辑区的UI。
- 程序现在支援JSON空值（`null`）。
- 翻译中的占位符现在只允许`%s`或`%n$s`（`n`是正整数）这两种形式。
    - 这不影响任何已有的翻译，因为其余形式的占位符在之前从未被使用过。
- 将`en_us.json`中的`json_editor.settings.restart`从“*Need to Restart*”变更为“*Requires Restart*”。

## 修复
- 语言设置没有显示语言ID。
- 禁用实验性内容时，实验性设置会被重置为默认值。
- 插件描述栏位可编辑。
