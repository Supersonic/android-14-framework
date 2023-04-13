package com.android.ims.internal;
/* loaded from: classes.dex */
public class ImsStreamMediaSession {
    private static final String TAG = "ImsStreamMediaSession";
    private Listener mListener;

    /* loaded from: classes.dex */
    public static class Listener {
    }

    ImsStreamMediaSession(IImsStreamMediaSession mediaSession) {
    }

    ImsStreamMediaSession(IImsStreamMediaSession mediaSession, Listener listener) {
        this(mediaSession);
        setListener(listener);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }
}
