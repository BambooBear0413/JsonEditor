# 竹子熊的JSON编辑器
版本：0.1.0 (Beta)

**竹子熊的JSON编辑器**（以下简称“JSON编辑器”）是一款由Java写成，用于编辑JSON文件的程序。

※详细内容请参阅附带的HTML文件。

## Java
“JSON编辑器”需要至少**Java 21**才能运行。
- [Oracle Java](https://www.oracle.com/java/tectnologies/downloads)
- [Eclipse Temurin Java (Adoptium)](https://adoptium.net/temurin/releases/?version=21)

如果计算机已经有安装Java，或者安装完Java后，可以开启命令行（Command Line），如Windows系统中的cmd，输入命令`java -version`检查Java版本。

## “JSON编辑器”本身支援的语言
- English (US) （`en_us`） *默认语言
- 繁體中文（台灣）（`zh_tw`）
- 简体中文（中国大陆） (`zh_cn`) 

## 设置（`settings.properties`）
“JSON编辑器”的设置保存于`settings.properties`文件中，目前设置文件中的所有设置如下：
|键|类型|默认值|备注|
|--|--|:--:|--|
|`language`|语言代码|`en_us`||
|`isExperimentalFeaturesEnabled`|布尔值|`false`|是否启用实验性内容。须重启程序，变更才会生效。|
|`fontFamily`|字族名称|`Dialog`|`Dialog`是Java默认字族的名称。<b style="color:red">实验性内容</b>|
|`fontSize`|整数值|`15`|字体大小，最小值为`8`，最大值为`28`。|
|`enablesPrettyPrinting`|布尔值|`false`|相较于禁用时，启用此项后保存的JSON文件可读性较高（经过排版），但文件大小会稍大。|
|`enablesHtmlEscaping`|布尔值|`false`|启用此项后，保存JSON文件时，会将`=`、`>`、`<`等于HTML中有特殊功能的字符转成转义序列。|
|`enablesUnicodeEscaping`|布尔值|`false`|启用此项后，保存JSON文件时，会将“非ASCII字符”转成Unicode转义序列。<b style="color:red">实验性内容</b>|

这些设置大多都能在“JSON编辑器”中设置（有些设置需要”启用实验性内容“才能于程序内修改，也可透过直接修改`settings.properties`设置（不建议）。

## 插件
“JSON编辑器”的“插件”可以是一个文件夹，或是一个压缩文件（ZIP格式，且扩展名必须是`.zip`），插件类似于Minecraft中的数据包，可以透过不需撰写任何代码的情况下，添加部分功能或内容到“JSON编辑器”中。

碍于篇幅，更多关于插件的说明，请参阅附带的HTML文件。

## 第三方开源函式库
### Gson
版本：2.13.0<br>
Copyright 2008 Google Inc.

Gson是一款有Google开发（非官方产品）的Java函式库，主要用于序列化Java对象为JSON，或反序列化JSON为Java对象（Java对象←→JSON）。<br>
“JSON编辑器”使用它来处理JSON，包含读取于写出等。

许可证：[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)<br>
查看更多：https://github.com/google/gson

## Credits
- 作者：竹子熊。
- 翻译协助（“JSON编辑器”）：RandomWill（`zh_cn`）