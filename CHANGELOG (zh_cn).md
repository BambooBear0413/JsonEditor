# 0.1.0
## 新功能
### 翻译键重映射
- 目前仅“JSON编辑器”可使用。
- 允许开发者仅透过`lang/key_mappings.json`即可“更新”翻译键。
- 此功能的目的在于实现向后兼容——开发者仍应尽可能更新语言文件与原始码中的翻译键，而非只更新`lang/key-mappings.json`。

## 变更
### 翻译
- 占位符现在只允许`%s`或`%n$s`两种形式，其中`n`时正整数。
  - 此更新不影响现存翻译，因为其他格式的占位符在此前从未使用过。
- 将`en_us.json`中的`json_editor.settings.restart`从“*Need to Restart*”变更为“*Requires Restart*”。
- 更新语言文件的格式。
  - 现在支援2种格式：
    1. 包含`"translations"`JSON对象，翻译键值对）及其他元数据（可选）的JSON对象。
       - 目前支援的元数据只有`"alternative_languages"`——包含零个或数个「语言ID」的JSON数组。
       - 如果当前语言中不包含某翻译键对应的翻译，“JSON编辑器”会先于替代语言（由`"alternative_langauges"`定义）中寻找，在寻找默认语言。
       - 此格式此前是JSON数组。
    2. 格式一中`"translations"`的值。
  - 如果语言文件的根元素时包含`"translations"`的JSON对象，且`"translations"`的值是JSON对象，则此元素会被视为格式一。
- 更新翻译键：
  - `json_editor.error.invalid_root_element.title` → `json_editor.warning.invalid_root_element.title`
  - `json_editor.error.invalid_root_element` → `json_editor.warning.invalid_root_element`

### JSON支援
- 现在支援JSON空值（`null`）。

### 错误报告
- 错误报告的产生现在是线程安全的。
- 开进错误报告的命名逻辑，以避免覆写现存文件。

### 文件加载
- 当错误（I/O错误、JSON格式错误、内存不足......）发生时，将会同知用户，并在需要时产生错误报告。
- 当用户尝试开启根元素非JSON对象或数组的JSON文件时：
  - 不再关闭当前文件。
  - 显示“警告”对话框，替代原本的“错误”对话框。

### 加载对话框
- 现在会保持在最上层，避免失去焦点后消失。
- 当对话框在主窗口出现前关闭，会一并结束程序（退出码`-1`）。

### 「关于」窗口
- 添加链接到[GitHub仓库](https://github.com/BambooBear0413/JsonEditor)的按钮。
- 在最底部新增一行字：“Copyright 2025 Bamboo Bear”。

### 一般
- 现在需要至少**Java 21**才能执行。
- 更新**GSON**至**2.13.0**。
- 编辑区的UI已更改。

## 移除
### 翻译
- 移除未使用的翻译键：
  - `json_editor.color_picker.title`

### “关于”窗口
- 移除连接到HTML文档的按钮及其容器。

### 一般
- 移除插件系统
  - 计划在未来版本中引入更强大的插件系统。

## 修复
- 语言设置没有显示语言ID。
- 禁用实验性内容时，实验性设置会被重置为默认值。
- 生成错误报告的过程中发生错误时，现实的对话框讯息中的拼字错误：
  - contant → content
  - imcomplete → incomplete
- 编辑栏位的过程中按下Ctrl+Z（撤销）或Ctrl+Y（重做）可能导致不正确的行为。
