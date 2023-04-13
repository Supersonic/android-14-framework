package com.android.internal.app;

import android.app.LocaleConfig;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.InstallSourceInfo;
import android.content.p001pm.PackageManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.LocaleList;
import android.util.Log;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
/* loaded from: classes4.dex */
public class AppLocaleStore {
    private static final String TAG = AppLocaleStore.class.getSimpleName();

    public static AppLocaleResult getAppSupportedLocales(Context context, String packageName) {
        LocaleConfig localeConfig = null;
        AppLocaleResult.LocaleStatus localeStatus = AppLocaleResult.LocaleStatus.UNKNOWN_FAILURE;
        HashSet<Locale> appSupportedLocales = new HashSet<>();
        HashSet<Locale> assetLocale = getAssetLocales(context, packageName);
        boolean shouldFilterNotMatchingLocale = false;
        try {
            localeConfig = new LocaleConfig(context.createPackageContext(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.m104w(TAG, "Can not found the package name : " + packageName + " / " + e);
        }
        if (localeConfig != null) {
            if (localeConfig.getStatus() == 0) {
                LocaleList packageLocaleList = localeConfig.getSupportedLocales();
                if (!hasInstallerInfo(context, packageName) && isSystemApp(context, packageName)) {
                    shouldFilterNotMatchingLocale = true;
                }
                Log.m112d(TAG, "filterNonMatchingLocale. , shouldFilterNotMatchingLocale: " + shouldFilterNotMatchingLocale + ", assetLocale size: " + assetLocale.size() + ", packageLocaleList size: " + packageLocaleList.size());
                for (int i = 0; i < packageLocaleList.size(); i++) {
                    appSupportedLocales.add(packageLocaleList.get(i));
                }
                if (shouldFilterNotMatchingLocale) {
                    appSupportedLocales = filterNotMatchingLocale(appSupportedLocales, assetLocale);
                }
                localeStatus = appSupportedLocales.size() > 0 ? AppLocaleResult.LocaleStatus.GET_SUPPORTED_LANGUAGE_FROM_LOCAL_CONFIG : AppLocaleResult.LocaleStatus.NO_SUPPORTED_LANGUAGE_IN_APP;
            } else if (localeConfig.getStatus() == 1) {
                if (assetLocale.size() > 0) {
                    localeStatus = AppLocaleResult.LocaleStatus.GET_SUPPORTED_LANGUAGE_FROM_ASSET;
                    appSupportedLocales = assetLocale;
                } else {
                    localeStatus = AppLocaleResult.LocaleStatus.ASSET_LOCALE_IS_EMPTY;
                }
            }
        }
        Log.m112d(TAG, "getAppSupportedLocales(). package: " + packageName + ", status: " + localeStatus + ", appSupportedLocales:" + appSupportedLocales.size());
        return new AppLocaleResult(localeStatus, appSupportedLocales);
    }

    private static HashSet<Locale> getAssetLocales(Context context, String packageName) {
        HashSet<Locale> result = new HashSet<>();
        try {
            PackageManager packageManager = context.getPackageManager();
            String[] locales = packageManager.getResourcesForApplication(packageManager.getPackageInfo(packageName, 131072).applicationInfo).getAssets().getNonSystemLocales();
            if (locales == null) {
                Log.m108i(TAG, NavigationBarInflaterView.SIZE_MOD_START + packageName + "] locales are null.");
            } else if (locales.length <= 0) {
                Log.m108i(TAG, NavigationBarInflaterView.SIZE_MOD_START + packageName + "] locales length is 0.");
            } else {
                for (String language : locales) {
                    result.add(Locale.forLanguageTag(language));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.m104w(TAG, "Can not found the package name : " + packageName + " / " + e);
        }
        return result;
    }

    private static HashSet<Locale> filterNotMatchingLocale(HashSet<Locale> appSupportedLocales, final HashSet<Locale> assetLocale) {
        return (HashSet) appSupportedLocales.stream().filter(new Predicate() { // from class: com.android.internal.app.AppLocaleStore$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean matchLanguageInSet;
                matchLanguageInSet = AppLocaleStore.matchLanguageInSet((Locale) obj, assetLocale);
                return matchLanguageInSet;
            }
        }).collect(Collectors.toCollection(new Supplier() { // from class: com.android.internal.app.AppLocaleStore$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                return new HashSet();
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean matchLanguageInSet(Locale locale, HashSet<Locale> localesSet) {
        if (localesSet.contains(locale)) {
            return true;
        }
        Iterator<Locale> it = localesSet.iterator();
        while (it.hasNext()) {
            Locale l = it.next();
            if (LocaleList.matchesLanguageAndScript(l, locale)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasInstallerInfo(Context context, String packageName) {
        try {
            InstallSourceInfo installSourceInfo = context.getPackageManager().getInstallSourceInfo(packageName);
            return installSourceInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m104w(TAG, "Installer info not found for: " + packageName);
            return false;
        }
    }

    private static boolean isSystemApp(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfoAsUser(packageName, 0, context.getUserId());
            return applicationInfo.isSystemApp();
        } catch (PackageManager.NameNotFoundException e) {
            Log.m104w(TAG, "Application info not found for: " + packageName);
            return false;
        }
    }

    /* loaded from: classes4.dex */
    public static class AppLocaleResult {
        public HashSet<Locale> mAppSupportedLocales;
        LocaleStatus mLocaleStatus;

        /* loaded from: classes4.dex */
        public enum LocaleStatus {
            UNKNOWN_FAILURE,
            NO_SUPPORTED_LANGUAGE_IN_APP,
            ASSET_LOCALE_IS_EMPTY,
            GET_SUPPORTED_LANGUAGE_FROM_LOCAL_CONFIG,
            GET_SUPPORTED_LANGUAGE_FROM_ASSET
        }

        public AppLocaleResult(LocaleStatus localeStatus, HashSet<Locale> appSupportedLocales) {
            this.mLocaleStatus = localeStatus;
            this.mAppSupportedLocales = appSupportedLocales;
        }
    }
}
