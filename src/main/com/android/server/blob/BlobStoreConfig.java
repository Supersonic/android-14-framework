package com.android.server.blob;

import android.content.Context;
import android.os.Environment;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DataUnit;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.server.blob.BlobStoreConfig;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class BlobStoreConfig {
    public static final boolean LOGV = Log.isLoggable("BlobStore", 2);

    /* loaded from: classes.dex */
    public static class DeviceConfigProperties {
        public static long COMMIT_COOL_OFF_DURATION_MS;
        public static final long DEFAULT_COMMIT_COOL_OFF_DURATION_MS;
        public static final long DEFAULT_DELETE_ON_LAST_LEASE_DELAY_MS;
        public static final long DEFAULT_IDLE_JOB_PERIOD_MS;
        public static final long DEFAULT_LEASE_ACQUISITION_WAIT_DURATION_MS;
        public static int DEFAULT_LEASE_DESC_CHAR_LIMIT;
        public static int DEFAULT_MAX_ACTIVE_SESSIONS;
        public static int DEFAULT_MAX_BLOB_ACCESS_PERMITTED_PACKAGES;
        public static int DEFAULT_MAX_COMMITTED_BLOBS;
        public static int DEFAULT_MAX_LEASED_BLOBS;
        public static final long DEFAULT_SESSION_EXPIRY_TIMEOUT_MS;
        public static final long DEFAULT_TOTAL_BYTES_PER_APP_LIMIT_FLOOR;
        public static long DELETE_ON_LAST_LEASE_DELAY_MS;
        public static long IDLE_JOB_PERIOD_MS;
        public static long LEASE_ACQUISITION_WAIT_DURATION_MS;
        public static int LEASE_DESC_CHAR_LIMIT;
        public static int MAX_ACTIVE_SESSIONS;
        public static int MAX_BLOB_ACCESS_PERMITTED_PACKAGES;
        public static int MAX_COMMITTED_BLOBS;
        public static int MAX_LEASED_BLOBS;
        public static long SESSION_EXPIRY_TIMEOUT_MS;
        public static long TOTAL_BYTES_PER_APP_LIMIT_FLOOR;
        public static float TOTAL_BYTES_PER_APP_LIMIT_FRACTION;
        public static boolean USE_REVOCABLE_FD_FOR_READS;

        static {
            TimeUnit timeUnit = TimeUnit.DAYS;
            long millis = timeUnit.toMillis(1L);
            DEFAULT_IDLE_JOB_PERIOD_MS = millis;
            IDLE_JOB_PERIOD_MS = millis;
            long millis2 = timeUnit.toMillis(7L);
            DEFAULT_SESSION_EXPIRY_TIMEOUT_MS = millis2;
            SESSION_EXPIRY_TIMEOUT_MS = millis2;
            long bytes = DataUnit.MEBIBYTES.toBytes(300L);
            DEFAULT_TOTAL_BYTES_PER_APP_LIMIT_FLOOR = bytes;
            TOTAL_BYTES_PER_APP_LIMIT_FLOOR = bytes;
            TOTAL_BYTES_PER_APP_LIMIT_FRACTION = 0.01f;
            TimeUnit timeUnit2 = TimeUnit.HOURS;
            long millis3 = timeUnit2.toMillis(6L);
            DEFAULT_LEASE_ACQUISITION_WAIT_DURATION_MS = millis3;
            LEASE_ACQUISITION_WAIT_DURATION_MS = millis3;
            long millis4 = timeUnit2.toMillis(48L);
            DEFAULT_COMMIT_COOL_OFF_DURATION_MS = millis4;
            COMMIT_COOL_OFF_DURATION_MS = millis4;
            USE_REVOCABLE_FD_FOR_READS = false;
            long millis5 = timeUnit2.toMillis(6L);
            DEFAULT_DELETE_ON_LAST_LEASE_DELAY_MS = millis5;
            DELETE_ON_LAST_LEASE_DELAY_MS = millis5;
            DEFAULT_MAX_ACTIVE_SESSIONS = 250;
            MAX_ACTIVE_SESSIONS = 250;
            DEFAULT_MAX_COMMITTED_BLOBS = 1000;
            MAX_COMMITTED_BLOBS = 1000;
            DEFAULT_MAX_LEASED_BLOBS = 500;
            MAX_LEASED_BLOBS = 500;
            DEFAULT_MAX_BLOB_ACCESS_PERMITTED_PACKAGES = 300;
            MAX_BLOB_ACCESS_PERMITTED_PACKAGES = 300;
            DEFAULT_LEASE_DESC_CHAR_LIMIT = 300;
            LEASE_DESC_CHAR_LIMIT = 300;
        }

        public static void refresh(final DeviceConfig.Properties properties) {
            if ("blobstore".equals(properties.getNamespace())) {
                properties.getKeyset().forEach(new Consumer() { // from class: com.android.server.blob.BlobStoreConfig$DeviceConfigProperties$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        BlobStoreConfig.DeviceConfigProperties.lambda$refresh$0(properties, (String) obj);
                    }
                });
            }
        }

        public static /* synthetic */ void lambda$refresh$0(DeviceConfig.Properties properties, String str) {
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -1925137189:
                    if (str.equals("max_active_sessions")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1390984180:
                    if (str.equals("use_revocable_fd_for_reads")) {
                        c = 1;
                        break;
                    }
                    break;
                case -409761467:
                    if (str.equals("lease_acquisition_wait_time_ms")) {
                        c = 2;
                        break;
                    }
                    break;
                case -284382148:
                    if (str.equals("max_leased_blobs")) {
                        c = 3;
                        break;
                    }
                    break;
                case 12210070:
                    if (str.equals("max_permitted_pks")) {
                        c = 4;
                        break;
                    }
                    break;
                case 271099507:
                    if (str.equals("commit_cool_off_duration_ms")) {
                        c = 5;
                        break;
                    }
                    break;
                case 964101945:
                    if (str.equals("total_bytes_per_app_limit_floor")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1401805851:
                    if (str.equals("lease_desc_char_limit")) {
                        c = 7;
                        break;
                    }
                    break;
                case 1419134232:
                    if (str.equals("max_committed_blobs")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 1733063605:
                    if (str.equals("total_bytes_per_app_limit_fraction")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 1838729636:
                    if (str.equals("delete_on_last_lease_delay_ms")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 1903375799:
                    if (str.equals("idle_job_period_ms")) {
                        c = 11;
                        break;
                    }
                    break;
                case 2022996583:
                    if (str.equals("session_expiry_timeout_ms")) {
                        c = '\f';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    MAX_ACTIVE_SESSIONS = properties.getInt(str, DEFAULT_MAX_ACTIVE_SESSIONS);
                    return;
                case 1:
                    USE_REVOCABLE_FD_FOR_READS = properties.getBoolean(str, false);
                    return;
                case 2:
                    LEASE_ACQUISITION_WAIT_DURATION_MS = properties.getLong(str, DEFAULT_LEASE_ACQUISITION_WAIT_DURATION_MS);
                    return;
                case 3:
                    MAX_LEASED_BLOBS = properties.getInt(str, DEFAULT_MAX_LEASED_BLOBS);
                    return;
                case 4:
                    MAX_BLOB_ACCESS_PERMITTED_PACKAGES = properties.getInt(str, DEFAULT_MAX_BLOB_ACCESS_PERMITTED_PACKAGES);
                    return;
                case 5:
                    COMMIT_COOL_OFF_DURATION_MS = properties.getLong(str, DEFAULT_COMMIT_COOL_OFF_DURATION_MS);
                    return;
                case 6:
                    TOTAL_BYTES_PER_APP_LIMIT_FLOOR = properties.getLong(str, DEFAULT_TOTAL_BYTES_PER_APP_LIMIT_FLOOR);
                    return;
                case 7:
                    LEASE_DESC_CHAR_LIMIT = properties.getInt(str, DEFAULT_LEASE_DESC_CHAR_LIMIT);
                    return;
                case '\b':
                    MAX_COMMITTED_BLOBS = properties.getInt(str, DEFAULT_MAX_COMMITTED_BLOBS);
                    return;
                case '\t':
                    TOTAL_BYTES_PER_APP_LIMIT_FRACTION = properties.getFloat(str, 0.01f);
                    return;
                case '\n':
                    DELETE_ON_LAST_LEASE_DELAY_MS = properties.getLong(str, DEFAULT_DELETE_ON_LAST_LEASE_DELAY_MS);
                    return;
                case 11:
                    IDLE_JOB_PERIOD_MS = properties.getLong(str, DEFAULT_IDLE_JOB_PERIOD_MS);
                    return;
                case '\f':
                    SESSION_EXPIRY_TIMEOUT_MS = properties.getLong(str, DEFAULT_SESSION_EXPIRY_TIMEOUT_MS);
                    return;
                default:
                    Slog.wtf("BlobStore", "Unknown key in device config properties: " + str);
                    return;
            }
        }

        public static void dump(IndentingPrintWriter indentingPrintWriter, Context context) {
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "idle_job_period_ms", TimeUtils.formatDuration(IDLE_JOB_PERIOD_MS), TimeUtils.formatDuration(DEFAULT_IDLE_JOB_PERIOD_MS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "session_expiry_timeout_ms", TimeUtils.formatDuration(SESSION_EXPIRY_TIMEOUT_MS), TimeUtils.formatDuration(DEFAULT_SESSION_EXPIRY_TIMEOUT_MS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "total_bytes_per_app_limit_floor", Formatter.formatFileSize(context, TOTAL_BYTES_PER_APP_LIMIT_FLOOR, 8), Formatter.formatFileSize(context, DEFAULT_TOTAL_BYTES_PER_APP_LIMIT_FLOOR, 8)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "total_bytes_per_app_limit_fraction", Float.valueOf(TOTAL_BYTES_PER_APP_LIMIT_FRACTION), Float.valueOf(0.01f)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "lease_acquisition_wait_time_ms", TimeUtils.formatDuration(LEASE_ACQUISITION_WAIT_DURATION_MS), TimeUtils.formatDuration(DEFAULT_LEASE_ACQUISITION_WAIT_DURATION_MS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "commit_cool_off_duration_ms", TimeUtils.formatDuration(COMMIT_COOL_OFF_DURATION_MS), TimeUtils.formatDuration(DEFAULT_COMMIT_COOL_OFF_DURATION_MS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "use_revocable_fd_for_reads", Boolean.valueOf(USE_REVOCABLE_FD_FOR_READS), Boolean.FALSE));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "delete_on_last_lease_delay_ms", TimeUtils.formatDuration(DELETE_ON_LAST_LEASE_DELAY_MS), TimeUtils.formatDuration(DEFAULT_DELETE_ON_LAST_LEASE_DELAY_MS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "max_active_sessions", Integer.valueOf(MAX_ACTIVE_SESSIONS), Integer.valueOf(DEFAULT_MAX_ACTIVE_SESSIONS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "max_committed_blobs", Integer.valueOf(MAX_COMMITTED_BLOBS), Integer.valueOf(DEFAULT_MAX_COMMITTED_BLOBS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "max_leased_blobs", Integer.valueOf(MAX_LEASED_BLOBS), Integer.valueOf(DEFAULT_MAX_LEASED_BLOBS)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "max_permitted_pks", Integer.valueOf(MAX_BLOB_ACCESS_PERMITTED_PACKAGES), Integer.valueOf(DEFAULT_MAX_BLOB_ACCESS_PERMITTED_PACKAGES)));
            indentingPrintWriter.println(String.format("%s: [cur: %s, def: %s]", "lease_desc_char_limit", Integer.valueOf(LEASE_DESC_CHAR_LIMIT), Integer.valueOf(DEFAULT_LEASE_DESC_CHAR_LIMIT)));
        }
    }

    public static void initialize(Context context) {
        DeviceConfig.addOnPropertiesChangedListener("blobstore", context.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.blob.BlobStoreConfig$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                BlobStoreConfig.DeviceConfigProperties.refresh(properties);
            }
        });
        DeviceConfigProperties.refresh(DeviceConfig.getProperties("blobstore", new String[0]));
    }

    public static long getIdleJobPeriodMs() {
        return DeviceConfigProperties.IDLE_JOB_PERIOD_MS;
    }

    public static boolean hasSessionExpired(long j) {
        return j < System.currentTimeMillis() - DeviceConfigProperties.SESSION_EXPIRY_TIMEOUT_MS;
    }

    public static long getAppDataBytesLimit() {
        return Math.max(DeviceConfigProperties.TOTAL_BYTES_PER_APP_LIMIT_FLOOR, ((float) Environment.getDataSystemDirectory().getTotalSpace()) * DeviceConfigProperties.TOTAL_BYTES_PER_APP_LIMIT_FRACTION);
    }

    public static boolean hasLeaseWaitTimeElapsed(long j) {
        return j + DeviceConfigProperties.LEASE_ACQUISITION_WAIT_DURATION_MS < System.currentTimeMillis();
    }

    public static long getAdjustedCommitTimeMs(long j, long j2) {
        return (j == 0 || hasCommitCoolOffPeriodElapsed(j)) ? j2 : j;
    }

    public static boolean hasCommitCoolOffPeriodElapsed(long j) {
        return j + DeviceConfigProperties.COMMIT_COOL_OFF_DURATION_MS < System.currentTimeMillis();
    }

    public static boolean shouldUseRevocableFdForReads() {
        return DeviceConfigProperties.USE_REVOCABLE_FD_FOR_READS;
    }

    public static long getDeletionOnLastLeaseDelayMs() {
        return DeviceConfigProperties.DELETE_ON_LAST_LEASE_DELAY_MS;
    }

    public static int getMaxActiveSessions() {
        return DeviceConfigProperties.MAX_ACTIVE_SESSIONS;
    }

    public static int getMaxCommittedBlobs() {
        return DeviceConfigProperties.MAX_COMMITTED_BLOBS;
    }

    public static int getMaxLeasedBlobs() {
        return DeviceConfigProperties.MAX_LEASED_BLOBS;
    }

    public static int getMaxPermittedPackages() {
        return DeviceConfigProperties.MAX_BLOB_ACCESS_PERMITTED_PACKAGES;
    }

    public static CharSequence getTruncatedLeaseDescription(CharSequence charSequence) {
        return TextUtils.isEmpty(charSequence) ? charSequence : TextUtils.trimToLengthWithEllipsis(charSequence, DeviceConfigProperties.LEASE_DESC_CHAR_LIMIT);
    }

    public static File prepareBlobFile(long j) {
        File prepareBlobsDir = prepareBlobsDir();
        if (prepareBlobsDir == null) {
            return null;
        }
        return getBlobFile(prepareBlobsDir, j);
    }

    public static File getBlobFile(long j) {
        return getBlobFile(getBlobsDir(), j);
    }

    public static File getBlobFile(File file, long j) {
        return new File(file, String.valueOf(j));
    }

    public static File prepareBlobsDir() {
        File blobsDir = getBlobsDir(prepareBlobStoreRootDir());
        if (blobsDir.exists() || blobsDir.mkdir()) {
            return blobsDir;
        }
        Slog.e("BlobStore", "Failed to mkdir(): " + blobsDir);
        return null;
    }

    public static File getBlobsDir() {
        return getBlobsDir(getBlobStoreRootDir());
    }

    public static File getBlobsDir(File file) {
        return new File(file, "blobs");
    }

    public static File prepareSessionIndexFile() {
        File prepareBlobStoreRootDir = prepareBlobStoreRootDir();
        if (prepareBlobStoreRootDir == null) {
            return null;
        }
        return new File(prepareBlobStoreRootDir, "sessions_index.xml");
    }

    public static File prepareBlobsIndexFile() {
        File prepareBlobStoreRootDir = prepareBlobStoreRootDir();
        if (prepareBlobStoreRootDir == null) {
            return null;
        }
        return new File(prepareBlobStoreRootDir, "blobs_index.xml");
    }

    public static File prepareBlobStoreRootDir() {
        File blobStoreRootDir = getBlobStoreRootDir();
        if (blobStoreRootDir.exists() || blobStoreRootDir.mkdir()) {
            return blobStoreRootDir;
        }
        Slog.e("BlobStore", "Failed to mkdir(): " + blobStoreRootDir);
        return null;
    }

    public static File getBlobStoreRootDir() {
        return new File(Environment.getDataSystemDirectory(), "blobstore");
    }

    public static void dump(IndentingPrintWriter indentingPrintWriter, Context context) {
        indentingPrintWriter.println("XML current version: 6");
        indentingPrintWriter.println("Idle job ID: 191934935");
        indentingPrintWriter.println("Total bytes per app limit: " + Formatter.formatFileSize(context, getAppDataBytesLimit(), 8));
        indentingPrintWriter.println("Device config properties:");
        indentingPrintWriter.increaseIndent();
        DeviceConfigProperties.dump(indentingPrintWriter, context);
        indentingPrintWriter.decreaseIndent();
    }
}
