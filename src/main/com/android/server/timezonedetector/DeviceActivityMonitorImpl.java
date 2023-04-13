package com.android.server.timezonedetector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.timezonedetector.DeviceActivityMonitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public class DeviceActivityMonitorImpl implements DeviceActivityMonitor {
    @GuardedBy({"this"})
    public final List<DeviceActivityMonitor.Listener> mListeners = new ArrayList();

    @Override // com.android.server.timezonedetector.Dumpable
    public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
    }

    public static DeviceActivityMonitor create(Context context, Handler handler) {
        return new DeviceActivityMonitorImpl(context, handler);
    }

    public DeviceActivityMonitorImpl(Context context, Handler handler) {
        final ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, new ContentObserver(handler) { // from class: com.android.server.timezonedetector.DeviceActivityMonitorImpl.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                try {
                    if (Settings.Global.getInt(contentResolver, "airplane_mode_on") == 0) {
                        DeviceActivityMonitorImpl.this.notifyFlightComplete();
                    }
                } catch (Settings.SettingNotFoundException e) {
                    Slog.e("time_zone_detector", "Unable to read airplane mode state", e);
                }
            }
        });
    }

    @Override // com.android.server.timezonedetector.DeviceActivityMonitor
    public synchronized void addListener(DeviceActivityMonitor.Listener listener) {
        Objects.requireNonNull(listener);
        this.mListeners.add(listener);
    }

    public final void notifyFlightComplete() {
        ArrayList<DeviceActivityMonitor.Listener> arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.mListeners);
        }
        for (DeviceActivityMonitor.Listener listener : arrayList) {
            listener.onFlightComplete();
        }
    }
}
