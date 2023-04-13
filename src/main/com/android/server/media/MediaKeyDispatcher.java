package com.android.server.media;

import android.app.PendingIntent;
import android.media.session.MediaSession;
import android.view.KeyEvent;
import java.util.Map;
/* loaded from: classes2.dex */
public abstract class MediaKeyDispatcher {
    public Map<Integer, Integer> mOverriddenKeyEvents;

    public static boolean isDoubleTapOverridden(int i) {
        return (i & 2) != 0;
    }

    public static boolean isLongPressOverridden(int i) {
        return (i & 8) != 0;
    }

    public static boolean isSingleTapOverridden(int i) {
        return (i & 1) != 0;
    }

    public static boolean isTripleTapOverridden(int i) {
        return (i & 4) != 0;
    }

    public PendingIntent getMediaButtonReceiver(KeyEvent keyEvent, int i, boolean z) {
        return null;
    }

    public MediaSession.Token getMediaSession(KeyEvent keyEvent, int i, boolean z) {
        return null;
    }

    public void onDoubleTap(KeyEvent keyEvent) {
    }

    public void onLongPress(KeyEvent keyEvent) {
    }

    public void onSingleTap(KeyEvent keyEvent) {
    }

    public void onTripleTap(KeyEvent keyEvent) {
    }

    public Map<Integer, Integer> getOverriddenKeyEvents() {
        return this.mOverriddenKeyEvents;
    }
}
