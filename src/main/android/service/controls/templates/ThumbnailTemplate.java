package android.service.controls.templates;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.p008os.Bundle;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
public final class ThumbnailTemplate extends ControlTemplate {
    private static final String KEY_ACTIVE = "key_active";
    private static final String KEY_CONTENT_DESCRIPTION = "key_content_description";
    private static final String KEY_ICON = "key_icon";
    private static final int TYPE = 3;
    private final boolean mActive;
    private final CharSequence mContentDescription;
    private final Icon mThumbnail;

    public ThumbnailTemplate(String templateId, boolean active, Icon thumbnail, CharSequence contentDescription) {
        super(templateId);
        Preconditions.checkNotNull(thumbnail);
        Preconditions.checkNotNull(contentDescription);
        this.mActive = active;
        this.mThumbnail = thumbnail;
        this.mContentDescription = contentDescription;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThumbnailTemplate(Bundle b) {
        super(b);
        this.mActive = b.getBoolean(KEY_ACTIVE);
        this.mThumbnail = (Icon) b.getParcelable(KEY_ICON, Icon.class);
        this.mContentDescription = b.getCharSequence(KEY_CONTENT_DESCRIPTION, "");
    }

    public boolean isActive() {
        return this.mActive;
    }

    public Icon getThumbnail() {
        return this.mThumbnail;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @Override // android.service.controls.templates.ControlTemplate
    public int getTemplateType() {
        return 3;
    }

    @Override // android.service.controls.templates.ControlTemplate
    public void prepareTemplateForBinder(Context context) {
        int width = context.getResources().getDimensionPixelSize(C4057R.dimen.controls_thumbnail_image_max_width);
        int height = context.getResources().getDimensionPixelSize(C4057R.dimen.controls_thumbnail_image_max_height);
        rescaleThumbnail(width, height);
    }

    private void rescaleThumbnail(int width, int height) {
        this.mThumbnail.scaleDownIfNecessary(width, height);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.service.controls.templates.ControlTemplate
    public Bundle getDataBundle() {
        Bundle b = super.getDataBundle();
        b.putBoolean(KEY_ACTIVE, this.mActive);
        b.putObject(KEY_ICON, this.mThumbnail);
        b.putObject(KEY_CONTENT_DESCRIPTION, this.mContentDescription);
        return b;
    }
}
