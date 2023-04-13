package com.android.server.p008om;

import android.content.om.OverlayableInfo;
import android.util.ArrayMap;
import com.android.server.p011pm.pkg.PackageState;
import java.io.IOException;
import java.util.Map;
/* renamed from: com.android.server.om.PackageManagerHelper */
/* loaded from: classes2.dex */
public interface PackageManagerHelper {
    boolean doesTargetDefineOverlayable(String str, int i) throws IOException;

    void enforcePermission(String str, String str2) throws SecurityException;

    String getConfigSignaturePackage();

    Map<String, Map<String, String>> getNamedActors();

    OverlayableInfo getOverlayableForTarget(String str, String str2, int i) throws IOException;

    PackageState getPackageStateForUser(String str, int i);

    String[] getPackagesForUid(int i);

    ArrayMap<String, PackageState> initializeForUser(int i);

    boolean signaturesMatching(String str, String str2, int i);
}
