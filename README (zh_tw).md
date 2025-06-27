# 竹子熊的JSON編輯器
版本：0.0.0

**竹子熊的JSON編輯器**（以下簡稱「JSON編輯器」）是一款由Java寫成，用於編輯JSON檔案的程式。

※詳細內容請參閱附帶的HTML檔案。

## Java
「Json編輯器」需要至少**Java 17**才能運行。
- [Oracle Java](https://www.oracle.com/java/technologies/downloads/)
- [Eclipse Temurin Java (Adoptium)](https://adoptium.net/temurin/releases/?version=17)

如果電腦已有安裝Java，或者安裝完Java後，可以開啟命令行（Command Line），如Windows系統中的cmd，輸入命令`java -version`檢查Java版本。

※近期「JSON編輯器」的Java最低所需版本可能會升至Java 21，因此亦可考慮直接安裝Java 21。

## 「JSON編輯器」本身支援的語言
- English (US) （`en_us`） *預設語言
- 繁體中文（台灣）（`zh_tw`）
- 简体中文（中国大陆）（`zh_cn`）

## 設定（`settings.properties`）
「JSON編輯器」的設定儲存在`settings.properties`檔案中，目前設定檔中的所有設置如下：

| 鍵                               | 類型   |   預設值    | 備註                                                                       |
|---------------------------------|------|:--------:|--------------------------------------------------------------------------|
| `language`                      | 語言代碼 | `en_us`  |                                                                          |
| `isExperimentalFeaturesEnabled` | 布林值  | `false`  | 是否啟用實驗性內容。須重啟程式，變更才會生效。                                                  |
| `fontFamily`                    | 字族名稱 | `Dialog` | `Dialog`是Java預設字族的名稱。<b style="color:red">實驗性功能</b>                      |
| `fontSize`                      | 整數值  |   `15`   | 字體大小。最小值為`8`，最大值為`28`。                                                   |
| `enablesPrettyPrinting`         | 布林值  | `false`  | 相較於未啟用時，啟用此項後儲存的JSON檔案可讀性較高（經過排版），但檔案大小會稍大。                              |
| `enablesHtmlEscaping`           | 布林值  | `false`  | 啟用此項後，儲存JSON檔案時，會將`=`、`>`、`<`等於HTML中有特殊功能的字元轉成跳脫序列。                      |
| `enablesUnicodeEscaping`        | 布林值  | `false`  | 啟用此項後，儲存JSON檔案時，會將「非ASCII字元」轉成Unicode跳脫序列。<b style="color:red">實驗性功能</b> |

這些設定大多都能在「JSON編輯器」中設定（有些設定需要「啟用實驗性功能」才能於程式內修改），也可透過直接修改`settings.properties`設定（不建議）。

## 插件
「JSON編輯器」的「插件」可以是一個資料夾，或一個壓縮檔（ZIP格式，且副檔名必須為`.zip`），插件類似於Minecraft中的資料包，可在不需撰寫任何程式碼的情況下，添加部分功能或內容到「JSON編輯器」中。

礙於篇幅，更多關於插件的說明，請參閱附帶的HTML檔案。

## 第三方開源程式庫
### Gson
版本：2.10.1<br>
Copyright 2008 Google Inc.

Gson是一款Google開發（非官方產品）的Java程式庫，主要用於序列化Java物件為JSON，或反序列化JSON為Java物件（Java物件←→JSON）。<br>
「JSON編輯器」使用它來處理JSON，包含讀取與寫出。

許可協定：[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)<br>
查看更多：https://github.com/google/gson

## Credits
- 作者：竹子熊。
- 翻譯協助（「JSON編輯器」）：RandomWill （`zh_cn`）。