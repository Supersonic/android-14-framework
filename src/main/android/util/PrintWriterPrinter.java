package android.util;

import java.io.PrintWriter;
/* loaded from: classes3.dex */
public class PrintWriterPrinter implements Printer {
    private final PrintWriter mPW;

    public PrintWriterPrinter(PrintWriter pw) {
        this.mPW = pw;
    }

    @Override // android.util.Printer
    public void println(String x) {
        this.mPW.println(x);
    }
}
