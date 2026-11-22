# -*- coding: utf-8 -*-
import io
from docx import Document
from docx.table import Table
from docx.text.paragraph import Paragraph
from docx.oxml.ns import qn

path = r"C:\Users\LIN\Documents\软件园食堂线上点单系统.docx"
doc = Document(path)
out = io.open(r"C:\Users\LIN\IdeaProjects\design\OnlineOrder\docx_content.txt", "w", encoding="utf-8")

def iter_block_items(parent):
    body = parent.element.body
    for child in body.iterchildren():
        if child.tag == qn('w:p'):
            yield Paragraph(child, parent)
        elif child.tag == qn('w:tbl'):
            yield Table(child, parent)

for block in iter_block_items(doc):
    if isinstance(block, Paragraph):
        txt = block.text.strip()
        if txt:
            style = block.style.name if block.style else ""
            out.write(f"[P:{style}] {txt}\n")
    elif isinstance(block, Table):
        out.write("=== TABLE ===\n")
        for row in block.rows:
            cells = [c.text.strip().replace("\n", " / ") for c in row.cells]
            out.write(" | ".join(cells) + "\n")
        out.write("=== END TABLE ===\n")
out.close()
print("done")
