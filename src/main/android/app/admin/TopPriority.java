package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class TopPriority<V> extends ResolutionMechanism<V> {
    public static final Parcelable.Creator<TopPriority<?>> CREATOR = new Parcelable.Creator<TopPriority<?>>() { // from class: android.app.admin.TopPriority.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TopPriority<?> createFromParcel(Parcel source) {
            return new TopPriority<>(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TopPriority<?>[] newArray(int size) {
            return new TopPriority[size];
        }
    };
    private final List<Authority> mHighestToLowestPriorityAuthorities;

    public TopPriority(List<Authority> highestToLowestPriorityAuthorities) {
        this.mHighestToLowestPriorityAuthorities = (List) Objects.requireNonNull(highestToLowestPriorityAuthorities);
    }

    private TopPriority(Parcel source) {
        this.mHighestToLowestPriorityAuthorities = new ArrayList();
        int size = source.readInt();
        for (int i = 0; i < size; i++) {
            this.mHighestToLowestPriorityAuthorities.add((Authority) source.readParcelable(Authority.class.getClassLoader()));
        }
    }

    public List<Authority> getHighestToLowestPriorityAuthorities() {
        return this.mHighestToLowestPriorityAuthorities;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        try {
            TopPriority<V> other = (TopPriority) o;
            return Objects.equals(this.mHighestToLowestPriorityAuthorities, other.mHighestToLowestPriorityAuthorities);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.mHighestToLowestPriorityAuthorities.hashCode();
    }

    public String toString() {
        return "TopPriority { mHighestToLowestPriorityAuthorities= " + this.mHighestToLowestPriorityAuthorities + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mHighestToLowestPriorityAuthorities.size());
        for (Authority authority : this.mHighestToLowestPriorityAuthorities) {
            dest.writeParcelable(authority, flags);
        }
    }
}
