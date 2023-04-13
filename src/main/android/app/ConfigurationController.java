package android.app;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.HardwareRenderer;
import android.p008os.LocaleList;
import android.p008os.Trace;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.WindowManagerGlobal;
import android.window.ConfigurationHelper;
import java.util.ArrayList;
import java.util.Locale;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ConfigurationController {
    private static final String TAG = "ConfigurationController";
    private final ActivityThreadInternal mActivityThread;
    private Configuration mCompatConfiguration;
    private Configuration mConfiguration;
    private Configuration mPendingConfiguration;
    private final ResourcesManager mResourcesManager = ResourcesManager.getInstance();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationController(ActivityThreadInternal activityThread) {
        this.mActivityThread = activityThread;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Configuration updatePendingConfiguration(Configuration config) {
        synchronized (this.mResourcesManager) {
            Configuration configuration = this.mPendingConfiguration;
            if (configuration != null && !configuration.isOtherSeqNewer(config)) {
                return null;
            }
            this.mPendingConfiguration = config;
            return config;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Configuration getPendingConfiguration(boolean clearPending) {
        Configuration outConfig = null;
        synchronized (this.mResourcesManager) {
            Configuration configuration = this.mPendingConfiguration;
            if (configuration != null) {
                outConfig = configuration;
                if (clearPending) {
                    this.mPendingConfiguration = null;
                }
            }
        }
        return outConfig;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCompatConfiguration(Configuration config) {
        this.mCompatConfiguration = new Configuration(config);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Configuration getCompatConfiguration() {
        return this.mCompatConfiguration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Configuration applyCompatConfiguration() {
        Configuration config = this.mConfiguration;
        int displayDensity = config.densityDpi;
        if (this.mCompatConfiguration == null) {
            this.mCompatConfiguration = new Configuration();
        }
        this.mCompatConfiguration.setTo(this.mConfiguration);
        if (this.mResourcesManager.applyCompatConfiguration(displayDensity, this.mCompatConfiguration)) {
            return this.mCompatConfiguration;
        }
        return config;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConfiguration(Configuration config) {
        this.mConfiguration = new Configuration(config);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Configuration getConfiguration() {
        return this.mConfiguration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleConfigurationChanged(Configuration config) {
        Trace.traceBegin(64L, "configChanged");
        handleConfigurationChanged(config, null);
        Trace.traceEnd(64L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleConfigurationChanged(CompatibilityInfo compat) {
        handleConfigurationChanged(this.mConfiguration, compat);
        WindowManagerGlobal.getInstance().reportNewConfiguration(this.mConfiguration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleConfigurationChanged(Configuration config, CompatibilityInfo compat) {
        Resources.Theme systemTheme = this.mActivityThread.getSystemContext().getTheme();
        ContextImpl systemUiContext = this.mActivityThread.getSystemUiContextNoCreate();
        Resources.Theme systemUiTheme = systemUiContext != null ? systemUiContext.getTheme() : null;
        synchronized (this.mResourcesManager) {
            Configuration configuration = this.mPendingConfiguration;
            if (configuration != null) {
                if (!configuration.isOtherSeqNewer(config)) {
                    config = this.mPendingConfiguration;
                    updateDefaultDensity(config.densityDpi);
                }
                this.mPendingConfiguration = null;
            }
            if (config == null) {
                return;
            }
            Configuration configuration2 = this.mConfiguration;
            boolean equivalent = configuration2 != null && configuration2.diffPublicOnly(config) == 0;
            Application app = this.mActivityThread.getApplication();
            app.getResources();
            this.mResourcesManager.applyConfigurationToResources(config, compat);
            updateLocaleListFromAppContext(app.getApplicationContext());
            if (this.mConfiguration == null) {
                this.mConfiguration = new Configuration();
            }
            if (this.mConfiguration.isOtherSeqNewer(config) || compat != null) {
                int configDiff = this.mConfiguration.updateFrom(config);
                Configuration config2 = applyCompatConfiguration();
                HardwareRenderer.sendDeviceConfigurationForDebugging(config2);
                if ((systemTheme.getChangingConfigurations() & configDiff) != 0) {
                    systemTheme.rebase();
                }
                if (systemUiTheme != null && (systemUiTheme.getChangingConfigurations() & configDiff) != 0) {
                    systemUiTheme.rebase();
                }
                ArrayList<ComponentCallbacks2> callbacks = this.mActivityThread.collectComponentCallbacks(false);
                ConfigurationHelper.freeTextLayoutCachesIfNeeded(configDiff);
                if (callbacks != null) {
                    int size = callbacks.size();
                    for (int i = 0; i < size; i++) {
                        ComponentCallbacks2 cb = callbacks.get(i);
                        if (!equivalent) {
                            performConfigurationChanged(cb, config2);
                        }
                    }
                }
            }
        }
    }

    void performConfigurationChanged(ComponentCallbacks2 cb, Configuration newConfig) {
        Configuration contextThemeWrapperOverrideConfig = null;
        if (cb instanceof ContextThemeWrapper) {
            ContextThemeWrapper contextThemeWrapper = (ContextThemeWrapper) cb;
            contextThemeWrapperOverrideConfig = contextThemeWrapper.getOverrideConfiguration();
        }
        Configuration configToReport = createNewConfigAndUpdateIfNotNull(newConfig, contextThemeWrapperOverrideConfig);
        cb.onConfigurationChanged(configToReport);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDefaultDensity(int densityDpi) {
        if (!this.mActivityThread.isInDensityCompatMode() && densityDpi != 0 && densityDpi != DisplayMetrics.DENSITY_DEVICE) {
            DisplayMetrics.DENSITY_DEVICE = densityDpi;
            Bitmap.setDefaultDensity(densityDpi);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCurDefaultDisplayDpi() {
        return this.mConfiguration.densityDpi;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateLocaleListFromAppContext(Context context) {
        Locale bestLocale = context.getResources().getConfiguration().getLocales().get(0);
        LocaleList newLocaleList = this.mResourcesManager.getConfiguration().getLocales();
        int newLocaleListSize = newLocaleList.size();
        for (int i = 0; i < newLocaleListSize; i++) {
            if (bestLocale.equals(newLocaleList.get(i))) {
                LocaleList.setDefault(newLocaleList, i);
                return;
            }
        }
        LocaleList.setDefault(new LocaleList(bestLocale, newLocaleList));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Configuration createNewConfigAndUpdateIfNotNull(Configuration base, Configuration override) {
        if (override == null) {
            return base;
        }
        Configuration newConfig = new Configuration(base);
        newConfig.updateFrom(override);
        return newConfig;
    }
}
