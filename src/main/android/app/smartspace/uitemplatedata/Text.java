package android.app.smartspace.uitemplatedata;

import android.annotation.SystemApi;
import android.app.smartspace.SmartspaceUtils;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class Text implements Parcelable {
    public static final Parcelable.Creator<Text> CREATOR = new Parcelable.Creator<Text>() { // from class: android.app.smartspace.uitemplatedata.Text.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Text createFromParcel(Parcel in) {
            return new Text(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Text[] newArray(int size) {
            return new Text[size];
        }
    };
    private final int mMaxLines;
    private final CharSequence mText;
    private final TextUtils.TruncateAt mTruncateAtType;

    Text(Parcel in) {
        this.mText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mTruncateAtType = TextUtils.TruncateAt.valueOf(in.readString());
        this.mMaxLines = in.readInt();
    }

    private Text(CharSequence text, TextUtils.TruncateAt truncateAtType, int maxLines) {
        this.mText = text;
        this.mTruncateAtType = truncateAtType;
        this.mMaxLines = maxLines;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public TextUtils.TruncateAt getTruncateAtType() {
        return this.mTruncateAtType;
    }

    public int getMaxLines() {
        return this.mMaxLines;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Text) {
            Text that = (Text) o;
            return this.mTruncateAtType == that.mTruncateAtType && SmartspaceUtils.isEqual(this.mText, that.mText) && this.mMaxLines == that.mMaxLines;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mText, this.mTruncateAtType, Integer.valueOf(this.mMaxLines));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        TextUtils.writeToParcel(this.mText, out, flags);
        out.writeString(this.mTruncateAtType.name());
        out.writeInt(this.mMaxLines);
    }

    public String toString() {
        return "Text{mText=" + ((Object) this.mText) + ", mTruncateAtType=" + this.mTruncateAtType + ", mMaxLines=" + this.mMaxLines + '}';
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private final CharSequence mText;
        private TextUtils.TruncateAt mTruncateAtType = TextUtils.TruncateAt.END;
        private int mMaxLines = 1;

        public Builder(CharSequence text) {
            this.mText = (CharSequence) Objects.requireNonNull(text);
        }

        public Builder setTruncateAtType(TextUtils.TruncateAt truncateAtType) {
            this.mTruncateAtType = (TextUtils.TruncateAt) Objects.requireNonNull(truncateAtType);
            return this;
        }

        public Builder setMaxLines(int maxLines) {
            this.mMaxLines = maxLines;
            return this;
        }

        public Text build() {
            return new Text(this.mText, this.mTruncateAtType, this.mMaxLines);
        }
    }
}
