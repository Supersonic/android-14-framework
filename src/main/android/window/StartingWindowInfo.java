package android.window;

import android.app.ActivityManager;
import android.content.p001pm.ActivityInfo;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.view.InsetsState;
import android.view.SurfaceControl;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.window.IWindowlessStartingSurfaceCallback;
/* loaded from: classes4.dex */
public final class StartingWindowInfo implements Parcelable {
    public static final Parcelable.Creator<StartingWindowInfo> CREATOR = new Parcelable.Creator<StartingWindowInfo>() { // from class: android.window.StartingWindowInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartingWindowInfo createFromParcel(Parcel source) {
            return new StartingWindowInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartingWindowInfo[] newArray(int size) {
            return new StartingWindowInfo[size];
        }
    };
    public static final int STARTING_WINDOW_TYPE_LEGACY_SPLASH_SCREEN = 4;
    public static final int STARTING_WINDOW_TYPE_NONE = 0;
    public static final int STARTING_WINDOW_TYPE_SNAPSHOT = 2;
    public static final int STARTING_WINDOW_TYPE_SOLID_COLOR_SPLASH_SCREEN = 3;
    public static final int STARTING_WINDOW_TYPE_SPLASH_SCREEN = 1;
    public static final int STARTING_WINDOW_TYPE_WINDOWLESS = 5;
    public static final int TYPE_PARAMETER_ACTIVITY_CREATED = 16;
    public static final int TYPE_PARAMETER_ACTIVITY_DRAWN = 64;
    public static final int TYPE_PARAMETER_ALLOW_HANDLE_SOLID_COLOR_SCREEN = 128;
    public static final int TYPE_PARAMETER_ALLOW_TASK_SNAPSHOT = 8;
    public static final int TYPE_PARAMETER_LEGACY_SPLASH_SCREEN = Integer.MIN_VALUE;
    public static final int TYPE_PARAMETER_NEW_TASK = 1;
    public static final int TYPE_PARAMETER_PROCESS_RUNNING = 4;
    public static final int TYPE_PARAMETER_TASK_SWITCH = 2;
    public static final int TYPE_PARAMETER_USE_SOLID_COLOR_SPLASH_SCREEN = 32;
    public static final int TYPE_PARAMETER_WINDOWLESS = 256;
    public IBinder appToken;
    public boolean isKeyguardOccluded;
    public WindowManager.LayoutParams mainWindowLayoutParams;
    public int requestedVisibleTypes;
    public SurfaceControl rootSurface;
    public int splashScreenThemeResId;
    public int startingWindowTypeParameter;
    public ActivityInfo targetActivityInfo;
    public ActivityManager.RunningTaskInfo taskInfo;
    public TaskSnapshot taskSnapshot;
    public InsetsState topOpaqueWindowInsetsState;
    public WindowManager.LayoutParams topOpaqueWindowLayoutParams;
    public IWindowlessStartingSurfaceCallback windowlessStartingSurfaceCallback;

    /* loaded from: classes4.dex */
    public @interface StartingTypeParams {
    }

    /* loaded from: classes4.dex */
    public @interface StartingWindowType {
    }

    public void notifyAddComplete(SurfaceControl addedSurface) {
        IWindowlessStartingSurfaceCallback iWindowlessStartingSurfaceCallback = this.windowlessStartingSurfaceCallback;
        if (iWindowlessStartingSurfaceCallback != null) {
            try {
                iWindowlessStartingSurfaceCallback.onSurfaceAdded(addedSurface);
            } catch (RemoteException e) {
            }
        }
    }

    public StartingWindowInfo() {
        this.isKeyguardOccluded = false;
        this.requestedVisibleTypes = WindowInsets.Type.defaultVisible();
    }

    private StartingWindowInfo(Parcel source) {
        this.isKeyguardOccluded = false;
        this.requestedVisibleTypes = WindowInsets.Type.defaultVisible();
        readFromParcel(source);
    }

    public boolean allowHandleSolidColorSplashScreen() {
        return (this.startingWindowTypeParameter & 128) != 0;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.taskInfo, flags);
        dest.writeTypedObject(this.targetActivityInfo, flags);
        dest.writeInt(this.startingWindowTypeParameter);
        dest.writeTypedObject(this.topOpaqueWindowInsetsState, flags);
        dest.writeTypedObject(this.topOpaqueWindowLayoutParams, flags);
        dest.writeTypedObject(this.mainWindowLayoutParams, flags);
        dest.writeInt(this.splashScreenThemeResId);
        dest.writeBoolean(this.isKeyguardOccluded);
        dest.writeTypedObject(this.taskSnapshot, flags);
        dest.writeInt(this.requestedVisibleTypes);
        dest.writeStrongBinder(this.appToken);
        dest.writeStrongInterface(this.windowlessStartingSurfaceCallback);
        dest.writeTypedObject(this.rootSurface, flags);
    }

    void readFromParcel(Parcel source) {
        this.taskInfo = (ActivityManager.RunningTaskInfo) source.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
        this.targetActivityInfo = (ActivityInfo) source.readTypedObject(ActivityInfo.CREATOR);
        this.startingWindowTypeParameter = source.readInt();
        this.topOpaqueWindowInsetsState = (InsetsState) source.readTypedObject(InsetsState.CREATOR);
        this.topOpaqueWindowLayoutParams = (WindowManager.LayoutParams) source.readTypedObject(WindowManager.LayoutParams.CREATOR);
        this.mainWindowLayoutParams = (WindowManager.LayoutParams) source.readTypedObject(WindowManager.LayoutParams.CREATOR);
        this.splashScreenThemeResId = source.readInt();
        this.isKeyguardOccluded = source.readBoolean();
        this.taskSnapshot = (TaskSnapshot) source.readTypedObject(TaskSnapshot.CREATOR);
        this.requestedVisibleTypes = source.readInt();
        this.appToken = source.readStrongBinder();
        this.windowlessStartingSurfaceCallback = IWindowlessStartingSurfaceCallback.Stub.asInterface(source.readStrongBinder());
        this.rootSurface = (SurfaceControl) source.readTypedObject(SurfaceControl.CREATOR);
    }

    public String toString() {
        return "StartingWindowInfo{taskId=" + this.taskInfo.taskId + " targetActivityInfo=" + this.targetActivityInfo + " displayId=" + this.taskInfo.displayId + " topActivityType=" + this.taskInfo.topActivityType + " preferredStartingWindowType=" + Integer.toHexString(this.startingWindowTypeParameter) + " insetsState=" + this.topOpaqueWindowInsetsState + " topWindowLayoutParams=" + this.topOpaqueWindowLayoutParams + " mainWindowLayoutParams=" + this.mainWindowLayoutParams + " splashScreenThemeResId " + Integer.toHexString(this.splashScreenThemeResId);
    }
}
