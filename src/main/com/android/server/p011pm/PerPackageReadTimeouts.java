package com.android.server.p011pm;

import android.text.TextUtils;
import com.android.internal.util.HexDump;
import com.android.internal.util.jobs.XmlUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* renamed from: com.android.server.pm.PerPackageReadTimeouts */
/* loaded from: classes2.dex */
public class PerPackageReadTimeouts {
    public final String packageName;
    public final byte[] sha256certificate;
    public final Timeouts timeouts;
    public final VersionCodes versionCodes;

    public static long tryParseLong(String str, long j) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException unused) {
            return j;
        }
    }

    public static byte[] tryParseSha256(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return HexDump.hexStringToByteArray(str);
        } catch (RuntimeException unused) {
            return null;
        }
    }

    /* renamed from: com.android.server.pm.PerPackageReadTimeouts$Timeouts */
    /* loaded from: classes2.dex */
    public static class Timeouts {
        public static final Timeouts DEFAULT = new Timeouts(3600000000L, 3600000000L, 3600000000L);
        public final long maxPendingTimeUs;
        public final long minPendingTimeUs;
        public final long minTimeUs;

        public Timeouts(long j, long j2, long j3) {
            this.minTimeUs = j;
            this.minPendingTimeUs = j2;
            this.maxPendingTimeUs = j3;
        }

        public static Timeouts parse(String str) {
            String[] split = str.split(XmlUtils.STRING_ARRAY_SEPARATOR, 3);
            if (split.length != 3) {
                return DEFAULT;
            }
            String str2 = split[0];
            Timeouts timeouts = DEFAULT;
            long tryParseLong = PerPackageReadTimeouts.tryParseLong(str2, timeouts.minTimeUs);
            long tryParseLong2 = PerPackageReadTimeouts.tryParseLong(split[1], timeouts.minPendingTimeUs);
            long tryParseLong3 = PerPackageReadTimeouts.tryParseLong(split[2], timeouts.maxPendingTimeUs);
            return (0 > tryParseLong || tryParseLong > tryParseLong2 || tryParseLong2 > tryParseLong3) ? timeouts : new Timeouts(tryParseLong, tryParseLong2, tryParseLong3);
        }
    }

    /* renamed from: com.android.server.pm.PerPackageReadTimeouts$VersionCodes */
    /* loaded from: classes2.dex */
    public static class VersionCodes {
        public static final VersionCodes ALL_VERSION_CODES = new VersionCodes(Long.MIN_VALUE, Long.MAX_VALUE);
        public final long maxVersionCode;
        public final long minVersionCode;

        public VersionCodes(long j, long j2) {
            this.minVersionCode = j;
            this.maxVersionCode = j2;
        }

        public static VersionCodes parse(String str) {
            if (TextUtils.isEmpty(str)) {
                return ALL_VERSION_CODES;
            }
            String[] split = str.split(PackageManagerShellCommandDataLoader.STDIN_PATH, 2);
            int length = split.length;
            if (length == 1) {
                try {
                    long parseLong = Long.parseLong(split[0]);
                    return new VersionCodes(parseLong, parseLong);
                } catch (NumberFormatException unused) {
                    return ALL_VERSION_CODES;
                }
            }
            if (length == 2) {
                String str2 = split[0];
                VersionCodes versionCodes = ALL_VERSION_CODES;
                long tryParseLong = PerPackageReadTimeouts.tryParseLong(str2, versionCodes.minVersionCode);
                long tryParseLong2 = PerPackageReadTimeouts.tryParseLong(split[1], versionCodes.maxVersionCode);
                if (tryParseLong <= tryParseLong2) {
                    return new VersionCodes(tryParseLong, tryParseLong2);
                }
            }
            return ALL_VERSION_CODES;
        }
    }

    public PerPackageReadTimeouts(String str, byte[] bArr, VersionCodes versionCodes, Timeouts timeouts) {
        this.packageName = str;
        this.sha256certificate = bArr;
        this.versionCodes = versionCodes;
        this.timeouts = timeouts;
    }

    public static PerPackageReadTimeouts parse(String str, VersionCodes versionCodes, Timeouts timeouts) {
        byte[] bArr;
        String[] split = str.split(XmlUtils.STRING_ARRAY_SEPARATOR, 4);
        int length = split.length;
        if (length != 1) {
            if (length != 2) {
                if (length != 3) {
                    if (length != 4) {
                        return null;
                    }
                    timeouts = Timeouts.parse(split[3]);
                }
                versionCodes = VersionCodes.parse(split[2]);
            }
            bArr = tryParseSha256(split[1]);
        } else {
            bArr = null;
        }
        String str2 = split[0];
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        return new PerPackageReadTimeouts(str2, bArr, versionCodes, timeouts);
    }

    public static List<PerPackageReadTimeouts> parseDigestersList(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            return Collections.emptyList();
        }
        VersionCodes versionCodes = VersionCodes.ALL_VERSION_CODES;
        Timeouts parse = Timeouts.parse(str);
        String[] split = str2.split(",");
        ArrayList arrayList = new ArrayList(split.length);
        for (String str3 : split) {
            PerPackageReadTimeouts parse2 = parse(str3, versionCodes, parse);
            if (parse2 != null) {
                arrayList.add(parse2);
            }
        }
        return arrayList;
    }
}
