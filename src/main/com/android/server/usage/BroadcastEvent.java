package com.android.server.usage;

import android.util.LongArrayQueue;
import java.util.Objects;
/* loaded from: classes2.dex */
public class BroadcastEvent {
    public long mIdForResponseEvent;
    public int mSourceUid;
    public String mTargetPackage;
    public int mTargetUserId;
    public final LongArrayQueue mTimestampsMs = new LongArrayQueue();

    public BroadcastEvent(int i, String str, int i2, long j) {
        this.mSourceUid = i;
        this.mTargetPackage = str;
        this.mTargetUserId = i2;
        this.mIdForResponseEvent = j;
    }

    public int getSourceUid() {
        return this.mSourceUid;
    }

    public String getTargetPackage() {
        return this.mTargetPackage;
    }

    public int getTargetUserId() {
        return this.mTargetUserId;
    }

    public long getIdForResponseEvent() {
        return this.mIdForResponseEvent;
    }

    public LongArrayQueue getTimestampsMs() {
        return this.mTimestampsMs;
    }

    public void addTimestampMs(long j) {
        this.mTimestampsMs.addLast(j);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BroadcastEvent)) {
            return false;
        }
        BroadcastEvent broadcastEvent = (BroadcastEvent) obj;
        return this.mSourceUid == broadcastEvent.mSourceUid && this.mIdForResponseEvent == broadcastEvent.mIdForResponseEvent && this.mTargetUserId == broadcastEvent.mTargetUserId && this.mTargetPackage.equals(broadcastEvent.mTargetPackage);
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSourceUid), this.mTargetPackage, Integer.valueOf(this.mTargetUserId), Long.valueOf(this.mIdForResponseEvent));
    }

    public String toString() {
        return "BroadcastEvent {srcUid=" + this.mSourceUid + ",tgtPkg=" + this.mTargetPackage + ",tgtUser=" + this.mTargetUserId + ",id=" + this.mIdForResponseEvent + "}";
    }
}
