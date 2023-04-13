package android.content;
/* loaded from: classes.dex */
public class MutableContextWrapper extends ContextWrapper {
    public MutableContextWrapper(Context base) {
        super(base);
    }

    public void setBaseContext(Context base) {
        this.mBase = base;
    }
}
