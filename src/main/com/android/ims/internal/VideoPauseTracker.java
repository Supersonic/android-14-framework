package com.android.ims.internal;

import android.util.ArraySet;
import android.util.Log;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class VideoPauseTracker {
    private static final String LOG_TAG = VideoPauseTracker.class.getSimpleName();
    public static final int SOURCE_DATA_ENABLED = 2;
    private static final String SOURCE_DATA_ENABLED_STR = "DATA_ENABLED";
    public static final int SOURCE_INCALL = 1;
    private static final String SOURCE_INCALL_STR = "INCALL";
    private Set<Integer> mPauseRequests = new ArraySet(2);
    private Object mPauseRequestsLock = new Object();

    public boolean shouldPauseVideoFor(int source) {
        synchronized (this.mPauseRequestsLock) {
            boolean wasPaused = isPaused();
            this.mPauseRequests.add(Integer.valueOf(source));
            if (!wasPaused) {
                Log.i(LOG_TAG, String.format("shouldPauseVideoFor: source=%s, pendingRequests=%s - should pause", sourceToString(source), sourcesToString(this.mPauseRequests)));
                return true;
            }
            Log.i(LOG_TAG, String.format("shouldPauseVideoFor: source=%s, pendingRequests=%s - already paused", sourceToString(source), sourcesToString(this.mPauseRequests)));
            return false;
        }
    }

    public boolean shouldResumeVideoFor(int source) {
        synchronized (this.mPauseRequestsLock) {
            boolean wasPaused = isPaused();
            this.mPauseRequests.remove(Integer.valueOf(source));
            boolean isPaused = isPaused();
            if (wasPaused && !isPaused) {
                Log.i(LOG_TAG, String.format("shouldResumeVideoFor: source=%s, pendingRequests=%s - should resume", sourceToString(source), sourcesToString(this.mPauseRequests)));
                return true;
            } else if (wasPaused && isPaused) {
                Log.i(LOG_TAG, String.format("shouldResumeVideoFor: source=%s, pendingRequests=%s - stay paused", sourceToString(source), sourcesToString(this.mPauseRequests)));
                return false;
            } else {
                Log.i(LOG_TAG, String.format("shouldResumeVideoFor: source=%s, pendingRequests=%s - not paused", sourceToString(source), sourcesToString(this.mPauseRequests)));
                return true;
            }
        }
    }

    public boolean isPaused() {
        boolean z;
        synchronized (this.mPauseRequestsLock) {
            z = !this.mPauseRequests.isEmpty();
        }
        return z;
    }

    public boolean wasVideoPausedFromSource(int source) {
        boolean contains;
        synchronized (this.mPauseRequestsLock) {
            contains = this.mPauseRequests.contains(Integer.valueOf(source));
        }
        return contains;
    }

    public void clearPauseRequests() {
        synchronized (this.mPauseRequestsLock) {
            this.mPauseRequests.clear();
        }
    }

    private String sourceToString(int source) {
        switch (source) {
            case 1:
                return SOURCE_INCALL_STR;
            case 2:
                return SOURCE_DATA_ENABLED_STR;
            default:
                return "unknown";
        }
    }

    private String sourcesToString(Collection<Integer> sources) {
        String str;
        synchronized (this.mPauseRequestsLock) {
            str = (String) sources.stream().map(new Function() { // from class: com.android.ims.internal.VideoPauseTracker$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$sourcesToString$0;
                    lambda$sourcesToString$0 = VideoPauseTracker.this.lambda$sourcesToString$0((Integer) obj);
                    return lambda$sourcesToString$0;
                }
            }).collect(Collectors.joining(", "));
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$sourcesToString$0(Integer source) {
        return sourceToString(source.intValue());
    }
}
