package com.android.server.p014wm;

import android.os.Looper;
import android.util.Slog;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
/* renamed from: com.android.server.wm.DragInputEventReceiver */
/* loaded from: classes2.dex */
public class DragInputEventReceiver extends InputEventReceiver {
    public final DragDropController mDragDropController;
    public boolean mIsStartEvent;
    public boolean mMuteInput;
    public boolean mStylusButtonDownAtStart;

    public DragInputEventReceiver(InputChannel inputChannel, Looper looper, DragDropController dragDropController) {
        super(inputChannel, looper);
        this.mIsStartEvent = true;
        this.mMuteInput = false;
        this.mDragDropController = dragDropController;
    }

    public void onInputEvent(InputEvent inputEvent) {
        try {
            if ((inputEvent instanceof MotionEvent) && (inputEvent.getSource() & 2) != 0 && !this.mMuteInput) {
                MotionEvent motionEvent = (MotionEvent) inputEvent;
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                boolean z = (motionEvent.getButtonState() & 32) != 0;
                if (this.mIsStartEvent) {
                    this.mStylusButtonDownAtStart = z;
                    this.mIsStartEvent = false;
                }
                int action = motionEvent.getAction();
                if (action == 0) {
                    Slog.w(StartingSurfaceController.TAG, "Unexpected ACTION_DOWN in drag layer");
                    return;
                }
                if (action == 1) {
                    Slog.d(StartingSurfaceController.TAG, "Got UP on move channel; dropping at " + rawX + "," + rawY);
                    this.mMuteInput = true;
                } else if (action != 2) {
                    if (action != 3) {
                        return;
                    }
                    Slog.d(StartingSurfaceController.TAG, "Drag cancelled!");
                    this.mMuteInput = true;
                } else if (this.mStylusButtonDownAtStart && !z) {
                    Slog.d(StartingSurfaceController.TAG, "Button no longer pressed; dropping at " + rawX + "," + rawY);
                    this.mMuteInput = true;
                }
                this.mDragDropController.handleMotionEvent(!this.mMuteInput, rawX, rawY);
                finishInputEvent(inputEvent, true);
            }
        } catch (Exception e) {
            Slog.e(StartingSurfaceController.TAG, "Exception caught by drag handleMotion", e);
        } finally {
            finishInputEvent(inputEvent, false);
        }
    }
}
