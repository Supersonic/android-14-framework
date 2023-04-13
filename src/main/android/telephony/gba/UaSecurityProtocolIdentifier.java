package android.telephony.gba;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class UaSecurityProtocolIdentifier implements Parcelable {
    public static final int ORG_3GPP = 1;
    public static final int ORG_3GPP2 = 2;
    public static final int ORG_GSMA = 4;
    public static final int ORG_LOCAL = 255;
    public static final int ORG_NONE = 0;
    public static final int ORG_OMA = 3;
    private static final int PROTOCOL_SIZE = 5;
    public static final int UA_SECURITY_PROTOCOL_3GPP_GENERATION_TMPI = 256;
    public static final int UA_SECURITY_PROTOCOL_3GPP_GENERIC_PUSH_LAYER = 5;
    public static final int UA_SECURITY_PROTOCOL_3GPP_HTTP_BASED_MBMS = 3;
    public static final int UA_SECURITY_PROTOCOL_3GPP_HTTP_DIGEST_AUTHENTICATION = 2;
    public static final int UA_SECURITY_PROTOCOL_3GPP_IMS_MEDIA_PLANE = 6;
    public static final int UA_SECURITY_PROTOCOL_3GPP_MBMS = 1;
    public static final int UA_SECURITY_PROTOCOL_3GPP_SIP_BASED_MBMS = 4;
    public static final int UA_SECURITY_PROTOCOL_3GPP_SUBSCRIBER_CERTIFICATE = 0;
    public static final int UA_SECURITY_PROTOCOL_3GPP_TLS_BROWSER = 131072;
    public static final int UA_SECURITY_PROTOCOL_3GPP_TLS_DEFAULT = 65536;
    private int mOrg;
    private int mProtocol;
    private int mTlsCipherSuite;
    private static final int[] sUaSp3gppIds = {0, 1, 2, 3, 4, 5, 6, 256, 65536, 131072};
    public static final Parcelable.Creator<UaSecurityProtocolIdentifier> CREATOR = new Parcelable.Creator<UaSecurityProtocolIdentifier>() { // from class: android.telephony.gba.UaSecurityProtocolIdentifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UaSecurityProtocolIdentifier createFromParcel(Parcel in) {
            int org2 = in.readInt();
            int protocol = in.readInt();
            int cs = in.readInt();
            if (org2 < 0 || protocol < 0 || cs < 0) {
                return null;
            }
            Builder builder = new Builder();
            if (org2 > 0) {
                try {
                    builder.setOrg(org2);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            if (protocol > 0) {
                builder.setProtocol(protocol);
            }
            if (cs > 0) {
                builder.setTlsCipherSuite(cs);
            }
            return builder.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UaSecurityProtocolIdentifier[] newArray(int size) {
            return new UaSecurityProtocolIdentifier[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface OrganizationCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface UaSecurityProtocol3gpp {
    }

    private UaSecurityProtocolIdentifier() {
    }

    private UaSecurityProtocolIdentifier(UaSecurityProtocolIdentifier sp) {
        this.mOrg = sp.mOrg;
        this.mProtocol = sp.mProtocol;
        this.mTlsCipherSuite = sp.mTlsCipherSuite;
    }

    public byte[] toByteArray() {
        byte[] data = new byte[5];
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.put((byte) this.mOrg);
        buf.putInt(this.mProtocol | this.mTlsCipherSuite);
        return data;
    }

    public int getOrg() {
        return this.mOrg;
    }

    public int getProtocol() {
        return this.mProtocol;
    }

    public int getTlsCipherSuite() {
        return this.mTlsCipherSuite;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mOrg);
        out.writeInt(this.mProtocol);
        out.writeInt(this.mTlsCipherSuite);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "UaSecurityProtocolIdentifier[" + this.mOrg + " , " + (this.mProtocol | this.mTlsCipherSuite) + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public boolean equals(Object obj) {
        if (obj instanceof UaSecurityProtocolIdentifier) {
            UaSecurityProtocolIdentifier other = (UaSecurityProtocolIdentifier) obj;
            return this.mOrg == other.mOrg && this.mProtocol == other.mProtocol && this.mTlsCipherSuite == other.mTlsCipherSuite;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mOrg), Integer.valueOf(this.mProtocol), Integer.valueOf(this.mTlsCipherSuite));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isTlsSupported() {
        if (this.mOrg == 1) {
            int i = this.mProtocol;
            return i == 65536 || i == 131072;
        }
        return false;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final UaSecurityProtocolIdentifier mSp;

        public Builder() {
            this.mSp = new UaSecurityProtocolIdentifier();
        }

        public Builder(UaSecurityProtocolIdentifier sp) {
            Objects.requireNonNull(sp);
            this.mSp = new UaSecurityProtocolIdentifier();
        }

        public Builder setOrg(int orgCode) {
            if (orgCode < 0 || orgCode > 255) {
                throw new IllegalArgumentException("illegal organization code");
            }
            this.mSp.mOrg = orgCode;
            this.mSp.mProtocol = 0;
            this.mSp.mTlsCipherSuite = 0;
            return this;
        }

        public Builder setProtocol(int protocol) {
            if (protocol < 0 || ((protocol > 6 && protocol != 256 && protocol != 65536 && protocol != 131072) || this.mSp.mOrg != 1)) {
                throw new IllegalArgumentException("illegal protocol code");
            }
            this.mSp.mProtocol = protocol;
            this.mSp.mTlsCipherSuite = 0;
            return this;
        }

        public Builder setTlsCipherSuite(int cs) {
            if (!this.mSp.isTlsSupported()) {
                throw new IllegalArgumentException("The protocol does not support TLS");
            }
            if (!TlsParams.isTlsCipherSuiteSupported(cs)) {
                throw new IllegalArgumentException("TLS cipher suite is not supported");
            }
            this.mSp.mTlsCipherSuite = cs;
            return this;
        }

        public UaSecurityProtocolIdentifier build() {
            return new UaSecurityProtocolIdentifier();
        }
    }
}
