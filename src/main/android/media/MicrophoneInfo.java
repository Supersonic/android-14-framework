package android.media;

import android.util.Pair;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
/* loaded from: classes2.dex */
public final class MicrophoneInfo {
    public static final int CHANNEL_MAPPING_DIRECT = 1;
    public static final int CHANNEL_MAPPING_PROCESSED = 2;
    public static final int DIRECTIONALITY_BI_DIRECTIONAL = 2;
    public static final int DIRECTIONALITY_CARDIOID = 3;
    public static final int DIRECTIONALITY_HYPER_CARDIOID = 4;
    public static final int DIRECTIONALITY_OMNI = 1;
    public static final int DIRECTIONALITY_SUPER_CARDIOID = 5;
    public static final int DIRECTIONALITY_UNKNOWN = 0;
    public static final int GROUP_UNKNOWN = -1;
    public static final int INDEX_IN_THE_GROUP_UNKNOWN = -1;
    public static final int LOCATION_MAINBODY = 1;
    public static final int LOCATION_MAINBODY_MOVABLE = 2;
    public static final int LOCATION_PERIPHERAL = 3;
    public static final int LOCATION_UNKNOWN = 0;
    public static final float SENSITIVITY_UNKNOWN = -3.4028235E38f;
    public static final float SPL_UNKNOWN = -3.4028235E38f;
    private String mAddress;
    private List<Pair<Integer, Integer>> mChannelMapping;
    private String mDeviceId;
    private int mDirectionality;
    private List<Pair<Float, Float>> mFrequencyResponse;
    private int mGroup;
    private int mIndexInTheGroup;
    private int mLocation;
    private float mMaxSpl;
    private float mMinSpl;
    private Coordinate3F mOrientation;
    private int mPortId;
    private Coordinate3F mPosition;
    private float mSensitivity;
    private int mType;
    public static final Coordinate3F POSITION_UNKNOWN = new Coordinate3F(-3.4028235E38f, -3.4028235E38f, -3.4028235E38f);
    public static final Coordinate3F ORIENTATION_UNKNOWN = new Coordinate3F(0.0f, 0.0f, 0.0f);

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface MicrophoneDirectionality {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface MicrophoneLocation {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MicrophoneInfo(String deviceId, int type, String address, int location, int group, int indexInTheGroup, Coordinate3F position, Coordinate3F orientation, List<Pair<Float, Float>> frequencyResponse, List<Pair<Integer, Integer>> channelMapping, float sensitivity, float maxSpl, float minSpl, int directionality) {
        this.mDeviceId = deviceId;
        this.mType = type;
        this.mAddress = address;
        this.mLocation = location;
        this.mGroup = group;
        this.mIndexInTheGroup = indexInTheGroup;
        this.mPosition = position;
        this.mOrientation = orientation;
        this.mFrequencyResponse = frequencyResponse;
        this.mChannelMapping = channelMapping;
        this.mSensitivity = sensitivity;
        this.mMaxSpl = maxSpl;
        this.mMinSpl = minSpl;
        this.mDirectionality = directionality;
    }

    public String getDescription() {
        return this.mDeviceId;
    }

    public int getId() {
        return this.mPortId;
    }

    public int getInternalDeviceType() {
        return this.mType;
    }

    public int getType() {
        return AudioDeviceInfo.convertInternalDeviceToDeviceType(this.mType);
    }

    public String getAddress() {
        return this.mAddress;
    }

    public int getLocation() {
        return this.mLocation;
    }

    public int getGroup() {
        return this.mGroup;
    }

    public int getIndexInTheGroup() {
        return this.mIndexInTheGroup;
    }

    public Coordinate3F getPosition() {
        return this.mPosition;
    }

    public Coordinate3F getOrientation() {
        return this.mOrientation;
    }

    public List<Pair<Float, Float>> getFrequencyResponse() {
        return this.mFrequencyResponse;
    }

    public List<Pair<Integer, Integer>> getChannelMapping() {
        return this.mChannelMapping;
    }

    public float getSensitivity() {
        return this.mSensitivity;
    }

    public float getMaxSpl() {
        return this.mMaxSpl;
    }

    public float getMinSpl() {
        return this.mMinSpl;
    }

    public int getDirectionality() {
        return this.mDirectionality;
    }

    public void setId(int portId) {
        this.mPortId = portId;
    }

    public void setChannelMapping(List<Pair<Integer, Integer>> channelMapping) {
        this.mChannelMapping = channelMapping;
    }

    /* loaded from: classes2.dex */
    public static final class Coordinate3F {

        /* renamed from: x */
        public final float f275x;

        /* renamed from: y */
        public final float f276y;

        /* renamed from: z */
        public final float f277z;

        Coordinate3F(float x, float y, float z) {
            this.f275x = x;
            this.f276y = y;
            this.f277z = z;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Coordinate3F) {
                Coordinate3F other = (Coordinate3F) obj;
                return this.f275x == other.f275x && this.f276y == other.f276y && this.f277z == other.f277z;
            }
            return false;
        }
    }
}
