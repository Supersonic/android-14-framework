package com.android.internal.util;

import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import java.util.ArrayList;
/* loaded from: classes3.dex */
public class LocalLog {
    private final String mTag;
    private final int mMaxLines = 20;
    private final ArrayList<String> mLines = new ArrayList<>(20);

    public LocalLog(String tag) {
        this.mTag = tag;
    }

    /* renamed from: w */
    public void m18w(String msg) {
        synchronized (this.mLines) {
            Slog.m90w(this.mTag, msg);
            if (this.mLines.size() >= 20) {
                this.mLines.remove(0);
            }
            this.mLines.add(msg);
        }
    }

    public boolean dump(android.util.IndentingPrintWriter pw, String header) {
        synchronized (this.mLines) {
            if (this.mLines.size() <= 0) {
                return false;
            }
            if (header != null) {
                pw.println(header);
                pw.increaseIndent();
            }
            for (int i = 0; i < this.mLines.size(); i++) {
                pw.println(this.mLines.get(i));
            }
            if (header != null) {
                pw.decreaseIndent();
            }
            return true;
        }
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        synchronized (this.mLines) {
            for (int i = 0; i < this.mLines.size(); i++) {
                proto.write(2237677961217L, this.mLines.get(i));
            }
        }
        proto.end(token);
    }
}
