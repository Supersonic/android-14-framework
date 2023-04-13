package android.view.autofill;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.HashMap;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ParcelableMap extends HashMap<AutofillId, AutofillValue> implements Parcelable {
    public static final Parcelable.Creator<ParcelableMap> CREATOR = new Parcelable.Creator<ParcelableMap>() { // from class: android.view.autofill.ParcelableMap.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableMap createFromParcel(Parcel source) {
            int size = source.readInt();
            ParcelableMap map = new ParcelableMap(size);
            for (int i = 0; i < size; i++) {
                AutofillId key = (AutofillId) source.readParcelable(null, AutofillId.class);
                AutofillValue value = (AutofillValue) source.readParcelable(null, AutofillValue.class);
                map.put(key, value);
            }
            return map;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableMap[] newArray(int size) {
            return new ParcelableMap[size];
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParcelableMap(int size) {
        super(size);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());
        for (Map.Entry<AutofillId, AutofillValue> entry : entrySet()) {
            dest.writeParcelable(entry.getKey(), 0);
            dest.writeParcelable(entry.getValue(), 0);
        }
    }
}
