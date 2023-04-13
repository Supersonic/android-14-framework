package com.android.server.devicepolicy;

import android.app.admin.DevicePolicyCache;
import android.util.IndentingPrintWriter;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class DevicePolicyCacheImpl extends DevicePolicyCache {
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public int mScreenCaptureDisallowedUser = -10000;
    @GuardedBy({"mLock"})
    public final SparseIntArray mPasswordQuality = new SparseIntArray();
    @GuardedBy({"mLock"})
    public final SparseIntArray mPermissionPolicy = new SparseIntArray();
    @GuardedBy({"mLock"})
    public List<String> mLauncherShortcutOverrides = new ArrayList();
    public final AtomicBoolean mCanGrantSensorsPermissions = new AtomicBoolean(false);

    public void onUserRemoved(int i) {
        synchronized (this.mLock) {
            this.mPasswordQuality.delete(i);
            this.mPermissionPolicy.delete(i);
        }
    }

    public boolean isScreenCaptureAllowed(int i) {
        boolean z;
        synchronized (this.mLock) {
            int i2 = this.mScreenCaptureDisallowedUser;
            z = (i2 == -1 || i2 == i) ? false : true;
        }
        return z;
    }

    public int getScreenCaptureDisallowedUser() {
        int i;
        synchronized (this.mLock) {
            i = this.mScreenCaptureDisallowedUser;
        }
        return i;
    }

    public void setScreenCaptureDisallowedUser(int i) {
        synchronized (this.mLock) {
            this.mScreenCaptureDisallowedUser = i;
        }
    }

    public int getPasswordQuality(int i) {
        int i2;
        synchronized (this.mLock) {
            i2 = this.mPasswordQuality.get(i, 0);
        }
        return i2;
    }

    public void setPasswordQuality(int i, int i2) {
        synchronized (this.mLock) {
            this.mPasswordQuality.put(i, i2);
        }
    }

    public int getPermissionPolicy(int i) {
        int i2;
        synchronized (this.mLock) {
            i2 = this.mPermissionPolicy.get(i, 0);
        }
        return i2;
    }

    public void setPermissionPolicy(int i, int i2) {
        synchronized (this.mLock) {
            this.mPermissionPolicy.put(i, i2);
        }
    }

    public boolean canAdminGrantSensorsPermissions() {
        return this.mCanGrantSensorsPermissions.get();
    }

    public void setAdminCanGrantSensorsPermissions(boolean z) {
        this.mCanGrantSensorsPermissions.set(z);
    }

    public List<String> getLauncherShortcutOverrides() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mLauncherShortcutOverrides);
        }
        return arrayList;
    }

    public void setLauncherShortcutOverrides(List<String> list) {
        synchronized (this.mLock) {
            this.mLauncherShortcutOverrides = new ArrayList(list);
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            indentingPrintWriter.println("Device policy cache:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Screen capture disallowed user: " + this.mScreenCaptureDisallowedUser);
            indentingPrintWriter.println("Password quality: " + this.mPasswordQuality);
            indentingPrintWriter.println("Permission policy: " + this.mPermissionPolicy);
            indentingPrintWriter.println("Admin can grant sensors permission: " + this.mCanGrantSensorsPermissions.get());
            indentingPrintWriter.print("Shortcuts overrides: ");
            indentingPrintWriter.println(this.mLauncherShortcutOverrides);
            indentingPrintWriter.decreaseIndent();
        }
    }
}
