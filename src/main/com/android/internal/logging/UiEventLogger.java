package com.android.internal.logging;
/* loaded from: classes4.dex */
public interface UiEventLogger {

    /* loaded from: classes4.dex */
    public interface UiEventEnum {
        public static final int RESERVE_NEW_UI_EVENT_ID = Integer.MIN_VALUE;

        int getId();
    }

    void log(UiEventEnum uiEventEnum);

    void log(UiEventEnum uiEventEnum, int i, String str);

    void log(UiEventEnum uiEventEnum, InstanceId instanceId);

    void logWithInstanceId(UiEventEnum uiEventEnum, int i, String str, InstanceId instanceId);

    void logWithInstanceIdAndPosition(UiEventEnum uiEventEnum, int i, String str, InstanceId instanceId, int i2);

    void logWithPosition(UiEventEnum uiEventEnum, int i, String str, int i2);
}
