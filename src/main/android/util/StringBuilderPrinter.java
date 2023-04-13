package android.util;
/* loaded from: classes3.dex */
public class StringBuilderPrinter implements Printer {
    private final StringBuilder mBuilder;

    public StringBuilderPrinter(StringBuilder builder) {
        this.mBuilder = builder;
    }

    @Override // android.util.Printer
    public void println(String x) {
        this.mBuilder.append(x);
        int len = x.length();
        if (len <= 0 || x.charAt(len - 1) != '\n') {
            this.mBuilder.append('\n');
        }
    }
}
