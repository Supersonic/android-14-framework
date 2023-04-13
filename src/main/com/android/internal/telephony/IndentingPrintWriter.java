package com.android.internal.telephony;

import java.io.Writer;
@Deprecated
/* loaded from: classes.dex */
public class IndentingPrintWriter extends AndroidUtilIndentingPrintWriter {
    public IndentingPrintWriter(Writer writer, String str) {
        super(writer, str, -1);
    }

    public IndentingPrintWriter(Writer writer, String str, int i) {
        super(writer, str, i);
    }

    public IndentingPrintWriter(Writer writer, String str, String str2, int i) {
        super(writer, str, str2, i);
    }

    @Override // com.android.internal.telephony.AndroidUtilIndentingPrintWriter
    public IndentingPrintWriter setIndent(String str) {
        super.setIndent(str);
        return this;
    }

    @Override // com.android.internal.telephony.AndroidUtilIndentingPrintWriter
    public IndentingPrintWriter setIndent(int i) {
        super.setIndent(i);
        return this;
    }

    @Override // com.android.internal.telephony.AndroidUtilIndentingPrintWriter
    public IndentingPrintWriter increaseIndent() {
        super.increaseIndent();
        return this;
    }

    @Override // com.android.internal.telephony.AndroidUtilIndentingPrintWriter
    public IndentingPrintWriter decreaseIndent() {
        super.decreaseIndent();
        return this;
    }

    public IndentingPrintWriter printPair(String str, Object obj) {
        super.print(str, obj);
        return this;
    }

    public IndentingPrintWriter printPair(String str, Object[] objArr) {
        super.print(str, objArr);
        return this;
    }

    public IndentingPrintWriter printHexPair(String str, int i) {
        super.printHexInt(str, i);
        return this;
    }
}
