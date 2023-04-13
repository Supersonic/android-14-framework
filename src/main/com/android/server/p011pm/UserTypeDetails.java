package com.android.server.p011pm;

import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserManager;
import android.p005os.IInstalld;
import com.android.internal.util.Preconditions;
import com.android.server.BundleUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* renamed from: com.android.server.pm.UserTypeDetails */
/* loaded from: classes2.dex */
public final class UserTypeDetails {
    public final int[] mBadgeColors;
    public final int[] mBadgeLabels;
    public final int mBadgeNoBackground;
    public final int mBadgePlain;
    public final int mBaseType;
    public final int[] mDarkThemeBadgeColors;
    public final List<DefaultCrossProfileIntentFilter> mDefaultCrossProfileIntentFilters;
    public final Bundle mDefaultRestrictions;
    public final Bundle mDefaultSecureSettings;
    public final Bundle mDefaultSystemSettings;
    public final int mDefaultUserInfoPropertyFlags;
    public final UserProperties mDefaultUserProperties;
    public final boolean mEnabled;
    public final int mIconBadge;
    public final int mLabel;
    public final int mMaxAllowed;
    public final int mMaxAllowedPerParent;
    public final String mName;

    public UserTypeDetails(String str, boolean z, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int[] iArr, int[] iArr2, int[] iArr3, Bundle bundle, Bundle bundle2, Bundle bundle3, List<DefaultCrossProfileIntentFilter> list, UserProperties userProperties) {
        this.mName = str;
        this.mEnabled = z;
        this.mMaxAllowed = i;
        this.mMaxAllowedPerParent = i5;
        this.mBaseType = i2;
        this.mDefaultUserInfoPropertyFlags = i3;
        this.mDefaultRestrictions = bundle;
        this.mDefaultSystemSettings = bundle2;
        this.mDefaultSecureSettings = bundle3;
        this.mDefaultCrossProfileIntentFilters = list;
        this.mIconBadge = i6;
        this.mBadgePlain = i7;
        this.mBadgeNoBackground = i8;
        this.mLabel = i4;
        this.mBadgeLabels = iArr;
        this.mBadgeColors = iArr2;
        this.mDarkThemeBadgeColors = iArr3;
        this.mDefaultUserProperties = userProperties;
    }

