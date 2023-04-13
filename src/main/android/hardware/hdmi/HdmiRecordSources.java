package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.util.Log;
@SystemApi
/* loaded from: classes2.dex */
public final class HdmiRecordSources {
    public static final int ANALOGUE_BROADCAST_TYPE_CABLE = 0;
    public static final int ANALOGUE_BROADCAST_TYPE_SATELLITE = 1;
    public static final int ANALOGUE_BROADCAST_TYPE_TERRESTRIAL = 2;
    public static final int BROADCAST_SYSTEM_NTSC_M = 3;
    public static final int BROADCAST_SYSTEM_PAL_BG = 0;
    public static final int BROADCAST_SYSTEM_PAL_DK = 8;
    public static final int BROADCAST_SYSTEM_PAL_I = 4;
    public static final int BROADCAST_SYSTEM_PAL_M = 2;
    public static final int BROADCAST_SYSTEM_PAL_OTHER_SYSTEM = 31;
    public static final int BROADCAST_SYSTEM_SECAM_BG = 6;
    public static final int BROADCAST_SYSTEM_SECAM_DK = 5;
    public static final int BROADCAST_SYSTEM_SECAM_L = 7;
    public static final int BROADCAST_SYSTEM_SECAM_LP = 1;
    private static final int CHANNEL_NUMBER_FORMAT_1_PART = 1;
    private static final int CHANNEL_NUMBER_FORMAT_2_PART = 2;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB = 0;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB_BS = 8;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB_CS = 9;
    public static final int DIGITAL_BROADCAST_TYPE_ARIB_T = 10;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC = 1;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC_CABLE = 16;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC_SATELLITE = 17;
    public static final int DIGITAL_BROADCAST_TYPE_ATSC_TERRESTRIAL = 18;
    public static final int DIGITAL_BROADCAST_TYPE_DVB = 2;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_C = 24;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_S = 25;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_S2 = 26;
    public static final int DIGITAL_BROADCAST_TYPE_DVB_T = 27;
    private static final int RECORD_SOURCE_TYPE_ANALOGUE_SERVICE = 3;
    private static final int RECORD_SOURCE_TYPE_DIGITAL_SERVICE = 2;
    private static final int RECORD_SOURCE_TYPE_EXTERNAL_PHYSICAL_ADDRESS = 5;
    private static final int RECORD_SOURCE_TYPE_EXTERNAL_PLUG = 4;
    private static final int RECORD_SOURCE_TYPE_OWN_SOURCE = 1;
    private static final String TAG = "HdmiRecordSources";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface DigitalServiceIdentification {
        int toByteArray(byte[] bArr, int i);
    }

