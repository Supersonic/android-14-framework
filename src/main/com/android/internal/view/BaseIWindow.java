package com.android.internal.view;

import android.hardware.input.InputManager;
import android.p008os.Bundle;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.util.MergedConfiguration;
import android.view.DragEvent;
import android.view.IScrollCaptureResponseListener;
import android.view.IWindow;
import android.view.IWindowSession;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.ScrollCaptureResponse;
import android.view.inputmethod.ImeTracker;
import android.window.ClientWindowFrames;
import com.android.internal.p028os.IResultReceiver;
import java.io.IOException;
/* loaded from: classes2.dex */
public class BaseIWindow extends IWindow.Stub {
    private IWindowSession mSession;

    public void setSession(IWindowSession session) {
        this.mSession = session;
    }

    public void resized(ClientWindowFrames frames, boolean reportDraw, MergedConfiguration mergedConfiguration, InsetsState insetsState, boolean forceLayout, boolean alwaysConsumeSystemBars, int displayId, int seqId, boolean dragResizing) {
        if (reportDraw) {
            try {
                this.mSession.finishDrawing(this, null, seqId);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.IWindow
    public void insetsControlChanged(InsetsState insetsState, InsetsSourceControl[] activeControls) {
    }

    @Override // android.view.IWindow
    public void showInsets(int types, boolean fromIme, ImeTracker.Token statsToken) {
    }

    @Override // android.view.IWindow
    public void hideInsets(int types, boolean fromIme, ImeTracker.Token statsToken) {
    }

    public void moved(int newX, int newY) {
    }

    public void dispatchAppVisibility(boolean visible) {
    }

    @Override // android.view.IWindow
    public void dispatchGetNewSurface() {
    }

    @Override // android.view.IWindow
    public void executeCommand(String command, String parameters, ParcelFileDescriptor out) {
        if (out != null) {
            try {
                out.closeWithError("Unsupported command " + command);
            } catch (IOException e) {
            }
        }
    }

    @Override // android.view.IWindow
    public void closeSystemDialogs(String reason) {
    }

    public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep, float zoom, boolean sync) {
        if (sync) {
            try {
                this.mSession.wallpaperOffsetsComplete(asBinder());
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.IWindow
    public void dispatchDragEvent(DragEvent event) {
        if (event.getAction() == 3) {
            try {
                this.mSession.reportDropResult(this, false);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.IWindow
    public void updatePointerIcon(float x, float y) {
        InputManager.getInstance().setPointerIconType(1);
    }

    public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras, boolean sync) {
        if (sync) {
            try {
                this.mSession.wallpaperCommandComplete(asBinder(), null);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.IWindow
    public void dispatchWindowShown() {
    }

    @Override // android.view.IWindow
    public void requestAppKeyboardShortcuts(IResultReceiver receiver, int deviceId) {
    }

    @Override // android.view.IWindow
    public void requestScrollCapture(IScrollCaptureResponseListener listener) {
        try {
            listener.onScrollCaptureResponse(new ScrollCaptureResponse.Builder().setDescription("Not Implemented").build());
        } catch (RemoteException e) {
        }
    }
}
