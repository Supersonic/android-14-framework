package android.service.controls.actions;

import android.p008os.Bundle;
/* loaded from: classes3.dex */
public final class FloatAction extends ControlAction {
    private static final String KEY_NEW_VALUE = "key_new_value";
    private static final int TYPE = 2;
    private final float mNewValue;

    public FloatAction(String templateId, float newValue) {
        this(templateId, newValue, null);
    }

    public FloatAction(String templateId, float newValue, String challengeValue) {
        super(templateId, challengeValue);
        this.mNewValue = newValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatAction(Bundle b) {
        super(b);
        this.mNewValue = b.getFloat(KEY_NEW_VALUE);
    }

    public float getNewValue() {
        return this.mNewValue;
    }

    @Override // android.service.controls.actions.ControlAction
    public int getActionType() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.service.controls.actions.ControlAction
    public Bundle getDataBundle() {
        Bundle b = super.getDataBundle();
        b.putFloat(KEY_NEW_VALUE, this.mNewValue);
        return b;
    }
}
