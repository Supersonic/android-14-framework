package android.telecom;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
/* loaded from: classes3.dex */
public class GatewayInfo implements Parcelable {
    public static final Parcelable.Creator<GatewayInfo> CREATOR = new Parcelable.Creator<GatewayInfo>() { // from class: android.telecom.GatewayInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GatewayInfo createFromParcel(Parcel source) {
            String gatewayPackageName = source.readString();
            Uri gatewayUri = Uri.CREATOR.createFromParcel(source);
            Uri originalAddress = Uri.CREATOR.createFromParcel(source);
            return new GatewayInfo(gatewayPackageName, gatewayUri, originalAddress);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GatewayInfo[] newArray(int size) {
            return new GatewayInfo[size];
        }
    };
    private final Uri mGatewayAddress;
    private final String mGatewayProviderPackageName;
    private final Uri mOriginalAddress;

    public GatewayInfo(String packageName, Uri gatewayUri, Uri originalAddress) {
        this.mGatewayProviderPackageName = packageName;
        this.mGatewayAddress = gatewayUri;
        this.mOriginalAddress = originalAddress;
    }

    public String getGatewayProviderPackageName() {
        return this.mGatewayProviderPackageName;
    }

    public Uri getGatewayAddress() {
        return this.mGatewayAddress;
    }

    public Uri getOriginalAddress() {
        return this.mOriginalAddress;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(this.mGatewayProviderPackageName) || this.mGatewayAddress == null;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(this.mGatewayProviderPackageName);
        Uri.writeToParcel(destination, this.mGatewayAddress);
        Uri.writeToParcel(destination, this.mOriginalAddress);
    }
}
