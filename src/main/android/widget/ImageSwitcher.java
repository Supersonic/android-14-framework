package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
/* loaded from: classes4.dex */
public class ImageSwitcher extends ViewSwitcher {
    public ImageSwitcher(Context context) {
        super(context);
    }

    public ImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageResource(int resid) {
        ImageView image = (ImageView) getNextView();
        image.setImageResource(resid);
        showNext();
    }

    public void setImageURI(Uri uri) {
        ImageView image = (ImageView) getNextView();
        image.setImageURI(uri);
        showNext();
    }

    public void setImageDrawable(Drawable drawable) {
        ImageView image = (ImageView) getNextView();
        image.setImageDrawable(drawable);
        showNext();
    }

    @Override // android.widget.ViewSwitcher, android.widget.ViewAnimator, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ImageSwitcher.class.getName();
    }
}
