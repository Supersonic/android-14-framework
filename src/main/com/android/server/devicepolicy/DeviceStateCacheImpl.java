package com.android.server.devicepolicy;

import android.app.admin.DeviceStateCache;
import android.util.IndentingPrintWriter;
import com.android.internal.annotations.GuardedBy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class DeviceStateCacheImpl extends DeviceStateCache {
    public final Object mLock = new Object();
    public AtomicInteger mDeviceOwnerType = new AtomicInteger(-1);
    public Map<Integer, Boolean> mHasProfileOwner = new ConcurrentHashMap();
    @GuardedBy({"mLock"})
    public boolean mIsDeviceProvisioned = false;

    public boolean isDeviceProvisioned() {
        return this.mIsDeviceProvisioned;
    }

    public void setDeviceProvisioned(boolean z) {
        synchronized (this.mLock) {
            this.mIsDeviceProvisioned = z;
        }
    }

    public void setDeviceOwnerType(int i) {
        this.mDeviceOwnerType.set(i);
    }

    public void setHasProfileOwner(int i, boolean z) {
        if (z) {
            this.mHasProfileOwner.put(Integer.valueOf(i), Boolean.TRUE);
        } else {
            this.mHasProfileOwner.remove(Integer.valueOf(i));
        }
    }

    public boolean isUserOrganizationManaged(int i) {
        return this.mHasProfileOwner.getOrDefault(Integer.valueOf(i), Boolean.FALSE).booleanValue() || hasEnterpriseDeviceOwner();
    }

    public final boolean hasEnterpriseDeviceOwner() {
        return this.mDeviceOwnerType.get() == 0;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("Device state cache:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Device provisioned: " + this.mIsDeviceProvisioned);
        indentingPrintWriter.println("Device Owner Type: " + this.mDeviceOwnerType.get());
        indentingPrintWriter.println("Has PO:");
        for (Integer num : this.mHasProfileOwner.keySet()) {
            indentingPrintWriter.println("User " + num + ": " + this.mHasProfileOwner.get(num));
        }
        indentingPrintWriter.decreaseIndent();
    }
}
