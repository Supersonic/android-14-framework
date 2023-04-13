package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes4.dex */
public class ViewOverlay {
    OverlayViewGroup mOverlayViewGroup;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewOverlay(Context context, View hostView) {
        this.mOverlayViewGroup = new OverlayViewGroup(context, hostView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewGroup getOverlayView() {
        return this.mOverlayViewGroup;
    }

    public void add(Drawable drawable) {
        this.mOverlayViewGroup.add(drawable);
    }

    public void remove(Drawable drawable) {
        this.mOverlayViewGroup.remove(drawable);
    }

    public void clear() {
        this.mOverlayViewGroup.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.mOverlayViewGroup.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class OverlayViewGroup extends ViewGroup {
        ArrayList<Drawable> mDrawables;
        final View mHostView;

        OverlayViewGroup(Context context, View hostView) {
            super(context);
            this.mDrawables = null;
            this.mHostView = hostView;
            this.mAttachInfo = hostView.mAttachInfo;
            this.mRight = hostView.getWidth();
            this.mBottom = hostView.getHeight();
            this.mRenderNode.setLeftTopRightBottom(0, 0, this.mRight, this.mBottom);
        }

        public void add(Drawable drawable) {
            if (drawable == null) {
                throw new IllegalArgumentException("drawable must be non-null");
            }
            if (this.mDrawables == null) {
                this.mDrawables = new ArrayList<>();
            }
            if (!this.mDrawables.contains(drawable)) {
                this.mDrawables.add(drawable);
                invalidate(drawable.getBounds());
                drawable.setCallback(this);
            }
        }

        public void remove(Drawable drawable) {
            if (drawable == null) {
                throw new IllegalArgumentException("drawable must be non-null");
            }
            ArrayList<Drawable> arrayList = this.mDrawables;
            if (arrayList != null) {
                arrayList.remove(drawable);
                invalidate(drawable.getBounds());
                drawable.setCallback(null);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public boolean verifyDrawable(Drawable who) {
            ArrayList<Drawable> arrayList;
            return super.verifyDrawable(who) || ((arrayList = this.mDrawables) != null && arrayList.contains(who));
        }

        public void add(View child) {
            if (child == null) {
                throw new IllegalArgumentException("view must be non-null");
            }
            if (child.getParent() instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) child.getParent();
                if (parent != this.mHostView && parent.getParent() != null && parent.mAttachInfo != null) {
                    int[] parentLocation = new int[2];
                    int[] hostViewLocation = new int[2];
                    parent.getLocationOnScreen(parentLocation);
                    this.mHostView.getLocationOnScreen(hostViewLocation);
                    child.offsetLeftAndRight(parentLocation[0] - hostViewLocation[0]);
                    child.offsetTopAndBottom(parentLocation[1] - hostViewLocation[1]);
                }
                parent.removeView(child);
                if (parent.getLayoutTransition() != null) {
                    parent.getLayoutTransition().cancel(3);
                }
                if (child.getParent() != null) {
                    child.mParent = null;
                }
            }
            super.addView(child);
        }

        public void remove(View view) {
            if (view == null) {
                throw new IllegalArgumentException("view must be non-null");
            }
            super.removeView(view);
        }

        public void clear() {
            removeAllViews();
            ArrayList<Drawable> arrayList = this.mDrawables;
            if (arrayList != null) {
                Iterator<Drawable> it = arrayList.iterator();
                while (it.hasNext()) {
                    Drawable drawable = it.next();
                    drawable.setCallback(null);
                }
                this.mDrawables.clear();
            }
        }

        boolean isEmpty() {
            if (getChildCount() == 0) {
                ArrayList<Drawable> arrayList = this.mDrawables;
                if (arrayList == null || arrayList.size() == 0) {
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override // android.view.View, android.graphics.drawable.Drawable.Callback
        public void invalidateDrawable(Drawable drawable) {
            invalidate(drawable.getBounds());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            canvas.enableZ();
            super.dispatchDraw(canvas);
            canvas.disableZ();
            ArrayList<Drawable> arrayList = this.mDrawables;
            int numDrawables = arrayList == null ? 0 : arrayList.size();
            for (int i = 0; i < numDrawables; i++) {
                this.mDrawables.get(i).draw(canvas);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override // android.view.View
        public void invalidate(Rect dirty) {
            super.invalidate(dirty);
            View view = this.mHostView;
            if (view != null) {
                view.invalidate(dirty);
            }
        }

        @Override // android.view.View
        public void invalidate(int l, int t, int r, int b) {
            super.invalidate(l, t, r, b);
            View view = this.mHostView;
            if (view != null) {
                view.invalidate(l, t, r, b);
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            View view = this.mHostView;
            if (view != null) {
                view.invalidate();
            }
        }

        @Override // android.view.View
        public void invalidate(boolean invalidateCache) {
            super.invalidate(invalidateCache);
            View view = this.mHostView;
            if (view != null) {
                view.invalidate(invalidateCache);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.view.View
        public void invalidateViewProperty(boolean invalidateParent, boolean forceRedraw) {
            super.invalidateViewProperty(invalidateParent, forceRedraw);
            View view = this.mHostView;
            if (view != null) {
                view.invalidateViewProperty(invalidateParent, forceRedraw);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void invalidateParentCaches() {
            super.invalidateParentCaches();
            View view = this.mHostView;
            if (view != null) {
                view.invalidateParentCaches();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void invalidateParentIfNeeded() {
            super.invalidateParentIfNeeded();
            View view = this.mHostView;
            if (view != null) {
                view.invalidateParentIfNeeded();
            }
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void onDescendantInvalidated(View child, View target) {
            View view = this.mHostView;
            if (view != null) {
                if (view instanceof ViewGroup) {
                    ((ViewGroup) view).onDescendantInvalidated(view, target);
                    super.onDescendantInvalidated(child, target);
                    return;
                }
                invalidate();
            }
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
            if (this.mHostView != null) {
                dirty.offset(location[0], location[1]);
                if (this.mHostView instanceof ViewGroup) {
                    location[0] = 0;
                    location[1] = 0;
                    super.invalidateChildInParent(location, dirty);
                    return ((ViewGroup) this.mHostView).invalidateChildInParent(location, dirty);
                }
                invalidate(dirty);
                return null;
            }
            return null;
        }
    }
}
