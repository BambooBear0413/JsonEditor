# Bamboo Bear's JSON Editor
Version: 0.1.0 (Beta)

**Bamboo Bear's JSON Editor** ("JSON Editor") is a Java program used for editing JSON files.

\*More information can be found in the attached HTML file.

## Java
**Java 21** is required to run the "JSON Editor".
- [Oracle Java](https://www.oracle.com/java/technologies/downloads/)
- [Eclipse Temurin Java (Adoptium)](https://adoptium.net/temurin/releases/?version=21)

If your computer already has Java installed, or you have just installed it, you can open a command line tool, such as Command Prompt (cmd) in Windows, and run the command `java -version` to check the Java version.

## Supported Language
- English (US) (`en_us`) *Default Language
- 繁體中文（台灣） (`zh_tw`)
- 简体中文（中国大陆） (`zh_cn`)

## Settings (`settings.properties`)
The settings of the "JSON Editor" are saved in the file `settings.properties`. All settings in the file are listed below:

| Key                             | Type             | Default Value | Notes                                                                                                                                                                                   |
|---------------------------------|------------------|:-------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `language`                      | language code    |    `en_us`    |                                                                                                                                                                                         |
| `isExperimentalFeaturesEnabled` | Boolean          |    `false`    | Enable experimental features. The change will take effect after restarting the program.                                                                                                 |
| `fontFamily`                    | font family name |   `Dialog`    | `Dialog` is the name of the Java default font family. <b style="color:red">Experimental Feature</b>                                                                                     |
| `fontSize`                      | integer          |     `15`      | Font Size. The minimum value is `8`, and the maximum value is `28`                                                                                                                      |
| `enablesPrettyPrinting`         | Boolean          |    `false`    | After enabling pretty printing, the JSON file saved by the "JSON Editor" will be more readable, but the file size will be slightly larger compared to when pretty printing is disabled. |
| `enablesHtmlEscaping`           | Boolean          |    `false`    | After enabling HTML escaping, characters with special meaning in HTML, such as `=`,`>`, and `<`, will be escaped when saving the file.                                                  |
| `enablesUnicodeEscaping`        | Boolean          |    `false`    | After enabling Unicode escaping, non-ASCII characters will be escaped when saving the file. <b style="color:red">Experimental Feature</b>                                               |

All settings listed above can be changed in the "JSON Editor" (some cannot only be changed if experimental features are disabled) or edited by other application (not recommended).

## Third-Party Open Source Libraries
### Gson
Version: 2.13.0<br>
Copyright 2008 Google Inc.

A Java library developed by Google that serializes Java objects to JSON, and deserializes JSON to Java objects (Java objects <-> JSON).<br>
In "JSON Editor", we used Gson to process JSON.

License: [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)<br>
See more: [https://github.com/google/gson](https://github.com/google/gson)

## Credits
- Author: Bamboo Bear (Chinese: 竹子熊)
- Translation Assistance ("JSON Editor"): RandomWill (`zh_cn`)