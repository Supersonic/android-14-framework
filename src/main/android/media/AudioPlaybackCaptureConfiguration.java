package android.media;

import android.media.AudioAttributes;
import android.media.audiopolicy.AudioMix;
import android.media.audiopolicy.AudioMixingRule;
import android.media.projection.MediaProjection;
import android.p008os.RemoteException;
import com.android.internal.util.Preconditions;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
/* loaded from: classes2.dex */
public final class AudioPlaybackCaptureConfiguration {
    private final AudioMixingRule mAudioMixingRule;
    private final MediaProjection mProjection;

    private AudioPlaybackCaptureConfiguration(AudioMixingRule audioMixingRule, MediaProjection projection) {
        this.mAudioMixingRule = audioMixingRule;
        this.mProjection = projection;
    }

    public MediaProjection getMediaProjection() {
        return this.mProjection;
    }

    public int[] getMatchingUsages() {
        return getIntPredicates(1, new ToIntFunction() { // from class: android.media.AudioPlaybackCaptureConfiguration$$ExternalSyntheticLambda3
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int usage;
                usage = ((AudioMixingRule.AudioMixMatchCriterion) obj).getAudioAttributes().getUsage();
                return usage;
            }
        });
    }

    public int[] getMatchingUids() {
        return getIntPredicates(4, new ToIntFunction() { // from class: android.media.AudioPlaybackCaptureConfiguration$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intProp;
                intProp = ((AudioMixingRule.AudioMixMatchCriterion) obj).getIntProp();
                return intProp;
            }
        });
    }

    public int[] getExcludeUsages() {
        return getIntPredicates(32769, new ToIntFunction() { // from class: android.media.AudioPlaybackCaptureConfiguration$$ExternalSyntheticLambda2
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int usage;
                usage = ((AudioMixingRule.AudioMixMatchCriterion) obj).getAudioAttributes().getUsage();
                return usage;
            }
        });
    }

    public int[] getExcludeUids() {
        return getIntPredicates(32772, new ToIntFunction() { // from class: android.media.AudioPlaybackCaptureConfiguration$$ExternalSyntheticLambda4
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intProp;
                intProp = ((AudioMixingRule.AudioMixMatchCriterion) obj).getIntProp();
                return intProp;
            }
        });
    }

    private int[] getIntPredicates(final int rule, ToIntFunction<AudioMixingRule.AudioMixMatchCriterion> getPredicate) {
        return this.mAudioMixingRule.getCriteria().stream().filter(new Predicate() { // from class: android.media.AudioPlaybackCaptureConfiguration$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return AudioPlaybackCaptureConfiguration.lambda$getIntPredicates$4(rule, (AudioMixingRule.AudioMixMatchCriterion) obj);
            }
        }).mapToInt(getPredicate).toArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getIntPredicates$4(int rule, AudioMixingRule.AudioMixMatchCriterion criterion) {
        return criterion.getRule() == rule;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public android.media.audiopolicy.AudioMix createAudioMix(AudioFormat audioFormat) {
        return new AudioMix.Builder(this.mAudioMixingRule).setFormat(audioFormat).setRouteFlags(3).build();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private static final String ERROR_MESSAGE_MISMATCHED_RULES = "Inclusive and exclusive usage rules cannot be combined";
        private static final String ERROR_MESSAGE_NON_AUDIO_PROJECTION = "MediaProjection can not project audio";
        private static final String ERROR_MESSAGE_START_ACTIVITY_FAILED = "startActivityForResult failed";
        private static final int MATCH_TYPE_EXCLUSIVE = 2;
        private static final int MATCH_TYPE_INCLUSIVE = 1;
        private static final int MATCH_TYPE_UNSPECIFIED = 0;
        private final AudioMixingRule.Builder mAudioMixingRuleBuilder;
        private final MediaProjection mProjection;
        private int mUsageMatchType = 0;
        private int mUidMatchType = 0;

        public Builder(MediaProjection projection) {
            Preconditions.checkNotNull(projection);
            try {
                Preconditions.checkArgument(projection.getProjection().canProjectAudio(), ERROR_MESSAGE_NON_AUDIO_PROJECTION);
                this.mProjection = projection;
                this.mAudioMixingRuleBuilder = new AudioMixingRule.Builder();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public Builder addMatchingUsage(int usage) {
            Preconditions.checkState(this.mUsageMatchType != 2, ERROR_MESSAGE_MISMATCHED_RULES);
            this.mAudioMixingRuleBuilder.addRule(new AudioAttributes.Builder().setUsage(usage).build(), 1);
            this.mUsageMatchType = 1;
            return this;
        }

        public Builder addMatchingUid(int uid) {
            Preconditions.checkState(this.mUidMatchType != 2, ERROR_MESSAGE_MISMATCHED_RULES);
            this.mAudioMixingRuleBuilder.addMixRule(4, Integer.valueOf(uid));
            this.mUidMatchType = 1;
            return this;
        }

        public Builder excludeUsage(int usage) {
            Preconditions.checkState(this.mUsageMatchType != 1, ERROR_MESSAGE_MISMATCHED_RULES);
            this.mAudioMixingRuleBuilder.excludeRule(new AudioAttributes.Builder().setUsage(usage).build(), 1);
            this.mUsageMatchType = 2;
            return this;
        }

        public Builder excludeUid(int uid) {
            Preconditions.checkState(this.mUidMatchType != 1, ERROR_MESSAGE_MISMATCHED_RULES);
            this.mAudioMixingRuleBuilder.excludeMixRule(4, Integer.valueOf(uid));
            this.mUidMatchType = 2;
            return this;
        }

        public AudioPlaybackCaptureConfiguration build() {
            return new AudioPlaybackCaptureConfiguration(this.mAudioMixingRuleBuilder.build(), this.mProjection);
        }
    }
}
