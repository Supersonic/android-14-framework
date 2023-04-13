package android.media;

import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class VolumeShaper implements AutoCloseable {
    private int mId;
    private final WeakReference<PlayerBase> mWeakPlayerBase;

    /* JADX INFO: Access modifiers changed from: package-private */
    public VolumeShaper(Configuration configuration, PlayerBase playerBase) {
        this.mWeakPlayerBase = new WeakReference<>(playerBase);
        this.mId = applyPlayer(configuration, new Operation.Builder().defer().build());
    }

    int getId() {
        return this.mId;
    }

    public void apply(Operation operation) {
        applyPlayer(new Configuration(this.mId), operation);
    }

    public void replace(Configuration configuration, Operation operation, boolean join) {
        this.mId = applyPlayer(configuration, new Operation.Builder(operation).replace(this.mId, join).build());
    }

    public float getVolume() {
        return getStatePlayer(this.mId).getVolume();
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        try {
            applyPlayer(new Configuration(this.mId), new Operation.Builder().terminate().build());
        } catch (IllegalStateException e) {
        }
        WeakReference<PlayerBase> weakReference = this.mWeakPlayerBase;
        if (weakReference != null) {
            weakReference.clear();
        }
    }

    protected void finalize() {
        close();
    }

    private int applyPlayer(Configuration configuration, Operation operation) {
        WeakReference<PlayerBase> weakReference = this.mWeakPlayerBase;
        if (weakReference != null) {
            PlayerBase player = weakReference.get();
            if (player == null) {
                throw new IllegalStateException("player deallocated");
            }
            int id = player.playerApplyVolumeShaper(configuration, operation);
            if (id < 0) {
                if (id == -38) {
                    throw new IllegalStateException("player or VolumeShaper deallocated");
                }
                throw new IllegalArgumentException("invalid configuration or operation: " + id);
            }
            return id;
        }
        throw new IllegalStateException("uninitialized shaper");
    }

    private State getStatePlayer(int id) {
        WeakReference<PlayerBase> weakReference = this.mWeakPlayerBase;
        if (weakReference != null) {
            PlayerBase player = weakReference.get();
            if (player == null) {
                throw new IllegalStateException("player deallocated");
            }
            State state = player.playerGetVolumeShaperState(id);
            if (state == null) {
                throw new IllegalStateException("shaper cannot be found");
            }
            return state;
        }
        throw new IllegalStateException("uninitialized shaper");
    }

    /* loaded from: classes2.dex */
    public static final class Configuration implements Parcelable {
        public static final Parcelable.Creator<Configuration> CREATOR;
        public static final int INTERPOLATOR_TYPE_CUBIC = 2;
        public static final int INTERPOLATOR_TYPE_CUBIC_MONOTONIC = 3;
        public static final int INTERPOLATOR_TYPE_LINEAR = 1;
        public static final int INTERPOLATOR_TYPE_STEP = 0;
        private static final int MAXIMUM_CURVE_POINTS = 16;
        public static final int OPTION_FLAG_CLOCK_TIME = 2;
        private static final int OPTION_FLAG_PUBLIC_ALL = 3;
        public static final int OPTION_FLAG_VOLUME_IN_DBFS = 1;
        public static final Configuration SCURVE_RAMP;
        public static final Configuration SINE_RAMP;
        static final int TYPE_ID = 0;
        static final int TYPE_SCALE = 1;
        private final double mDurationMs;
        private final int mId;
        private final int mInterpolatorType;
        private final int mOptionFlags;
        private final float[] mTimes;
        private final int mType;
        private final float[] mVolumes;
        public static final Configuration LINEAR_RAMP = new Builder().setInterpolatorType(1).setCurve(new float[]{0.0f, 1.0f}, new float[]{0.0f, 1.0f}).setDuration(1000).build();
        public static final Configuration CUBIC_RAMP = new Builder().setInterpolatorType(2).setCurve(new float[]{0.0f, 1.0f}, new float[]{0.0f, 1.0f}).setDuration(1000).build();

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface InterpolatorType {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface OptionFlag {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface Type {
        }

        public static int getMaximumCurvePoints() {
            return 16;
        }

        static {
            float[] times = new float[16];
            float[] sines = new float[16];
            float[] scurve = new float[16];
            for (int i = 0; i < 16; i++) {
                times[i] = i / 15.0f;
                float sine = (float) Math.sin((times[i] * 3.141592653589793d) / 2.0d);
                sines[i] = sine;
                scurve[i] = sine * sine;
            }
            SINE_RAMP = new Builder().setInterpolatorType(2).setCurve(times, sines).setDuration(1000L).build();
            SCURVE_RAMP = new Builder().setInterpolatorType(2).setCurve(times, scurve).setDuration(1000L).build();
            CREATOR = new Parcelable.Creator<Configuration>() { // from class: android.media.VolumeShaper.Configuration.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.p008os.Parcelable.Creator
                public Configuration createFromParcel(Parcel p) {
                    return Configuration.fromParcelable(VolumeShaperConfiguration.CREATOR.createFromParcel(p));
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.p008os.Parcelable.Creator
                public Configuration[] newArray(int size) {
                    return new Configuration[size];
                }
            };
        }

        public String toString() {
            return "VolumeShaper.Configuration{mType = " + this.mType + ", mId = " + this.mId + (this.mType != 0 ? ", mOptionFlags = 0x" + Integer.toHexString(this.mOptionFlags).toUpperCase() + ", mDurationMs = " + this.mDurationMs + ", mInterpolatorType = " + this.mInterpolatorType + ", mTimes[] = " + Arrays.toString(this.mTimes) + ", mVolumes[] = " + Arrays.toString(this.mVolumes) + "}" : "}");
        }

        public int hashCode() {
            int i = this.mType;
            if (i == 0) {
                return Objects.hash(Integer.valueOf(i), Integer.valueOf(this.mId));
            }
            return Objects.hash(Integer.valueOf(i), Integer.valueOf(this.mId), Integer.valueOf(this.mOptionFlags), Double.valueOf(this.mDurationMs), Integer.valueOf(this.mInterpolatorType), Integer.valueOf(Arrays.hashCode(this.mTimes)), Integer.valueOf(Arrays.hashCode(this.mVolumes)));
        }

        public boolean equals(Object o) {
            if (o instanceof Configuration) {
                if (o == this) {
                    return true;
                }
                Configuration other = (Configuration) o;
                int i = this.mType;
                if (i == other.mType && this.mId == other.mId) {
                    return i == 0 || (this.mOptionFlags == other.mOptionFlags && this.mDurationMs == other.mDurationMs && this.mInterpolatorType == other.mInterpolatorType && Arrays.equals(this.mTimes, other.mTimes) && Arrays.equals(this.mVolumes, other.mVolumes));
                }
                return false;
            }
            return false;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            VolumeShaperConfiguration parcelable = toParcelable();
            parcelable.writeToParcel(dest, flags);
        }

        public VolumeShaperConfiguration toParcelable() {
            VolumeShaperConfiguration parcelable = new VolumeShaperConfiguration();
            parcelable.type = typeToAidl(this.mType);
            parcelable.f280id = this.mId;
            if (this.mType != 0) {
                parcelable.optionFlags = optionFlagsToAidl(this.mOptionFlags);
                parcelable.durationMs = this.mDurationMs;
                parcelable.interpolatorConfig = toInterpolatorParcelable();
            }
            return parcelable;
        }

        private InterpolatorConfig toInterpolatorParcelable() {
            InterpolatorConfig parcelable = new InterpolatorConfig();
            parcelable.type = interpolatorTypeToAidl(this.mInterpolatorType);
            parcelable.firstSlope = 0.0f;
            parcelable.lastSlope = 0.0f;
            parcelable.f266xy = new float[this.mTimes.length * 2];
            for (int i = 0; i < this.mTimes.length; i++) {
                parcelable.f266xy[i * 2] = this.mTimes[i];
                parcelable.f266xy[(i * 2) + 1] = this.mVolumes[i];
            }
            return parcelable;
        }

        public static Configuration fromParcelable(VolumeShaperConfiguration parcelable) {
            int type = typeFromAidl(parcelable.type);
            int id = parcelable.f280id;
            if (type == 0) {
                return new Configuration(id);
            }
            int optionFlags = optionFlagsFromAidl(parcelable.optionFlags);
            double durationMs = parcelable.durationMs;
            int interpolatorType = interpolatorTypeFromAidl(parcelable.interpolatorConfig.type);
            int length = parcelable.interpolatorConfig.f266xy.length;
            if (length % 2 != 0) {
                throw new BadParcelableException("xy length must be even");
            }
            float[] times = new float[length / 2];
            float[] volumes = new float[length / 2];
            for (int i = 0; i < length / 2; i++) {
                times[i] = parcelable.interpolatorConfig.f266xy[i * 2];
                volumes[i] = parcelable.interpolatorConfig.f266xy[(i * 2) + 1];
            }
            return new Configuration(type, id, optionFlags, durationMs, interpolatorType, times, volumes);
        }

        private static int interpolatorTypeFromAidl(int aidl) {
            switch (aidl) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                default:
                    throw new BadParcelableException("Unknown interpolator type");
            }
        }

        private static int interpolatorTypeToAidl(int type) {
            switch (type) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                default:
                    throw new RuntimeException("Unknown interpolator type");
            }
        }

        private static int typeFromAidl(int aidl) {
            switch (aidl) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    throw new BadParcelableException("Unknown type");
            }
        }

        private static int typeToAidl(int type) {
            switch (type) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    throw new RuntimeException("Unknown type");
            }
        }

        private static int optionFlagsFromAidl(int aidl) {
            int result = 0;
            if ((aidl & 1) != 0) {
                result = 0 | 1;
            }
            if ((aidl & 2) != 0) {
                return result | 2;
            }
            return result;
        }

        private static int optionFlagsToAidl(int flags) {
            int result = 0;
            if ((flags & 1) != 0) {
                result = 0 | 1;
            }
            if ((flags & 2) != 0) {
                return result | 2;
            }
            return result;
        }

        public Configuration(int id) {
            if (id < 0) {
                throw new IllegalArgumentException("negative id " + id);
            }
            this.mType = 0;
            this.mId = id;
            this.mInterpolatorType = 0;
            this.mOptionFlags = 0;
            this.mDurationMs = 0.0d;
            this.mTimes = null;
            this.mVolumes = null;
        }

        private Configuration(int type, int id, int optionFlags, double durationMs, int interpolatorType, float[] times, float[] volumes) {
            this.mType = type;
            this.mId = id;
            this.mOptionFlags = optionFlags;
            this.mDurationMs = durationMs;
            this.mInterpolatorType = interpolatorType;
            this.mTimes = times;
            this.mVolumes = volumes;
        }

        public int getType() {
            return this.mType;
        }

        public int getId() {
            return this.mId;
        }

        public int getInterpolatorType() {
            return this.mInterpolatorType;
        }

        public int getOptionFlags() {
            return this.mOptionFlags & 3;
        }

        int getAllOptionFlags() {
            return this.mOptionFlags;
        }

        public long getDuration() {
            return (long) this.mDurationMs;
        }

        public float[] getTimes() {
            return this.mTimes;
        }

        public float[] getVolumes() {
            return this.mVolumes;
        }

        private static String checkCurveForErrors(float[] times, float[] volumes, boolean log) {
            if (times == null) {
                return "times array must be non-null";
            }
            if (volumes == null) {
                return "volumes array must be non-null";
            }
            if (times.length != volumes.length) {
                return "array length must match";
            }
            if (times.length < 2) {
                return "array length must be at least 2";
            }
            if (times.length > 16) {
                return "array length must be no larger than 16";
            }
            if (times[0] != 0.0f) {
                return "times must start at 0.f";
            }
            if (times[times.length - 1] != 1.0f) {
                return "times must end at 1.f";
            }
            for (int i = 1; i < times.length; i++) {
                if (times[i] <= times[i - 1]) {
                    return "times not monotonic increasing, check index " + i;
                }
            }
            if (log) {
                for (int i2 = 0; i2 < volumes.length; i2++) {
                    if (volumes[i2] > 0.0f) {
                        return "volumes for log scale cannot be positive, check index " + i2;
                    }
                }
                return null;
            }
            for (int i3 = 0; i3 < volumes.length; i3++) {
                if (volumes[i3] < 0.0f || volumes[i3] > 1.0f) {
                    return "volumes for linear scale must be between 0.f and 1.f, check index " + i3;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void checkCurveForErrorsAndThrowException(float[] times, float[] volumes, boolean log, boolean ise) {
            String error = checkCurveForErrors(times, volumes, log);
            if (error != null) {
                if (ise) {
                    throw new IllegalStateException(error);
                }
                throw new IllegalArgumentException(error);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void checkValidVolumeAndThrowException(float volume, boolean log) {
            if (log) {
                if (volume > 0.0f) {
                    throw new IllegalArgumentException("dbfs volume must be 0.f or less");
                }
            } else if (volume < 0.0f || volume > 1.0f) {
                throw new IllegalArgumentException("volume must be >= 0.f and <= 1.f");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void clampVolume(float[] volumes, boolean log) {
            if (log) {
                for (int i = 0; i < volumes.length; i++) {
                    if (volumes[i] > 0.0f) {
                        volumes[i] = 0.0f;
                    }
                }
                return;
            }
            for (int i2 = 0; i2 < volumes.length; i2++) {
                if (volumes[i2] < 0.0f) {
                    volumes[i2] = 0.0f;
                } else if (volumes[i2] > 1.0f) {
                    volumes[i2] = 1.0f;
                }
            }
        }

        /* loaded from: classes2.dex */
        public static final class Builder {
            private double mDurationMs;
            private int mId;
            private int mInterpolatorType;
            private int mOptionFlags;
            private float[] mTimes;
            private int mType;
            private float[] mVolumes;

            public Builder() {
                this.mType = 1;
                this.mId = -1;
                this.mInterpolatorType = 2;
                this.mOptionFlags = 2;
                this.mDurationMs = 1000.0d;
                this.mTimes = null;
                this.mVolumes = null;
            }

            public Builder(Configuration configuration) {
                this.mType = 1;
                this.mId = -1;
                this.mInterpolatorType = 2;
                this.mOptionFlags = 2;
                this.mDurationMs = 1000.0d;
                this.mTimes = null;
                this.mVolumes = null;
                this.mType = configuration.getType();
                this.mId = configuration.getId();
                this.mOptionFlags = configuration.getAllOptionFlags();
                this.mInterpolatorType = configuration.getInterpolatorType();
                this.mDurationMs = configuration.getDuration();
                this.mTimes = (float[]) configuration.getTimes().clone();
                this.mVolumes = (float[]) configuration.getVolumes().clone();
            }

            public Builder setId(int id) {
                if (id < -1) {
                    throw new IllegalArgumentException("invalid id: " + id);
                }
                this.mId = id;
                return this;
            }

            public Builder setInterpolatorType(int interpolatorType) {
                switch (interpolatorType) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        this.mInterpolatorType = interpolatorType;
                        return this;
                    default:
                        throw new IllegalArgumentException("invalid interpolatorType: " + interpolatorType);
                }
            }

            public Builder setOptionFlags(int optionFlags) {
                if ((optionFlags & (-4)) != 0) {
                    throw new IllegalArgumentException("invalid bits in flag: " + optionFlags);
                }
                this.mOptionFlags = (this.mOptionFlags & (-4)) | optionFlags;
                return this;
            }

            public Builder setDuration(long durationMillis) {
                if (durationMillis <= 0) {
                    throw new IllegalArgumentException("duration: " + durationMillis + " not positive");
                }
                this.mDurationMs = durationMillis;
                return this;
            }

            public Builder setCurve(float[] times, float[] volumes) {
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(times, volumes, log, false);
                this.mTimes = (float[]) times.clone();
                this.mVolumes = (float[]) volumes.clone();
                return this;
            }

            public Builder reflectTimes() {
                float[] fArr;
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, log, true);
                int i = 0;
                while (true) {
                    fArr = this.mTimes;
                    if (i >= fArr.length / 2) {
                        break;
                    }
                    float temp = fArr[i];
                    fArr[i] = 1.0f - fArr[(fArr.length - 1) - i];
                    fArr[(fArr.length - 1) - i] = 1.0f - temp;
                    float[] fArr2 = this.mVolumes;
                    float temp2 = fArr2[i];
                    fArr2[i] = fArr2[(fArr2.length - 1) - i];
                    fArr2[(fArr2.length - 1) - i] = temp2;
                    i++;
                }
                if ((1 & fArr.length) != 0) {
                    fArr[i] = 1.0f - fArr[i];
                }
                return this;
            }

            public Builder invertVolumes() {
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, log, true);
                float[] fArr = this.mVolumes;
                float min = fArr[0];
                float max = fArr[0];
                int i = 1;
                while (true) {
                    float[] fArr2 = this.mVolumes;
                    if (i >= fArr2.length) {
                        break;
                    }
                    float f = fArr2[i];
                    if (f < min) {
                        min = fArr2[i];
                    } else if (f > max) {
                        max = fArr2[i];
                    }
                    i++;
                }
                float maxmin = max + min;
                int i2 = 0;
                while (true) {
                    float[] fArr3 = this.mVolumes;
                    if (i2 < fArr3.length) {
                        fArr3[i2] = maxmin - fArr3[i2];
                        i2++;
                    } else {
                        return this;
                    }
                }
            }

            public Builder scaleToEndVolume(float volume) {
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, log, true);
                Configuration.checkValidVolumeAndThrowException(volume, log);
                float[] fArr = this.mVolumes;
                float startVolume = fArr[0];
                float endVolume = fArr[fArr.length - 1];
                if (endVolume == startVolume) {
                    float offset = volume - startVolume;
                    int i = 0;
                    while (true) {
                        float[] fArr2 = this.mVolumes;
                        if (i >= fArr2.length) {
                            break;
                        }
                        fArr2[i] = fArr2[i] + (this.mTimes[i] * offset);
                        i++;
                    }
                } else {
                    float scale = (volume - startVolume) / (endVolume - startVolume);
                    int i2 = 0;
                    while (true) {
                        float[] fArr3 = this.mVolumes;
                        if (i2 >= fArr3.length) {
                            break;
                        }
                        fArr3[i2] = ((fArr3[i2] - startVolume) * scale) + startVolume;
                        i2++;
                    }
                }
                Configuration.clampVolume(this.mVolumes, log);
                return this;
            }

            public Builder scaleToStartVolume(float volume) {
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, log, true);
                Configuration.checkValidVolumeAndThrowException(volume, log);
                float[] fArr = this.mVolumes;
                float startVolume = fArr[0];
                float endVolume = fArr[fArr.length - 1];
                if (endVolume == startVolume) {
                    float offset = volume - startVolume;
                    int i = 0;
                    while (true) {
                        float[] fArr2 = this.mVolumes;
                        if (i >= fArr2.length) {
                            break;
                        }
                        fArr2[i] = fArr2[i] + ((1.0f - this.mTimes[i]) * offset);
                        i++;
                    }
                } else {
                    float scale = (volume - endVolume) / (startVolume - endVolume);
                    int i2 = 0;
                    while (true) {
                        float[] fArr3 = this.mVolumes;
                        if (i2 >= fArr3.length) {
                            break;
                        }
                        fArr3[i2] = ((fArr3[i2] - endVolume) * scale) + endVolume;
                        i2++;
                    }
                }
                Configuration.clampVolume(this.mVolumes, log);
                return this;
            }

            public Configuration build() {
                boolean log = (this.mOptionFlags & 1) != 0;
                Configuration.checkCurveForErrorsAndThrowException(this.mTimes, this.mVolumes, log, true);
                return new Configuration(this.mType, this.mId, this.mOptionFlags, this.mDurationMs, this.mInterpolatorType, this.mTimes, this.mVolumes);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class Operation implements Parcelable {
        private static final int FLAG_CREATE_IF_NEEDED = 16;
        private static final int FLAG_DEFER = 8;
        private static final int FLAG_JOIN = 4;
        private static final int FLAG_NONE = 0;
        private static final int FLAG_PUBLIC_ALL = 3;
        private static final int FLAG_REVERSE = 1;
        private static final int FLAG_TERMINATE = 2;
        private final int mFlags;
        private final int mReplaceId;
        private final float mXOffset;
        public static final Operation PLAY = new Builder().build();
        public static final Operation REVERSE = new Builder().reverse().build();
        public static final Parcelable.Creator<Operation> CREATOR = new Parcelable.Creator<Operation>() { // from class: android.media.VolumeShaper.Operation.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Operation createFromParcel(Parcel p) {
                return Operation.fromParcelable(VolumeShaperOperation.CREATOR.createFromParcel(p));
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Operation[] newArray(int size) {
                return new Operation[size];
            }
        };

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface Flag {
        }

        public String toString() {
            return "VolumeShaper.Operation{mFlags = 0x" + Integer.toHexString(this.mFlags).toUpperCase() + ", mReplaceId = " + this.mReplaceId + ", mXOffset = " + this.mXOffset + "}";
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mFlags), Integer.valueOf(this.mReplaceId), Float.valueOf(this.mXOffset));
        }

        public boolean equals(Object o) {
            if (o instanceof Operation) {
                if (o == this) {
                    return true;
                }
                Operation other = (Operation) o;
                return this.mFlags == other.mFlags && this.mReplaceId == other.mReplaceId && Float.compare(this.mXOffset, other.mXOffset) == 0;
            }
            return false;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            toParcelable().writeToParcel(dest, flags);
        }

        public VolumeShaperOperation toParcelable() {
            VolumeShaperOperation result = new VolumeShaperOperation();
            result.flags = flagsToAidl(this.mFlags);
            result.replaceId = this.mReplaceId;
            result.xOffset = this.mXOffset;
            return result;
        }

        public static Operation fromParcelable(VolumeShaperOperation parcelable) {
            return new Operation(flagsFromAidl(parcelable.flags), parcelable.replaceId, parcelable.xOffset);
        }

        private static int flagsFromAidl(int aidl) {
            int result = 0;
            if ((aidl & 1) != 0) {
                result = 0 | 1;
            }
            if ((aidl & 2) != 0) {
                result |= 2;
            }
            if ((aidl & 4) != 0) {
                result |= 4;
            }
            if ((aidl & 8) != 0) {
                result |= 8;
            }
            if ((aidl & 16) != 0) {
                return result | 16;
            }
            return result;
        }

        private static int flagsToAidl(int flags) {
            int result = 0;
            if ((flags & 1) != 0) {
                result = 0 | 1;
            }
            if ((flags & 2) != 0) {
                result |= 2;
            }
            if ((flags & 4) != 0) {
                result |= 4;
            }
            if ((flags & 8) != 0) {
                result |= 8;
            }
            if ((flags & 16) != 0) {
                return result | 16;
            }
            return result;
        }

        private Operation(int flags, int replaceId, float xOffset) {
            this.mFlags = flags;
            this.mReplaceId = replaceId;
            this.mXOffset = xOffset;
        }

        /* loaded from: classes2.dex */
        public static final class Builder {
            int mFlags;
            int mReplaceId;
            float mXOffset;

            public Builder() {
                this.mFlags = 0;
                this.mReplaceId = -1;
                this.mXOffset = Float.NaN;
            }

            public Builder(Operation operation) {
                this.mReplaceId = operation.mReplaceId;
                this.mFlags = operation.mFlags;
                this.mXOffset = operation.mXOffset;
            }

            public Builder replace(int id, boolean join) {
                this.mReplaceId = id;
                if (join) {
                    this.mFlags |= 4;
                } else {
                    this.mFlags &= -5;
                }
                return this;
            }

            public Builder defer() {
                this.mFlags |= 8;
                return this;
            }

            public Builder terminate() {
                this.mFlags |= 2;
                return this;
            }

            public Builder reverse() {
                this.mFlags ^= 1;
                return this;
            }

            public Builder createIfNeeded() {
                this.mFlags |= 16;
                return this;
            }

            public Builder setXOffset(float xOffset) {
                if (xOffset < -0.0f) {
                    throw new IllegalArgumentException("Negative xOffset not allowed");
                }
                if (xOffset > 1.0f) {
                    throw new IllegalArgumentException("xOffset > 1.f not allowed");
                }
                this.mXOffset = xOffset;
                return this;
            }

            private Builder setFlags(int flags) {
                if ((flags & (-4)) != 0) {
                    throw new IllegalArgumentException("flag has unknown bits set: " + flags);
                }
                this.mFlags = (this.mFlags & (-4)) | flags;
                return this;
            }

            public Operation build() {
                return new Operation(this.mFlags, this.mReplaceId, this.mXOffset);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class State implements Parcelable {
        public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() { // from class: android.media.VolumeShaper.State.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public State createFromParcel(Parcel p) {
                return State.fromParcelable(VolumeShaperState.CREATOR.createFromParcel(p));
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public State[] newArray(int size) {
                return new State[size];
            }
        };
        private float mVolume;
        private float mXOffset;

        public String toString() {
            return "VolumeShaper.State{mVolume = " + this.mVolume + ", mXOffset = " + this.mXOffset + "}";
        }

        public int hashCode() {
            return Objects.hash(Float.valueOf(this.mVolume), Float.valueOf(this.mXOffset));
        }

        public boolean equals(Object o) {
            if (o instanceof State) {
                if (o == this) {
                    return true;
                }
                State other = (State) o;
                return this.mVolume == other.mVolume && this.mXOffset == other.mXOffset;
            }
            return false;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            toParcelable().writeToParcel(dest, flags);
        }

        public VolumeShaperState toParcelable() {
            VolumeShaperState result = new VolumeShaperState();
            result.volume = this.mVolume;
            result.xOffset = this.mXOffset;
            return result;
        }

        public static State fromParcelable(VolumeShaperState p) {
            return new State(p.volume, p.xOffset);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public State(float volume, float xOffset) {
            this.mVolume = volume;
            this.mXOffset = xOffset;
        }

        public float getVolume() {
            return this.mVolume;
        }

        public float getXOffset() {
            return this.mXOffset;
        }
    }
}
