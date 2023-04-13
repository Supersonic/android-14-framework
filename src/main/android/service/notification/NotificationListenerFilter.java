package android.service.notification;

import android.content.p001pm.VersionedPackage;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
/* loaded from: classes3.dex */
public class NotificationListenerFilter implements Parcelable {
    public static final Parcelable.Creator<NotificationListenerFilter> CREATOR = new Parcelable.Creator<NotificationListenerFilter>() { // from class: android.service.notification.NotificationListenerFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotificationListenerFilter createFromParcel(Parcel in) {
            return new NotificationListenerFilter(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotificationListenerFilter[] newArray(int size) {
            return new NotificationListenerFilter[size];
        }
    };
    private static final int DEFAULT_TYPES = 15;
    private int mAllowedNotificationTypes;
    private ArraySet<VersionedPackage> mDisallowedPackages;

    public NotificationListenerFilter() {
        this.mAllowedNotificationTypes = 15;
        this.mDisallowedPackages = new ArraySet<>();
    }

    public NotificationListenerFilter(int types, ArraySet<VersionedPackage> pkgs) {
        this.mAllowedNotificationTypes = types;
        this.mDisallowedPackages = pkgs;
    }

    protected NotificationListenerFilter(Parcel in) {
        this.mAllowedNotificationTypes = in.readInt();
        this.mDisallowedPackages = in.readArraySet(VersionedPackage.class.getClassLoader());
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAllowedNotificationTypes);
        dest.writeArraySet(this.mDisallowedPackages);
    }

    public boolean isTypeAllowed(int type) {
        return (this.mAllowedNotificationTypes & type) != 0;
    }

    public boolean areAllTypesAllowed() {
        return 15 == this.mAllowedNotificationTypes;
    }

    public boolean isPackageAllowed(VersionedPackage pkg) {
        return !this.mDisallowedPackages.contains(pkg);
    }

    public int getTypes() {
        return this.mAllowedNotificationTypes;
    }

    public ArraySet<VersionedPackage> getDisallowedPackages() {
        return this.mDisallowedPackages;
    }

    public void setTypes(int types) {
        this.mAllowedNotificationTypes = types;
    }

    public void setDisallowedPackages(ArraySet<VersionedPackage> pkgs) {
        this.mDisallowedPackages = pkgs;
    }

    public void removePackage(VersionedPackage pkg) {
        this.mDisallowedPackages.remove(pkg);
    }

    public void addPackage(VersionedPackage pkg) {
        this.mDisallowedPackages.add(pkg);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
