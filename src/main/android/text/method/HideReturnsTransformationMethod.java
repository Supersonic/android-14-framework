package android.text.method;
/* loaded from: classes3.dex */
public class HideReturnsTransformationMethod extends ReplacementTransformationMethod {
    private static char[] ORIGINAL = {'\r'};
    private static char[] REPLACEMENT = {65279};
    private static HideReturnsTransformationMethod sInstance;

    @Override // android.text.method.ReplacementTransformationMethod
    protected char[] getOriginal() {
        return ORIGINAL;
    }

    @Override // android.text.method.ReplacementTransformationMethod
    protected char[] getReplacement() {
        return REPLACEMENT;
    }

    public static HideReturnsTransformationMethod getInstance() {
        HideReturnsTransformationMethod hideReturnsTransformationMethod = sInstance;
        if (hideReturnsTransformationMethod != null) {
            return hideReturnsTransformationMethod;
        }
        HideReturnsTransformationMethod hideReturnsTransformationMethod2 = new HideReturnsTransformationMethod();
        sInstance = hideReturnsTransformationMethod2;
        return hideReturnsTransformationMethod2;
    }
}
