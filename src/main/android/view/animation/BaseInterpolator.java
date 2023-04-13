package android.view.animation;
/* loaded from: classes4.dex */
public abstract class BaseInterpolator implements Interpolator {
    private int mChangingConfiguration;

    public int getChangingConfiguration() {
        return this.mChangingConfiguration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setChangingConfiguration(int changingConfiguration) {
        this.mChangingConfiguration = changingConfiguration;
    }
}
