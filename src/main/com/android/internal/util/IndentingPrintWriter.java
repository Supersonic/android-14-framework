package com.android.internal.util;

import java.io.Writer;
@Deprecated
/* loaded from: classes3.dex */
public class IndentingPrintWriter extends android.util.IndentingPrintWriter {
    public IndentingPrintWriter(Writer writer, String singleIndent) {
        super(writer, singleIndent, -1);
    }

    public IndentingPrintWriter(Writer writer, String singleIndent, int wrapLength) {
        super(writer, singleIndent, wrapLength);
    }

    public IndentingPrintWriter(Writer writer, String singleIndent, String prefix, int wrapLength) {
        super(writer, singleIndent, prefix, wrapLength);
    }

    @Override // android.util.IndentingPrintWriter
    public IndentingPrintWriter setIndent(String indent) {
        super.setIndent(indent);
        return this;
    }

    @Override // android.util.IndentingPrintWriter
    public IndentingPrintWriter setIndent(int indent) {
        super.setIndent(indent);
        return this;
    }

    @Override // android.util.IndentingPrintWriter
    public IndentingPrintWriter increaseIndent() {
        super.increaseIndent();
        return this;
    }

    @Override // android.util.IndentingPrintWriter
    public IndentingPrintWriter decreaseIndent() {
        super.decreaseIndent();
        return this;
    }

    public IndentingPrintWriter printPair(String key, Object value) {
        super.print(key, value);
        return this;
    }

    public IndentingPrintWriter printPair(String key, Object[] value) {
        super.print(key, value);
        return this;
    }

    public IndentingPrintWriter printHexPair(String key, int value) {
        super.printHexInt(key, value);
        return this;
    }
}