    public String getName() {
        return this.mName;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public int getMaxAllowed() {
        return this.mMaxAllowed;
    }

    public int getMaxAllowedPerParent() {
        return this.mMaxAllowedPerParent;
    }

    public int getDefaultUserInfoFlags() {
        return this.mBaseType | this.mDefaultUserInfoPropertyFlags;
    }

    public boolean hasBadge() {
        return this.mIconBadge != 0;
    }

    public int getIconBadge() {
        return this.mIconBadge;
    }

    public int getBadgePlain() {
        return this.mBadgePlain;
    }

    public int getBadgeNoBackground() {
        return this.mBadgeNoBackground;
    }

    public int getBadgeLabel(int i) {
        int[] iArr = this.mBadgeLabels;
        if (iArr == null || iArr.length == 0 || i < 0) {
            return 0;
        }
        return iArr[Math.min(i, iArr.length - 1)];
    }

    public int getBadgeColor(int i) {
        int[] iArr = this.mBadgeColors;
        if (iArr == null || iArr.length == 0 || i < 0) {
            return 0;
        }
        return iArr[Math.min(i, iArr.length - 1)];
    }

    public int getDarkThemeBadgeColor(int i) {
        int[] iArr = this.mDarkThemeBadgeColors;
        if (iArr == null || iArr.length == 0 || i < 0) {
            return getBadgeColor(i);
        }
        return iArr[Math.min(i, iArr.length - 1)];
    }

    public UserProperties getDefaultUserPropertiesReference() {
        return this.mDefaultUserProperties;
    }

    public boolean isProfile() {
        return (this.mBaseType & IInstalld.FLAG_USE_QUOTA) != 0;
    }

    public boolean isFull() {
        return (this.mBaseType & 1024) != 0;
    }

    public boolean isSystem() {
        return (this.mBaseType & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
    }

    public Bundle getDefaultRestrictions() {
        return BundleUtils.clone(this.mDefaultRestrictions);
    }

    public void addDefaultRestrictionsTo(Bundle bundle) {
        UserRestrictionsUtils.merge(bundle, this.mDefaultRestrictions);
    }

    public Bundle getDefaultSystemSettings() {
        return BundleUtils.clone(this.mDefaultSystemSettings);
    }

    public Bundle getDefaultSecureSettings() {
        return BundleUtils.clone(this.mDefaultSecureSettings);
    }

    public List<DefaultCrossProfileIntentFilter> getDefaultCrossProfileIntentFilters() {
        if (this.mDefaultCrossProfileIntentFilters != null) {
            return new ArrayList(this.mDefaultCrossProfileIntentFilters);
        }
        return Collections.emptyList();
    }

    public void dump(PrintWriter printWriter, String str) {
        String[] stringArray;
        printWriter.print(str);
        printWriter.print("mName: ");
        printWriter.println(this.mName);
        printWriter.print(str);
        printWriter.print("mBaseType: ");
        printWriter.println(UserInfo.flagsToString(this.mBaseType));
        printWriter.print(str);
        printWriter.print("mEnabled: ");
        printWriter.println(this.mEnabled);
        printWriter.print(str);
        printWriter.print("mMaxAllowed: ");
        printWriter.println(this.mMaxAllowed);
        printWriter.print(str);
        printWriter.print("mMaxAllowedPerParent: ");
        printWriter.println(this.mMaxAllowedPerParent);
        printWriter.print(str);
        printWriter.print("mDefaultUserInfoFlags: ");
        printWriter.println(UserInfo.flagsToString(this.mDefaultUserInfoPropertyFlags));
        printWriter.print(str);
        printWriter.print("mLabel: ");
        printWriter.println(this.mLabel);
        this.mDefaultUserProperties.println(printWriter, str);
        String str2 = str + "    ";
        if (isSystem()) {
            printWriter.print(str);
            printWriter.println("config_defaultFirstUserRestrictions: ");
            try {
                Bundle bundle = new Bundle();
                for (String str3 : Resources.getSystem().getStringArray(17236019)) {
                    if (UserRestrictionsUtils.isValidRestriction(str3)) {
                        bundle.putBoolean(str3, true);
                    }
                }
                UserRestrictionsUtils.dumpRestrictions(printWriter, str2, bundle);
            } catch (Resources.NotFoundException unused) {
                printWriter.print(str2);
                printWriter.println("none - resource not found");
            }
        } else {
            printWriter.print(str);
            printWriter.println("mDefaultRestrictions: ");
            UserRestrictionsUtils.dumpRestrictions(printWriter, str2, this.mDefaultRestrictions);
        }
        printWriter.print(str);
        printWriter.print("mIconBadge: ");
        printWriter.println(this.mIconBadge);
        printWriter.print(str);
        printWriter.print("mBadgePlain: ");
        printWriter.println(this.mBadgePlain);
        printWriter.print(str);
        printWriter.print("mBadgeNoBackground: ");
        printWriter.println(this.mBadgeNoBackground);
        printWriter.print(str);
        printWriter.print("mBadgeLabels.length: ");
        int[] iArr = this.mBadgeLabels;
        printWriter.println(iArr != null ? Integer.valueOf(iArr.length) : "0(null)");
        printWriter.print(str);
        printWriter.print("mBadgeColors.length: ");
        int[] iArr2 = this.mBadgeColors;
        printWriter.println(iArr2 != null ? Integer.valueOf(iArr2.length) : "0(null)");
        printWriter.print(str);
        printWriter.print("mDarkThemeBadgeColors.length: ");
        int[] iArr3 = this.mDarkThemeBadgeColors;
        printWriter.println(iArr3 != null ? Integer.valueOf(iArr3.length) : "0(null)");
    }

    /* renamed from: com.android.server.pm.UserTypeDetails$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        public int mBaseType;
        public String mName;
        public int mMaxAllowed = -1;
        public int mMaxAllowedPerParent = -1;
        public int mDefaultUserInfoPropertyFlags = 0;
        public Bundle mDefaultRestrictions = null;
        public Bundle mDefaultSystemSettings = null;
        public Bundle mDefaultSecureSettings = null;
        public List<DefaultCrossProfileIntentFilter> mDefaultCrossProfileIntentFilters = null;
        public int mEnabled = 1;
        public int mLabel = 0;
        public int[] mBadgeLabels = null;
        public int[] mBadgeColors = null;
        public int[] mDarkThemeBadgeColors = null;
        public int mIconBadge = 0;
        public int mBadgePlain = 0;
        public int mBadgeNoBackground = 0;
        public UserProperties mDefaultUserProperties = null;

        public Builder setName(String str) {
            this.mName = str;
            return this;
        }

        public Builder setEnabled(int i) {
            this.mEnabled = i;
            return this;
        }

        public Builder setMaxAllowed(int i) {
            this.mMaxAllowed = i;
            return this;
        }

        public Builder setMaxAllowedPerParent(int i) {
            this.mMaxAllowedPerParent = i;
            return this;
        }

        public Builder setBaseType(int i) {
            this.mBaseType = i;
            return this;
        }

        public Builder setDefaultUserInfoPropertyFlags(int i) {
            this.mDefaultUserInfoPropertyFlags = i;
            return this;
        }

        public Builder setBadgeLabels(int... iArr) {
            this.mBadgeLabels = iArr;
            return this;
        }

        public Builder setBadgeColors(int... iArr) {
            this.mBadgeColors = iArr;
            return this;
        }

        public Builder setDarkThemeBadgeColors(int... iArr) {
            this.mDarkThemeBadgeColors = iArr;
            return this;
        }

        public Builder setIconBadge(int i) {
            this.mIconBadge = i;
            return this;
        }

        public Builder setBadgePlain(int i) {
            this.mBadgePlain = i;
            return this;
        }

        public Builder setBadgeNoBackground(int i) {
            this.mBadgeNoBackground = i;
            return this;
        }

        public Builder setLabel(int i) {
            this.mLabel = i;
            return this;
        }

        public Builder setDefaultRestrictions(Bundle bundle) {
            this.mDefaultRestrictions = bundle;
            return this;
        }

        public Builder setDefaultSecureSettings(Bundle bundle) {
            this.mDefaultSecureSettings = bundle;
            return this;
        }

        public Builder setDefaultCrossProfileIntentFilters(List<DefaultCrossProfileIntentFilter> list) {
            this.mDefaultCrossProfileIntentFilters = list;
            return this;
        }

        public Builder setDefaultUserProperties(UserProperties.Builder builder) {
            this.mDefaultUserProperties = builder.build();
            return this;
        }

        public UserProperties getDefaultUserProperties() {
            if (this.mDefaultUserProperties == null) {
                this.mDefaultUserProperties = new UserProperties.Builder().build();
            }
            return this.mDefaultUserProperties;
        }

        public int getBaseType() {
            return this.mBaseType;
        }

        public UserTypeDetails createUserTypeDetails() {
            Preconditions.checkArgument(this.mName != null, "Cannot create a UserTypeDetails with no name.");
            boolean hasValidBaseType = hasValidBaseType();
            Preconditions.checkArgument(hasValidBaseType, "UserTypeDetails " + this.mName + " has invalid baseType: " + this.mBaseType);
            boolean hasValidPropertyFlags = hasValidPropertyFlags();
            Preconditions.checkArgument(hasValidPropertyFlags, "UserTypeDetails " + this.mName + " has invalid flags: " + Integer.toHexString(this.mDefaultUserInfoPropertyFlags));
            checkSystemAndMainUserPreconditions();
            if (hasBadge()) {
                int[] iArr = this.mBadgeLabels;
                boolean z = (iArr == null || iArr.length == 0) ? false : true;
                Preconditions.checkArgument(z, "UserTypeDetails " + this.mName + " has badge but no badgeLabels.");
                int[] iArr2 = this.mBadgeColors;
                boolean z2 = (iArr2 == null || iArr2.length == 0) ? false : true;
                Preconditions.checkArgument(z2, "UserTypeDetails " + this.mName + " has badge but no badgeColors.");
            }
            if (!isProfile()) {
                List<DefaultCrossProfileIntentFilter> list = this.mDefaultCrossProfileIntentFilters;
                Preconditions.checkArgument(list == null || list.isEmpty(), "UserTypeDetails %s has a non empty defaultCrossProfileIntentFilters", new Object[]{this.mName});
            }
            String str = this.mName;
            boolean z3 = this.mEnabled != 0;
            int i = this.mMaxAllowed;
            int i2 = this.mBaseType;
            int i3 = this.mDefaultUserInfoPropertyFlags;
            int i4 = this.mLabel;
            int i5 = this.mMaxAllowedPerParent;
            int i6 = this.mIconBadge;
            int i7 = this.mBadgePlain;
            int i8 = this.mBadgeNoBackground;
            int[] iArr3 = this.mBadgeLabels;
            int[] iArr4 = this.mBadgeColors;
            int[] iArr5 = this.mDarkThemeBadgeColors;
            return new UserTypeDetails(str, z3, i, i2, i3, i4, i5, i6, i7, i8, iArr3, iArr4, iArr5 == null ? iArr4 : iArr5, this.mDefaultRestrictions, this.mDefaultSystemSettings, this.mDefaultSecureSettings, this.mDefaultCrossProfileIntentFilters, getDefaultUserProperties());
        }

        public final boolean hasBadge() {
            return this.mIconBadge != 0;
        }

        public final boolean isProfile() {
            return (this.mBaseType & IInstalld.FLAG_USE_QUOTA) != 0;
        }

        public final boolean hasValidBaseType() {
            int i = this.mBaseType;
            return i == 1024 || i == 4096 || i == 2048 || i == 3072;
        }

        public final boolean hasValidPropertyFlags() {
            return (this.mDefaultUserInfoPropertyFlags & 7312) == 0;
        }

        public final void checkSystemAndMainUserPreconditions() {
            boolean z = false;
            Preconditions.checkArgument(((this.mBaseType & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) == ((this.mDefaultUserInfoPropertyFlags & 1) != 0), "UserTypeDetails " + this.mName + " cannot be SYSTEM xor PRIMARY.");
            if ((this.mDefaultUserInfoPropertyFlags & 16384) == 0 || this.mMaxAllowed == 1) {
                z = true;
            }
            Preconditions.checkArgument(z, "UserTypeDetails " + this.mName + " must not sanction more than one MainUser.");
        }
    }

    public boolean isManagedProfile() {
        return UserManager.isUserTypeManagedProfile(this.mName);
    }
}
