package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiPortInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.hdmi.HdmiCecController;
import com.android.server.location.gnss.hal.GnssNative;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
@VisibleForTesting
/* loaded from: classes.dex */
public class HdmiCecNetwork {
    public final Handler mHandler;
    public final HdmiCecController mHdmiCecController;
    public final HdmiControlService mHdmiControlService;
    public final HdmiMhlControllerStub mHdmiMhlController;
    public final Object mLock;
    public UnmodifiableSparseArray<HdmiDeviceInfo> mPortDeviceMap;
    public UnmodifiableSparseIntArray mPortIdMap;
    public UnmodifiableSparseArray<HdmiPortInfo> mPortInfoMap;
    public final SparseArray<HdmiCecLocalDevice> mLocalDevices = new SparseArray<>();
    public final SparseArray<HdmiDeviceInfo> mDeviceInfos = new SparseArray<>();
    public final ArraySet<Integer> mCecSwitches = new ArraySet<>();
    @GuardedBy({"mLock"})
    public List<HdmiDeviceInfo> mSafeAllDeviceInfos = Collections.emptyList();
    @GuardedBy({"mLock"})
    public List<HdmiDeviceInfo> mSafeExternalInputs = Collections.emptyList();
    @GuardedBy({"mLock"})
    public List<HdmiPortInfo> mPortInfo = Collections.emptyList();

    public static boolean isParentPath(int i, int i2) {
        for (int i3 = 0; i3 <= 12; i3 += 4) {
            if (((i2 >> i3) & 15) != 0) {
                if (((i >> i3) & 15) == 0) {
                    int i4 = i3 + 4;
                    return (i2 >> i4) == (i >> i4);
                }
                return false;
            }
        }
        return false;
    }

