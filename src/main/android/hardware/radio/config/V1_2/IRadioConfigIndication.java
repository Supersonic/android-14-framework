package android.hardware.radio.config.V1_2;

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
public interface IRadioConfigIndication extends android.hardware.radio.config.V1_0.IRadioConfigIndication {
    void simSlotsStatusChanged_1_2(int i, ArrayList<SimSlotStatus> arrayList) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IRadioConfigIndication {
        @Override // android.hardware.radio.config.V1_0.IRadioConfigIndication
        public IHwBinder asBinder() {
            return this;
        }

        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        public final String interfaceDescriptor() {
            return "android.hardware.radio.config@1.2::IRadioConfigIndication";
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
            return new ArrayList<>(Arrays.asList("android.hardware.radio.config@1.2::IRadioConfigIndication", "android.hardware.radio.config@1.1::IRadioConfigIndication", "android.hardware.radio.config@1.0::IRadioConfigIndication", "android.hidl.base@1.0::IBase"));
        }

        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-80, -44, 82, -7, -94, -28, 95, Byte.MIN_VALUE, -67, -74, 114, -79, -28, -53, 100, -97, -1, 80, 41, AnswerToReset.DIRECT_CONVENTION, -33, 32, Byte.MIN_VALUE, -103, -66, 65, 115, -113, 17, -51, 46, -83}, new byte[]{Byte.MAX_VALUE, -49, 22, Byte.MAX_VALUE, 89, AnswerToReset.DIRECT_CONVENTION, 16, -58, 123, 89, -85, 112, 50, 23, -127, -62, 106, 85, 117, -22, -74, 8, 3, -25, -53, -79, -63, 76, 113, 8, 90, AnswerToReset.DIRECT_CONVENTION}, new byte[]{34, -117, 46, -29, -56, -62, 118, -55, -16, -81, -83, 45, -61, 19, -54, 61, 107, -67, -98, 72, 45, -33, 49, 60, 114, 4, -58, 10, -39, -74, 54, -85}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, AnswerToReset.DIRECT_CONVENTION, 24, -54, 76}));
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
            if ("android.hardware.radio.config@1.2::IRadioConfigIndication".equals(str)) {
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
            if (i == 1) {
                hwParcel.enforceInterface("android.hardware.radio.config@1.0::IRadioConfigIndication");
                simSlotsStatusChanged(hwParcel.readInt32(), android.hardware.radio.config.V1_0.SimSlotStatus.readVectorFromParcel(hwParcel));
            } else if (i == 2) {
                hwParcel.enforceInterface("android.hardware.radio.config@1.2::IRadioConfigIndication");
                simSlotsStatusChanged_1_2(hwParcel.readInt32(), SimSlotStatus.readVectorFromParcel(hwParcel));
            } else {
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
