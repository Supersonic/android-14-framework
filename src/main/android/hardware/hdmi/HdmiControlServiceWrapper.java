package android.hardware.hdmi;

import android.hardware.hdmi.IHdmiControlService;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class HdmiControlServiceWrapper {
    public static final int DEVICE_PURE_CEC_SWITCH = 6;
    private List<HdmiPortInfo> mInfoList = null;
    private int[] mTypes = null;
    private final IHdmiControlService mInterface = new IHdmiControlService.Stub() { // from class: android.hardware.hdmi.HdmiControlServiceWrapper.1
        @Override // android.hardware.hdmi.IHdmiControlService
        public int[] getSupportedTypes() {
            return HdmiControlServiceWrapper.this.getSupportedTypes();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public HdmiDeviceInfo getActiveSource() {
            return HdmiControlServiceWrapper.this.getActiveSource();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void oneTouchPlay(IHdmiControlCallback callback) {
            HdmiControlServiceWrapper.this.oneTouchPlay(callback);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void toggleAndFollowTvPower() {
            HdmiControlServiceWrapper.this.toggleAndFollowTvPower();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean shouldHandleTvPowerKey() {
            return HdmiControlServiceWrapper.this.shouldHandleTvPowerKey();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void queryDisplayStatus(IHdmiControlCallback callback) {
            HdmiControlServiceWrapper.this.queryDisplayStatus(callback);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) {
            HdmiControlServiceWrapper.this.addHdmiControlStatusChangeListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) {
            HdmiControlServiceWrapper.this.removeHdmiControlStatusChangeListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHotplugEventListener(IHdmiHotplugEventListener listener) {
            HdmiControlServiceWrapper.this.addHotplugEventListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeHotplugEventListener(IHdmiHotplugEventListener listener) {
            HdmiControlServiceWrapper.this.removeHotplugEventListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addDeviceEventListener(IHdmiDeviceEventListener listener) {
            HdmiControlServiceWrapper.this.addDeviceEventListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void deviceSelect(int deviceId, IHdmiControlCallback callback) {
            HdmiControlServiceWrapper.this.deviceSelect(deviceId, callback);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void portSelect(int portId, IHdmiControlCallback callback) {
            HdmiControlServiceWrapper.this.portSelect(portId, callback);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendKeyEvent(int deviceType, int keyCode, boolean isPressed) {
            HdmiControlServiceWrapper.this.sendKeyEvent(deviceType, keyCode, isPressed);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendVolumeKeyEvent(int deviceType, int keyCode, boolean isPressed) {
            HdmiControlServiceWrapper.this.sendVolumeKeyEvent(deviceType, keyCode, isPressed);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<HdmiPortInfo> getPortInfo() {
            return HdmiControlServiceWrapper.this.getPortInfo();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean canChangeSystemAudioMode() {
            return HdmiControlServiceWrapper.this.canChangeSystemAudioMode();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean getSystemAudioMode() {
            return HdmiControlServiceWrapper.this.getSystemAudioMode();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int getPhysicalAddress() {
            return HdmiControlServiceWrapper.this.getPhysicalAddress();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioMode(boolean enabled, IHdmiControlCallback callback) {
            HdmiControlServiceWrapper.this.setSystemAudioMode(enabled, callback);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) {
            HdmiControlServiceWrapper.this.addSystemAudioModeChangeListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) {
            HdmiControlServiceWrapper.this.removeSystemAudioModeChangeListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setArcMode(boolean enabled) {
            HdmiControlServiceWrapper.this.setArcMode(enabled);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setProhibitMode(boolean enabled) {
            HdmiControlServiceWrapper.this.setProhibitMode(enabled);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioVolume(int oldIndex, int newIndex, int maxIndex) {
            HdmiControlServiceWrapper.this.setSystemAudioVolume(oldIndex, newIndex, maxIndex);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioMute(boolean mute) {
            HdmiControlServiceWrapper.this.setSystemAudioMute(mute);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setInputChangeListener(IHdmiInputChangeListener listener) {
            HdmiControlServiceWrapper.this.setInputChangeListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<HdmiDeviceInfo> getInputDevices() {
            return HdmiControlServiceWrapper.this.getInputDevices();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<HdmiDeviceInfo> getDeviceList() {
            return HdmiControlServiceWrapper.this.getDeviceList();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void powerOffRemoteDevice(int logicalAddress, int powerStatus) {
            HdmiControlServiceWrapper.this.powerOffRemoteDevice(logicalAddress, powerStatus);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void powerOnRemoteDevice(int logicalAddress, int powerStatus) {
            HdmiControlServiceWrapper.this.powerOnRemoteDevice(logicalAddress, powerStatus);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void askRemoteDeviceToBecomeActiveSource(int physicalAddress) {
            HdmiControlServiceWrapper.this.askRemoteDeviceToBecomeActiveSource(physicalAddress);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendVendorCommand(int deviceType, int targetAddress, byte[] params, boolean hasVendorId) {
            HdmiControlServiceWrapper.this.sendVendorCommand(deviceType, targetAddress, params, hasVendorId);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addVendorCommandListener(IHdmiVendorCommandListener listener, int vendorId) {
            HdmiControlServiceWrapper.this.addVendorCommandListener(listener, vendorId);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendStandby(int deviceType, int deviceId) {
            HdmiControlServiceWrapper.this.sendStandby(deviceType, deviceId);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setHdmiRecordListener(IHdmiRecordListener callback) {
            HdmiControlServiceWrapper.this.setHdmiRecordListener(callback);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void startOneTouchRecord(int recorderAddress, byte[] recordSource) {
            HdmiControlServiceWrapper.this.startOneTouchRecord(recorderAddress, recordSource);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void stopOneTouchRecord(int recorderAddress) {
            HdmiControlServiceWrapper.this.stopOneTouchRecord(recorderAddress);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void startTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) {
            HdmiControlServiceWrapper.this.startTimerRecording(recorderAddress, sourceType, recordSource);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void clearTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) {
            HdmiControlServiceWrapper.this.clearTimerRecording(recorderAddress, sourceType, recordSource);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void sendMhlVendorCommand(int portId, int offset, int length, byte[] data) {
            HdmiControlServiceWrapper.this.sendMhlVendorCommand(portId, offset, length, data);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener listener) {
            HdmiControlServiceWrapper.this.addHdmiMhlVendorCommandListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setStandbyMode(boolean isStandbyModeOn) {
            HdmiControlServiceWrapper.this.setStandbyMode(isStandbyModeOn);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void reportAudioStatus(int deviceType, int volume, int maxVolume, boolean isMute) {
            HdmiControlServiceWrapper.this.reportAudioStatus(deviceType, volume, maxVolume, isMute);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setSystemAudioModeOnForAudioOnlySource() {
            HdmiControlServiceWrapper.this.setSystemAudioModeOnForAudioOnlySource();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) {
            HdmiControlServiceWrapper.this.addHdmiCecVolumeControlFeatureListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) {
            HdmiControlServiceWrapper.this.removeHdmiCecVolumeControlFeatureListener(listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int getMessageHistorySize() {
            return HdmiControlServiceWrapper.this.getMessageHistorySize();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public boolean setMessageHistorySize(int newSize) {
            return HdmiControlServiceWrapper.this.setMessageHistorySize(newSize);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void addCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) {
            HdmiControlServiceWrapper.this.addCecSettingChangeListener(name, listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void removeCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) {
            HdmiControlServiceWrapper.this.removeCecSettingChangeListener(name, listener);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<String> getUserCecSettings() {
            return HdmiControlServiceWrapper.this.getUserCecSettings();
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public List<String> getAllowedCecSettingStringValues(String name) {
            return HdmiControlServiceWrapper.this.getAllowedCecSettingStringValues(name);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int[] getAllowedCecSettingIntValues(String name) {
            return HdmiControlServiceWrapper.this.getAllowedCecSettingIntValues(name);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public String getCecSettingStringValue(String name) {
            return HdmiControlServiceWrapper.this.getCecSettingStringValue(name);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setCecSettingStringValue(String name, String value) {
            HdmiControlServiceWrapper.this.setCecSettingStringValue(name, value);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public int getCecSettingIntValue(String name) {
            return HdmiControlServiceWrapper.this.getCecSettingIntValue(name);
        }

        @Override // android.hardware.hdmi.IHdmiControlService
        public void setCecSettingIntValue(String name, int value) {
            HdmiControlServiceWrapper.this.setCecSettingIntValue(name, value);
        }
    };

    public HdmiControlManager createHdmiControlManager() {
        return new HdmiControlManager(this.mInterface);
    }

    public void setPortInfo(List<HdmiPortInfo> infoList) {
        this.mInfoList = infoList;
    }

    public void setDeviceTypes(int[] types) {
        this.mTypes = types;
    }

    public List<HdmiPortInfo> getPortInfo() {
        return this.mInfoList;
    }

    public int[] getSupportedTypes() {
        return this.mTypes;
    }

    public HdmiDeviceInfo getActiveSource() {
        return null;
    }

    public void oneTouchPlay(IHdmiControlCallback callback) {
    }

    public void toggleAndFollowTvPower() {
    }

    public boolean shouldHandleTvPowerKey() {
        return true;
    }

    public void queryDisplayStatus(IHdmiControlCallback callback) {
    }

    public void addHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) {
    }

    public void removeHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener listener) {
    }

    public void addHotplugEventListener(IHdmiHotplugEventListener listener) {
    }

    public void removeHotplugEventListener(IHdmiHotplugEventListener listener) {
    }

    public void addDeviceEventListener(IHdmiDeviceEventListener listener) {
    }

    public void deviceSelect(int deviceId, IHdmiControlCallback callback) {
    }

    public void portSelect(int portId, IHdmiControlCallback callback) {
    }

    public void sendKeyEvent(int deviceType, int keyCode, boolean isPressed) {
    }

    public void sendVolumeKeyEvent(int deviceType, int keyCode, boolean isPressed) {
    }

    public boolean canChangeSystemAudioMode() {
        return true;
    }

    public boolean getSystemAudioMode() {
        return true;
    }

    public int getPhysicalAddress() {
        return 65535;
    }

    public void setSystemAudioMode(boolean enabled, IHdmiControlCallback callback) {
    }

    public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) {
    }

    public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) {
    }

    public void setArcMode(boolean enabled) {
    }

    public void setProhibitMode(boolean enabled) {
    }

    public void setSystemAudioVolume(int oldIndex, int newIndex, int maxIndex) {
    }

    public void setSystemAudioMute(boolean mute) {
    }

    public void setInputChangeListener(IHdmiInputChangeListener listener) {
    }

    public List<HdmiDeviceInfo> getInputDevices() {
        return null;
    }

    public List<HdmiDeviceInfo> getDeviceList() {
        return null;
    }

    public void powerOffRemoteDevice(int logicalAddress, int powerStatus) {
    }

    public void powerOnRemoteDevice(int logicalAddress, int powerStatus) {
    }

    public void askRemoteDeviceToBecomeActiveSource(int physicalAddress) {
    }

    public void sendVendorCommand(int deviceType, int targetAddress, byte[] params, boolean hasVendorId) {
    }

    public void addVendorCommandListener(IHdmiVendorCommandListener listener, int vendorId) {
    }

    public void sendStandby(int deviceType, int deviceId) {
    }

    public void setHdmiRecordListener(IHdmiRecordListener callback) {
    }

    public void startOneTouchRecord(int recorderAddress, byte[] recordSource) {
    }

    public void stopOneTouchRecord(int recorderAddress) {
    }

    public void startTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) {
    }

    public void clearTimerRecording(int recorderAddress, int sourceType, byte[] recordSource) {
    }

    public void sendMhlVendorCommand(int portId, int offset, int length, byte[] data) {
    }

    public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener listener) {
    }

    public void setStandbyMode(boolean isStandbyModeOn) {
    }

    public void setHdmiCecVolumeControlEnabled(boolean isHdmiCecVolumeControlEnabled) {
    }

    public boolean isHdmiCecVolumeControlEnabled() {
        return true;
    }

    public void reportAudioStatus(int deviceType, int volume, int maxVolume, boolean isMute) {
    }

    public void setSystemAudioModeOnForAudioOnlySource() {
    }

    public void addHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) {
    }

    public void removeHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener listener) {
    }

    public int getMessageHistorySize() {
        return 0;
    }

    public boolean setMessageHistorySize(int newSize) {
        return true;
    }

    public void addCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) {
    }

    public void removeCecSettingChangeListener(String name, IHdmiCecSettingChangeListener listener) {
    }

    public List<String> getUserCecSettings() {
        return new ArrayList();
    }

    public List<String> getAllowedCecSettingStringValues(String name) {
        return new ArrayList();
    }

    public int[] getAllowedCecSettingIntValues(String name) {
        return new int[0];
    }

    public String getCecSettingStringValue(String name) {
        return "";
    }

    public void setCecSettingStringValue(String name, String value) {
    }

    public int getCecSettingIntValue(String name) {
        return 0;
    }

    public void setCecSettingIntValue(String name, int value) {
    }
}
