package android.content.p001pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.p008os.UserHandle;
/* renamed from: android.content.pm.LauncherActivityInfo */
/* loaded from: classes.dex */
public class LauncherActivityInfo {
    private final LauncherActivityInfoInternal mInternal;
    private final PackageManager mPm;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LauncherActivityInfo(Context context, LauncherActivityInfoInternal internal) {
        this.mPm = context.getPackageManager();
        this.mInternal = internal;
    }

    public ComponentName getComponentName() {
        return this.mInternal.getComponentName();
    }

    public UserHandle getUser() {
        return this.mInternal.getUser();
    }

    public CharSequence getLabel() {
        return getActivityInfo().loadLabel(this.mPm);
    }

    public float getLoadingProgress() {
        return this.mInternal.getIncrementalStatesInfo().getProgress();
    }

    public Drawable getIcon(int density) {
        int iconRes = getActivityInfo().getIconResource();
        Drawable icon = null;
        if (density != 0 && iconRes != 0) {
            try {
                Resources resources = this.mPm.getResourcesForApplication(getActivityInfo().applicationInfo);
                icon = resources.getDrawableForDensity(iconRes, density);
            } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
            }
        }
        if (icon == null) {
            return getActivityInfo().loadIcon(this.mPm);
        }
        return icon;
    }

    public int getApplicationFlags() {
        return getActivityInfo().flags;
    }

    public ActivityInfo getActivityInfo() {
        return this.mInternal.getActivityInfo();
    }

    public ApplicationInfo getApplicationInfo() {
        return getActivityInfo().applicationInfo;
    }

    public long getFirstInstallTime() {
        try {
            return this.mPm.getPackageInfo(getActivityInfo().packageName, 8192).firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            return 0L;
        }
    }

    public String getName() {
        return getActivityInfo().name;
    }

    public Drawable getBadgedIcon(int density) {
        Drawable originalIcon = getIcon(density);
        return this.mPm.getUserBadgedIcon(originalIcon, this.mInternal.getUser());
    }
}
