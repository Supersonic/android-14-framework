package com.android.server.utils;
/* loaded from: classes2.dex */
public class Snapshots {
    public static <T> T maybeSnapshot(T t) {
        return t instanceof Snappable ? (T) ((Snappable) t).snapshot() : t;
    }
}
