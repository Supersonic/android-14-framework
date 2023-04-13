package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import com.android.internal.C4057R;
@Deprecated
/* loaded from: classes4.dex */
public class ZoomControls extends LinearLayout {
    private final ZoomButton mZoomIn;
    private final ZoomButton mZoomOut;

    public ZoomControls(Context context) {
        this(context, null);
    }

    public ZoomControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(C4057R.layout.zoom_controls, (ViewGroup) this, true);
        this.mZoomIn = (ZoomButton) findViewById(C4057R.C4059id.zoomIn);
        this.mZoomOut = (ZoomButton) findViewById(C4057R.C4059id.zoomOut);
    }

    public void setOnZoomInClickListener(View.OnClickListener listener) {
        this.mZoomIn.setOnClickListener(listener);
    }

    public void setOnZoomOutClickListener(View.OnClickListener listener) {
        this.mZoomOut.setOnClickListener(listener);
    }

    public void setZoomSpeed(long speed) {
        this.mZoomIn.setZoomSpeed(speed);
        this.mZoomOut.setZoomSpeed(speed);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void show() {
        fade(0, 0.0f, 1.0f);
    }

    public void hide() {
        fade(8, 1.0f, 0.0f);
    }

    private void fade(int visibility, float startAlpha, float endAlpha) {
        AlphaAnimation anim = new AlphaAnimation(startAlpha, endAlpha);
        anim.setDuration(500L);
        startAnimation(anim);
        setVisibility(visibility);
    }

    public void setIsZoomInEnabled(boolean isEnabled) {
        this.mZoomIn.setEnabled(isEnabled);
    }

    public void setIsZoomOutEnabled(boolean isEnabled) {
        this.mZoomOut.setEnabled(isEnabled);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean hasFocus() {
        return this.mZoomIn.hasFocus() || this.mZoomOut.hasFocus();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ZoomControls.class.getName();
    }
}
