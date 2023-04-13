package android.p008os.connectivity;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.p028os.PowerProfile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.os.connectivity.WifiActivityEnergyInfo */
/* loaded from: classes3.dex */
public final class WifiActivityEnergyInfo implements Parcelable {
    public static final Parcelable.Creator<WifiActivityEnergyInfo> CREATOR = new Parcelable.Creator<WifiActivityEnergyInfo>() { // from class: android.os.connectivity.WifiActivityEnergyInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiActivityEnergyInfo createFromParcel(Parcel in) {
            long timestamp = in.readLong();
            int stackState = in.readInt();
            long txTime = in.readLong();
            long rxTime = in.readLong();
            long scanTime = in.readLong();
            long idleTime = in.readLong();
            return new WifiActivityEnergyInfo(timestamp, stackState, txTime, rxTime, scanTime, idleTime);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WifiActivityEnergyInfo[] newArray(int size) {
            return new WifiActivityEnergyInfo[size];
        }
    };
    public static final int STACK_STATE_INVALID = 0;
    public static final int STACK_STATE_STATE_ACTIVE = 1;
    public static final int STACK_STATE_STATE_IDLE = 3;
    public static final int STACK_STATE_STATE_SCANNING = 2;
    private final long mControllerEnergyUsedMicroJoules;
    private final long mControllerIdleDurationMillis;
    private final long mControllerRxDurationMillis;
    private final long mControllerScanDurationMillis;
    private final long mControllerTxDurationMillis;
    private final int mStackState;
    private final long mTimeSinceBootMillis;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.connectivity.WifiActivityEnergyInfo$StackState */
    /* loaded from: classes3.dex */
    public @interface StackState {
    }

    public WifiActivityEnergyInfo(long timeSinceBootMillis, int stackState, long txDurationMillis, long rxDurationMillis, long scanDurationMillis, long idleDurationMillis) {
        this(timeSinceBootMillis, stackState, txDurationMillis, rxDurationMillis, scanDurationMillis, idleDurationMillis, calculateEnergyMicroJoules(txDurationMillis, rxDurationMillis, idleDurationMillis));
    }

    private static long calculateEnergyMicroJoules(long txDurationMillis, long rxDurationMillis, long idleDurationMillis) {
        Context context = ActivityThread.currentActivityThread().getSystemContext();
        if (context == null) {
            return 0L;
        }
        PowerProfile powerProfile = new PowerProfile(context);
        double idleCurrent = powerProfile.getAveragePower(PowerProfile.POWER_WIFI_CONTROLLER_IDLE);
        double rxCurrent = powerProfile.getAveragePower(PowerProfile.POWER_WIFI_CONTROLLER_RX);
        double txCurrent = powerProfile.getAveragePower(PowerProfile.POWER_WIFI_CONTROLLER_TX);
        double voltage = powerProfile.getAveragePower(PowerProfile.POWER_WIFI_CONTROLLER_OPERATING_VOLTAGE) / 1000.0d;
        double rxCurrent2 = idleDurationMillis;
        return (long) (((txDurationMillis * txCurrent) + (rxDurationMillis * rxCurrent) + (rxCurrent2 * idleCurrent)) * voltage);
    }

    public WifiActivityEnergyInfo(long timeSinceBootMillis, int stackState, long txDurationMillis, long rxDurationMillis, long scanDurationMillis, long idleDurationMillis, long energyUsedMicroJoules) {
        this.mTimeSinceBootMillis = timeSinceBootMillis;
        this.mStackState = stackState;
        this.mControllerTxDurationMillis = txDurationMillis;
        this.mControllerRxDurationMillis = rxDurationMillis;
        this.mControllerScanDurationMillis = scanDurationMillis;
        this.mControllerIdleDurationMillis = idleDurationMillis;
        this.mControllerEnergyUsedMicroJoules = energyUsedMicroJoules;
    }

    public String toString() {
        return "WifiActivityEnergyInfo{ mTimeSinceBootMillis=" + this.mTimeSinceBootMillis + " mStackState=" + this.mStackState + " mControllerTxDurationMillis=" + this.mControllerTxDurationMillis + " mControllerRxDurationMillis=" + this.mControllerRxDurationMillis + " mControllerScanDurationMillis=" + this.mControllerScanDurationMillis + " mControllerIdleDurationMillis=" + this.mControllerIdleDurationMillis + " mControllerEnergyUsedMicroJoules=" + this.mControllerEnergyUsedMicroJoules + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mTimeSinceBootMillis);
        out.writeInt(this.mStackState);
        out.writeLong(this.mControllerTxDurationMillis);
        out.writeLong(this.mControllerRxDurationMillis);
        out.writeLong(this.mControllerScanDurationMillis);
        out.writeLong(this.mControllerIdleDurationMillis);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public long getTimeSinceBootMillis() {
        return this.mTimeSinceBootMillis;
    }

    public int getStackState() {
        return this.mStackState;
    }

    public long getControllerTxDurationMillis() {
        return this.mControllerTxDurationMillis;
    }

    public long getControllerRxDurationMillis() {
        return this.mControllerRxDurationMillis;
    }

    public long getControllerScanDurationMillis() {
        return this.mControllerScanDurationMillis;
    }

    public long getControllerIdleDurationMillis() {
        return this.mControllerIdleDurationMillis;
    }

    public long getControllerEnergyUsedMicroJoules() {
        return this.mControllerEnergyUsedMicroJoules;
    }

    public boolean isValid() {
        return this.mControllerTxDurationMillis >= 0 && this.mControllerRxDurationMillis >= 0 && this.mControllerScanDurationMillis >= 0 && this.mControllerIdleDurationMillis >= 0;
    }
}
