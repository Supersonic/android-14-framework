package android.media;

import android.icu.util.ULocale;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioPresentation implements Parcelable {
    public static final int CONTENT_COMMENTARY = 5;
    public static final int CONTENT_DIALOG = 4;
    public static final int CONTENT_EMERGENCY = 6;
    public static final int CONTENT_HEARING_IMPAIRED = 3;
    public static final int CONTENT_MAIN = 0;
    public static final int CONTENT_MUSIC_AND_EFFECTS = 1;
    public static final int CONTENT_UNKNOWN = -1;
    public static final int CONTENT_VISUALLY_IMPAIRED = 2;
    public static final int CONTENT_VOICEOVER = 7;
    public static final Parcelable.Creator<AudioPresentation> CREATOR = new Parcelable.Creator<AudioPresentation>() { // from class: android.media.AudioPresentation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPresentation createFromParcel(Parcel in) {
            return new AudioPresentation(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioPresentation[] newArray(int size) {
            return new AudioPresentation[size];
        }
    };
    public static final int MASTERED_FOR_3D = 3;
    public static final int MASTERED_FOR_HEADPHONE = 4;
    public static final int MASTERED_FOR_STEREO = 1;
    public static final int MASTERED_FOR_SURROUND = 2;
    public static final int MASTERING_NOT_INDICATED = 0;
    public static final int PRESENTATION_ID_UNKNOWN = -1;
    public static final int PROGRAM_ID_UNKNOWN = -1;
    private final boolean mAudioDescriptionAvailable;
    private final boolean mDialogueEnhancementAvailable;
    private final HashMap<ULocale, String> mLabels;
    private final ULocale mLanguage;
    private final int mMasteringIndication;
    private final int mPresentationId;
    private final int mProgramId;
    private final boolean mSpokenSubtitlesAvailable;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ContentClassifier {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface MasteringIndicationType {
    }

    private AudioPresentation(int presentationId, int programId, ULocale language, int masteringIndication, boolean audioDescriptionAvailable, boolean spokenSubtitlesAvailable, boolean dialogueEnhancementAvailable, Map<ULocale, String> labels) {
        this.mPresentationId = presentationId;
        this.mProgramId = programId;
        this.mLanguage = language;
        this.mMasteringIndication = masteringIndication;
        this.mAudioDescriptionAvailable = audioDescriptionAvailable;
        this.mSpokenSubtitlesAvailable = spokenSubtitlesAvailable;
        this.mDialogueEnhancementAvailable = dialogueEnhancementAvailable;
        this.mLabels = new HashMap<>(labels);
    }

    private AudioPresentation(Parcel in) {
        this.mPresentationId = in.readInt();
        this.mProgramId = in.readInt();
        this.mLanguage = (ULocale) in.readSerializable(ULocale.class.getClassLoader(), ULocale.class);
        this.mMasteringIndication = in.readInt();
        this.mAudioDescriptionAvailable = in.readBoolean();
        this.mSpokenSubtitlesAvailable = in.readBoolean();
        this.mDialogueEnhancementAvailable = in.readBoolean();
        this.mLabels = (HashMap) in.readSerializable(HashMap.class.getClassLoader(), HashMap.class);
    }

    public int getPresentationId() {
        return this.mPresentationId;
    }

    public int getProgramId() {
        return this.mProgramId;
    }

    public Map<Locale, String> getLabels() {
        Map<Locale, String> localeLabels = new HashMap<>(this.mLabels.size());
        for (Map.Entry<ULocale, String> entry : this.mLabels.entrySet()) {
            localeLabels.put(entry.getKey().toLocale(), entry.getValue());
        }
        return localeLabels;
    }

    private Map<ULocale, String> getULabels() {
        return this.mLabels;
    }

    public Locale getLocale() {
        return this.mLanguage.toLocale();
    }

    private ULocale getULocale() {
        return this.mLanguage;
    }

    public int getMasteringIndication() {
        return this.mMasteringIndication;
    }

    public boolean hasAudioDescription() {
        return this.mAudioDescriptionAvailable;
    }

    public boolean hasSpokenSubtitles() {
        return this.mSpokenSubtitlesAvailable;
    }

    public boolean hasDialogueEnhancement() {
        return this.mDialogueEnhancementAvailable;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof AudioPresentation) {
            AudioPresentation obj = (AudioPresentation) o;
            return this.mPresentationId == obj.getPresentationId() && this.mProgramId == obj.getProgramId() && this.mLanguage.equals(obj.getULocale()) && this.mMasteringIndication == obj.getMasteringIndication() && this.mAudioDescriptionAvailable == obj.hasAudioDescription() && this.mSpokenSubtitlesAvailable == obj.hasSpokenSubtitles() && this.mDialogueEnhancementAvailable == obj.hasDialogueEnhancement() && this.mLabels.equals(obj.getULabels());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPresentationId), Integer.valueOf(this.mProgramId), Integer.valueOf(this.mLanguage.hashCode()), Integer.valueOf(this.mMasteringIndication), Boolean.valueOf(this.mAudioDescriptionAvailable), Boolean.valueOf(this.mSpokenSubtitlesAvailable), Boolean.valueOf(this.mDialogueEnhancementAvailable), Integer.valueOf(this.mLabels.hashCode()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName() + " ");
        sb.append("{ presentation id=" + this.mPresentationId);
        sb.append(", program id=" + this.mProgramId);
        sb.append(", language=" + this.mLanguage);
        sb.append(", labels=" + this.mLabels);
        sb.append(", mastering indication=" + this.mMasteringIndication);
        sb.append(", audio description=" + this.mAudioDescriptionAvailable);
        sb.append(", spoken subtitles=" + this.mSpokenSubtitlesAvailable);
        sb.append(", dialogue enhancement=" + this.mDialogueEnhancementAvailable);
        sb.append(" }");
        return sb.toString();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private final int mPresentationId;
        private int mProgramId = -1;
        private ULocale mLanguage = new ULocale("");
        private int mMasteringIndication = 0;
        private boolean mAudioDescriptionAvailable = false;
        private boolean mSpokenSubtitlesAvailable = false;
        private boolean mDialogueEnhancementAvailable = false;
        private HashMap<ULocale, String> mLabels = new HashMap<>();

        public Builder(int presentationId) {
            this.mPresentationId = presentationId;
        }

        public Builder setProgramId(int programId) {
            this.mProgramId = programId;
            return this;
        }

        public Builder setLocale(ULocale language) {
            this.mLanguage = language;
            return this;
        }

        public Builder setMasteringIndication(int masteringIndication) {
            if (masteringIndication != 0 && masteringIndication != 1 && masteringIndication != 2 && masteringIndication != 3 && masteringIndication != 4) {
                throw new IllegalArgumentException("Unknown mastering indication: " + masteringIndication);
            }
            this.mMasteringIndication = masteringIndication;
            return this;
        }

        public Builder setLabels(Map<ULocale, CharSequence> labels) {
            this.mLabels.clear();
            for (Map.Entry<ULocale, CharSequence> entry : labels.entrySet()) {
                this.mLabels.put(entry.getKey(), entry.getValue().toString());
            }
            return this;
        }

        public Builder setHasAudioDescription(boolean audioDescriptionAvailable) {
            this.mAudioDescriptionAvailable = audioDescriptionAvailable;
            return this;
        }

        public Builder setHasSpokenSubtitles(boolean spokenSubtitlesAvailable) {
            this.mSpokenSubtitlesAvailable = spokenSubtitlesAvailable;
            return this;
        }

        public Builder setHasDialogueEnhancement(boolean dialogueEnhancementAvailable) {
            this.mDialogueEnhancementAvailable = dialogueEnhancementAvailable;
            return this;
        }

        public AudioPresentation build() {
            return new AudioPresentation(this.mPresentationId, this.mProgramId, this.mLanguage, this.mMasteringIndication, this.mAudioDescriptionAvailable, this.mSpokenSubtitlesAvailable, this.mDialogueEnhancementAvailable, this.mLabels);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getPresentationId());
        dest.writeInt(getProgramId());
        dest.writeSerializable(getULocale());
        dest.writeInt(getMasteringIndication());
        dest.writeBoolean(hasAudioDescription());
        dest.writeBoolean(hasSpokenSubtitles());
        dest.writeBoolean(hasDialogueEnhancement());
        dest.writeSerializable(this.mLabels);
    }
}
