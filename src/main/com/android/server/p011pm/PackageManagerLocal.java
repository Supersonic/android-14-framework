package com.android.server.p011pm;

import android.annotation.SystemApi;
import android.os.UserHandle;
import com.android.server.p011pm.pkg.PackageState;
import java.io.IOException;
import java.util.List;
import java.util.Map;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* renamed from: com.android.server.pm.PackageManagerLocal */
/* loaded from: classes2.dex */
public interface PackageManagerLocal {
    public static final int FLAG_STORAGE_CE = 2;
    public static final int FLAG_STORAGE_DE = 1;

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    /* renamed from: com.android.server.pm.PackageManagerLocal$FilteredSnapshot */
    /* loaded from: classes2.dex */
    public interface FilteredSnapshot extends AutoCloseable {
        @Override // java.lang.AutoCloseable
        void close();

        PackageState getPackageState(String str);

        Map<String, PackageState> getPackageStates();
    }

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    /* renamed from: com.android.server.pm.PackageManagerLocal$UnfilteredSnapshot */
    /* loaded from: classes2.dex */
    public interface UnfilteredSnapshot extends AutoCloseable {
        @Override // java.lang.AutoCloseable
        void close();

        FilteredSnapshot filtered(int i, UserHandle userHandle);

        Map<String, PackageState> getDisabledSystemPackageStates();

        Map<String, PackageState> getPackageStates();
    }

    void reconcileSdkData(String str, String str2, List<String> list, int i, int i2, int i3, String str3, int i4) throws IOException;

    FilteredSnapshot withFilteredSnapshot();

    FilteredSnapshot withFilteredSnapshot(int i, UserHandle userHandle);

    UnfilteredSnapshot withUnfilteredSnapshot();
}
