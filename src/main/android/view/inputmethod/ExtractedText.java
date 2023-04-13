package android.view.inputmethod;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
/* loaded from: classes4.dex */
public class ExtractedText implements Parcelable {
    public static final Parcelable.Creator<ExtractedText> CREATOR = new Parcelable.Creator<ExtractedText>() { // from class: android.view.inputmethod.ExtractedText.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExtractedText createFromParcel(Parcel source) {
            ExtractedText res = new ExtractedText();
            res.text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            res.startOffset = source.readInt();
            res.partialStartOffset = source.readInt();
            res.partialEndOffset = source.readInt();
            res.selectionStart = source.readInt();
            res.selectionEnd = source.readInt();
            res.flags = source.readInt();
            res.hint = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            return res;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExtractedText[] newArray(int size) {
            return new ExtractedText[size];
        }
    };
    public static final int FLAG_SELECTING = 2;
    public static final int FLAG_SINGLE_LINE = 1;
    public int flags;
    public CharSequence hint;
    public int partialEndOffset;
    public int partialStartOffset;
    public int selectionEnd;
    public int selectionStart;
    public int startOffset;
    public CharSequence text;

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(this.text, dest, flags);
        dest.writeInt(this.startOffset);
        dest.writeInt(this.partialStartOffset);
        dest.writeInt(this.partialEndOffset);
        dest.writeInt(this.selectionStart);
        dest.writeInt(this.selectionEnd);
        dest.writeInt(this.flags);
        TextUtils.writeToParcel(this.hint, dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
