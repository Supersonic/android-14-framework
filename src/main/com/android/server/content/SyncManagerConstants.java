package com.android.server.content;

import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class SyncManagerConstants extends ContentObserver {
    public final Context mContext;
    public int mInitialSyncRetryTimeInSeconds;
    public int mKeyExemptionTempWhitelistDurationInSeconds;
    public final Object mLock;
    public int mMaxRetriesWithAppStandbyExemption;
    public int mMaxSyncRetryTimeInSeconds;
    public float mRetryTimeIncreaseFactor;

    public SyncManagerConstants(Context context) {
        super(null);
        this.mLock = new Object();
        this.mInitialSyncRetryTimeInSeconds = 30;
        this.mRetryTimeIncreaseFactor = 2.0f;
        this.mMaxSyncRetryTimeInSeconds = 3600;
        this.mMaxRetriesWithAppStandbyExemption = 5;
        this.mKeyExemptionTempWhitelistDurationInSeconds = 600;
        this.mContext = context;
    }

    public void start() {
        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.content.SyncManagerConstants$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SyncManagerConstants.this.lambda$start$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$start$0() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("sync_manager_constants"), false, this);
        refresh();
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        refresh();
    }

    public final void refresh() {
        synchronized (this.mLock) {
            String string = Settings.Global.getString(this.mContext.getContentResolver(), "sync_manager_constants");
            KeyValueListParser keyValueListParser = new KeyValueListParser(',');
            try {
                keyValueListParser.setString(string);
            } catch (IllegalArgumentException unused) {
                Slog.wtf("SyncManagerConfig", "Bad constants: " + string);
            }
            this.mInitialSyncRetryTimeInSeconds = keyValueListParser.getInt("initial_sync_retry_time_in_seconds", 30);
            this.mMaxSyncRetryTimeInSeconds = keyValueListParser.getInt("max_sync_retry_time_in_seconds", 3600);
            this.mRetryTimeIncreaseFactor = keyValueListParser.getFloat("retry_time_increase_factor", 2.0f);
            this.mMaxRetriesWithAppStandbyExemption = keyValueListParser.getInt("max_retries_with_app_standby_exemption", 5);
            this.mKeyExemptionTempWhitelistDurationInSeconds = keyValueListParser.getInt("exemption_temp_whitelist_duration_in_seconds", 600);
        }
    }

    public int getInitialSyncRetryTimeInSeconds() {
        int i;
        synchronized (this.mLock) {
            i = this.mInitialSyncRetryTimeInSeconds;
        }
        return i;
    }

    public float getRetryTimeIncreaseFactor() {
        float f;
        synchronized (this.mLock) {
            f = this.mRetryTimeIncreaseFactor;
        }
        return f;
    }

    public int getMaxSyncRetryTimeInSeconds() {
        int i;
        synchronized (this.mLock) {
            i = this.mMaxSyncRetryTimeInSeconds;
        }
        return i;
    }

    public int getMaxRetriesWithAppStandbyExemption() {
        int i;
        synchronized (this.mLock) {
            i = this.mMaxRetriesWithAppStandbyExemption;
        }
        return i;
    }

    public int getKeyExemptionTempWhitelistDurationInSeconds() {
        int i;
        synchronized (this.mLock) {
            i = this.mKeyExemptionTempWhitelistDurationInSeconds;
        }
        return i;
    }

    public void dump(PrintWriter printWriter, String str) {
        synchronized (this.mLock) {
            printWriter.print(str);
            printWriter.println("SyncManager Config:");
            printWriter.print(str);
            printWriter.print("  mInitialSyncRetryTimeInSeconds=");
            printWriter.println(this.mInitialSyncRetryTimeInSeconds);
            printWriter.print(str);
            printWriter.print("  mRetryTimeIncreaseFactor=");
            printWriter.println(this.mRetryTimeIncreaseFactor);
            printWriter.print(str);
            printWriter.print("  mMaxSyncRetryTimeInSeconds=");
            printWriter.println(this.mMaxSyncRetryTimeInSeconds);
            printWriter.print(str);
            printWriter.print("  mMaxRetriesWithAppStandbyExemption=");
            printWriter.println(this.mMaxRetriesWithAppStandbyExemption);
            printWriter.print(str);
            printWriter.print("  mKeyExemptionTempWhitelistDurationInSeconds=");
            printWriter.println(this.mKeyExemptionTempWhitelistDurationInSeconds);
        }
    }
}
