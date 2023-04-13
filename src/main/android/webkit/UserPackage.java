package android.webkit;

import android.content.Context;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.UserInfo;
import android.p008os.UserManager;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class UserPackage {
    public static final int MINIMUM_SUPPORTED_SDK = 33;
    private final PackageInfo mPackageInfo;
    private final UserInfo mUserInfo;

    public UserPackage(UserInfo user, PackageInfo packageInfo) {
        this.mUserInfo = user;
        this.mPackageInfo = packageInfo;
    }

    public static List<UserPackage> getPackageInfosAllUsers(Context context, String packageName, int packageFlags) {
        List<UserInfo> users = getAllUsers(context);
        List<UserPackage> userPackages = new ArrayList<>(users.size());
        for (UserInfo user : users) {
            PackageInfo packageInfo = null;
            try {
                packageInfo = context.getPackageManager().getPackageInfoAsUser(packageName, packageFlags, user.f48id);
            } catch (PackageManager.NameNotFoundException e) {
            }
            userPackages.add(new UserPackage(user, packageInfo));
        }
        return userPackages;
    }

    public boolean isEnabledPackage() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null) {
            return false;
        }
        return packageInfo.applicationInfo.enabled;
    }

    public boolean isInstalledPackage() {
        PackageInfo packageInfo = this.mPackageInfo;
        return (packageInfo == null || (packageInfo.applicationInfo.flags & 8388608) == 0 || (this.mPackageInfo.applicationInfo.privateFlags & 1) != 0) ? false : true;
    }

    public static boolean hasCorrectTargetSdkVersion(PackageInfo packageInfo) {
        return packageInfo.applicationInfo.targetSdkVersion >= 33;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public PackageInfo getPackageInfo() {
        return this.mPackageInfo;
    }

    private static List<UserInfo> getAllUsers(Context context) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        return userManager.getUsers();
    }
}
