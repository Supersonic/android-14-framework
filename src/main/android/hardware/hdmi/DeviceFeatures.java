package android.hardware.hdmi;

import android.hardware.gnss.GnssSignalType;
import com.android.net.module.util.NetworkStackConstants;
import java.util.Objects;
/* loaded from: classes2.dex */
public class DeviceFeatures {
    public static final int FEATURE_NOT_SUPPORTED = 0;
    public static final int FEATURE_SUPPORTED = 1;
    public static final int FEATURE_SUPPORT_UNKNOWN = 2;
    private final int mArcRxSupport;
    private final int mArcTxSupport;
    private final int mDeckControlSupport;
    private final int mRecordTvScreenSupport;
    private final int mSetAudioRateSupport;
    private final int mSetAudioVolumeLevelSupport;
    private final int mSetOsdStringSupport;
    public static final DeviceFeatures ALL_FEATURES_SUPPORT_UNKNOWN = new Builder(2).build();
    public static final DeviceFeatures NO_FEATURES_SUPPORTED = new Builder(0).build();

    /* loaded from: classes2.dex */
    public @interface FeatureSupportStatus {
    }

    private DeviceFeatures(Builder builder) {
        this.mRecordTvScreenSupport = builder.mRecordTvScreenSupport;
        this.mSetOsdStringSupport = builder.mOsdStringSupport;
        this.mDeckControlSupport = builder.mDeckControlSupport;
        this.mSetAudioRateSupport = builder.mSetAudioRateSupport;
        this.mArcTxSupport = builder.mArcTxSupport;
        this.mArcRxSupport = builder.mArcRxSupport;
        this.mSetAudioVolumeLevelSupport = builder.mSetAudioVolumeLevelSupport;
    }

    public Builder toBuilder() {
        return new Builder();
    }

