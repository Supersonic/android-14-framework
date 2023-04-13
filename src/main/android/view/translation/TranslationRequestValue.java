package android.view.translation;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TranslationRequestValue implements Parcelable {
    public static final Parcelable.Creator<TranslationRequestValue> CREATOR = new Parcelable.Creator<TranslationRequestValue>() { // from class: android.view.translation.TranslationRequestValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationRequestValue[] newArray(int size) {
            return new TranslationRequestValue[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationRequestValue createFromParcel(Parcel in) {
            return new TranslationRequestValue(in);
        }
    };
    private final CharSequence mText;

    public static TranslationRequestValue forText(CharSequence text) {
        Objects.requireNonNull(text, "text should not be null");
        return new TranslationRequestValue(text);
    }

    public CharSequence getText() {
        return this.mText;
    }

    public TranslationRequestValue(CharSequence text) {
        this.mText = text;
    }

    public String toString() {
        return "TranslationRequestValue { text = " + ((Object) this.mText) + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TranslationRequestValue that = (TranslationRequestValue) o;
        return Objects.equals(this.mText, that.mText);
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mText);
        return _hash;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mText != null ? (byte) (0 | 1) : (byte) 0;
        dest.writeByte(flg);
        CharSequence charSequence = this.mText;
        if (charSequence != null) {
            dest.writeCharSequence(charSequence);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    TranslationRequestValue(Parcel in) {
        byte flg = in.readByte();
        CharSequence text = (flg & 1) == 0 ? null : in.readCharSequence();
        this.mText = text;
    }

    @Deprecated
    private void __metadata() {
    }
}
