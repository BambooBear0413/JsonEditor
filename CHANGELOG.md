# 0.1.0
## New Features
### Translation Key Remapping
- Currently available for the "JSON Editor" only.
- Allows developers to update translation keys by editing `lang/key-mappings.json`.
- Intended for backward compatibility--developers are encouraged to update translation keys in language files and/or source code, not just in `lang/key_mappings.json`, whenever possible.

## Changes
### Translations
- Placeholders now only support either `%s` or `%n$s`, where `n` is a positive integer.
  - This update does not affect any existing translations as other placeholder formats were never used.
- Updated `json_editor.settings.restart` in `en_us.json` from "*Need to Restart*" to "*Requires Restart*".
- Updated the language file format.
  - 2 formats are now supported:
    1. A JSON object containing a `"translations"` key, whose value is another JSON object holding translation entries, along with optional metadata.
       - Currently, the only supported metadata is `"alternative_languages"`, an array of zero or more language IDs.
       - If a translation key is missing in the current language, the JSON Editor will attempt to retrieve it from the alternative languages (defined by `"alternative_languages"`) first, and then fall back to the default language.
       - This replaces the previous format, where the root element was a JSON array.
    2. A JSON object that directly represents the value of the `"translations"` key from Format 1.
  - If the root element of a language file is a JSON object containing a `"translations"` key with an object value, it will be considered to be in Format 1.
- Updated translation keys:
  - `json_editor.error.invalid_root_element.title` -> `json_editor.warning.invalid_root_element.title`
  - `json_editor.error.invalid_root_element` -> `json_editor.warning.invalid_root_element`

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

### Loading Dialog
- Now stays always on top to prevent it from disappearing when it loses focus.
- If the dialog is closed before the main window shows, the program will now exit (with status code `-1`).

### About Window
- Added a button linking to the [GitHub repository](https://github.com/BambooBear0413/JsonEditor).
- Added a line of text at the bottom: Copyright 2025 Bamboo Bear.

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
- Pressing Ctrl+Z (Undo) or Ctrl+Y (Redo) while editing the field could cause incorrect undo/redo behavior.
