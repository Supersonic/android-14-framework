package android.service.controls.templates;

import android.p008os.Bundle;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
public final class ToggleTemplate extends ControlTemplate {
    private static final String KEY_BUTTON = "key_button";
    private static final int TYPE = 1;
    private final ControlButton mButton;

    public ToggleTemplate(String templateId, ControlButton button) {
        super(templateId);
        Preconditions.checkNotNull(button);
        this.mButton = button;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToggleTemplate(Bundle b) {
        super(b);
        this.mButton = (ControlButton) b.getParcelable(KEY_BUTTON, ControlButton.class);
    }

    public boolean isChecked() {
        return this.mButton.isChecked();
    }

    public CharSequence getContentDescription() {
        return this.mButton.getActionDescription();
    }

    @Override // android.service.controls.templates.ControlTemplate
    public int getTemplateType() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.service.controls.templates.ControlTemplate
    public Bundle getDataBundle() {
        Bundle b = super.getDataBundle();
        b.putParcelable(KEY_BUTTON, this.mButton);
        return b;
    }
}
