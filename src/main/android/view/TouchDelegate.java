package android.view;

import android.graphics.Rect;
import android.graphics.Region;
import android.util.ArrayMap;
import android.view.accessibility.AccessibilityNodeInfo;
/* loaded from: classes4.dex */
public class TouchDelegate {
    public static final int ABOVE = 1;
    public static final int BELOW = 2;
    public static final int TO_LEFT = 4;
    public static final int TO_RIGHT = 8;
    private Rect mBounds;
    private boolean mDelegateTargeted;
    private View mDelegateView;
    private int mSlop;
    private Rect mSlopBounds;
    private AccessibilityNodeInfo.TouchDelegateInfo mTouchDelegateInfo;

    public TouchDelegate(Rect bounds, View delegateView) {
        this.mBounds = bounds;
        this.mSlop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
        Rect rect = new Rect(bounds);
        this.mSlopBounds = rect;
        int i = this.mSlop;
        rect.inset(-i, -i);
        this.mDelegateView = delegateView;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean sendToDelegate = false;
        boolean hit = true;
        switch (event.getActionMasked()) {
            case 0:
                this.mDelegateTargeted = this.mBounds.contains(x, y);
                sendToDelegate = this.mDelegateTargeted;
                break;
            case 1:
            case 2:
            case 5:
            case 6:
                sendToDelegate = this.mDelegateTargeted;
                if (sendToDelegate) {
                    Rect slopBounds = this.mSlopBounds;
                    if (!slopBounds.contains(x, y)) {
                        hit = false;
                        break;
                    }
                }
                break;
            case 3:
                sendToDelegate = this.mDelegateTargeted;
                this.mDelegateTargeted = false;
                break;
        }
        if (!sendToDelegate) {
            return false;
        }
        if (hit) {
            event.setLocation(this.mDelegateView.getWidth() / 2, this.mDelegateView.getHeight() / 2);
        } else {
            int slop = this.mSlop;
            event.setLocation(-(slop * 2), -(slop * 2));
        }
        boolean handled = this.mDelegateView.dispatchTouchEvent(event);
        return handled;
    }

    public boolean onTouchExplorationHoverEvent(MotionEvent event) {
        if (this.mBounds == null) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean hit = true;
        boolean isInbound = this.mBounds.contains(x, y);
        switch (event.getActionMasked()) {
            case 7:
                if (isInbound) {
                    this.mDelegateTargeted = true;
                    break;
                } else if (this.mDelegateTargeted && !this.mSlopBounds.contains(x, y)) {
                    hit = false;
                    break;
                }
                break;
            case 9:
                this.mDelegateTargeted = isInbound;
                break;
            case 10:
                this.mDelegateTargeted = true;
                break;
        }
        if (!this.mDelegateTargeted) {
            return false;
        }
        if (hit) {
            event.setLocation(this.mDelegateView.getWidth() / 2, this.mDelegateView.getHeight() / 2);
        } else {
            this.mDelegateTargeted = false;
        }
        boolean handled = this.mDelegateView.dispatchHoverEvent(event);
        return handled;
    }

    public AccessibilityNodeInfo.TouchDelegateInfo getTouchDelegateInfo() {
        if (this.mTouchDelegateInfo == null) {
            ArrayMap<Region, View> targetMap = new ArrayMap<>(1);
            Rect bounds = this.mBounds;
            if (bounds == null) {
                bounds = new Rect();
            }
            targetMap.put(new Region(bounds), this.mDelegateView);
            this.mTouchDelegateInfo = new AccessibilityNodeInfo.TouchDelegateInfo(targetMap);
        }
        return this.mTouchDelegateInfo;
    }
}
