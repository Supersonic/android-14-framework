package com.android.internal.telephony;

import android.content.Context;
import android.provider.Settings;
import android.telephony.AnomalyReporter;
import com.android.internal.annotations.VisibleForTesting;
import com.android.telephony.Rlog;
import java.util.HashMap;
import java.util.UUID;
/* loaded from: classes.dex */
public class RadioBugDetector {
    @VisibleForTesting
    protected static final int RADIO_BUG_REPETITIVE_SYSTEM_ERROR = 2;
    private Context mContext;
    private int mSlotId;
    private int mContinuousWakelockTimoutCount = 0;
    private int mRadioBugStatus = 0;
    private int mWakelockTimeoutThreshold = 0;
    private int mSystemErrorThreshold = 0;
    private HashMap<Integer, Integer> mSysErrRecord = new HashMap<>();

    public RadioBugDetector(Context context, int i) {
        this.mContext = context;
        this.mSlotId = i;
        init();
    }

    private void init() {
        this.mWakelockTimeoutThreshold = Settings.Global.getInt(this.mContext.getContentResolver(), "radio_bug_wakelock_timeout_count_threshold", 10);
        this.mSystemErrorThreshold = Settings.Global.getInt(this.mContext.getContentResolver(), "radio_bug_system_error_count_threshold", 100);
    }

    public synchronized void detectRadioBug(int i, int i2) {
        this.mContinuousWakelockTimoutCount = 0;
        if (i2 == 39) {
            this.mSysErrRecord.put(Integer.valueOf(i), Integer.valueOf(this.mSysErrRecord.getOrDefault(Integer.valueOf(i), 0).intValue() + 1));
            broadcastBug(true);
        } else {
            this.mSysErrRecord.remove(Integer.valueOf(i));
            if (!isFrequentSystemError()) {
                this.mRadioBugStatus = 0;
            }
        }
    }

    public void processWakelockTimeout() {
        this.mContinuousWakelockTimoutCount++;
        broadcastBug(false);
    }

    private synchronized void broadcastBug(boolean z) {
        if (z) {
            if (!isFrequentSystemError()) {
                return;
            }
        } else if (this.mContinuousWakelockTimoutCount < this.mWakelockTimeoutThreshold) {
            return;
        }
        if (this.mRadioBugStatus == 0) {
            this.mRadioBugStatus = z ? 2 : 1;
            String str = "Repeated radio error " + this.mRadioBugStatus + " on slot " + this.mSlotId;
            Rlog.d("RadioBugDetector", str);
            Phone phone = PhoneFactory.getPhone(this.mSlotId);
            AnomalyReporter.reportAnomaly(UUID.fromString("d264ead0-3f05-11ea-b77f-2e728ce88125"), str, phone == null ? -1 : phone.getCarrierId());
        }
    }

    private boolean isFrequentSystemError() {
        int i = 0;
        for (Integer num : this.mSysErrRecord.values()) {
            i += num.intValue();
            if (i >= this.mSystemErrorThreshold) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    public int getRadioBugStatus() {
        return this.mRadioBugStatus;
    }

    @VisibleForTesting
    public int getWakelockTimeoutThreshold() {
        return this.mWakelockTimeoutThreshold;
    }

    @VisibleForTesting
    public int getSystemErrorThreshold() {
        return this.mSystemErrorThreshold;
    }

    @VisibleForTesting
    public int getWakelockTimoutCount() {
        return this.mContinuousWakelockTimoutCount;
    }
}
