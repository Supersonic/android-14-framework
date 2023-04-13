package android.service.quickaccesswallet;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public final class GetWalletCardsResponse implements Parcelable {
    public static final Parcelable.Creator<GetWalletCardsResponse> CREATOR = new Parcelable.Creator<GetWalletCardsResponse>() { // from class: android.service.quickaccesswallet.GetWalletCardsResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetWalletCardsResponse createFromParcel(Parcel source) {
            return GetWalletCardsResponse.readFromParcel(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetWalletCardsResponse[] newArray(int size) {
            return new GetWalletCardsResponse[size];
        }
    };
    private final int mSelectedIndex;
    private final List<WalletCard> mWalletCards;

    public GetWalletCardsResponse(List<WalletCard> walletCards, int selectedIndex) {
        this.mWalletCards = walletCards;
        this.mSelectedIndex = selectedIndex;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mWalletCards.size());
        dest.writeParcelableList(this.mWalletCards, flags);
        dest.writeInt(this.mSelectedIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static GetWalletCardsResponse readFromParcel(Parcel source) {
        int size = source.readInt();
        List<WalletCard> walletCards = source.readParcelableList(new ArrayList(size), WalletCard.class.getClassLoader(), WalletCard.class);
        int selectedIndex = source.readInt();
        return new GetWalletCardsResponse(walletCards, selectedIndex);
    }

    public List<WalletCard> getWalletCards() {
        return this.mWalletCards;
    }

    public int getSelectedIndex() {
        return this.mSelectedIndex;
    }
}
