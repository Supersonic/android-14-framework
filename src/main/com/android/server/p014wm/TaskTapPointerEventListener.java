package com.android.server.p014wm;

import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.view.MotionEvent;
import android.view.WindowManagerPolicyConstants;
/* renamed from: com.android.server.wm.TaskTapPointerEventListener */
/* loaded from: classes2.dex */
public class TaskTapPointerEventListener implements WindowManagerPolicyConstants.PointerEventListener {
    public final DisplayContent mDisplayContent;
    public final WindowManagerService mService;
    public final Region mTouchExcludeRegion = new Region();
    public final Rect mTmpRect = new Rect();
    public int mPointerIconType = 1;

    public TaskTapPointerEventListener(WindowManagerService windowManagerService, DisplayContent displayContent) {
        this.mService = windowManagerService;
        this.mDisplayContent = displayContent;
    }

    public final void restorePointerIcon(int i, int i2) {
        if (this.mPointerIconType != 1) {
            this.mPointerIconType = 1;
            this.mService.f1164mH.removeMessages(55);
            this.mService.f1164mH.obtainMessage(55, i, i2, this.mDisplayContent).sendToTarget();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x006b, code lost:
        if (r8 > r1.bottom) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x007a, code lost:
        if (r8 > r1.bottom) goto L27;
     */
    /* JADX WARN: Removed duplicated region for block: B:45:0x008d  */
    /* JADX WARN: Removed duplicated region for block: B:67:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onPointerEvent(MotionEvent motionEvent) {
        int x;
        float y;
        int i;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            if (motionEvent.getSource() == 8194) {
                x = (int) motionEvent.getXCursorPosition();
                y = motionEvent.getYCursorPosition();
            } else {
                x = (int) motionEvent.getX();
                y = motionEvent.getY();
            }
            int i2 = (int) y;
            synchronized (this) {
                if (!this.mTouchExcludeRegion.contains(x, i2)) {
                    this.mService.mTaskPositioningController.handleTapOutsideTask(this.mDisplayContent, x, i2);
                }
            }
        } else if (actionMasked != 7 && actionMasked != 9) {
            if (actionMasked != 10) {
                return;
            }
            restorePointerIcon((int) motionEvent.getX(), (int) motionEvent.getY());
        } else {
            int x2 = (int) motionEvent.getX();
            int y2 = (int) motionEvent.getY();
            if (this.mTouchExcludeRegion.contains(x2, y2)) {
                restorePointerIcon(x2, y2);
                return;
            }
            Task findTaskForResizePoint = this.mDisplayContent.findTaskForResizePoint(x2, y2);
            if (findTaskForResizePoint != null) {
                findTaskForResizePoint.getDimBounds(this.mTmpRect);
                if (!this.mTmpRect.isEmpty() && !this.mTmpRect.contains(x2, y2)) {
                    Rect rect = this.mTmpRect;
                    i = 1014;
                    if (x2 < rect.left) {
                        if (y2 >= rect.top) {
                        }
                        i = 1017;
                    } else if (x2 > rect.right) {
                        if (y2 >= rect.top) {
                        }
                        i = 1016;
                    } else if (y2 < rect.top || y2 > rect.bottom) {
                        i = 1015;
                    }
                    if (this.mPointerIconType == i) {
                        this.mPointerIconType = i;
                        if (i == 1) {
                            this.mService.f1164mH.removeMessages(55);
                            this.mService.f1164mH.obtainMessage(55, x2, y2, this.mDisplayContent).sendToTarget();
                            return;
                        }
                        InputManager.getInstance().setPointerIconType(this.mPointerIconType);
                        return;
                    }
                    return;
                }
            }
            i = 1;
            if (this.mPointerIconType == i) {
            }
        }
    }

    public void setTouchExcludeRegion(Region region) {
        synchronized (this) {
            this.mTouchExcludeRegion.set(region);
        }
    }
}
