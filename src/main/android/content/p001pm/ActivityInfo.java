package android.content.p001pm;

import android.app.compat.CompatChanges;
import android.content.res.TypedArray;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.util.ArraySet;
import android.util.NtpTrustedTime;
import android.util.Printer;
import com.android.internal.util.Parcelling;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
/* renamed from: android.content.pm.ActivityInfo */
/* loaded from: classes.dex */
public class ActivityInfo extends ComponentInfo implements Parcelable {
    public static final long ALWAYS_SANDBOX_DISPLAY_APIS = 185004937;
    private static final long CHECK_MIN_WIDTH_HEIGHT_FOR_MULTI_WINDOW = 197654537;
    public static final int COLOR_MODE_A8 = 4;
    public static final int COLOR_MODE_DEFAULT = 0;
    public static final int COLOR_MODE_HDR = 2;
    public static final int COLOR_MODE_HDR10 = 3;
    public static final int COLOR_MODE_WIDE_COLOR_GAMUT = 1;
    public static final int CONFIG_ASSETS_PATHS = Integer.MIN_VALUE;
    public static final int CONFIG_COLOR_MODE = 16384;
    public static final int CONFIG_DENSITY = 4096;
    public static final int CONFIG_FONT_SCALE = 1073741824;
    public static final int CONFIG_FONT_WEIGHT_ADJUSTMENT = 268435456;
    public static final int CONFIG_GRAMMATICAL_GENDER = 32768;
    public static final int CONFIG_KEYBOARD = 16;
    public static final int CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int CONFIG_LAYOUT_DIRECTION = 8192;
    public static final int CONFIG_LOCALE = 4;
    public static final int CONFIG_MCC = 1;
    public static final int CONFIG_MNC = 2;
    public static final int CONFIG_NAVIGATION = 64;
    public static final int CONFIG_ORIENTATION = 128;
    public static final int CONFIG_SCREEN_LAYOUT = 256;
    public static final int CONFIG_SCREEN_SIZE = 1024;
    public static final int CONFIG_SMALLEST_SCREEN_SIZE = 2048;
    public static final int CONFIG_TOUCHSCREEN = 8;
    public static final int CONFIG_UI_MODE = 512;
    public static final int CONFIG_WINDOW_CONFIGURATION = 536870912;
    public static final int DOCUMENT_LAUNCH_ALWAYS = 2;
    public static final int DOCUMENT_LAUNCH_INTO_EXISTING = 1;
    public static final int DOCUMENT_LAUNCH_NEVER = 3;
    public static final int DOCUMENT_LAUNCH_NONE = 0;
    @Deprecated
    public static final int FLAG_ALLOW_EMBEDDED = Integer.MIN_VALUE;
    public static final int FLAG_ALLOW_TASK_REPARENTING = 64;
    public static final int FLAG_ALLOW_UNTRUSTED_ACTIVITY_EMBEDDING = 268435456;
    public static final int FLAG_ALWAYS_FOCUSABLE = 262144;
    public static final int FLAG_ALWAYS_RETAIN_TASK_STATE = 8;
    public static final int FLAG_AUTO_REMOVE_FROM_RECENTS = 8192;
    public static final int FLAG_CAN_DISPLAY_ON_REMOTE_DEVICES = 65536;
    public static final int FLAG_CLEAR_TASK_ON_LAUNCH = 4;
    public static final int FLAG_ENABLE_VR_MODE = 32768;
    public static final int FLAG_EXCLUDE_FROM_RECENTS = 32;
    public static final int FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS = 256;
    public static final int FLAG_FINISH_ON_TASK_LAUNCH = 2;
    public static final int FLAG_HARDWARE_ACCELERATED = 512;
    public static final int FLAG_IMMERSIVE = 2048;
    public static final int FLAG_IMPLICITLY_VISIBLE_TO_INSTANT_APP = 2097152;
    public static final int FLAG_INHERIT_SHOW_WHEN_LOCKED = 1;
    public static final int FLAG_MULTIPROCESS = 1;
    public static final int FLAG_NO_HISTORY = 128;
    public static final int FLAG_PREFER_MINIMAL_POST_PROCESSING = 33554432;
    public static final int FLAG_RELINQUISH_TASK_IDENTITY = 4096;
    public static final int FLAG_RESUME_WHILE_PAUSING = 16384;
    public static final int FLAG_SHOW_FOR_ALL_USERS = 1024;
    public static final int FLAG_SHOW_WHEN_LOCKED = 8388608;
    public static final int FLAG_SINGLE_USER = 1073741824;
    public static final int FLAG_STATE_NOT_NEEDED = 16;
    public static final int FLAG_SUPPORTS_PICTURE_IN_PICTURE = 4194304;
    public static final int FLAG_SYSTEM_USER_ONLY = 536870912;
    public static final int FLAG_TURN_SCREEN_ON = 16777216;
    public static final int FLAG_VISIBLE_TO_INSTANT_APP = 1048576;
    public static final long FORCE_NON_RESIZE_APP = 181136395;
    public static final long FORCE_RESIZE_APP = 174042936;
    public static final int LAUNCH_MULTIPLE = 0;
    public static final int LAUNCH_SINGLE_INSTANCE = 3;
    public static final int LAUNCH_SINGLE_INSTANCE_PER_TASK = 4;
    public static final int LAUNCH_SINGLE_TASK = 2;
    public static final int LAUNCH_SINGLE_TOP = 1;
    public static final int LOCK_TASK_LAUNCH_MODE_ALWAYS = 2;
    public static final int LOCK_TASK_LAUNCH_MODE_DEFAULT = 0;
    public static final int LOCK_TASK_LAUNCH_MODE_IF_ALLOWLISTED = 3;
    public static final int LOCK_TASK_LAUNCH_MODE_NEVER = 1;
    public static final long NEVER_SANDBOX_DISPLAY_APIS = 184838306;
    public static final long OVERRIDE_ANY_ORIENTATION = 265464455;
    public static final long OVERRIDE_CAMERA_COMPAT_DISABLE_FORCE_ROTATION = 263959004;
    public static final long OVERRIDE_CAMERA_COMPAT_DISABLE_REFRESH = 264304459;
    public static final long OVERRIDE_CAMERA_COMPAT_ENABLE_REFRESH_VIA_PAUSE = 264301586;
    public static final long OVERRIDE_ENABLE_COMPAT_FAKE_FOCUS = 263259275;
    public static final long OVERRIDE_ENABLE_COMPAT_IGNORE_REQUESTED_ORIENTATION = 254631730;
    public static final long OVERRIDE_LANDSCAPE_ORIENTATION_TO_REVERSE_LANDSCAPE = 266124927;
    public static final long OVERRIDE_MIN_ASPECT_RATIO = 174042980;
    public static final long OVERRIDE_MIN_ASPECT_RATIO_EXCLUDE_PORTRAIT_FULLSCREEN = 218959984;
    public static final long OVERRIDE_MIN_ASPECT_RATIO_LARGE = 180326787;
    public static final float OVERRIDE_MIN_ASPECT_RATIO_LARGE_VALUE = 1.7777778f;
    public static final long OVERRIDE_MIN_ASPECT_RATIO_MEDIUM = 180326845;
    public static final float OVERRIDE_MIN_ASPECT_RATIO_MEDIUM_VALUE = 1.5f;
    public static final long OVERRIDE_MIN_ASPECT_RATIO_PORTRAIT_ONLY = 203647190;
    public static final long OVERRIDE_MIN_ASPECT_RATIO_TO_ALIGN_WITH_SPLIT_SCREEN = 208648326;
    public static final long OVERRIDE_ORIENTATION_ONLY_FOR_CAMERA = 265456536;
    public static final long OVERRIDE_RESPECT_REQUESTED_ORIENTATION = 236283604;
    public static final long OVERRIDE_SANDBOX_VIEW_BOUNDS_APIS = 237531167;
    public static final long OVERRIDE_UNDEFINED_ORIENTATION_TO_NOSENSOR = 265451093;
    public static final long OVERRIDE_UNDEFINED_ORIENTATION_TO_PORTRAIT = 265452344;
    public static final long OVERRIDE_USE_DISPLAY_LANDSCAPE_NATURAL_ORIENTATION = 255940284;
    public static final int PERSIST_ACROSS_REBOOTS = 2;
    public static final int PERSIST_NEVER = 1;
    public static final int PERSIST_ROOT_ONLY = 0;
    public static final int PRIVATE_FLAG_DISABLE_ON_BACK_INVOKED_CALLBACK = 8;
    public static final int PRIVATE_FLAG_ENABLE_ON_BACK_INVOKED_CALLBACK = 4;
    public static final int PRIVATE_FLAG_HOME_TRANSITION_SOUND = 2;
    public static final int RESIZE_MODE_FORCE_RESIZABLE_LANDSCAPE_ONLY = 5;
    public static final int RESIZE_MODE_FORCE_RESIZABLE_PORTRAIT_ONLY = 6;
    public static final int RESIZE_MODE_FORCE_RESIZABLE_PRESERVE_ORIENTATION = 7;
    public static final int RESIZE_MODE_FORCE_RESIZEABLE = 4;
    public static final int RESIZE_MODE_RESIZEABLE = 2;
    public static final int RESIZE_MODE_RESIZEABLE_AND_PIPABLE_DEPRECATED = 3;
    public static final int RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION = 1;
    public static final int RESIZE_MODE_UNRESIZEABLE = 0;
    public static final int SCREEN_ORIENTATION_BEHIND = 3;
    public static final int SCREEN_ORIENTATION_FULL_SENSOR = 10;
    public static final int SCREEN_ORIENTATION_FULL_USER = 13;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
    public static final int SCREEN_ORIENTATION_LOCKED = 14;
    public static final int SCREEN_ORIENTATION_NOSENSOR = 5;
    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
    public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
    public static final int SCREEN_ORIENTATION_SENSOR = 4;
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
    public static final int SCREEN_ORIENTATION_UNSET = -2;
    public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
    public static final int SCREEN_ORIENTATION_USER = 2;
    public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
    public static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12;
    public static final int SIZE_CHANGES_SUPPORTED_METADATA = 2;
    public static final int SIZE_CHANGES_SUPPORTED_OVERRIDE = 3;
    public static final int SIZE_CHANGES_UNSUPPORTED_METADATA = 0;
    public static final int SIZE_CHANGES_UNSUPPORTED_OVERRIDE = 1;
    public static final int UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW = 1;
    public int colorMode;
    public int configChanges;
    public int documentLaunchMode;
    public int flags;
    public int launchMode;
    public String launchToken;
    public int lockTaskLaunchMode;
    private Set<String> mKnownActivityEmbeddingCerts;
    private float mMaxAspectRatio;
    private float mMinAspectRatio;
    public int maxRecents;
    public String parentActivityName;
    public String permission;
    public int persistableMode;
    public int privateFlags;
    public String requestedVrComponent;
    public String requiredDisplayCategory;
    public int resizeMode;
    public int rotationAnimation;
    public int screenOrientation;
    public int softInputMode;
    public boolean supportsSizeChanges;
    public String targetActivity;
    public String taskAffinity;
    public int theme;
    public int uiOptions;
    public WindowLayout windowLayout;
    private static final Parcelling.BuiltIn.ForStringSet sForStringSet = (Parcelling.BuiltIn.ForStringSet) Parcelling.Cache.getOrCreate(Parcelling.BuiltIn.ForStringSet.class);
    public static int[] CONFIG_NATIVE_BITS = {2, 1, 4, 8, 16, 32, 64, 128, 2048, 4096, 512, 8192, 256, 16384, 65536, 131072};
    public static final Parcelable.Creator<ActivityInfo> CREATOR = new Parcelable.Creator<ActivityInfo>() { // from class: android.content.pm.ActivityInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityInfo createFromParcel(Parcel source) {
            return new ActivityInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityInfo[] newArray(int size) {
            return new ActivityInfo[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ActivityInfo$ColorMode */
    /* loaded from: classes.dex */
    public @interface ColorMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ActivityInfo$Config */
    /* loaded from: classes.dex */
    public @interface Config {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ActivityInfo$LaunchMode */
    /* loaded from: classes.dex */
    public @interface LaunchMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ActivityInfo$ScreenOrientation */
    /* loaded from: classes.dex */
    public @interface ScreenOrientation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ActivityInfo$SizeChangesSupportMode */
    /* loaded from: classes.dex */
    public @interface SizeChangesSupportMode {
    }

    public static String launchModeToString(int launchMode) {
        switch (launchMode) {
            case 0:
                return "LAUNCH_MULTIPLE";
            case 1:
                return "LAUNCH_SINGLE_TOP";
            case 2:
                return "LAUNCH_SINGLE_TASK";
            case 3:
                return "LAUNCH_SINGLE_INSTANCE";
            case 4:
                return "LAUNCH_SINGLE_INSTANCE_PER_TASK";
            default:
                return "unknown=" + launchMode;
        }
    }

    public static int activityInfoConfigJavaToNative(int input) {
        int output = 0;
        int i = 0;
        while (true) {
            int[] iArr = CONFIG_NATIVE_BITS;
            if (i < iArr.length) {
                if (((1 << i) & input) != 0) {
                    output |= iArr[i];
                }
                i++;
            } else {
                return output;
            }
        }
    }

    public static int activityInfoConfigNativeToJava(int input) {
        int output = 0;
        int i = 0;
        while (true) {
            int[] iArr = CONFIG_NATIVE_BITS;
            if (i < iArr.length) {
                if ((iArr[i] & input) != 0) {
                    output |= 1 << i;
                }
                i++;
            } else {
                return output;
            }
        }
    }

    public int getRealConfigChanged() {
        if (this.applicationInfo.targetSdkVersion < 13) {
            return this.configChanges | 1024 | 2048;
        }
        return this.configChanges;
    }

    public static final String lockTaskLaunchModeToString(int lockTaskLaunchMode) {
        switch (lockTaskLaunchMode) {
            case 0:
                return "LOCK_TASK_LAUNCH_MODE_DEFAULT";
            case 1:
                return "LOCK_TASK_LAUNCH_MODE_NEVER";
            case 2:
                return "LOCK_TASK_LAUNCH_MODE_ALWAYS";
            case 3:
                return "LOCK_TASK_LAUNCH_MODE_IF_ALLOWLISTED";
            default:
                return "unknown=" + lockTaskLaunchMode;
        }
    }

    public ActivityInfo() {
        this.resizeMode = 2;
        this.colorMode = 0;
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.rotationAnimation = -1;
    }

    public ActivityInfo(ActivityInfo orig) {
        super(orig);
        this.resizeMode = 2;
        this.colorMode = 0;
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.rotationAnimation = -1;
        this.theme = orig.theme;
        this.launchMode = orig.launchMode;
        this.documentLaunchMode = orig.documentLaunchMode;
        this.permission = orig.permission;
        this.mKnownActivityEmbeddingCerts = orig.mKnownActivityEmbeddingCerts;
        this.taskAffinity = orig.taskAffinity;
        this.targetActivity = orig.targetActivity;
        this.flags = orig.flags;
        this.privateFlags = orig.privateFlags;
        this.screenOrientation = orig.screenOrientation;
        this.configChanges = orig.configChanges;
        this.softInputMode = orig.softInputMode;
        this.uiOptions = orig.uiOptions;
        this.parentActivityName = orig.parentActivityName;
        this.maxRecents = orig.maxRecents;
        this.lockTaskLaunchMode = orig.lockTaskLaunchMode;
        this.windowLayout = orig.windowLayout;
        this.resizeMode = orig.resizeMode;
        this.requestedVrComponent = orig.requestedVrComponent;
        this.rotationAnimation = orig.rotationAnimation;
        this.colorMode = orig.colorMode;
        this.mMaxAspectRatio = orig.mMaxAspectRatio;
        this.mMinAspectRatio = orig.mMinAspectRatio;
        this.supportsSizeChanges = orig.supportsSizeChanges;
        this.requiredDisplayCategory = orig.requiredDisplayCategory;
    }

    public final int getThemeResource() {
        int i = this.theme;
        return i != 0 ? i : this.applicationInfo.theme;
    }

    private String persistableModeToString() {
        switch (this.persistableMode) {
            case 0:
                return "PERSIST_ROOT_ONLY";
            case 1:
                return "PERSIST_NEVER";
            case 2:
                return "PERSIST_ACROSS_REBOOTS";
            default:
                return "UNKNOWN=" + this.persistableMode;
        }
    }

    public boolean hasFixedAspectRatio() {
        return (getMaxAspectRatio() == 0.0f && getMinAspectRatio() == 0.0f) ? false : true;
    }

    public boolean isFixedOrientation() {
        return isFixedOrientation(this.screenOrientation);
    }

    public static boolean isFixedOrientation(int orientation) {
        return orientation == 14 || orientation == 5 || isFixedOrientationLandscape(orientation) || isFixedOrientationPortrait(orientation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFixedOrientationLandscape() {
        return isFixedOrientationLandscape(this.screenOrientation);
    }

    public static boolean isFixedOrientationLandscape(int orientation) {
        return orientation == 0 || orientation == 6 || orientation == 8 || orientation == 11;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFixedOrientationPortrait() {
        return isFixedOrientationPortrait(this.screenOrientation);
    }

    public static boolean isFixedOrientationPortrait(int orientation) {
        return orientation == 1 || orientation == 7 || orientation == 9 || orientation == 12;
    }

    public static int reverseOrientation(int orientation) {
        switch (orientation) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
            case 3:
            case 4:
            case 5:
            case 10:
            default:
                return orientation;
            case 6:
                return 7;
            case 7:
                return 6;
            case 8:
                return 9;
            case 9:
                return 8;
            case 11:
                return 12;
            case 12:
                return 11;
        }
    }

    public boolean supportsPictureInPicture() {
        return (this.flags & 4194304) != 0;
    }

    public int supportsSizeChanges() {
        if (isChangeEnabled(FORCE_NON_RESIZE_APP)) {
            return 1;
        }
        if (this.supportsSizeChanges) {
            return 2;
        }
        if (isChangeEnabled(FORCE_RESIZE_APP)) {
            return 3;
        }
        return 0;
    }

    public boolean neverSandboxDisplayApis(ConstrainDisplayApisConfig constrainDisplayApisConfig) {
        return isChangeEnabled(NEVER_SANDBOX_DISPLAY_APIS) || constrainDisplayApisConfig.getNeverConstrainDisplayApis(this.applicationInfo);
    }

    public boolean alwaysSandboxDisplayApis(ConstrainDisplayApisConfig constrainDisplayApisConfig) {
        return isChangeEnabled(ALWAYS_SANDBOX_DISPLAY_APIS) || constrainDisplayApisConfig.getAlwaysConstrainDisplayApis(this.applicationInfo);
    }

    public void setMaxAspectRatio(float maxAspectRatio) {
        this.mMaxAspectRatio = maxAspectRatio >= 0.0f ? maxAspectRatio : 0.0f;
    }

    public float getMaxAspectRatio() {
        return this.mMaxAspectRatio;
    }

    public void setMinAspectRatio(float minAspectRatio) {
        this.mMinAspectRatio = minAspectRatio >= 0.0f ? minAspectRatio : 0.0f;
    }

    public float getMinAspectRatio() {
        return this.mMinAspectRatio;
    }

    public Set<String> getKnownActivityEmbeddingCerts() {
        Set<String> set = this.mKnownActivityEmbeddingCerts;
        return set == null ? Collections.emptySet() : set;
    }

    public void setKnownActivityEmbeddingCerts(Set<String> knownActivityEmbeddingCerts) {
        this.mKnownActivityEmbeddingCerts = new ArraySet();
        for (String knownCert : knownActivityEmbeddingCerts) {
            this.mKnownActivityEmbeddingCerts.add(knownCert.toUpperCase(Locale.US));
        }
    }

    public boolean isChangeEnabled(long changeId) {
        return CompatChanges.isChangeEnabled(changeId, this.applicationInfo.packageName, UserHandle.getUserHandleForUid(this.applicationInfo.uid));
    }

    public float getManifestMinAspectRatio() {
        return this.mMinAspectRatio;
    }

    public static boolean isResizeableMode(int mode) {
        return mode == 2 || mode == 4 || mode == 6 || mode == 5 || mode == 7 || mode == 1;
    }

    public static boolean isPreserveOrientationMode(int mode) {
        return mode == 6 || mode == 5 || mode == 7;
    }

    public static String resizeModeToString(int mode) {
        switch (mode) {
            case 0:
                return "RESIZE_MODE_UNRESIZEABLE";
            case 1:
                return "RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION";
            case 2:
                return "RESIZE_MODE_RESIZEABLE";
            case 3:
            default:
                return "unknown=" + mode;
            case 4:
                return "RESIZE_MODE_FORCE_RESIZEABLE";
            case 5:
                return "RESIZE_MODE_FORCE_RESIZABLE_LANDSCAPE_ONLY";
            case 6:
                return "RESIZE_MODE_FORCE_RESIZABLE_PORTRAIT_ONLY";
            case 7:
                return "RESIZE_MODE_FORCE_RESIZABLE_PRESERVE_ORIENTATION";
        }
    }

    public static String sizeChangesSupportModeToString(int mode) {
        switch (mode) {
            case 0:
                return "SIZE_CHANGES_UNSUPPORTED_METADATA";
            case 1:
                return "SIZE_CHANGES_UNSUPPORTED_OVERRIDE";
            case 2:
                return "SIZE_CHANGES_SUPPORTED_METADATA";
            case 3:
                return "SIZE_CHANGES_SUPPORTED_OVERRIDE";
            default:
                return "unknown=" + mode;
        }
    }

    public boolean shouldCheckMinWidthHeightForMultiWindow() {
        return isChangeEnabled(CHECK_MIN_WIDTH_HEIGHT_FOR_MULTI_WINDOW);
    }

    public boolean hasOnBackInvokedCallbackEnabled() {
        return (this.privateFlags & 12) != 0;
    }

    public boolean isOnBackInvokedCallbackEnabled() {
        return hasOnBackInvokedCallbackEnabled() && (this.privateFlags & 4) != 0;
    }

    public void dump(Printer pw, String prefix) {
        dump(pw, prefix, 3);
    }

    public void dump(Printer pw, String prefix, int dumpFlags) {
        super.dumpFront(pw, prefix);
        if (this.permission != null) {
            pw.println(prefix + "permission=" + this.permission);
        }
        if ((dumpFlags & 1) != 0) {
            pw.println(prefix + "taskAffinity=" + this.taskAffinity + " targetActivity=" + this.targetActivity + " persistableMode=" + persistableModeToString());
        }
        if (this.launchMode != 0 || this.flags != 0 || this.privateFlags != 0 || this.theme != 0) {
            pw.println(prefix + "launchMode=" + launchModeToString(this.launchMode) + " flags=0x" + Integer.toHexString(this.flags) + " privateFlags=0x" + Integer.toHexString(this.privateFlags) + " theme=0x" + Integer.toHexString(this.theme));
        }
        if (this.screenOrientation != -1 || this.configChanges != 0 || this.softInputMode != 0) {
            pw.println(prefix + "screenOrientation=" + this.screenOrientation + " configChanges=0x" + Integer.toHexString(this.configChanges) + " softInputMode=0x" + Integer.toHexString(this.softInputMode));
        }
        if (this.uiOptions != 0) {
            pw.println(prefix + " uiOptions=0x" + Integer.toHexString(this.uiOptions));
        }
        if ((dumpFlags & 1) != 0) {
            pw.println(prefix + "lockTaskLaunchMode=" + lockTaskLaunchModeToString(this.lockTaskLaunchMode));
        }
        if (this.windowLayout != null) {
            pw.println(prefix + "windowLayout=" + this.windowLayout.width + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.windowLayout.widthFraction + ", " + this.windowLayout.height + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.windowLayout.heightFraction + ", " + this.windowLayout.gravity);
        }
        pw.println(prefix + "resizeMode=" + resizeModeToString(this.resizeMode));
        if (this.requestedVrComponent != null) {
            pw.println(prefix + "requestedVrComponent=" + this.requestedVrComponent);
        }
        if (getMaxAspectRatio() != 0.0f) {
            pw.println(prefix + "maxAspectRatio=" + getMaxAspectRatio());
        }
        float minAspectRatio = getMinAspectRatio();
        if (minAspectRatio != 0.0f) {
            pw.println(prefix + "minAspectRatio=" + minAspectRatio);
        }
        if (this.supportsSizeChanges) {
            pw.println(prefix + "supportsSizeChanges=true");
        }
        if (this.mKnownActivityEmbeddingCerts != null) {
            pw.println(prefix + "knownActivityEmbeddingCerts=" + this.mKnownActivityEmbeddingCerts);
        }
        if (this.requiredDisplayCategory != null) {
            pw.println(prefix + "requiredDisplayCategory=" + this.requiredDisplayCategory);
        }
        super.dumpBack(pw, prefix, dumpFlags);
    }

    public String toString() {
        return "ActivityInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.p001pm.ComponentInfo, android.content.p001pm.PackageItemInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeInt(this.theme);
        dest.writeInt(this.launchMode);
        dest.writeInt(this.documentLaunchMode);
        dest.writeString8(this.permission);
        dest.writeString8(this.taskAffinity);
        dest.writeString8(this.targetActivity);
        dest.writeString8(this.launchToken);
        dest.writeInt(this.flags);
        dest.writeInt(this.privateFlags);
        dest.writeInt(this.screenOrientation);
        dest.writeInt(this.configChanges);
        dest.writeInt(this.softInputMode);
        dest.writeInt(this.uiOptions);
        dest.writeString8(this.parentActivityName);
        dest.writeInt(this.persistableMode);
        dest.writeInt(this.maxRecents);
        dest.writeInt(this.lockTaskLaunchMode);
        if (this.windowLayout != null) {
            dest.writeInt(1);
            this.windowLayout.writeToParcel(dest);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.resizeMode);
        dest.writeString8(this.requestedVrComponent);
        dest.writeInt(this.rotationAnimation);
        dest.writeInt(this.colorMode);
        dest.writeFloat(this.mMaxAspectRatio);
        dest.writeFloat(this.mMinAspectRatio);
        dest.writeBoolean(this.supportsSizeChanges);
        sForStringSet.parcel(this.mKnownActivityEmbeddingCerts, dest, this.flags);
        dest.writeString8(this.requiredDisplayCategory);
    }

    public static boolean isTranslucentOrFloating(TypedArray attributes) {
        boolean isTranslucent = attributes.getBoolean(5, false);
        boolean isFloating = attributes.getBoolean(4, false);
        return isFloating || isTranslucent;
    }

    public static String screenOrientationToString(int orientation) {
        switch (orientation) {
            case -2:
                return "SCREEN_ORIENTATION_UNSET";
            case -1:
                return "SCREEN_ORIENTATION_UNSPECIFIED";
            case 0:
                return "SCREEN_ORIENTATION_LANDSCAPE";
            case 1:
                return "SCREEN_ORIENTATION_PORTRAIT";
            case 2:
                return "SCREEN_ORIENTATION_USER";
            case 3:
                return "SCREEN_ORIENTATION_BEHIND";
            case 4:
                return "SCREEN_ORIENTATION_SENSOR";
            case 5:
                return "SCREEN_ORIENTATION_NOSENSOR";
            case 6:
                return "SCREEN_ORIENTATION_SENSOR_LANDSCAPE";
            case 7:
                return "SCREEN_ORIENTATION_SENSOR_PORTRAIT";
            case 8:
                return "SCREEN_ORIENTATION_REVERSE_LANDSCAPE";
            case 9:
                return "SCREEN_ORIENTATION_REVERSE_PORTRAIT";
            case 10:
                return "SCREEN_ORIENTATION_FULL_SENSOR";
            case 11:
                return "SCREEN_ORIENTATION_USER_LANDSCAPE";
            case 12:
                return "SCREEN_ORIENTATION_USER_PORTRAIT";
            case 13:
                return "SCREEN_ORIENTATION_FULL_USER";
            case 14:
                return "SCREEN_ORIENTATION_LOCKED";
            default:
                return Integer.toString(orientation);
        }
    }

    public static String colorModeToString(int colorMode) {
        switch (colorMode) {
            case 0:
                return "COLOR_MODE_DEFAULT";
            case 1:
                return "COLOR_MODE_WIDE_COLOR_GAMUT";
            case 2:
                return "COLOR_MODE_HDR";
            case 3:
            default:
                return Integer.toString(colorMode);
            case 4:
                return "COLOR_MODE_A8";
        }
    }

    private ActivityInfo(Parcel source) {
        super(source);
        this.resizeMode = 2;
        this.colorMode = 0;
        this.screenOrientation = -1;
        this.uiOptions = 0;
        this.rotationAnimation = -1;
        this.theme = source.readInt();
        this.launchMode = source.readInt();
        this.documentLaunchMode = source.readInt();
        this.permission = source.readString8();
        this.taskAffinity = source.readString8();
        this.targetActivity = source.readString8();
        this.launchToken = source.readString8();
        this.flags = source.readInt();
        this.privateFlags = source.readInt();
        this.screenOrientation = source.readInt();
        this.configChanges = source.readInt();
        this.softInputMode = source.readInt();
        this.uiOptions = source.readInt();
        this.parentActivityName = source.readString8();
        this.persistableMode = source.readInt();
        this.maxRecents = source.readInt();
        this.lockTaskLaunchMode = source.readInt();
        if (source.readInt() == 1) {
            this.windowLayout = new WindowLayout(source);
        }
        this.resizeMode = source.readInt();
        this.requestedVrComponent = source.readString8();
        this.rotationAnimation = source.readInt();
        this.colorMode = source.readInt();
        this.mMaxAspectRatio = source.readFloat();
        this.mMinAspectRatio = source.readFloat();
        this.supportsSizeChanges = source.readBoolean();
        Set<String> unparcel = sForStringSet.unparcel(source);
        this.mKnownActivityEmbeddingCerts = unparcel;
        if (unparcel.isEmpty()) {
            this.mKnownActivityEmbeddingCerts = null;
        }
        this.requiredDisplayCategory = source.readString8();
    }

    /* renamed from: android.content.pm.ActivityInfo$WindowLayout */
    /* loaded from: classes.dex */
    public static final class WindowLayout {
        public final int gravity;
        public final int height;
        public final float heightFraction;
        public final int minHeight;
        public final int minWidth;
        public final int width;
        public final float widthFraction;
        public String windowLayoutAffinity;

        public WindowLayout(int width, float widthFraction, int height, float heightFraction, int gravity, int minWidth, int minHeight) {
            this(width, widthFraction, height, heightFraction, gravity, minWidth, minHeight, null);
        }

        public WindowLayout(int width, float widthFraction, int height, float heightFraction, int gravity, int minWidth, int minHeight, String windowLayoutAffinity) {
            this.width = width;
            this.widthFraction = widthFraction;
            this.height = height;
            this.heightFraction = heightFraction;
            this.gravity = gravity;
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            this.windowLayoutAffinity = windowLayoutAffinity;
        }

        public WindowLayout(Parcel source) {
            this.width = source.readInt();
            this.widthFraction = source.readFloat();
            this.height = source.readInt();
            this.heightFraction = source.readFloat();
            this.gravity = source.readInt();
            this.minWidth = source.readInt();
            this.minHeight = source.readInt();
            this.windowLayoutAffinity = source.readString8();
        }

        public boolean hasSpecifiedSize() {
            return this.width >= 0 || this.height >= 0 || this.widthFraction >= 0.0f || this.heightFraction >= 0.0f;
        }

        public void writeToParcel(Parcel dest) {
            dest.writeInt(this.width);
            dest.writeFloat(this.widthFraction);
            dest.writeInt(this.height);
            dest.writeFloat(this.heightFraction);
            dest.writeInt(this.gravity);
            dest.writeInt(this.minWidth);
            dest.writeInt(this.minHeight);
            dest.writeString8(this.windowLayoutAffinity);
        }
    }
}
