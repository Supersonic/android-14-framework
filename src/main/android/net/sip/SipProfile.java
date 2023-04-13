package android.net.sip;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
/* loaded from: classes.dex */
public class SipProfile implements Parcelable, Serializable, Cloneable {
    public static final Parcelable.Creator<SipProfile> CREATOR = new Parcelable.Creator<SipProfile>() { // from class: android.net.sip.SipProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SipProfile createFromParcel(Parcel in) {
            return new SipProfile(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SipProfile[] newArray(int size) {
            return new SipProfile[size];
        }
    };
    private static final int DEFAULT_PORT = 5060;
    private static final String TCP = "TCP";
    private static final String UDP = "UDP";
    private static final long serialVersionUID = 1;
    private Address mAddress;
    private String mAuthUserName;
    private boolean mAutoRegistration;
    private transient int mCallingUid;
    private String mDomain;
    private String mPassword;
    private int mPort;
    private String mProfileName;
    private String mProtocol;
    private String mProxyAddress;
    private boolean mSendKeepAlive;

    /* loaded from: classes.dex */
    public static class Builder {
        private AddressFactory mAddressFactory;
        private String mDisplayName;
        private SipProfile mProfile;
        private String mProxyAddress;
        private SipURI mUri;

        public Builder(SipProfile profile) {
            this.mProfile = new SipProfile();
            try {
                this.mAddressFactory = SipFactory.getInstance().createAddressFactory();
                if (profile != null) {
                    try {
                        SipProfile sipProfile = (SipProfile) profile.clone();
                        this.mProfile = sipProfile;
                        sipProfile.mAddress = null;
                        SipURI uri = profile.getUri();
                        this.mUri = uri;
                        uri.setUserPassword(profile.getPassword());
                        this.mDisplayName = profile.getDisplayName();
                        this.mProxyAddress = profile.getProxyAddress();
                        this.mProfile.mPort = profile.getPort();
                        return;
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException("should not occur", e);
                    }
                }
                throw new NullPointerException();
            } catch (PeerUnavailableException e2) {
                throw new RuntimeException(e2);
            }
        }

        public Builder(String uriString) throws ParseException {
            this.mProfile = new SipProfile();
            try {
                AddressFactory createAddressFactory = SipFactory.getInstance().createAddressFactory();
                this.mAddressFactory = createAddressFactory;
                if (uriString == null) {
                    throw new NullPointerException("uriString cannot be null");
                }
                URI uri = createAddressFactory.createURI(fix(uriString));
                if (uri instanceof SipURI) {
                    SipURI sipURI = (SipURI) uri;
                    this.mUri = sipURI;
                    this.mProfile.mDomain = sipURI.getHost();
                    return;
                }
                throw new ParseException(uriString + " is not a SIP URI", 0);
            } catch (PeerUnavailableException e) {
                throw new RuntimeException(e);
            }
        }

        public Builder(String username, String serverDomain) throws ParseException {
            this.mProfile = new SipProfile();
            try {
                AddressFactory createAddressFactory = SipFactory.getInstance().createAddressFactory();
                this.mAddressFactory = createAddressFactory;
                if (username == null || serverDomain == null) {
                    throw new NullPointerException("username and serverDomain cannot be null");
                }
                this.mUri = createAddressFactory.createSipURI(username, serverDomain);
                this.mProfile.mDomain = serverDomain;
            } catch (PeerUnavailableException e) {
                throw new RuntimeException(e);
            }
        }

        private String fix(String uriString) {
            if (uriString.trim().toLowerCase().startsWith("sip:")) {
                return uriString;
            }
            return "sip:" + uriString;
        }

        public Builder setAuthUserName(String name) {
            this.mProfile.mAuthUserName = name;
            return this;
        }

        public Builder setProfileName(String name) {
            this.mProfile.mProfileName = name;
            return this;
        }

        public Builder setPassword(String password) {
            this.mUri.setUserPassword(password);
            return this;
        }

        public Builder setPort(int port) throws IllegalArgumentException {
            if (port > 65535 || port < 1000) {
                throw new IllegalArgumentException("incorrect port arugment: " + port);
            }
            this.mProfile.mPort = port;
            return this;
        }

        public Builder setProtocol(String protocol) throws IllegalArgumentException {
            if (protocol == null) {
                throw new NullPointerException("protocol cannot be null");
            }
            String protocol2 = protocol.toUpperCase();
            if (!protocol2.equals("UDP") && !protocol2.equals("TCP")) {
                throw new IllegalArgumentException("unsupported protocol: " + protocol2);
            }
            this.mProfile.mProtocol = protocol2;
            return this;
        }

        public Builder setOutboundProxy(String outboundProxy) {
            this.mProxyAddress = outboundProxy;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.mDisplayName = displayName;
            return this;
        }

        public Builder setSendKeepAlive(boolean flag) {
            this.mProfile.mSendKeepAlive = flag;
            return this;
        }

        public Builder setAutoRegistration(boolean flag) {
            this.mProfile.mAutoRegistration = flag;
            return this;
        }

        public SipProfile build() {
            this.mProfile.mPassword = this.mUri.getUserPassword();
            this.mUri.setUserPassword(null);
            try {
                if (!TextUtils.isEmpty(this.mProxyAddress)) {
                    SipURI uri = (SipURI) this.mAddressFactory.createURI(fix(this.mProxyAddress));
                    this.mProfile.mProxyAddress = uri.getHost();
                } else {
                    if (!this.mProfile.mProtocol.equals("UDP")) {
                        this.mUri.setTransportParam(this.mProfile.mProtocol);
                    }
                    if (this.mProfile.mPort != 5060) {
                        this.mUri.setPort(this.mProfile.mPort);
                    }
                }
                this.mProfile.mAddress = this.mAddressFactory.createAddress(this.mDisplayName, this.mUri);
                return this.mProfile;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (InvalidArgumentException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    private SipProfile() {
        this.mProtocol = "UDP";
        this.mPort = 5060;
        this.mSendKeepAlive = false;
        this.mAutoRegistration = true;
        this.mCallingUid = 0;
    }

    private SipProfile(Parcel in) {
        boolean z;
        this.mProtocol = "UDP";
        this.mPort = 5060;
        this.mSendKeepAlive = false;
        this.mAutoRegistration = true;
        this.mCallingUid = 0;
        this.mAddress = (Address) in.readSerializable();
        this.mProxyAddress = in.readString();
        this.mPassword = in.readString();
        this.mDomain = in.readString();
        this.mProtocol = in.readString();
        this.mProfileName = in.readString();
        if (in.readInt() == 0) {
            z = false;
        } else {
            z = true;
        }
        this.mSendKeepAlive = z;
        this.mAutoRegistration = in.readInt() != 0;
        this.mCallingUid = in.readInt();
        this.mPort = in.readInt();
        this.mAuthUserName = in.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(this.mAddress);
        out.writeString(this.mProxyAddress);
        out.writeString(this.mPassword);
        out.writeString(this.mDomain);
        out.writeString(this.mProtocol);
        out.writeString(this.mProfileName);
        out.writeInt(this.mSendKeepAlive ? 1 : 0);
        out.writeInt(this.mAutoRegistration ? 1 : 0);
        out.writeInt(this.mCallingUid);
        out.writeInt(this.mPort);
        out.writeString(this.mAuthUserName);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SipURI getUri() {
        return (SipURI) this.mAddress.getURI();
    }

    public String getUriString() {
        if (!TextUtils.isEmpty(this.mProxyAddress)) {
            return "sip:" + getUserName() + Separators.f10AT + this.mDomain;
        }
        return getUri().toString();
    }

    public Address getSipAddress() {
        return this.mAddress;
    }

    public String getDisplayName() {
        return this.mAddress.getDisplayName();
    }

    public String getUserName() {
        return getUri().getUser();
    }

    public String getAuthUserName() {
        return this.mAuthUserName;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public String getSipDomain() {
        return this.mDomain;
    }

    public int getPort() {
        return this.mPort;
    }

    public String getProtocol() {
        return this.mProtocol;
    }

    public String getProxyAddress() {
        return this.mProxyAddress;
    }

    public String getProfileName() {
        return this.mProfileName;
    }

    public boolean getSendKeepAlive() {
        return this.mSendKeepAlive;
    }

    public boolean getAutoRegistration() {
        return this.mAutoRegistration;
    }

    public void setCallingUid(int uid) {
        this.mCallingUid = uid;
    }

    @SystemApi
    public int getCallingUid() {
        return this.mCallingUid;
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.mPort == 0) {
            this.mPort = 5060;
        }
        return this;
    }
}
