package com.android.server.uri;

import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.IBinder;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public interface UriGrantsManagerInternal {
    boolean checkAuthorityGrants(int i, ProviderInfo providerInfo, int i2, boolean z);

    int checkGrantUriPermission(int i, String str, Uri uri, int i2, int i3);

    NeededUriGrants checkGrantUriPermissionFromIntent(Intent intent, int i, String str, int i2);

    boolean checkUriPermission(GrantUri grantUri, int i, int i2);

    void dump(PrintWriter printWriter, boolean z, String str);

    void grantUriPermissionUncheckedFromIntent(NeededUriGrants neededUriGrants, UriPermissionOwner uriPermissionOwner);

    IBinder newUriPermissionOwner(String str);

    void onSystemReady();

    void removeUriPermissionIfNeeded(UriPermission uriPermission);

    void removeUriPermissionsForPackage(String str, int i, boolean z, boolean z2);

    void revokeUriPermission(String str, int i, GrantUri grantUri, int i2);

    void revokeUriPermissionFromOwner(IBinder iBinder, Uri uri, int i, int i2);

    void revokeUriPermissionFromOwner(IBinder iBinder, Uri uri, int i, int i2, String str, int i3);
}
