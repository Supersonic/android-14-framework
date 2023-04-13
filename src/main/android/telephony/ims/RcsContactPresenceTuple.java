package android.telephony.ims;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Build;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public final class RcsContactPresenceTuple implements Parcelable {
    public static final Parcelable.Creator<RcsContactPresenceTuple> CREATOR = new Parcelable.Creator<RcsContactPresenceTuple>() { // from class: android.telephony.ims.RcsContactPresenceTuple.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsContactPresenceTuple createFromParcel(Parcel in) {
            return new RcsContactPresenceTuple(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsContactPresenceTuple[] newArray(int size) {
            return new RcsContactPresenceTuple[size];
        }
    };
    private static final String LOG_TAG = "RcsContactPresenceTuple";
    public static final String SERVICE_ID_CALL_COMPOSER = "org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callcomposer";
    public static final String SERVICE_ID_CHATBOT = "org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot";
    public static final String SERVICE_ID_CHATBOT_ROLE = "org.gsma.rcs.isbot";
    public static final String SERVICE_ID_CHATBOT_STANDALONE = " org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot.sa";
    public static final String SERVICE_ID_CHAT_V1 = "org.openmobilealliance:IM-session";
    public static final String SERVICE_ID_CHAT_V2 = "org.openmobilealliance:ChatSession";
    public static final String SERVICE_ID_FT = "org.openmobilealliance:File-Transfer-HTTP";
    public static final String SERVICE_ID_FT_OVER_SMS = "org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.ftsms";
    public static final String SERVICE_ID_GEO_PUSH = "org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geopush";
    public static final String SERVICE_ID_GEO_PUSH_VIA_SMS = "org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geosms";
    public static final String SERVICE_ID_MMTEL = "org.3gpp.urn:urn-7:3gpp-service.ims.icsi.mmtel";
    public static final String SERVICE_ID_POST_CALL = "org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callunanswered";
    public static final String SERVICE_ID_PRESENCE = "org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcse.dp";
    public static final String SERVICE_ID_SHARED_MAP = "org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedmap";
    public static final String SERVICE_ID_SHARED_SKETCH = "org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedsketch";
    public static final String SERVICE_ID_SLM = "org.openmobilealliance:StandaloneMsg";
    public static final String TUPLE_BASIC_STATUS_CLOSED = "closed";
    public static final String TUPLE_BASIC_STATUS_OPEN = "open";
    private Uri mContactUri;
    private ServiceCapabilities mServiceCapabilities;
    private String mServiceDescription;
    private String mServiceId;
    private String mServiceVersion;
    private String mStatus;
    private Instant mTimestamp;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface BasicStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ServiceId {
    }

    /* loaded from: classes3.dex */
    public static final class ServiceCapabilities implements Parcelable {
        public static final Parcelable.Creator<ServiceCapabilities> CREATOR = new Parcelable.Creator<ServiceCapabilities>() { // from class: android.telephony.ims.RcsContactPresenceTuple.ServiceCapabilities.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ServiceCapabilities createFromParcel(Parcel in) {
                return new ServiceCapabilities(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ServiceCapabilities[] newArray(int size) {
                return new ServiceCapabilities[size];
            }
        };
        public static final String DUPLEX_MODE_FULL = "full";
        public static final String DUPLEX_MODE_HALF = "half";
        public static final String DUPLEX_MODE_RECEIVE_ONLY = "receive-only";
        public static final String DUPLEX_MODE_SEND_ONLY = "send-only";
        private final boolean mIsAudioCapable;
        private final boolean mIsVideoCapable;
        private final List<String> mSupportedDuplexModeList;
        private final List<String> mUnsupportedDuplexModeList;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface DuplexMode {
        }

        /* loaded from: classes3.dex */
        public static final class Builder {
            private ServiceCapabilities mCapabilities;

            public Builder(boolean isAudioCapable, boolean isVideoCapable) {
                this.mCapabilities = new ServiceCapabilities(isAudioCapable, isVideoCapable);
            }

            public Builder addSupportedDuplexMode(String mode) {
                this.mCapabilities.mSupportedDuplexModeList.add(mode);
                return this;
            }

            public Builder addUnsupportedDuplexMode(String mode) {
                this.mCapabilities.mUnsupportedDuplexModeList.add(mode);
                return this;
            }

            public ServiceCapabilities build() {
                return this.mCapabilities;
            }
        }

        ServiceCapabilities(boolean isAudioCapable, boolean isVideoCapable) {
            this.mSupportedDuplexModeList = new ArrayList();
            this.mUnsupportedDuplexModeList = new ArrayList();
            this.mIsAudioCapable = isAudioCapable;
            this.mIsVideoCapable = isVideoCapable;
        }

        private ServiceCapabilities(Parcel in) {
            ArrayList arrayList = new ArrayList();
            this.mSupportedDuplexModeList = arrayList;
            ArrayList arrayList2 = new ArrayList();
            this.mUnsupportedDuplexModeList = arrayList2;
            this.mIsAudioCapable = in.readBoolean();
            this.mIsVideoCapable = in.readBoolean();
            in.readStringList(arrayList);
            in.readStringList(arrayList2);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeBoolean(this.mIsAudioCapable);
            out.writeBoolean(this.mIsVideoCapable);
            out.writeStringList(this.mSupportedDuplexModeList);
            out.writeStringList(this.mUnsupportedDuplexModeList);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public boolean isAudioCapable() {
            return this.mIsAudioCapable;
        }

        public boolean isVideoCapable() {
            return this.mIsVideoCapable;
        }

        public List<String> getSupportedDuplexModes() {
            return Collections.unmodifiableList(this.mSupportedDuplexModeList);
        }

        public List<String> getUnsupportedDuplexModes() {
            return Collections.unmodifiableList(this.mUnsupportedDuplexModeList);
        }

        public String toString() {
            return "servCaps{a=" + this.mIsAudioCapable + ", v=" + this.mIsVideoCapable + ", supported=" + this.mSupportedDuplexModeList + ", unsupported=" + this.mUnsupportedDuplexModeList + '}';
        }
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final RcsContactPresenceTuple mPresenceTuple;

        public Builder(String status, String serviceId, String serviceVersion) {
            this.mPresenceTuple = new RcsContactPresenceTuple(status, serviceId, serviceVersion);
        }

        public Builder setContactUri(Uri contactUri) {
            this.mPresenceTuple.mContactUri = contactUri;
            return this;
        }

        public Builder setTime(Instant timestamp) {
            this.mPresenceTuple.mTimestamp = timestamp;
            return this;
        }

        public Builder setServiceDescription(String description) {
            this.mPresenceTuple.mServiceDescription = description;
            return this;
        }

        public Builder setServiceCapabilities(ServiceCapabilities caps) {
            this.mPresenceTuple.mServiceCapabilities = caps;
            return this;
        }

        public RcsContactPresenceTuple build() {
            return this.mPresenceTuple;
        }
    }

    private RcsContactPresenceTuple(String status, String serviceId, String serviceVersion) {
        this.mStatus = status;
        this.mServiceId = serviceId;
        this.mServiceVersion = serviceVersion;
    }

    private RcsContactPresenceTuple(Parcel in) {
        this.mContactUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        this.mTimestamp = convertStringFormatTimeToInstant(in.readString());
        this.mStatus = in.readString();
        this.mServiceId = in.readString();
        this.mServiceVersion = in.readString();
        this.mServiceDescription = in.readString();
        this.mServiceCapabilities = (ServiceCapabilities) in.readParcelable(ServiceCapabilities.class.getClassLoader(), ServiceCapabilities.class);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.mContactUri, flags);
        out.writeString(convertInstantToStringFormat(this.mTimestamp));
        out.writeString(this.mStatus);
        out.writeString(this.mServiceId);
        out.writeString(this.mServiceVersion);
        out.writeString(this.mServiceDescription);
        out.writeParcelable(this.mServiceCapabilities, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private String convertInstantToStringFormat(Instant instant) {
        if (instant == null) {
            return "";
        }
        return instant.toString();
    }

    private Instant convertStringFormatTimeToInstant(String timestamp) {
        if (TextUtils.isEmpty(timestamp)) {
            return null;
        }
        try {
            return (Instant) DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timestamp, new TemporalQuery() { // from class: android.telephony.ims.RcsContactPresenceTuple$$ExternalSyntheticLambda0
                @Override // java.time.temporal.TemporalQuery
                public final Object queryFrom(TemporalAccessor temporalAccessor) {
                    return Instant.from(temporalAccessor);
                }
            });
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public String getStatus() {
        return this.mStatus;
    }

    public String getServiceId() {
        return this.mServiceId;
    }

    public String getServiceVersion() {
        return this.mServiceVersion;
    }

    public Uri getContactUri() {
        return this.mContactUri;
    }

    public Instant getTime() {
        return this.mTimestamp;
    }

    public String getServiceDescription() {
        return this.mServiceDescription;
    }

    public ServiceCapabilities getServiceCapabilities() {
        return this.mServiceCapabilities;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        if (Build.IS_ENG) {
            builder.append("u=");
            builder.append(this.mContactUri);
        } else {
            builder.append("u=");
            builder.append(this.mContactUri != null ? "XXX" : "null");
        }
        builder.append(", id=");
        builder.append(this.mServiceId);
        builder.append(", v=");
        builder.append(this.mServiceVersion);
        builder.append(", s=");
        builder.append(this.mStatus);
        if (this.mTimestamp != null) {
            builder.append(", timestamp=");
            builder.append(this.mTimestamp);
        }
        if (this.mServiceDescription != null) {
            builder.append(", servDesc=");
            builder.append(this.mServiceDescription);
        }
        if (this.mServiceCapabilities != null) {
            builder.append(", servCaps=");
            builder.append(this.mServiceCapabilities);
        }
        builder.append("}");
        return builder.toString();
    }
}
