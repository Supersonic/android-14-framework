package android.util;

import java.io.PrintWriter;
/* loaded from: classes3.dex */
public interface Dumpable {
    void dump(PrintWriter printWriter, String[] strArr);

    default String getDumpableName() {
        return getClass().getName();
    }
}
