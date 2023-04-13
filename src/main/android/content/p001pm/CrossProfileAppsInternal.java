package android.content.p001pm;

import android.content.p001pm.PackageManager;
import android.p008os.UserHandle;
import java.util.List;
/* renamed from: android.content.pm.CrossProfileAppsInternal */
/* loaded from: classes.dex */
public abstract class CrossProfileAppsInternal {
    public abstract List<UserHandle> getTargetUserProfiles(String str, int i);

    public abstract void setInteractAcrossProfilesAppOp(String str, int i, int i2);

    public abstract boolean verifyPackageHasInteractAcrossProfilePermission(String str, int i) throws PackageManager.NameNotFoundException;

    public abstract boolean verifyUidHasInteractAcrossProfilePermission(String str, int i);
}
