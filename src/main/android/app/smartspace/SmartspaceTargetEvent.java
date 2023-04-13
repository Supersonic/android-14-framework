package android.app.smartspace;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.format.DateFormat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes.dex */
public final class SmartspaceTargetEvent implements Parcelable {
    public static final Parcelable.Creator<SmartspaceTargetEvent> CREATOR = new Parcelable.Creator<SmartspaceTargetEvent>() { // from class: android.app.smartspace.SmartspaceTargetEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceTargetEvent createFromParcel(Parcel parcel) {
            return new SmartspaceTargetEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceTargetEvent[] newArray(int size) {
            return new SmartspaceTargetEvent[size];
        }
    };
    public static final int EVENT_TARGET_BLOCK = 5;
    public static final int EVENT_TARGET_DISMISS = 4;
    public static final int EVENT_TARGET_HIDDEN = 3;
    public static final int EVENT_TARGET_INTERACTION = 1;
    public static final int EVENT_TARGET_SHOWN = 2;
    public static final int EVENT_UI_SURFACE_HIDDEN = 7;
    public static final int EVENT_UI_SURFACE_SHOWN = 6;
    private final int mEventType;
    private final String mSmartspaceActionId;
    private final SmartspaceTarget mSmartspaceTarget;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface EventType {
    }

    private SmartspaceTargetEvent(SmartspaceTarget smartspaceTarget, String smartspaceActionId, int eventType) {
        this.mSmartspaceTarget = smartspaceTarget;
        this.mSmartspaceActionId = smartspaceActionId;
        this.mEventType = eventType;
    }

    private SmartspaceTargetEvent(Parcel parcel) {
        this.mSmartspaceTarget = (SmartspaceTarget) parcel.readParcelable(null, SmartspaceTarget.class);
        this.mSmartspaceActionId = parcel.readString();
        this.mEventType = parcel.readInt();
    }

    public SmartspaceTarget getSmartspaceTarget() {
        return this.mSmartspaceTarget;
    }

    public String getSmartspaceActionId() {
        return this.mSmartspaceActionId;
    }

    public int getEventType() {
        return this.mEventType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSmartspaceTarget, flags);
        dest.writeString(this.mSmartspaceActionId);
        dest.writeInt(this.mEventType);
    }

    public String toString() {
        return "SmartspaceTargetEvent{mSmartspaceTarget=" + this.mSmartspaceTarget + ", mSmartspaceActionId='" + this.mSmartspaceActionId + DateFormat.QUOTE + ", mEventType=" + this.mEventType + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private final int mEventType;
        private String mSmartspaceActionId;
        private SmartspaceTarget mSmartspaceTarget;

        public Builder(int eventType) {
            this.mEventType = eventType;
        }

        public Builder setSmartspaceTarget(SmartspaceTarget smartspaceTarget) {
            this.mSmartspaceTarget = smartspaceTarget;
            return this;
        }

        public Builder setSmartspaceActionId(String smartspaceActionId) {
            this.mSmartspaceActionId = smartspaceActionId;
            return this;
        }

        public SmartspaceTargetEvent build() {
            return new SmartspaceTargetEvent(this.mSmartspaceTarget, this.mSmartspaceActionId, this.mEventType);
        }
    }
}
