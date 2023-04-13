package com.android.server.input;

import android.graphics.PointF;
import android.hardware.display.DisplayViewport;
import android.os.IBinder;
import android.view.InputChannel;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodSubtypeHandle;
import java.util.List;
/* loaded from: classes.dex */
public abstract class InputManagerInternal {

    /* loaded from: classes.dex */
    public interface LidSwitchCallback {
        void notifyLidSwitchChanged(long j, boolean z);
    }

    public abstract void addKeyboardLayoutAssociation(String str, String str2, String str3);

    public abstract InputChannel createInputChannel(String str);

    public abstract void decrementKeyboardBacklight(int i);

    public abstract PointF getCursorPosition();

    public abstract int getVirtualMousePointerDisplayId();

    public abstract void incrementKeyboardBacklight(int i);

    public abstract void notifyUserActivity();

    public abstract void onInputMethodSubtypeChangedForKeyboardLayoutMapping(int i, InputMethodSubtypeHandle inputMethodSubtypeHandle, InputMethodSubtype inputMethodSubtype);

    public abstract void registerLidSwitchCallback(LidSwitchCallback lidSwitchCallback);

    public abstract void removeKeyboardLayoutAssociation(String str);

    public abstract void setDisplayEligibilityForPointerCapture(int i, boolean z);

    public abstract void setDisplayViewports(List<DisplayViewport> list);

    public abstract void setInteractive(boolean z);

    public abstract void setPointerAcceleration(float f, int i);

    public abstract void setPointerIconVisible(boolean z, int i);

    public abstract void setPulseGestureEnabled(boolean z);

    public abstract void setStylusButtonMotionEventsEnabled(boolean z);

    public abstract void setTypeAssociation(String str, String str2);

    public abstract boolean setVirtualMousePointerDisplayId(int i);

    public abstract void toggleCapsLock(int i);

    public abstract boolean transferTouchFocus(IBinder iBinder, IBinder iBinder2);

    public abstract void unsetTypeAssociation(String str);
}
