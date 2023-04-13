package com.android.internal.util;

import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.IProgressListener;
import android.p008os.RemoteCallbackList;
import android.p008os.RemoteException;
import android.util.MathUtils;
/* loaded from: classes3.dex */
public class ProgressReporter {
    private static final int STATE_FINISHED = 2;
    private static final int STATE_INIT = 0;
    private static final int STATE_STARTED = 1;
    private final int mId;
    private final RemoteCallbackList<IProgressListener> mListeners = new RemoteCallbackList<>();
    private int mState = 0;
    private int mProgress = 0;
    private Bundle mExtras = new Bundle();
    private int[] mSegmentRange = {0, 100};

    public ProgressReporter(int id) {
        this.mId = id;
    }

    public void addListener(IProgressListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (this) {
            this.mListeners.register(listener);
            switch (this.mState) {
                case 1:
                    try {
                        listener.onStarted(this.mId, null);
                        listener.onProgress(this.mId, this.mProgress, this.mExtras);
                        break;
                    } catch (RemoteException e) {
                        break;
                    }
                case 2:
                    try {
                        listener.onFinished(this.mId, null);
                        break;
                    } catch (RemoteException e2) {
                        break;
                    }
            }
        }
    }

    public void setProgress(int progress) {
        setProgress(progress, 100, null);
    }

    public void setProgress(int progress, CharSequence title) {
        setProgress(progress, 100, title);
    }

    public void setProgress(int n, int m) {
        setProgress(n, m, null);
    }

    public void setProgress(int n, int m, CharSequence title) {
        synchronized (this) {
            if (this.mState != 1) {
                throw new IllegalStateException("Must be started to change progress");
            }
            int[] iArr = this.mSegmentRange;
            int i = iArr[0];
            int i2 = iArr[1];
            this.mProgress = i + MathUtils.constrain((n * i2) / m, 0, i2);
            if (title != null) {
                this.mExtras.putCharSequence(Intent.EXTRA_TITLE, title);
            }
            notifyProgress(this.mId, this.mProgress, this.mExtras);
        }
    }

    public int[] startSegment(int size) {
        int[] lastRange;
        synchronized (this) {
            lastRange = this.mSegmentRange;
            this.mSegmentRange = new int[]{this.mProgress, (lastRange[1] * size) / 100};
        }
        return lastRange;
    }

    public void endSegment(int[] lastRange) {
        synchronized (this) {
            int[] iArr = this.mSegmentRange;
            this.mProgress = iArr[0] + iArr[1];
            this.mSegmentRange = lastRange;
        }
    }

    int getProgress() {
        return this.mProgress;
    }

    int[] getSegmentRange() {
        return this.mSegmentRange;
    }

    public void start() {
        synchronized (this) {
            this.mState = 1;
            notifyStarted(this.mId, null);
            notifyProgress(this.mId, this.mProgress, this.mExtras);
        }
    }

    public void finish() {
        synchronized (this) {
            this.mState = 2;
            notifyFinished(this.mId, null);
            this.mListeners.kill();
        }
    }

    private void notifyStarted(int id, Bundle extras) {
        for (int i = this.mListeners.beginBroadcast() - 1; i >= 0; i--) {
            try {
                this.mListeners.getBroadcastItem(i).onStarted(id, extras);
            } catch (RemoteException e) {
            }
        }
        this.mListeners.finishBroadcast();
    }

    private void notifyProgress(int id, int progress, Bundle extras) {
        for (int i = this.mListeners.beginBroadcast() - 1; i >= 0; i--) {
            try {
                this.mListeners.getBroadcastItem(i).onProgress(id, progress, extras);
            } catch (RemoteException e) {
            }
        }
        this.mListeners.finishBroadcast();
    }

    private void notifyFinished(int id, Bundle extras) {
        for (int i = this.mListeners.beginBroadcast() - 1; i >= 0; i--) {
            try {
                this.mListeners.getBroadcastItem(i).onFinished(id, extras);
            } catch (RemoteException e) {
            }
        }
        this.mListeners.finishBroadcast();
    }
}
