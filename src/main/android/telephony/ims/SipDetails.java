package android.telephony.ims;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class SipDetails implements Parcelable {
    public static final Parcelable.Creator<SipDetails> CREATOR = new Parcelable.Creator<SipDetails>() { // from class: android.telephony.ims.SipDetails.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDetails createFromParcel(Parcel source) {
            return new SipDetails(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDetails[] newArray(int size) {
            return new SipDetails[size];
        }
    };
    public static final int METHOD_PUBLISH = 2;
    public static final int METHOD_REGISTER = 1;
    public static final int METHOD_SUBSCRIBE = 3;
    public static final int METHOD_UNKNOWN = 0;
    private final String mCallId;
    private final int mCseq;
    private final int mMethod;
    private final int mReasonHeaderCause;
    private final String mReasonHeaderText;
    private final int mResponseCode;
    private final String mResponsePhrase;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Method {
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private String mCallId;
        private int mMethod;
        private int mCseq = 0;
        private int mResponseCode = 0;
        private String mResponsePhrase = "";
        private int mReasonHeaderCause = 0;
        private String mReasonHeaderText = "";

        public Builder(int method) {
            this.mMethod = method;
        }

        public Builder setCSeq(int cSeq) {
            this.mCseq = cSeq;
            return this;
        }

        public Builder setSipResponseCode(int responseCode, String responsePhrase) {
            this.mResponseCode = responseCode;
            this.mResponsePhrase = responsePhrase;
            return this;
        }

        public Builder setSipResponseReasonHeader(int reasonHeaderCause, String reasonHeaderText) {
            this.mReasonHeaderCause = reasonHeaderCause;
            this.mReasonHeaderText = reasonHeaderText;
            return this;
        }

        public Builder setCallId(String callId) {
            this.mCallId = callId;
            return this;
        }

        public SipDetails build() {
            return new SipDetails(this);
        }
    }

    private SipDetails(Builder builder) {
        this.mMethod = builder.mMethod;
        this.mCseq = builder.mCseq;
        this.mResponseCode = builder.mResponseCode;
        this.mResponsePhrase = builder.mResponsePhrase;
        this.mReasonHeaderCause = builder.mReasonHeaderCause;
        this.mReasonHeaderText = builder.mReasonHeaderText;
        this.mCallId = builder.mCallId;
    }

    public int getMethod() {
        return this.mMethod;
    }

    public int getCSeq() {
        return this.mCseq;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public String getResponsePhrase() {
        return this.mResponsePhrase;
    }

    public int getReasonHeaderCause() {
        return this.mReasonHeaderCause;
    }

    public String getReasonHeaderText() {
        return this.mReasonHeaderText;
    }

    public String getCallId() {
        return this.mCallId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMethod);
        dest.writeInt(this.mCseq);
        dest.writeInt(this.mResponseCode);
        dest.writeString(this.mResponsePhrase);
        dest.writeInt(this.mReasonHeaderCause);
        dest.writeString(this.mReasonHeaderText);
        dest.writeString(this.mCallId);
    }

    private SipDetails(Parcel in) {
        this.mMethod = in.readInt();
        this.mCseq = in.readInt();
        this.mResponseCode = in.readInt();
        this.mResponsePhrase = in.readString();
        this.mReasonHeaderCause = in.readInt();
        this.mReasonHeaderText = in.readString();
        this.mCallId = in.readString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SipDetails that = (SipDetails) o;
        if (this.mMethod == that.mMethod && this.mCseq == that.mCseq && this.mResponseCode == that.mResponseCode && TextUtils.equals(this.mResponsePhrase, that.mResponsePhrase) && this.mReasonHeaderCause == that.mReasonHeaderCause && TextUtils.equals(this.mReasonHeaderText, that.mReasonHeaderText) && TextUtils.equals(this.mCallId, that.mCallId)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mMethod), Integer.valueOf(this.mCseq), Integer.valueOf(this.mResponseCode), this.mResponsePhrase, Integer.valueOf(this.mReasonHeaderCause), this.mReasonHeaderText, this.mCallId);
    }

    public String toString() {
        return "SipDetails { methodType= " + this.mMethod + ", cSeq=" + this.mCseq + ", ResponseCode=" + this.mResponseCode + ", ResponseCPhrase=" + this.mResponsePhrase + ", ReasonHeaderCause=" + this.mReasonHeaderCause + ", ReasonHeaderText=" + this.mReasonHeaderText + ", callId=" + this.mCallId + "}";
    }
}
