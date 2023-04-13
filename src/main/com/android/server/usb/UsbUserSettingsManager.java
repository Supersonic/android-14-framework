package com.android.server.usb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.hardware.usb.AccessoryFilter;
import android.hardware.usb.DeviceFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.dump.DumpUtils;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class UsbUserSettingsManager {
    public static final String TAG = "UsbUserSettingsManager";
    public final Object mLock = new Object();
    public final PackageManager mPackageManager;
    public final UserHandle mUser;
    public final Context mUserContext;

    public UsbUserSettingsManager(Context context, UserHandle userHandle) {
        try {
            Context createPackageContextAsUser = context.createPackageContextAsUser(PackageManagerShellCommandDataLoader.PACKAGE, 0, userHandle);
            this.mUserContext = createPackageContextAsUser;
            this.mPackageManager = createPackageContextAsUser.getPackageManager();
            this.mUser = userHandle;
        } catch (PackageManager.NameNotFoundException unused) {
            throw new RuntimeException("Missing android package");
        }
    }

    public List<ResolveInfo> queryIntentActivities(Intent intent) {
        return this.mPackageManager.queryIntentActivitiesAsUser(intent, 128, this.mUser.getIdentifier());
    }

    public boolean canBeDefault(UsbDevice usbDevice, String str) {
        XmlResourceParser loadXmlMetaData;
        ActivityInfo[] packageActivities = getPackageActivities(str);
        if (packageActivities != null) {
            for (ActivityInfo activityInfo : packageActivities) {
                try {
                    loadXmlMetaData = activityInfo.loadXmlMetaData(this.mPackageManager, "android.hardware.usb.action.USB_DEVICE_ATTACHED");
                } catch (Exception e) {
                    Slog.w(TAG, "Unable to load component info " + activityInfo.toString(), e);
                }
                if (loadXmlMetaData != null) {
                    XmlUtils.nextElement(loadXmlMetaData);
                    while (loadXmlMetaData.getEventType() != 1) {
                        if (!"usb-device".equals(loadXmlMetaData.getName()) || !DeviceFilter.read(loadXmlMetaData).matches(usbDevice)) {
                            XmlUtils.nextElement(loadXmlMetaData);
                        } else {
                            loadXmlMetaData.close();
                            return true;
                        }
                    }
                } else if (loadXmlMetaData == null) {
                }
                loadXmlMetaData.close();
            }
        }
        return false;
    }

    public boolean canBeDefault(UsbAccessory usbAccessory, String str) {
        XmlResourceParser loadXmlMetaData;
        ActivityInfo[] packageActivities = getPackageActivities(str);
        if (packageActivities != null) {
            for (ActivityInfo activityInfo : packageActivities) {
                try {
                    loadXmlMetaData = activityInfo.loadXmlMetaData(this.mPackageManager, "android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                } catch (Exception e) {
                    Slog.w(TAG, "Unable to load component info " + activityInfo.toString(), e);
                }
                if (loadXmlMetaData != null) {
                    XmlUtils.nextElement(loadXmlMetaData);
                    while (loadXmlMetaData.getEventType() != 1) {
                        if (!"usb-accessory".equals(loadXmlMetaData.getName()) || !AccessoryFilter.read(loadXmlMetaData).matches(usbAccessory)) {
                            XmlUtils.nextElement(loadXmlMetaData);
                        } else {
                            loadXmlMetaData.close();
                            return true;
                        }
                    }
                } else if (loadXmlMetaData == null) {
                }
                loadXmlMetaData.close();
            }
        }
        return false;
    }

    public final ActivityInfo[] getPackageActivities(String str) {
        try {
            return this.mPackageManager.getPackageInfo(str, 129).activities;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long start = dualDumpOutputStream.start(str, j);
        synchronized (this.mLock) {
            dualDumpOutputStream.write("user_id", 1120986464257L, this.mUser.getIdentifier());
            List<ResolveInfo> queryIntentActivities = queryIntentActivities(new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED"));
            int size = queryIntentActivities.size();
            int i = 0;
            while (i < size) {
                ResolveInfo resolveInfo = queryIntentActivities.get(i);
                int i2 = i;
                long start2 = dualDumpOutputStream.start("device_attached_activities", 2246267895812L);
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                DumpUtils.writeComponentName(dualDumpOutputStream, "activity", 1146756268033L, new ComponentName(activityInfo.packageName, activityInfo.name));
                ArrayList<DeviceFilter> deviceFilters = UsbProfileGroupSettingsManager.getDeviceFilters(this.mPackageManager, resolveInfo);
                if (deviceFilters != null) {
                    int size2 = deviceFilters.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        deviceFilters.get(i3).dump(dualDumpOutputStream, "filters", 2246267895810L);
                    }
                }
                dualDumpOutputStream.end(start2);
                i = i2 + 1;
            }
            List<ResolveInfo> queryIntentActivities2 = queryIntentActivities(new Intent("android.hardware.usb.action.USB_ACCESSORY_ATTACHED"));
            int size3 = queryIntentActivities2.size();
            int i4 = 0;
            while (i4 < size3) {
                ResolveInfo resolveInfo2 = queryIntentActivities2.get(i4);
                long start3 = dualDumpOutputStream.start("accessory_attached_activities", 2246267895813L);
                ActivityInfo activityInfo2 = resolveInfo2.activityInfo;
                DumpUtils.writeComponentName(dualDumpOutputStream, "activity", 1146756268033L, new ComponentName(activityInfo2.packageName, activityInfo2.name));
                ArrayList<AccessoryFilter> accessoryFilters = UsbProfileGroupSettingsManager.getAccessoryFilters(this.mPackageManager, resolveInfo2);
                if (accessoryFilters != null) {
                    int size4 = accessoryFilters.size();
                    int i5 = 0;
                    while (i5 < size4) {
                        accessoryFilters.get(i5).dump(dualDumpOutputStream, "filters", 2246267895810L);
                        i5++;
                        queryIntentActivities2 = queryIntentActivities2;
                        size3 = size3;
                    }
                }
                dualDumpOutputStream.end(start3);
                i4++;
                queryIntentActivities2 = queryIntentActivities2;
                size3 = size3;
            }
        }
        dualDumpOutputStream.end(start);
    }
}
