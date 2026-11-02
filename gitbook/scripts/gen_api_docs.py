#!/usr/bin/env python3
"""Generate the MULLION SDK API reference (signatures + KDoc) from Kotlin sources.

Usage: python3 gitbook/scripts/gen_api_docs.py
"""

import re
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]

MODULES = [
    ("mullion-core", "core"),
    ("mullion-compose", "compose"),
    ("mullion-view", "view"),
]

# KDoc tags that end the description and start structured sections.
TAG_RE = re.compile(r"^@(param|return|property|receiver|throws|since|author|sample)\b")

DECL_START = re.compile(
    r"^\s*(?:@\w+(?:\([^)]*\))?\s*)*"
    r"(?:public\s+)?(?:final\s+|open\s+|abstract\s+|sealed\s+|data\s+|enum\s+|annotation\s+|inner\s+)*"
    r"(fun|val|var|class|interface|object|typealias)\b"
)


def strip_doc(line: str) -> str:
    line = line.strip()
    if line.startswith("/**"):
        line = line[3:]
    if line.endswith("*/"):
        line = line[:-2]
    line = line.lstrip("*").strip()
    return line


def parse_kdoc(lines: list[str]):
    """Return (description, tags) from the lines of a KDoc block."""
    body = [strip_doc(l) for l in lines]
    desc, tags, current = [], [], None
    for line in body:
        m = TAG_RE.match(line)
        if m:
            current = {"tag": m.group(1), "text": line[m.end():].strip()}
            tags.append(current)
        elif current is not None:
            if line:
                current["text"] += " " + line
        elif line:
            desc.append(line)
    return " ".join(desc).strip(), tags


def balanced(text: str) -> int:
    """Rough brace/paren balance ignoring strings — good enough for headers."""
    return text.count("(") - text.count(")") + text.count("{") - text.count("}")


def capture_declaration(src: list[str], i: int):
    """Capture annotation + declaration header starting at line i.

    Returns (annotations, header_lines, next_index).
    """
    annotations = []
    while i < len(src) and src[i].strip().startswith("@"):
        annotations.append(src[i].strip())
        i += 1
    if i >= len(src) or not DECL_START.match(src[i]):
        return annotations, [], i
    header = []
    paren = 0
    while i < len(src):
        s = src[i].rstrip()
        header.append(s)
        paren += s.count("(") - s.count(")")
        i += 1
        if paren <= 0:
            if s.endswith("{"):
                break  # body follows on next lines
            if "=" in s:
                break  # expression-bodied declaration
            if s.endswith("(") or s.endswith(","):
                continue  # multi-line signature / argument list
            break  # complete single-line declaration
    return annotations, header, i


def symbol_name(header_lines: list[str]) -> str:
    """Pick the symbol name from the FIRST line of the declaration header."""
    first = header_lines[0].strip() if header_lines else ""
    first = re.sub(r"\s+", " ", first)
    # fun first (also matches `public fun of(...)` and `val x = ...` lines with no fun)
    m = re.search(r"\bfun\s+`?([^`(<\s]+)`?\s*(?:<[^>]*>)?\s*\(", first)
    if m:
        return m.group(1)
    m = re.search(r"\b(?:class|interface|object|typealias)\s+`?([^`<\s(]+)`?", first)
    if m:
        return m.group(1)
    m = re.search(r"\b(?:val|var)\s+`?([^`=\s:]+)`?", first)
    if m:
        return m.group(1)
    return first[:60]


def clean_header(header_lines: list[str]) -> str:
    out = []
    for line in header_lines:
        line = line.rstrip()
        # drop trailing body opener
        if line.endswith("{"):
            line = line[:-1].rstrip()
        if line.strip():
            out.append(line)
    # strip leading indentation of first line
    if out:
        first = out[0]
        indent = len(first) - len(first.lstrip())
        out = [l[indent:] if len(l) > indent else l.lstrip() for l in out]
    return "\n".join(out).strip()


def fmt_tags(tags) -> tuple[str, str]:
    params, returns = [], []
    for t in tags:
        if t["tag"] in ("param", "property", "receiver"):
            parts = t["text"].split(None, 1)
            if parts:
                name = parts[0].strip("`")
                desc = parts[1] if len(parts) > 1 else ""
                params.append((name, desc))
        elif t["tag"] == "return":
            returns.append(t["text"])
    return params, returns


