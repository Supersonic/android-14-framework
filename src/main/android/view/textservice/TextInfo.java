package android.view.textservice;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.SpellCheckSpan;
/* loaded from: classes4.dex */
public final class TextInfo implements Parcelable {
    public static final Parcelable.Creator<TextInfo> CREATOR = new Parcelable.Creator<TextInfo>() { // from class: android.view.textservice.TextInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextInfo createFromParcel(Parcel source) {
            return new TextInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextInfo[] newArray(int size) {
            return new TextInfo[size];
        }
    };
    private static final int DEFAULT_COOKIE = 0;
    private static final int DEFAULT_SEQUENCE_NUMBER = 0;
    private final CharSequence mCharSequence;
    private final int mCookie;
    private final int mSequenceNumber;

    public TextInfo(String text) {
        this(text, 0, getStringLengthOrZero(text), 0, 0);
    }

    public TextInfo(String text, int cookie, int sequenceNumber) {
        this(text, 0, getStringLengthOrZero(text), cookie, sequenceNumber);
    }

    private static int getStringLengthOrZero(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        return text.length();
    }

    public TextInfo(CharSequence charSequence, int start, int end, int cookie, int sequenceNumber) {
        if (TextUtils.isEmpty(charSequence)) {
            throw new IllegalArgumentException("charSequence is empty");
        }
        SpannableStringBuilder spannableString = new SpannableStringBuilder(charSequence, start, end);
        SpellCheckSpan[] spans = (SpellCheckSpan[]) spannableString.getSpans(0, spannableString.length(), SpellCheckSpan.class);
        for (SpellCheckSpan spellCheckSpan : spans) {
            spannableString.removeSpan(spellCheckSpan);
        }
        this.mCharSequence = spannableString;
        this.mCookie = cookie;
        this.mSequenceNumber = sequenceNumber;
    }

    public TextInfo(Parcel source) {
        this.mCharSequence = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.mCookie = source.readInt();
        this.mSequenceNumber = source.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(this.mCharSequence, dest, flags);
        dest.writeInt(this.mCookie);
        dest.writeInt(this.mSequenceNumber);
    }

    public String getText() {
        CharSequence charSequence = this.mCharSequence;
        if (charSequence == null) {
            return null;
        }
        return charSequence.toString();
    }

    public CharSequence getCharSequence() {
        return this.mCharSequence;
    }

    public int getCookie() {
        return this.mCookie;
    }

    public int getSequence() {
        return this.mSequenceNumber;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
