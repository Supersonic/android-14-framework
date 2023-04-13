package com.android.server.soundtrigger_middleware;
/* loaded from: classes2.dex */
public interface ICaptureStateNotifier {

    /* loaded from: classes2.dex */
    public interface Listener {
        void onCaptureStateChange(boolean z);
    }

    boolean registerListener(Listener listener);

    void unregisterListener(Listener listener);
}
