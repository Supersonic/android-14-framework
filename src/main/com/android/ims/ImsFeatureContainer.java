package com.android.ims;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.ims.ImsService;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.feature.ImsFeature;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class ImsFeatureContainer implements Parcelable {
    public static final Parcelable.Creator<ImsFeatureContainer> CREATOR = new Parcelable.Creator<ImsFeatureContainer>() { // from class: com.android.ims.ImsFeatureContainer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsFeatureContainer createFromParcel(Parcel source) {
            return new ImsFeatureContainer(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsFeatureContainer[] newArray(int size) {
            return new ImsFeatureContainer[size];
        }
    };
    public final IImsConfig imsConfig;
    public final IBinder imsFeature;
    public final IImsRegistration imsRegistration;
    private long mCapabilities;
    private int mState;
    public final ISipTransport sipTransport;

    public ImsFeatureContainer(IBinder iFace, IImsConfig iConfig, IImsRegistration iReg, ISipTransport transport, long initialCaps) {
        this.mState = 0;
        this.imsFeature = iFace;
        this.imsConfig = iConfig;
        this.imsRegistration = iReg;
        this.sipTransport = transport;
        this.mCapabilities = initialCaps;
    }

    private ImsFeatureContainer(Parcel in) {
        this.mState = 0;
        this.imsFeature = in.readStrongBinder();
        this.imsConfig = IImsConfig.Stub.asInterface(in.readStrongBinder());
        this.imsRegistration = IImsRegistration.Stub.asInterface(in.readStrongBinder());
        this.sipTransport = ISipTransport.Stub.asInterface(in.readStrongBinder());
        this.mState = in.readInt();
        this.mCapabilities = in.readLong();
    }

    public long getCapabilities() {
        return this.mCapabilities;
    }

    public void setCapabilities(long caps) {
        this.mCapabilities = caps;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImsFeatureContainer that = (ImsFeatureContainer) o;
        if (this.imsFeature.equals(that.imsFeature) && this.imsConfig.equals(that.imsConfig) && this.imsRegistration.equals(that.imsRegistration) && this.sipTransport.equals(that.sipTransport) && this.mState == that.getState() && this.mCapabilities == that.getCapabilities()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.imsFeature, this.imsConfig, this.imsRegistration, this.sipTransport, Integer.valueOf(this.mState), Long.valueOf(this.mCapabilities));
    }

    public String toString() {
        return "FeatureContainer{imsFeature=" + this.imsFeature + ", imsConfig=" + this.imsConfig + ", imsRegistration=" + this.imsRegistration + ", sipTransport=" + this.sipTransport + ", state=" + ImsFeature.STATE_LOG_MAP.get(Integer.valueOf(this.mState)) + ", capabilities = " + ImsService.getCapabilitiesString(this.mCapabilities) + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.imsFeature);
        dest.writeStrongInterface(this.imsConfig);
        dest.writeStrongInterface(this.imsRegistration);
        dest.writeStrongInterface(this.sipTransport);
        dest.writeInt(this.mState);
        dest.writeLong(this.mCapabilities);
    }
}
