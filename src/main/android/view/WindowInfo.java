package android.view;

import android.graphics.Matrix;
import android.graphics.Region;
import android.p008os.IBinder;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Pools;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class WindowInfo implements Parcelable {
    private static final int MAX_POOL_SIZE = 10;
    public IBinder activityToken;
    public List<IBinder> childTokens;
    public boolean focused;
    public boolean hasFlagWatchOutsideTouch;
    public boolean inPictureInPicture;
    public int layer;
    public IBinder parentToken;
    public CharSequence title;
    public IBinder token;
    public int type;
    private static final Pools.SynchronizedPool<WindowInfo> sPool = new Pools.SynchronizedPool<>(10);
    public static final Parcelable.Creator<WindowInfo> CREATOR = new Parcelable.Creator<WindowInfo>() { // from class: android.view.WindowInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WindowInfo createFromParcel(Parcel parcel) {
            WindowInfo window = WindowInfo.obtain();
            window.initFromParcel(parcel);
            return window;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WindowInfo[] newArray(int size) {
            return new WindowInfo[size];
        }
    };
    public Region regionInScreen = new Region();
    public long accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
    public int displayId = -1;
    public int taskId = -1;
    public float[] mTransformMatrix = new float[9];
    public MagnificationSpec mMagnificationSpec = new MagnificationSpec();
    public LocaleList locales = LocaleList.getEmptyLocaleList();

    private WindowInfo() {
    }

    public static WindowInfo obtain() {
        WindowInfo window = sPool.acquire();
        if (window == null) {
            return new WindowInfo();
        }
        return window;
    }

    public static WindowInfo obtain(WindowInfo other) {
        WindowInfo window = obtain();
        window.displayId = other.displayId;
        window.taskId = other.taskId;
        window.type = other.type;
        window.layer = other.layer;
        window.token = other.token;
        window.parentToken = other.parentToken;
        window.activityToken = other.activityToken;
        window.focused = other.focused;
        window.regionInScreen.set(other.regionInScreen);
        window.title = other.title;
        window.accessibilityIdOfAnchor = other.accessibilityIdOfAnchor;
        window.inPictureInPicture = other.inPictureInPicture;
        window.hasFlagWatchOutsideTouch = other.hasFlagWatchOutsideTouch;
        int i = 0;
        while (true) {
            float[] fArr = window.mTransformMatrix;
            if (i >= fArr.length) {
                break;
            }
            fArr[i] = other.mTransformMatrix[i];
            i++;
        }
        List<IBinder> list = other.childTokens;
        if (list != null && !list.isEmpty()) {
            List<IBinder> list2 = window.childTokens;
            if (list2 == null) {
                window.childTokens = new ArrayList(other.childTokens);
            } else {
                list2.addAll(other.childTokens);
            }
        }
        window.mMagnificationSpec.setTo(other.mMagnificationSpec);
        window.locales = other.locales;
        return window;
    }

    public void recycle() {
        clear();
        sPool.release(this);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.displayId);
        parcel.writeInt(this.taskId);
        parcel.writeInt(this.type);
        parcel.writeInt(this.layer);
        parcel.writeStrongBinder(this.token);
        parcel.writeStrongBinder(this.parentToken);
        parcel.writeStrongBinder(this.activityToken);
        parcel.writeInt(this.focused ? 1 : 0);
        this.regionInScreen.writeToParcel(parcel, flags);
        parcel.writeCharSequence(this.title);
        parcel.writeLong(this.accessibilityIdOfAnchor);
        parcel.writeInt(this.inPictureInPicture ? 1 : 0);
        parcel.writeInt(this.hasFlagWatchOutsideTouch ? 1 : 0);
        parcel.writeFloatArray(this.mTransformMatrix);
        List<IBinder> list = this.childTokens;
        if (list != null && !list.isEmpty()) {
            parcel.writeInt(1);
            parcel.writeBinderList(this.childTokens);
        } else {
            parcel.writeInt(0);
        }
        this.mMagnificationSpec.writeToParcel(parcel, flags);
        parcel.writeParcelable(this.locales, flags);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WindowInfo[");
        builder.append("title=").append(this.title);
        builder.append(", displayId=").append(this.displayId);
        builder.append(", taskId=").append(this.taskId);
        builder.append(", type=").append(this.type);
        builder.append(", layer=").append(this.layer);
        builder.append(", token=").append(this.token);
        builder.append(", region=").append(this.regionInScreen);
        builder.append(", bounds=").append(this.regionInScreen.getBounds());
        builder.append(", parent=").append(this.parentToken);
        builder.append(", focused=").append(this.focused);
        builder.append(", children=").append(this.childTokens);
        builder.append(", accessibility anchor=").append(this.accessibilityIdOfAnchor);
        builder.append(", pictureInPicture=").append(this.inPictureInPicture);
        builder.append(", watchOutsideTouch=").append(this.hasFlagWatchOutsideTouch);
        Matrix matrix = new Matrix();
        matrix.setValues(this.mTransformMatrix);
        builder.append(", mTransformMatrix=").append(matrix);
        builder.append(", mMagnificationSpec=").append(this.mMagnificationSpec);
        builder.append(", locales=").append(this.locales);
        builder.append(']');
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.displayId = parcel.readInt();
        this.taskId = parcel.readInt();
        this.type = parcel.readInt();
        this.layer = parcel.readInt();
        this.token = parcel.readStrongBinder();
        this.parentToken = parcel.readStrongBinder();
        this.activityToken = parcel.readStrongBinder();
        this.focused = parcel.readInt() == 1;
        this.regionInScreen = Region.CREATOR.createFromParcel(parcel);
        this.title = parcel.readCharSequence();
        this.accessibilityIdOfAnchor = parcel.readLong();
        this.inPictureInPicture = parcel.readInt() == 1;
        this.hasFlagWatchOutsideTouch = parcel.readInt() == 1;
        parcel.readFloatArray(this.mTransformMatrix);
        boolean hasChildren = parcel.readInt() == 1;
        if (hasChildren) {
            if (this.childTokens == null) {
                this.childTokens = new ArrayList();
            }
            parcel.readBinderList(this.childTokens);
        }
        this.mMagnificationSpec = MagnificationSpec.CREATOR.createFromParcel(parcel);
        this.locales = (LocaleList) parcel.readParcelable(null, LocaleList.class);
    }

    private void clear() {
        this.displayId = -1;
        this.taskId = -1;
        this.type = 0;
        this.layer = 0;
        this.token = null;
        this.parentToken = null;
        this.activityToken = null;
        this.focused = false;
        this.regionInScreen.setEmpty();
        List<IBinder> list = this.childTokens;
        if (list != null) {
            list.clear();
        }
        this.inPictureInPicture = false;
        this.hasFlagWatchOutsideTouch = false;
        int i = 0;
        while (true) {
            float[] fArr = this.mTransformMatrix;
            if (i < fArr.length) {
                fArr[i] = 0.0f;
                i++;
            } else {
                this.mMagnificationSpec.clear();
                this.title = null;
                this.accessibilityIdOfAnchor = AccessibilityNodeInfo.UNDEFINED_NODE_ID;
                this.locales = LocaleList.getEmptyLocaleList();
                return;
            }
        }
    }
}
