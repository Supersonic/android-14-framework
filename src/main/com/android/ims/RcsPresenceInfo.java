package com.android.ims;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.ims.internal.Logger;
/* loaded from: classes.dex */
public class RcsPresenceInfo implements Parcelable {
    private static final String CONTACT_NUMBER = "contact_number";
    public static final Parcelable.Creator<RcsPresenceInfo> CREATOR = new Parcelable.Creator<RcsPresenceInfo>() { // from class: com.android.ims.RcsPresenceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RcsPresenceInfo createFromParcel(Parcel in) {
            return new RcsPresenceInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RcsPresenceInfo[] newArray(int size) {
            return new RcsPresenceInfo[size];
        }
    };
    public static final String VOLTE_STATUS = "volte_status";
    private Bundle mServiceInfo;

    /* loaded from: classes.dex */
    public static class ServiceInfoKey {
        public static final String SERVICE_CONTACT = "service_contact";
        public static final String SERVICE_TYPE = "service_type";
        public static final String STATE = "state";
        public static final String TIMESTAMP = "timestamp";
    }

    /* loaded from: classes.dex */
    public static class ServiceState {
        public static final int OFFLINE = 0;
        public static final int ONLINE = 1;
        public static final int UNKNOWN = -1;
    }

    /* loaded from: classes.dex */
    public interface ServiceType {
        public static final int VOLTE_CALL = 1;
        public static final int VT_CALL = 2;
    }

    /* loaded from: classes.dex */
    public static class VolteStatus {
        public static final int VOLTE_DISABLED = 0;
        public static final int VOLTE_ENABLED = 1;
        public static final int VOLTE_UNKNOWN = -1;
    }

    public String getContactNumber() {
        return this.mServiceInfo.getString(CONTACT_NUMBER);
    }

    public int getVolteStatus() {
        return this.mServiceInfo.getInt(VOLTE_STATUS);
    }

    public int getServiceState(int serviceType) {
        return getServiceInfo(serviceType, ServiceInfoKey.STATE, -1);
    }

    public String getServiceContact(int serviceType) {
        return getServiceInfo(serviceType, ServiceInfoKey.SERVICE_CONTACT, "");
    }

    public long getTimeStamp(int serviceType) {
        return getServiceInfo(serviceType, "timestamp", 0L);
    }

    public RcsPresenceInfo() {
        this.mServiceInfo = new Bundle();
    }

    public RcsPresenceInfo(Parcel source) {
        Bundle bundle = new Bundle();
        this.mServiceInfo = bundle;
        bundle.readFromParcel(source);
    }

    private Bundle getBundle() {
        return this.mServiceInfo;
    }

    public RcsPresenceInfo(String contactNumber, int volteStatus, int ipVoiceCallState, String ipVoiceCallServiceNumber, long ipVoiceCallTimestamp, int ipVideoCallState, String ipVideoCallServiceNumber, long ipVideoCallTimestamp) {
        Bundle bundle = new Bundle();
        this.mServiceInfo = bundle;
        bundle.putString(CONTACT_NUMBER, contactNumber);
        this.mServiceInfo.putInt(VOLTE_STATUS, volteStatus);
        set(1, ipVoiceCallState, ipVoiceCallServiceNumber, ipVoiceCallTimestamp);
        set(2, ipVideoCallState, ipVideoCallServiceNumber, ipVideoCallTimestamp);
    }

    private void set(int serviceType, int state, String serviceNumber, long timestamp) {
        Bundle capability = new Bundle();
        capability.putInt(ServiceInfoKey.SERVICE_TYPE, serviceType);
        capability.putInt(ServiceInfoKey.STATE, state);
        capability.putString(ServiceInfoKey.SERVICE_CONTACT, serviceNumber);
        capability.putLong("timestamp", timestamp);
        this.mServiceInfo.putBundle(String.valueOf(serviceType), capability);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mServiceInfo.writeToParcel(dest, flags);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private String getServiceInfo(int serviceType, String infoKey, String defaultValue) {
        Bundle serviceInfo = this.mServiceInfo.getBundle(String.valueOf(serviceType));
        if (serviceInfo != null) {
            return serviceInfo.getString(infoKey);
        }
        return defaultValue;
    }

    private long getServiceInfo(int serviceType, String infoKey, long defaultValue) {
        Bundle serviceInfo = this.mServiceInfo.getBundle(String.valueOf(serviceType));
        if (serviceInfo != null) {
            return serviceInfo.getLong(infoKey);
        }
        return defaultValue;
    }

    private int getServiceInfo(int serviceType, String infoType, int defaultValue) {
        Bundle serviceInfo = this.mServiceInfo.getBundle(String.valueOf(serviceType));
        if (serviceInfo != null) {
            return serviceInfo.getInt(infoType);
        }
        return defaultValue;
    }

    private Uri getServiceInfo(int serviceType, String infoKey, Uri defaultValue) {
        Bundle serviceInfo = this.mServiceInfo.getBundle(String.valueOf(serviceType));
        if (serviceInfo != null) {
            return (Uri) serviceInfo.getParcelable(infoKey);
        }
        return defaultValue;
    }

    public String toString() {
        return " contactNumber=" + Logger.hidePhoneNumberPii(getContactNumber()) + " volteStatus=" + getVolteStatus() + " ipVoiceCallSate=" + getServiceState(1) + " ipVoiceCallServiceNumber=" + Logger.hidePhoneNumberPii(getServiceContact(1)) + " ipVoiceCallTimestamp=" + getTimeStamp(1) + " ipVideoCallSate=" + getServiceState(2) + " ipVideoCallServiceNumber=" + Logger.hidePhoneNumberPii(getServiceContact(2)) + " ipVideoCallTimestamp=" + getTimeStamp(2);
    }
}