    private HdmiRecordSources() {
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static abstract class RecordSource {
        final int mExtraDataSize;
        final int mSourceType;

        abstract int extraParamToByteArray(byte[] bArr, int i);

        /* JADX INFO: Access modifiers changed from: package-private */
        public RecordSource(int sourceType, int extraDataSize) {
            this.mSourceType = sourceType;
            this.mExtraDataSize = extraDataSize;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final int getDataSize(boolean includeType) {
            int i = this.mExtraDataSize;
            return includeType ? i + 1 : i;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final int toByteArray(boolean includeType, byte[] data, int index) {
            if (includeType) {
                data[index] = (byte) this.mSourceType;
                index++;
            }
            extraParamToByteArray(data, index);
            return getDataSize(includeType);
        }
    }

    public static OwnSource ofOwnSource() {
        return new OwnSource();
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class OwnSource extends RecordSource {
        private static final int EXTRA_DATA_SIZE = 0;

        private OwnSource() {
            super(1, 0);
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.RecordSource
        int extraParamToByteArray(byte[] data, int index) {
            return 0;
        }
    }

    /* loaded from: classes2.dex */
    public static final class AribData implements DigitalServiceIdentification {
        private final int mOriginalNetworkId;
        private final int mServiceId;
        private final int mTransportStreamId;

        public AribData(int transportStreamId, int serviceId, int originalNetworkId) {
            this.mTransportStreamId = transportStreamId;
            this.mServiceId = serviceId;
            this.mOriginalNetworkId = originalNetworkId;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.DigitalServiceIdentification
        public int toByteArray(byte[] data, int index) {
            return HdmiRecordSources.threeFieldsToSixBytes(this.mTransportStreamId, this.mServiceId, this.mOriginalNetworkId, data, index);
        }
    }

    /* loaded from: classes2.dex */
    public static final class AtscData implements DigitalServiceIdentification {
        private final int mProgramNumber;
        private final int mTransportStreamId;

        public AtscData(int transportStreamId, int programNumber) {
            this.mTransportStreamId = transportStreamId;
            this.mProgramNumber = programNumber;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.DigitalServiceIdentification
        public int toByteArray(byte[] data, int index) {
            return HdmiRecordSources.threeFieldsToSixBytes(this.mTransportStreamId, this.mProgramNumber, 0, data, index);
        }
    }

    /* loaded from: classes2.dex */
    public static final class DvbData implements DigitalServiceIdentification {
        private final int mOriginalNetworkId;
        private final int mServiceId;
        private final int mTransportStreamId;

        public DvbData(int transportStreamId, int serviceId, int originalNetworkId) {
            this.mTransportStreamId = transportStreamId;
            this.mServiceId = serviceId;
            this.mOriginalNetworkId = originalNetworkId;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.DigitalServiceIdentification
        public int toByteArray(byte[] data, int index) {
            return HdmiRecordSources.threeFieldsToSixBytes(this.mTransportStreamId, this.mServiceId, this.mOriginalNetworkId, data, index);
        }
    }

    /* loaded from: classes2.dex */
    private static final class ChannelIdentifier {
        private final int mChannelNumberFormat;
        private final int mMajorChannelNumber;
        private final int mMinorChannelNumber;

        private ChannelIdentifier(int format, int majorNumber, int minorNumer) {
            this.mChannelNumberFormat = format;
            this.mMajorChannelNumber = majorNumber;
            this.mMinorChannelNumber = minorNumer;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int toByteArray(byte[] data, int index) {
            int i = this.mMajorChannelNumber;
            data[index] = (byte) ((this.mChannelNumberFormat << 2) | ((i >>> 8) & 3));
            data[index + 1] = (byte) (i & 255);
            HdmiRecordSources.shortToByteArray((short) this.mMinorChannelNumber, data, index + 2);
            return 4;
        }
    }

    /* loaded from: classes2.dex */
    public static final class DigitalChannelData implements DigitalServiceIdentification {
        private final ChannelIdentifier mChannelIdentifier;

        public static DigitalChannelData ofTwoNumbers(int majorNumber, int minorNumber) {
            return new DigitalChannelData(new ChannelIdentifier(2, majorNumber, minorNumber));
        }

        public static DigitalChannelData ofOneNumber(int number) {
            return new DigitalChannelData(new ChannelIdentifier(1, 0, number));
        }

        private DigitalChannelData(ChannelIdentifier id) {
            this.mChannelIdentifier = id;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.DigitalServiceIdentification
        public int toByteArray(byte[] data, int index) {
            this.mChannelIdentifier.toByteArray(data, index);
            data[index + 4] = 0;
            data[index + 5] = 0;
            return 6;
        }
    }

    public static DigitalServiceSource ofDigitalChannelId(int broadcastSystem, DigitalChannelData data) {
        if (data == null) {
            throw new IllegalArgumentException("data should not be null.");
        }
        switch (broadcastSystem) {
            case 0:
            case 1:
            case 2:
            case 8:
            case 9:
            case 10:
            case 16:
            case 17:
            case 18:
            case 24:
            case 25:
            case 26:
            case 27:
                return new DigitalServiceSource(1, broadcastSystem, data);
            default:
                Log.m104w(TAG, "Invalid broadcast type:" + broadcastSystem);
                throw new IllegalArgumentException("Invalid broadcast system value:" + broadcastSystem);
        }
    }

    public static DigitalServiceSource ofArib(int aribType, AribData data) {
        if (data == null) {
            throw new IllegalArgumentException("data should not be null.");
        }
        switch (aribType) {
            case 0:
            case 8:
            case 9:
            case 10:
                return new DigitalServiceSource(0, aribType, data);
            default:
                Log.m104w(TAG, "Invalid ARIB type:" + aribType);
                throw new IllegalArgumentException("type should not be null.");
        }
    }

    public static DigitalServiceSource ofAtsc(int atscType, AtscData data) {
        if (data == null) {
            throw new IllegalArgumentException("data should not be null.");
        }
        switch (atscType) {
            case 1:
            case 16:
            case 17:
            case 18:
                return new DigitalServiceSource(0, atscType, data);
            default:
                Log.m104w(TAG, "Invalid ATSC type:" + atscType);
                throw new IllegalArgumentException("Invalid ATSC type:" + atscType);
        }
    }

    public static DigitalServiceSource ofDvb(int dvbType, DvbData data) {
        if (data == null) {
            throw new IllegalArgumentException("data should not be null.");
        }
        switch (dvbType) {
            case 2:
            case 24:
            case 25:
            case 26:
            case 27:
                return new DigitalServiceSource(0, dvbType, data);
            default:
                Log.m104w(TAG, "Invalid DVB type:" + dvbType);
                throw new IllegalArgumentException("Invalid DVB type:" + dvbType);
        }
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class DigitalServiceSource extends RecordSource {
        private static final int DIGITAL_SERVICE_IDENTIFIED_BY_CHANNEL = 1;
        private static final int DIGITAL_SERVICE_IDENTIFIED_BY_DIGITAL_ID = 0;
        static final int EXTRA_DATA_SIZE = 7;
        private final int mBroadcastSystem;
        private final DigitalServiceIdentification mIdentification;
        private final int mIdentificationMethod;

        private DigitalServiceSource(int identificatinoMethod, int broadcastSystem, DigitalServiceIdentification identification) {
            super(2, 7);
            this.mIdentificationMethod = identificatinoMethod;
            this.mBroadcastSystem = broadcastSystem;
            this.mIdentification = identification;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.RecordSource
        int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) ((this.mIdentificationMethod << 7) | (this.mBroadcastSystem & 127));
            this.mIdentification.toByteArray(data, index + 1);
            return 7;
        }
    }

    public static AnalogueServiceSource ofAnalogue(int broadcastType, int frequency, int broadcastSystem) {
        if (broadcastType < 0 || broadcastType > 2) {
            Log.m104w(TAG, "Invalid Broadcast type:" + broadcastType);
            throw new IllegalArgumentException("Invalid Broadcast type:" + broadcastType);
        } else if (frequency < 0 || frequency > 65535) {
            Log.m104w(TAG, "Invalid frequency value[0x0000-0xFFFF]:" + frequency);
            throw new IllegalArgumentException("Invalid frequency value[0x0000-0xFFFF]:" + frequency);
        } else if (broadcastSystem < 0 || broadcastSystem > 31) {
            Log.m104w(TAG, "Invalid Broadcast system:" + broadcastSystem);
            throw new IllegalArgumentException("Invalid Broadcast system:" + broadcastSystem);
        } else {
            return new AnalogueServiceSource(broadcastType, frequency, broadcastSystem);
        }
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class AnalogueServiceSource extends RecordSource {
        static final int EXTRA_DATA_SIZE = 4;
        private final int mBroadcastSystem;
        private final int mBroadcastType;
        private final int mFrequency;

        private AnalogueServiceSource(int broadcastType, int frequency, int broadcastSystem) {
            super(3, 4);
            this.mBroadcastType = broadcastType;
            this.mFrequency = frequency;
            this.mBroadcastSystem = broadcastSystem;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.RecordSource
        int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) this.mBroadcastType;
            HdmiRecordSources.shortToByteArray((short) this.mFrequency, data, index + 1);
            data[index + 3] = (byte) this.mBroadcastSystem;
            return 4;
        }
    }

    public static ExternalPlugData ofExternalPlug(int plugNumber) {
        if (plugNumber < 1 || plugNumber > 255) {
            Log.m104w(TAG, "Invalid plug number[1-255]" + plugNumber);
            throw new IllegalArgumentException("Invalid plug number[1-255]" + plugNumber);
        }
        return new ExternalPlugData(plugNumber);
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class ExternalPlugData extends RecordSource {
        static final int EXTRA_DATA_SIZE = 1;
        private final int mPlugNumber;

        private ExternalPlugData(int plugNumber) {
            super(4, 1);
            this.mPlugNumber = plugNumber;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.RecordSource
        int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) this.mPlugNumber;
            return 1;
        }
    }

    public static ExternalPhysicalAddress ofExternalPhysicalAddress(int physicalAddress) {
        if (((-65536) & physicalAddress) != 0) {
            Log.m104w(TAG, "Invalid physical address:" + physicalAddress);
            throw new IllegalArgumentException("Invalid physical address:" + physicalAddress);
        }
        return new ExternalPhysicalAddress(physicalAddress);
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class ExternalPhysicalAddress extends RecordSource {
        static final int EXTRA_DATA_SIZE = 2;
        private final int mPhysicalAddress;

        private ExternalPhysicalAddress(int physicalAddress) {
            super(5, 2);
            this.mPhysicalAddress = physicalAddress;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.RecordSource
        int extraParamToByteArray(byte[] data, int index) {
            HdmiRecordSources.shortToByteArray((short) this.mPhysicalAddress, data, index);
            return 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int threeFieldsToSixBytes(int first, int second, int third, byte[] data, int index) {
        shortToByteArray((short) first, data, index);
        shortToByteArray((short) second, data, index + 2);
        shortToByteArray((short) third, data, index + 4);
        return 6;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int shortToByteArray(short value, byte[] byteArray, int index) {
        byteArray[index] = (byte) ((value >>> 8) & 255);
        byteArray[index + 1] = (byte) (value & 255);
        return 2;
    }

    @SystemApi
    public static boolean checkRecordSource(byte[] recordSource) {
        if (recordSource == null || recordSource.length == 0) {
            return false;
        }
        int recordSourceType = recordSource[0];
        int extraDataSize = recordSource.length - 1;
        switch (recordSourceType) {
            case 1:
                return extraDataSize == 0;
            case 2:
                return extraDataSize == 7;
            case 3:
                return extraDataSize == 4;
            case 4:
                return extraDataSize == 1;
            case 5:
                return extraDataSize == 2;
            default:
                return false;
        }
    }
}
