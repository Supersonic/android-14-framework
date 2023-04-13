package android.app.usage;

import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ParceledListSlice;
import android.p008os.ParcelableException;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.p008os.storage.CrateInfo;
import android.p008os.storage.StorageManager;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes.dex */
public class StorageStatsManager {
    private final Context mContext;
    private final IStorageStatsManager mService;

    public StorageStatsManager(Context context, IStorageStatsManager service) {
        this.mContext = (Context) Objects.requireNonNull(context);
        this.mService = (IStorageStatsManager) Objects.requireNonNull(service);
    }

    public boolean isQuotaSupported(UUID storageUuid) {
        try {
            return this.mService.isQuotaSupported(StorageManager.convert(storageUuid), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean isQuotaSupported(String uuid) {
        return isQuotaSupported(StorageManager.convert(uuid));
    }

    public boolean isReservedSupported(UUID storageUuid) {
        try {
            return this.mService.isReservedSupported(StorageManager.convert(storageUuid), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public long getTotalBytes(UUID storageUuid) throws IOException {
        try {
            return this.mService.getTotalBytes(StorageManager.convert(storageUuid), this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public long getTotalBytes(String uuid) throws IOException {
        return getTotalBytes(StorageManager.convert(uuid));
    }

    public long getFreeBytes(UUID storageUuid) throws IOException {
        try {
            return this.mService.getFreeBytes(StorageManager.convert(storageUuid), this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public long getFreeBytes(String uuid) throws IOException {
        return getFreeBytes(StorageManager.convert(uuid));
    }

    public long getCacheBytes(UUID storageUuid) throws IOException {
        try {
            return this.mService.getCacheBytes(StorageManager.convert(storageUuid), this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public long getCacheBytes(String uuid) throws IOException {
        return getCacheBytes(StorageManager.convert(uuid));
    }

    public StorageStats queryStatsForPackage(UUID storageUuid, String packageName, UserHandle user) throws PackageManager.NameNotFoundException, IOException {
        try {
            return this.mService.queryStatsForPackage(StorageManager.convert(storageUuid), packageName, user.getIdentifier(), this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(PackageManager.NameNotFoundException.class);
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public StorageStats queryStatsForPackage(String uuid, String packageName, UserHandle user) throws PackageManager.NameNotFoundException, IOException {
        return queryStatsForPackage(StorageManager.convert(uuid), packageName, user);
    }

    public StorageStats queryStatsForUid(UUID storageUuid, int uid) throws IOException {
        try {
            return this.mService.queryStatsForUid(StorageManager.convert(storageUuid), uid, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public StorageStats queryStatsForUid(String uuid, int uid) throws IOException {
        return queryStatsForUid(StorageManager.convert(uuid), uid);
    }

    public StorageStats queryStatsForUser(UUID storageUuid, UserHandle user) throws IOException {
        try {
            return this.mService.queryStatsForUser(StorageManager.convert(storageUuid), user.getIdentifier(), this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public StorageStats queryStatsForUser(String uuid, UserHandle user) throws IOException {
        return queryStatsForUser(StorageManager.convert(uuid), user);
    }

    public ExternalStorageStats queryExternalStatsForUser(UUID storageUuid, UserHandle user) throws IOException {
        try {
            return this.mService.queryExternalStatsForUser(StorageManager.convert(storageUuid), user.getIdentifier(), this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public ExternalStorageStats queryExternalStatsForUser(String uuid, UserHandle user) throws IOException {
        return queryExternalStatsForUser(StorageManager.convert(uuid), user);
    }

    public long getCacheQuotaBytes(String volumeUuid, int uid) {
        try {
            return this.mService.getCacheQuotaBytes(volumeUuid, uid, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Collection<CrateInfo> queryCratesForUid(UUID storageUuid, int uid) throws IOException, PackageManager.NameNotFoundException {
        try {
            ParceledListSlice<CrateInfo> crateInfoList = this.mService.queryCratesForUid(StorageManager.convert(storageUuid), uid, this.mContext.getOpPackageName());
            return ((ParceledListSlice) Objects.requireNonNull(crateInfoList)).getList();
        } catch (ParcelableException e) {
            e.maybeRethrow(PackageManager.NameNotFoundException.class);
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public Collection<CrateInfo> queryCratesForPackage(UUID storageUuid, String packageName, UserHandle user) throws PackageManager.NameNotFoundException, IOException {
        try {
            ParceledListSlice<CrateInfo> crateInfoList = this.mService.queryCratesForPackage(StorageManager.convert(storageUuid), packageName, user.getIdentifier(), this.mContext.getOpPackageName());
            return ((ParceledListSlice) Objects.requireNonNull(crateInfoList)).getList();
        } catch (ParcelableException e) {
            e.maybeRethrow(PackageManager.NameNotFoundException.class);
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public Collection<CrateInfo> queryCratesForUser(UUID storageUuid, UserHandle user) throws PackageManager.NameNotFoundException, IOException {
        try {
            ParceledListSlice<CrateInfo> crateInfoList = this.mService.queryCratesForUser(StorageManager.convert(storageUuid), user.getIdentifier(), this.mContext.getOpPackageName());
            return ((ParceledListSlice) Objects.requireNonNull(crateInfoList)).getList();
        } catch (ParcelableException e) {
            e.maybeRethrow(PackageManager.NameNotFoundException.class);
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }
}
