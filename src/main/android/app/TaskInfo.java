package android.app;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.LocusId;
import android.content.p001pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.window.WindowContainerToken;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public class TaskInfo {
    public static final int CAMERA_COMPAT_CONTROL_DISMISSED = 3;
    public static final int CAMERA_COMPAT_CONTROL_HIDDEN = 0;
    public static final int CAMERA_COMPAT_CONTROL_TREATMENT_APPLIED = 2;
    public static final int CAMERA_COMPAT_CONTROL_TREATMENT_SUGGESTED = 1;
    private static final String TAG = "TaskInfo";
    public ComponentName baseActivity;
    public Intent baseIntent;
    public int defaultMinSize;
    public Rect displayCutoutInsets;
    public int displayId;
    public boolean isFocused;
    public boolean isResizeable;
    public boolean isRunning;
    public boolean isSleeping;
    public boolean isVisible;
    public long lastActiveTime;
    public int launchIntoPipHostTaskId;
    public LocusId mTopActivityLocusId;
    public int minHeight;
    public int minWidth;
    public int numActivities;
    public ComponentName origActivity;
    public int parentTaskId;
    public PictureInPictureParams pictureInPictureParams;
    public Point positionInParent;
    public ComponentName realActivity;
    public int resizeMode;
    public boolean shouldDockBigOverlays;
    public boolean supportsMultiWindow;
    public ActivityManager.TaskDescription taskDescription;
    public int taskId;
    public WindowContainerToken token;
    public ComponentName topActivity;
    public boolean topActivityEligibleForLetterboxEducation;
    public boolean topActivityInSizeCompat;
    public ActivityInfo topActivityInfo;
    public int topActivityType;
    public int userId;
    public int displayAreaFeatureId = -1;
    public final Configuration configuration = new Configuration();
    public ArrayList<IBinder> launchCookies = new ArrayList<>();
    public int cameraCompatControlState = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CameraCompatControlState {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskInfo() {
    }

    private TaskInfo(Parcel source) {
        readFromParcel(source);
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public WindowContainerToken getToken() {
        return this.token;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public PictureInPictureParams getPictureInPictureParams() {
        return this.pictureInPictureParams;
    }

    public boolean shouldDockBigOverlays() {
        return this.shouldDockBigOverlays;
    }

    public int getWindowingMode() {
        return this.configuration.windowConfiguration.getWindowingMode();
    }

    public int getActivityType() {
        return this.configuration.windowConfiguration.getActivityType();
    }

    public void addLaunchCookie(IBinder cookie) {
        if (cookie == null || this.launchCookies.contains(cookie)) {
            return;
        }
        this.launchCookies.add(cookie);
    }

    public boolean hasCameraCompatControl() {
        int i = this.cameraCompatControlState;
        return (i == 0 || i == 3) ? false : true;
    }

    public boolean hasCompatUI() {
        return hasCameraCompatControl() || this.topActivityInSizeCompat || this.topActivityEligibleForLetterboxEducation;
    }

    public boolean containsLaunchCookie(IBinder cookie) {
        return this.launchCookies.contains(cookie);
    }

    public int getParentTaskId() {
        return this.parentTaskId;
    }

    public boolean hasParentTask() {
        return this.parentTaskId != -1;
    }

    public int getDisplayId() {
        return this.displayId;
    }

    public boolean equalsForTaskOrganizer(TaskInfo that) {
        return that != null && this.topActivityType == that.topActivityType && this.isResizeable == that.isResizeable && this.supportsMultiWindow == that.supportsMultiWindow && this.displayAreaFeatureId == that.displayAreaFeatureId && Objects.equals(this.positionInParent, that.positionInParent) && Objects.equals(this.pictureInPictureParams, that.pictureInPictureParams) && Objects.equals(Boolean.valueOf(this.shouldDockBigOverlays), Boolean.valueOf(that.shouldDockBigOverlays)) && Objects.equals(this.displayCutoutInsets, that.displayCutoutInsets) && getWindowingMode() == that.getWindowingMode() && this.configuration.uiMode == that.configuration.uiMode && Objects.equals(this.taskDescription, that.taskDescription) && this.isFocused == that.isFocused && this.isVisible == that.isVisible && this.isSleeping == that.isSleeping && Objects.equals(this.mTopActivityLocusId, that.mTopActivityLocusId) && this.parentTaskId == that.parentTaskId && Objects.equals(this.topActivity, that.topActivity);
    }

    public boolean equalsForCompatUi(TaskInfo that) {
        if (that == null || this.displayId != that.displayId || this.taskId != that.taskId || this.topActivityInSizeCompat != that.topActivityInSizeCompat || this.topActivityEligibleForLetterboxEducation != that.topActivityEligibleForLetterboxEducation || this.cameraCompatControlState != that.cameraCompatControlState) {
            return false;
        }
        if (hasCompatUI() && !this.configuration.windowConfiguration.getBounds().equals(that.configuration.windowConfiguration.getBounds())) {
            return false;
        }
        if (hasCompatUI() && this.configuration.getLayoutDirection() != that.configuration.getLayoutDirection()) {
            return false;
        }
        if (!hasCompatUI() || this.configuration.uiMode == that.configuration.uiMode) {
            return !hasCompatUI() || this.isVisible == that.isVisible;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readFromParcel(Parcel source) {
        this.userId = source.readInt();
        this.taskId = source.readInt();
        this.displayId = source.readInt();
        this.isRunning = source.readBoolean();
        this.baseIntent = (Intent) source.readTypedObject(Intent.CREATOR);
        this.baseActivity = ComponentName.readFromParcel(source);
        this.topActivity = ComponentName.readFromParcel(source);
        this.origActivity = ComponentName.readFromParcel(source);
        this.realActivity = ComponentName.readFromParcel(source);
        this.numActivities = source.readInt();
        this.lastActiveTime = source.readLong();
        this.taskDescription = (ActivityManager.TaskDescription) source.readTypedObject(ActivityManager.TaskDescription.CREATOR);
        this.supportsMultiWindow = source.readBoolean();
        this.resizeMode = source.readInt();
        this.configuration.readFromParcel(source);
        this.token = WindowContainerToken.CREATOR.createFromParcel(source);
        this.topActivityType = source.readInt();
        this.pictureInPictureParams = (PictureInPictureParams) source.readTypedObject(PictureInPictureParams.CREATOR);
        this.shouldDockBigOverlays = source.readBoolean();
        this.launchIntoPipHostTaskId = source.readInt();
        this.displayCutoutInsets = (Rect) source.readTypedObject(Rect.CREATOR);
        this.topActivityInfo = (ActivityInfo) source.readTypedObject(ActivityInfo.CREATOR);
        this.isResizeable = source.readBoolean();
        this.minWidth = source.readInt();
        this.minHeight = source.readInt();
        this.defaultMinSize = source.readInt();
        source.readBinderList(this.launchCookies);
        this.positionInParent = (Point) source.readTypedObject(Point.CREATOR);
        this.parentTaskId = source.readInt();
        this.isFocused = source.readBoolean();
        this.isVisible = source.readBoolean();
        this.isSleeping = source.readBoolean();
        this.topActivityInSizeCompat = source.readBoolean();
        this.topActivityEligibleForLetterboxEducation = source.readBoolean();
        this.mTopActivityLocusId = (LocusId) source.readTypedObject(LocusId.CREATOR);
        this.displayAreaFeatureId = source.readInt();
        this.cameraCompatControlState = source.readInt();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeInt(this.taskId);
        dest.writeInt(this.displayId);
        dest.writeBoolean(this.isRunning);
        dest.writeTypedObject(this.baseIntent, 0);
        ComponentName.writeToParcel(this.baseActivity, dest);
        ComponentName.writeToParcel(this.topActivity, dest);
        ComponentName.writeToParcel(this.origActivity, dest);
        ComponentName.writeToParcel(this.realActivity, dest);
        dest.writeInt(this.numActivities);
        dest.writeLong(this.lastActiveTime);
        dest.writeTypedObject(this.taskDescription, flags);
        dest.writeBoolean(this.supportsMultiWindow);
        dest.writeInt(this.resizeMode);
        this.configuration.writeToParcel(dest, flags);
        this.token.writeToParcel(dest, flags);
        dest.writeInt(this.topActivityType);
        dest.writeTypedObject(this.pictureInPictureParams, flags);
        dest.writeBoolean(this.shouldDockBigOverlays);
        dest.writeInt(this.launchIntoPipHostTaskId);
        dest.writeTypedObject(this.displayCutoutInsets, flags);
        dest.writeTypedObject(this.topActivityInfo, flags);
        dest.writeBoolean(this.isResizeable);
        dest.writeInt(this.minWidth);
        dest.writeInt(this.minHeight);
        dest.writeInt(this.defaultMinSize);
        dest.writeBinderList(this.launchCookies);
        dest.writeTypedObject(this.positionInParent, flags);
        dest.writeInt(this.parentTaskId);
        dest.writeBoolean(this.isFocused);
        dest.writeBoolean(this.isVisible);
        dest.writeBoolean(this.isSleeping);
        dest.writeBoolean(this.topActivityInSizeCompat);
        dest.writeBoolean(this.topActivityEligibleForLetterboxEducation);
        dest.writeTypedObject(this.mTopActivityLocusId, flags);
        dest.writeInt(this.displayAreaFeatureId);
        dest.writeInt(this.cameraCompatControlState);
    }

    public String toString() {
        return "TaskInfo{userId=" + this.userId + " taskId=" + this.taskId + " displayId=" + this.displayId + " isRunning=" + this.isRunning + " baseIntent=" + this.baseIntent + " baseActivity=" + this.baseActivity + " topActivity=" + this.topActivity + " origActivity=" + this.origActivity + " realActivity=" + this.realActivity + " numActivities=" + this.numActivities + " lastActiveTime=" + this.lastActiveTime + " supportsMultiWindow=" + this.supportsMultiWindow + " resizeMode=" + this.resizeMode + " isResizeable=" + this.isResizeable + " minWidth=" + this.minWidth + " minHeight=" + this.minHeight + " defaultMinSize=" + this.defaultMinSize + " token=" + this.token + " topActivityType=" + this.topActivityType + " pictureInPictureParams=" + this.pictureInPictureParams + " shouldDockBigOverlays=" + this.shouldDockBigOverlays + " launchIntoPipHostTaskId=" + this.launchIntoPipHostTaskId + " displayCutoutSafeInsets=" + this.displayCutoutInsets + " topActivityInfo=" + this.topActivityInfo + " launchCookies=" + this.launchCookies + " positionInParent=" + this.positionInParent + " parentTaskId=" + this.parentTaskId + " isFocused=" + this.isFocused + " isVisible=" + this.isVisible + " isSleeping=" + this.isSleeping + " topActivityInSizeCompat=" + this.topActivityInSizeCompat + " topActivityEligibleForLetterboxEducation= " + this.topActivityEligibleForLetterboxEducation + " locusId=" + this.mTopActivityLocusId + " displayAreaFeatureId=" + this.displayAreaFeatureId + " cameraCompatControlState=" + cameraCompatControlStateToString(this.cameraCompatControlState) + "}";
    }

    public static String cameraCompatControlStateToString(int cameraCompatControlState) {
        switch (cameraCompatControlState) {
            case 0:
                return "hidden";
            case 1:
                return "treatment-suggested";
            case 2:
                return "treatment-applied";
            case 3:
                return "dismissed";
            default:
                throw new AssertionError("Unexpected camera compat control state: " + cameraCompatControlState);
        }
    }
}
