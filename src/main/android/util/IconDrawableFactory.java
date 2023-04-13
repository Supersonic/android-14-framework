package android.util;

import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyResources;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageItemInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import com.android.internal.C4057R;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public class IconDrawableFactory {
    protected final Context mContext;
    protected final DevicePolicyManager mDpm;
    protected final boolean mEmbedShadow;
    protected final LauncherIcons mLauncherIcons;
    protected final PackageManager mPm;
    protected final UserManager mUm;

    private IconDrawableFactory(Context context, boolean embedShadow) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mUm = (UserManager) context.getSystemService(UserManager.class);
        this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        this.mLauncherIcons = new LauncherIcons(context);
        this.mEmbedShadow = embedShadow;
    }

    protected boolean needsBadging(ApplicationInfo appInfo, int userId) {
        return appInfo.isInstantApp() || this.mUm.hasBadge(userId);
    }

    public Drawable getBadgedIcon(ApplicationInfo appInfo) {
        return getBadgedIcon(appInfo, UserHandle.getUserId(appInfo.uid));
    }

    public Drawable getBadgedIcon(ApplicationInfo appInfo, int userId) {
        return getBadgedIcon(appInfo, appInfo, userId);
    }

    public Drawable getBadgedIcon(PackageItemInfo itemInfo, ApplicationInfo appInfo, final int userId) {
        Drawable icon = this.mPm.loadUnbadgedItemIcon(itemInfo, appInfo);
        if (!this.mEmbedShadow && !needsBadging(appInfo, userId)) {
            return icon;
        }
        Drawable icon2 = getShadowedIcon(icon);
        if (appInfo.isInstantApp()) {
            int badgeColor = Resources.getSystem().getColor(C4057R.color.instant_app_badge, null);
            Drawable badge = this.mContext.getDrawable(C4057R.C4058drawable.ic_instant_icon_badge_bolt);
            icon2 = this.mLauncherIcons.getBadgedDrawable(icon2, badge, badgeColor);
        }
        if (this.mUm.hasBadge(userId)) {
            Drawable badge2 = this.mDpm.getResources().getDrawable(getUpdatableUserIconBadgeId(userId), DevicePolicyResources.Drawables.Style.SOLID_COLORED, new Supplier() { // from class: android.util.IconDrawableFactory$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    Drawable lambda$getBadgedIcon$0;
                    lambda$getBadgedIcon$0 = IconDrawableFactory.this.lambda$getBadgedIcon$0(userId);
                    return lambda$getBadgedIcon$0;
                }
            });
            return this.mLauncherIcons.getBadgedDrawable(icon2, badge2, this.mUm.getUserBadgeColor(userId));
        }
        return icon2;
    }

    private String getUpdatableUserIconBadgeId(int userId) {
        return this.mUm.isManagedProfile(userId) ? DevicePolicyResources.Drawables.WORK_PROFILE_ICON_BADGE : DevicePolicyResources.UNDEFINED;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: getDefaultUserIconBadge */
    public Drawable lambda$getBadgedIcon$0(int userId) {
        return this.mContext.getResources().getDrawable(this.mUm.getUserIconBadgeResId(userId));
    }

    public Drawable getShadowedIcon(Drawable icon) {
        return this.mLauncherIcons.wrapIconDrawableWithShadow(icon);
    }

    public static IconDrawableFactory newInstance(Context context) {
        return new IconDrawableFactory(context, true);
    }

    public static IconDrawableFactory newInstance(Context context, boolean embedShadow) {
        return new IconDrawableFactory(context, embedShadow);
    }
}
