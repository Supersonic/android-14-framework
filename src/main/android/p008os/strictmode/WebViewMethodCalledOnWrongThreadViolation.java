package android.p008os.strictmode;
/* renamed from: android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation */
/* loaded from: classes3.dex */
public final class WebViewMethodCalledOnWrongThreadViolation extends Violation {
    public WebViewMethodCalledOnWrongThreadViolation(Throwable originStack) {
        super(null);
        setStackTrace(originStack.getStackTrace());
    }
}
