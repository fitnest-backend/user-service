#!/usr/bin/env python3
"""Strip all comments from Java files in service/src."""
import os
import re

SRC_ROOT = os.path.join(os.path.dirname(__file__), "src")

def strip_comments(content):
    lines = content.split('\n')
    result = []
    in_block_comment = False
    prev_was_blank = False

    for line in lines:
        stripped = line.strip()

        # Handle block comments (/* ... */ and /** ... */)
        if in_block_comment:
            if '*/' in line:
                in_block_comment = False
                # Keep anything after the closing */
                after = line[line.index('*/') + 2:]
                if after.strip():
                    result.append(after.rstrip())
                    prev_was_blank = False
            continue

        # Check for block comment start
        if stripped.startswith('/*'):
            if '*/' in stripped[2:]:
                # Single-line block comment like /* foo */
                before = line[:line.index('/*')]
                after_close = line[line.index('*/') + 2:]
                combined = before + after_close
                if combined.strip():
                    result.append(combined.rstrip())
                    prev_was_blank = False
                continue
            else:
                in_block_comment = True
                # Keep anything before the /*
                before = line[:line.index('/*')]
                if before.strip():
                    result.append(before.rstrip())
                    prev_was_blank = False
                continue

        # Standalone line comment (entire line is a comment)
        if stripped.startswith('//'):
            continue

        # Inline comment after code — remove the comment part
        # Be careful not to remove // inside strings
        clean_line = remove_inline_comment(line)

        # Collapse multiple blank lines
        if clean_line.strip() == '':
            if prev_was_blank:
                continue
            prev_was_blank = True
        else:
            prev_was_blank = False

        result.append(clean_line.rstrip())

    # Remove trailing blank lines
    while result and result[-1].strip() == '':
        result.pop()

    return '\n'.join(result) + '\n'

def remove_inline_comment(line):
    """Remove // comments that appear after code, respecting string literals."""
    in_string = False
    string_char = None
    i = 0
    while i < len(line):
        c = line[i]
        if in_string:
            if c == '\\':
                i += 2
                continue
            if c == string_char:
                in_string = False
        else:
            if c == '"' or c == '\'':
                in_string = True
                string_char = c
            elif c == '/' and i + 1 < len(line) and line[i + 1] == '/':
                # Found inline comment — strip it
                return line[:i].rstrip()
        i += 1
    return line

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        original = f.read()

    cleaned = strip_comments(original)

    if cleaned != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(cleaned)
        return True
    return False

def main():
    changed = 0
    total = 0
    for root, dirs, files in os.walk(SRC_ROOT):
        for fname in files:
            if fname.endswith('.java'):
                total += 1
                filepath = os.path.join(root, fname)
                if process_file(filepath):
                    rel = os.path.relpath(filepath, SRC_ROOT)
                    print(f"  Cleaned: {rel}")
                    changed += 1
    print(f"\nDone. Cleaned {changed}/{total} Java files.")

if __name__ == '__main__':
    main()

