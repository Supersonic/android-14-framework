package android.app;

import android.annotation.SystemApi;
import android.content.Context;
import android.content.res.Configuration;
import android.p008os.LocaleList;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public class LocaleManager {
    private static final String TAG = "LocaleManager";
    private Context mContext;
    private ILocaleManager mService;

    public LocaleManager(Context context, ILocaleManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public void setApplicationLocales(LocaleList locales) {
        setApplicationLocales(this.mContext.getPackageName(), locales, false);
    }

    @SystemApi
    public void setApplicationLocales(String appPackageName, LocaleList locales) {
        setApplicationLocales(appPackageName, locales, true);
    }

    private void setApplicationLocales(String appPackageName, LocaleList locales, boolean fromDelegate) {
        try {
            this.mService.setApplicationLocales(appPackageName, this.mContext.getUserId(), locales, fromDelegate);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public LocaleList getApplicationLocales() {
        return getApplicationLocales(this.mContext.getPackageName());
    }

    public LocaleList getApplicationLocales(String appPackageName) {
        try {
            return this.mService.getApplicationLocales(appPackageName, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public LocaleList getSystemLocales() {
        try {
            return this.mService.getSystemLocales();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setSystemLocales(LocaleList locales) {
        try {
            Configuration conf = new Configuration();
            conf.setLocales(locales);
            ActivityManager.getService().updatePersistentConfiguration(conf);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setOverrideLocaleConfig(LocaleConfig localeConfig) {
        try {
            this.mService.setOverrideLocaleConfig(this.mContext.getPackageName(), this.mContext.getUserId(), localeConfig);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public LocaleConfig getOverrideLocaleConfig() {
        try {
            return this.mService.getOverrideLocaleConfig(this.mContext.getPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
