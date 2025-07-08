# 0.1.0
## New Features
### Translation Key Remapping
- Currently available for the "JSON Editor" only.
- Allows developers to update translation keys by editing `lang/key_mappings.json`.
- Intended for backward compatibility--developers are encouraged to update translation keys in language files and/or source code, not just in `lang/key_mappings.json`, whenever possible.

## Changes
### Translations
- Placeholders now only support either `%s` or `%n$s`, where `n` is a positive integer.
  - This update does not affect any existing translations as other placeholder formats were never used.
- Updated `json_editor.settings.restart` in `en_us.json` from "*Need to Restart*" to "*Requires Restart*".

### JSON Support
- Now supports the JSON `null` value.

### Error Report
- The error report generation is now thread-safe.
- Improved the error report naming logic to prevent accidental overwriting of existing files.

### File Loading
- Users will now be notified if an error occurs--such as an I/O error, JSON syntax error, or out-of-memory error--and an error report may be generated as needed.
- When users attempt to open a JSON file whose root element is not a JSON object or array:
  - The current file is no longer closed.
  - A warning dialog is shown instead of an error dialog.

### General
- Now requires **Java 21**.
- Updated **GSON** to **2.13.0**.
- The UI of the editing zone has been modified.

## Removal
### Translations
- Removed unused translation key:
  - `json_editor.color_picker.title`

### About Window
- Removed the buttons linked to the HTML attachments, along with their container.

### General
- Removed the plugin system.
  - A new and more powerful plugin system is planned for a future release.

## Fixes
- The language ID was not displayed in the language options.
- The values of experimental settings were reset to the defaults when experimental features were disabled.
- Typos in the dialog message shown when an error occurs during error report generation:
  - contant -> content
  - imcomplete -> incomplete
