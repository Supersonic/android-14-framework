package com.android.internal.p028os;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.util.Range;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* renamed from: com.android.internal.os.KernelCpuThreadReaderSettingsObserver */
/* loaded from: classes4.dex */
public class KernelCpuThreadReaderSettingsObserver extends ContentObserver {
    private static final String COLLECTED_UIDS_DEFAULT = "0-0;1000-1000";
    private static final String COLLECTED_UIDS_SETTINGS_KEY = "collected_uids";
    private static final int MINIMUM_TOTAL_CPU_USAGE_MILLIS_DEFAULT = 10000;
    private static final String MINIMUM_TOTAL_CPU_USAGE_MILLIS_SETTINGS_KEY = "minimum_total_cpu_usage_millis";
    private static final int NUM_BUCKETS_DEFAULT = 8;
    private static final String NUM_BUCKETS_SETTINGS_KEY = "num_buckets";
    private static final String TAG = "KernelCpuThreadReaderSettingsObserver";
    private final Context mContext;
    private final KernelCpuThreadReader mKernelCpuThreadReader;
    private final KernelCpuThreadReaderDiff mKernelCpuThreadReaderDiff;

    public static KernelCpuThreadReaderDiff getSettingsModifiedReader(Context context) {
        KernelCpuThreadReaderSettingsObserver settingsObserver = new KernelCpuThreadReaderSettingsObserver(context);
        Uri settingsUri = Settings.Global.getUriFor(Settings.Global.KERNEL_CPU_THREAD_READER);
        context.getContentResolver().registerContentObserver(settingsUri, false, settingsObserver, 0);
        return settingsObserver.mKernelCpuThreadReaderDiff;
    }

    private KernelCpuThreadReaderSettingsObserver(Context context) {
        super(BackgroundThread.getHandler());
        KernelCpuThreadReaderDiff kernelCpuThreadReaderDiff;
        this.mContext = context;
        KernelCpuThreadReader create = KernelCpuThreadReader.create(8, UidPredicate.fromString(COLLECTED_UIDS_DEFAULT));
        this.mKernelCpuThreadReader = create;
        if (create == null) {
            kernelCpuThreadReaderDiff = null;
        } else {
            kernelCpuThreadReaderDiff = new KernelCpuThreadReaderDiff(create, 10000);
        }
        this.mKernelCpuThreadReaderDiff = kernelCpuThreadReaderDiff;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange, Collection<Uri> uris, int flags, int userId) {
        updateReader();
    }

    private void updateReader() {
        if (this.mKernelCpuThreadReader == null) {
            return;
        }
        KeyValueListParser parser = new KeyValueListParser(',');
        try {
            parser.setString(Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.KERNEL_CPU_THREAD_READER));
            try {
                UidPredicate uidPredicate = UidPredicate.fromString(parser.getString(COLLECTED_UIDS_SETTINGS_KEY, COLLECTED_UIDS_DEFAULT));
                this.mKernelCpuThreadReader.setNumBuckets(parser.getInt(NUM_BUCKETS_SETTINGS_KEY, 8));
                this.mKernelCpuThreadReader.setUidPredicate(uidPredicate);
                this.mKernelCpuThreadReaderDiff.setMinimumTotalCpuUsageMillis(parser.getInt(MINIMUM_TOTAL_CPU_USAGE_MILLIS_SETTINGS_KEY, 10000));
            } catch (NumberFormatException e) {
                Slog.m89w(TAG, "Failed to get UID predicate", e);
            }
        } catch (IllegalArgumentException e2) {
            Slog.m95e(TAG, "Bad settings", e2);
        }
    }

    /* renamed from: com.android.internal.os.KernelCpuThreadReaderSettingsObserver$UidPredicate */
    /* loaded from: classes4.dex */
    public static class UidPredicate implements Predicate<Integer> {
        private static final Pattern UID_RANGE_PATTERN = Pattern.compile("([0-9]+)-([0-9]+)");
        private static final String UID_SPECIFIER_DELIMITER = ";";
        private final List<Range<Integer>> mAcceptedUidRanges;

        public static UidPredicate fromString(String predicateString) throws NumberFormatException {
            String[] split;
            List<Range<Integer>> acceptedUidRanges = new ArrayList<>();
            for (String uidSpecifier : predicateString.split(";")) {
                Matcher uidRangeMatcher = UID_RANGE_PATTERN.matcher(uidSpecifier);
                if (!uidRangeMatcher.matches()) {
                    throw new NumberFormatException("Failed to recognize as number range: " + uidSpecifier);
                }
                acceptedUidRanges.add(Range.create(Integer.valueOf(Integer.parseInt(uidRangeMatcher.group(1))), Integer.valueOf(Integer.parseInt(uidRangeMatcher.group(2)))));
            }
            return new UidPredicate(acceptedUidRanges);
        }

        private UidPredicate(List<Range<Integer>> acceptedUidRanges) {
            this.mAcceptedUidRanges = acceptedUidRanges;
        }

        @Override // java.util.function.Predicate
        public boolean test(Integer uid) {
            for (int i = 0; i < this.mAcceptedUidRanges.size(); i++) {
                if (this.mAcceptedUidRanges.get(i).contains((Range<Integer>) uid)) {
                    return true;
                }
            }
            return false;
        }
    }
}
