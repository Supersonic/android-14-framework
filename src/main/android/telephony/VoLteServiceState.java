package android.telephony;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@Deprecated
/* loaded from: classes3.dex */
public final class VoLteServiceState implements Parcelable {
    public static final Parcelable.Creator<VoLteServiceState> CREATOR = new Parcelable.Creator() { // from class: android.telephony.VoLteServiceState.1
        @Override // android.p008os.Parcelable.Creator
        public VoLteServiceState createFromParcel(Parcel in) {
            return new VoLteServiceState(in);
        }

        @Override // android.p008os.Parcelable.Creator
        public VoLteServiceState[] newArray(int size) {
            return new VoLteServiceState[size];
        }
    };
    private static final boolean DBG = false;
    public static final int HANDOVER_CANCELED = 3;
    public static final int HANDOVER_COMPLETED = 1;
    public static final int HANDOVER_FAILED = 2;
    public static final int HANDOVER_STARTED = 0;
    public static final int INVALID = Integer.MAX_VALUE;
    private static final String LOG_TAG = "VoLteServiceState";
    public static final int NOT_SUPPORTED = 0;
    public static final int SUPPORTED = 1;
    private int mSrvccState;

    public static VoLteServiceState newFromBundle(Bundle m) {
        VoLteServiceState ret = new VoLteServiceState();
        ret.setFromNotifierBundle(m);
        return ret;
    }

    public VoLteServiceState() {
        initialize();
    }

    public VoLteServiceState(int srvccState) {
        initialize();
        this.mSrvccState = srvccState;
    }

    public VoLteServiceState(VoLteServiceState s) {
        copyFrom(s);
    }

    private void initialize() {
        this.mSrvccState = Integer.MAX_VALUE;
    }

    protected void copyFrom(VoLteServiceState s) {
        this.mSrvccState = s.mSrvccState;
    }

    public VoLteServiceState(Parcel in) {
        this.mSrvccState = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mSrvccState);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void validateInput() {
    }

    public int hashCode() {
        return this.mSrvccState * 31;
    }

    public boolean equals(Object o) {
        try {
            VoLteServiceState s = (VoLteServiceState) o;
            return o != null && this.mSrvccState == s.mSrvccState;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "VoLteServiceState: " + this.mSrvccState;
    }

    private void setFromNotifierBundle(Bundle m) {
        this.mSrvccState = m.getInt("mSrvccState");
    }

    public void fillInNotifierBundle(Bundle m) {
        m.putInt("mSrvccState", this.mSrvccState);
    }

    public int getSrvccState() {
        return this.mSrvccState;
    }

    private static void log(String s) {
        com.android.telephony.Rlog.m2w(LOG_TAG, s);
    }
}
