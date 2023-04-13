package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class DetectedPhrase implements Parcelable {
    public static final Parcelable.Creator<DetectedPhrase> CREATOR = new Parcelable.Creator<DetectedPhrase>() { // from class: android.service.voice.DetectedPhrase.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DetectedPhrase[] newArray(int size) {
            return new DetectedPhrase[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DetectedPhrase createFromParcel(Parcel in) {
            return new DetectedPhrase(in);
        }
    };
    private int mId;
    private String mPhrase;

    static int defaultHotwordPhraseId() {
        return 0;
    }

    public Builder buildUpon() {
        return new Builder().setId(this.mId).setPhrase(this.mPhrase);
    }

    private void onConstructed() {
        Preconditions.checkArgumentNonnegative(this.mId, "hotwordPhraseId");
    }

    DetectedPhrase(int id, String phrase) {
        this.mId = 0;
        this.mPhrase = null;
        this.mId = id;
        this.mPhrase = phrase;
        onConstructed();
    }

    public int getId() {
        return this.mId;
    }

    public String getPhrase() {
        return this.mPhrase;
    }

    public String toString() {
        return "DetectedPhrase { id = " + this.mId + ", phrase = " + this.mPhrase + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DetectedPhrase that = (DetectedPhrase) o;
        if (this.mId == that.mId && Objects.equals(this.mPhrase, that.mPhrase)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mId;
        return (_hash * 31) + Objects.hashCode(this.mPhrase);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mPhrase != null ? (byte) (0 | 2) : (byte) 0;
        dest.writeByte(flg);
        dest.writeInt(this.mId);
        String str = this.mPhrase;
        if (str != null) {
            dest.writeString(str);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    DetectedPhrase(Parcel in) {
        this.mId = 0;
        this.mPhrase = null;
        byte flg = in.readByte();
        int id = in.readInt();
        String phrase = (flg & 2) != 0 ? in.readString() : null;
        this.mId = id;
        this.mPhrase = phrase;
        onConstructed();
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private int mId;
        private String mPhrase;

        public Builder setId(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mId = value;
            return this;
        }

        public Builder setPhrase(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mPhrase = value;
            return this;
        }

        public DetectedPhrase build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 4;
            this.mBuilderFieldsSet = j;
            if ((1 & j) == 0) {
                this.mId = 0;
            }
            if ((j & 2) == 0) {
                this.mPhrase = null;
            }
            DetectedPhrase o = new DetectedPhrase(this.mId, this.mPhrase);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 4) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
