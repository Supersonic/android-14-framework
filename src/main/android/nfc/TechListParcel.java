package android.nfc;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public class TechListParcel implements Parcelable {
    public static final Parcelable.Creator<TechListParcel> CREATOR = new Parcelable.Creator<TechListParcel>() { // from class: android.nfc.TechListParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TechListParcel createFromParcel(Parcel source) {
            int count = source.readInt();
            String[][] techLists = new String[count];
            for (int i = 0; i < count; i++) {
                techLists[i] = source.createStringArray();
            }
            return new TechListParcel(techLists);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TechListParcel[] newArray(int size) {
            return new TechListParcel[size];
        }
    };
    private String[][] mTechLists;

    public TechListParcel(String[]... strings) {
        this.mTechLists = strings;
    }

    public String[][] getTechLists() {
        return this.mTechLists;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int count = this.mTechLists.length;
        dest.writeInt(count);
        for (int i = 0; i < count; i++) {
            String[] techList = this.mTechLists[i];
            dest.writeStringArray(techList);
        }
    }
}
