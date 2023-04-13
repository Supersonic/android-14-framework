package com.android.server.inputmethod;

import android.os.IBinder;
import android.os.InputConstants;
import android.os.Process;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
/* loaded from: classes.dex */
public final class HandwritingEventReceiverSurface {
    public final InputChannel mClientChannel;
    public final SurfaceControl mInputSurface;
    public boolean mIsIntercepting;
    public final InputWindowHandle mWindowHandle;

    public HandwritingEventReceiverSurface(String str, int i, SurfaceControl surfaceControl, InputChannel inputChannel) {
        this.mClientChannel = inputChannel;
        this.mInputSurface = surfaceControl;
        InputWindowHandle inputWindowHandle = new InputWindowHandle(new InputApplicationHandle((IBinder) null, str, InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS), i);
        this.mWindowHandle = inputWindowHandle;
        inputWindowHandle.name = str;
        inputWindowHandle.token = inputChannel.getToken();
        inputWindowHandle.layoutParamsType = 2015;
        inputWindowHandle.dispatchingTimeoutMillis = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        inputWindowHandle.ownerPid = Process.myPid();
        inputWindowHandle.ownerUid = Process.myUid();
        inputWindowHandle.scaleFactor = 1.0f;
        inputWindowHandle.inputConfig = 49420;
        inputWindowHandle.replaceTouchableRegionWithCrop((SurfaceControl) null);
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        transaction.setInputWindowInfo(surfaceControl, inputWindowHandle);
        transaction.setLayer(surfaceControl, Integer.MAX_VALUE);
        transaction.setPosition(surfaceControl, 0.0f, 0.0f);
        transaction.setCrop(surfaceControl, null);
        transaction.show(surfaceControl);
        transaction.apply();
        this.mIsIntercepting = false;
    }

    public void startIntercepting(int i, int i2) {
        InputWindowHandle inputWindowHandle = this.mWindowHandle;
        inputWindowHandle.ownerPid = i;
        inputWindowHandle.ownerUid = i2;
        inputWindowHandle.inputConfig &= -16385;
        new SurfaceControl.Transaction().setInputWindowInfo(this.mInputSurface, this.mWindowHandle).apply();
        this.mIsIntercepting = true;
    }

    public boolean isIntercepting() {
        return this.mIsIntercepting;
    }

    public void remove() {
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        transaction.remove(this.mInputSurface);
        transaction.apply();
    }

    public InputChannel getInputChannel() {
        return this.mClientChannel;
    }

    public SurfaceControl getSurface() {
        return this.mInputSurface;
    }
}
