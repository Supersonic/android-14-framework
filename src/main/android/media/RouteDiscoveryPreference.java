package android.media;

import android.annotation.SystemApi;
import android.media.RouteDiscoveryPreference;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public final class RouteDiscoveryPreference implements Parcelable {
    public static final Parcelable.Creator<RouteDiscoveryPreference> CREATOR = new Parcelable.Creator<RouteDiscoveryPreference>() { // from class: android.media.RouteDiscoveryPreference.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RouteDiscoveryPreference createFromParcel(Parcel in) {
            return new RouteDiscoveryPreference(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RouteDiscoveryPreference[] newArray(int size) {
            return new RouteDiscoveryPreference[size];
        }
    };
    @SystemApi
    public static final RouteDiscoveryPreference EMPTY = new Builder(Collections.emptyList(), false).build();
    private final List<String> mAllowedPackages;
    private final Bundle mExtras;
    private final List<String> mPackageOrder;
    private final List<String> mPreferredFeatures;
    private final boolean mShouldPerformActiveScan;

    RouteDiscoveryPreference(Builder builder) {
        this.mPreferredFeatures = builder.mPreferredFeatures;
        this.mPackageOrder = builder.mPackageOrder;
        this.mAllowedPackages = builder.mAllowedPackages;
        this.mShouldPerformActiveScan = builder.mActiveScan;
        this.mExtras = builder.mExtras;
    }

    RouteDiscoveryPreference(Parcel in) {
        this.mPreferredFeatures = in.createStringArrayList();
        this.mPackageOrder = in.createStringArrayList();
        this.mAllowedPackages = in.createStringArrayList();
        this.mShouldPerformActiveScan = in.readBoolean();
        this.mExtras = in.readBundle();
    }

    public List<String> getPreferredFeatures() {
        return this.mPreferredFeatures;
    }

    public List<String> getDeduplicationPackageOrder() {
        return this.mPackageOrder;
    }

    public List<String> getAllowedPackages() {
        return this.mAllowedPackages;
    }

    public boolean shouldPerformActiveScan() {
        return this.mShouldPerformActiveScan;
    }

    public boolean shouldRemoveDuplicates() {
        return !this.mPackageOrder.isEmpty();
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mPreferredFeatures);
        dest.writeStringList(this.mPackageOrder);
        dest.writeStringList(this.mAllowedPackages);
        dest.writeBoolean(this.mShouldPerformActiveScan);
        dest.writeBundle(this.mExtras);
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "RouteDiscoveryPreference");
        String indent = prefix + "  ";
        pw.println(indent + "mPreferredFeatures=" + this.mPreferredFeatures);
        pw.println(indent + "mPackageOrder=" + this.mPackageOrder);
        pw.println(indent + "mAllowedPackages=" + this.mAllowedPackages);
        pw.println(indent + "mExtras=" + this.mExtras);
    }

    public String toString() {
        StringBuilder result = new StringBuilder().append("RouteDiscoveryRequest{ ").append("preferredFeatures={").append(String.join(", ", this.mPreferredFeatures)).append("}").append(", activeScan=").append(this.mShouldPerformActiveScan).append(" }");
        return result.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof RouteDiscoveryPreference) {
            RouteDiscoveryPreference other = (RouteDiscoveryPreference) o;
            return Objects.equals(this.mPreferredFeatures, other.mPreferredFeatures) && Objects.equals(this.mPackageOrder, other.mPackageOrder) && Objects.equals(this.mAllowedPackages, other.mAllowedPackages) && this.mShouldPerformActiveScan == other.mShouldPerformActiveScan;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mPreferredFeatures, this.mPackageOrder, this.mAllowedPackages, Boolean.valueOf(this.mShouldPerformActiveScan));
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        boolean mActiveScan;
        List<String> mAllowedPackages;
        Bundle mExtras;
        List<String> mPackageOrder;
        List<String> mPreferredFeatures;

        public Builder(List<String> preferredFeatures, boolean activeScan) {
            Objects.requireNonNull(preferredFeatures, "preferredFeatures must not be null");
            this.mPreferredFeatures = (List) preferredFeatures.stream().filter(new Predicate() { // from class: android.media.RouteDiscoveryPreference$Builder$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return RouteDiscoveryPreference.Builder.lambda$new$0((String) obj);
                }
            }).collect(Collectors.toList());
            this.mPackageOrder = List.of();
            this.mAllowedPackages = List.of();
            this.mActiveScan = activeScan;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$new$0(String str) {
            return !TextUtils.isEmpty(str);
        }

        public Builder(RouteDiscoveryPreference preference) {
            Objects.requireNonNull(preference, "preference must not be null");
            this.mPreferredFeatures = preference.getPreferredFeatures();
            this.mPackageOrder = preference.getDeduplicationPackageOrder();
            this.mAllowedPackages = preference.getAllowedPackages();
            this.mActiveScan = preference.shouldPerformActiveScan();
            this.mExtras = preference.getExtras();
        }

        public Builder(Collection<RouteDiscoveryPreference> preferences) {
            Objects.requireNonNull(preferences, "preferences must not be null");
            Set<String> preferredFeatures = new HashSet<>();
            Set<String> allowedPackages = new HashSet<>();
            this.mPackageOrder = List.of();
            boolean activeScan = false;
            for (RouteDiscoveryPreference preference : preferences) {
                preferredFeatures.addAll(preference.mPreferredFeatures);
                allowedPackages.addAll(preference.mAllowedPackages);
                activeScan |= preference.mShouldPerformActiveScan;
                if (this.mPackageOrder.isEmpty() && !preference.mPackageOrder.isEmpty()) {
                    this.mPackageOrder = List.copyOf(preference.mPackageOrder);
                }
            }
            this.mPreferredFeatures = List.copyOf(preferredFeatures);
            this.mAllowedPackages = List.copyOf(allowedPackages);
            this.mActiveScan = activeScan;
        }

        public Builder setPreferredFeatures(List<String> preferredFeatures) {
            Objects.requireNonNull(preferredFeatures, "preferredFeatures must not be null");
            this.mPreferredFeatures = (List) preferredFeatures.stream().filter(new Predicate() { // from class: android.media.RouteDiscoveryPreference$Builder$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return RouteDiscoveryPreference.Builder.lambda$setPreferredFeatures$1((String) obj);
                }
            }).collect(Collectors.toList());
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$setPreferredFeatures$1(String str) {
            return !TextUtils.isEmpty(str);
        }

        public Builder setAllowedPackages(List<String> allowedPackages) {
            Objects.requireNonNull(allowedPackages, "allowedPackages must not be null");
            this.mAllowedPackages = List.copyOf(allowedPackages);
            return this;
        }

        public Builder setShouldPerformActiveScan(boolean activeScan) {
            this.mActiveScan = activeScan;
            return this;
        }

        public Builder setDeduplicationPackageOrder(List<String> packageOrder) {
            Objects.requireNonNull(packageOrder, "packageOrder must not be null");
            this.mPackageOrder = List.copyOf(packageOrder);
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public RouteDiscoveryPreference build() {
            return new RouteDiscoveryPreference(this);
        }
    }
}
