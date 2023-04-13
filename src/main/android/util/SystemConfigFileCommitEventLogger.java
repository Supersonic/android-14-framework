package android.util;

import android.annotation.SystemApi;
import android.p008os.SystemClock;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes3.dex */
public class SystemConfigFileCommitEventLogger {
    private final String mName;
    private long mStartTime;

    public SystemConfigFileCommitEventLogger(String name) {
        this.mName = name;
    }

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStartWrite() {
        if (this.mStartTime == 0) {
            this.mStartTime = SystemClock.uptimeMillis();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFinishWrite() {
        com.android.internal.logging.EventLogTags.writeCommitSysConfigFile(this.mName, SystemClock.uptimeMillis() - this.mStartTime);
    }
}
