package com.android.server.dreams;

import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes.dex */
public interface DreamUiEventLogger {
    void log(UiEventLogger.UiEventEnum uiEventEnum, String str);

    /* loaded from: classes.dex */
    public enum DreamUiEventEnum implements UiEventLogger.UiEventEnum {
        DREAM_START(577),
        DREAM_STOP(FrameworkStatsLog.HOTWORD_AUDIO_EGRESS_EVENT_REPORTED);
        
        private final int mId;

        DreamUiEventEnum(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }
}
