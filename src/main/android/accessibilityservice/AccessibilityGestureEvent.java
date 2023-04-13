package android.accessibilityservice;

import android.content.p001pm.ParceledListSlice;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.MotionEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class AccessibilityGestureEvent implements Parcelable {
    public static final Parcelable.Creator<AccessibilityGestureEvent> CREATOR = new Parcelable.Creator<AccessibilityGestureEvent>() { // from class: android.accessibilityservice.AccessibilityGestureEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessibilityGestureEvent createFromParcel(Parcel parcel) {
            return new AccessibilityGestureEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessibilityGestureEvent[] newArray(int size) {
            return new AccessibilityGestureEvent[size];
        }
    };
    private final int mDisplayId;
    private final int mGestureId;
    private List<MotionEvent> mMotionEvents;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface GestureId {
    }

    public AccessibilityGestureEvent(int gestureId, int displayId, List<MotionEvent> motionEvents) {
        ArrayList arrayList = new ArrayList();
        this.mMotionEvents = arrayList;
        this.mGestureId = gestureId;
        this.mDisplayId = displayId;
        arrayList.addAll(motionEvents);
    }

    public AccessibilityGestureEvent(int gestureId, int displayId) {
        this(gestureId, displayId, new ArrayList());
    }

    private AccessibilityGestureEvent(Parcel parcel) {
        this.mMotionEvents = new ArrayList();
        this.mGestureId = parcel.readInt();
        this.mDisplayId = parcel.readInt();
        ParceledListSlice<MotionEvent> slice = (ParceledListSlice) parcel.readParcelable(getClass().getClassLoader(), ParceledListSlice.class);
        this.mMotionEvents = slice.getList();
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public int getGestureId() {
        return this.mGestureId;
    }

    public List<MotionEvent> getMotionEvents() {
        return this.mMotionEvents;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("AccessibilityGestureEvent[");
        stringBuilder.append("gestureId: ").append(gestureIdToString(this.mGestureId));
        stringBuilder.append(", ");
        stringBuilder.append("displayId: ").append(this.mDisplayId);
        stringBuilder.append(", ");
        stringBuilder.append("Motion Events: [");
        for (int i = 0; i < this.mMotionEvents.size(); i++) {
            String action = MotionEvent.actionToString(this.mMotionEvents.get(i).getActionMasked());
            stringBuilder.append(action);
            if (i < this.mMotionEvents.size() - 1) {
                stringBuilder.append(", ");
            } else {
                stringBuilder.append(NavigationBarInflaterView.SIZE_MOD_END);
            }
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public static String gestureIdToString(int id) {
        switch (id) {
            case -2:
                return "GESTURE_TOUCH_EXPLORATION";
            case -1:
                return "GESTURE_PASSTHROUGH";
            case 0:
                return "GESTURE_UNKNOWN";
            case 1:
                return "GESTURE_SWIPE_UP";
            case 2:
                return "GESTURE_SWIPE_DOWN";
            case 3:
                return "GESTURE_SWIPE_LEFT";
            case 4:
                return "GESTURE_SWIPE_RIGHT";
            case 5:
                return "GESTURE_SWIPE_LEFT_AND_RIGHT";
            case 6:
                return "GESTURE_SWIPE_RIGHT_AND_LEFT";
            case 7:
                return "GESTURE_SWIPE_UP_AND_DOWN";
            case 8:
                return "GESTURE_SWIPE_DOWN_AND_UP";
            case 9:
                return "GESTURE_SWIPE_LEFT_AND_UP";
            case 10:
                return "GESTURE_SWIPE_LEFT_AND_DOWN";
            case 11:
                return "GESTURE_SWIPE_RIGHT_AND_UP";
            case 12:
                return "GESTURE_SWIPE_RIGHT_AND_DOWN";
            case 13:
                return "GESTURE_SWIPE_UP_AND_LEFT";
            case 14:
                return "GESTURE_SWIPE_UP_AND_RIGHT";
            case 15:
                return "GESTURE_SWIPE_DOWN_AND_LEFT";
            case 16:
                return "GESTURE_SWIPE_DOWN_AND_RIGHT";
            case 17:
                return "GESTURE_DOUBLE_TAP";
            case 18:
                return "GESTURE_DOUBLE_TAP_AND_HOLD";
            case 19:
                return "GESTURE_2_FINGER_SINGLE_TAP";
            case 20:
                return "GESTURE_2_FINGER_DOUBLE_TAP";
            case 21:
                return "GESTURE_2_FINGER_TRIPLE_TAP";
            case 22:
                return "GESTURE_3_FINGER_SINGLE_TAP";
            case 23:
                return "GESTURE_3_FINGER_DOUBLE_TAP";
            case 24:
                return "GESTURE_3_FINGER_TRIPLE_TAP";
            case 25:
                return "GESTURE_2_FINGER_SWIPE_UP";
            case 26:
                return "GESTURE_2_FINGER_SWIPE_DOWN";
            case 27:
                return "GESTURE_2_FINGER_SWIPE_LEFT";
            case 28:
                return "GESTURE_2_FINGER_SWIPE_RIGHT";
            case 29:
                return "GESTURE_3_FINGER_SWIPE_UP";
            case 30:
                return "GESTURE_3_FINGER_SWIPE_DOWN";
            case 31:
                return "GESTURE_3_FINGER_SWIPE_LEFT";
            case 32:
                return "GESTURE_3_FINGER_SWIPE_RIGHT";
            case 33:
                return "GESTURE_4_FINGER_SWIPE_UP";
            case 34:
                return "GESTURE_4_FINGER_SWIPE_DOWN";
            case 35:
                return "GESTURE_4_FINGER_SWIPE_LEFT";
            case 36:
                return "GESTURE_4_FINGER_SWIPE_RIGHT";
            case 37:
                return "GESTURE_4_FINGER_SINGLE_TAP";
            case 38:
                return "GESTURE_4_FINGER_DOUBLE_TAP";
            case 39:
                return "GESTURE_4_FINGER_TRIPLE_TAP";
            case 40:
                return "GESTURE_2_FINGER_DOUBLE_TAP_AND_HOLD";
            case 41:
                return "GESTURE_3_FINGER_DOUBLE_TAP_AND_HOLD";
            case 42:
                return "GESTURE_4_FINGER_DOUBLE_TAP_AND_HOLD";
            case 43:
                return "GESTURE_2_FINGER_TRIPLE_TAP_AND_HOLD";
            case 44:
                return "GESTURE_3_FINGER_SINGLE_TAP_AND_HOLD";
            case 45:
                return "GESTURE_3_FINGER_TRIPLE_TAP_AND_HOLD";
            default:
                return Integer.toHexString(id);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mGestureId);
        parcel.writeInt(this.mDisplayId);
        parcel.writeParcelable(new ParceledListSlice(this.mMotionEvents), 0);
    }
}
