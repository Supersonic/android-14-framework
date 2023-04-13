package com.android.server.p014wm;

import android.app.IAssistDataReceiver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.p006am.AssistDataRequester;
/* renamed from: com.android.server.wm.AssistDataReceiverProxy */
/* loaded from: classes2.dex */
public class AssistDataReceiverProxy implements AssistDataRequester.AssistDataRequesterCallbacks, IBinder.DeathRecipient {
    public String mCallerPackage;
    public IAssistDataReceiver mReceiver;

    @Override // com.android.server.p006am.AssistDataRequester.AssistDataRequesterCallbacks
    public boolean canHandleReceivedAssistDataLocked() {
        return true;
    }

    public AssistDataReceiverProxy(IAssistDataReceiver iAssistDataReceiver, String str) {
        this.mReceiver = iAssistDataReceiver;
        this.mCallerPackage = str;
        linkToDeath();
    }

    @Override // com.android.server.p006am.AssistDataRequester.AssistDataRequesterCallbacks
    public void onAssistDataReceivedLocked(Bundle bundle, int i, int i2) {
        IAssistDataReceiver iAssistDataReceiver = this.mReceiver;
        if (iAssistDataReceiver != null) {
            try {
                iAssistDataReceiver.onHandleAssistData(bundle);
            } catch (RemoteException e) {
                Log.w("ActivityTaskManager", "Failed to proxy assist data to receiver in package=" + this.mCallerPackage, e);
            }
        }
    }

    @Override // com.android.server.p006am.AssistDataRequester.AssistDataRequesterCallbacks
    public void onAssistScreenshotReceivedLocked(Bitmap bitmap) {
        IAssistDataReceiver iAssistDataReceiver = this.mReceiver;
        if (iAssistDataReceiver != null) {
            try {
                iAssistDataReceiver.onHandleAssistScreenshot(bitmap);
            } catch (RemoteException e) {
                Log.w("ActivityTaskManager", "Failed to proxy assist screenshot to receiver in package=" + this.mCallerPackage, e);
            }
        }
    }

    @Override // com.android.server.p006am.AssistDataRequester.AssistDataRequesterCallbacks
    public void onAssistRequestCompleted() {
        unlinkToDeath();
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        unlinkToDeath();
    }

    public final void linkToDeath() {
        try {
            this.mReceiver.asBinder().linkToDeath(this, 0);
        } catch (RemoteException e) {
            Log.w("ActivityTaskManager", "Could not link to client death", e);
        }
    }

    public final void unlinkToDeath() {
        IAssistDataReceiver iAssistDataReceiver = this.mReceiver;
        if (iAssistDataReceiver != null) {
            iAssistDataReceiver.asBinder().unlinkToDeath(this, 0);
        }
        this.mReceiver = null;
    }
}