def process_module(module: str, out_name: str) -> str:
    root = REPO / module / "src/main/kotlin"
    files = sorted(root.rglob("*.kt"))
    by_package: dict[str, list[tuple[str, list]]] = {}
    for f in files:
        src = f.read_text(encoding="utf-8").splitlines()
        pkg = next((l.split()[1] for l in src if l.startswith("package ")), "unknown")
        symbols = []
        i = 0
        while i < len(src):
            if src[i].strip().startswith("/**"):
                j = i
                while j < len(src) and "*/" not in src[j]:
                    j += 1
                kdoc_lines = src[i:j + 1]
                desc, tags = parse_kdoc(kdoc_lines)
                ann, header, nxt = capture_declaration(src, j + 1)
                if header and "public" in " ".join(header[:3]):
                    symbols.append({
                        "annotations": ann,
                        "header": clean_header(header),
                        "name": symbol_name(header),
                        "desc": desc,
                        "tags": tags,
                        "file": f.name,
                        "documented": True,
                    })
                i = max(nxt, j + 1)
            else:
                # public declaration without KDoc: still document it (signature is the contract)
                k = i
                while k < len(src) and src[k].strip().startswith("@"):
                    k += 1
                if k < len(src) and DECL_START.match(src[k]) and "public" in src[k]:
                    ann, header, nxt = capture_declaration(src, i)
                    if header and "public" in " ".join(header[:3]):
                        symbols.append({
                            "annotations": ann,
                            "header": clean_header(header),
                            "name": symbol_name(header),
                            "desc": "",
                            "tags": [],
                            "file": f.name,
                            "documented": False,
                        })
                    i = max(nxt, i + 1)
                else:
                    i += 1
        if symbols:
            by_package.setdefault(pkg, []).append((f.name, symbols))

    lines = [f"# MULLION {out_name.upper()} API", ""]
    lines.append(f"> 本页由 `gitbook/scripts/gen_api_docs.py` 从 `{module}` 源码自动生成：")
    lines.append("> 每个公开符号给出 Kotlin 签名与释义（释义取自源码 KDoc，含 `@param` / `@return`）。")
    lines.append("")
    total = sum(len(syms) for items in by_package.values() for _, syms in items)
    lines.append(f"共 **{total}** 个公开符号，按包与源文件分组。")
    lines.append("")
    for pkg in sorted(by_package):
        lines.append(f"## 包 `{pkg}`")
        lines.append("")
        for fname, symbols in sorted(by_package[pkg], key=lambda x: x[0]):
            lines.append(f"### `{fname}`")
            lines.append("")
            for s in symbols:
                ann = "".join(f"{a}\n" for a in s["annotations"])
                lines.append(f"#### {s['name']}")
                lines.append("")
                lines.append("```kotlin")
                lines.append(ann + s["header"])
                lines.append("```")
                lines.append("")
                if s["desc"]:
                    lines.append(s["desc"])
                    lines.append("")
                else:
                    lines.append("*(源码未提供 KDoc；以签名为准。)*")
                    lines.append("")
                params, returns = fmt_tags(s["tags"])
                if params:
                    lines.append("| 参数 | 说明 |")
                    lines.append("| --- | --- |")
                    for name, d in params:
                        lines.append(f"| `{name}` | {d} |")
                    lines.append("")
                for r in returns:
                    lines.append(f"**返回**：{r}")
                    lines.append("")
    return "\n".join(lines)


def main():
    out_dir = REPO / "gitbook" / "docs" / "api"
    out_dir.mkdir(parents=True, exist_ok=True)
    mapping = {
        "mullion-core": "Core",
        "mullion-compose": "Compose",
        "mullion-view": "View",
    }
    for module, out_name in mapping.items():
        md = process_module(module, out_name)
        (out_dir / f"{module}.md").write_text(md, encoding="utf-8")
        count = md.count("\n#### ")
        print(f"{module}.md: {count} symbols")


if __name__ == "__main__":
    main()
