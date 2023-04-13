package android.hardware.hdmi;

import android.hardware.hdmi.IHdmiCecSettingChangeListener;
import android.hardware.hdmi.IHdmiCecVolumeControlFeatureListener;
import android.hardware.hdmi.IHdmiControlCallback;
import android.hardware.hdmi.IHdmiControlStatusChangeListener;
import android.hardware.hdmi.IHdmiDeviceEventListener;
import android.hardware.hdmi.IHdmiHotplugEventListener;
import android.hardware.hdmi.IHdmiInputChangeListener;
import android.hardware.hdmi.IHdmiMhlVendorCommandListener;
import android.hardware.hdmi.IHdmiRecordListener;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.hardware.hdmi.IHdmiVendorCommandListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IHdmiControlService extends IInterface {
    void addCecSettingChangeListener(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) throws RemoteException;

    void addDeviceEventListener(IHdmiDeviceEventListener iHdmiDeviceEventListener) throws RemoteException;

    void addHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener iHdmiCecVolumeControlFeatureListener) throws RemoteException;

    void addHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener) throws RemoteException;

    void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener iHdmiMhlVendorCommandListener) throws RemoteException;

    void addHotplugEventListener(IHdmiHotplugEventListener iHdmiHotplugEventListener) throws RemoteException;

    void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener) throws RemoteException;

    void addVendorCommandListener(IHdmiVendorCommandListener iHdmiVendorCommandListener, int i) throws RemoteException;

    void askRemoteDeviceToBecomeActiveSource(int i) throws RemoteException;

    boolean canChangeSystemAudioMode() throws RemoteException;

    void clearTimerRecording(int i, int i2, byte[] bArr) throws RemoteException;

    void deviceSelect(int i, IHdmiControlCallback iHdmiControlCallback) throws RemoteException;

    HdmiDeviceInfo getActiveSource() throws RemoteException;

    int[] getAllowedCecSettingIntValues(String str) throws RemoteException;

    List<String> getAllowedCecSettingStringValues(String str) throws RemoteException;

    int getCecSettingIntValue(String str) throws RemoteException;

    String getCecSettingStringValue(String str) throws RemoteException;

    List<HdmiDeviceInfo> getDeviceList() throws RemoteException;

    List<HdmiDeviceInfo> getInputDevices() throws RemoteException;

    int getMessageHistorySize() throws RemoteException;

    int getPhysicalAddress() throws RemoteException;

    List<HdmiPortInfo> getPortInfo() throws RemoteException;

    int[] getSupportedTypes() throws RemoteException;

    boolean getSystemAudioMode() throws RemoteException;

    List<String> getUserCecSettings() throws RemoteException;

    void oneTouchPlay(IHdmiControlCallback iHdmiControlCallback) throws RemoteException;

    void portSelect(int i, IHdmiControlCallback iHdmiControlCallback) throws RemoteException;

    void powerOffRemoteDevice(int i, int i2) throws RemoteException;

    void powerOnRemoteDevice(int i, int i2) throws RemoteException;

    void queryDisplayStatus(IHdmiControlCallback iHdmiControlCallback) throws RemoteException;

    void removeCecSettingChangeListener(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) throws RemoteException;

    void removeHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener iHdmiCecVolumeControlFeatureListener) throws RemoteException;

    void removeHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener) throws RemoteException;

    void removeHotplugEventListener(IHdmiHotplugEventListener iHdmiHotplugEventListener) throws RemoteException;

    void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener) throws RemoteException;

    void reportAudioStatus(int i, int i2, int i3, boolean z) throws RemoteException;

    void sendKeyEvent(int i, int i2, boolean z) throws RemoteException;

    void sendMhlVendorCommand(int i, int i2, int i3, byte[] bArr) throws RemoteException;

    void sendStandby(int i, int i2) throws RemoteException;

    void sendVendorCommand(int i, int i2, byte[] bArr, boolean z) throws RemoteException;

    void sendVolumeKeyEvent(int i, int i2, boolean z) throws RemoteException;

    void setArcMode(boolean z) throws RemoteException;

    void setCecSettingIntValue(String str, int i) throws RemoteException;

    void setCecSettingStringValue(String str, String str2) throws RemoteException;

    void setHdmiRecordListener(IHdmiRecordListener iHdmiRecordListener) throws RemoteException;

    void setInputChangeListener(IHdmiInputChangeListener iHdmiInputChangeListener) throws RemoteException;

    boolean setMessageHistorySize(int i) throws RemoteException;

    void setProhibitMode(boolean z) throws RemoteException;

    void setStandbyMode(boolean z) throws RemoteException;

    void setSystemAudioMode(boolean z, IHdmiControlCallback iHdmiControlCallback) throws RemoteException;

    void setSystemAudioModeOnForAudioOnlySource() throws RemoteException;

    void setSystemAudioMute(boolean z) throws RemoteException;

    void setSystemAudioVolume(int i, int i2, int i3) throws RemoteException;

    boolean shouldHandleTvPowerKey() throws RemoteException;

    void startOneTouchRecord(int i, byte[] bArr) throws RemoteException;

    void startTimerRecording(int i, int i2, byte[] bArr) throws RemoteException;

    void stopOneTouchRecord(int i) throws RemoteException;

    void toggleAndFollowTvPower() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IHdmiControlService {
        @Override // android.hardware.hdmi.IHdmiControlService
        public int[] getSupportedTypes() throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public HdmiDeviceInfo getActiveSource() throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void oneTouchPlay(IHdmiControlCallback callback) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void toggleAndFollowTvPower() throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean shouldHandleTvPowerKey() throws RemoteException {
            return false;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void queryDisplayStatus(IHdmiControlCallback callback) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHotplugEventListener(IHdmiHotplugEventListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeHotplugEventListener(IHdmiHotplugEventListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addDeviceEventListener(IHdmiDeviceEventListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void deviceSelect(int deviceId, IHdmiControlCallback callback) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void portSelect(int portId, IHdmiControlCallback callback) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendKeyEvent(int deviceType, int keyCode, boolean isPressed) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendVolumeKeyEvent(int deviceType, int keyCode, boolean isPressed) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<HdmiPortInfo> getPortInfo() throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean canChangeSystemAudioMode() throws RemoteException {
            return false;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean getSystemAudioMode() throws RemoteException {
            return false;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int getPhysicalAddress() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioMode(boolean enabled, IHdmiControlCallback callback) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setArcMode(boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setProhibitMode(boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioVolume(int oldIndex, int newIndex, int maxIndex) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioMute(boolean mute) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setInputChangeListener(IHdmiInputChangeListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<HdmiDeviceInfo> getInputDevices() throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<HdmiDeviceInfo> getDeviceList() throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void powerOffRemoteDevice(int logicalAddress, int powerStatus) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void powerOnRemoteDevice(int logicalAddress, int powerStatus) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void askRemoteDeviceToBecomeActiveSource(int physicalAddress) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendVendorCommand(int deviceType, int targetAddress, byte[] params, boolean hasVendorId) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addVendorCommandListener(IHdmiVendorCommandListener listener, int vendorId) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendStandby(int deviceType, int deviceId) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setHdmiRecordListener(IHdmiRecordListener callback) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void startOneTouchRecord(int recorderAddress, byte[] recordSource) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void stopOneTouchRecord(int recorderAddress) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void startTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void clearTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendMhlVendorCommand(int portId, int offset, int length, byte[] data) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setStandbyMode(boolean isStandbyModeOn) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void reportAudioStatus(int deviceType, int volume, int maxVolume, boolean isMute) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioModeOnForAudioOnlySource() throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean setMessageHistorySize(int newSize) throws RemoteException {
            return false;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int getMessageHistorySize() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<String> getUserCecSettings() throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<String> getAllowedCecSettingStringValues(String name) throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int[] getAllowedCecSettingIntValues(String name) throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public String getCecSettingStringValue(String name) throws RemoteException {
            return null;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setCecSettingStringValue(String name, String value) throws RemoteException {
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int getCecSettingIntValue(String name) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setCecSettingIntValue(String name, int value) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IHdmiControlService {
        public static final String DESCRIPTOR = "android.hardware.hdmi.IHdmiControlService";
        static final int TRANSACTION_addCecSettingChangeListener = 50;
        static final int TRANSACTION_addDeviceEventListener = 13;
        static final int TRANSACTION_addHdmiCecVolumeControlFeatureListener = 9;
        static final int TRANSACTION_addHdmiControlStatusChangeListener = 7;
        static final int TRANSACTION_addHdmiMhlVendorCommandListener = 44;
        static final int TRANSACTION_addHotplugEventListener = 11;
        static final int TRANSACTION_addSystemAudioModeChangeListener = 23;
        static final int TRANSACTION_addVendorCommandListener = 36;
        static final int TRANSACTION_askRemoteDeviceToBecomeActiveSource = 34;
        static final int TRANSACTION_canChangeSystemAudioMode = 19;
        static final int TRANSACTION_clearTimerRecording = 42;
        static final int TRANSACTION_deviceSelect = 14;
        static final int TRANSACTION_getActiveSource = 2;
        static final int TRANSACTION_getAllowedCecSettingIntValues = 54;
        static final int TRANSACTION_getAllowedCecSettingStringValues = 53;
        static final int TRANSACTION_getCecSettingIntValue = 57;
        static final int TRANSACTION_getCecSettingStringValue = 55;
        static final int TRANSACTION_getDeviceList = 31;
        static final int TRANSACTION_getInputDevices = 30;
        static final int TRANSACTION_getMessageHistorySize = 49;
        static final int TRANSACTION_getPhysicalAddress = 21;
        static final int TRANSACTION_getPortInfo = 18;
        static final int TRANSACTION_getSupportedTypes = 1;
        static final int TRANSACTION_getSystemAudioMode = 20;
        static final int TRANSACTION_getUserCecSettings = 52;
        static final int TRANSACTION_oneTouchPlay = 3;
        static final int TRANSACTION_portSelect = 15;
        static final int TRANSACTION_powerOffRemoteDevice = 32;
        static final int TRANSACTION_powerOnRemoteDevice = 33;
        static final int TRANSACTION_queryDisplayStatus = 6;
        static final int TRANSACTION_removeCecSettingChangeListener = 51;
        static final int TRANSACTION_removeHdmiCecVolumeControlFeatureListener = 10;
        static final int TRANSACTION_removeHdmiControlStatusChangeListener = 8;
        static final int TRANSACTION_removeHotplugEventListener = 12;
        static final int TRANSACTION_removeSystemAudioModeChangeListener = 24;
        static final int TRANSACTION_reportAudioStatus = 46;
        static final int TRANSACTION_sendKeyEvent = 16;
        static final int TRANSACTION_sendMhlVendorCommand = 43;
        static final int TRANSACTION_sendStandby = 37;
        static final int TRANSACTION_sendVendorCommand = 35;
        static final int TRANSACTION_sendVolumeKeyEvent = 17;
        static final int TRANSACTION_setArcMode = 25;
        static final int TRANSACTION_setCecSettingIntValue = 58;
        static final int TRANSACTION_setCecSettingStringValue = 56;
        static final int TRANSACTION_setHdmiRecordListener = 38;
        static final int TRANSACTION_setInputChangeListener = 29;
        static final int TRANSACTION_setMessageHistorySize = 48;
        static final int TRANSACTION_setProhibitMode = 26;
        static final int TRANSACTION_setStandbyMode = 45;
        static final int TRANSACTION_setSystemAudioMode = 22;
        static final int TRANSACTION_setSystemAudioModeOnForAudioOnlySource = 47;
        static final int TRANSACTION_setSystemAudioMute = 28;
        static final int TRANSACTION_setSystemAudioVolume = 27;
        static final int TRANSACTION_shouldHandleTvPowerKey = 5;
        static final int TRANSACTION_startOneTouchRecord = 39;
        static final int TRANSACTION_startTimerRecording = 41;
        static final int TRANSACTION_stopOneTouchRecord = 40;
        static final int TRANSACTION_toggleAndFollowTvPower = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IHdmiControlService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IHdmiControlService)) {
                return (IHdmiControlService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getSupportedTypes";
                case 2:
                    return "getActiveSource";
                case 3:
                    return "oneTouchPlay";
                case 4:
                    return "toggleAndFollowTvPower";
                case 5:
                    return "shouldHandleTvPowerKey";
                case 6:
                    return "queryDisplayStatus";
                case 7:
                    return "addHdmiControlStatusChangeListener";
                case 8:
                    return "removeHdmiControlStatusChangeListener";
                case 9:
                    return "addHdmiCecVolumeControlFeatureListener";
                case 10:
                    return "removeHdmiCecVolumeControlFeatureListener";
                case 11:
                    return "addHotplugEventListener";
                case 12:
                    return "removeHotplugEventListener";
                case 13:
                    return "addDeviceEventListener";
                case 14:
                    return "deviceSelect";
                case 15:
                    return "portSelect";
                case 16:
                    return "sendKeyEvent";
                case 17:
                    return "sendVolumeKeyEvent";
                case 18:
                    return "getPortInfo";
                case 19:
                    return "canChangeSystemAudioMode";
                case 20:
                    return "getSystemAudioMode";
                case 21:
                    return "getPhysicalAddress";
                case 22:
                    return "setSystemAudioMode";
                case 23:
                    return "addSystemAudioModeChangeListener";
                case 24:
                    return "removeSystemAudioModeChangeListener";
                case 25:
                    return "setArcMode";
                case 26:
                    return "setProhibitMode";
                case 27:
                    return "setSystemAudioVolume";
                case 28:
                    return "setSystemAudioMute";
                case 29:
                    return "setInputChangeListener";
                case 30:
                    return "getInputDevices";
                case 31:
                    return "getDeviceList";
                case 32:
                    return "powerOffRemoteDevice";
                case 33:
                    return "powerOnRemoteDevice";
                case 34:
                    return "askRemoteDeviceToBecomeActiveSource";
                case 35:
                    return "sendVendorCommand";
                case 36:
                    return "addVendorCommandListener";
                case 37:
                    return "sendStandby";
                case 38:
                    return "setHdmiRecordListener";
                case 39:
                    return "startOneTouchRecord";
                case 40:
                    return "stopOneTouchRecord";
                case 41:
                    return "startTimerRecording";
                case 42:
                    return "clearTimerRecording";
                case 43:
                    return "sendMhlVendorCommand";
                case 44:
                    return "addHdmiMhlVendorCommandListener";
                case 45:
                    return "setStandbyMode";
                case 46:
                    return "reportAudioStatus";
                case 47:
                    return "setSystemAudioModeOnForAudioOnlySource";
                case 48:
                    return "setMessageHistorySize";
                case 49:
                    return "getMessageHistorySize";
                case 50:
                    return "addCecSettingChangeListener";
                case 51:
                    return "removeCecSettingChangeListener";
                case 52:
                    return "getUserCecSettings";
                case 53:
                    return "getAllowedCecSettingStringValues";
                case 54:
                    return "getAllowedCecSettingIntValues";
                case 55:
                    return "getCecSettingStringValue";
                case 56:
                    return "setCecSettingStringValue";
                case 57:
                    return "getCecSettingIntValue";
                case 58:
                    return "setCecSettingIntValue";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int[] _result = getSupportedTypes();
                            reply.writeNoException();
                            reply.writeIntArray(_result);
                            break;
                        case 2:
                            HdmiDeviceInfo _result2 = getActiveSource();
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            IHdmiControlCallback _arg0 = IHdmiControlCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            oneTouchPlay(_arg0);
                            reply.writeNoException();
                            break;
                        case 4:
                            toggleAndFollowTvPower();
                            reply.writeNoException();
                            break;
                        case 5:
                            boolean _result3 = shouldHandleTvPowerKey();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            IHdmiControlCallback _arg02 = IHdmiControlCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            queryDisplayStatus(_arg02);
                            reply.writeNoException();
                            break;
                        case 7:
                            IHdmiControlStatusChangeListener _arg03 = IHdmiControlStatusChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addHdmiControlStatusChangeListener(_arg03);
                            reply.writeNoException();
                            break;
                        case 8:
                            IHdmiControlStatusChangeListener _arg04 = IHdmiControlStatusChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeHdmiControlStatusChangeListener(_arg04);
                            reply.writeNoException();
                            break;
                        case 9:
                            IHdmiCecVolumeControlFeatureListener _arg05 = IHdmiCecVolumeControlFeatureListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addHdmiCecVolumeControlFeatureListener(_arg05);
                            reply.writeNoException();
                            break;
                        case 10:
                            IHdmiCecVolumeControlFeatureListener _arg06 = IHdmiCecVolumeControlFeatureListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeHdmiCecVolumeControlFeatureListener(_arg06);
                            reply.writeNoException();
                            break;
                        case 11:
                            IHdmiHotplugEventListener _arg07 = IHdmiHotplugEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addHotplugEventListener(_arg07);
                            reply.writeNoException();
                            break;
                        case 12:
                            IHdmiHotplugEventListener _arg08 = IHdmiHotplugEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeHotplugEventListener(_arg08);
                            reply.writeNoException();
                            break;
                        case 13:
                            IHdmiDeviceEventListener _arg09 = IHdmiDeviceEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addDeviceEventListener(_arg09);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg010 = data.readInt();
                            IHdmiControlCallback _arg1 = IHdmiControlCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deviceSelect(_arg010, _arg1);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg011 = data.readInt();
                            IHdmiControlCallback _arg12 = IHdmiControlCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            portSelect(_arg011, _arg12);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg012 = data.readInt();
                            int _arg13 = data.readInt();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            sendKeyEvent(_arg012, _arg13, _arg2);
                            reply.writeNoException();
                            break;
                        case 17:
                            int _arg013 = data.readInt();
                            int _arg14 = data.readInt();
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            sendVolumeKeyEvent(_arg013, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 18:
                            List<HdmiPortInfo> _result4 = getPortInfo();
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            break;
                        case 19:
                            boolean _result5 = canChangeSystemAudioMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 20:
                            boolean _result6 = getSystemAudioMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 21:
                            int _result7 = getPhysicalAddress();
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 22:
                            boolean _arg014 = data.readBoolean();
                            IHdmiControlCallback _arg15 = IHdmiControlCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setSystemAudioMode(_arg014, _arg15);
                            reply.writeNoException();
                            break;
                        case 23:
                            IHdmiSystemAudioModeChangeListener _arg015 = IHdmiSystemAudioModeChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addSystemAudioModeChangeListener(_arg015);
                            reply.writeNoException();
                            break;
                        case 24:
                            IHdmiSystemAudioModeChangeListener _arg016 = IHdmiSystemAudioModeChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeSystemAudioModeChangeListener(_arg016);
                            reply.writeNoException();
                            break;
                        case 25:
                            boolean _arg017 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setArcMode(_arg017);
                            reply.writeNoException();
                            break;
                        case 26:
                            boolean _arg018 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setProhibitMode(_arg018);
                            reply.writeNoException();
                            break;
                        case 27:
                            int _arg019 = data.readInt();
                            int _arg16 = data.readInt();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            setSystemAudioVolume(_arg019, _arg16, _arg23);
                            reply.writeNoException();
                            break;
                        case 28:
                            boolean _arg020 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSystemAudioMute(_arg020);
                            reply.writeNoException();
                            break;
                        case 29:
                            IHdmiInputChangeListener _arg021 = IHdmiInputChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setInputChangeListener(_arg021);
                            reply.writeNoException();
                            break;
                        case 30:
                            List<HdmiDeviceInfo> _result8 = getInputDevices();
                            reply.writeNoException();
                            reply.writeTypedList(_result8, 1);
                            break;
                        case 31:
                            List<HdmiDeviceInfo> _result9 = getDeviceList();
                            reply.writeNoException();
                            reply.writeTypedList(_result9, 1);
                            break;
                        case 32:
                            int _arg022 = data.readInt();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            powerOffRemoteDevice(_arg022, _arg17);
                            reply.writeNoException();
                            break;
                        case 33:
                            int _arg023 = data.readInt();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            powerOnRemoteDevice(_arg023, _arg18);
                            reply.writeNoException();
                            break;
                        case 34:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            askRemoteDeviceToBecomeActiveSource(_arg024);
                            reply.writeNoException();
                            break;
                        case 35:
                            int _arg025 = data.readInt();
                            int _arg19 = data.readInt();
                            byte[] _arg24 = data.createByteArray();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            sendVendorCommand(_arg025, _arg19, _arg24, _arg3);
                            reply.writeNoException();
                            break;
                        case 36:
                            IHdmiVendorCommandListener _arg026 = IHdmiVendorCommandListener.Stub.asInterface(data.readStrongBinder());
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            addVendorCommandListener(_arg026, _arg110);
                            reply.writeNoException();
                            break;
                        case 37:
                            int _arg027 = data.readInt();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            sendStandby(_arg027, _arg111);
                            reply.writeNoException();
                            break;
                        case 38:
                            IHdmiRecordListener _arg028 = IHdmiRecordListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setHdmiRecordListener(_arg028);
                            reply.writeNoException();
                            break;
                        case 39:
                            int _arg029 = data.readInt();
                            byte[] _arg112 = data.createByteArray();
                            data.enforceNoDataAvail();
                            startOneTouchRecord(_arg029, _arg112);
                            reply.writeNoException();
                            break;
                        case 40:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            stopOneTouchRecord(_arg030);
                            reply.writeNoException();
                            break;
                        case 41:
                            int _arg031 = data.readInt();
                            int _arg113 = data.readInt();
                            byte[] _arg25 = data.createByteArray();
                            data.enforceNoDataAvail();
                            startTimerRecording(_arg031, _arg113, _arg25);
                            reply.writeNoException();
                            break;
                        case 42:
                            int _arg032 = data.readInt();
                            int _arg114 = data.readInt();
                            byte[] _arg26 = data.createByteArray();
                            data.enforceNoDataAvail();
                            clearTimerRecording(_arg032, _arg114, _arg26);
                            reply.writeNoException();
                            break;
                        case 43:
                            int _arg033 = data.readInt();
                            int _arg115 = data.readInt();
                            int _arg27 = data.readInt();
                            byte[] _arg32 = data.createByteArray();
                            data.enforceNoDataAvail();
                            sendMhlVendorCommand(_arg033, _arg115, _arg27, _arg32);
                            reply.writeNoException();
                            break;
                        case 44:
                            IHdmiMhlVendorCommandListener _arg034 = IHdmiMhlVendorCommandListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addHdmiMhlVendorCommandListener(_arg034);
                            reply.writeNoException();
                            break;
                        case 45:
                            boolean _arg035 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setStandbyMode(_arg035);
                            reply.writeNoException();
                            break;
                        case 46:
                            int _arg036 = data.readInt();
                            int _arg116 = data.readInt();
                            int _arg28 = data.readInt();
                            boolean _arg33 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reportAudioStatus(_arg036, _arg116, _arg28, _arg33);
                            reply.writeNoException();
                            break;
                        case 47:
                            setSystemAudioModeOnForAudioOnlySource();
                            reply.writeNoException();
                            break;
                        case 48:
                            int _arg037 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result10 = setMessageHistorySize(_arg037);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 49:
                            int _result11 = getMessageHistorySize();
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 50:
                            String _arg038 = data.readString();
                            IHdmiCecSettingChangeListener _arg117 = IHdmiCecSettingChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addCecSettingChangeListener(_arg038, _arg117);
                            reply.writeNoException();
                            break;
                        case 51:
                            String _arg039 = data.readString();
                            IHdmiCecSettingChangeListener _arg118 = IHdmiCecSettingChangeListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeCecSettingChangeListener(_arg039, _arg118);
                            reply.writeNoException();
                            break;
                        case 52:
                            List<String> _result12 = getUserCecSettings();
                            reply.writeNoException();
                            reply.writeStringList(_result12);
                            break;
                        case 53:
                            String _arg040 = data.readString();
                            data.enforceNoDataAvail();
                            List<String> _result13 = getAllowedCecSettingStringValues(_arg040);
                            reply.writeNoException();
                            reply.writeStringList(_result13);
                            break;
                        case 54:
                            String _arg041 = data.readString();
                            data.enforceNoDataAvail();
                            int[] _result14 = getAllowedCecSettingIntValues(_arg041);
                            reply.writeNoException();
                            reply.writeIntArray(_result14);
                            break;
                        case 55:
                            String _arg042 = data.readString();
                            data.enforceNoDataAvail();
                            String _result15 = getCecSettingStringValue(_arg042);
                            reply.writeNoException();
                            reply.writeString(_result15);
                            break;
                        case 56:
                            String _arg043 = data.readString();
                            String _arg119 = data.readString();
                            data.enforceNoDataAvail();
                            setCecSettingStringValue(_arg043, _arg119);
                            reply.writeNoException();
                            break;
                        case 57:
                            String _arg044 = data.readString();
                            data.enforceNoDataAvail();
                            int _result16 = getCecSettingIntValue(_arg044);
                            reply.writeNoException();
                            reply.writeInt(_result16);
                            break;
                        case 58:
                            String _arg045 = data.readString();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            setCecSettingIntValue(_arg045, _arg120);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IHdmiControlService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public int[] getSupportedTypes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public HdmiDeviceInfo getActiveSource() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    HdmiDeviceInfo _result = (HdmiDeviceInfo) _reply.readTypedObject(HdmiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void oneTouchPlay(IHdmiControlCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void toggleAndFollowTvPower() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public boolean shouldHandleTvPowerKey() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void queryDisplayStatus(IHdmiControlCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void removeHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void removeHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addHotplugEventListener(IHdmiHotplugEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void removeHotplugEventListener(IHdmiHotplugEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addDeviceEventListener(IHdmiDeviceEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void deviceSelect(int deviceId, IHdmiControlCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void portSelect(int portId, IHdmiControlCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(portId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void sendKeyEvent(int deviceType, int keyCode, boolean isPressed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceType);
                    _data.writeInt(keyCode);
                    _data.writeBoolean(isPressed);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void sendVolumeKeyEvent(int deviceType, int keyCode, boolean isPressed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceType);
                    _data.writeInt(keyCode);
                    _data.writeBoolean(isPressed);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public List<HdmiPortInfo> getPortInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    List<HdmiPortInfo> _result = _reply.createTypedArrayList(HdmiPortInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public boolean canChangeSystemAudioMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public boolean getSystemAudioMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public int getPhysicalAddress() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setSystemAudioMode(boolean enabled, IHdmiControlCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setArcMode(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setProhibitMode(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setSystemAudioVolume(int oldIndex, int newIndex, int maxIndex) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(oldIndex);
                    _data.writeInt(newIndex);
                    _data.writeInt(maxIndex);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setSystemAudioMute(boolean mute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(mute);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setInputChangeListener(IHdmiInputChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public List<HdmiDeviceInfo> getInputDevices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    List<HdmiDeviceInfo> _result = _reply.createTypedArrayList(HdmiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public List<HdmiDeviceInfo> getDeviceList() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    List<HdmiDeviceInfo> _result = _reply.createTypedArrayList(HdmiDeviceInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void powerOffRemoteDevice(int logicalAddress, int powerStatus) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(logicalAddress);
                    _data.writeInt(powerStatus);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void powerOnRemoteDevice(int logicalAddress, int powerStatus) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(logicalAddress);
                    _data.writeInt(powerStatus);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void askRemoteDeviceToBecomeActiveSource(int physicalAddress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(physicalAddress);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void sendVendorCommand(int deviceType, int targetAddress, byte[] params, boolean hasVendorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceType);
                    _data.writeInt(targetAddress);
                    _data.writeByteArray(params);
                    _data.writeBoolean(hasVendorId);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addVendorCommandListener(IHdmiVendorCommandListener listener, int vendorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(vendorId);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void sendStandby(int deviceType, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceType);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setHdmiRecordListener(IHdmiRecordListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void startOneTouchRecord(int recorderAddress, byte[] recordSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(recorderAddress);
                    _data.writeByteArray(recordSource);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void stopOneTouchRecord(int recorderAddress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(recorderAddress);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void startTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(recorderAddress);
                    _data.writeInt(sourceType);
                    _data.writeByteArray(recordSource);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void clearTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(recorderAddress);
                    _data.writeInt(sourceType);
                    _data.writeByteArray(recordSource);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void sendMhlVendorCommand(int portId, int offset, int length, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(portId);
                    _data.writeInt(offset);
                    _data.writeInt(length);
                    _data.writeByteArray(data);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setStandbyMode(boolean isStandbyModeOn) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isStandbyModeOn);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void reportAudioStatus(int deviceType, int volume, int maxVolume, boolean isMute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceType);
                    _data.writeInt(volume);
                    _data.writeInt(maxVolume);
                    _data.writeBoolean(isMute);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setSystemAudioModeOnForAudioOnlySource() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public boolean setMessageHistorySize(int newSize) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(newSize);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public int getMessageHistorySize() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void addCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void removeCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public List<String> getUserCecSettings() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public List<String> getAllowedCecSettingStringValues(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public int[] getAllowedCecSettingIntValues(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public String getCecSettingStringValue(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setCecSettingStringValue(String name, String value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(value);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public int getCecSettingIntValue(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.hdmi.IHdmiControlService
            public void setCecSettingIntValue(String name, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(value);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 57;
        }
    }
}