    public static int logicalAddressToDeviceType(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
            case 2:
            case 9:
                return 1;
            case 3:
            case 6:
            case 7:
            case 10:
                return 3;
            case 4:
            case 8:
            case 11:
                return 4;
            case 5:
                return 5;
            default:
                return 2;
        }
    }

    public HdmiCecNetwork(HdmiControlService hdmiControlService, HdmiCecController hdmiCecController, HdmiMhlControllerStub hdmiMhlControllerStub) {
        this.mHdmiControlService = hdmiControlService;
        this.mHdmiCecController = hdmiCecController;
        this.mHdmiMhlController = hdmiMhlControllerStub;
        this.mHandler = new Handler(hdmiControlService.getServiceLooper());
        this.mLock = hdmiControlService.getServiceLock();
    }

    public static boolean isConnectedToCecSwitch(int i, Collection<Integer> collection) {
        for (Integer num : collection) {
            if (isParentPath(num.intValue(), i)) {
                return true;
            }
        }
        return false;
    }

    public void addLocalDevice(int i, HdmiCecLocalDevice hdmiCecLocalDevice) {
        this.mLocalDevices.put(i, hdmiCecLocalDevice);
    }

    public HdmiCecLocalDevice getLocalDevice(int i) {
        return this.mLocalDevices.get(i);
    }

    public List<HdmiCecLocalDevice> getLocalDeviceList() {
        assertRunOnServiceThread();
        return HdmiUtils.sparseArrayToList(this.mLocalDevices);
    }

    public boolean isAllocatedLocalDeviceAddress(int i) {
        assertRunOnServiceThread();
        for (int i2 = 0; i2 < this.mLocalDevices.size(); i2++) {
            if (this.mLocalDevices.valueAt(i2).isAddressOf(i)) {
                return true;
            }
        }
        return false;
    }

    public void clearLocalDevices() {
        assertRunOnServiceThread();
        this.mLocalDevices.clear();
    }

    public HdmiDeviceInfo getDeviceInfo(int i) {
        return this.mDeviceInfos.get(i);
    }

    public final HdmiDeviceInfo addDeviceInfo(HdmiDeviceInfo hdmiDeviceInfo) {
        assertRunOnServiceThread();
        HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(hdmiDeviceInfo.getLogicalAddress());
        this.mHdmiControlService.checkLogicalAddressConflictAndReallocate(hdmiDeviceInfo.getLogicalAddress(), hdmiDeviceInfo.getPhysicalAddress());
        if (cecDeviceInfo != null) {
            removeDeviceInfo(hdmiDeviceInfo.getId());
        }
        this.mDeviceInfos.append(hdmiDeviceInfo.getId(), hdmiDeviceInfo);
        updateSafeDeviceInfoList();
        return cecDeviceInfo;
    }

    public final HdmiDeviceInfo removeDeviceInfo(int i) {
        assertRunOnServiceThread();
        HdmiDeviceInfo hdmiDeviceInfo = this.mDeviceInfos.get(i);
        if (hdmiDeviceInfo != null) {
            this.mDeviceInfos.remove(i);
        }
        updateSafeDeviceInfoList();
        return hdmiDeviceInfo;
    }

    public HdmiDeviceInfo getCecDeviceInfo(int i) {
        assertRunOnServiceThread();
        return this.mDeviceInfos.get(HdmiDeviceInfo.idForCecDevice(i));
    }

    public final void addCecDevice(HdmiDeviceInfo hdmiDeviceInfo) {
        assertRunOnServiceThread();
        HdmiDeviceInfo addDeviceInfo = addDeviceInfo(hdmiDeviceInfo);
        if (isLocalDeviceAddress(hdmiDeviceInfo.getLogicalAddress())) {
            return;
        }
        this.mHdmiControlService.checkAndUpdateAbsoluteVolumeControlState();
        if (hdmiDeviceInfo.getPhysicalAddress() == 65535) {
            return;
        }
        if (addDeviceInfo == null || addDeviceInfo.getPhysicalAddress() == 65535) {
            invokeDeviceEventListener(hdmiDeviceInfo, 1);
        } else if (addDeviceInfo.equals(hdmiDeviceInfo)) {
        } else {
            invokeDeviceEventListener(addDeviceInfo, 2);
            invokeDeviceEventListener(hdmiDeviceInfo, 1);
        }
    }

    public final void invokeDeviceEventListener(HdmiDeviceInfo hdmiDeviceInfo, int i) {
        if (hideDevicesBehindLegacySwitch(hdmiDeviceInfo)) {
            return;
        }
        this.mHdmiControlService.invokeDeviceEventListeners(hdmiDeviceInfo, i);
    }

    public final void updateCecDevice(HdmiDeviceInfo hdmiDeviceInfo) {
        assertRunOnServiceThread();
        HdmiDeviceInfo addDeviceInfo = addDeviceInfo(hdmiDeviceInfo);
        if (hdmiDeviceInfo.getPhysicalAddress() == 65535) {
            return;
        }
        if (addDeviceInfo == null || addDeviceInfo.getPhysicalAddress() == 65535) {
            invokeDeviceEventListener(hdmiDeviceInfo, 1);
        } else if (addDeviceInfo.equals(hdmiDeviceInfo)) {
        } else {
            invokeDeviceEventListener(hdmiDeviceInfo, 3);
        }
    }

    public final void updateSafeDeviceInfoList() {
        assertRunOnServiceThread();
        List<HdmiDeviceInfo> sparseArrayToList = HdmiUtils.sparseArrayToList(this.mDeviceInfos);
        List<HdmiDeviceInfo> inputDevices = getInputDevices();
        this.mSafeAllDeviceInfos = sparseArrayToList;
        this.mSafeExternalInputs = inputDevices;
    }

    public List<HdmiDeviceInfo> getDeviceInfoList(boolean z) {
        assertRunOnServiceThread();
        if (z) {
            return HdmiUtils.sparseArrayToList(this.mDeviceInfos);
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mDeviceInfos.size(); i++) {
            HdmiDeviceInfo valueAt = this.mDeviceInfos.valueAt(i);
            if (!isLocalDeviceAddress(valueAt.getLogicalAddress())) {
                arrayList.add(valueAt);
            }
        }
        return arrayList;
    }

    @GuardedBy({"mLock"})
    public List<HdmiDeviceInfo> getSafeExternalInputsLocked() {
        return this.mSafeExternalInputs;
    }

    public final List<HdmiDeviceInfo> getInputDevices() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mDeviceInfos.size(); i++) {
            HdmiDeviceInfo valueAt = this.mDeviceInfos.valueAt(i);
            if (!isLocalDeviceAddress(valueAt.getLogicalAddress()) && valueAt.isSourceType() && !hideDevicesBehindLegacySwitch(valueAt)) {
                arrayList.add(valueAt);
            }
        }
        return arrayList;
    }

    public final boolean hideDevicesBehindLegacySwitch(HdmiDeviceInfo hdmiDeviceInfo) {
        return (!isLocalDeviceAddress(0) || isConnectedToCecSwitch(hdmiDeviceInfo.getPhysicalAddress(), getCecSwitches()) || hdmiDeviceInfo.getPhysicalAddress() == 65535) ? false : true;
    }

    public final void removeCecDevice(HdmiCecLocalDevice hdmiCecLocalDevice, int i) {
        assertRunOnServiceThread();
        HdmiDeviceInfo removeDeviceInfo = removeDeviceInfo(HdmiDeviceInfo.idForCecDevice(i));
        this.mHdmiControlService.checkAndUpdateAbsoluteVolumeControlState();
        hdmiCecLocalDevice.mCecMessageCache.flushMessagesFrom(i);
        if (removeDeviceInfo.getPhysicalAddress() == 65535) {
            return;
        }
        invokeDeviceEventListener(removeDeviceInfo, 2);
    }

    public void updateDevicePowerStatus(int i, int i2) {
        HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(i);
        if (cecDeviceInfo == null) {
            Slog.w("HdmiCecNetwork", "Can not update power status of non-existing device:" + i);
        } else if (cecDeviceInfo.getDevicePowerStatus() == i2) {
        } else {
            updateCecDevice(cecDeviceInfo.toBuilder().setDevicePowerStatus(i2).build());
        }
    }

    public boolean isConnectedToArcPort(int i) {
        int physicalAddressToPortId = physicalAddressToPortId(i);
        if (physicalAddressToPortId == -1 || physicalAddressToPortId == 0) {
            return false;
        }
        return this.mPortInfoMap.get(physicalAddressToPortId).isArcSupported();
    }

    @VisibleForTesting
    public void initPortInfo() {
        assertRunOnServiceThread();
        HdmiCecController hdmiCecController = this.mHdmiCecController;
        HdmiPortInfo[] portInfos = hdmiCecController != null ? hdmiCecController.getPortInfos() : null;
        if (portInfos == null) {
            return;
        }
        SparseArray sparseArray = new SparseArray();
        SparseIntArray sparseIntArray = new SparseIntArray();
        SparseArray sparseArray2 = new SparseArray();
        for (HdmiPortInfo hdmiPortInfo : portInfos) {
            sparseIntArray.put(hdmiPortInfo.getAddress(), hdmiPortInfo.getId());
            sparseArray.put(hdmiPortInfo.getId(), hdmiPortInfo);
            sparseArray2.put(hdmiPortInfo.getId(), HdmiDeviceInfo.hardwarePort(hdmiPortInfo.getAddress(), hdmiPortInfo.getId()));
        }
        this.mPortIdMap = new UnmodifiableSparseIntArray(sparseIntArray);
        this.mPortInfoMap = new UnmodifiableSparseArray<>(sparseArray);
        this.mPortDeviceMap = new UnmodifiableSparseArray<>(sparseArray2);
        HdmiMhlControllerStub hdmiMhlControllerStub = this.mHdmiMhlController;
        if (hdmiMhlControllerStub == null) {
            return;
        }
        HdmiPortInfo[] portInfos2 = hdmiMhlControllerStub.getPortInfos();
        ArraySet arraySet = new ArraySet(portInfos2.length);
        for (HdmiPortInfo hdmiPortInfo2 : portInfos2) {
            if (hdmiPortInfo2.isMhlSupported()) {
                arraySet.add(Integer.valueOf(hdmiPortInfo2.getId()));
            }
        }
        if (arraySet.isEmpty()) {
            setPortInfo(Collections.unmodifiableList(Arrays.asList(portInfos)));
            return;
        }
        ArrayList arrayList = new ArrayList(portInfos.length);
        for (HdmiPortInfo hdmiPortInfo3 : portInfos) {
            if (arraySet.contains(Integer.valueOf(hdmiPortInfo3.getId()))) {
                arrayList.add(new HdmiPortInfo.Builder(hdmiPortInfo3.getId(), hdmiPortInfo3.getType(), hdmiPortInfo3.getAddress()).setCecSupported(hdmiPortInfo3.isCecSupported()).setMhlSupported(true).setArcSupported(hdmiPortInfo3.isArcSupported()).setEarcSupported(hdmiPortInfo3.isEarcSupported()).build());
            } else {
                arrayList.add(hdmiPortInfo3);
            }
        }
        setPortInfo(Collections.unmodifiableList(arrayList));
    }

    public boolean isInDeviceList(int i, int i2) {
        assertRunOnServiceThread();
        HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(i);
        return cecDeviceInfo != null && cecDeviceInfo.getPhysicalAddress() == i2;
    }

    public void handleCecMessage(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int source = hdmiCecMessage.getSource();
        if (getCecDeviceInfo(source) == null) {
            addCecDevice(HdmiDeviceInfo.cecDeviceBuilder().setLogicalAddress(source).setDisplayName(HdmiUtils.getDefaultDeviceName(source)).setDeviceType(logicalAddressToDeviceType(source)).build());
        }
        if (hdmiCecMessage instanceof ReportFeaturesMessage) {
            handleReportFeatures((ReportFeaturesMessage) hdmiCecMessage);
        }
        int opcode = hdmiCecMessage.getOpcode();
        if (opcode == 0) {
            handleFeatureAbort(hdmiCecMessage);
        } else if (opcode == 71) {
            handleSetOsdName(hdmiCecMessage);
        } else if (opcode == 132) {
            handleReportPhysicalAddress(hdmiCecMessage);
        } else if (opcode == 135) {
            handleDeviceVendorId(hdmiCecMessage);
        } else if (opcode == 144) {
            handleReportPowerStatus(hdmiCecMessage);
        } else if (opcode != 158) {
        } else {
            handleCecVersion(hdmiCecMessage);
        }
    }

    public final void handleReportFeatures(ReportFeaturesMessage reportFeaturesMessage) {
        assertRunOnServiceThread();
        updateCecDevice(getCecDeviceInfo(reportFeaturesMessage.getSource()).toBuilder().setCecVersion(reportFeaturesMessage.getCecVersion()).updateDeviceFeatures(reportFeaturesMessage.getDeviceFeatures()).build());
        this.mHdmiControlService.checkAndUpdateAbsoluteVolumeControlState();
    }

    public final void handleFeatureAbort(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        if (hdmiCecMessage.getParams().length < 2) {
            return;
        }
        int i = hdmiCecMessage.getParams()[0] & 255;
        int i2 = hdmiCecMessage.getParams()[1] & 255;
        if (i == 115) {
            int i3 = i2 == 0 ? 0 : 2;
            HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(hdmiCecMessage.getSource());
            updateCecDevice(cecDeviceInfo.toBuilder().updateDeviceFeatures(cecDeviceInfo.getDeviceFeatures().toBuilder().setSetAudioVolumeLevelSupport(i3).build()).build());
            this.mHdmiControlService.checkAndUpdateAbsoluteVolumeControlState();
        }
    }

    public final void handleCecVersion(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        updateDeviceCecVersion(hdmiCecMessage.getSource(), Byte.toUnsignedInt(hdmiCecMessage.getParams()[0]));
    }

    public final void handleReportPhysicalAddress(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int source = hdmiCecMessage.getSource();
        int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams());
        byte b = hdmiCecMessage.getParams()[2];
        if (updateCecSwitchInfo(source, b, twoBytesToInt)) {
            return;
        }
        HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(source);
        if (cecDeviceInfo == null) {
            Slog.i("HdmiCecNetwork", "Unknown source device info for <Report Physical Address> " + hdmiCecMessage);
            return;
        }
        updateCecDevice(cecDeviceInfo.toBuilder().setPhysicalAddress(twoBytesToInt).setPortId(physicalAddressToPortId(twoBytesToInt)).setDeviceType(b).build());
    }

    public final void handleReportPowerStatus(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        updateDevicePowerStatus(hdmiCecMessage.getSource(), hdmiCecMessage.getParams()[0] & 255);
        if (hdmiCecMessage.getDestination() == 15) {
            updateDeviceCecVersion(hdmiCecMessage.getSource(), 6);
        }
    }

    public final void updateDeviceCecVersion(int i, int i2) {
        assertRunOnServiceThread();
        HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(i);
        if (cecDeviceInfo == null) {
            Slog.w("HdmiCecNetwork", "Can not update CEC version of non-existing device:" + i);
        } else if (cecDeviceInfo.getCecVersion() == i2) {
        } else {
            updateCecDevice(cecDeviceInfo.toBuilder().setCecVersion(i2).build());
        }
    }

    public final void handleSetOsdName(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(hdmiCecMessage.getSource());
        if (cecDeviceInfo == null) {
            Slog.i("HdmiCecNetwork", "No source device info for <Set Osd Name>." + hdmiCecMessage);
            return;
        }
        try {
            String str = new String(hdmiCecMessage.getParams(), "US-ASCII");
            if (cecDeviceInfo.getDisplayName() != null && cecDeviceInfo.getDisplayName().equals(str)) {
                Slog.d("HdmiCecNetwork", "Ignore incoming <Set Osd Name> having same osd name:" + hdmiCecMessage);
                return;
            }
            Slog.d("HdmiCecNetwork", "Updating device OSD name from " + cecDeviceInfo.getDisplayName() + " to " + str);
            updateCecDevice(cecDeviceInfo.toBuilder().setDisplayName(str).build());
        } catch (UnsupportedEncodingException e) {
            Slog.e("HdmiCecNetwork", "Invalid <Set Osd Name> request:" + hdmiCecMessage, e);
        }
    }

    public final void handleDeviceVendorId(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int source = hdmiCecMessage.getSource();
        int threeBytesToInt = HdmiUtils.threeBytesToInt(hdmiCecMessage.getParams());
        HdmiDeviceInfo cecDeviceInfo = getCecDeviceInfo(source);
        if (cecDeviceInfo == null) {
            Slog.i("HdmiCecNetwork", "Unknown source device info for <Device Vendor ID> " + hdmiCecMessage);
            return;
        }
        updateCecDevice(cecDeviceInfo.toBuilder().setVendorId(threeBytesToInt).build());
    }

    public void addCecSwitch(int i) {
        this.mCecSwitches.add(Integer.valueOf(i));
    }

    public ArraySet<Integer> getCecSwitches() {
        return this.mCecSwitches;
    }

    public void removeCecSwitches(int i) {
        Iterator<Integer> it = this.mCecSwitches.iterator();
        while (it.hasNext()) {
            int physicalAddressToPortId = physicalAddressToPortId(it.next().intValue());
            if (physicalAddressToPortId == i || physicalAddressToPortId == -1) {
                it.remove();
            }
        }
    }

    public void removeDevicesConnectedToPort(int i) {
        removeCecSwitches(i);
        ArrayList<Integer> arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.mDeviceInfos.size(); i2++) {
            int keyAt = this.mDeviceInfos.keyAt(i2);
            int physicalAddressToPortId = physicalAddressToPortId(this.mDeviceInfos.get(keyAt).getPhysicalAddress());
            if (physicalAddressToPortId == i || physicalAddressToPortId == -1) {
                arrayList.add(Integer.valueOf(keyAt));
            }
        }
        for (Integer num : arrayList) {
            removeDeviceInfo(num.intValue());
        }
    }

    public boolean updateCecSwitchInfo(int i, int i2, int i3) {
        if (i == 15 && i2 == 6) {
            this.mCecSwitches.add(Integer.valueOf(i3));
            updateSafeDeviceInfoList();
            return true;
        } else if (i2 == 5) {
            this.mCecSwitches.add(Integer.valueOf(i3));
            return false;
        } else {
            return false;
        }
    }

    @GuardedBy({"mLock"})
    public List<HdmiDeviceInfo> getSafeCecDevicesLocked() {
        ArrayList arrayList = new ArrayList();
        for (HdmiDeviceInfo hdmiDeviceInfo : this.mSafeAllDeviceInfos) {
            if (!isLocalDeviceAddress(hdmiDeviceInfo.getLogicalAddress())) {
                arrayList.add(hdmiDeviceInfo);
            }
        }
        return arrayList;
    }

    public HdmiDeviceInfo getSafeCecDeviceInfo(int i) {
        for (HdmiDeviceInfo hdmiDeviceInfo : this.mSafeAllDeviceInfos) {
            if (hdmiDeviceInfo.isCecDevice() && hdmiDeviceInfo.getLogicalAddress() == i) {
                return hdmiDeviceInfo;
            }
        }
        return null;
    }

    public final HdmiDeviceInfo getDeviceInfoByPath(int i) {
        assertRunOnServiceThread();
        for (HdmiDeviceInfo hdmiDeviceInfo : getDeviceInfoList(false)) {
            if (hdmiDeviceInfo.getPhysicalAddress() == i) {
                return hdmiDeviceInfo;
            }
        }
        return null;
    }

    public HdmiDeviceInfo getSafeDeviceInfoByPath(int i) {
        for (HdmiDeviceInfo hdmiDeviceInfo : this.mSafeAllDeviceInfos) {
            if (hdmiDeviceInfo.getPhysicalAddress() == i) {
                return hdmiDeviceInfo;
            }
        }
        return null;
    }

    public int getPhysicalAddress() {
        return this.mHdmiCecController.getPhysicalAddress();
    }

    public void clearDeviceList() {
        assertRunOnServiceThread();
        for (HdmiDeviceInfo hdmiDeviceInfo : HdmiUtils.sparseArrayToList(this.mDeviceInfos)) {
            if (hdmiDeviceInfo.getPhysicalAddress() != getPhysicalAddress() && hdmiDeviceInfo.getPhysicalAddress() != 65535) {
                invokeDeviceEventListener(hdmiDeviceInfo, 2);
            }
        }
        this.mDeviceInfos.clear();
        updateSafeDeviceInfoList();
    }

    public HdmiPortInfo getPortInfo(int i) {
        return this.mPortInfoMap.get(i, null);
    }

    public int portIdToPath(int i) {
        HdmiPortInfo portInfo = getPortInfo(i);
        if (portInfo == null) {
            Slog.e("HdmiCecNetwork", "Cannot find the port info: " + i);
            return GnssNative.GNSS_AIDING_TYPE_ALL;
        }
        return portInfo.getAddress();
    }

    public int physicalAddressToPortId(int i) {
        int physicalAddress = getPhysicalAddress();
        if (i == physicalAddress) {
            return 0;
        }
        int i2 = 61440;
        int i3 = physicalAddress;
        int i4 = 61440;
        while (i3 != 0) {
            i3 = physicalAddress & i4;
            i2 |= i4;
            i4 >>= 4;
        }
        return this.mPortIdMap.get(i & i2, -1);
    }

    public List<HdmiPortInfo> getPortInfo() {
        return this.mPortInfo;
    }

    public void setPortInfo(List<HdmiPortInfo> list) {
        this.mPortInfo = list;
    }

    public final boolean isLocalDeviceAddress(int i) {
        for (int i2 = 0; i2 < this.mLocalDevices.size(); i2++) {
            if (this.mLocalDevices.get(this.mLocalDevices.keyAt(i2)).getDeviceInfo().getLogicalAddress() == i) {
                return true;
            }
        }
        return false;
    }

    public final void assertRunOnServiceThread() {
        if (Looper.myLooper() != this.mHandler.getLooper()) {
            throw new IllegalStateException("Should run on service thread.");
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("HDMI CEC Network");
        indentingPrintWriter.increaseIndent();
        HdmiUtils.dumpIterable(indentingPrintWriter, "mPortInfo:", this.mPortInfo);
        for (int i = 0; i < this.mLocalDevices.size(); i++) {
            indentingPrintWriter.println("HdmiCecLocalDevice #" + this.mLocalDevices.keyAt(i) + XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            this.mLocalDevices.valueAt(i).dump(indentingPrintWriter);
            indentingPrintWriter.println("Active Source history:");
            indentingPrintWriter.increaseIndent();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Iterator<HdmiCecController.Dumpable> it = this.mLocalDevices.valueAt(i).getActiveSourceHistory().iterator();
            while (it.hasNext()) {
                it.next().dump(indentingPrintWriter, simpleDateFormat);
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.decreaseIndent();
        }
        HdmiUtils.dumpIterable(indentingPrintWriter, "mDeviceInfos:", this.mSafeAllDeviceInfos);
        indentingPrintWriter.decreaseIndent();
    }
}
