package android.view;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ViewTraversalTracingStrings {
    public final String classSimpleName;
    public final String onLayout;
    public final String onMeasure;
    public final String onMeasureBeforeLayout;
    public final String requestLayoutStacktracePrefix;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewTraversalTracingStrings(View v) {
        String className = v.getClass().getSimpleName();
        this.classSimpleName = className;
        this.onMeasureBeforeLayout = getTraceName("onMeasureBeforeLayout", className, v);
        this.onMeasure = getTraceName("onMeasure", className, v);
        this.onLayout = getTraceName("onLayout", className, v);
        this.requestLayoutStacktracePrefix = "requestLayout " + className;
    }

    private String getTraceName(String sectionName, String className, View v) {
        StringBuilder out = new StringBuilder();
        out.append(sectionName);
        out.append(" ");
        out.append(className);
        v.appendId(out);
        return out.substring(0, Math.min(out.length() - 1, 126));
    }
}
