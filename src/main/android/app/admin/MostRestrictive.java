package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
/* loaded from: classes.dex */
public final class MostRestrictive<V> extends ResolutionMechanism<V> {
    public static final Parcelable.Creator<MostRestrictive<?>> CREATOR = new Parcelable.Creator<MostRestrictive<?>>() { // from class: android.app.admin.MostRestrictive.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MostRestrictive<?> createFromParcel(Parcel source) {
            return new MostRestrictive<>(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MostRestrictive<?>[] newArray(int size) {
            return new MostRestrictive[size];
        }
    };
    private final List<PolicyValue<V>> mMostToLeastRestrictive;

    public MostRestrictive(List<PolicyValue<V>> mostToLeastRestrictive) {
        this.mMostToLeastRestrictive = new ArrayList(mostToLeastRestrictive);
    }

    public List<V> getMostToLeastRestrictiveValues() {
        return this.mMostToLeastRestrictive.stream().map(new Function() { // from class: android.app.admin.MostRestrictive$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((PolicyValue) obj).getValue();
            }
        }).toList();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        try {
            MostRestrictive<V> other = (MostRestrictive) o;
            return Objects.equals(this.mMostToLeastRestrictive, other.mMostToLeastRestrictive);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.mMostToLeastRestrictive.hashCode();
    }

    public MostRestrictive(Parcel source) {
        this.mMostToLeastRestrictive = new ArrayList();
        int size = source.readInt();
        for (int i = 0; i < size; i++) {
            this.mMostToLeastRestrictive.add((PolicyValue) source.readParcelable(PolicyValue.class.getClassLoader()));
        }
    }

    public String toString() {
        return "MostRestrictive { mMostToLeastRestrictive= " + this.mMostToLeastRestrictive + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMostToLeastRestrictive.size());
        for (PolicyValue<V> entry : this.mMostToLeastRestrictive) {
            dest.writeParcelable(entry, flags);
        }
    }
}
