package android.p008os.storage;

import android.p008os.IVold;
import java.util.List;
import java.util.Set;
/* renamed from: android.os.storage.StorageManagerInternal */
/* loaded from: classes3.dex */
public abstract class StorageManagerInternal {

    /* renamed from: android.os.storage.StorageManagerInternal$CloudProviderChangeListener */
    /* loaded from: classes3.dex */
    public interface CloudProviderChangeListener {
        void onCloudProviderChanged(int i, String str);
    }

    /* renamed from: android.os.storage.StorageManagerInternal$ResetListener */
    /* loaded from: classes3.dex */
    public interface ResetListener {
        void onReset(IVold iVold);
    }

    public abstract void addResetListener(ResetListener resetListener);

    public abstract void freeCache(String str, long j);

    public abstract int getExternalStorageMountMode(int i, String str);

    public abstract List<String> getPrimaryVolumeIds();

    public abstract boolean hasExternalStorageAccess(int i, String str);

    public abstract boolean hasLegacyExternalStorage(int i);

    public abstract boolean isCeStoragePrepared(int i);

    public abstract boolean isExternalStorageService(int i);

    public abstract boolean isFuseMounted(int i);

    public abstract void markCeStoragePrepared(int i);

    public abstract void onAppOpsChanged(int i, int i2, String str, int i3, int i4);

    public abstract void prepareAppDataAfterInstall(String str, int i);

    public abstract boolean prepareStorageDirs(int i, Set<String> set, String str);

    public abstract void registerCloudProviderChangeListener(CloudProviderChangeListener cloudProviderChangeListener);

    public abstract void resetUser(int i);
}
