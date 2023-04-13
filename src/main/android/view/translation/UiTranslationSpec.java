package android.view.translation;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes4.dex */
public final class UiTranslationSpec implements Parcelable {
    public static final Parcelable.Creator<UiTranslationSpec> CREATOR = new Parcelable.Creator<UiTranslationSpec>() { // from class: android.view.translation.UiTranslationSpec.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiTranslationSpec[] newArray(int size) {
            return new UiTranslationSpec[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiTranslationSpec createFromParcel(Parcel in) {
            return new UiTranslationSpec(in);
        }
    };
    private boolean mShouldPadContentForCompat;

    public boolean shouldPadContentForCompat() {
        return this.mShouldPadContentForCompat;
    }

    UiTranslationSpec(boolean shouldPadContentForCompat) {
        this.mShouldPadContentForCompat = false;
        this.mShouldPadContentForCompat = shouldPadContentForCompat;
    }

    public String toString() {
        return "UiTranslationSpec { shouldPadContentForCompat = " + this.mShouldPadContentForCompat + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UiTranslationSpec that = (UiTranslationSpec) o;
        if (this.mShouldPadContentForCompat == that.mShouldPadContentForCompat) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Boolean.hashCode(this.mShouldPadContentForCompat);
        return _hash;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mShouldPadContentForCompat ? (byte) (0 | 1) : (byte) 0;
        dest.writeByte(flg);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    UiTranslationSpec(Parcel in) {
        this.mShouldPadContentForCompat = false;
        byte flg = in.readByte();
        boolean shouldPadContentForCompat = (flg & 1) != 0;
        this.mShouldPadContentForCompat = shouldPadContentForCompat;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private boolean mShouldPadContentForCompat;

        public Builder setShouldPadContentForCompat(boolean value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mShouldPadContentForCompat = value;
            return this;
        }

        public UiTranslationSpec build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 2;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mShouldPadContentForCompat = false;
            }
            UiTranslationSpec o = new UiTranslationSpec(this.mShouldPadContentForCompat);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 2) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
