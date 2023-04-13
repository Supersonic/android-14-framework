package android.service.controls.actions;

import android.p008os.Bundle;
/* loaded from: classes3.dex */
public final class ModeAction extends ControlAction {
    private static final String KEY_MODE = "key_mode";
    private static final int TYPE = 4;
    private final int mNewMode;

    @Override // android.service.controls.actions.ControlAction
    public int getActionType() {
        return 4;
    }

    public ModeAction(String templateId, int newMode, String challengeValue) {
        super(templateId, challengeValue);
        this.mNewMode = newMode;
    }

    public ModeAction(String templateId, int newMode) {
        this(templateId, newMode, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModeAction(Bundle b) {
        super(b);
        this.mNewMode = b.getInt(KEY_MODE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.service.controls.actions.ControlAction
    public Bundle getDataBundle() {
        Bundle b = super.getDataBundle();
        b.putInt(KEY_MODE, this.mNewMode);
        return b;
    }

    public int getNewMode() {
        return this.mNewMode;
    }
}
