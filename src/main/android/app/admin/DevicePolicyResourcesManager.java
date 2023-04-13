package android.app.admin;

import android.annotation.SystemApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.p008os.RemoteException;
import android.provider.DeviceConfig;
import android.util.Log;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class DevicePolicyResourcesManager {
    private final Context mContext;
    private final IDevicePolicyManager mService;
    private static String TAG = "DevicePolicyResourcesManager";
    private static String DISABLE_RESOURCES_UPDATABILITY_FLAG = "disable_resources_updatability";
    private static boolean DEFAULT_DISABLE_RESOURCES_UPDATABILITY = false;

    /* JADX INFO: Access modifiers changed from: protected */
    public DevicePolicyResourcesManager(Context context, IDevicePolicyManager service) {
        this.mContext = context;
        this.mService = service;
    }

    @SystemApi
    public void setDrawables(Set<DevicePolicyDrawableResource> drawables) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setDrawables(new ArrayList(drawables));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public void resetDrawables(Set<String> drawableIds) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.resetDrawables(new ArrayList(drawableIds));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public Drawable getDrawable(String drawableId, String drawableStyle, Supplier<Drawable> defaultDrawableLoader) {
        return getDrawable(drawableId, drawableStyle, DevicePolicyResources.UNDEFINED, defaultDrawableLoader);
    }

    public Drawable getDrawable(String drawableId, String drawableStyle, String drawableSource, Supplier<Drawable> defaultDrawableLoader) {
        Objects.requireNonNull(drawableId, "drawableId can't be null");
        Objects.requireNonNull(drawableStyle, "drawableStyle can't be null");
        Objects.requireNonNull(drawableSource, "drawableSource can't be null");
        Objects.requireNonNull(defaultDrawableLoader, "defaultDrawableLoader can't be null");
        if (drawableId.equals(DevicePolicyResources.UNDEFINED) || DeviceConfig.getBoolean("device_policy_manager", DISABLE_RESOURCES_UPDATABILITY_FLAG, DEFAULT_DISABLE_RESOURCES_UPDATABILITY)) {
            return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
        }
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ParcelableResource resource = iDevicePolicyManager.getDrawable(drawableId, drawableStyle, drawableSource);
                if (resource == null) {
                    return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
                }
                return resource.getDrawable(this.mContext, 0, defaultDrawableLoader);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error getting the updated drawable from DevicePolicyManagerService.", e);
                return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
            }
        }
        return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
    }

    public Drawable getDrawableForDensity(String drawableId, String drawableStyle, int density, Supplier<Drawable> defaultDrawableLoader) {
        return getDrawableForDensity(drawableId, drawableStyle, DevicePolicyResources.UNDEFINED, density, defaultDrawableLoader);
    }

    public Drawable getDrawableForDensity(String drawableId, String drawableStyle, String drawableSource, int density, Supplier<Drawable> defaultDrawableLoader) {
        Objects.requireNonNull(drawableId, "drawableId can't be null");
        Objects.requireNonNull(drawableStyle, "drawableStyle can't be null");
        Objects.requireNonNull(drawableSource, "drawableSource can't be null");
        Objects.requireNonNull(defaultDrawableLoader, "defaultDrawableLoader can't be null");
        if (drawableId.equals(DevicePolicyResources.UNDEFINED) || DeviceConfig.getBoolean("device_policy_manager", DISABLE_RESOURCES_UPDATABILITY_FLAG, DEFAULT_DISABLE_RESOURCES_UPDATABILITY)) {
            return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
        }
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ParcelableResource resource = iDevicePolicyManager.getDrawable(drawableId, drawableStyle, drawableSource);
                if (resource == null) {
                    return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
                }
                return resource.getDrawable(this.mContext, density, defaultDrawableLoader);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error getting the updated drawable from DevicePolicyManagerService.", e);
                return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
            }
        }
        return ParcelableResource.loadDefaultDrawable(defaultDrawableLoader);
    }

    public Icon getDrawableAsIcon(String drawableId, String drawableStyle, String drawableSource, Icon defaultIcon) {
        Objects.requireNonNull(drawableId, "drawableId can't be null");
        Objects.requireNonNull(drawableStyle, "drawableStyle can't be null");
        Objects.requireNonNull(drawableSource, "drawableSource can't be null");
        Objects.requireNonNull(defaultIcon, "defaultIcon can't be null");
        if (drawableId.equals(DevicePolicyResources.UNDEFINED) || DeviceConfig.getBoolean("device_policy_manager", DISABLE_RESOURCES_UPDATABILITY_FLAG, DEFAULT_DISABLE_RESOURCES_UPDATABILITY)) {
            return defaultIcon;
        }
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ParcelableResource resource = iDevicePolicyManager.getDrawable(drawableId, drawableStyle, drawableSource);
                if (resource == null) {
                    return defaultIcon;
                }
                return Icon.createWithResource(resource.getPackageName(), resource.getResourceId());
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error getting the updated drawable from DevicePolicyManagerService.", e);
                return defaultIcon;
            }
        }
        return defaultIcon;
    }

    public Icon getDrawableAsIcon(String drawableId, String drawableStyle, Icon defaultIcon) {
        return getDrawableAsIcon(drawableId, drawableStyle, DevicePolicyResources.UNDEFINED, defaultIcon);
    }

    @SystemApi
    public void setStrings(Set<DevicePolicyStringResource> strings) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.setStrings(new ArrayList(strings));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public void resetStrings(Set<String> stringIds) {
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                iDevicePolicyManager.resetStrings(new ArrayList(stringIds));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public String getString(String stringId, Supplier<String> defaultStringLoader) {
        Objects.requireNonNull(stringId, "stringId can't be null");
        Objects.requireNonNull(defaultStringLoader, "defaultStringLoader can't be null");
        if (stringId.equals(DevicePolicyResources.UNDEFINED) || DeviceConfig.getBoolean("device_policy_manager", DISABLE_RESOURCES_UPDATABILITY_FLAG, DEFAULT_DISABLE_RESOURCES_UPDATABILITY)) {
            return ParcelableResource.loadDefaultString(defaultStringLoader);
        }
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ParcelableResource resource = iDevicePolicyManager.getString(stringId);
                if (resource == null) {
                    return ParcelableResource.loadDefaultString(defaultStringLoader);
                }
                return resource.getString(this.mContext, defaultStringLoader);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error getting the updated string from DevicePolicyManagerService.", e);
                return ParcelableResource.loadDefaultString(defaultStringLoader);
            }
        }
        return ParcelableResource.loadDefaultString(defaultStringLoader);
    }

    public String getString(String stringId, Supplier<String> defaultStringLoader, Object... formatArgs) {
        Objects.requireNonNull(stringId, "stringId can't be null");
        Objects.requireNonNull(defaultStringLoader, "defaultStringLoader can't be null");
        if (stringId.equals(DevicePolicyResources.UNDEFINED) || DeviceConfig.getBoolean("device_policy_manager", DISABLE_RESOURCES_UPDATABILITY_FLAG, DEFAULT_DISABLE_RESOURCES_UPDATABILITY)) {
            return ParcelableResource.loadDefaultString(defaultStringLoader);
        }
        IDevicePolicyManager iDevicePolicyManager = this.mService;
        if (iDevicePolicyManager != null) {
            try {
                ParcelableResource resource = iDevicePolicyManager.getString(stringId);
                if (resource == null) {
                    return ParcelableResource.loadDefaultString(defaultStringLoader);
                }
                return resource.getString(this.mContext, defaultStringLoader, formatArgs);
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error getting the updated string from DevicePolicyManagerService.", e);
                return ParcelableResource.loadDefaultString(defaultStringLoader);
            }
        }
        return ParcelableResource.loadDefaultString(defaultStringLoader);
    }
}
