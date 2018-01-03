# Strip Html Tags filter plugin for Embulk

This plugin strips HTML tags from values of specified columns.

## Overview

* **Plugin type**: filter

## Configuration

- **columns**: column names  (array<string>, default: `[]`)

## Example

This settings strips tags on column foo and bar, leaves other columns untouched.

```yaml
in:
  type: file
  path_prefix: ./test.csv
  parser:
    type: csv
    charset: UTF-8
    delimiter: ","
    columns:
      - {name: foo, type: string}
      - {name: bar, type: string}
      - {name: baz, type: string}

filters:
  - type: strip_html_tags
    columns:
      - foo
      - bar

out:
  type: stdout
```

it converts a CSV record like this:

```csv
<a>foo</a>,<div>bar</div>,<p>baz</p>
```

into:

```
foo,bar,<p>baz</p>
```

## Build

```
$ ./gradlew gem
```
