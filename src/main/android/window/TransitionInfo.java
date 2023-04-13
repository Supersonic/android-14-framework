package android.window;

import android.app.ActivityManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.security.keystore.KeyProperties;
import android.telecom.Logging.Session;
import android.util.NtpTrustedTime;
import android.view.SurfaceControl;
import android.view.WindowManager;
import com.android.internal.accessibility.common.ShortcutConstants;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public final class TransitionInfo implements Parcelable {
    public static final Parcelable.Creator<TransitionInfo> CREATOR = new Parcelable.Creator<TransitionInfo>() { // from class: android.window.TransitionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TransitionInfo createFromParcel(Parcel in) {
            return new TransitionInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TransitionInfo[] newArray(int size) {
            return new TransitionInfo[size];
        }
    };
    public static final int FLAGS_IS_NON_APP_WINDOW = 65794;
    public static final int FLAG_BACK_GESTURE_ANIMATED = 131072;
    public static final int FLAG_CROSS_PROFILE_OWNER_THUMBNAIL = 4096;
    public static final int FLAG_CROSS_PROFILE_WORK_THUMBNAIL = 8192;
    public static final int FLAG_DISPLAY_HAS_ALERT_WINDOWS = 128;
    public static final int FLAG_FILLS_TASK = 1024;
    public static final int FLAG_FIRST_CUSTOM = 524288;
    public static final int FLAG_IN_TASK_WITH_EMBEDDED_ACTIVITY = 512;
    public static final int FLAG_IS_BEHIND_STARTING_WINDOW = 16384;
    public static final int FLAG_IS_DISPLAY = 32;
    public static final int FLAG_IS_INPUT_METHOD = 256;
    public static final int FLAG_IS_OCCLUDED = 32768;
    public static final int FLAG_IS_SYSTEM_WINDOW = 65536;
    public static final int FLAG_IS_VOICE_INTERACTION = 16;
    public static final int FLAG_IS_WALLPAPER = 2;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_NO_ANIMATION = 262144;
    public static final int FLAG_OCCLUDES_KEYGUARD = 64;
    public static final int FLAG_SHOW_WALLPAPER = 1;
    public static final int FLAG_STARTING_WINDOW_TRANSFER_RECIPIENT = 8;
    public static final int FLAG_TRANSLUCENT = 4;
    public static final int FLAG_WILL_IME_SHOWN = 2048;
    private final ArrayList<Change> mChanges;
    private final int mFlags;
    private AnimationOptions mOptions;
    private SurfaceControl mRootLeash;
    private final Point mRootOffset;
    private final int mType;

    /* loaded from: classes4.dex */
    public @interface ChangeFlags {
    }

    /* loaded from: classes4.dex */
    public @interface TransitionMode {
    }

    public TransitionInfo(int type, int flags) {
        this.mChanges = new ArrayList<>();
        this.mRootOffset = new Point();
        this.mType = type;
        this.mFlags = flags;
    }

    private TransitionInfo(Parcel in) {
        ArrayList<Change> arrayList = new ArrayList<>();
        this.mChanges = arrayList;
        Point point = new Point();
        this.mRootOffset = point;
        this.mType = in.readInt();
        this.mFlags = in.readInt();
        in.readTypedList(arrayList, Change.CREATOR);
        SurfaceControl surfaceControl = new SurfaceControl();
        this.mRootLeash = surfaceControl;
        surfaceControl.readFromParcel(in);
        this.mRootLeash.setUnreleasedWarningCallSite("TransitionInfo");
        point.readFromParcel(in);
        this.mOptions = (AnimationOptions) in.readTypedObject(AnimationOptions.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeInt(this.mFlags);
        dest.writeTypedList(this.mChanges);
        this.mRootLeash.writeToParcel(dest, flags);
        this.mRootOffset.writeToParcel(dest, flags);
        dest.writeTypedObject(this.mOptions, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void setRootLeash(SurfaceControl leash, int offsetLeft, int offsetTop) {
        this.mRootLeash = leash;
        this.mRootOffset.set(offsetLeft, offsetTop);
    }

    public void setAnimationOptions(AnimationOptions options) {
        this.mOptions = options;
    }

    public int getType() {
        return this.mType;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public SurfaceControl getRootLeash() {
        SurfaceControl surfaceControl = this.mRootLeash;
        if (surfaceControl == null) {
            throw new IllegalStateException("Trying to get a leash which wasn't set");
        }
        return surfaceControl;
    }

    public Point getRootOffset() {
        return this.mRootOffset;
    }

    public AnimationOptions getAnimationOptions() {
        return this.mOptions;
    }

    public List<Change> getChanges() {
        return this.mChanges;
    }

    public Change getChange(WindowContainerToken token) {
        for (int i = this.mChanges.size() - 1; i >= 0; i--) {
            if (token.equals(this.mChanges.get(i).mContainer)) {
                return this.mChanges.get(i);
            }
        }
        return null;
    }

    public void addChange(Change change) {
        this.mChanges.add(change);
    }

    public boolean isKeyguardGoingAway() {
        return (this.mFlags & 256) != 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{t=" + WindowManager.transitTypeToString(this.mType) + " f=0x" + Integer.toHexString(this.mFlags) + " ro=" + this.mRootOffset + " c=[");
        for (int i = 0; i < this.mChanges.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(this.mChanges.get(i));
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String modeToString(int mode) {
        switch (mode) {
            case 0:
                return KeyProperties.DIGEST_NONE;
            case 1:
                return "OPEN";
            case 2:
                return "CLOSE";
            case 3:
                return "SHOW";
            case 4:
                return "HIDE";
            case 5:
            default:
                return "<unknown:" + mode + ">";
            case 6:
                return "CHANGE";
        }
    }

    public static String flagsToString(int flags) {
        if (flags == 0) {
            return KeyProperties.DIGEST_NONE;
        }
        StringBuilder sb = new StringBuilder();
        if ((flags & 1) != 0) {
            sb.append("SHOW_WALLPAPER");
        }
        if ((flags & 2) != 0) {
            sb.append("IS_WALLPAPER");
        }
        if ((flags & 256) != 0) {
            sb.append("IS_INPUT_METHOD");
        }
        if ((flags & 4) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("TRANSLUCENT");
        }
        if ((flags & 8) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("STARTING_WINDOW_TRANSFER");
        }
        if ((flags & 16) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("IS_VOICE_INTERACTION");
        }
        if ((flags & 32) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("IS_DISPLAY");
        }
        if ((flags & 64) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("OCCLUDES_KEYGUARD");
        }
        if ((flags & 128) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("DISPLAY_HAS_ALERT_WINDOWS");
        }
        if ((flags & 512) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("IN_TASK_WITH_EMBEDDED_ACTIVITY");
        }
        if ((flags & 1024) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("FILLS_TASK");
        }
        if ((flags & 16384) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("IS_BEHIND_STARTING_WINDOW");
        }
        if ((32768 & flags) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("IS_OCCLUDED");
        }
        if ((65536 & flags) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("FLAG_IS_SYSTEM_WINDOW");
        }
        if ((131072 & flags) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("FLAG_BACK_GESTURE_ANIMATED");
        }
        if ((262144 & flags) != 0) {
            sb.append(sb.length() == 0 ? "" : NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append("NO_ANIMATION");
        }
        if ((524288 & flags) != 0) {
            sb.append(sb.length() != 0 ? NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER : "").append("FIRST_CUSTOM");
        }
        return sb.toString();
    }

    public static boolean isIndependent(Change change, TransitionInfo info) {
        if (change.getParent() == null) {
            return true;
        }
        if (change.getMode() == 6) {
            return false;
        }
        Change parentChg = info.getChange(change.getParent());
        while (parentChg != null && parentChg.getMode() == 6) {
            if (parentChg.getParent() == null) {
                return true;
            }
            parentChg = info.getChange(parentChg.getParent());
        }
        return false;
    }

    public void releaseAnimSurfaces() {
        for (int i = this.mChanges.size() - 1; i >= 0; i--) {
            Change c = this.mChanges.get(i);
            if (c.mSnapshot != null) {
                c.mSnapshot.release();
                c.mSnapshot = null;
            }
        }
        SurfaceControl surfaceControl = this.mRootLeash;
        if (surfaceControl != null) {
            surfaceControl.release();
        }
    }

    public void releaseAllSurfaces() {
        releaseAnimSurfaces();
        for (int i = this.mChanges.size() - 1; i >= 0; i--) {
            this.mChanges.get(i).getLeash().release();
        }
    }

    public TransitionInfo localRemoteCopy() {
        TransitionInfo out = new TransitionInfo(this.mType, this.mFlags);
        for (int i = 0; i < this.mChanges.size(); i++) {
            out.mChanges.add(this.mChanges.get(i).localRemoteCopy());
        }
        out.mRootLeash = this.mRootLeash != null ? new SurfaceControl(this.mRootLeash, "localRemote") : null;
        out.mOptions = this.mOptions;
        out.mRootOffset.set(this.mRootOffset);
        return out;
    }

    /* loaded from: classes4.dex */
    public static final class Change implements Parcelable {
        public static final Parcelable.Creator<Change> CREATOR = new Parcelable.Creator<Change>() { // from class: android.window.TransitionInfo.Change.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Change createFromParcel(Parcel in) {
                return new Change(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Change[] newArray(int size) {
                return new Change[size];
            }
        };
        private boolean mAllowEnterPip;
        private int mBackgroundColor;
        private final WindowContainerToken mContainer;
        private final Rect mEndAbsBounds;
        private int mEndFixedRotation;
        private final Point mEndRelOffset;
        private int mEndRotation;
        private int mFlags;
        private WindowContainerToken mLastParent;
        private final SurfaceControl mLeash;
        private int mMode;
        private WindowContainerToken mParent;
        private int mRotationAnimation;
        private SurfaceControl mSnapshot;
        private float mSnapshotLuma;
        private final Rect mStartAbsBounds;
        private int mStartRotation;
        private ActivityManager.RunningTaskInfo mTaskInfo;

        public Change(WindowContainerToken container, SurfaceControl leash) {
            this.mMode = 0;
            this.mFlags = 0;
            this.mStartAbsBounds = new Rect();
            this.mEndAbsBounds = new Rect();
            this.mEndRelOffset = new Point();
            this.mTaskInfo = null;
            this.mStartRotation = -1;
            this.mEndRotation = -1;
            this.mEndFixedRotation = -1;
            this.mRotationAnimation = -1;
            this.mSnapshot = null;
            this.mContainer = container;
            this.mLeash = leash;
        }

        private Change(Parcel in) {
            this.mMode = 0;
            this.mFlags = 0;
            Rect rect = new Rect();
            this.mStartAbsBounds = rect;
            Rect rect2 = new Rect();
            this.mEndAbsBounds = rect2;
            Point point = new Point();
            this.mEndRelOffset = point;
            this.mTaskInfo = null;
            this.mStartRotation = -1;
            this.mEndRotation = -1;
            this.mEndFixedRotation = -1;
            this.mRotationAnimation = -1;
            this.mSnapshot = null;
            this.mContainer = (WindowContainerToken) in.readTypedObject(WindowContainerToken.CREATOR);
            this.mParent = (WindowContainerToken) in.readTypedObject(WindowContainerToken.CREATOR);
            this.mLastParent = (WindowContainerToken) in.readTypedObject(WindowContainerToken.CREATOR);
            SurfaceControl surfaceControl = new SurfaceControl();
            this.mLeash = surfaceControl;
            surfaceControl.readFromParcel(in);
            this.mMode = in.readInt();
            this.mFlags = in.readInt();
            rect.readFromParcel(in);
            rect2.readFromParcel(in);
            point.readFromParcel(in);
            this.mTaskInfo = (ActivityManager.RunningTaskInfo) in.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
            this.mAllowEnterPip = in.readBoolean();
            this.mStartRotation = in.readInt();
            this.mEndRotation = in.readInt();
            this.mEndFixedRotation = in.readInt();
            this.mRotationAnimation = in.readInt();
            this.mBackgroundColor = in.readInt();
            this.mSnapshot = (SurfaceControl) in.readTypedObject(SurfaceControl.CREATOR);
            this.mSnapshotLuma = in.readFloat();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Change localRemoteCopy() {
            Change out = new Change(this.mContainer, new SurfaceControl(this.mLeash, "localRemote"));
            out.mParent = this.mParent;
            out.mLastParent = this.mLastParent;
            out.mMode = this.mMode;
            out.mFlags = this.mFlags;
            out.mStartAbsBounds.set(this.mStartAbsBounds);
            out.mEndAbsBounds.set(this.mEndAbsBounds);
            out.mEndRelOffset.set(this.mEndRelOffset);
            out.mTaskInfo = this.mTaskInfo;
            out.mAllowEnterPip = this.mAllowEnterPip;
            out.mStartRotation = this.mStartRotation;
            out.mEndRotation = this.mEndRotation;
            out.mEndFixedRotation = this.mEndFixedRotation;
            out.mRotationAnimation = this.mRotationAnimation;
            out.mBackgroundColor = this.mBackgroundColor;
            out.mSnapshot = this.mSnapshot != null ? new SurfaceControl(this.mSnapshot, "localRemote") : null;
            out.mSnapshotLuma = this.mSnapshotLuma;
            return out;
        }

        public void setParent(WindowContainerToken parent) {
            this.mParent = parent;
        }

        public void setLastParent(WindowContainerToken lastParent) {
            this.mLastParent = lastParent;
        }

        public void setMode(int mode) {
            this.mMode = mode;
        }

        public void setFlags(int flags) {
            this.mFlags = flags;
        }

        public void setStartAbsBounds(Rect rect) {
            this.mStartAbsBounds.set(rect);
        }

        public void setEndAbsBounds(Rect rect) {
            this.mEndAbsBounds.set(rect);
        }

        public void setEndRelOffset(int left, int top) {
            this.mEndRelOffset.set(left, top);
        }

        public void setTaskInfo(ActivityManager.RunningTaskInfo taskInfo) {
            this.mTaskInfo = taskInfo;
        }

        public void setAllowEnterPip(boolean allowEnterPip) {
            this.mAllowEnterPip = allowEnterPip;
        }

        public void setRotation(int start, int end) {
            this.mStartRotation = start;
            this.mEndRotation = end;
        }

        public void setEndFixedRotation(int endFixedRotation) {
            this.mEndFixedRotation = endFixedRotation;
        }

        public void setRotationAnimation(int anim) {
            this.mRotationAnimation = anim;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.mBackgroundColor = backgroundColor;
        }

        public void setSnapshot(SurfaceControl snapshot, float luma) {
            this.mSnapshot = snapshot;
            this.mSnapshotLuma = luma;
        }

        public WindowContainerToken getContainer() {
            return this.mContainer;
        }

        public WindowContainerToken getParent() {
            return this.mParent;
        }

        public WindowContainerToken getLastParent() {
            return this.mLastParent;
        }

        public int getMode() {
            return this.mMode;
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean hasFlags(int flags) {
            return (this.mFlags & flags) != 0;
        }

        public boolean hasAllFlags(int flags) {
            return (this.mFlags & flags) == flags;
        }

        public Rect getStartAbsBounds() {
            return this.mStartAbsBounds;
        }

        public Rect getEndAbsBounds() {
            return this.mEndAbsBounds;
        }

        public Point getEndRelOffset() {
            return this.mEndRelOffset;
        }

        public SurfaceControl getLeash() {
            return this.mLeash;
        }

        public ActivityManager.RunningTaskInfo getTaskInfo() {
            return this.mTaskInfo;
        }

        public boolean getAllowEnterPip() {
            return this.mAllowEnterPip;
        }

        public int getStartRotation() {
            return this.mStartRotation;
        }

        public int getEndRotation() {
            return this.mEndRotation;
        }

        public int getEndFixedRotation() {
            return this.mEndFixedRotation;
        }

        public int getRotationAnimation() {
            return this.mRotationAnimation;
        }

        public int getBackgroundColor() {
            return this.mBackgroundColor;
        }

        public SurfaceControl getSnapshot() {
            return this.mSnapshot;
        }

        public float getSnapshotLuma() {
            return this.mSnapshotLuma;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedObject(this.mContainer, flags);
            dest.writeTypedObject(this.mParent, flags);
            dest.writeTypedObject(this.mLastParent, flags);
            this.mLeash.writeToParcel(dest, flags);
            dest.writeInt(this.mMode);
            dest.writeInt(this.mFlags);
            this.mStartAbsBounds.writeToParcel(dest, flags);
            this.mEndAbsBounds.writeToParcel(dest, flags);
            this.mEndRelOffset.writeToParcel(dest, flags);
            dest.writeTypedObject(this.mTaskInfo, flags);
            dest.writeBoolean(this.mAllowEnterPip);
            dest.writeInt(this.mStartRotation);
            dest.writeInt(this.mEndRotation);
            dest.writeInt(this.mEndFixedRotation);
            dest.writeInt(this.mRotationAnimation);
            dest.writeInt(this.mBackgroundColor);
            dest.writeTypedObject(this.mSnapshot, flags);
            dest.writeFloat(this.mSnapshotLuma);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            sb.append(this.mContainer);
            sb.append(" m=");
            sb.append(TransitionInfo.modeToString(this.mMode));
            sb.append(" f=");
            sb.append(TransitionInfo.flagsToString(this.mFlags));
            if (this.mParent != null) {
                sb.append(" p=");
                sb.append(this.mParent);
            }
            if (this.mLeash != null) {
                sb.append(" leash=");
                sb.append(this.mLeash);
            }
            sb.append(" sb=");
            sb.append(this.mStartAbsBounds);
            sb.append(" eb=");
            sb.append(this.mEndAbsBounds);
            if (this.mEndRelOffset.f76x != 0 || this.mEndRelOffset.f77y != 0) {
                sb.append(" eo=");
                sb.append(this.mEndRelOffset);
            }
            if (this.mStartRotation != this.mEndRotation) {
                sb.append(" r=");
                sb.append(this.mStartRotation);
                sb.append(Session.SUBSESSION_SEPARATION_CHAR);
                sb.append(this.mEndRotation);
                sb.append(ShortcutConstants.SERVICES_SEPARATOR);
                sb.append(this.mRotationAnimation);
            }
            if (this.mEndFixedRotation != -1) {
                sb.append(" endFixedRotation=");
                sb.append(this.mEndFixedRotation);
            }
            if (this.mSnapshot != null) {
                sb.append(" snapshot=");
                sb.append(this.mSnapshot);
            }
            if (this.mLastParent != null) {
                sb.append(" lastParent=");
                sb.append(this.mLastParent);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    /* loaded from: classes4.dex */
    public static final class AnimationOptions implements Parcelable {
        public static final Parcelable.Creator<AnimationOptions> CREATOR = new Parcelable.Creator<AnimationOptions>() { // from class: android.window.TransitionInfo.AnimationOptions.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AnimationOptions createFromParcel(Parcel in) {
                return new AnimationOptions(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AnimationOptions[] newArray(int size) {
                return new AnimationOptions[size];
            }
        };
        private int mAnimations;
        private int mBackgroundColor;
        private CustomActivityTransition mCustomActivityCloseTransition;
        private CustomActivityTransition mCustomActivityOpenTransition;
        private int mEnterResId;
        private int mExitResId;
        private boolean mOverrideTaskTransition;
        private String mPackageName;
        private HardwareBuffer mThumbnail;
        private final Rect mTransitionBounds;
        private int mType;

        private AnimationOptions(int type) {
            this.mTransitionBounds = new Rect();
            this.mType = type;
        }

        public AnimationOptions(Parcel in) {
            Rect rect = new Rect();
            this.mTransitionBounds = rect;
            this.mType = in.readInt();
            this.mEnterResId = in.readInt();
            this.mExitResId = in.readInt();
            this.mBackgroundColor = in.readInt();
            this.mOverrideTaskTransition = in.readBoolean();
            this.mPackageName = in.readString();
            rect.readFromParcel(in);
            this.mThumbnail = (HardwareBuffer) in.readTypedObject(HardwareBuffer.CREATOR);
            this.mAnimations = in.readInt();
            this.mCustomActivityOpenTransition = (CustomActivityTransition) in.readTypedObject(CustomActivityTransition.CREATOR);
            this.mCustomActivityCloseTransition = (CustomActivityTransition) in.readTypedObject(CustomActivityTransition.CREATOR);
        }

        public static AnimationOptions makeCommonAnimOptions(String packageName) {
            AnimationOptions options = new AnimationOptions(14);
            options.mPackageName = packageName;
            return options;
        }

        public static AnimationOptions makeAnimOptionsFromLayoutParameters(WindowManager.LayoutParams lp) {
            AnimationOptions options = new AnimationOptions(14);
            options.mPackageName = lp.packageName;
            options.mAnimations = lp.windowAnimations;
            return options;
        }

        public void addOptionsFromLayoutParameters(WindowManager.LayoutParams lp) {
            this.mAnimations = lp.windowAnimations;
        }

        public void addCustomActivityTransition(boolean isOpen, int enterResId, int exitResId, int backgroundColor) {
            CustomActivityTransition customTransition = isOpen ? this.mCustomActivityOpenTransition : this.mCustomActivityCloseTransition;
            if (customTransition == null) {
                customTransition = new CustomActivityTransition();
                if (isOpen) {
                    this.mCustomActivityOpenTransition = customTransition;
                } else {
                    this.mCustomActivityCloseTransition = customTransition;
                }
            }
            customTransition.addCustomActivityTransition(enterResId, exitResId, backgroundColor);
        }

        public static AnimationOptions makeCustomAnimOptions(String packageName, int enterResId, int exitResId, int backgroundColor, boolean overrideTaskTransition) {
            AnimationOptions options = new AnimationOptions(1);
            options.mPackageName = packageName;
            options.mEnterResId = enterResId;
            options.mExitResId = exitResId;
            options.mBackgroundColor = backgroundColor;
            options.mOverrideTaskTransition = overrideTaskTransition;
            return options;
        }

        public static AnimationOptions makeClipRevealAnimOptions(int startX, int startY, int width, int height) {
            AnimationOptions options = new AnimationOptions(11);
            options.mTransitionBounds.set(startX, startY, startX + width, startY + height);
            return options;
        }

        public static AnimationOptions makeScaleUpAnimOptions(int startX, int startY, int width, int height) {
            AnimationOptions options = new AnimationOptions(2);
            options.mTransitionBounds.set(startX, startY, startX + width, startY + height);
            return options;
        }

        public static AnimationOptions makeThumbnailAnimOptions(HardwareBuffer srcThumb, int startX, int startY, boolean scaleUp) {
            AnimationOptions options = new AnimationOptions(scaleUp ? 3 : 4);
            options.mTransitionBounds.set(startX, startY, startX, startY);
            options.mThumbnail = srcThumb;
            return options;
        }

        public static AnimationOptions makeCrossProfileAnimOptions() {
            AnimationOptions options = new AnimationOptions(12);
            return options;
        }

        public int getType() {
            return this.mType;
        }

        public int getEnterResId() {
            return this.mEnterResId;
        }

        public int getExitResId() {
            return this.mExitResId;
        }

        public int getBackgroundColor() {
            return this.mBackgroundColor;
        }

        public boolean getOverrideTaskTransition() {
            return this.mOverrideTaskTransition;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public Rect getTransitionBounds() {
            return this.mTransitionBounds;
        }

        public HardwareBuffer getThumbnail() {
            return this.mThumbnail;
        }

        public int getAnimations() {
            return this.mAnimations;
        }

        public CustomActivityTransition getCustomActivityTransition(boolean open) {
            return open ? this.mCustomActivityOpenTransition : this.mCustomActivityCloseTransition;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mType);
            dest.writeInt(this.mEnterResId);
            dest.writeInt(this.mExitResId);
            dest.writeInt(this.mBackgroundColor);
            dest.writeBoolean(this.mOverrideTaskTransition);
            dest.writeString(this.mPackageName);
            this.mTransitionBounds.writeToParcel(dest, flags);
            dest.writeTypedObject(this.mThumbnail, flags);
            dest.writeInt(this.mAnimations);
            dest.writeTypedObject(this.mCustomActivityOpenTransition, flags);
            dest.writeTypedObject(this.mCustomActivityCloseTransition, flags);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        private static String typeToString(int mode) {
            switch (mode) {
                case 1:
                    return "ANIM_CUSTOM";
                case 2:
                    return "ANIM_SCALE_UP";
                case 3:
                    return "ANIM_THUMBNAIL_SCALE_UP";
                case 4:
                    return "ANIM_THUMBNAIL_SCALE_DOWN";
                case 11:
                    return "ANIM_CLIP_REVEAL";
                case 12:
                    return "ANIM_OPEN_CROSS_PROFILE_APPS";
                default:
                    return "<unknown:" + mode + ">";
            }
        }

        public String toString() {
            return "{ AnimationOptions type= " + typeToString(this.mType) + " package=" + this.mPackageName + " override=" + this.mOverrideTaskTransition + " b=" + this.mTransitionBounds + "}";
        }

        /* loaded from: classes4.dex */
        public static class CustomActivityTransition implements Parcelable {
            public static final Parcelable.Creator<CustomActivityTransition> CREATOR = new Parcelable.Creator<CustomActivityTransition>() { // from class: android.window.TransitionInfo.AnimationOptions.CustomActivityTransition.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.p008os.Parcelable.Creator
                public CustomActivityTransition createFromParcel(Parcel in) {
                    return new CustomActivityTransition(in);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.p008os.Parcelable.Creator
                public CustomActivityTransition[] newArray(int size) {
                    return new CustomActivityTransition[size];
                }
            };
            private int mCustomBackgroundColor;
            private int mCustomEnterResId;
            private int mCustomExitResId;

            public int getCustomEnterResId() {
                return this.mCustomEnterResId;
            }

            public int getCustomExitResId() {
                return this.mCustomExitResId;
            }

            public int getCustomBackgroundColor() {
                return this.mCustomBackgroundColor;
            }

            CustomActivityTransition() {
            }

            CustomActivityTransition(Parcel in) {
                this.mCustomEnterResId = in.readInt();
                this.mCustomExitResId = in.readInt();
                this.mCustomBackgroundColor = in.readInt();
            }

            public void addCustomActivityTransition(int enterResId, int exitResId, int backgroundColor) {
                this.mCustomEnterResId = enterResId;
                this.mCustomExitResId = exitResId;
                this.mCustomBackgroundColor = backgroundColor;
            }

            @Override // android.p008os.Parcelable
            public int describeContents() {
                return 0;
            }

            @Override // android.p008os.Parcelable
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.mCustomEnterResId);
                dest.writeInt(this.mCustomExitResId);
                dest.writeInt(this.mCustomBackgroundColor);
            }
        }
    }
}
