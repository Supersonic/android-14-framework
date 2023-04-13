package android.service.quickaccesswallet;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class WalletServiceEventListenerRequest implements Parcelable {
    public static final Parcelable.Creator<WalletServiceEventListenerRequest> CREATOR = new Parcelable.Creator<WalletServiceEventListenerRequest>() { // from class: android.service.quickaccesswallet.WalletServiceEventListenerRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WalletServiceEventListenerRequest createFromParcel(Parcel source) {
            return WalletServiceEventListenerRequest.readFromParcel(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WalletServiceEventListenerRequest[] newArray(int size) {
            return new WalletServiceEventListenerRequest[size];
        }
    };
    private final String mListenerId;

    public WalletServiceEventListenerRequest(String listenerKey) {
        this.mListenerId = listenerKey;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mListenerId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static WalletServiceEventListenerRequest readFromParcel(Parcel source) {
        String listenerId = source.readString();
        return new WalletServiceEventListenerRequest(listenerId);
    }

    public String getListenerId() {
        return this.mListenerId;
    }
}
