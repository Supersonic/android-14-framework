package com.android.server.p014wm;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.IntArray;
import android.view.IDisplayWindowListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.DisplayWindowListenerController */
/* loaded from: classes2.dex */
public class DisplayWindowListenerController {
    public RemoteCallbackList<IDisplayWindowListener> mDisplayListeners = new RemoteCallbackList<>();
    public final WindowManagerService mService;

    public DisplayWindowListenerController(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
    }

    public int[] registerListener(IDisplayWindowListener iDisplayWindowListener) {
        int[] array;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mDisplayListeners.register(iDisplayWindowListener);
                final IntArray intArray = new IntArray();
                this.mService.mAtmService.mRootWindowContainer.forAllDisplays(new Consumer() { // from class: com.android.server.wm.DisplayWindowListenerController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DisplayWindowListenerController.lambda$registerListener$0(intArray, (DisplayContent) obj);
                    }
                });
                array = intArray.toArray();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return array;
    }

    public static /* synthetic */ void lambda$registerListener$0(IntArray intArray, DisplayContent displayContent) {
        intArray.add(displayContent.mDisplayId);
    }

    public void unregisterListener(IDisplayWindowListener iDisplayWindowListener) {
        this.mDisplayListeners.unregister(iDisplayWindowListener);
    }

    public void dispatchDisplayAdded(DisplayContent displayContent) {
        int beginBroadcast = this.mDisplayListeners.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mDisplayListeners.getBroadcastItem(i).onDisplayAdded(displayContent.mDisplayId);
            } catch (RemoteException unused) {
            }
        }
        this.mDisplayListeners.finishBroadcast();
    }

    public void dispatchDisplayChanged(DisplayContent displayContent, Configuration configuration) {
        boolean z = false;
        for (int i = 0; i < displayContent.getParent().getChildCount(); i++) {
            if (displayContent.getParent().getChildAt(i) == displayContent) {
                z = true;
            }
        }
        if (z) {
            int beginBroadcast = this.mDisplayListeners.beginBroadcast();
            for (int i2 = 0; i2 < beginBroadcast; i2++) {
                try {
                    this.mDisplayListeners.getBroadcastItem(i2).onDisplayConfigurationChanged(displayContent.getDisplayId(), configuration);
                } catch (RemoteException unused) {
                }
            }
            this.mDisplayListeners.finishBroadcast();
        }
    }

    public void dispatchDisplayRemoved(DisplayContent displayContent) {
        int beginBroadcast = this.mDisplayListeners.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mDisplayListeners.getBroadcastItem(i).onDisplayRemoved(displayContent.mDisplayId);
            } catch (RemoteException unused) {
            }
        }
        this.mDisplayListeners.finishBroadcast();
    }

    public void dispatchFixedRotationStarted(DisplayContent displayContent, int i) {
        int beginBroadcast = this.mDisplayListeners.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mDisplayListeners.getBroadcastItem(i2).onFixedRotationStarted(displayContent.mDisplayId, i);
            } catch (RemoteException unused) {
            }
        }
        this.mDisplayListeners.finishBroadcast();
    }

    public void dispatchFixedRotationFinished(DisplayContent displayContent) {
        int beginBroadcast = this.mDisplayListeners.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mDisplayListeners.getBroadcastItem(i).onFixedRotationFinished(displayContent.mDisplayId);
            } catch (RemoteException unused) {
            }
        }
        this.mDisplayListeners.finishBroadcast();
    }

    public void dispatchKeepClearAreasChanged(DisplayContent displayContent, Set<Rect> set, Set<Rect> set2) {
        int beginBroadcast = this.mDisplayListeners.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mDisplayListeners.getBroadcastItem(i).onKeepClearAreasChanged(displayContent.mDisplayId, new ArrayList(set), new ArrayList(set2));
            } catch (RemoteException unused) {
            }
        }
        this.mDisplayListeners.finishBroadcast();
    }
}
