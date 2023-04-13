package android.inputmethodservice.navigationbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/* loaded from: classes2.dex */
public class NavigationHandle extends View implements ButtonInterface {
    public NavigationHandle(Context context) {
        this(context, null);
    }

    public NavigationHandle(Context context, AttributeSet attr) {
        super(context, attr);
        setFocusable(false);
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

    @Override // android.inputmethodservice.navigationbar.ButtonInterface
    public void setImageDrawable(Drawable drawable) {
    }

    @Override // android.inputmethodservice.navigationbar.ButtonInterface
    public void setDarkIntensity(float intensity) {
    }

    @Override // android.inputmethodservice.navigationbar.ButtonInterface
    public void setDelayTouchFeedback(boolean shouldDelay) {
    }
}