    public static DeviceFeatures fromOperand(byte[] deviceFeaturesOperand) {
        Builder builder = new Builder(2);
        if (deviceFeaturesOperand.length >= 1) {
            byte b = deviceFeaturesOperand[0];
            builder.setRecordTvScreenSupport(bitToFeatureSupportStatus(((b >> 6) & 1) == 1)).setSetOsdStringSupport(bitToFeatureSupportStatus(((b >> 5) & 1) == 1)).setDeckControlSupport(bitToFeatureSupportStatus(((b >> 4) & 1) == 1)).setSetAudioRateSupport(bitToFeatureSupportStatus(((b >> 3) & 1) == 1)).setArcTxSupport(bitToFeatureSupportStatus(((b >> 2) & 1) == 1)).setArcRxSupport(bitToFeatureSupportStatus(((b >> 1) & 1) == 1)).setSetAudioVolumeLevelSupport(bitToFeatureSupportStatus((b & 1) == 1));
        }
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int updateFeatureSupportStatus(int oldStatus, int newStatus) {
        if (newStatus == 2) {
            return oldStatus;
        }
        return newStatus;
    }

    public byte[] toOperand() {
        byte result = this.mRecordTvScreenSupport == 1 ? (byte) (0 | 64) : (byte) 0;
        if (this.mSetOsdStringSupport == 1) {
            result = (byte) (result | NetworkStackConstants.TCPHDR_URG);
        }
        if (this.mDeckControlSupport == 1) {
            result = (byte) (result | 16);
        }
        if (this.mSetAudioRateSupport == 1) {
            result = (byte) (result | 8);
        }
        if (this.mArcTxSupport == 1) {
            result = (byte) (result | 4);
        }
        if (this.mArcRxSupport == 1) {
            result = (byte) (result | 2);
        }
        if (this.mSetAudioVolumeLevelSupport == 1) {
            result = (byte) (result | 1);
        }
        return new byte[]{result};
    }

    private static int bitToFeatureSupportStatus(boolean bit) {
        return bit ? 1 : 0;
    }

    public int getRecordTvScreenSupport() {
        return this.mRecordTvScreenSupport;
    }

    public int getSetOsdStringSupport() {
        return this.mSetOsdStringSupport;
    }

    public int getDeckControlSupport() {
        return this.mDeckControlSupport;
    }

    public int getSetAudioRateSupport() {
        return this.mSetAudioRateSupport;
    }

    public int getArcTxSupport() {
        return this.mArcTxSupport;
    }

    public int getArcRxSupport() {
        return this.mArcRxSupport;
    }

    public int getSetAudioVolumeLevelSupport() {
        return this.mSetAudioVolumeLevelSupport;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DeviceFeatures) {
            DeviceFeatures other = (DeviceFeatures) obj;
            return this.mRecordTvScreenSupport == other.mRecordTvScreenSupport && this.mSetOsdStringSupport == other.mSetOsdStringSupport && this.mDeckControlSupport == other.mDeckControlSupport && this.mSetAudioRateSupport == other.mSetAudioRateSupport && this.mArcTxSupport == other.mArcTxSupport && this.mArcRxSupport == other.mArcRxSupport && this.mSetAudioVolumeLevelSupport == other.mSetAudioVolumeLevelSupport;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRecordTvScreenSupport), Integer.valueOf(this.mSetOsdStringSupport), Integer.valueOf(this.mDeckControlSupport), Integer.valueOf(this.mSetAudioRateSupport), Integer.valueOf(this.mArcTxSupport), Integer.valueOf(this.mArcRxSupport), Integer.valueOf(this.mSetAudioVolumeLevelSupport));
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Device features: ");
        s.append("record_tv_screen: ").append(featureSupportStatusToString(this.mRecordTvScreenSupport)).append(" ");
        s.append("set_osd_string: ").append(featureSupportStatusToString(this.mSetOsdStringSupport)).append(" ");
        s.append("deck_control: ").append(featureSupportStatusToString(this.mDeckControlSupport)).append(" ");
        s.append("set_audio_rate: ").append(featureSupportStatusToString(this.mSetAudioRateSupport)).append(" ");
        s.append("arc_tx: ").append(featureSupportStatusToString(this.mArcTxSupport)).append(" ");
        s.append("arc_rx: ").append(featureSupportStatusToString(this.mArcRxSupport)).append(" ");
        s.append("set_audio_volume_level: ").append(featureSupportStatusToString(this.mSetAudioVolumeLevelSupport)).append(" ");
        return s.toString();
    }

    private static String featureSupportStatusToString(int status) {
        switch (status) {
            case 0:
                return GnssSignalType.CODE_TYPE_N;
            case 1:
                return GnssSignalType.CODE_TYPE_Y;
            default:
                return "?";
        }
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mArcRxSupport;
        private int mArcTxSupport;
        private int mDeckControlSupport;
        private int mOsdStringSupport;
        private int mRecordTvScreenSupport;
        private int mSetAudioRateSupport;
        private int mSetAudioVolumeLevelSupport;

        private Builder(int defaultFeatureSupportStatus) {
            this.mRecordTvScreenSupport = defaultFeatureSupportStatus;
            this.mOsdStringSupport = defaultFeatureSupportStatus;
            this.mDeckControlSupport = defaultFeatureSupportStatus;
            this.mSetAudioRateSupport = defaultFeatureSupportStatus;
            this.mArcTxSupport = defaultFeatureSupportStatus;
            this.mArcRxSupport = defaultFeatureSupportStatus;
            this.mSetAudioVolumeLevelSupport = defaultFeatureSupportStatus;
        }

        private Builder(DeviceFeatures info) {
            this.mRecordTvScreenSupport = info.getRecordTvScreenSupport();
            this.mOsdStringSupport = info.getSetOsdStringSupport();
            this.mDeckControlSupport = info.getDeckControlSupport();
            this.mSetAudioRateSupport = info.getSetAudioRateSupport();
            this.mArcTxSupport = info.getArcTxSupport();
            this.mArcRxSupport = info.getArcRxSupport();
            this.mSetAudioVolumeLevelSupport = info.getSetAudioVolumeLevelSupport();
        }

        public DeviceFeatures build() {
            return new DeviceFeatures(this);
        }

        public Builder setRecordTvScreenSupport(int recordTvScreenSupport) {
            this.mRecordTvScreenSupport = recordTvScreenSupport;
            return this;
        }

        public Builder setSetOsdStringSupport(int setOsdStringSupport) {
            this.mOsdStringSupport = setOsdStringSupport;
            return this;
        }

        public Builder setDeckControlSupport(int deckControlSupport) {
            this.mDeckControlSupport = deckControlSupport;
            return this;
        }

        public Builder setSetAudioRateSupport(int setAudioRateSupport) {
            this.mSetAudioRateSupport = setAudioRateSupport;
            return this;
        }

        public Builder setArcTxSupport(int arcTxSupport) {
            this.mArcTxSupport = arcTxSupport;
            return this;
        }

        public Builder setArcRxSupport(int arcRxSupport) {
            this.mArcRxSupport = arcRxSupport;
            return this;
        }

        public Builder setSetAudioVolumeLevelSupport(int setAudioVolumeLevelSupport) {
            this.mSetAudioVolumeLevelSupport = setAudioVolumeLevelSupport;
            return this;
        }

        public Builder update(DeviceFeatures newDeviceFeatures) {
            this.mRecordTvScreenSupport = DeviceFeatures.updateFeatureSupportStatus(this.mRecordTvScreenSupport, newDeviceFeatures.getRecordTvScreenSupport());
            this.mOsdStringSupport = DeviceFeatures.updateFeatureSupportStatus(this.mOsdStringSupport, newDeviceFeatures.getSetOsdStringSupport());
            this.mDeckControlSupport = DeviceFeatures.updateFeatureSupportStatus(this.mDeckControlSupport, newDeviceFeatures.getDeckControlSupport());
            this.mSetAudioRateSupport = DeviceFeatures.updateFeatureSupportStatus(this.mSetAudioRateSupport, newDeviceFeatures.getSetAudioRateSupport());
            this.mArcTxSupport = DeviceFeatures.updateFeatureSupportStatus(this.mArcTxSupport, newDeviceFeatures.getArcTxSupport());
            this.mArcRxSupport = DeviceFeatures.updateFeatureSupportStatus(this.mArcRxSupport, newDeviceFeatures.getArcRxSupport());
            this.mSetAudioVolumeLevelSupport = DeviceFeatures.updateFeatureSupportStatus(this.mSetAudioVolumeLevelSupport, newDeviceFeatures.getSetAudioVolumeLevelSupport());
            return this;
        }
    }
}
