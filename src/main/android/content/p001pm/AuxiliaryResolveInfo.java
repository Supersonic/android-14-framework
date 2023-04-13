package android.content.p001pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.p008os.Bundle;
import android.text.format.DateFormat;
import java.util.Collections;
import java.util.List;
/* renamed from: android.content.pm.AuxiliaryResolveInfo */
/* loaded from: classes.dex */
public final class AuxiliaryResolveInfo {
    public final Intent failureIntent;
    public final List<AuxiliaryFilter> filters;
    public final int[] hostDigestPrefixSecure;
    public final ComponentName installFailureActivity;
    public final boolean needsPhaseTwo;
    public final String token;

    public AuxiliaryResolveInfo(String token, boolean needsPhase2, Intent failureIntent, List<AuxiliaryFilter> filters, int[] hostDigestPrefix) {
        this.token = token;
        this.needsPhaseTwo = needsPhase2;
        this.failureIntent = failureIntent;
        this.filters = filters;
        this.installFailureActivity = null;
        this.hostDigestPrefixSecure = hostDigestPrefix;
    }

    public AuxiliaryResolveInfo(ComponentName failureActivity, Intent failureIntent, List<AuxiliaryFilter> filters) {
        this.installFailureActivity = failureActivity;
        this.filters = filters;
        this.token = null;
        this.needsPhaseTwo = false;
        this.failureIntent = failureIntent;
        this.hostDigestPrefixSecure = null;
    }

    public AuxiliaryResolveInfo(ComponentName failureActivity, String packageName, long versionCode, String splitName) {
        this(failureActivity, null, Collections.singletonList(new AuxiliaryFilter(packageName, versionCode, splitName)));
    }

    /* renamed from: android.content.pm.AuxiliaryResolveInfo$AuxiliaryFilter */
    /* loaded from: classes.dex */
    public static final class AuxiliaryFilter extends IntentFilter {
        public final Bundle extras;
        public final String packageName;
        public final InstantAppResolveInfo resolveInfo;
        public final String splitName;
        public final long versionCode;

        public AuxiliaryFilter(IntentFilter orig, InstantAppResolveInfo resolveInfo, String splitName, Bundle extras) {
            super(orig);
            this.resolveInfo = resolveInfo;
            this.packageName = resolveInfo.getPackageName();
            this.versionCode = resolveInfo.getLongVersionCode();
            this.splitName = splitName;
            this.extras = extras;
        }

        public AuxiliaryFilter(InstantAppResolveInfo resolveInfo, String splitName, Bundle extras) {
            this.resolveInfo = resolveInfo;
            this.packageName = resolveInfo.getPackageName();
            this.versionCode = resolveInfo.getLongVersionCode();
            this.splitName = splitName;
            this.extras = extras;
        }

        public AuxiliaryFilter(String packageName, long versionCode, String splitName) {
            this.resolveInfo = null;
            this.packageName = packageName;
            this.versionCode = versionCode;
            this.splitName = splitName;
            this.extras = null;
        }

        @Override // android.content.IntentFilter
        public String toString() {
            return "AuxiliaryFilter{packageName='" + this.packageName + DateFormat.QUOTE + ", versionCode=" + this.versionCode + ", splitName='" + this.splitName + DateFormat.QUOTE + '}';
        }
    }
}
