# 0.1.0
## New Features
- Now can add third-party Java Swing looks and feels via plugins. 
    - Currently, this only works on directory plugins.

## New Features (Experimental Features)
- Now can select looks and feels by a new setting called `lookAndFeel`.

## Changes
- The program now requires **Java 21**.
- Updated **GSON** to **2.13.0**.
- Modify the UI of the editing zone.
- The program now supports JSON `null`.
- The format of placeholders in translations now only allows either `%s` or `%n$s`, where `n` is a positive integer.
    - This update does not affect any existing translations since other placeholder formats had never been used before.
- Updated `json_editor.settings.restart` in `en_us.json` from "*Need to Restart*" to "*Requires Restart*".

## Fixes
- The language ID was not displayed in the language options.
- The values of experimental settings were reset to the defaults when experimental features were disabled.
- The plugin description field was editable.
