package android.hardware.biometrics;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SensorPropertiesInternal implements Parcelable {
    public static final Parcelable.Creator<SensorPropertiesInternal> CREATOR = new Parcelable.Creator<SensorPropertiesInternal>() { // from class: android.hardware.biometrics.SensorPropertiesInternal.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SensorPropertiesInternal createFromParcel(Parcel in) {
            return new SensorPropertiesInternal(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SensorPropertiesInternal[] newArray(int size) {
            return new SensorPropertiesInternal[size];
        }
    };
    public final List<ComponentInfoInternal> componentInfo;
    public final int maxEnrollmentsPerUser;
    public final boolean resetLockoutRequiresChallenge;
    public final boolean resetLockoutRequiresHardwareAuthToken;
    public final int sensorId;
    public final int sensorStrength;

    public static SensorPropertiesInternal from(SensorPropertiesInternal prop) {
        return new SensorPropertiesInternal(prop.sensorId, prop.sensorStrength, prop.maxEnrollmentsPerUser, prop.componentInfo, prop.resetLockoutRequiresHardwareAuthToken, prop.resetLockoutRequiresChallenge);
    }

    public SensorPropertiesInternal(int sensorId, int sensorStrength, int maxEnrollmentsPerUser, List<ComponentInfoInternal> componentInfo, boolean resetLockoutRequiresHardwareAuthToken, boolean resetLockoutRequiresChallenge) {
        this.sensorId = sensorId;
        this.sensorStrength = sensorStrength;
        this.maxEnrollmentsPerUser = maxEnrollmentsPerUser;
        this.componentInfo = componentInfo;
        this.resetLockoutRequiresHardwareAuthToken = resetLockoutRequiresHardwareAuthToken;
        this.resetLockoutRequiresChallenge = resetLockoutRequiresChallenge;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SensorPropertiesInternal(Parcel in) {
        this.sensorId = in.readInt();
        this.sensorStrength = in.readInt();
        this.maxEnrollmentsPerUser = in.readInt();
        ArrayList arrayList = new ArrayList();
        this.componentInfo = arrayList;
        in.readList(arrayList, ComponentInfoInternal.class.getClassLoader(), ComponentInfoInternal.class);
        this.resetLockoutRequiresHardwareAuthToken = in.readBoolean();
        this.resetLockoutRequiresChallenge = in.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sensorId);
        dest.writeInt(this.sensorStrength);
        dest.writeInt(this.maxEnrollmentsPerUser);
        dest.writeList(this.componentInfo);
        dest.writeBoolean(this.resetLockoutRequiresHardwareAuthToken);
        dest.writeBoolean(this.resetLockoutRequiresChallenge);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (ComponentInfoInternal info : this.componentInfo) {
            sb.append(NavigationBarInflaterView.SIZE_MOD_START).append(info.toString());
            sb.append("] ");
        }
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return "ID: " + this.sensorId + ", Strength: " + this.sensorStrength + ", MaxEnrollmentsPerUser: " + this.maxEnrollmentsPerUser + ", ComponentInfo: " + sb.toString();
    }
}
