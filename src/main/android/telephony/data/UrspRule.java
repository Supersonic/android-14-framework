package android.telephony.data;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class UrspRule implements Parcelable {
    public static final Parcelable.Creator<UrspRule> CREATOR = new Parcelable.Creator<UrspRule>() { // from class: android.telephony.data.UrspRule.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UrspRule createFromParcel(Parcel source) {
            return new UrspRule(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UrspRule[] newArray(int size) {
            return new UrspRule[size];
        }
    };
    public static final int MAX_URSP_PRECEDENCE = 255;
    public static final int MIN_URSP_PRECEDENCE = 0;
    private final int mPrecedence;
    private final List<RouteSelectionDescriptor> mRouteSelectionDescriptor;
    private final List<TrafficDescriptor> mTrafficDescriptors;

    public UrspRule(int precedence, List<TrafficDescriptor> trafficDescriptors, List<RouteSelectionDescriptor> routeSelectionDescriptor) {
        this.mPrecedence = precedence;
        ArrayList arrayList = new ArrayList();
        this.mTrafficDescriptors = arrayList;
        arrayList.addAll(trafficDescriptors);
        ArrayList arrayList2 = new ArrayList();
        this.mRouteSelectionDescriptor = arrayList2;
        arrayList2.addAll(routeSelectionDescriptor);
    }

    private UrspRule(Parcel p) {
        this.mPrecedence = p.readInt();
        this.mTrafficDescriptors = p.createTypedArrayList(TrafficDescriptor.CREATOR);
        this.mRouteSelectionDescriptor = p.createTypedArrayList(RouteSelectionDescriptor.CREATOR);
    }

    public int getPrecedence() {
        return this.mPrecedence;
    }

    public List<TrafficDescriptor> getTrafficDescriptors() {
        return this.mTrafficDescriptors;
    }

    public List<RouteSelectionDescriptor> getRouteSelectionDescriptor() {
        return this.mRouteSelectionDescriptor;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPrecedence);
        dest.writeTypedList(this.mTrafficDescriptors, flags);
        dest.writeTypedList(this.mRouteSelectionDescriptor, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UrspRule that = (UrspRule) o;
        if (this.mPrecedence == that.mPrecedence && this.mTrafficDescriptors.size() == that.mTrafficDescriptors.size() && this.mTrafficDescriptors.containsAll(that.mTrafficDescriptors) && this.mRouteSelectionDescriptor.size() == that.mRouteSelectionDescriptor.size() && this.mRouteSelectionDescriptor.containsAll(that.mRouteSelectionDescriptor)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPrecedence), this.mTrafficDescriptors, this.mRouteSelectionDescriptor);
    }

    public String toString() {
        return "{.precedence = " + this.mPrecedence + ", .trafficDescriptors = " + this.mTrafficDescriptors + ", .routeSelectionDescriptor = " + this.mRouteSelectionDescriptor + "}";
    }
}
