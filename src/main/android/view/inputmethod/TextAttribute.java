package android.view.inputmethod;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes4.dex */
public final class TextAttribute implements Parcelable {
    public static final Parcelable.Creator<TextAttribute> CREATOR = new Parcelable.Creator<TextAttribute>() { // from class: android.view.inputmethod.TextAttribute.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextAttribute createFromParcel(Parcel source) {
            return new TextAttribute(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextAttribute[] newArray(int size) {
            return new TextAttribute[size];
        }
    };
    private final PersistableBundle mExtras;
    private final List<String> mTextConversionSuggestions;

    private TextAttribute(Builder builder) {
        this.mTextConversionSuggestions = builder.mTextConversionSuggestions;
        this.mExtras = builder.mExtras;
    }

    private TextAttribute(Parcel source) {
        this.mTextConversionSuggestions = source.createStringArrayList();
        this.mExtras = source.readPersistableBundle();
    }

    public List<String> getTextConversionSuggestions() {
        return this.mTextConversionSuggestions;
    }

    public PersistableBundle getExtras() {
        return this.mExtras;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private List<String> mTextConversionSuggestions = new ArrayList();
        private PersistableBundle mExtras = new PersistableBundle();

        public Builder setTextConversionSuggestions(List<String> textConversionSuggestions) {
            this.mTextConversionSuggestions = Collections.unmodifiableList(textConversionSuggestions);
            return this;
        }

        public Builder setExtras(PersistableBundle extras) {
            this.mExtras = extras;
            return this;
        }

        public TextAttribute build() {
            return new TextAttribute(this);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mTextConversionSuggestions);
        dest.writePersistableBundle(this.mExtras);
    }
}
