package android.hardware.soundtrigger;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import com.android.internal.util.AnnotationValidations;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class KeyphraseMetadata implements Parcelable {
    public static final Parcelable.Creator<KeyphraseMetadata> CREATOR = new Parcelable.Creator<KeyphraseMetadata>() { // from class: android.hardware.soundtrigger.KeyphraseMetadata.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyphraseMetadata[] newArray(int size) {
            return new KeyphraseMetadata[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyphraseMetadata createFromParcel(Parcel in) {
            return new KeyphraseMetadata(in);
        }
    };
    private final int mId;
    private final String mKeyphrase;
    private final int mRecognitionModeFlags;
    private final ArraySet<Locale> mSupportedLocales;

    public KeyphraseMetadata(int id, String keyphrase, Set<Locale> supportedLocales, int recognitionModeFlags) {
        this.mId = id;
        this.mKeyphrase = keyphrase;
        this.mSupportedLocales = new ArraySet<>(supportedLocales);
        this.mRecognitionModeFlags = recognitionModeFlags;
    }

    public int getId() {
        return this.mId;
    }

    public String getKeyphrase() {
        return this.mKeyphrase;
    }

    public Set<Locale> getSupportedLocales() {
        return this.mSupportedLocales;
    }

    public int getRecognitionModeFlags() {
        return this.mRecognitionModeFlags;
    }

    public boolean supportsPhrase(String phrase) {
        return getKeyphrase().isEmpty() || getKeyphrase().equalsIgnoreCase(phrase);
    }

    public boolean supportsLocale(Locale locale) {
        return getSupportedLocales().isEmpty() || getSupportedLocales().contains(locale);
    }

    public String toString() {
        return "KeyphraseMetadata { id = " + this.mId + ", keyphrase = " + this.mKeyphrase + ", supportedLocales = " + this.mSupportedLocales + ", recognitionModeFlags = " + this.mRecognitionModeFlags + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyphraseMetadata that = (KeyphraseMetadata) o;
        if (this.mId == that.mId && Objects.equals(this.mKeyphrase, that.mKeyphrase) && Objects.equals(this.mSupportedLocales, that.mSupportedLocales) && this.mRecognitionModeFlags == that.mRecognitionModeFlags) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mId;
        return (((((_hash * 31) + Objects.hashCode(this.mKeyphrase)) * 31) + Objects.hashCode(this.mSupportedLocales)) * 31) + this.mRecognitionModeFlags;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mKeyphrase);
        dest.writeArraySet(this.mSupportedLocales);
        dest.writeInt(this.mRecognitionModeFlags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    KeyphraseMetadata(Parcel in) {
        int id = in.readInt();
        String keyphrase = in.readString();
        ArraySet readArraySet = in.readArraySet(null);
        int recognitionModeFlags = in.readInt();
        this.mId = id;
        this.mKeyphrase = keyphrase;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) keyphrase);
        this.mSupportedLocales = readArraySet;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) readArraySet);
        this.mRecognitionModeFlags = recognitionModeFlags;
    }

    @Deprecated
    private void __metadata() {
    }
}
