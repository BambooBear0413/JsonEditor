# 0.1.0
## Changes
- The program now requires **Java 21**.
- Updated **GSON** to **2.13.0**.
- Modify the UI of the editing zone.
- The program now supports JSON `null`.
- The format of placeholders in translations now only allows either `%s` or `%n$s`, where `n` is a positive integer.
    - This update does not affect any existing translations since other placeholder formats had never been used before.
- Updated `json_editor.settings.restart` in `en_us.json` from "*Need to Restart*" to "*Requires Restart*".
- The program now notifies users if an error--such as an I/O error, JSON syntax error, or out-of-memory error--occurs while opening a JSON file, and may generate an error report as needed.
- The error report generation is now thread-safe.
- Improved the error report naming logic to prevent accidental overwriting of existing files.
- The program no longer "closes" the current file when users attempt to open a JSON file whose root element is not a JSON object or array.

## Removal
- The plugin system.
- Unused translations:
  - `json_editor.color_picker.title`
- The buttons in the About window that previously linked to the HTML attachments, along with their container.

## Fixes
- The language ID was not displayed in the language options.
- The values of experimental settings were reset to the defaults when experimental features were disabled.
- Typos in the dialog message shown when an error occurs during error report generation:
  - contant -> content
  - imcomplete -> incomplete
