package android.service.voice;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.content.p001pm.AppSearchShortcutInfo;
import android.content.res.Resources;
import android.media.AudioRecord;
import android.media.MediaSyncEvent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.service.voice.DetectedPhrase;
import com.android.internal.C4057R;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class HotwordDetectedResult implements Parcelable {
    public static final int AUDIO_CHANNEL_UNSET = -1;
    public static final int CONFIDENCE_LEVEL_HIGH = 5;
    public static final int CONFIDENCE_LEVEL_LOW = 1;
    public static final int CONFIDENCE_LEVEL_LOW_MEDIUM = 2;
    public static final int CONFIDENCE_LEVEL_MEDIUM = 3;
    public static final int CONFIDENCE_LEVEL_MEDIUM_HIGH = 4;
    public static final int CONFIDENCE_LEVEL_NONE = 0;
    public static final int CONFIDENCE_LEVEL_VERY_HIGH = 6;
    private static final String EXTRA_PROXIMITY = "android.service.voice.extra.PROXIMITY";
    public static final int HOTWORD_OFFSET_UNSET = -1;
    private static final int LIMIT_AUDIO_CHANNEL_MAX_VALUE = 63;
    private static final int LIMIT_HOTWORD_OFFSET_MAX_VALUE = 3600000;
    public static final int PROXIMITY_FAR = 2;
    public static final int PROXIMITY_NEAR = 1;
    public static final int PROXIMITY_UNKNOWN = -1;
    private int mAudioChannel;
    private final List<HotwordAudioStream> mAudioStreams;
    private final int mConfidenceLevel;
    private DetectedPhrase mDetectedPhrase;
    private final PersistableBundle mExtras;
    private boolean mHotwordDetectionPersonalized;
    private int mHotwordDurationMillis;
    private int mHotwordOffsetMillis;
    private MediaSyncEvent mMediaSyncEvent;
    private final int mPersonalizedScore;
    private final int mScore;
    private static int sMaxBundleSize = -1;
    public static final Parcelable.Creator<HotwordDetectedResult> CREATOR = new Parcelable.Creator<HotwordDetectedResult>() { // from class: android.service.voice.HotwordDetectedResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordDetectedResult[] newArray(int size) {
            return new HotwordDetectedResult[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordDetectedResult createFromParcel(Parcel in) {
            return new HotwordDetectedResult(in);
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ConfidenceLevel {
    }

    /* loaded from: classes3.dex */
    @interface HotwordConfidenceLevelValue {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface Limit {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Proximity {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ProximityValue {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultConfidenceLevel() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultScore() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultPersonalizedScore() {
        return 0;
    }

    public static int getMaxScore() {
        return 255;
    }

    @Deprecated
    public int getHotwordPhraseId() {
        return this.mDetectedPhrase.getId();
    }

    @Deprecated
    public static int getMaxHotwordPhraseId() {
        return Integer.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<HotwordAudioStream> defaultAudioStreams() {
        return Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PersistableBundle defaultExtras() {
        return new PersistableBundle();
    }

    public static int getMaxBundleSize() {
        if (sMaxBundleSize < 0) {
            sMaxBundleSize = Resources.getSystem().getInteger(C4057R.integer.config_hotwordDetectedResultMaxBundleSize);
        }
        return sMaxBundleSize;
    }

    public MediaSyncEvent getMediaSyncEvent() {
        return this.mMediaSyncEvent;
    }

    public static int getParcelableSize(Parcelable parcelable) {
        Parcel p = Parcel.obtain();
        parcelable.writeToParcel(p, 0);
        p.setDataPosition(0);
        int size = p.dataSize();
        p.recycle();
        return size;
    }

    public static int getUsageSize(HotwordDetectedResult hotwordDetectedResult) {
        int totalBits = hotwordDetectedResult.getConfidenceLevel() != defaultConfidenceLevel() ? 0 + bitCount(6L) : 0;
        if (hotwordDetectedResult.getHotwordOffsetMillis() != -1) {
            totalBits += bitCount(3600000L);
        }
        if (hotwordDetectedResult.getHotwordDurationMillis() != 0) {
            totalBits += bitCount(AudioRecord.getMaxSharedAudioHistoryMillis());
        }
        if (hotwordDetectedResult.getAudioChannel() != -1) {
            totalBits += bitCount(63L);
        }
        int totalBits2 = totalBits + 1;
        if (hotwordDetectedResult.getScore() != defaultScore()) {
            totalBits2 += bitCount(getMaxScore());
        }
        if (hotwordDetectedResult.getPersonalizedScore() != defaultPersonalizedScore()) {
            totalBits2 += bitCount(getMaxScore());
        }
        PersistableBundle persistableBundle = hotwordDetectedResult.getExtras();
        if (!persistableBundle.isEmpty()) {
            return totalBits2 + (getParcelableSize(persistableBundle) * 8);
        }
        return totalBits2;
    }

    static int bitCount(long value) {
        int bits = 0;
        while (value > 0) {
            bits++;
            value >>= 1;
        }
        return bits;
    }

    private void onConstructed() {
        Preconditions.checkArgumentInRange(this.mScore, 0, getMaxScore(), "score");
        Preconditions.checkArgumentInRange(this.mPersonalizedScore, 0, getMaxScore(), "personalizedScore");
        Preconditions.checkArgumentInRange(this.mHotwordDurationMillis, 0L, AudioRecord.getMaxSharedAudioHistoryMillis(), "hotwordDurationMillis");
        int i = this.mHotwordOffsetMillis;
        if (i != -1) {
            Preconditions.checkArgumentInRange(i, 0, (int) LIMIT_HOTWORD_OFFSET_MAX_VALUE, "hotwordOffsetMillis");
        }
        int i2 = this.mAudioChannel;
        if (i2 != -1) {
            Preconditions.checkArgumentInRange(i2, 0, 63, "audioChannel");
        }
        if (!this.mExtras.isEmpty()) {
            if (this.mExtras.containsKey(EXTRA_PROXIMITY)) {
                int proximityValue = this.mExtras.getInt(EXTRA_PROXIMITY);
                this.mExtras.remove(EXTRA_PROXIMITY);
                if (this.mExtras.size() > 0) {
                    Preconditions.checkArgumentInRange(getParcelableSize(this.mExtras), 0, getMaxBundleSize(), AppSearchShortcutInfo.KEY_EXTRAS);
                }
                this.mExtras.putInt(EXTRA_PROXIMITY, proximityValue);
                return;
            }
            Preconditions.checkArgumentInRange(getParcelableSize(this.mExtras), 0, getMaxBundleSize(), AppSearchShortcutInfo.KEY_EXTRAS);
        }
    }

    public List<HotwordAudioStream> getAudioStreams() {
        return List.copyOf(this.mAudioStreams);
    }

    public void setProximity(double distance) {
        int proximityLevel = convertToProximityLevel(distance);
        if (proximityLevel != -1) {
            this.mExtras.putInt(EXTRA_PROXIMITY, proximityLevel);
        }
    }

    public int getProximity() {
        return this.mExtras.getInt(EXTRA_PROXIMITY, -1);
    }

    private int convertToProximityLevel(double distance) {
        if (distance < 0.0d) {
            return -1;
        }
        if (distance <= 3.0d) {
            return 1;
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static abstract class BaseBuilder {
        BaseBuilder() {
        }

        public Builder setAudioStreams(List<HotwordAudioStream> value) {
            Objects.requireNonNull(value, "value should not be null");
            Builder builder = (Builder) this;
            builder.mBuilderFieldsSet |= 256;
            builder.mAudioStreams = List.copyOf(value);
            return builder;
        }

        @Deprecated
        public Builder setHotwordPhraseId(int value) {
            Builder builder = (Builder) this;
            builder.setDetectedPhrase(new DetectedPhrase.Builder().setId(value).build());
            return builder;
        }
    }

    public Builder buildUpon() {
        return new Builder().setConfidenceLevel(this.mConfidenceLevel).setMediaSyncEvent(this.mMediaSyncEvent).setHotwordOffsetMillis(this.mHotwordOffsetMillis).setHotwordDurationMillis(this.mHotwordDurationMillis).setAudioChannel(this.mAudioChannel).setHotwordDetectionPersonalized(this.mHotwordDetectionPersonalized).setScore(this.mScore).setPersonalizedScore(this.mPersonalizedScore).setAudioStreams(this.mAudioStreams).setExtras(this.mExtras).setDetectedPhrase(this.mDetectedPhrase);
    }

    public static String confidenceLevelToString(int value) {
        switch (value) {
            case 0:
                return "CONFIDENCE_LEVEL_NONE";
            case 1:
                return "CONFIDENCE_LEVEL_LOW";
            case 2:
                return "CONFIDENCE_LEVEL_LOW_MEDIUM";
            case 3:
                return "CONFIDENCE_LEVEL_MEDIUM";
            case 4:
                return "CONFIDENCE_LEVEL_MEDIUM_HIGH";
            case 5:
                return "CONFIDENCE_LEVEL_HIGH";
            case 6:
                return "CONFIDENCE_LEVEL_VERY_HIGH";
            default:
                return Integer.toHexString(value);
        }
    }

    static String limitToString(int value) {
        switch (value) {
            case 63:
                return "LIMIT_AUDIO_CHANNEL_MAX_VALUE";
            case LIMIT_HOTWORD_OFFSET_MAX_VALUE /* 3600000 */:
                return "LIMIT_HOTWORD_OFFSET_MAX_VALUE";
            default:
                return Integer.toHexString(value);
        }
    }

    public static String proximityToString(int value) {
        switch (value) {
            case -1:
                return "PROXIMITY_UNKNOWN";
            case 0:
            default:
                return Integer.toHexString(value);
            case 1:
                return "PROXIMITY_NEAR";
            case 2:
                return "PROXIMITY_FAR";
        }
    }

    HotwordDetectedResult(int confidenceLevel, MediaSyncEvent mediaSyncEvent, int hotwordOffsetMillis, int hotwordDurationMillis, int audioChannel, boolean hotwordDetectionPersonalized, int score, int personalizedScore, List<HotwordAudioStream> audioStreams, PersistableBundle extras, DetectedPhrase detectedPhrase) {
        this.mMediaSyncEvent = null;
        this.mHotwordOffsetMillis = -1;
        this.mHotwordDurationMillis = 0;
        this.mAudioChannel = -1;
        this.mHotwordDetectionPersonalized = false;
        this.mDetectedPhrase = new DetectedPhrase.Builder().build();
        this.mConfidenceLevel = confidenceLevel;
        AnnotationValidations.validate((Class<? extends Annotation>) HotwordConfidenceLevelValue.class, (Annotation) null, confidenceLevel);
        this.mMediaSyncEvent = mediaSyncEvent;
        this.mHotwordOffsetMillis = hotwordOffsetMillis;
        this.mHotwordDurationMillis = hotwordDurationMillis;
        this.mAudioChannel = audioChannel;
        this.mHotwordDetectionPersonalized = hotwordDetectionPersonalized;
        this.mScore = score;
        this.mPersonalizedScore = personalizedScore;
        this.mAudioStreams = audioStreams;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) audioStreams);
        this.mExtras = extras;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) extras);
        this.mDetectedPhrase = detectedPhrase;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) detectedPhrase);
        onConstructed();
    }

    public int getConfidenceLevel() {
        return this.mConfidenceLevel;
    }

    public int getHotwordOffsetMillis() {
        return this.mHotwordOffsetMillis;
    }

    public int getHotwordDurationMillis() {
        return this.mHotwordDurationMillis;
    }

    public int getAudioChannel() {
        return this.mAudioChannel;
    }

    public boolean isHotwordDetectionPersonalized() {
        return this.mHotwordDetectionPersonalized;
    }

    public int getScore() {
        return this.mScore;
    }

    public int getPersonalizedScore() {
        return this.mPersonalizedScore;
    }

    public PersistableBundle getExtras() {
        return this.mExtras;
    }

    public DetectedPhrase getDetectedPhrase() {
        return this.mDetectedPhrase;
    }

    public String toString() {
        return "HotwordDetectedResult { confidenceLevel = " + this.mConfidenceLevel + ", mediaSyncEvent = " + this.mMediaSyncEvent + ", hotwordOffsetMillis = " + this.mHotwordOffsetMillis + ", hotwordDurationMillis = " + this.mHotwordDurationMillis + ", audioChannel = " + this.mAudioChannel + ", hotwordDetectionPersonalized = " + this.mHotwordDetectionPersonalized + ", score = " + this.mScore + ", personalizedScore = " + this.mPersonalizedScore + ", audioStreams = " + this.mAudioStreams + ", extras = " + this.mExtras + ", detectedPhrase = " + this.mDetectedPhrase + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HotwordDetectedResult that = (HotwordDetectedResult) o;
        if (this.mConfidenceLevel == that.mConfidenceLevel && Objects.equals(this.mMediaSyncEvent, that.mMediaSyncEvent) && this.mHotwordOffsetMillis == that.mHotwordOffsetMillis && this.mHotwordDurationMillis == that.mHotwordDurationMillis && this.mAudioChannel == that.mAudioChannel && this.mHotwordDetectionPersonalized == that.mHotwordDetectionPersonalized && this.mScore == that.mScore && this.mPersonalizedScore == that.mPersonalizedScore && Objects.equals(this.mAudioStreams, that.mAudioStreams) && Objects.equals(this.mExtras, that.mExtras) && Objects.equals(this.mDetectedPhrase, that.mDetectedPhrase)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mConfidenceLevel;
        return (((((((((((((((((((_hash * 31) + Objects.hashCode(this.mMediaSyncEvent)) * 31) + this.mHotwordOffsetMillis) * 31) + this.mHotwordDurationMillis) * 31) + this.mAudioChannel) * 31) + Boolean.hashCode(this.mHotwordDetectionPersonalized)) * 31) + this.mScore) * 31) + this.mPersonalizedScore) * 31) + Objects.hashCode(this.mAudioStreams)) * 31) + Objects.hashCode(this.mExtras)) * 31) + Objects.hashCode(this.mDetectedPhrase);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int flg = this.mHotwordDetectionPersonalized ? 0 | 32 : 0;
        if (this.mMediaSyncEvent != null) {
            flg |= 2;
        }
        dest.writeInt(flg);
        dest.writeInt(this.mConfidenceLevel);
        MediaSyncEvent mediaSyncEvent = this.mMediaSyncEvent;
        if (mediaSyncEvent != null) {
            dest.writeTypedObject(mediaSyncEvent, flags);
        }
        dest.writeInt(this.mHotwordOffsetMillis);
        dest.writeInt(this.mHotwordDurationMillis);
        dest.writeInt(this.mAudioChannel);
        dest.writeInt(this.mScore);
        dest.writeInt(this.mPersonalizedScore);
        dest.writeParcelableList(this.mAudioStreams, flags);
        dest.writeTypedObject(this.mExtras, flags);
        dest.writeTypedObject(this.mDetectedPhrase, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    HotwordDetectedResult(Parcel in) {
        this.mMediaSyncEvent = null;
        this.mHotwordOffsetMillis = -1;
        this.mHotwordDurationMillis = 0;
        this.mAudioChannel = -1;
        this.mHotwordDetectionPersonalized = false;
        this.mDetectedPhrase = new DetectedPhrase.Builder().build();
        int flg = in.readInt();
        boolean hotwordDetectionPersonalized = (flg & 32) != 0;
        int confidenceLevel = in.readInt();
        MediaSyncEvent mediaSyncEvent = (flg & 2) == 0 ? null : (MediaSyncEvent) in.readTypedObject(MediaSyncEvent.CREATOR);
        int hotwordOffsetMillis = in.readInt();
        int hotwordDurationMillis = in.readInt();
        int audioChannel = in.readInt();
        int score = in.readInt();
        int personalizedScore = in.readInt();
        ArrayList arrayList = new ArrayList();
        in.readParcelableList(arrayList, HotwordAudioStream.class.getClassLoader());
        PersistableBundle extras = (PersistableBundle) in.readTypedObject(PersistableBundle.CREATOR);
        DetectedPhrase detectedPhrase = (DetectedPhrase) in.readTypedObject(DetectedPhrase.CREATOR);
        this.mConfidenceLevel = confidenceLevel;
        AnnotationValidations.validate((Class<? extends Annotation>) HotwordConfidenceLevelValue.class, (Annotation) null, confidenceLevel);
        this.mMediaSyncEvent = mediaSyncEvent;
        this.mHotwordOffsetMillis = hotwordOffsetMillis;
        this.mHotwordDurationMillis = hotwordDurationMillis;
        this.mAudioChannel = audioChannel;
        this.mHotwordDetectionPersonalized = hotwordDetectionPersonalized;
        this.mScore = score;
        this.mPersonalizedScore = personalizedScore;
        this.mAudioStreams = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
        this.mExtras = extras;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) extras);
        this.mDetectedPhrase = detectedPhrase;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) detectedPhrase);
        onConstructed();
    }

    /* loaded from: classes3.dex */
    public static final class Builder extends BaseBuilder {
        private int mAudioChannel;
        private List<HotwordAudioStream> mAudioStreams;
        private long mBuilderFieldsSet = 0;
        private int mConfidenceLevel;
        private DetectedPhrase mDetectedPhrase;
        private PersistableBundle mExtras;
        private boolean mHotwordDetectionPersonalized;
        private int mHotwordDurationMillis;
        private int mHotwordOffsetMillis;
        private MediaSyncEvent mMediaSyncEvent;
        private int mPersonalizedScore;
        private int mScore;

        @Override // android.service.voice.HotwordDetectedResult.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setAudioStreams(List list) {
            return super.setAudioStreams(list);
        }

        @Override // android.service.voice.HotwordDetectedResult.BaseBuilder
        @Deprecated
        public /* bridge */ /* synthetic */ Builder setHotwordPhraseId(int i) {
            return super.setHotwordPhraseId(i);
        }

        public Builder setConfidenceLevel(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mConfidenceLevel = value;
            return this;
        }

        public Builder setMediaSyncEvent(MediaSyncEvent value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mMediaSyncEvent = value;
            return this;
        }

        public Builder setHotwordOffsetMillis(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mHotwordOffsetMillis = value;
            return this;
        }

        public Builder setHotwordDurationMillis(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mHotwordDurationMillis = value;
            return this;
        }

        public Builder setAudioChannel(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            this.mAudioChannel = value;
            return this;
        }

        public Builder setHotwordDetectionPersonalized(boolean value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 32;
            this.mHotwordDetectionPersonalized = value;
            return this;
        }

        public Builder setScore(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 64;
            this.mScore = value;
            return this;
        }

        public Builder setPersonalizedScore(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 128;
            this.mPersonalizedScore = value;
            return this;
        }

        public Builder setExtras(PersistableBundle value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 512;
            this.mExtras = value;
            return this;
        }

        public Builder setDetectedPhrase(DetectedPhrase value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1024;
            this.mDetectedPhrase = value;
            return this;
        }

        public HotwordDetectedResult build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 2048;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mConfidenceLevel = HotwordDetectedResult.defaultConfidenceLevel();
            }
            long j2 = this.mBuilderFieldsSet;
            if ((2 & j2) == 0) {
                this.mMediaSyncEvent = null;
            }
            if ((4 & j2) == 0) {
                this.mHotwordOffsetMillis = -1;
            }
            if ((8 & j2) == 0) {
                this.mHotwordDurationMillis = 0;
            }
            if ((16 & j2) == 0) {
                this.mAudioChannel = -1;
            }
            if ((32 & j2) == 0) {
                this.mHotwordDetectionPersonalized = false;
            }
            if ((j2 & 64) == 0) {
                this.mScore = HotwordDetectedResult.defaultScore();
            }
            if ((this.mBuilderFieldsSet & 128) == 0) {
                this.mPersonalizedScore = HotwordDetectedResult.defaultPersonalizedScore();
            }
            if ((this.mBuilderFieldsSet & 256) == 0) {
                this.mAudioStreams = HotwordDetectedResult.defaultAudioStreams();
            }
            if ((this.mBuilderFieldsSet & 512) == 0) {
                this.mExtras = HotwordDetectedResult.defaultExtras();
            }
            if ((this.mBuilderFieldsSet & 1024) == 0) {
                this.mDetectedPhrase = new DetectedPhrase.Builder().build();
            }
            HotwordDetectedResult o = new HotwordDetectedResult(this.mConfidenceLevel, this.mMediaSyncEvent, this.mHotwordOffsetMillis, this.mHotwordDurationMillis, this.mAudioChannel, this.mHotwordDetectionPersonalized, this.mScore, this.mPersonalizedScore, this.mAudioStreams, this.mExtras, this.mDetectedPhrase);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 2048) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
