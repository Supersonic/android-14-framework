package com.android.server.p014wm;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.InputConstants;
import android.os.RemoteException;
import android.os.UserHandle;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.InputConsumerImpl */
/* loaded from: classes2.dex */
public class InputConsumerImpl implements IBinder.DeathRecipient {
    public final InputApplicationHandle mApplicationHandle;
    public final InputChannel mClientChannel;
    public final int mClientPid;
    public final UserHandle mClientUser;
    public final SurfaceControl mInputSurface;
    public final String mName;
    public final WindowManagerService mService;
    public final IBinder mToken;
    public final InputWindowHandle mWindowHandle;
    public Rect mTmpClipRect = new Rect();
    public final Rect mTmpRect = new Rect();
    public final Point mOldPosition = new Point();
    public final Rect mOldWindowCrop = new Rect();

    public InputConsumerImpl(WindowManagerService windowManagerService, IBinder iBinder, String str, InputChannel inputChannel, int i, UserHandle userHandle, int i2) {
        this.mService = windowManagerService;
        this.mToken = iBinder;
        this.mName = str;
        this.mClientPid = i;
        this.mClientUser = userHandle;
        InputChannel createInputChannel = windowManagerService.mInputManager.createInputChannel(str);
        this.mClientChannel = createInputChannel;
        if (inputChannel != null) {
            createInputChannel.copyTo(inputChannel);
        }
        InputApplicationHandle inputApplicationHandle = new InputApplicationHandle(new Binder(), str, InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS);
        this.mApplicationHandle = inputApplicationHandle;
        InputWindowHandle inputWindowHandle = new InputWindowHandle(inputApplicationHandle, i2);
        this.mWindowHandle = inputWindowHandle;
        inputWindowHandle.name = str;
        inputWindowHandle.token = createInputChannel.getToken();
        inputWindowHandle.layoutParamsType = 2022;
        inputWindowHandle.dispatchingTimeoutMillis = InputConstants.DEFAULT_DISPATCHING_TIMEOUT_MILLIS;
        inputWindowHandle.ownerPid = WindowManagerService.MY_PID;
        inputWindowHandle.ownerUid = WindowManagerService.MY_UID;
        inputWindowHandle.scaleFactor = 1.0f;
        inputWindowHandle.inputConfig = 260;
        SurfaceControl.Builder containerLayer = windowManagerService.makeSurfaceBuilder(windowManagerService.mRoot.getDisplayContent(i2).getSession()).setContainerLayer();
        this.mInputSurface = containerLayer.setName("Input Consumer " + str).setCallsite("InputConsumerImpl").build();
    }

    public void linkToDeathRecipient() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            return;
        }
        try {
            iBinder.linkToDeath(this, 0);
        } catch (RemoteException unused) {
        }
    }

    public void unlinkFromDeathRecipient() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            return;
        }
        iBinder.unlinkToDeath(this, 0);
    }

    public void layout(SurfaceControl.Transaction transaction, int i, int i2) {
        this.mTmpRect.set(0, 0, i, i2);
        layout(transaction, this.mTmpRect);
    }

    public void layout(SurfaceControl.Transaction transaction, Rect rect) {
        this.mTmpClipRect.set(0, 0, rect.width(), rect.height());
        if (this.mOldPosition.equals(rect.left, rect.top) && this.mOldWindowCrop.equals(this.mTmpClipRect)) {
            return;
        }
        transaction.setPosition(this.mInputSurface, rect.left, rect.top);
        transaction.setWindowCrop(this.mInputSurface, this.mTmpClipRect);
        this.mOldPosition.set(rect.left, rect.top);
        this.mOldWindowCrop.set(this.mTmpClipRect);
    }

    public void hide(SurfaceControl.Transaction transaction) {
        transaction.hide(this.mInputSurface);
    }

    public void show(SurfaceControl.Transaction transaction, WindowContainer windowContainer) {
        transaction.show(this.mInputSurface);
        transaction.setInputWindowInfo(this.mInputSurface, this.mWindowHandle);
        transaction.setRelativeLayer(this.mInputSurface, windowContainer.getSurfaceControl(), 1);
    }

    public void show(SurfaceControl.Transaction transaction, int i) {
        transaction.show(this.mInputSurface);
        transaction.setInputWindowInfo(this.mInputSurface, this.mWindowHandle);
        transaction.setLayer(this.mInputSurface, i);
    }

    public void reparent(SurfaceControl.Transaction transaction, WindowContainer windowContainer) {
        transaction.reparent(this.mInputSurface, windowContainer.getSurfaceControl());
    }

    public void disposeChannelsLw(SurfaceControl.Transaction transaction) {
        this.mService.mInputManager.removeInputChannel(this.mClientChannel.getToken());
        this.mClientChannel.dispose();
        transaction.remove(this.mInputSurface);
        unlinkFromDeathRecipient();
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this.mService.getWindowManagerLock()) {
            DisplayContent displayContent = this.mService.mRoot.getDisplayContent(this.mWindowHandle.displayId);
            if (displayContent == null) {
                return;
            }
            displayContent.getInputMonitor().destroyInputConsumer(this.mName);
            unlinkFromDeathRecipient();
        }
    }

    public void dump(PrintWriter printWriter, String str, String str2) {
        printWriter.println(str2 + "  name=" + str + " pid=" + this.mClientPid + " user=" + this.mClientUser);
    }
}
