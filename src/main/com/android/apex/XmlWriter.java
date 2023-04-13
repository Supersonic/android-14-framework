package com.android.apex;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
/* loaded from: classes4.dex */
public class XmlWriter implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private PrintWriter out;
    private StringBuilder outBuffer = new StringBuilder();
    private int indent = 0;
    private boolean startLine = true;

    public XmlWriter(PrintWriter printWriter) {
        this.out = printWriter;
    }

    private void printIndent() {
        for (int i = 0; i < this.indent; i++) {
            this.outBuffer.append("    ");
        }
        this.startLine = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void print(String code) {
        String[] lines = code.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (this.startLine && !lines[i].isEmpty()) {
                printIndent();
            }
            this.outBuffer.append(lines[i]);
            if (i + 1 < lines.length) {
                this.outBuffer.append("\n");
                this.startLine = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void increaseIndent() {
        this.indent++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void decreaseIndent() {
        this.indent--;
    }

    void printXml() {
        this.out.print(this.outBuffer.toString());
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        PrintWriter printWriter = this.out;
        if (printWriter != null) {
            printWriter.close();
        }
    }

    public static void write(XmlWriter _out, ApexInfoList apexInfoList) throws IOException {
        _out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        if (apexInfoList != null) {
            apexInfoList.write(_out, "apex-info-list");
        }
        _out.printXml();
    }
}
