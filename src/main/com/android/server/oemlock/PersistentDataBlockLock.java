package com.android.server.oemlock;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.persistentdata.PersistentDataBlockManager;
import android.util.Slog;
/* loaded from: classes2.dex */
public class PersistentDataBlockLock extends OemLock {
    public Context mContext;

    @Override // com.android.server.oemlock.OemLock
    public String getLockName() {
        return "";
    }

    public PersistentDataBlockLock(Context context) {
        this.mContext = context;
    }

    @Override // com.android.server.oemlock.OemLock
    public void setOemUnlockAllowedByCarrier(boolean z, byte[] bArr) {
        if (bArr != null) {
            Slog.w("OemLock", "Signature provided but is not being used");
        }
        UserManager.get(this.mContext).setUserRestriction("no_oem_unlock", !z, UserHandle.SYSTEM);
        if (z) {
            return;
        }
        disallowUnlockIfNotUnlocked();
    }

    @Override // com.android.server.oemlock.OemLock
    public boolean isOemUnlockAllowedByCarrier() {
        return !UserManager.get(this.mContext).hasUserRestriction("no_oem_unlock", UserHandle.SYSTEM);
    }

    @Override // com.android.server.oemlock.OemLock
    public void setOemUnlockAllowedByDevice(boolean z) {
        PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) this.mContext.getSystemService("persistent_data_block");
        if (persistentDataBlockManager == null) {
            Slog.w("OemLock", "PersistentDataBlock is not supported on this device");
        } else {
            persistentDataBlockManager.setOemUnlockEnabled(z);
        }
    }

    @Override // com.android.server.oemlock.OemLock
    public boolean isOemUnlockAllowedByDevice() {
        PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) this.mContext.getSystemService("persistent_data_block");
        if (persistentDataBlockManager == null) {
            Slog.w("OemLock", "PersistentDataBlock is not supported on this device");
            return false;
        }
        return persistentDataBlockManager.getOemUnlockEnabled();
    }

    public final void disallowUnlockIfNotUnlocked() {
        PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) this.mContext.getSystemService("persistent_data_block");
        if (persistentDataBlockManager == null) {
            Slog.w("OemLock", "PersistentDataBlock is not supported on this device");
        } else if (persistentDataBlockManager.getFlashLockState() != 0) {
            persistentDataBlockManager.setOemUnlockEnabled(false);
        }
    }
}
