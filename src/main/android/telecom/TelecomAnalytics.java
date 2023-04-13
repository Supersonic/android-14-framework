package android.telecom;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public final class TelecomAnalytics implements Parcelable {
    public static final Parcelable.Creator<TelecomAnalytics> CREATOR = new Parcelable.Creator<TelecomAnalytics>() { // from class: android.telecom.TelecomAnalytics.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelecomAnalytics createFromParcel(Parcel in) {
            return new TelecomAnalytics(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelecomAnalytics[] newArray(int size) {
            return new TelecomAnalytics[size];
        }
    };
    private List<ParcelableCallAnalytics> mCallAnalytics;
    private List<SessionTiming> mSessionTimings;

    /* loaded from: classes3.dex */
    public static final class SessionTiming extends TimedEvent<Integer> implements Parcelable {
        public static final Parcelable.Creator<SessionTiming> CREATOR = new Parcelable.Creator<SessionTiming>() { // from class: android.telecom.TelecomAnalytics.SessionTiming.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SessionTiming createFromParcel(Parcel in) {
                return new SessionTiming(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SessionTiming[] newArray(int size) {
                return new SessionTiming[size];
            }
        };
        public static final int CSW_ADD_CONFERENCE_CALL = 108;
        public static final int CSW_HANDLE_CREATE_CONNECTION_COMPLETE = 100;
        public static final int CSW_REMOVE_CALL = 106;
        public static final int CSW_SET_ACTIVE = 101;
        public static final int CSW_SET_DIALING = 103;
        public static final int CSW_SET_DISCONNECTED = 104;
        public static final int CSW_SET_IS_CONFERENCED = 107;
        public static final int CSW_SET_ON_HOLD = 105;
        public static final int CSW_SET_RINGING = 102;
        public static final int ICA_ANSWER_CALL = 1;
        public static final int ICA_CONFERENCE = 8;
        public static final int ICA_DISCONNECT_CALL = 3;
        public static final int ICA_HOLD_CALL = 4;
        public static final int ICA_MUTE = 6;
        public static final int ICA_REJECT_CALL = 2;
        public static final int ICA_SET_AUDIO_ROUTE = 7;
        public static final int ICA_UNHOLD_CALL = 5;
        private int mId;
        private long mTime;

        public SessionTiming(int id, long time) {
            this.mId = id;
            this.mTime = time;
        }

        private SessionTiming(Parcel in) {
            this.mId = in.readInt();
            this.mTime = in.readLong();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.telecom.TimedEvent
        public Integer getKey() {
            return Integer.valueOf(this.mId);
        }

        @Override // android.telecom.TimedEvent
        public long getTime() {
            return this.mTime;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mId);
            out.writeLong(this.mTime);
        }
    }

    public TelecomAnalytics(List<SessionTiming> sessionTimings, List<ParcelableCallAnalytics> callAnalytics) {
        this.mSessionTimings = sessionTimings;
        this.mCallAnalytics = callAnalytics;
    }

    private TelecomAnalytics(Parcel in) {
        ArrayList arrayList = new ArrayList();
        this.mSessionTimings = arrayList;
        in.readTypedList(arrayList, SessionTiming.CREATOR);
        ArrayList arrayList2 = new ArrayList();
        this.mCallAnalytics = arrayList2;
        in.readTypedList(arrayList2, ParcelableCallAnalytics.CREATOR);
    }

    public List<SessionTiming> getSessionTimings() {
        return this.mSessionTimings;
    }

    public List<ParcelableCallAnalytics> getCallAnalytics() {
        return this.mCallAnalytics;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(this.mSessionTimings);
        out.writeTypedList(this.mCallAnalytics);
    }
}
