package android.p008os;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.PackageTagsList;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
/* renamed from: android.os.PackageTagsList */
/* loaded from: classes3.dex */
public final class PackageTagsList implements Parcelable {
    public static final Parcelable.Creator<PackageTagsList> CREATOR = new Parcelable.Creator<PackageTagsList>() { // from class: android.os.PackageTagsList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackageTagsList createFromParcel(Parcel in) {
            int count = in.readInt();
            ArrayMap arrayMap = new ArrayMap(count);
            for (int i = 0; i < count; i++) {
                String key = in.readString8();
                arrayMap.append(key, in.readArraySet(null));
            }
            return new PackageTagsList(arrayMap);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackageTagsList[] newArray(int size) {
            return new PackageTagsList[size];
        }
    };
    private final ArrayMap<String, ArraySet<String>> mPackageTags;

    private PackageTagsList(ArrayMap<String, ArraySet<String>> packageTags) {
        this.mPackageTags = (ArrayMap) Objects.requireNonNull(packageTags);
    }

    public boolean isEmpty() {
        return this.mPackageTags.isEmpty();
    }

    public boolean includes(String packageName) {
        return this.mPackageTags.containsKey(packageName);
    }

    public boolean includesTag(String attributionTag) {
        int size = this.mPackageTags.size();
        for (int i = 0; i < size; i++) {
            ArraySet<String> tags = this.mPackageTags.valueAt(i);
            if (tags.contains(attributionTag)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(String packageName) {
        Set<String> tags = this.mPackageTags.get(packageName);
        return tags != null && tags.isEmpty();
    }

    public boolean contains(String packageName, String attributionTag) {
        Set<String> tags = this.mPackageTags.get(packageName);
        if (tags == null) {
            return false;
        }
        if (tags.isEmpty()) {
            return true;
        }
        return tags.contains(attributionTag);
    }

    public boolean contains(PackageTagsList packageTagsList) {
        int otherSize = packageTagsList.mPackageTags.size();
        if (otherSize > this.mPackageTags.size()) {
            return false;
        }
        for (int i = 0; i < otherSize; i++) {
            String packageName = packageTagsList.mPackageTags.keyAt(i);
            ArraySet<String> tags = this.mPackageTags.get(packageName);
            if (tags == null) {
                return false;
            }
            if (!tags.isEmpty()) {
                ArraySet<String> otherTags = packageTagsList.mPackageTags.valueAt(i);
                if (otherTags.isEmpty() || !tags.containsAll(otherTags)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Deprecated
    public Collection<String> getPackages() {
        return new ArrayList(this.mPackageTags.keySet());
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        int count = this.mPackageTags.size();
        parcel.writeInt(count);
        for (int i = 0; i < count; i++) {
            parcel.writeString8(this.mPackageTags.keyAt(i));
            parcel.writeArraySet(this.mPackageTags.valueAt(i));
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PackageTagsList)) {
            return false;
        }
        PackageTagsList that = (PackageTagsList) o;
        return this.mPackageTags.equals(that.mPackageTags);
    }

    public int hashCode() {
        return Objects.hash(this.mPackageTags);
    }

    public String toString() {
        return this.mPackageTags.toString();
    }

    public void dump(PrintWriter pw) {
        int size = this.mPackageTags.size();
        for (int i = 0; i < size; i++) {
            String packageName = this.mPackageTags.keyAt(i);
            pw.print(packageName);
            pw.print(NavigationBarInflaterView.SIZE_MOD_START);
            int tagsSize = this.mPackageTags.valueAt(i).size();
            if (tagsSize == 0) {
                pw.print("*");
            } else {
                for (int j = 0; j < tagsSize; j++) {
                    String attributionTag = this.mPackageTags.valueAt(i).valueAt(j);
                    if (j > 0) {
                        pw.print(", ");
                    }
                    if (attributionTag != null && attributionTag.startsWith(packageName)) {
                        pw.print(attributionTag.substring(packageName.length()));
                    } else {
                        pw.print(attributionTag);
                    }
                }
            }
            pw.println(NavigationBarInflaterView.SIZE_MOD_END);
        }
    }

    /* renamed from: android.os.PackageTagsList$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private final ArrayMap<String, ArraySet<String>> mPackageTags;

        public Builder() {
            this.mPackageTags = new ArrayMap<>();
        }

        public Builder(int capacity) {
            this.mPackageTags = new ArrayMap<>(capacity);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ ArraySet lambda$add$0(String p) {
            return new ArraySet();
        }

        public Builder add(String packageName) {
            this.mPackageTags.computeIfAbsent(packageName, new Function() { // from class: android.os.PackageTagsList$Builder$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return PackageTagsList.Builder.lambda$add$0((String) obj);
                }
            }).clear();
            return this;
        }

        public Builder add(String packageName, String attributionTag) {
            ArraySet<String> tags = this.mPackageTags.get(packageName);
            if (tags == null) {
                ArraySet<String> tags2 = new ArraySet<>(1);
                tags2.add(attributionTag);
                this.mPackageTags.put(packageName, tags2);
            } else if (!tags.isEmpty()) {
                tags.add(attributionTag);
            }
            return this;
        }

        public Builder add(String packageName, Collection<String> attributionTags) {
            if (attributionTags.isEmpty()) {
                return this;
            }
            ArraySet<String> tags = this.mPackageTags.get(packageName);
            if (tags == null) {
                this.mPackageTags.put(packageName, new ArraySet<>(attributionTags));
            } else if (!tags.isEmpty()) {
                tags.addAll(attributionTags);
            }
            return this;
        }

        public Builder add(PackageTagsList packageTagsList) {
            return add(packageTagsList.mPackageTags);
        }

        public Builder add(Map<String, ? extends Set<String>> packageTagsMap) {
            this.mPackageTags.ensureCapacity(packageTagsMap.size());
            for (Map.Entry<String, ? extends Set<String>> entry : packageTagsMap.entrySet()) {
                Set<String> newTags = entry.getValue();
                if (newTags.isEmpty()) {
                    add(entry.getKey());
                } else {
                    add(entry.getKey(), newTags);
                }
            }
            return this;
        }

        public Builder remove(String packageName) {
            this.mPackageTags.remove(packageName);
            return this;
        }

        public Builder remove(String packageName, String attributionTag) {
            ArraySet<String> tags = this.mPackageTags.get(packageName);
            if (tags != null && tags.remove(attributionTag) && tags.isEmpty()) {
                this.mPackageTags.remove(packageName);
            }
            return this;
        }

        public Builder remove(String packageName, Collection<String> attributionTags) {
            if (attributionTags.isEmpty()) {
                return this;
            }
            ArraySet<String> tags = this.mPackageTags.get(packageName);
            if (tags != null && tags.removeAll(attributionTags) && tags.isEmpty()) {
                this.mPackageTags.remove(packageName);
            }
            return this;
        }

        public Builder remove(PackageTagsList packageTagsList) {
            return remove(packageTagsList.mPackageTags);
        }

        public Builder remove(Map<String, ? extends Set<String>> packageTagsMap) {
            for (Map.Entry<String, ? extends Set<String>> entry : packageTagsMap.entrySet()) {
                Set<String> removedTags = entry.getValue();
                if (removedTags.isEmpty()) {
                    remove(entry.getKey());
                } else {
                    remove(entry.getKey(), removedTags);
                }
            }
            return this;
        }

        public Builder clear() {
            this.mPackageTags.clear();
            return this;
        }

        public PackageTagsList build() {
            return new PackageTagsList(copy(this.mPackageTags));
        }

        private static ArrayMap<String, ArraySet<String>> copy(ArrayMap<String, ArraySet<String>> value) {
            int size = value.size();
            ArrayMap<String, ArraySet<String>> copy = new ArrayMap<>(size);
            for (int i = 0; i < size; i++) {
                String packageName = value.keyAt(i);
                ArraySet<String> tags = new ArraySet<>((ArraySet<String>) Objects.requireNonNull(value.valueAt(i)));
                copy.append(packageName, tags);
            }
            return copy;
        }
    }
}
