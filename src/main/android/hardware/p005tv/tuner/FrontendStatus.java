package android.hardware.p005tv.tuner;

import android.content.Intent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.hardware.tv.tuner.FrontendStatus */
/* loaded from: classes2.dex */
public final class FrontendStatus implements Parcelable {
    public static final Parcelable.Creator<FrontendStatus> CREATOR = new Parcelable.Creator<FrontendStatus>() { // from class: android.hardware.tv.tuner.FrontendStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendStatus createFromParcel(Parcel _aidl_source) {
            return new FrontendStatus(_aidl_source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FrontendStatus[] newArray(int _aidl_size) {
            return new FrontendStatus[_aidl_size];
        }
    };
    public static final int agc = 14;
    public static final int allPlpInfo = 41;
    public static final int bandwidth = 25;
    public static final int ber = 2;
    public static final int bers = 23;
    public static final int codeRates = 24;
    public static final int dvbtCellIds = 40;
    public static final int freqOffset = 18;
    public static final int hierarchy = 19;
    public static final int innerFec = 8;
    public static final int interleaving = 30;
    public static final int interval = 26;
    public static final int inversion = 10;
    public static final int iptvAverageJitterMs = 46;
    public static final int iptvContentUrl = 42;
    public static final int iptvPacketsLost = 44;
    public static final int iptvPacketsReceived = 43;
    public static final int iptvWorstJitterMs = 45;
    public static final int isDemodLocked = 0;
    public static final int isEWBS = 13;
    public static final int isLayerError = 16;
    public static final int isLinear = 35;
    public static final int isLnaOn = 15;
    public static final int isMiso = 34;
    public static final int isRfLocked = 20;
    public static final int isShortFrames = 36;
    public static final int isdbtMode = 37;
    public static final int isdbtSegment = 31;
    public static final int lnbVoltage = 11;
    public static final int mer = 17;
    public static final int modulationStatus = 9;
    public static final int modulations = 22;
    public static final int partialReceptionFlag = 38;
    public static final int per = 3;
    public static final int plpId = 12;
    public static final int plpInfo = 21;
    public static final int preBer = 4;
    public static final int rollOff = 33;
    public static final int signalQuality = 5;
    public static final int signalStrength = 6;
    public static final int snr = 1;
    public static final int streamIdList = 39;
    public static final int symbolRate = 7;
    public static final int systemId = 29;
    public static final int transmissionMode = 27;
    public static final int tsDataRate = 32;
    public static final int uec = 28;
    private int _tag;
    private Object _value;

    /* renamed from: android.hardware.tv.tuner.FrontendStatus$Tag */
    /* loaded from: classes2.dex */
    public @interface Tag {
        public static final int agc = 14;
        public static final int allPlpInfo = 41;
        public static final int bandwidth = 25;
        public static final int ber = 2;
        public static final int bers = 23;
        public static final int codeRates = 24;
        public static final int dvbtCellIds = 40;
        public static final int freqOffset = 18;
        public static final int hierarchy = 19;
        public static final int innerFec = 8;
        public static final int interleaving = 30;
        public static final int interval = 26;
        public static final int inversion = 10;
        public static final int iptvAverageJitterMs = 46;
        public static final int iptvContentUrl = 42;
        public static final int iptvPacketsLost = 44;
        public static final int iptvPacketsReceived = 43;
        public static final int iptvWorstJitterMs = 45;
        public static final int isDemodLocked = 0;
        public static final int isEWBS = 13;
        public static final int isLayerError = 16;
        public static final int isLinear = 35;
        public static final int isLnaOn = 15;
        public static final int isMiso = 34;
        public static final int isRfLocked = 20;
        public static final int isShortFrames = 36;
        public static final int isdbtMode = 37;
        public static final int isdbtSegment = 31;
        public static final int lnbVoltage = 11;
        public static final int mer = 17;
        public static final int modulationStatus = 9;
        public static final int modulations = 22;
        public static final int partialReceptionFlag = 38;
        public static final int per = 3;
        public static final int plpId = 12;
        public static final int plpInfo = 21;
        public static final int preBer = 4;
        public static final int rollOff = 33;
        public static final int signalQuality = 5;
        public static final int signalStrength = 6;
        public static final int snr = 1;
        public static final int streamIdList = 39;
        public static final int symbolRate = 7;
        public static final int systemId = 29;
        public static final int transmissionMode = 27;
        public static final int tsDataRate = 32;
        public static final int uec = 28;
    }

    public FrontendStatus() {
        this._tag = 0;
        this._value = false;
    }

    private FrontendStatus(Parcel _aidl_parcel) {
        readFromParcel(_aidl_parcel);
    }

    private FrontendStatus(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }

    public int getTag() {
        return this._tag;
    }

    public static FrontendStatus isDemodLocked(boolean _value) {
        return new FrontendStatus(0, Boolean.valueOf(_value));
    }

    public boolean getIsDemodLocked() {
        _assertTag(0);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsDemodLocked(boolean _value) {
        _set(0, Boolean.valueOf(_value));
    }

    public static FrontendStatus snr(int _value) {
        return new FrontendStatus(1, Integer.valueOf(_value));
    }

    public int getSnr() {
        _assertTag(1);
        return ((Integer) this._value).intValue();
    }

    public void setSnr(int _value) {
        _set(1, Integer.valueOf(_value));
    }

    public static FrontendStatus ber(int _value) {
        return new FrontendStatus(2, Integer.valueOf(_value));
    }

    public int getBer() {
        _assertTag(2);
        return ((Integer) this._value).intValue();
    }

    public void setBer(int _value) {
        _set(2, Integer.valueOf(_value));
    }

    public static FrontendStatus per(int _value) {
        return new FrontendStatus(3, Integer.valueOf(_value));
    }

    public int getPer() {
        _assertTag(3);
        return ((Integer) this._value).intValue();
    }

    public void setPer(int _value) {
        _set(3, Integer.valueOf(_value));
    }

    public static FrontendStatus preBer(int _value) {
        return new FrontendStatus(4, Integer.valueOf(_value));
    }

    public int getPreBer() {
        _assertTag(4);
        return ((Integer) this._value).intValue();
    }

    public void setPreBer(int _value) {
        _set(4, Integer.valueOf(_value));
    }

    public static FrontendStatus signalQuality(int _value) {
        return new FrontendStatus(5, Integer.valueOf(_value));
    }

    public int getSignalQuality() {
        _assertTag(5);
        return ((Integer) this._value).intValue();
    }

    public void setSignalQuality(int _value) {
        _set(5, Integer.valueOf(_value));
    }

    public static FrontendStatus signalStrength(int _value) {
        return new FrontendStatus(6, Integer.valueOf(_value));
    }

    public int getSignalStrength() {
        _assertTag(6);
        return ((Integer) this._value).intValue();
    }

    public void setSignalStrength(int _value) {
        _set(6, Integer.valueOf(_value));
    }

    public static FrontendStatus symbolRate(int _value) {
        return new FrontendStatus(7, Integer.valueOf(_value));
    }

    public int getSymbolRate() {
        _assertTag(7);
        return ((Integer) this._value).intValue();
    }

    public void setSymbolRate(int _value) {
        _set(7, Integer.valueOf(_value));
    }

    public static FrontendStatus innerFec(long _value) {
        return new FrontendStatus(8, Long.valueOf(_value));
    }

    public long getInnerFec() {
        _assertTag(8);
        return ((Long) this._value).longValue();
    }

    public void setInnerFec(long _value) {
        _set(8, Long.valueOf(_value));
    }

    public static FrontendStatus modulationStatus(FrontendModulationStatus _value) {
        return new FrontendStatus(9, _value);
    }

    public FrontendModulationStatus getModulationStatus() {
        _assertTag(9);
        return (FrontendModulationStatus) this._value;
    }

    public void setModulationStatus(FrontendModulationStatus _value) {
        _set(9, _value);
    }

    public static FrontendStatus inversion(int _value) {
        return new FrontendStatus(10, Integer.valueOf(_value));
    }

    public int getInversion() {
        _assertTag(10);
        return ((Integer) this._value).intValue();
    }

    public void setInversion(int _value) {
        _set(10, Integer.valueOf(_value));
    }

    public static FrontendStatus lnbVoltage(int _value) {
        return new FrontendStatus(11, Integer.valueOf(_value));
    }

    public int getLnbVoltage() {
        _assertTag(11);
        return ((Integer) this._value).intValue();
    }

    public void setLnbVoltage(int _value) {
        _set(11, Integer.valueOf(_value));
    }

    public static FrontendStatus plpId(int _value) {
        return new FrontendStatus(12, Integer.valueOf(_value));
    }

    public int getPlpId() {
        _assertTag(12);
        return ((Integer) this._value).intValue();
    }

    public void setPlpId(int _value) {
        _set(12, Integer.valueOf(_value));
    }

    public static FrontendStatus isEWBS(boolean _value) {
        return new FrontendStatus(13, Boolean.valueOf(_value));
    }

    public boolean getIsEWBS() {
        _assertTag(13);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsEWBS(boolean _value) {
        _set(13, Boolean.valueOf(_value));
    }

    public static FrontendStatus agc(int _value) {
        return new FrontendStatus(14, Integer.valueOf(_value));
    }

    public int getAgc() {
        _assertTag(14);
        return ((Integer) this._value).intValue();
    }

    public void setAgc(int _value) {
        _set(14, Integer.valueOf(_value));
    }

    public static FrontendStatus isLnaOn(boolean _value) {
        return new FrontendStatus(15, Boolean.valueOf(_value));
    }

    public boolean getIsLnaOn() {
        _assertTag(15);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsLnaOn(boolean _value) {
        _set(15, Boolean.valueOf(_value));
    }

    public static FrontendStatus isLayerError(boolean[] _value) {
        return new FrontendStatus(16, _value);
    }

    public boolean[] getIsLayerError() {
        _assertTag(16);
        return (boolean[]) this._value;
    }

    public void setIsLayerError(boolean[] _value) {
        _set(16, _value);
    }

    public static FrontendStatus mer(int _value) {
        return new FrontendStatus(17, Integer.valueOf(_value));
    }

    public int getMer() {
        _assertTag(17);
        return ((Integer) this._value).intValue();
    }

    public void setMer(int _value) {
        _set(17, Integer.valueOf(_value));
    }

    public static FrontendStatus freqOffset(long _value) {
        return new FrontendStatus(18, Long.valueOf(_value));
    }

    public long getFreqOffset() {
        _assertTag(18);
        return ((Long) this._value).longValue();
    }

    public void setFreqOffset(long _value) {
        _set(18, Long.valueOf(_value));
    }

    public static FrontendStatus hierarchy(int _value) {
        return new FrontendStatus(19, Integer.valueOf(_value));
    }

    public int getHierarchy() {
        _assertTag(19);
        return ((Integer) this._value).intValue();
    }

    public void setHierarchy(int _value) {
        _set(19, Integer.valueOf(_value));
    }

    public static FrontendStatus isRfLocked(boolean _value) {
        return new FrontendStatus(20, Boolean.valueOf(_value));
    }

    public boolean getIsRfLocked() {
        _assertTag(20);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsRfLocked(boolean _value) {
        _set(20, Boolean.valueOf(_value));
    }

    public static FrontendStatus plpInfo(FrontendStatusAtsc3PlpInfo[] _value) {
        return new FrontendStatus(21, _value);
    }

    public FrontendStatusAtsc3PlpInfo[] getPlpInfo() {
        _assertTag(21);
        return (FrontendStatusAtsc3PlpInfo[]) this._value;
    }

    public void setPlpInfo(FrontendStatusAtsc3PlpInfo[] _value) {
        _set(21, _value);
    }

    public static FrontendStatus modulations(FrontendModulation[] _value) {
        return new FrontendStatus(22, _value);
    }

    public FrontendModulation[] getModulations() {
        _assertTag(22);
        return (FrontendModulation[]) this._value;
    }

    public void setModulations(FrontendModulation[] _value) {
        _set(22, _value);
    }

    public static FrontendStatus bers(int[] _value) {
        return new FrontendStatus(23, _value);
    }

    public int[] getBers() {
        _assertTag(23);
        return (int[]) this._value;
    }

    public void setBers(int[] _value) {
        _set(23, _value);
    }

    public static FrontendStatus codeRates(long[] _value) {
        return new FrontendStatus(24, _value);
    }

    public long[] getCodeRates() {
        _assertTag(24);
        return (long[]) this._value;
    }

    public void setCodeRates(long[] _value) {
        _set(24, _value);
    }

    public static FrontendStatus bandwidth(FrontendBandwidth _value) {
        return new FrontendStatus(25, _value);
    }

    public FrontendBandwidth getBandwidth() {
        _assertTag(25);
        return (FrontendBandwidth) this._value;
    }

    public void setBandwidth(FrontendBandwidth _value) {
        _set(25, _value);
    }

    public static FrontendStatus interval(FrontendGuardInterval _value) {
        return new FrontendStatus(26, _value);
    }

    public FrontendGuardInterval getInterval() {
        _assertTag(26);
        return (FrontendGuardInterval) this._value;
    }

    public void setInterval(FrontendGuardInterval _value) {
        _set(26, _value);
    }

    public static FrontendStatus transmissionMode(FrontendTransmissionMode _value) {
        return new FrontendStatus(27, _value);
    }

    public FrontendTransmissionMode getTransmissionMode() {
        _assertTag(27);
        return (FrontendTransmissionMode) this._value;
    }

    public void setTransmissionMode(FrontendTransmissionMode _value) {
        _set(27, _value);
    }

    public static FrontendStatus uec(int _value) {
        return new FrontendStatus(28, Integer.valueOf(_value));
    }

    public int getUec() {
        _assertTag(28);
        return ((Integer) this._value).intValue();
    }

    public void setUec(int _value) {
        _set(28, Integer.valueOf(_value));
    }

    public static FrontendStatus systemId(int _value) {
        return new FrontendStatus(29, Integer.valueOf(_value));
    }

    public int getSystemId() {
        _assertTag(29);
        return ((Integer) this._value).intValue();
    }

    public void setSystemId(int _value) {
        _set(29, Integer.valueOf(_value));
    }

    public static FrontendStatus interleaving(FrontendInterleaveMode[] _value) {
        return new FrontendStatus(30, _value);
    }

    public FrontendInterleaveMode[] getInterleaving() {
        _assertTag(30);
        return (FrontendInterleaveMode[]) this._value;
    }

    public void setInterleaving(FrontendInterleaveMode[] _value) {
        _set(30, _value);
    }

    public static FrontendStatus isdbtSegment(int[] _value) {
        return new FrontendStatus(31, _value);
    }

    public int[] getIsdbtSegment() {
        _assertTag(31);
        return (int[]) this._value;
    }

    public void setIsdbtSegment(int[] _value) {
        _set(31, _value);
    }

    public static FrontendStatus tsDataRate(int[] _value) {
        return new FrontendStatus(32, _value);
    }

    public int[] getTsDataRate() {
        _assertTag(32);
        return (int[]) this._value;
    }

    public void setTsDataRate(int[] _value) {
        _set(32, _value);
    }

    public static FrontendStatus rollOff(FrontendRollOff _value) {
        return new FrontendStatus(33, _value);
    }

    public FrontendRollOff getRollOff() {
        _assertTag(33);
        return (FrontendRollOff) this._value;
    }

    public void setRollOff(FrontendRollOff _value) {
        _set(33, _value);
    }

    public static FrontendStatus isMiso(boolean _value) {
        return new FrontendStatus(34, Boolean.valueOf(_value));
    }

    public boolean getIsMiso() {
        _assertTag(34);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsMiso(boolean _value) {
        _set(34, Boolean.valueOf(_value));
    }

    public static FrontendStatus isLinear(boolean _value) {
        return new FrontendStatus(35, Boolean.valueOf(_value));
    }

    public boolean getIsLinear() {
        _assertTag(35);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsLinear(boolean _value) {
        _set(35, Boolean.valueOf(_value));
    }

    public static FrontendStatus isShortFrames(boolean _value) {
        return new FrontendStatus(36, Boolean.valueOf(_value));
    }

    public boolean getIsShortFrames() {
        _assertTag(36);
        return ((Boolean) this._value).booleanValue();
    }

    public void setIsShortFrames(boolean _value) {
        _set(36, Boolean.valueOf(_value));
    }

    public static FrontendStatus isdbtMode(int _value) {
        return new FrontendStatus(37, Integer.valueOf(_value));
    }

    public int getIsdbtMode() {
        _assertTag(37);
        return ((Integer) this._value).intValue();
    }

    public void setIsdbtMode(int _value) {
        _set(37, Integer.valueOf(_value));
    }

    public static FrontendStatus partialReceptionFlag(int _value) {
        return new FrontendStatus(38, Integer.valueOf(_value));
    }

    public int getPartialReceptionFlag() {
        _assertTag(38);
        return ((Integer) this._value).intValue();
    }

    public void setPartialReceptionFlag(int _value) {
        _set(38, Integer.valueOf(_value));
    }

    public static FrontendStatus streamIdList(int[] _value) {
        return new FrontendStatus(39, _value);
    }

    public int[] getStreamIdList() {
        _assertTag(39);
        return (int[]) this._value;
    }

    public void setStreamIdList(int[] _value) {
        _set(39, _value);
    }

    public static FrontendStatus dvbtCellIds(int[] _value) {
        return new FrontendStatus(40, _value);
    }

    public int[] getDvbtCellIds() {
        _assertTag(40);
        return (int[]) this._value;
    }

    public void setDvbtCellIds(int[] _value) {
        _set(40, _value);
    }

    public static FrontendStatus allPlpInfo(FrontendScanAtsc3PlpInfo[] _value) {
        return new FrontendStatus(41, _value);
    }

    public FrontendScanAtsc3PlpInfo[] getAllPlpInfo() {
        _assertTag(41);
        return (FrontendScanAtsc3PlpInfo[]) this._value;
    }

    public void setAllPlpInfo(FrontendScanAtsc3PlpInfo[] _value) {
        _set(41, _value);
    }

    public static FrontendStatus iptvContentUrl(String _value) {
        return new FrontendStatus(42, _value);
    }

    public String getIptvContentUrl() {
        _assertTag(42);
        return (String) this._value;
    }

    public void setIptvContentUrl(String _value) {
        _set(42, _value);
    }

    public static FrontendStatus iptvPacketsReceived(long _value) {
        return new FrontendStatus(43, Long.valueOf(_value));
    }

    public long getIptvPacketsReceived() {
        _assertTag(43);
        return ((Long) this._value).longValue();
    }

    public void setIptvPacketsReceived(long _value) {
        _set(43, Long.valueOf(_value));
    }

    public static FrontendStatus iptvPacketsLost(long _value) {
        return new FrontendStatus(44, Long.valueOf(_value));
    }

    public long getIptvPacketsLost() {
        _assertTag(44);
        return ((Long) this._value).longValue();
    }

    public void setIptvPacketsLost(long _value) {
        _set(44, Long.valueOf(_value));
    }

    public static FrontendStatus iptvWorstJitterMs(int _value) {
        return new FrontendStatus(45, Integer.valueOf(_value));
    }

    public int getIptvWorstJitterMs() {
        _assertTag(45);
        return ((Integer) this._value).intValue();
    }

    public void setIptvWorstJitterMs(int _value) {
        _set(45, Integer.valueOf(_value));
    }

    public static FrontendStatus iptvAverageJitterMs(int _value) {
        return new FrontendStatus(46, Integer.valueOf(_value));
    }

    public int getIptvAverageJitterMs() {
        _assertTag(46);
        return ((Integer) this._value).intValue();
    }

    public void setIptvAverageJitterMs(int _value) {
        _set(46, Integer.valueOf(_value));
    }

    @Override // android.p008os.Parcelable
    public final int getStability() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        _aidl_parcel.writeInt(this._tag);
        switch (this._tag) {
            case 0:
                _aidl_parcel.writeBoolean(getIsDemodLocked());
                return;
            case 1:
                _aidl_parcel.writeInt(getSnr());
                return;
            case 2:
                _aidl_parcel.writeInt(getBer());
                return;
            case 3:
                _aidl_parcel.writeInt(getPer());
                return;
            case 4:
                _aidl_parcel.writeInt(getPreBer());
                return;
            case 5:
                _aidl_parcel.writeInt(getSignalQuality());
                return;
            case 6:
                _aidl_parcel.writeInt(getSignalStrength());
                return;
            case 7:
                _aidl_parcel.writeInt(getSymbolRate());
                return;
            case 8:
                _aidl_parcel.writeLong(getInnerFec());
                return;
            case 9:
                _aidl_parcel.writeTypedObject(getModulationStatus(), _aidl_flag);
                return;
            case 10:
                _aidl_parcel.writeInt(getInversion());
                return;
            case 11:
                _aidl_parcel.writeInt(getLnbVoltage());
                return;
            case 12:
                _aidl_parcel.writeInt(getPlpId());
                return;
            case 13:
                _aidl_parcel.writeBoolean(getIsEWBS());
                return;
            case 14:
                _aidl_parcel.writeInt(getAgc());
                return;
            case 15:
                _aidl_parcel.writeBoolean(getIsLnaOn());
                return;
            case 16:
                _aidl_parcel.writeBooleanArray(getIsLayerError());
                return;
            case 17:
                _aidl_parcel.writeInt(getMer());
                return;
            case 18:
                _aidl_parcel.writeLong(getFreqOffset());
                return;
            case 19:
                _aidl_parcel.writeInt(getHierarchy());
                return;
            case 20:
                _aidl_parcel.writeBoolean(getIsRfLocked());
                return;
            case 21:
                _aidl_parcel.writeTypedArray(getPlpInfo(), _aidl_flag);
                return;
            case 22:
                _aidl_parcel.writeTypedArray(getModulations(), _aidl_flag);
                return;
            case 23:
                _aidl_parcel.writeIntArray(getBers());
                return;
            case 24:
                _aidl_parcel.writeLongArray(getCodeRates());
                return;
            case 25:
                _aidl_parcel.writeTypedObject(getBandwidth(), _aidl_flag);
                return;
            case 26:
                _aidl_parcel.writeTypedObject(getInterval(), _aidl_flag);
                return;
            case 27:
                _aidl_parcel.writeTypedObject(getTransmissionMode(), _aidl_flag);
                return;
            case 28:
                _aidl_parcel.writeInt(getUec());
                return;
            case 29:
                _aidl_parcel.writeInt(getSystemId());
                return;
            case 30:
                _aidl_parcel.writeTypedArray(getInterleaving(), _aidl_flag);
                return;
            case 31:
                _aidl_parcel.writeIntArray(getIsdbtSegment());
                return;
            case 32:
                _aidl_parcel.writeIntArray(getTsDataRate());
                return;
            case 33:
                _aidl_parcel.writeTypedObject(getRollOff(), _aidl_flag);
                return;
            case 34:
                _aidl_parcel.writeBoolean(getIsMiso());
                return;
            case 35:
                _aidl_parcel.writeBoolean(getIsLinear());
                return;
            case 36:
                _aidl_parcel.writeBoolean(getIsShortFrames());
                return;
            case 37:
                _aidl_parcel.writeInt(getIsdbtMode());
                return;
            case 38:
                _aidl_parcel.writeInt(getPartialReceptionFlag());
                return;
            case 39:
                _aidl_parcel.writeIntArray(getStreamIdList());
                return;
            case 40:
                _aidl_parcel.writeIntArray(getDvbtCellIds());
                return;
            case 41:
                _aidl_parcel.writeTypedArray(getAllPlpInfo(), _aidl_flag);
                return;
            case 42:
                _aidl_parcel.writeString(getIptvContentUrl());
                return;
            case 43:
                _aidl_parcel.writeLong(getIptvPacketsReceived());
                return;
            case 44:
                _aidl_parcel.writeLong(getIptvPacketsLost());
                return;
            case 45:
                _aidl_parcel.writeInt(getIptvWorstJitterMs());
                return;
            case 46:
                _aidl_parcel.writeInt(getIptvAverageJitterMs());
                return;
            default:
                return;
        }
    }

    public void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_tag = _aidl_parcel.readInt();
        switch (_aidl_tag) {
            case 0:
                boolean _aidl_value = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value));
                return;
            case 1:
                int _aidl_value2 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value2));
                return;
            case 2:
                int _aidl_value3 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value3));
                return;
            case 3:
                int _aidl_value4 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value4));
                return;
            case 4:
                int _aidl_value5 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value5));
                return;
            case 5:
                int _aidl_value6 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value6));
                return;
            case 6:
                int _aidl_value7 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value7));
                return;
            case 7:
                int _aidl_value8 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value8));
                return;
            case 8:
                long _aidl_value9 = _aidl_parcel.readLong();
                _set(_aidl_tag, Long.valueOf(_aidl_value9));
                return;
            case 9:
                FrontendModulationStatus _aidl_value10 = (FrontendModulationStatus) _aidl_parcel.readTypedObject(FrontendModulationStatus.CREATOR);
                _set(_aidl_tag, _aidl_value10);
                return;
            case 10:
                int _aidl_value11 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value11));
                return;
            case 11:
                int _aidl_value12 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value12));
                return;
            case 12:
                int _aidl_value13 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value13));
                return;
            case 13:
                boolean _aidl_value14 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value14));
                return;
            case 14:
                int _aidl_value15 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value15));
                return;
            case 15:
                boolean _aidl_value16 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value16));
                return;
            case 16:
                boolean[] _aidl_value17 = _aidl_parcel.createBooleanArray();
                _set(_aidl_tag, _aidl_value17);
                return;
            case 17:
                int _aidl_value18 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value18));
                return;
            case 18:
                long _aidl_value19 = _aidl_parcel.readLong();
                _set(_aidl_tag, Long.valueOf(_aidl_value19));
                return;
            case 19:
                int _aidl_value20 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value20));
                return;
            case 20:
                boolean _aidl_value21 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value21));
                return;
            case 21:
                FrontendStatusAtsc3PlpInfo[] _aidl_value22 = (FrontendStatusAtsc3PlpInfo[]) _aidl_parcel.createTypedArray(FrontendStatusAtsc3PlpInfo.CREATOR);
                _set(_aidl_tag, _aidl_value22);
                return;
            case 22:
                FrontendModulation[] _aidl_value23 = (FrontendModulation[]) _aidl_parcel.createTypedArray(FrontendModulation.CREATOR);
                _set(_aidl_tag, _aidl_value23);
                return;
            case 23:
                int[] _aidl_value24 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value24);
                return;
            case 24:
                long[] _aidl_value25 = _aidl_parcel.createLongArray();
                _set(_aidl_tag, _aidl_value25);
                return;
            case 25:
                FrontendBandwidth _aidl_value26 = (FrontendBandwidth) _aidl_parcel.readTypedObject(FrontendBandwidth.CREATOR);
                _set(_aidl_tag, _aidl_value26);
                return;
            case 26:
                FrontendGuardInterval _aidl_value27 = (FrontendGuardInterval) _aidl_parcel.readTypedObject(FrontendGuardInterval.CREATOR);
                _set(_aidl_tag, _aidl_value27);
                return;
            case 27:
                FrontendTransmissionMode _aidl_value28 = (FrontendTransmissionMode) _aidl_parcel.readTypedObject(FrontendTransmissionMode.CREATOR);
                _set(_aidl_tag, _aidl_value28);
                return;
            case 28:
                int _aidl_value29 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value29));
                return;
            case 29:
                int _aidl_value30 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value30));
                return;
            case 30:
                FrontendInterleaveMode[] _aidl_value31 = (FrontendInterleaveMode[]) _aidl_parcel.createTypedArray(FrontendInterleaveMode.CREATOR);
                _set(_aidl_tag, _aidl_value31);
                return;
            case 31:
                int[] _aidl_value32 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value32);
                return;
            case 32:
                int[] _aidl_value33 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value33);
                return;
            case 33:
                FrontendRollOff _aidl_value34 = (FrontendRollOff) _aidl_parcel.readTypedObject(FrontendRollOff.CREATOR);
                _set(_aidl_tag, _aidl_value34);
                return;
            case 34:
                boolean _aidl_value35 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value35));
                return;
            case 35:
                boolean _aidl_value36 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value36));
                return;
            case 36:
                boolean _aidl_value37 = _aidl_parcel.readBoolean();
                _set(_aidl_tag, Boolean.valueOf(_aidl_value37));
                return;
            case 37:
                int _aidl_value38 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value38));
                return;
            case 38:
                int _aidl_value39 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value39));
                return;
            case 39:
                int[] _aidl_value40 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value40);
                return;
            case 40:
                int[] _aidl_value41 = _aidl_parcel.createIntArray();
                _set(_aidl_tag, _aidl_value41);
                return;
            case 41:
                FrontendScanAtsc3PlpInfo[] _aidl_value42 = (FrontendScanAtsc3PlpInfo[]) _aidl_parcel.createTypedArray(FrontendScanAtsc3PlpInfo.CREATOR);
                _set(_aidl_tag, _aidl_value42);
                return;
            case 42:
                String _aidl_value43 = _aidl_parcel.readString();
                _set(_aidl_tag, _aidl_value43);
                return;
            case 43:
                long _aidl_value44 = _aidl_parcel.readLong();
                _set(_aidl_tag, Long.valueOf(_aidl_value44));
                return;
            case 44:
                long _aidl_value45 = _aidl_parcel.readLong();
                _set(_aidl_tag, Long.valueOf(_aidl_value45));
                return;
            case 45:
                int _aidl_value46 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value46));
                return;
            case 46:
                int _aidl_value47 = _aidl_parcel.readInt();
                _set(_aidl_tag, Integer.valueOf(_aidl_value47));
                return;
            default:
                throw new IllegalArgumentException("union: unknown tag: " + _aidl_tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        switch (getTag()) {
            case 9:
                int _mask = 0 | describeContents(getModulationStatus());
                return _mask;
            case 21:
                int _mask2 = 0 | describeContents(getPlpInfo());
                return _mask2;
            case 22:
                int _mask3 = 0 | describeContents(getModulations());
                return _mask3;
            case 25:
                int _mask4 = 0 | describeContents(getBandwidth());
                return _mask4;
            case 26:
                int _mask5 = 0 | describeContents(getInterval());
                return _mask5;
            case 27:
                int _mask6 = 0 | describeContents(getTransmissionMode());
                return _mask6;
            case 30:
                int _mask7 = 0 | describeContents(getInterleaving());
                return _mask7;
            case 33:
                int _mask8 = 0 | describeContents(getRollOff());
                return _mask8;
            case 41:
                int _mask9 = 0 | describeContents(getAllPlpInfo());
                return _mask9;
            default:
                return 0;
        }
    }

    private int describeContents(Object _v) {
        Object[] objArr;
        if (_v == null) {
            return 0;
        }
        if (_v instanceof Object[]) {
            int _mask = 0;
            for (Object o : (Object[]) _v) {
                _mask |= describeContents(o);
            }
            return _mask;
        } else if (!(_v instanceof Parcelable)) {
            return 0;
        } else {
            return ((Parcelable) _v).describeContents();
        }
    }

    private void _assertTag(int tag) {
        if (getTag() != tag) {
            throw new IllegalStateException("bad access: " + _tagString(tag) + ", " + _tagString(getTag()) + " is available.");
        }
    }

    private String _tagString(int _tag) {
        switch (_tag) {
            case 0:
                return "isDemodLocked";
            case 1:
                return "snr";
            case 2:
                return "ber";
            case 3:
                return "per";
            case 4:
                return "preBer";
            case 5:
                return "signalQuality";
            case 6:
                return "signalStrength";
            case 7:
                return "symbolRate";
            case 8:
                return "innerFec";
            case 9:
                return "modulationStatus";
            case 10:
                return "inversion";
            case 11:
                return "lnbVoltage";
            case 12:
                return "plpId";
            case 13:
                return "isEWBS";
            case 14:
                return "agc";
            case 15:
                return "isLnaOn";
            case 16:
                return "isLayerError";
            case 17:
                return "mer";
            case 18:
                return "freqOffset";
            case 19:
                return "hierarchy";
            case 20:
                return "isRfLocked";
            case 21:
                return "plpInfo";
            case 22:
                return "modulations";
            case 23:
                return "bers";
            case 24:
                return "codeRates";
            case 25:
                return "bandwidth";
            case 26:
                return "interval";
            case 27:
                return "transmissionMode";
            case 28:
                return "uec";
            case 29:
                return Intent.EXTRA_SYSTEM_ID;
            case 30:
                return "interleaving";
            case 31:
                return "isdbtSegment";
            case 32:
                return "tsDataRate";
            case 33:
                return "rollOff";
            case 34:
                return "isMiso";
            case 35:
                return "isLinear";
            case 36:
                return "isShortFrames";
            case 37:
                return "isdbtMode";
            case 38:
                return "partialReceptionFlag";
            case 39:
                return "streamIdList";
            case 40:
                return "dvbtCellIds";
            case 41:
                return "allPlpInfo";
            case 42:
                return "iptvContentUrl";
            case 43:
                return "iptvPacketsReceived";
            case 44:
                return "iptvPacketsLost";
            case 45:
                return "iptvWorstJitterMs";
            case 46:
                return "iptvAverageJitterMs";
            default:
                throw new IllegalStateException("unknown field: " + _tag);
        }
    }

    private void _set(int _tag, Object _value) {
        this._tag = _tag;
        this._value = _value;
    }
}
