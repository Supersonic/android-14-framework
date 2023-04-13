package android.service.persistentdata;

import android.annotation.SystemApi;
import android.p008os.RemoteException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public class PersistentDataBlockManager {
    public static final int FLASH_LOCK_LOCKED = 1;
    public static final int FLASH_LOCK_UNKNOWN = -1;
    public static final int FLASH_LOCK_UNLOCKED = 0;
    private static final String TAG = PersistentDataBlockManager.class.getSimpleName();
    private IPersistentDataBlockService sService;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface FlashLockState {
    }

    public PersistentDataBlockManager(IPersistentDataBlockService service) {
        this.sService = service;
    }

    public int write(byte[] data) {
        try {
            return this.sService.write(data);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public byte[] read() {
        try {
            return this.sService.read();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getDataBlockSize() {
        try {
            return this.sService.getDataBlockSize();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public long getMaximumDataBlockSize() {
        try {
            return this.sService.getMaximumDataBlockSize();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void wipe() {
        try {
            this.sService.wipe();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setOemUnlockEnabled(boolean enabled) {
        try {
            this.sService.setOemUnlockEnabled(enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean getOemUnlockEnabled() {
        try {
            return this.sService.getOemUnlockEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getFlashLockState() {
        try {
            return this.sService.getFlashLockState();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public String getPersistentDataPackageName() {
        try {
            return this.sService.getPersistentDataPackageName();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
