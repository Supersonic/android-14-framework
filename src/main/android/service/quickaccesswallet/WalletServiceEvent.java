package android.service.quickaccesswallet;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class WalletServiceEvent implements Parcelable {
    public static final Parcelable.Creator<WalletServiceEvent> CREATOR = new Parcelable.Creator<WalletServiceEvent>() { // from class: android.service.quickaccesswallet.WalletServiceEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WalletServiceEvent createFromParcel(Parcel source) {
            int eventType = source.readInt();
            return new WalletServiceEvent(eventType);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WalletServiceEvent[] newArray(int size) {
            return new WalletServiceEvent[size];
        }
    };
    public static final int TYPE_NFC_PAYMENT_STARTED = 1;
    public static final int TYPE_WALLET_CARDS_UPDATED = 2;
    private final int mEventType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface EventType {
    }

    public WalletServiceEvent(int eventType) {
        this.mEventType = eventType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mEventType);
    }

    public int getEventType() {
        return this.mEventType;
    }
}
