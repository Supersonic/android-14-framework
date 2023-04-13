package android.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.view.IDragAndDropPermissions;
/* loaded from: classes4.dex */
public class DragEvent implements Parcelable {
    public static final int ACTION_DRAG_ENDED = 4;
    public static final int ACTION_DRAG_ENTERED = 5;
    public static final int ACTION_DRAG_EXITED = 6;
    public static final int ACTION_DRAG_LOCATION = 2;
    public static final int ACTION_DRAG_STARTED = 1;
    public static final int ACTION_DROP = 3;
    private static final int MAX_RECYCLED = 10;
    private static final boolean TRACK_RECYCLED_LOCATION = false;
    int mAction;
    ClipData mClipData;
    ClipDescription mClipDescription;
    IDragAndDropPermissions mDragAndDropPermissions;
    boolean mDragResult;
    private SurfaceControl mDragSurface;
    boolean mEventHandlerWasCalled;
    Object mLocalState;
    private DragEvent mNext;
    private float mOffsetX;
    private float mOffsetY;
    private boolean mRecycled;
    private RuntimeException mRecycledLocation;

    /* renamed from: mX */
    float f479mX;

    /* renamed from: mY */
    float f480mY;
    private static final Object gRecyclerLock = new Object();
    private static int gRecyclerUsed = 0;
    private static DragEvent gRecyclerTop = null;
    public static final Parcelable.Creator<DragEvent> CREATOR = new Parcelable.Creator<DragEvent>() { // from class: android.view.DragEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DragEvent createFromParcel(Parcel in) {
            DragEvent event = DragEvent.obtain();
            event.mAction = in.readInt();
            event.f479mX = in.readFloat();
            event.f480mY = in.readFloat();
            event.mOffsetX = in.readFloat();
            event.mOffsetY = in.readFloat();
            event.mDragResult = in.readInt() != 0;
            if (in.readInt() != 0) {
                event.mClipData = ClipData.CREATOR.createFromParcel(in);
            }
            if (in.readInt() != 0) {
                event.mClipDescription = ClipDescription.CREATOR.createFromParcel(in);
            }
            if (in.readInt() != 0) {
                event.mDragSurface = SurfaceControl.CREATOR.createFromParcel(in);
                event.mDragSurface.setUnreleasedWarningCallSite("DragEvent");
            }
            if (in.readInt() != 0) {
                event.mDragAndDropPermissions = IDragAndDropPermissions.Stub.asInterface(in.readStrongBinder());
            }
            return event;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DragEvent[] newArray(int size) {
            return new DragEvent[size];
        }
    };

    private DragEvent() {
    }

    private void init(int action, float x, float y, float offsetX, float offsetY, ClipDescription description, ClipData data, SurfaceControl dragSurface, IDragAndDropPermissions dragAndDropPermissions, Object localState, boolean result) {
        this.mAction = action;
        this.f479mX = x;
        this.f480mY = y;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;
        this.mClipDescription = description;
        this.mClipData = data;
        this.mDragSurface = dragSurface;
        this.mDragAndDropPermissions = dragAndDropPermissions;
        this.mLocalState = localState;
        this.mDragResult = result;
    }

    static DragEvent obtain() {
        return obtain(0, 0.0f, 0.0f, 0.0f, 0.0f, null, null, null, null, null, false);
    }

    public static DragEvent obtain(int action, float x, float y, float offsetX, float offsetY, Object localState, ClipDescription description, ClipData data, SurfaceControl dragSurface, IDragAndDropPermissions dragAndDropPermissions, boolean result) {
        synchronized (gRecyclerLock) {
            DragEvent ev = gRecyclerTop;
            if (ev == null) {
                DragEvent ev2 = new DragEvent();
                ev2.init(action, x, y, offsetX, offsetY, description, data, dragSurface, dragAndDropPermissions, localState, result);
                return ev2;
            }
            gRecyclerTop = ev.mNext;
            gRecyclerUsed--;
            ev.mRecycledLocation = null;
            ev.mRecycled = false;
            ev.mNext = null;
            ev.init(action, x, y, offsetX, offsetY, description, data, dragSurface, dragAndDropPermissions, localState, result);
            return ev;
        }
    }

    public static DragEvent obtain(DragEvent source) {
        return obtain(source.mAction, source.f479mX, source.f480mY, source.mOffsetX, source.mOffsetY, source.mLocalState, source.mClipDescription, source.mClipData, source.mDragSurface, source.mDragAndDropPermissions, source.mDragResult);
    }

    public int getAction() {
        return this.mAction;
    }

    public float getX() {
        return this.f479mX;
    }

    public float getY() {
        return this.f480mY;
    }

    public float getOffsetX() {
        return this.mOffsetX;
    }

    public float getOffsetY() {
        return this.mOffsetY;
    }

    public ClipData getClipData() {
        return this.mClipData;
    }

    public ClipDescription getClipDescription() {
        return this.mClipDescription;
    }

    public SurfaceControl getDragSurface() {
        return this.mDragSurface;
    }

    public IDragAndDropPermissions getDragAndDropPermissions() {
        return this.mDragAndDropPermissions;
    }

    public Object getLocalState() {
        return this.mLocalState;
    }

    public boolean getResult() {
        return this.mDragResult;
    }

    public final void recycle() {
        if (this.mRecycled) {
            throw new RuntimeException(toString() + " recycled twice!");
        }
        this.mRecycled = true;
        this.mClipData = null;
        this.mClipDescription = null;
        this.mLocalState = null;
        this.mEventHandlerWasCalled = false;
        synchronized (gRecyclerLock) {
            int i = gRecyclerUsed;
            if (i < 10) {
                gRecyclerUsed = i + 1;
                this.mNext = gRecyclerTop;
                gRecyclerTop = this;
            }
        }
    }

    public static String actionToString(int action) {
        switch (action) {
            case 1:
                return "ACTION_DRAG_STARTED";
            case 2:
                return "ACTION_DRAG_LOCATION";
            case 3:
                return "ACTION_DROP";
            case 4:
                return "ACTION_DRAG_ENDED";
            case 5:
                return "ACTION_DRAG_ENTERED";
            case 6:
                return "ACTION_DRAG_EXITED";
            default:
                return Integer.toString(action);
        }
    }

    public String toString() {
        return "DragEvent{" + Integer.toHexString(System.identityHashCode(this)) + " action=" + this.mAction + " @ (" + this.f479mX + ", " + this.f480mY + ") desc=" + this.mClipDescription + " data=" + this.mClipData + " local=" + this.mLocalState + " result=" + this.mDragResult + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAction);
        dest.writeFloat(this.f479mX);
        dest.writeFloat(this.f480mY);
        dest.writeFloat(this.mOffsetX);
        dest.writeFloat(this.mOffsetY);
        dest.writeInt(this.mDragResult ? 1 : 0);
        if (this.mClipData == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            this.mClipData.writeToParcel(dest, flags);
        }
        if (this.mClipDescription == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            this.mClipDescription.writeToParcel(dest, flags);
        }
        if (this.mDragSurface == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(1);
            this.mDragSurface.writeToParcel(dest, flags);
        }
        if (this.mDragAndDropPermissions == null) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(1);
        dest.writeStrongBinder(this.mDragAndDropPermissions.asBinder());
    }
}
