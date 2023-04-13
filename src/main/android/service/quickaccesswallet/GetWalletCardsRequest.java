package android.service.quickaccesswallet;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class GetWalletCardsRequest implements Parcelable {
    public static final Parcelable.Creator<GetWalletCardsRequest> CREATOR = new Parcelable.Creator<GetWalletCardsRequest>() { // from class: android.service.quickaccesswallet.GetWalletCardsRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetWalletCardsRequest createFromParcel(Parcel source) {
            int cardWidthPx = source.readInt();
            int cardHeightPx = source.readInt();
            int iconSizePx = source.readInt();
            int maxCards = source.readInt();
            return new GetWalletCardsRequest(cardWidthPx, cardHeightPx, iconSizePx, maxCards);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetWalletCardsRequest[] newArray(int size) {
            return new GetWalletCardsRequest[size];
        }
    };
    private final int mCardHeightPx;
    private final int mCardWidthPx;
    private final int mIconSizePx;
    private final int mMaxCards;

    public GetWalletCardsRequest(int cardWidthPx, int cardHeightPx, int iconSizePx, int maxCards) {
        this.mCardWidthPx = cardWidthPx;
        this.mCardHeightPx = cardHeightPx;
        this.mIconSizePx = iconSizePx;
        this.mMaxCards = maxCards;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCardWidthPx);
        dest.writeInt(this.mCardHeightPx);
        dest.writeInt(this.mIconSizePx);
        dest.writeInt(this.mMaxCards);
    }

    public int getCardWidthPx() {
        return this.mCardWidthPx;
    }

    public int getCardHeightPx() {
        return this.mCardHeightPx;
    }

    public int getIconSizePx() {
        return this.mIconSizePx;
    }

    public int getMaxCards() {
        return this.mMaxCards;
    }
}
