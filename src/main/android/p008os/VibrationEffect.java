package android.p008os;

import android.content.ContentResolver;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Parcelable;
import android.p008os.vibrator.PrebakedSegment;
import android.p008os.vibrator.PrimitiveSegment;
import android.p008os.vibrator.RampSegment;
import android.p008os.vibrator.StepSegment;
import android.p008os.vibrator.VibrationEffectSegment;
import android.util.MathUtils;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* renamed from: android.os.VibrationEffect */
/* loaded from: classes3.dex */
public abstract class VibrationEffect implements Parcelable {
    public static final int DEFAULT_AMPLITUDE = -1;
    public static final int EFFECT_CLICK = 0;
    public static final int EFFECT_DOUBLE_CLICK = 1;
    public static final int EFFECT_HEAVY_CLICK = 5;
    public static final int EFFECT_POP = 4;
    public static final int EFFECT_STRENGTH_LIGHT = 0;
    public static final int EFFECT_STRENGTH_MEDIUM = 1;
    public static final int EFFECT_STRENGTH_STRONG = 2;
    public static final int EFFECT_TEXTURE_TICK = 21;
    public static final int EFFECT_THUD = 3;
    public static final int EFFECT_TICK = 2;
    public static final int MAX_AMPLITUDE = 255;
    private static final long MAX_HAPTIC_FEEDBACK_COMPOSITION_SIZE = 3;
    private static final long MAX_HAPTIC_FEEDBACK_DURATION = 1000;
    private static final float SCALE_GAMMA = 0.65f;
    public static final int[] RINGTONES = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    public static final Parcelable.Creator<VibrationEffect> CREATOR = new Parcelable.Creator<VibrationEffect>() { // from class: android.os.VibrationEffect.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VibrationEffect createFromParcel(Parcel in) {
            return new Composed(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VibrationEffect[] newArray(int size) {
            return new VibrationEffect[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.VibrationEffect$EffectType */
    /* loaded from: classes3.dex */
    public @interface EffectType {
    }

    public abstract boolean areVibrationFeaturesSupported(Vibrator vibrator);

    public abstract long[] computeCreateWaveformOffOnTimingsOrNull();

    public abstract long getDuration();

    public abstract <T extends VibrationEffect> T resolve(int i);

    public abstract <T extends VibrationEffect> T scale(float f);

    public abstract void validate();

    public static VibrationEffect createOneShot(long milliseconds, int amplitude) {
        if (amplitude != 0) {
            return createWaveform(new long[]{milliseconds}, new int[]{amplitude}, -1);
        }
        throw new IllegalArgumentException("amplitude must either be DEFAULT_AMPLITUDE, or between 1 and 255 inclusive (amplitude=" + amplitude + NavigationBarInflaterView.KEY_CODE_END);
    }

    public static VibrationEffect createWaveform(long[] timings, int repeat) {
        int[] amplitudes = new int[timings.length];
        for (int i = 0; i < timings.length / 2; i++) {
            amplitudes[(i * 2) + 1] = -1;
        }
        return createWaveform(timings, amplitudes, repeat);
    }

    public static VibrationEffect createWaveform(long[] timings, int[] amplitudes, int repeat) {
        if (timings.length != amplitudes.length) {
            throw new IllegalArgumentException("timing and amplitude arrays must be of equal length (timings.length=" + timings.length + ", amplitudes.length=" + amplitudes.length + NavigationBarInflaterView.KEY_CODE_END);
        }
        List<StepSegment> segments = new ArrayList<>();
        for (int i = 0; i < timings.length; i++) {
            float parsedAmplitude = amplitudes[i] == -1 ? -1.0f : amplitudes[i] / 255.0f;
            segments.add(new StepSegment(parsedAmplitude, 0.0f, (int) timings[i]));
        }
        VibrationEffect effect = new Composed(segments, repeat);
        effect.validate();
        return effect;
    }

    public static VibrationEffect createPredefined(int effectId) {
        return get(effectId, true);
    }

    public static VibrationEffect get(int effectId) {
        return get(effectId, true);
    }

    public static VibrationEffect get(int effectId, boolean fallback) {
        VibrationEffect effect = new Composed(new PrebakedSegment(effectId, fallback, 1));
        effect.validate();
        return effect;
    }

    public static VibrationEffect get(Uri uri, Context context) {
        Uri mappedUri;
        String[] uris = context.getResources().getStringArray(C4057R.array.config_ringtoneEffectUris);
        if (uris.length == 0) {
            return null;
        }
        ContentResolver cr = context.getContentResolver();
        Uri uncanonicalUri = cr.uncanonicalize(uri);
        if (uncanonicalUri == null) {
            uncanonicalUri = uri;
        }
        for (int i = 0; i < uris.length; i++) {
            int[] iArr = RINGTONES;
            if (i >= iArr.length) {
                break;
            } else if (uris[i] != null && (mappedUri = cr.uncanonicalize(Uri.parse(uris[i]))) != null && mappedUri.equals(uncanonicalUri)) {
                return get(iArr[i]);
            }
        }
        return null;
    }

    public static Composition startComposition() {
        return new Composition();
    }

    public static WaveformBuilder startWaveform() {
        return new WaveformBuilder();
    }

    public static WaveformBuilder startWaveform(VibrationParameter initialParameter) {
        WaveformBuilder builder = startWaveform();
        builder.addTransition(Duration.ZERO, initialParameter);
        return builder;
    }

    public static WaveformBuilder startWaveform(VibrationParameter initialParameter1, VibrationParameter initialParameter2) {
        WaveformBuilder builder = startWaveform();
        builder.addTransition(Duration.ZERO, initialParameter1, initialParameter2);
        return builder;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean isHapticFeedbackCandidate() {
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends VibrationEffect> T applyEffectStrength(int effectStrength) {
        return this;
    }

    public static float scale(float intensity, float scaleFactor) {
        float scale = MathUtils.pow(scaleFactor, 1.5384616f);
        if (scaleFactor <= 1.0f) {
            return intensity * scale;
        }
        float extraScale = MathUtils.pow(scaleFactor, 4.0f - scaleFactor);
        float x = intensity * scale * extraScale;
        float maxX = scale * extraScale;
        float expX = MathUtils.exp(x);
        float expMaxX = MathUtils.exp(maxX);
        float a = (expMaxX + 1.0f) / (expMaxX - 1.0f);
        float fx = (expX - 1.0f) / (expX + 1.0f);
        return MathUtils.constrain(a * fx, 0.0f, 1.0f);
    }

    public static String effectIdToString(int effectId) {
        switch (effectId) {
            case 0:
                return "CLICK";
            case 1:
                return "DOUBLE_CLICK";
            case 2:
                return "TICK";
            case 3:
                return "THUD";
            case 4:
                return "POP";
            case 5:
                return "HEAVY_CLICK";
            case 21:
                return "TEXTURE_TICK";
            default:
                return Integer.toString(effectId);
        }
    }

    public static String effectStrengthToString(int effectStrength) {
        switch (effectStrength) {
            case 0:
                return "LIGHT";
            case 1:
                return "MEDIUM";
            case 2:
                return "STRONG";
            default:
                return Integer.toString(effectStrength);
        }
    }

    /* renamed from: android.os.VibrationEffect$Composed */
    /* loaded from: classes3.dex */
    public static final class Composed extends VibrationEffect {
        public static final Parcelable.Creator<Composed> CREATOR = new Parcelable.Creator<Composed>() { // from class: android.os.VibrationEffect.Composed.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Composed createFromParcel(Parcel in) {
                return new Composed(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Composed[] newArray(int size) {
                return new Composed[size];
            }
        };
        private final int mRepeatIndex;
        private final ArrayList<VibrationEffectSegment> mSegments;

        Composed(Parcel in) {
            this(in.readArrayList(VibrationEffectSegment.class.getClassLoader(), VibrationEffectSegment.class), in.readInt());
        }

        Composed(VibrationEffectSegment segment) {
            this(Arrays.asList(segment), -1);
        }

        public Composed(List<? extends VibrationEffectSegment> segments, int repeatIndex) {
            this.mSegments = new ArrayList<>(segments);
            this.mRepeatIndex = repeatIndex;
        }

        public List<VibrationEffectSegment> getSegments() {
            return this.mSegments;
        }

        public int getRepeatIndex() {
            return this.mRepeatIndex;
        }

        @Override // android.p008os.VibrationEffect
        public long[] computeCreateWaveformOffOnTimingsOrNull() {
            if (getRepeatIndex() >= 0) {
                return null;
            }
            List<VibrationEffectSegment> segments = getSegments();
            long[] patternBuffer = new long[segments.size() + 1];
            int patternIndex = 0;
            for (int i = 0; i < segments.size(); i++) {
                StepSegment stepSegment = castToValidStepSegmentForOffOnTimingsOrNull(segments.get(i));
                if (stepSegment == null) {
                    return null;
                }
                boolean isSegmentOff = stepSegment.getAmplitude() == 0.0f;
                boolean isCurrentPatternIndexOff = patternIndex % 2 == 0;
                if (isSegmentOff != isCurrentPatternIndexOff) {
                    patternIndex++;
                }
                patternBuffer[patternIndex] = patternBuffer[patternIndex] + stepSegment.getDuration();
            }
            return Arrays.copyOf(patternBuffer, patternIndex + 1);
        }

        @Override // android.p008os.VibrationEffect
        public void validate() {
            int segmentCount = this.mSegments.size();
            boolean hasNonZeroDuration = false;
            int i = 0;
            while (true) {
                boolean z = false;
                if (i >= segmentCount) {
                    break;
                }
                VibrationEffectSegment segment = this.mSegments.get(i);
                segment.validate();
                if (segment.getDuration() != 0) {
                    z = true;
                }
                hasNonZeroDuration |= z;
                i++;
            }
            if (!hasNonZeroDuration) {
                throw new IllegalArgumentException("at least one timing must be non-zero (segments=" + this.mSegments + NavigationBarInflaterView.KEY_CODE_END);
            }
            int i2 = this.mRepeatIndex;
            if (i2 != -1) {
                Preconditions.checkArgumentInRange(i2, 0, segmentCount - 1, "repeat index must be within the bounds of the segments (segments.length=" + segmentCount + ", index=" + this.mRepeatIndex + NavigationBarInflaterView.KEY_CODE_END);
            }
        }

        @Override // android.p008os.VibrationEffect
        public long getDuration() {
            if (this.mRepeatIndex >= 0) {
                return Long.MAX_VALUE;
            }
            int segmentCount = this.mSegments.size();
            long totalDuration = 0;
            for (int i = 0; i < segmentCount; i++) {
                long segmentDuration = this.mSegments.get(i).getDuration();
                if (segmentDuration < 0) {
                    return segmentDuration;
                }
                totalDuration += segmentDuration;
            }
            return totalDuration;
        }

        @Override // android.p008os.VibrationEffect
        public boolean areVibrationFeaturesSupported(Vibrator vibrator) {
            Iterator<VibrationEffectSegment> it = this.mSegments.iterator();
            while (it.hasNext()) {
                VibrationEffectSegment segment = it.next();
                if (!segment.areVibrationFeaturesSupported(vibrator)) {
                    return false;
                }
            }
            return true;
        }

        @Override // android.p008os.VibrationEffect
        public boolean isHapticFeedbackCandidate() {
            long totalDuration = getDuration();
            if (totalDuration > 1000) {
                return false;
            }
            int segmentCount = this.mSegments.size();
            if (segmentCount > 3) {
                return false;
            }
            long totalDuration2 = 0;
            for (int i = 0; i < segmentCount; i++) {
                if (!this.mSegments.get(i).isHapticFeedbackCandidate()) {
                    return false;
                }
                long segmentDuration = this.mSegments.get(i).getDuration();
                if (segmentDuration > 0) {
                    totalDuration2 += segmentDuration;
                }
            }
            return totalDuration2 <= 1000;
        }

        @Override // android.p008os.VibrationEffect
        public Composed resolve(int defaultAmplitude) {
            int segmentCount = this.mSegments.size();
            ArrayList<VibrationEffectSegment> resolvedSegments = new ArrayList<>(segmentCount);
            for (int i = 0; i < segmentCount; i++) {
                resolvedSegments.add(this.mSegments.get(i).resolve(defaultAmplitude));
            }
            if (resolvedSegments.equals(this.mSegments)) {
                return this;
            }
            Composed resolved = new Composed(resolvedSegments, this.mRepeatIndex);
            resolved.validate();
            return resolved;
        }

        @Override // android.p008os.VibrationEffect
        public Composed scale(float scaleFactor) {
            int segmentCount = this.mSegments.size();
            ArrayList<VibrationEffectSegment> scaledSegments = new ArrayList<>(segmentCount);
            for (int i = 0; i < segmentCount; i++) {
                scaledSegments.add(this.mSegments.get(i).scale(scaleFactor));
            }
            if (scaledSegments.equals(this.mSegments)) {
                return this;
            }
            Composed scaled = new Composed(scaledSegments, this.mRepeatIndex);
            scaled.validate();
            return scaled;
        }

        @Override // android.p008os.VibrationEffect
        public Composed applyEffectStrength(int effectStrength) {
            int segmentCount = this.mSegments.size();
            ArrayList<VibrationEffectSegment> scaledSegments = new ArrayList<>(segmentCount);
            for (int i = 0; i < segmentCount; i++) {
                scaledSegments.add(this.mSegments.get(i).applyEffectStrength(effectStrength));
            }
            if (scaledSegments.equals(this.mSegments)) {
                return this;
            }
            Composed scaled = new Composed(scaledSegments, this.mRepeatIndex);
            scaled.validate();
            return scaled;
        }

        public boolean equals(Object o) {
            if (o instanceof Composed) {
                Composed other = (Composed) o;
                return this.mSegments.equals(other.mSegments) && this.mRepeatIndex == other.mRepeatIndex;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mSegments, Integer.valueOf(this.mRepeatIndex));
        }

        public String toString() {
            return "Composed{segments=" + this.mSegments + ", repeat=" + this.mRepeatIndex + "}";
        }

        @Override // android.p008os.VibrationEffect, android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeList(this.mSegments);
            out.writeInt(this.mRepeatIndex);
        }

        private static StepSegment castToValidStepSegmentForOffOnTimingsOrNull(VibrationEffectSegment segment) {
            if (segment instanceof StepSegment) {
                StepSegment stepSegment = (StepSegment) segment;
                if (stepSegment.getFrequencyHz() != 0.0f) {
                    return null;
                }
                float amplitude = stepSegment.getAmplitude();
                if (amplitude == 0.0f || amplitude == -1.0f) {
                    return stepSegment;
                }
                return null;
            }
            return null;
        }
    }

    /* renamed from: android.os.VibrationEffect$Composition */
    /* loaded from: classes3.dex */
    public static final class Composition {
        public static final int PRIMITIVE_CLICK = 1;
        public static final int PRIMITIVE_LOW_TICK = 8;
        public static final int PRIMITIVE_NOOP = 0;
        public static final int PRIMITIVE_QUICK_FALL = 6;
        public static final int PRIMITIVE_QUICK_RISE = 4;
        public static final int PRIMITIVE_SLOW_RISE = 5;
        public static final int PRIMITIVE_SPIN = 3;
        public static final int PRIMITIVE_THUD = 2;
        public static final int PRIMITIVE_TICK = 7;
        private final ArrayList<VibrationEffectSegment> mSegments = new ArrayList<>();
        private int mRepeatIndex = -1;

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.os.VibrationEffect$Composition$PrimitiveType */
        /* loaded from: classes3.dex */
        public @interface PrimitiveType {
        }

        /* renamed from: android.os.VibrationEffect$Composition$UnreachableAfterRepeatingIndefinitelyException */
        /* loaded from: classes3.dex */
        public static final class UnreachableAfterRepeatingIndefinitelyException extends IllegalStateException {
            UnreachableAfterRepeatingIndefinitelyException() {
                super("Compositions ending in an indefinitely repeating effect can't be extended");
            }
        }

        Composition() {
        }

        public Composition addOffDuration(Duration duration) {
            int durationMs = (int) duration.toMillis();
            Preconditions.checkArgumentNonnegative(durationMs, "Off period must be non-negative");
            if (durationMs > 0) {
                addSegment(new StepSegment(0.0f, 0.0f, (int) duration.toMillis()));
            }
            return this;
        }

        public Composition addEffect(VibrationEffect effect) {
            return addSegments(effect);
        }

        public Composition repeatEffectIndefinitely(VibrationEffect effect) {
            Preconditions.checkArgument(effect.getDuration() < Long.MAX_VALUE, "Can't repeat an indefinitely repeating effect. Consider addEffect instead.");
            int previousSegmentCount = this.mSegments.size();
            addSegments(effect);
            this.mRepeatIndex = previousSegmentCount;
            return this;
        }

        public Composition addPrimitive(int primitiveId) {
            return addPrimitive(primitiveId, 1.0f, 0);
        }

        public Composition addPrimitive(int primitiveId, float scale) {
            return addPrimitive(primitiveId, scale, 0);
        }

        public Composition addPrimitive(int primitiveId, float scale, int delay) {
            PrimitiveSegment primitive = new PrimitiveSegment(primitiveId, scale, delay);
            primitive.validate();
            return addSegment(primitive);
        }

        private Composition addSegment(VibrationEffectSegment segment) {
            if (this.mRepeatIndex >= 0) {
                throw new UnreachableAfterRepeatingIndefinitelyException();
            }
            this.mSegments.add(segment);
            return this;
        }

        private Composition addSegments(VibrationEffect effect) {
            if (this.mRepeatIndex >= 0) {
                throw new UnreachableAfterRepeatingIndefinitelyException();
            }
            Composed composed = (Composed) effect;
            if (composed.getRepeatIndex() >= 0) {
                this.mRepeatIndex = this.mSegments.size() + composed.getRepeatIndex();
            }
            this.mSegments.addAll(composed.getSegments());
            return this;
        }

        public VibrationEffect compose() {
            if (this.mSegments.isEmpty()) {
                throw new IllegalStateException("Composition must have at least one element to compose.");
            }
            VibrationEffect effect = new Composed(this.mSegments, this.mRepeatIndex);
            effect.validate();
            return effect;
        }

        public static String primitiveToString(int id) {
            switch (id) {
                case 0:
                    return "PRIMITIVE_NOOP";
                case 1:
                    return "PRIMITIVE_CLICK";
                case 2:
                    return "PRIMITIVE_THUD";
                case 3:
                    return "PRIMITIVE_SPIN";
                case 4:
                    return "PRIMITIVE_QUICK_RISE";
                case 5:
                    return "PRIMITIVE_SLOW_RISE";
                case 6:
                    return "PRIMITIVE_QUICK_FALL";
                case 7:
                    return "PRIMITIVE_TICK";
                case 8:
                    return "PRIMITIVE_LOW_TICK";
                default:
                    return Integer.toString(id);
            }
        }
    }

    /* renamed from: android.os.VibrationEffect$WaveformBuilder */
    /* loaded from: classes3.dex */
    public static final class WaveformBuilder {
        private static final float EPSILON = 1.0E-5f;
        private ArrayList<VibrationEffectSegment> mSegments = new ArrayList<>();
        private float mLastAmplitude = 0.0f;
        private float mLastFrequencyHz = 0.0f;

        WaveformBuilder() {
        }

        public WaveformBuilder addTransition(Duration duration, VibrationParameter targetParameter) {
            Preconditions.checkNotNull(duration, "Duration is null");
            checkVibrationParameter(targetParameter, "targetParameter");
            float amplitude = extractTargetAmplitude(targetParameter, null);
            float frequencyHz = extractTargetFrequency(targetParameter, null);
            addTransitionSegment(duration, amplitude, frequencyHz);
            return this;
        }

        public WaveformBuilder addTransition(Duration duration, VibrationParameter targetParameter1, VibrationParameter targetParameter2) {
            Preconditions.checkNotNull(duration, "Duration is null");
            checkVibrationParameter(targetParameter1, "targetParameter1");
            checkVibrationParameter(targetParameter2, "targetParameter2");
            Preconditions.checkArgument(!Objects.equals(targetParameter1.getClass(), targetParameter2.getClass()), "Parameter arguments must specify different parameter types");
            float amplitude = extractTargetAmplitude(targetParameter1, targetParameter2);
            float frequencyHz = extractTargetFrequency(targetParameter1, targetParameter2);
            addTransitionSegment(duration, amplitude, frequencyHz);
            return this;
        }

        public WaveformBuilder addSustain(Duration duration) {
            int durationMs = (int) duration.toMillis();
            Preconditions.checkArgument(durationMs >= 1, "Sustain duration must be >= 1ms");
            this.mSegments.add(new StepSegment(this.mLastAmplitude, this.mLastFrequencyHz, durationMs));
            return this;
        }

        public VibrationEffect build() {
            if (this.mSegments.isEmpty()) {
                throw new IllegalStateException("WaveformBuilder must have at least one transition to build.");
            }
            VibrationEffect effect = new Composed(this.mSegments, -1);
            effect.validate();
            return effect;
        }

        private void checkVibrationParameter(VibrationParameter vibrationParameter, String paramName) {
            Preconditions.checkNotNull(vibrationParameter, "%s is null", paramName);
            Preconditions.checkArgument((vibrationParameter instanceof AmplitudeVibrationParameter) || (vibrationParameter instanceof FrequencyVibrationParameter), "%s is a unknown parameter", paramName);
        }

        private float extractTargetAmplitude(VibrationParameter target1, VibrationParameter target2) {
            if (target2 instanceof AmplitudeVibrationParameter) {
                return ((AmplitudeVibrationParameter) target2).amplitude;
            }
            if (target1 instanceof AmplitudeVibrationParameter) {
                return ((AmplitudeVibrationParameter) target1).amplitude;
            }
            return this.mLastAmplitude;
        }

        private float extractTargetFrequency(VibrationParameter target1, VibrationParameter target2) {
            if (target2 instanceof FrequencyVibrationParameter) {
                return ((FrequencyVibrationParameter) target2).frequencyHz;
            }
            if (target1 instanceof FrequencyVibrationParameter) {
                return ((FrequencyVibrationParameter) target1).frequencyHz;
            }
            return this.mLastFrequencyHz;
        }

        private void addTransitionSegment(Duration duration, float targetAmplitude, float targetFrequency) {
            Preconditions.checkNotNull(duration, "Duration is null");
            Preconditions.checkArgument(!duration.isNegative(), "Transition duration must be non-negative");
            int durationMs = (int) duration.toMillis();
            if (durationMs > 0) {
                if (Math.abs(this.mLastAmplitude - targetAmplitude) < EPSILON && Math.abs(this.mLastFrequencyHz - targetFrequency) < EPSILON) {
                    this.mSegments.add(new StepSegment(targetAmplitude, targetFrequency, durationMs));
                } else {
                    this.mSegments.add(new RampSegment(this.mLastAmplitude, targetAmplitude, this.mLastFrequencyHz, targetFrequency, durationMs));
                }
            }
            this.mLastAmplitude = targetAmplitude;
            this.mLastFrequencyHz = targetFrequency;
        }
    }

    /* renamed from: android.os.VibrationEffect$VibrationParameter */
    /* loaded from: classes3.dex */
    public static class VibrationParameter {
        VibrationParameter() {
        }

        public static VibrationParameter targetAmplitude(float amplitude) {
            return new AmplitudeVibrationParameter(amplitude);
        }

        public static VibrationParameter targetFrequency(float frequencyHz) {
            return new FrequencyVibrationParameter(frequencyHz);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.VibrationEffect$AmplitudeVibrationParameter */
    /* loaded from: classes3.dex */
    public static final class AmplitudeVibrationParameter extends VibrationParameter {
        public final float amplitude;

        AmplitudeVibrationParameter(float amplitude) {
            Preconditions.checkArgument(amplitude >= 0.0f && amplitude <= 1.0f, "Amplitude must be within [0,1]");
            this.amplitude = amplitude;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.VibrationEffect$FrequencyVibrationParameter */
    /* loaded from: classes3.dex */
    public static final class FrequencyVibrationParameter extends VibrationParameter {
        public final float frequencyHz;

        FrequencyVibrationParameter(float frequencyHz) {
            Preconditions.checkArgument(frequencyHz >= 1.0f, "Frequency must be >= 1");
            Preconditions.checkArgument(Float.isFinite(frequencyHz), "Frequency must be finite");
            this.frequencyHz = frequencyHz;
        }
    }
}
