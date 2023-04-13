package com.android.server.p014wm;

import android.content.res.Resources;
import android.provider.DeviceConfig;
import android.provider.DeviceConfigInterface;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import java.io.PrintWriter;
import java.util.Iterator;
/* renamed from: com.android.server.wm.HighRefreshRateDenylist */
/* loaded from: classes2.dex */
public class HighRefreshRateDenylist {
    public final String[] mDefaultDenylist;
    public final ArraySet<String> mDenylistedPackages = new ArraySet<>();
    public final Object mLock = new Object();

    public static HighRefreshRateDenylist create(Resources resources) {
        return new HighRefreshRateDenylist(resources, DeviceConfigInterface.REAL);
    }

    @VisibleForTesting
    public HighRefreshRateDenylist(Resources resources, DeviceConfigInterface deviceConfigInterface) {
        this.mDefaultDenylist = resources.getStringArray(17236083);
        deviceConfigInterface.addOnPropertiesChangedListener("display_manager", BackgroundThread.getExecutor(), new OnPropertiesChangedListener());
        updateDenylist(deviceConfigInterface.getProperty("display_manager", "high_refresh_rate_blacklist"));
    }

    public final void updateDenylist(String str) {
        synchronized (this.mLock) {
            this.mDenylistedPackages.clear();
            int i = 0;
            if (str != null) {
                String[] split = str.split(",");
                int length = split.length;
                while (i < length) {
                    String trim = split[i].trim();
                    if (!trim.isEmpty()) {
                        this.mDenylistedPackages.add(trim);
                    }
                    i++;
                }
            } else {
                String[] strArr = this.mDefaultDenylist;
                int length2 = strArr.length;
                while (i < length2) {
                    this.mDenylistedPackages.add(strArr[i]);
                    i++;
                }
            }
        }
    }

    public boolean isDenylisted(String str) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mDenylistedPackages.contains(str);
        }
        return contains;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("High Refresh Rate Denylist");
        printWriter.println("  Packages:");
        synchronized (this.mLock) {
            Iterator<String> it = this.mDenylistedPackages.iterator();
            while (it.hasNext()) {
                printWriter.println("    " + it.next());
            }
        }
    }

    /* renamed from: com.android.server.wm.HighRefreshRateDenylist$OnPropertiesChangedListener */
    /* loaded from: classes2.dex */
    public class OnPropertiesChangedListener implements DeviceConfig.OnPropertiesChangedListener {
        public OnPropertiesChangedListener() {
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (properties.getKeyset().contains("high_refresh_rate_blacklist")) {
                HighRefreshRateDenylist.this.updateDenylist(properties.getString("high_refresh_rate_blacklist", (String) null));
            }
        }
    }
}
