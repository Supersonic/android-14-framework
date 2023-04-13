package android.telephony.gba;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.IBootstrapAuthenticationCallback;
import com.android.internal.telephony.uicc.IccUtils;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes3.dex */
public final class GbaAuthRequest implements Parcelable {
    private int mAppType;
    private IBootstrapAuthenticationCallback mCallback;
    private boolean mForceBootStrapping;
    private Uri mNafUrl;
    private byte[] mSecurityProtocol;
    private int mSubId;
    private int mToken;
    private static AtomicInteger sUniqueToken = new AtomicInteger(0);
    public static final Parcelable.Creator<GbaAuthRequest> CREATOR = new Parcelable.Creator<GbaAuthRequest>() { // from class: android.telephony.gba.GbaAuthRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GbaAuthRequest createFromParcel(Parcel in) {
            int token = in.readInt();
            int subId = in.readInt();
            int appType = in.readInt();
            Uri nafUrl = (Uri) in.readParcelable(GbaAuthRequest.class.getClassLoader(), Uri.class);
            int len = in.readInt();
            byte[] protocol = new byte[len];
            in.readByteArray(protocol);
            boolean forceBootStrapping = in.readBoolean();
            IBootstrapAuthenticationCallback callback = IBootstrapAuthenticationCallback.Stub.asInterface(in.readStrongBinder());
            return new GbaAuthRequest(token, subId, appType, nafUrl, protocol, forceBootStrapping, callback);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GbaAuthRequest[] newArray(int size) {
            return new GbaAuthRequest[size];
        }
    };

    public GbaAuthRequest(int subId, int appType, Uri nafUrl, byte[] securityProtocol, boolean forceBootStrapping, IBootstrapAuthenticationCallback callback) {
        this(nextUniqueToken(), subId, appType, nafUrl, securityProtocol, forceBootStrapping, callback);
    }

    public GbaAuthRequest(GbaAuthRequest request) {
        this(request.mToken, request.mSubId, request.mAppType, request.mNafUrl, request.mSecurityProtocol, request.mForceBootStrapping, request.mCallback);
    }

    public GbaAuthRequest(int token, int subId, int appType, Uri nafUrl, byte[] securityProtocol, boolean forceBootStrapping, IBootstrapAuthenticationCallback callback) {
        this.mToken = token;
        this.mSubId = subId;
        this.mAppType = appType;
        this.mNafUrl = nafUrl;
        this.mSecurityProtocol = securityProtocol;
        this.mCallback = callback;
        this.mForceBootStrapping = forceBootStrapping;
    }

    public int getToken() {
        return this.mToken;
    }

    public int getSubId() {
        return this.mSubId;
    }

    public int getAppType() {
        return this.mAppType;
    }

    public Uri getNafUrl() {
        return this.mNafUrl;
    }

    public byte[] getSecurityProtocol() {
        return this.mSecurityProtocol;
    }

    public boolean isForceBootStrapping() {
        return this.mForceBootStrapping;
    }

    public void setCallback(IBootstrapAuthenticationCallback cb) {
        this.mCallback = cb;
    }

    public IBootstrapAuthenticationCallback getCallback() {
        return this.mCallback;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mToken);
        out.writeInt(this.mSubId);
        out.writeInt(this.mAppType);
        out.writeParcelable(this.mNafUrl, 0);
        out.writeInt(this.mSecurityProtocol.length);
        out.writeByteArray(this.mSecurityProtocol);
        out.writeBoolean(this.mForceBootStrapping);
        out.writeStrongInterface(this.mCallback);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static int nextUniqueToken() {
        return (sUniqueToken.getAndIncrement() << 16) | (((int) System.currentTimeMillis()) & 65535);
    }

    public String toString() {
        String str = "Token: " + this.mToken + "SubId:" + this.mSubId + ", AppType:" + this.mAppType + ", NafUrl:" + this.mNafUrl + ", SecurityProtocol:" + IccUtils.bytesToHexString(this.mSecurityProtocol) + ", ForceBootStrapping:" + this.mForceBootStrapping + ", CallBack:" + this.mCallback;
        return str;
    }
}
