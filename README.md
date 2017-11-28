# Strip Html Tags filter plugin for Embulk

This plugin strips HTML tags from values of specified columns.

## Overview

* **Plugin type**: filter

## Configuration

- **columns**: column names  (array<string>, default: `[]`)

## Example

This settings strips tags on column foo and bar, leaves other columns untouched.

```yaml
filters:
  - type: strip_html_tags
    columns:
      - foo
      - bar
```


## Build

```
$ rake
```
