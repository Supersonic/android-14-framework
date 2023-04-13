package android.media.p007tv;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* renamed from: android.media.tv.TunedInfo */
/* loaded from: classes2.dex */
public final class TunedInfo implements Parcelable {
    public static final int APP_TAG_SELF = 0;
    public static final int APP_TYPE_NON_SYSTEM = 3;
    public static final int APP_TYPE_SELF = 1;
    public static final int APP_TYPE_SYSTEM = 2;
    public static final Parcelable.Creator<TunedInfo> CREATOR = new Parcelable.Creator<TunedInfo>() { // from class: android.media.tv.TunedInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TunedInfo createFromParcel(Parcel source) {
            try {
                return new TunedInfo(source);
            } catch (Exception e) {
                Log.m109e(TunedInfo.TAG, "Exception creating TunedInfo from parcel", e);
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TunedInfo[] newArray(int size) {
            return new TunedInfo[size];
        }
    };
    static final String TAG = "TunedInfo";
    private final int mAppTag;
    private final int mAppType;
    private final Uri mChannelUri;
    private final String mInputId;
    private final boolean mIsMainSession;
    private final boolean mIsRecordingSession;
    private final boolean mIsVisible;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TunedInfo$AppType */
    /* loaded from: classes2.dex */
    public @interface AppType {
    }

    public TunedInfo(String inputId, Uri channelUri, boolean isRecordingSession, boolean isVisible, boolean isMainSession, int appType, int appTag) {
        this.mInputId = inputId;
        this.mChannelUri = channelUri;
        this.mIsRecordingSession = isRecordingSession;
        this.mIsVisible = isVisible;
        this.mIsMainSession = isMainSession;
        this.mAppType = appType;
        this.mAppTag = appTag;
    }

    private TunedInfo(Parcel source) {
        this.mInputId = source.readString();
        String uriString = source.readString();
        this.mChannelUri = uriString == null ? null : Uri.parse(uriString);
        this.mIsRecordingSession = source.readInt() == 1;
        this.mIsVisible = source.readInt() == 1;
        this.mIsMainSession = source.readInt() == 1;
        this.mAppType = source.readInt();
        this.mAppTag = source.readInt();
    }

    public String getInputId() {
        return this.mInputId;
    }

    public Uri getChannelUri() {
        return this.mChannelUri;
    }

    public boolean isRecordingSession() {
        return this.mIsRecordingSession;
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    public boolean isMainSession() {
        return this.mIsMainSession;
    }

    public int getAppTag() {
        return this.mAppTag;
    }

    public int getAppType() {
        return this.mAppType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mInputId);
        Uri uri = this.mChannelUri;
        String uriString = uri == null ? null : uri.toString();
        dest.writeString(uriString);
        dest.writeInt(this.mIsRecordingSession ? 1 : 0);
        dest.writeInt(this.mIsVisible ? 1 : 0);
        dest.writeInt(this.mIsMainSession ? 1 : 0);
        dest.writeInt(this.mAppType);
        dest.writeInt(this.mAppTag);
    }

    public String toString() {
        return "inputID=" + this.mInputId + ";channelUri=" + this.mChannelUri + ";isRecording=" + this.mIsRecordingSession + ";isVisible=" + this.mIsVisible + ";isMainSession=" + this.mIsMainSession + ";appType=" + this.mAppType + ";appTag=" + this.mAppTag;
    }

    public boolean equals(Object o) {
        if (o instanceof TunedInfo) {
            TunedInfo other = (TunedInfo) o;
            return TextUtils.equals(this.mInputId, other.getInputId()) && Objects.equals(this.mChannelUri, other.mChannelUri) && this.mIsRecordingSession == other.mIsRecordingSession && this.mIsVisible == other.mIsVisible && this.mIsMainSession == other.mIsMainSession && this.mAppType == other.mAppType && this.mAppTag == other.mAppTag;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mInputId, this.mChannelUri, Boolean.valueOf(this.mIsRecordingSession), Boolean.valueOf(this.mIsVisible), Boolean.valueOf(this.mIsMainSession), Integer.valueOf(this.mAppType), Integer.valueOf(this.mAppTag));
    }
}
