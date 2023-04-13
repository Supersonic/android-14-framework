package android.text.method;
/* loaded from: classes3.dex */
public class SingleLineTransformationMethod extends ReplacementTransformationMethod {
    private static char[] ORIGINAL = {'\n', '\r'};
    private static char[] REPLACEMENT = {' ', 65279};
    private static SingleLineTransformationMethod sInstance;

    @Override // android.text.method.ReplacementTransformationMethod
    protected char[] getOriginal() {
        return ORIGINAL;
    }

    @Override // android.text.method.ReplacementTransformationMethod
    protected char[] getReplacement() {
        return REPLACEMENT;
    }

    public static SingleLineTransformationMethod getInstance() {
        SingleLineTransformationMethod singleLineTransformationMethod = sInstance;
        if (singleLineTransformationMethod != null) {
            return singleLineTransformationMethod;
        }
        SingleLineTransformationMethod singleLineTransformationMethod2 = new SingleLineTransformationMethod();
        sInstance = singleLineTransformationMethod2;
        return singleLineTransformationMethod2;
    }
}
