package com.android.internal.telephony;

import android.hardware.radio.data.DataProfileInfo;
import android.hardware.radio.data.IRadioData;
import android.hardware.radio.data.KeepaliveRequest;
import android.net.KeepalivePacketData;
import android.net.LinkProperties;
import android.os.AsyncResult;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.Rlog;
import android.telephony.ServiceState;
import android.telephony.data.DataProfile;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.TrafficDescriptor;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class RadioDataProxy extends RadioServiceProxy {
    private volatile IRadioData mDataProxy = null;

    public HalVersion setAidl(HalVersion halVersion, IRadioData iRadioData) {
        try {
            halVersion = RIL.getServiceHalVersion(iRadioData.getInterfaceVersion());
        } catch (RemoteException e) {
            Rlog.e("RadioDataProxy", "setAidl: " + e);
        }
        this.mHalVersion = halVersion;
        this.mDataProxy = iRadioData;
        this.mIsAidl = true;
        Rlog.d("RadioDataProxy", "AIDL initialized mHalVersion=" + this.mHalVersion);
        return this.mHalVersion;
    }

    public IRadioData getAidl() {
        return this.mDataProxy;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void clear() {
        super.clear();
        this.mDataProxy = null;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public boolean isEmpty() {
        return this.mRadioProxy == null && this.mDataProxy == null;
    }

    public void allocatePduSessionId(int i) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.allocatePduSessionId(i);
        } else {
            this.mRadioProxy.allocatePduSessionId(i);
        }
    }

    public void cancelHandover(int i, int i2) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.cancelHandover(i, i2);
        } else {
            this.mRadioProxy.cancelHandover(i, i2);
        }
    }

    public void deactivateDataCall(int i, int i2, int i3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.deactivateDataCall(i, i2, i3);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_2)) {
            this.mRadioProxy.deactivateDataCall_1_2(i, i2, i3);
        } else {
            this.mRadioProxy.deactivateDataCall(i, i2, i3 == 2);
        }
    }

    public void getDataCallList(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.getDataCallList(i);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            this.mRadioProxy.getDataCallList_1_6(i);
        } else {
            this.mRadioProxy.getDataCallList(i);
        }
    }

    public void getSlicingConfig(int i) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.getSlicingConfig(i);
        } else {
            this.mRadioProxy.getSlicingConfig(i);
        }
    }

    public void releasePduSessionId(int i, int i2) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.releasePduSessionId(i, i2);
        } else {
            this.mRadioProxy.releasePduSessionId(i, i2);
        }
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void responseAcknowledgement() throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.responseAcknowledgement();
        } else {
            this.mRadioProxy.responseAcknowledgement();
        }
    }

    public void setDataAllowed(int i, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.setDataAllowed(i, z);
        } else {
            this.mRadioProxy.setDataAllowed(i, z);
        }
    }

    public void setDataProfile(int i, DataProfile[] dataProfileArr, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        int i2 = 0;
        if (isAidl()) {
            DataProfileInfo[] dataProfileInfoArr = new DataProfileInfo[dataProfileArr.length];
            while (i2 < dataProfileArr.length) {
                dataProfileInfoArr[i2] = RILUtils.convertToHalDataProfile(dataProfileArr[i2]);
                i2++;
            }
            this.mDataProxy.setDataProfile(i, dataProfileInfoArr);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_5)) {
            ArrayList arrayList = new ArrayList();
            int length = dataProfileArr.length;
            while (i2 < length) {
                arrayList.add(RILUtils.convertToHalDataProfile15(dataProfileArr[i2]));
                i2++;
            }
            this.mRadioProxy.setDataProfile_1_5(i, arrayList);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_4)) {
            ArrayList arrayList2 = new ArrayList();
            int length2 = dataProfileArr.length;
            while (i2 < length2) {
                arrayList2.add(RILUtils.convertToHalDataProfile14(dataProfileArr[i2]));
                i2++;
            }
            this.mRadioProxy.setDataProfile_1_4(i, arrayList2);
        } else {
            ArrayList arrayList3 = new ArrayList();
            int length3 = dataProfileArr.length;
            while (i2 < length3) {
                DataProfile dataProfile = dataProfileArr[i2];
                if (dataProfile.isPersistent()) {
                    arrayList3.add(RILUtils.convertToHalDataProfile10(dataProfile));
                }
                i2++;
            }
            if (arrayList3.isEmpty()) {
                return;
            }
            this.mRadioProxy.setDataProfile(i, arrayList3, z);
        }
    }

    public void setDataThrottling(int i, byte b, long j) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.setDataThrottling(i, b, j);
        } else {
            this.mRadioProxy.setDataThrottling(i, b, j);
        }
    }

    public void setInitialAttachApn(int i, DataProfile dataProfile, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.setInitialAttachApn(i, RILUtils.convertToHalDataProfile(dataProfile));
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_5)) {
            this.mRadioProxy.setInitialAttachApn_1_5(i, RILUtils.convertToHalDataProfile15(dataProfile));
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_4)) {
            this.mRadioProxy.setInitialAttachApn_1_4(i, RILUtils.convertToHalDataProfile14(dataProfile));
        } else {
            this.mRadioProxy.setInitialAttachApn(i, RILUtils.convertToHalDataProfile10(dataProfile), dataProfile.isPersistent(), z);
        }
    }

    public void setupDataCall(int i, int i2, int i3, DataProfile dataProfile, boolean z, boolean z2, int i4, LinkProperties linkProperties, int i5, NetworkSliceInfo networkSliceInfo, TrafficDescriptor trafficDescriptor, boolean z3) throws RemoteException {
        String[] strArr;
        ServiceState serviceState;
        if (isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int i6 = 0;
        if (linkProperties != null) {
            for (InetAddress inetAddress : linkProperties.getAddresses()) {
                arrayList.add(inetAddress.getHostAddress());
            }
            strArr = new String[linkProperties.getDnsServers().size()];
            for (int i7 = 0; i7 < linkProperties.getDnsServers().size(); i7++) {
                arrayList2.add(linkProperties.getDnsServers().get(i7).getHostAddress());
                strArr[i7] = linkProperties.getDnsServers().get(i7).getHostAddress();
            }
        } else {
            strArr = new String[0];
        }
        String[] strArr2 = strArr;
        if (isAidl()) {
            this.mDataProxy.setupDataCall(i, i3, RILUtils.convertToHalDataProfile(new DataProfile.Builder().setType(dataProfile.getType()).setPreferred(dataProfile.isPreferred()).setTrafficDescriptor(trafficDescriptor).setApnSetting(dataProfile.getApnSetting()).build()), z2, i4, RILUtils.convertToHalLinkProperties(linkProperties), strArr2, i5, RILUtils.convertToHalSliceInfoAidl(networkSliceInfo), z3);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            this.mRadioProxy.setupDataCall_1_6(i, i3, RILUtils.convertToHalDataProfile15(dataProfile), z2, i4, RILUtils.convertToHalLinkProperties15(linkProperties), arrayList2, i5, RILUtils.convertToHalSliceInfo(networkSliceInfo), RILUtils.convertToHalTrafficDescriptor(trafficDescriptor), z3);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_5)) {
            this.mRadioProxy.setupDataCall_1_5(i, i3, RILUtils.convertToHalDataProfile15(dataProfile), z2, i4, RILUtils.convertToHalLinkProperties15(linkProperties), arrayList2);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_4)) {
            this.mRadioProxy.setupDataCall_1_4(i, i3, RILUtils.convertToHalDataProfile14(dataProfile), z2, i4, arrayList, arrayList2);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_2)) {
            this.mRadioProxy.setupDataCall_1_2(i, i3, RILUtils.convertToHalDataProfile10(dataProfile), dataProfile.isPersistent(), z2, z, i4, arrayList, arrayList2);
        } else {
            Phone phone = PhoneFactory.getPhone(i2);
            if (phone != null && (serviceState = phone.getServiceState()) != null) {
                i6 = serviceState.getRilDataRadioTechnology();
            }
            this.mRadioProxy.setupDataCall(i, i6, RILUtils.convertToHalDataProfile10(dataProfile), dataProfile.isPersistent(), z2, z);
        }
    }

    public void startHandover(int i, int i2) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.startHandover(i, i2);
        } else {
            this.mRadioProxy.startHandover(i, i2);
        }
    }

    public void startKeepalive(int i, int i2, KeepalivePacketData keepalivePacketData, int i3, Message message) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_1)) {
            return;
        }
        if (isAidl()) {
            KeepaliveRequest keepaliveRequest = new KeepaliveRequest();
            keepaliveRequest.cid = i2;
            if (keepalivePacketData.getDstAddress() instanceof Inet4Address) {
                keepaliveRequest.type = 0;
            } else if (keepalivePacketData.getDstAddress() instanceof Inet6Address) {
                keepaliveRequest.type = 1;
            } else {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(44));
                message.sendToTarget();
                return;
            }
            InetAddress srcAddress = keepalivePacketData.getSrcAddress();
            InetAddress dstAddress = keepalivePacketData.getDstAddress();
            int length = srcAddress.getAddress().length;
            byte[] bArr = new byte[length];
            for (int i4 = 0; i4 < length; i4++) {
                bArr[i4] = srcAddress.getAddress()[i4];
            }
            keepaliveRequest.sourceAddress = bArr;
            keepaliveRequest.sourcePort = keepalivePacketData.getSrcPort();
            int length2 = dstAddress.getAddress().length;
            byte[] bArr2 = new byte[length2];
            for (int i5 = 0; i5 < length2; i5++) {
                bArr2[i5] = dstAddress.getAddress()[i5];
            }
            keepaliveRequest.destinationAddress = bArr2;
            keepaliveRequest.destinationPort = keepalivePacketData.getDstPort();
            keepaliveRequest.maxKeepaliveIntervalMillis = i3;
            this.mDataProxy.startKeepalive(i, keepaliveRequest);
            return;
        }
        android.hardware.radio.V1_1.KeepaliveRequest keepaliveRequest2 = new android.hardware.radio.V1_1.KeepaliveRequest();
        keepaliveRequest2.cid = i2;
        if (keepalivePacketData.getDstAddress() instanceof Inet4Address) {
            keepaliveRequest2.type = 0;
        } else if (keepalivePacketData.getDstAddress() instanceof Inet6Address) {
            keepaliveRequest2.type = 1;
        } else {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(44));
            message.sendToTarget();
            return;
        }
        InetAddress srcAddress2 = keepalivePacketData.getSrcAddress();
        InetAddress dstAddress2 = keepalivePacketData.getDstAddress();
        RILUtils.appendPrimitiveArrayToArrayList(srcAddress2.getAddress(), keepaliveRequest2.sourceAddress);
        keepaliveRequest2.sourcePort = keepalivePacketData.getSrcPort();
        RILUtils.appendPrimitiveArrayToArrayList(dstAddress2.getAddress(), keepaliveRequest2.destinationAddress);
        keepaliveRequest2.destinationPort = keepalivePacketData.getDstPort();
        keepaliveRequest2.maxKeepaliveIntervalMillis = i3;
        this.mRadioProxy.startKeepalive(i, keepaliveRequest2);
    }

    public void stopKeepalive(int i, int i2) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_1)) {
            return;
        }
        if (isAidl()) {
            this.mDataProxy.stopKeepalive(i, i2);
        } else {
            this.mRadioProxy.stopKeepalive(i, i2);
        }
    }
}
