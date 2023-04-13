package com.android.server.dreams;

import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes.dex */
public class DreamUiEventLoggerImpl implements DreamUiEventLogger {
    public final String[] mLoggableDreamPrefixes;

    public DreamUiEventLoggerImpl(String[] strArr) {
        this.mLoggableDreamPrefixes = strArr;
    }

    @Override // com.android.server.dreams.DreamUiEventLogger
    public void log(UiEventLogger.UiEventEnum uiEventEnum, String str) {
        int id = uiEventEnum.getId();
        if (id <= 0) {
            return;
        }
        if (!isFirstPartyDream(str)) {
            str = "other";
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.DREAM_UI_EVENT_REPORTED, 0, id, 0, str);
    }

    public final boolean isFirstPartyDream(String str) {
        int i = 0;
        while (true) {
            String[] strArr = this.mLoggableDreamPrefixes;
            if (i >= strArr.length) {
                return false;
            }
            if (str.startsWith(strArr[i])) {
                return true;
            }
            i++;
        }
    }
}
