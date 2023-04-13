package com.android.server.usage;

import com.android.server.backup.BackupManagerConstants;
/* loaded from: classes2.dex */
public class UnixCalendar {
    public long mTime;

    public UnixCalendar(long j) {
        this.mTime = j;
    }

    public void addDays(int i) {
        this.mTime += i * BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
    }

    public void addWeeks(int i) {
        this.mTime += i * 604800000;
    }

    public void addMonths(int i) {
        this.mTime += i * 2592000000L;
    }

    public void addYears(int i) {
        this.mTime += i * 31536000000L;
    }

    public void setTimeInMillis(long j) {
        this.mTime = j;
    }

    public long getTimeInMillis() {
        return this.mTime;
    }
}
