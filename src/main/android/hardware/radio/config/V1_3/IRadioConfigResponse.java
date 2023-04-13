package android.hardware.radio.config.V1_3;

import android.hardware.radio.V1_6.RadioResponseInfo;
import android.hardware.radio.config.V1_0.SimSlotStatus;
import android.hardware.radio.config.V1_1.ModemsConfig;
import android.hardware.radio.config.V1_1.PhoneCapability;
import android.internal.hidl.base.V1_0.DebugInfo;
import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwBlob;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwInterface;
import android.os.NativeHandle;
import android.os.RemoteException;
import com.android.internal.telephony.uicc.AnswerToReset;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public interface IRadioConfigResponse extends android.hardware.radio.config.V1_2.IRadioConfigResponse {
    void getHalDeviceCapabilitiesResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IRadioConfigResponse {
        @Override // android.hardware.radio.config.V1_0.IRadioConfigResponse
        public IHwBinder asBinder() {
            return this;
        }

        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        public final String interfaceDescriptor() {
            return "android.hardware.radio.config@1.3::IRadioConfigResponse";
        }

        public final boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) {
            return true;
        }

        public final void ping() {
        }

        public final void setHALInstrumentation() {
        }

        public final boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) {
            return true;
        }

        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.radio.config@1.3::IRadioConfigResponse", "android.hardware.radio.config@1.2::IRadioConfigResponse", "android.hardware.radio.config@1.1::IRadioConfigResponse", "android.hardware.radio.config@1.0::IRadioConfigResponse", "android.hidl.base@1.0::IBase"));
        }

        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-55, -83, 24, 114, -110, 104, 89, 61, 20, 104, 29, -120, -1, -83, 28, -105, -25, 7, 68, 74, 69, -31, -76, -19, Byte.MIN_VALUE, 77, -85, -108, -98, -37, -40, 79}, new byte[]{-40, -25, 113, 126, -127, -121, -35, 116, 83, -44, 20, 47, -113, 51, 30, 124, 50, 94, 122, 111, -98, -115, 68, -84, 13, 82, -77, -66, 80, 43, -2, -125}, new byte[]{-76, 46, -77, -69, -43, -25, -75, 25, -30, -125, 98, 52, 12, 34, 5, -86, 117, 53, 109, -26, -77, 15, 79, -48, -98, -62, -22, 120, 79, 37, 10, -80}, new byte[]{-94, -23, -73, -86, 9, -9, -108, 38, -9, 101, -125, -127, 116, -32, 75, 111, -102, 62, 108, -117, 118, -71, 35, -4, 23, 5, 99, 34, 7, -70, -44, 75}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, AnswerToReset.DIRECT_CONVENTION, 24, -54, 76}));
        }

        public final DebugInfo getDebugInfo() {
            DebugInfo debugInfo = new DebugInfo();
            debugInfo.pid = HidlSupport.getPidIfSharable();
            debugInfo.ptr = 0L;
            debugInfo.arch = 0;
            return debugInfo;
        }

        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        public IHwInterface queryLocalInterface(String str) {
            if ("android.hardware.radio.config@1.3::IRadioConfigResponse".equals(str)) {
                return this;
            }
            return null;
        }

        public void registerAsService(String str) throws RemoteException {
            registerService(str);
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        public void onTransact(int i, HwParcel hwParcel, HwParcel hwParcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.0::IRadioConfigResponse");
                    android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo = new android.hardware.radio.V1_0.RadioResponseInfo();
                    radioResponseInfo.readFromParcel(hwParcel);
                    getSimSlotsStatusResponse(radioResponseInfo, SimSlotStatus.readVectorFromParcel(hwParcel));
                    return;
                case 2:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.0::IRadioConfigResponse");
                    android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo2 = new android.hardware.radio.V1_0.RadioResponseInfo();
                    radioResponseInfo2.readFromParcel(hwParcel);
                    setSimSlotsMappingResponse(radioResponseInfo2);
                    return;
                case 3:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.1::IRadioConfigResponse");
                    android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo3 = new android.hardware.radio.V1_0.RadioResponseInfo();
                    radioResponseInfo3.readFromParcel(hwParcel);
                    PhoneCapability phoneCapability = new PhoneCapability();
                    phoneCapability.readFromParcel(hwParcel);
                    getPhoneCapabilityResponse(radioResponseInfo3, phoneCapability);
                    return;
                case 4:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.1::IRadioConfigResponse");
                    android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo4 = new android.hardware.radio.V1_0.RadioResponseInfo();
                    radioResponseInfo4.readFromParcel(hwParcel);
                    setPreferredDataModemResponse(radioResponseInfo4);
                    return;
                case 5:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.1::IRadioConfigResponse");
                    android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo5 = new android.hardware.radio.V1_0.RadioResponseInfo();
                    radioResponseInfo5.readFromParcel(hwParcel);
                    setModemsConfigResponse(radioResponseInfo5);
                    return;
                case 6:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.1::IRadioConfigResponse");
                    android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo6 = new android.hardware.radio.V1_0.RadioResponseInfo();
                    radioResponseInfo6.readFromParcel(hwParcel);
                    ModemsConfig modemsConfig = new ModemsConfig();
                    modemsConfig.readFromParcel(hwParcel);
                    getModemsConfigResponse(radioResponseInfo6, modemsConfig);
                    return;
                case 7:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.2::IRadioConfigResponse");
                    android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo7 = new android.hardware.radio.V1_0.RadioResponseInfo();
                    radioResponseInfo7.readFromParcel(hwParcel);
                    getSimSlotsStatusResponse_1_2(radioResponseInfo7, android.hardware.radio.config.V1_2.SimSlotStatus.readVectorFromParcel(hwParcel));
                    return;
                case 8:
                    hwParcel.enforceInterface("android.hardware.radio.config@1.3::IRadioConfigResponse");
                    RadioResponseInfo radioResponseInfo8 = new RadioResponseInfo();
                    radioResponseInfo8.readFromParcel(hwParcel);
                    getHalDeviceCapabilitiesResponse(radioResponseInfo8, hwParcel.readBool());
                    return;
                default:
                    switch (i) {
                        case 256067662:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            ArrayList<String> interfaceChain = interfaceChain();
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeStringVector(interfaceChain);
                            hwParcel2.send();
                            return;
                        case 256131655:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            debug(hwParcel.readNativeHandle(), hwParcel.readStringVector());
                            hwParcel2.writeStatus(0);
                            hwParcel2.send();
                            return;
                        case 256136003:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            String interfaceDescriptor = interfaceDescriptor();
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeString(interfaceDescriptor);
                            hwParcel2.send();
                            return;
                        case 256398152:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            ArrayList<byte[]> hashChain = getHashChain();
                            hwParcel2.writeStatus(0);
                            HwBlob hwBlob = new HwBlob(16);
                            int size = hashChain.size();
                            hwBlob.putInt32(8L, size);
                            hwBlob.putBool(12L, false);
                            HwBlob hwBlob2 = new HwBlob(size * 32);
                            for (int i3 = 0; i3 < size; i3++) {
                                long j = i3 * 32;
                                byte[] bArr = hashChain.get(i3);
                                if (bArr == null || bArr.length != 32) {
                                    throw new IllegalArgumentException("Array element is not of the expected length");
                                }
                                hwBlob2.putInt8Array(j, bArr);
                            }
                            hwBlob.putBlob(0L, hwBlob2);
                            hwParcel2.writeBuffer(hwBlob);
                            hwParcel2.send();
                            return;
                        case 256462420:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            setHALInstrumentation();
                            return;
                        case 256921159:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            ping();
                            hwParcel2.writeStatus(0);
                            hwParcel2.send();
                            return;
                        case 257049926:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            DebugInfo debugInfo = getDebugInfo();
                            hwParcel2.writeStatus(0);
                            debugInfo.writeToParcel(hwParcel2);
                            hwParcel2.send();
                            return;
                        case 257120595:
                            hwParcel.enforceInterface("android.hidl.base@1.0::IBase");
                            notifySyspropsChanged();
                            return;
                        default:
                            return;
                    }
            }
        }
    }
}
