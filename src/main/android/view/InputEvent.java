package android.view;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes4.dex */
public abstract class InputEvent implements Parcelable {
    protected static final int PARCEL_TOKEN_KEY_EVENT = 2;
    protected static final int PARCEL_TOKEN_MOTION_EVENT = 1;
    private static final boolean TRACK_RECYCLED_LOCATION = false;
    protected boolean mRecycled;
    private RuntimeException mRecycledLocation;
    protected int mSeq = mNextSeq.getAndIncrement();
    private static final AtomicInteger mNextSeq = new AtomicInteger();
    public static final Parcelable.Creator<InputEvent> CREATOR = new Parcelable.Creator<InputEvent>() { // from class: android.view.InputEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputEvent createFromParcel(Parcel in) {
            int token = in.readInt();
            if (token == 2) {
                return KeyEvent.createFromParcelBody(in);
            }
            if (token == 1) {
                return MotionEvent.createFromParcelBody(in);
            }
            throw new IllegalStateException("Unexpected input event type token in parcel.");
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputEvent[] newArray(int size) {
            return new InputEvent[size];
        }
    };

    public abstract void cancel();

    public abstract InputEvent copy();

    public abstract int getDeviceId();

    public abstract int getDisplayId();

    public abstract long getEventTime();

    public abstract long getEventTimeNano();

    public abstract int getId();

    public abstract int getSource();

    public abstract boolean isTainted();

    public abstract void setDisplayId(int i);

    public abstract void setSource(int i);

    public abstract void setTainted(boolean z);

    public final InputDevice getDevice() {
        return InputDevice.getDevice(getDeviceId());
    }

    public boolean isFromSource(int source) {
        return (getSource() & source) == source;
    }

    public void recycle() {
        if (this.mRecycled) {
            throw new RuntimeException(toString() + " recycled twice!");
        }
        this.mRecycled = true;
    }

    public void recycleIfNeededAfterDispatch() {
        recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareForReuse() {
        this.mRecycled = false;
        this.mRecycledLocation = null;
        this.mSeq = mNextSeq.getAndIncrement();
    }

    public int getSequenceNumber() {
        return this.mSeq;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
