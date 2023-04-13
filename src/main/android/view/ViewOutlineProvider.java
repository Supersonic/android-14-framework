package android.view;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
/* loaded from: classes4.dex */
public abstract class ViewOutlineProvider {
    public static final ViewOutlineProvider BACKGROUND = new ViewOutlineProvider() { // from class: android.view.ViewOutlineProvider.1
        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            Drawable background = view.getBackground();
            if (background != null) {
                background.getOutline(outline);
                return;
            }
            outline.setRect(0, 0, view.getWidth(), view.getHeight());
            outline.setAlpha(0.0f);
        }
    };
    public static final ViewOutlineProvider BOUNDS = new ViewOutlineProvider() { // from class: android.view.ViewOutlineProvider.2
        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setRect(0, 0, view.getWidth(), view.getHeight());
        }
    };
    public static final ViewOutlineProvider PADDED_BOUNDS = new ViewOutlineProvider() { // from class: android.view.ViewOutlineProvider.3
        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setRect(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
        }
    };

    public abstract void getOutline(View view, Outline outline);
}
