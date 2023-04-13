package com.android.server.location.injector;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.PackageTagsList;
import android.os.RemoteException;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.FgThread;
import com.android.server.SystemConfig;
import com.android.server.location.LocationManagerService;
import com.android.server.location.injector.SettingsHelper;
import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class SystemSettingsHelper extends SettingsHelper {
    public final LongGlobalSetting mBackgroundThrottleIntervalMs;
    public final StringSetCachedGlobalSetting mBackgroundThrottlePackageWhitelist;
    public final Context mContext;
    public final BooleanGlobalSetting mGnssMeasurementFullTracking;
    public final IntegerSecureSetting mLocationMode;
    public final StringListCachedSecureSetting mLocationPackageBlacklist;
    public final StringListCachedSecureSetting mLocationPackageWhitelist;
    public final PackageTagsListSetting mAdasPackageAllowlist = new PackageTagsListSetting("adas_settings_allowlist", new Supplier() { // from class: com.android.server.location.injector.SystemSettingsHelper$$ExternalSyntheticLambda1
        @Override // java.util.function.Supplier
        public final Object get() {
            ArrayMap lambda$new$1;
            lambda$new$1 = SystemSettingsHelper.lambda$new$1();
            return lambda$new$1;
        }
    });
    public final PackageTagsListSetting mIgnoreSettingsPackageAllowlist = new PackageTagsListSetting("ignore_settings_allowlist", new Supplier() { // from class: com.android.server.location.injector.SystemSettingsHelper$$ExternalSyntheticLambda2
        @Override // java.util.function.Supplier
        public final Object get() {
            ArrayMap lambda$new$2;
            lambda$new$2 = SystemSettingsHelper.lambda$new$2();
            return lambda$new$2;
        }
    });

    public SystemSettingsHelper(Context context) {
        this.mContext = context;
        this.mLocationMode = new IntegerSecureSetting(context, "location_mode", FgThread.getHandler());
        this.mBackgroundThrottleIntervalMs = new LongGlobalSetting(context, "location_background_throttle_interval_ms", FgThread.getHandler());
        this.mGnssMeasurementFullTracking = new BooleanGlobalSetting(context, "enable_gnss_raw_meas_full_tracking", FgThread.getHandler());
        this.mLocationPackageBlacklist = new StringListCachedSecureSetting(context, "locationPackagePrefixBlacklist", FgThread.getHandler());
        this.mLocationPackageWhitelist = new StringListCachedSecureSetting(context, "locationPackagePrefixWhitelist", FgThread.getHandler());
        this.mBackgroundThrottlePackageWhitelist = new StringSetCachedGlobalSetting(context, "location_background_throttle_package_whitelist", new Supplier() { // from class: com.android.server.location.injector.SystemSettingsHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                ArraySet lambda$new$0;
                lambda$new$0 = SystemSettingsHelper.lambda$new$0();
                return lambda$new$0;
            }
        }, FgThread.getHandler());
    }

    public static /* synthetic */ ArraySet lambda$new$0() {
        return SystemConfig.getInstance().getAllowUnthrottledLocation();
    }

    public static /* synthetic */ ArrayMap lambda$new$1() {
        return SystemConfig.getInstance().getAllowAdasLocationSettings();
    }

    public static /* synthetic */ ArrayMap lambda$new$2() {
        return SystemConfig.getInstance().getAllowIgnoreLocationSettings();
    }

    public void onSystemReady() {
        this.mLocationMode.register();
        this.mBackgroundThrottleIntervalMs.register();
        this.mLocationPackageBlacklist.register();
        this.mLocationPackageWhitelist.register();
        this.mBackgroundThrottlePackageWhitelist.register();
        this.mIgnoreSettingsPackageAllowlist.register();
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public boolean isLocationEnabled(int i) {
        return this.mLocationMode.getValueForUser(0, i) != 0;
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void setLocationEnabled(boolean z, int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "location_mode", z ? 3 : 0, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void addOnLocationEnabledChangedListener(SettingsHelper.UserSettingChangedListener userSettingChangedListener) {
        this.mLocationMode.addListener(userSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void removeOnLocationEnabledChangedListener(SettingsHelper.UserSettingChangedListener userSettingChangedListener) {
        this.mLocationMode.removeListener(userSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public long getBackgroundThrottleIntervalMs() {
        return this.mBackgroundThrottleIntervalMs.getValue(1800000L);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void addOnBackgroundThrottleIntervalChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mBackgroundThrottleIntervalMs.addListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void removeOnBackgroundThrottleIntervalChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mBackgroundThrottleIntervalMs.removeListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public boolean isLocationPackageBlacklisted(int i, String str) {
        List<String> valueForUser = this.mLocationPackageBlacklist.getValueForUser(i);
        if (valueForUser.isEmpty()) {
            return false;
        }
        for (String str2 : this.mLocationPackageWhitelist.getValueForUser(i)) {
            if (str.startsWith(str2)) {
                return false;
            }
        }
        for (String str3 : valueForUser) {
            if (str.startsWith(str3)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void addOnLocationPackageBlacklistChangedListener(SettingsHelper.UserSettingChangedListener userSettingChangedListener) {
        this.mLocationPackageBlacklist.addListener(userSettingChangedListener);
        this.mLocationPackageWhitelist.addListener(userSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void removeOnLocationPackageBlacklistChangedListener(SettingsHelper.UserSettingChangedListener userSettingChangedListener) {
        this.mLocationPackageBlacklist.removeListener(userSettingChangedListener);
        this.mLocationPackageWhitelist.removeListener(userSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public Set<String> getBackgroundThrottlePackageWhitelist() {
        return this.mBackgroundThrottlePackageWhitelist.getValue();
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void addOnBackgroundThrottlePackageWhitelistChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mBackgroundThrottlePackageWhitelist.addListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void removeOnBackgroundThrottlePackageWhitelistChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mBackgroundThrottlePackageWhitelist.removeListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public boolean isGnssMeasurementsFullTrackingEnabled() {
        return this.mGnssMeasurementFullTracking.getValue(false);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void addOnGnssMeasurementsFullTrackingEnabledChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mGnssMeasurementFullTracking.addListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void removeOnGnssMeasurementsFullTrackingEnabledChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mGnssMeasurementFullTracking.removeListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public PackageTagsList getAdasAllowlist() {
        return this.mAdasPackageAllowlist.getValue();
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void addAdasAllowlistChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mAdasPackageAllowlist.addListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void removeAdasAllowlistChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mAdasPackageAllowlist.removeListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public PackageTagsList getIgnoreSettingsAllowlist() {
        return this.mIgnoreSettingsPackageAllowlist.getValue();
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void addIgnoreSettingsAllowlistChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mIgnoreSettingsPackageAllowlist.addListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void removeIgnoreSettingsAllowlistChangedListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
        this.mIgnoreSettingsPackageAllowlist.removeListener(globalSettingChangedListener);
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public long getBackgroundThrottleProximityAlertIntervalMs() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return Settings.Global.getLong(this.mContext.getContentResolver(), "location_background_throttle_proximity_alert_interval_ms", 1800000L);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public float getCoarseLocationAccuracyM() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        ContentResolver contentResolver = this.mContext.getContentResolver();
        try {
            return Settings.Secure.getFloatForUser(contentResolver, "locationCoarseAccuracy", 2000.0f, contentResolver.getUserId());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @Override // com.android.server.location.injector.SettingsHelper
    public void dump(FileDescriptor fileDescriptor, IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        try {
            int[] runningUserIds = ActivityManager.getService().getRunningUserIds();
            indentingPrintWriter.print("Location Setting: ");
            indentingPrintWriter.increaseIndent();
            if (runningUserIds.length > 1) {
                indentingPrintWriter.println();
                for (int i : runningUserIds) {
                    indentingPrintWriter.print("[u");
                    indentingPrintWriter.print(i);
                    indentingPrintWriter.print("] ");
                    indentingPrintWriter.println(isLocationEnabled(i));
                }
            } else {
                indentingPrintWriter.println(isLocationEnabled(runningUserIds[0]));
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Location Allow/Deny Packages:");
            indentingPrintWriter.increaseIndent();
            if (runningUserIds.length > 1) {
                for (int i2 : runningUserIds) {
                    List<String> valueForUser = this.mLocationPackageBlacklist.getValueForUser(i2);
                    if (!valueForUser.isEmpty()) {
                        indentingPrintWriter.print("user ");
                        indentingPrintWriter.print(i2);
                        indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        indentingPrintWriter.increaseIndent();
                        for (String str : valueForUser) {
                            indentingPrintWriter.print("[deny] ");
                            indentingPrintWriter.println(str);
                        }
                        for (String str2 : this.mLocationPackageWhitelist.getValueForUser(i2)) {
                            indentingPrintWriter.print("[allow] ");
                            indentingPrintWriter.println(str2);
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                }
            } else {
                for (String str3 : this.mLocationPackageBlacklist.getValueForUser(runningUserIds[0])) {
                    indentingPrintWriter.print("[deny] ");
                    indentingPrintWriter.println(str3);
                }
                for (String str4 : this.mLocationPackageWhitelist.getValueForUser(runningUserIds[0])) {
                    indentingPrintWriter.print("[allow] ");
                    indentingPrintWriter.println(str4);
                }
            }
            indentingPrintWriter.decreaseIndent();
            Set<String> value = this.mBackgroundThrottlePackageWhitelist.getValue();
            if (!value.isEmpty()) {
                indentingPrintWriter.println("Throttling Allow Packages:");
                indentingPrintWriter.increaseIndent();
                for (String str5 : value) {
                    indentingPrintWriter.println(str5);
                }
                indentingPrintWriter.decreaseIndent();
            }
            PackageTagsList value2 = this.mIgnoreSettingsPackageAllowlist.getValue();
            if (!value2.isEmpty()) {
                indentingPrintWriter.println("Emergency Bypass Allow Packages:");
                indentingPrintWriter.increaseIndent();
                value2.dump(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
            }
            PackageTagsList value3 = this.mAdasPackageAllowlist.getValue();
            if (value3.isEmpty()) {
                return;
            }
            indentingPrintWriter.println("ADAS Bypass Allow Packages:");
            indentingPrintWriter.increaseIndent();
            value3.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ObservingSetting extends ContentObserver {
        public final CopyOnWriteArrayList<SettingsHelper.UserSettingChangedListener> mListeners;
        @GuardedBy({"this"})
        public boolean mRegistered;

        public ObservingSetting(Handler handler) {
            super(handler);
            this.mListeners = new CopyOnWriteArrayList<>();
        }

        public synchronized boolean isRegistered() {
            return this.mRegistered;
        }

        public synchronized void register(Context context, Uri uri) {
            if (this.mRegistered) {
                return;
            }
            context.getContentResolver().registerContentObserver(uri, false, this, -1);
            this.mRegistered = true;
        }

        public void addListener(SettingsHelper.UserSettingChangedListener userSettingChangedListener) {
            this.mListeners.add(userSettingChangedListener);
        }

        public void removeListener(SettingsHelper.UserSettingChangedListener userSettingChangedListener) {
            this.mListeners.remove(userSettingChangedListener);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            if (LocationManagerService.f1147D) {
                Log.d("LocationManagerService", "location setting changed [u" + i + "]: " + uri);
            }
            Iterator<SettingsHelper.UserSettingChangedListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onSettingChanged(i);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class IntegerSecureSetting extends ObservingSetting {
        public final Context mContext;
        public final String mSettingName;

        public IntegerSecureSetting(Context context, String str, Handler handler) {
            super(handler);
            this.mContext = context;
            this.mSettingName = str;
        }

        public void register() {
            register(this.mContext, Settings.Secure.getUriFor(this.mSettingName));
        }

        public int getValueForUser(int i, int i2) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), this.mSettingName, i, i2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class StringListCachedSecureSetting extends ObservingSetting {
        @GuardedBy({"this"})
        public int mCachedUserId;
        @GuardedBy({"this"})
        public List<String> mCachedValue;
        public final Context mContext;
        public final String mSettingName;

        public StringListCachedSecureSetting(Context context, String str, Handler handler) {
            super(handler);
            this.mContext = context;
            this.mSettingName = str;
            this.mCachedUserId = -10000;
        }

        public void register() {
            register(this.mContext, Settings.Secure.getUriFor(this.mSettingName));
        }

        public synchronized List<String> getValueForUser(int i) {
            List<String> list;
            List<String> asList;
            Preconditions.checkArgument(i != -10000);
            list = this.mCachedValue;
            if (i != this.mCachedUserId) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), this.mSettingName, i);
                if (TextUtils.isEmpty(stringForUser)) {
                    asList = Collections.emptyList();
                } else {
                    asList = Arrays.asList(stringForUser.split(","));
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                if (isRegistered()) {
                    this.mCachedUserId = i;
                    this.mCachedValue = asList;
                }
                list = asList;
            }
            return list;
        }

        public synchronized void invalidateForUser(int i) {
            if (this.mCachedUserId == i) {
                this.mCachedUserId = -10000;
                this.mCachedValue = null;
            }
        }

        @Override // com.android.server.location.injector.SystemSettingsHelper.ObservingSetting, android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            invalidateForUser(i);
            super.onChange(z, uri, i);
        }
    }

    /* loaded from: classes.dex */
    public static class BooleanGlobalSetting extends ObservingSetting {
        public final Context mContext;
        public final String mSettingName;

        public BooleanGlobalSetting(Context context, String str, Handler handler) {
            super(handler);
            this.mContext = context;
            this.mSettingName = str;
        }

        public boolean getValue(boolean z) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return Settings.Global.getInt(this.mContext.getContentResolver(), this.mSettingName, z ? 1 : 0) != 0;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class LongGlobalSetting extends ObservingSetting {
        public final Context mContext;
        public final String mSettingName;

        public LongGlobalSetting(Context context, String str, Handler handler) {
            super(handler);
            this.mContext = context;
            this.mSettingName = str;
        }

        public void register() {
            register(this.mContext, Settings.Global.getUriFor(this.mSettingName));
        }

        public long getValue(long j) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return Settings.Global.getLong(this.mContext.getContentResolver(), this.mSettingName, j);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class StringSetCachedGlobalSetting extends ObservingSetting {
        public final Supplier<ArraySet<String>> mBaseValuesSupplier;
        @GuardedBy({"this"})
        public ArraySet<String> mCachedValue;
        public final Context mContext;
        public final String mSettingName;
        @GuardedBy({"this"})
        public boolean mValid;

        public StringSetCachedGlobalSetting(Context context, String str, Supplier<ArraySet<String>> supplier, Handler handler) {
            super(handler);
            this.mContext = context;
            this.mSettingName = str;
            this.mBaseValuesSupplier = supplier;
            this.mValid = false;
        }

        public void register() {
            register(this.mContext, Settings.Global.getUriFor(this.mSettingName));
        }

        public synchronized Set<String> getValue() {
            ArraySet<String> arraySet;
            arraySet = this.mCachedValue;
            if (!this.mValid) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                ArraySet<String> arraySet2 = new ArraySet<>(this.mBaseValuesSupplier.get());
                String string = Settings.Global.getString(this.mContext.getContentResolver(), this.mSettingName);
                if (!TextUtils.isEmpty(string)) {
                    arraySet2.addAll(Arrays.asList(string.split(",")));
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                if (isRegistered()) {
                    this.mValid = true;
                    this.mCachedValue = arraySet2;
                }
                arraySet = arraySet2;
            }
            return arraySet;
        }

        public synchronized void invalidate() {
            this.mValid = false;
            this.mCachedValue = null;
        }

        @Override // com.android.server.location.injector.SystemSettingsHelper.ObservingSetting, android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            invalidate();
            super.onChange(z, uri, i);
        }
    }

    /* loaded from: classes.dex */
    public static class DeviceConfigSetting implements DeviceConfig.OnPropertiesChangedListener {
        public final CopyOnWriteArrayList<SettingsHelper.GlobalSettingChangedListener> mListeners = new CopyOnWriteArrayList<>();
        public final String mName;
        @GuardedBy({"this"})
        public boolean mRegistered;

        public DeviceConfigSetting(String str) {
            this.mName = str;
        }

        public synchronized boolean isRegistered() {
            return this.mRegistered;
        }

        public synchronized void register() {
            if (this.mRegistered) {
                return;
            }
            DeviceConfig.addOnPropertiesChangedListener("location", FgThread.getExecutor(), this);
            this.mRegistered = true;
        }

        public void addListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
            this.mListeners.add(globalSettingChangedListener);
        }

        public void removeListener(SettingsHelper.GlobalSettingChangedListener globalSettingChangedListener) {
            this.mListeners.remove(globalSettingChangedListener);
        }

        public final void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (properties.getKeyset().contains(this.mName)) {
                onPropertiesChanged();
            }
        }

        public void onPropertiesChanged() {
            if (LocationManagerService.f1147D) {
                Log.d("LocationManagerService", "location device config setting changed: " + this.mName);
            }
            Iterator<SettingsHelper.GlobalSettingChangedListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onSettingChanged(-1);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class PackageTagsListSetting extends DeviceConfigSetting {
        public final Supplier<ArrayMap<String, ArraySet<String>>> mBaseValuesSupplier;
        @GuardedBy({"this"})
        public PackageTagsList mCachedValue;
        @GuardedBy({"this"})
        public boolean mValid;

        public PackageTagsListSetting(String str, Supplier<ArrayMap<String, ArraySet<String>>> supplier) {
            super(str);
            this.mBaseValuesSupplier = supplier;
        }

        public synchronized PackageTagsList getValue() {
            PackageTagsList packageTagsList;
            String[] split;
            packageTagsList = this.mCachedValue;
            if (!this.mValid) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                PackageTagsList.Builder add = new PackageTagsList.Builder().add(this.mBaseValuesSupplier.get());
                String property = DeviceConfig.getProperty("location", this.mName);
                if (!TextUtils.isEmpty(property)) {
                    for (String str : property.split(",")) {
                        if (!TextUtils.isEmpty(str)) {
                            String[] split2 = str.split(";");
                            String str2 = split2[0];
                            if (split2.length == 1) {
                                add.add(str2);
                            } else {
                                for (int i = 1; i < split2.length; i++) {
                                    String str3 = split2[i];
                                    if ("null".equals(str3)) {
                                        str3 = null;
                                    }
                                    if ("*".equals(str3)) {
                                        add.add(str2);
                                    } else {
                                        add.add(str2, str3);
                                    }
                                }
                            }
                        }
                    }
                }
                PackageTagsList build = add.build();
                Binder.restoreCallingIdentity(clearCallingIdentity);
                if (isRegistered()) {
                    this.mValid = true;
                    this.mCachedValue = build;
                }
                packageTagsList = build;
            }
            return packageTagsList;
        }

        public synchronized void invalidate() {
            this.mValid = false;
            this.mCachedValue = null;
        }

        @Override // com.android.server.location.injector.SystemSettingsHelper.DeviceConfigSetting
        public void onPropertiesChanged() {
            invalidate();
            super.onPropertiesChanged();
        }
    }
}
