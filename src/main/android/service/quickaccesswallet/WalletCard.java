package android.service.quickaccesswallet;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public final class WalletCard implements Parcelable {
    public static final int CARD_TYPE_NON_PAYMENT = 2;
    public static final int CARD_TYPE_PAYMENT = 1;
    public static final int CARD_TYPE_UNKNOWN = 0;
    public static final Parcelable.Creator<WalletCard> CREATOR = new Parcelable.Creator<WalletCard>() { // from class: android.service.quickaccesswallet.WalletCard.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WalletCard createFromParcel(Parcel source) {
            return WalletCard.readFromParcel(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WalletCard[] newArray(int size) {
            return new WalletCard[size];
        }
    };
    private final Icon mCardIcon;
    private final String mCardId;
    private final Icon mCardImage;
    private final CharSequence mCardLabel;
    private List<Location> mCardLocations;
    private final int mCardType;
    private final CharSequence mContentDescription;
    private final Icon mNonPaymentCardSecondaryImage;
    private final PendingIntent mPendingIntent;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CardType {
    }

    private WalletCard(Builder builder) {
        this.mCardId = builder.mCardId;
        this.mCardType = builder.mCardType;
        this.mCardImage = builder.mCardImage;
        this.mContentDescription = builder.mContentDescription;
        this.mPendingIntent = builder.mPendingIntent;
        this.mCardIcon = builder.mCardIcon;
        this.mCardLabel = builder.mCardLabel;
        this.mNonPaymentCardSecondaryImage = builder.mNonPaymentCardSecondaryImage;
        this.mCardLocations = builder.mCardLocations;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCardId);
        dest.writeInt(this.mCardType);
        this.mCardImage.writeToParcel(dest, flags);
        TextUtils.writeToParcel(this.mContentDescription, dest, flags);
        PendingIntent.writePendingIntentOrNullToParcel(this.mPendingIntent, dest);
        writeIconIfNonNull(this.mCardIcon, dest, flags);
        TextUtils.writeToParcel(this.mCardLabel, dest, flags);
        writeIconIfNonNull(this.mNonPaymentCardSecondaryImage, dest, flags);
        dest.writeTypedList(this.mCardLocations, flags);
    }

    private void writeIconIfNonNull(Icon icon, Parcel dest, int flags) {
        if (icon == null) {
            dest.writeByte((byte) 0);
            return;
        }
        dest.writeByte((byte) 1);
        icon.writeToParcel(dest, flags);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static WalletCard readFromParcel(Parcel source) {
        String cardId = source.readString();
        int cardType = source.readInt();
        Icon cardImage = Icon.CREATOR.createFromParcel(source);
        CharSequence contentDesc = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        PendingIntent pendingIntent = PendingIntent.readPendingIntentOrNullFromParcel(source);
        Icon cardIcon = source.readByte() == 0 ? null : Icon.CREATOR.createFromParcel(source);
        CharSequence cardLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        Icon nonPaymentCardSecondaryImage = source.readByte() != 0 ? Icon.CREATOR.createFromParcel(source) : null;
        Builder builder = new Builder(cardId, cardType, cardImage, contentDesc, pendingIntent).setCardIcon(cardIcon).setCardLabel(cardLabel);
        if (cardType == 2) {
            builder.setNonPaymentCardSecondaryImage(nonPaymentCardSecondaryImage);
        }
        ArrayList arrayList = new ArrayList();
        source.readTypedList(arrayList, Location.CREATOR);
        builder.setCardLocations(arrayList);
        return builder.build();
    }

    public String getCardId() {
        return this.mCardId;
    }

    public int getCardType() {
        return this.mCardType;
    }

    public Icon getCardImage() {
        return this.mCardImage;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public Icon getCardIcon() {
        return this.mCardIcon;
    }

    public CharSequence getCardLabel() {
        return this.mCardLabel;
    }

    public Icon getNonPaymentCardSecondaryImage() {
        return this.mNonPaymentCardSecondaryImage;
    }

    public List<Location> getCardLocations() {
        return this.mCardLocations;
    }

    public void removeCardLocations() {
        this.mCardLocations = new ArrayList();
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private Icon mCardIcon;
        private String mCardId;
        private Icon mCardImage;
        private CharSequence mCardLabel;
        private List<Location> mCardLocations;
        private int mCardType;
        private CharSequence mContentDescription;
        private Icon mNonPaymentCardSecondaryImage;
        private PendingIntent mPendingIntent;

        public Builder(String cardId, int cardType, Icon cardImage, CharSequence contentDescription, PendingIntent pendingIntent) {
            this.mCardLocations = new ArrayList();
            this.mCardId = cardId;
            this.mCardType = cardType;
            this.mCardImage = cardImage;
            this.mContentDescription = contentDescription;
            this.mPendingIntent = pendingIntent;
        }

        public Builder(String cardId, Icon cardImage, CharSequence contentDescription, PendingIntent pendingIntent) {
            this(cardId, 0, cardImage, contentDescription, pendingIntent);
        }

        public Builder setCardIcon(Icon cardIcon) {
            this.mCardIcon = cardIcon;
            return this;
        }

        public Builder setCardLabel(CharSequence cardLabel) {
            this.mCardLabel = cardLabel;
            return this;
        }

        public Builder setNonPaymentCardSecondaryImage(Icon nonPaymentCardSecondaryImage) {
            Preconditions.checkState(this.mCardType == 2, "This field can only be set on non-payment cards");
            this.mNonPaymentCardSecondaryImage = nonPaymentCardSecondaryImage;
            return this;
        }

        public Builder setCardLocations(List<Location> cardLocations) {
            Preconditions.checkCollectionElementsNotNull(cardLocations, "cardLocations");
            this.mCardLocations = cardLocations;
            return this;
        }

        public WalletCard build() {
            return new WalletCard(this);
        }
    }
}
