package com.android.server.compat.overrides;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class XmlWriter implements Closeable {
    public PrintWriter out;
    public StringBuilder outBuffer = new StringBuilder();
    public int indent = 0;
    public boolean startLine = true;

    public XmlWriter(PrintWriter printWriter) {
        this.out = printWriter;
    }

    public final void printIndent() {
        for (int i = 0; i < this.indent; i++) {
            this.outBuffer.append("    ");
        }
        this.startLine = false;
    }

    public void print(String str) {
        String[] split = str.split("\n", -1);
        int i = 0;
        while (i < split.length) {
            if (this.startLine && !split[i].isEmpty()) {
                printIndent();
            }
            this.outBuffer.append(split[i]);
            i++;
            if (i < split.length) {
                this.outBuffer.append("\n");
                this.startLine = true;
            }
        }
    }

    public void increaseIndent() {
        this.indent++;
    }

    public void decreaseIndent() {
        this.indent--;
    }

    public void printXml() {
        this.out.print(this.outBuffer.toString());
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        PrintWriter printWriter = this.out;
        if (printWriter != null) {
            printWriter.close();
        }
    }

    public static void write(XmlWriter xmlWriter, Overrides overrides) throws IOException {
        xmlWriter.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        if (overrides != null) {
            overrides.write(xmlWriter, "overrides");
        }
        xmlWriter.printXml();
    }
}
