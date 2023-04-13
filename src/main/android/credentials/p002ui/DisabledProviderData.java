package android.credentials.p002ui;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.credentials.ui.DisabledProviderData */
/* loaded from: classes.dex */
public final class DisabledProviderData extends ProviderData implements Parcelable {
    public static final Parcelable.Creator<DisabledProviderData> CREATOR = new Parcelable.Creator<DisabledProviderData>() { // from class: android.credentials.ui.DisabledProviderData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisabledProviderData createFromParcel(Parcel in) {
            return new DisabledProviderData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisabledProviderData[] newArray(int size) {
            return new DisabledProviderData[size];
        }
    };

    public DisabledProviderData(String providerFlattenedComponentName) {
        super(providerFlattenedComponentName);
    }

    private DisabledProviderData(Parcel in) {
        super(in);
    }

    @Override // android.credentials.p002ui.ProviderData, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override // android.credentials.p002ui.ProviderData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
