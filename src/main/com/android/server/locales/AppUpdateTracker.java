package com.android.server.locales;

import android.app.LocaleConfig;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.LocaleList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class AppUpdateTracker {
    public final LocaleManagerBackupHelper mBackupHelper;
    public final Context mContext;
    public final LocaleManagerService mLocaleManagerService;

    public AppUpdateTracker(Context context, LocaleManagerService localeManagerService, LocaleManagerBackupHelper localeManagerBackupHelper) {
        this.mContext = context;
        this.mLocaleManagerService = localeManagerService;
        this.mBackupHelper = localeManagerBackupHelper;
    }

    public void onPackageUpdateFinished(String str, int i) {
        Log.d("AppUpdateTracker", "onPackageUpdateFinished " + str);
        cleanApplicationLocalesIfNeeded(str, UserHandle.getUserId(i));
    }

    public final void cleanApplicationLocalesIfNeeded(String str, int i) {
        Set<String> arraySet = new ArraySet<>();
        SharedPreferences persistedInfo = this.mBackupHelper.getPersistedInfo();
        if (persistedInfo != null) {
            arraySet = persistedInfo.getStringSet(Integer.toString(i), new ArraySet());
        }
        try {
            LocaleList applicationLocales = this.mLocaleManagerService.getApplicationLocales(str, i);
            if (applicationLocales.isEmpty() || isLocalesExistedInLocaleConfig(applicationLocales, str, i)) {
                return;
            }
            if (arraySet.contains(str)) {
                Slog.d("AppUpdateTracker", "Clear app locales for " + str);
                try {
                    this.mLocaleManagerService.setApplicationLocales(str, i, LocaleList.forLanguageTags(""), false);
                } catch (RemoteException | IllegalArgumentException e) {
                    Slog.e("AppUpdateTracker", "Could not clear locales for " + str, e);
                }
            }
        } catch (RemoteException | IllegalArgumentException e2) {
            Slog.e("AppUpdateTracker", "Exception when getting locales for " + str, e2);
        }
    }

    public final boolean isLocalesExistedInLocaleConfig(LocaleList localeList, String str, int i) {
        LocaleList packageLocales = getPackageLocales(str, i);
        HashSet<Locale> hashSet = new HashSet<>();
        if (isSettingsAppLocalesOptIn()) {
            if (packageLocales == null || packageLocales.isEmpty()) {
                Slog.d("AppUpdateTracker", "opt-in: the app locale feature is not enabled");
                return false;
            }
        } else if (packageLocales != null && packageLocales.isEmpty()) {
            Slog.d("AppUpdateTracker", "opt-out: the app locale feature is not enabled");
            return false;
        }
        if (packageLocales == null || packageLocales.isEmpty()) {
            return true;
        }
        for (int i2 = 0; i2 < packageLocales.size(); i2++) {
            hashSet.add(packageLocales.get(i2));
        }
        if (matchesLocale(hashSet, localeList)) {
            return true;
        }
        Slog.d("AppUpdateTracker", "App locales: " + localeList.toLanguageTags() + " are not existed in the supported locale list");
        return false;
    }

    @VisibleForTesting
    public LocaleList getPackageLocales(String str, int i) {
        try {
            LocaleConfig localeConfig = new LocaleConfig(this.mContext.createPackageContextAsUser(str, 0, UserHandle.of(i)));
            if (localeConfig.getStatus() == 0) {
                return localeConfig.getSupportedLocales();
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e("AppUpdateTracker", "Can not found the package name : " + str + " / " + e);
            return null;
        }
    }

    @VisibleForTesting
    public boolean isSettingsAppLocalesOptIn() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_app_locale_opt_in_enabled");
    }

    public final boolean matchesLocale(HashSet<Locale> hashSet, LocaleList localeList) {
        if (hashSet.size() <= 0 || localeList.size() <= 0) {
            return true;
        }
        for (int i = 0; i < localeList.size(); i++) {
            final Locale locale = localeList.get(i);
            if (hashSet.stream().anyMatch(new Predicate() { // from class: com.android.server.locales.AppUpdateTracker$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean matchesLanguageAndScript;
                    matchesLanguageAndScript = LocaleList.matchesLanguageAndScript((Locale) obj, locale);
                    return matchesLanguageAndScript;
                }
            })) {
                return true;
            }
        }
        return false;
    }
}
