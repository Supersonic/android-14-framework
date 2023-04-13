package com.android.internal.config.appcloning;

import android.content.Context;
import android.provider.DeviceConfig;
import java.util.Iterator;
/* loaded from: classes4.dex */
public class AppCloningDeviceConfigHelper {
    public static final String ENABLE_APP_CLONING_BUILDING_BLOCKS = "enable_app_cloning_building_blocks";
    private static AppCloningDeviceConfigHelper sInstance;
    private static final Object sLock = new Object();
    private DeviceConfig.OnPropertiesChangedListener mDeviceConfigChangeListener;
    private volatile boolean mEnableAppCloningBuildingBlocks = true;

    private AppCloningDeviceConfigHelper() {
    }

    public static AppCloningDeviceConfigHelper getInstance(Context context) {
        AppCloningDeviceConfigHelper appCloningDeviceConfigHelper;
        synchronized (sLock) {
            if (sInstance == null) {
                AppCloningDeviceConfigHelper appCloningDeviceConfigHelper2 = new AppCloningDeviceConfigHelper();
                sInstance = appCloningDeviceConfigHelper2;
                appCloningDeviceConfigHelper2.init(context);
            }
            appCloningDeviceConfigHelper = sInstance;
        }
        return appCloningDeviceConfigHelper;
    }

    private void init(Context context) {
        initializeDeviceConfigChangeListener();
        DeviceConfig.addOnPropertiesChangedListener("app_cloning", context.getMainExecutor(), this.mDeviceConfigChangeListener);
    }

    private void initializeDeviceConfigChangeListener() {
        this.mDeviceConfigChangeListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.internal.config.appcloning.AppCloningDeviceConfigHelper$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                AppCloningDeviceConfigHelper.this.lambda$initializeDeviceConfigChangeListener$0(properties);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initializeDeviceConfigChangeListener$0(DeviceConfig.Properties properties) {
        String name;
        if (!"app_cloning".equals(properties.getNamespace())) {
            return;
        }
        Iterator it = properties.getKeyset().iterator();
        while (it.hasNext() && (name = (String) it.next()) != null) {
            if (ENABLE_APP_CLONING_BUILDING_BLOCKS.equals(name)) {
                updateEnableAppCloningBuildingBlocks();
            }
        }
    }

    private void updateEnableAppCloningBuildingBlocks() {
        this.mEnableAppCloningBuildingBlocks = DeviceConfig.getBoolean("app_cloning", ENABLE_APP_CLONING_BUILDING_BLOCKS, true);
    }

    public boolean getEnableAppCloningBuildingBlocks() {
        return this.mEnableAppCloningBuildingBlocks;
    }
}
