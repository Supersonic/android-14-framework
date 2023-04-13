package android.content;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.p008os.Binder;
import android.p008os.Build;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.permission.PermissionManager;
import android.util.ArraySet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class AttributionSource implements Parcelable {
    private final AttributionSourceState mAttributionSourceState;
    private AttributionSource mNextCached;
    private Set<String> mRenouncedPermissionsCached;
    private static final String DESCRIPTOR = "android.content.AttributionSource";
    private static final Binder sDefaultToken = new Binder(DESCRIPTOR);
    public static final Parcelable.Creator<AttributionSource> CREATOR = new Parcelable.Creator<AttributionSource>() { // from class: android.content.AttributionSource.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AttributionSource[] newArray(int size) {
            return new AttributionSource[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AttributionSource createFromParcel(Parcel in) {
            return new AttributionSource(in);
        }
    };

    public AttributionSource(int uid, String packageName, String attributionTag) {
        this(uid, packageName, attributionTag, sDefaultToken);
    }

    public AttributionSource(int uid, String packageName, String attributionTag, IBinder token) {
        this(uid, packageName, attributionTag, token, null, null);
    }

    public AttributionSource(int uid, String packageName, String attributionTag, IBinder token, AttributionSource next) {
        this(uid, packageName, attributionTag, token, null, next);
    }

    public AttributionSource(int uid, String packageName, String attributionTag, Set<String> renouncedPermissions, AttributionSource next) {
        this(uid, packageName, attributionTag, renouncedPermissions != null ? (String[]) renouncedPermissions.toArray(new String[0]) : null, next);
    }

    public AttributionSource(AttributionSource current, AttributionSource next) {
        this(current.getUid(), current.getPackageName(), current.getAttributionTag(), current.getToken(), current.mAttributionSourceState.renouncedPermissions, next);
    }

    AttributionSource(int uid, String packageName, String attributionTag, String[] renouncedPermissions, AttributionSource next) {
        this(uid, packageName, attributionTag, sDefaultToken, renouncedPermissions, next);
    }

    AttributionSource(int uid, String packageName, String attributionTag, IBinder token, String[] renouncedPermissions, AttributionSource next) {
        AttributionSourceState attributionSourceState = new AttributionSourceState();
        this.mAttributionSourceState = attributionSourceState;
        attributionSourceState.uid = uid;
        attributionSourceState.token = token;
        attributionSourceState.packageName = packageName;
        attributionSourceState.attributionTag = attributionTag;
        attributionSourceState.renouncedPermissions = renouncedPermissions;
        attributionSourceState.next = next != null ? new AttributionSourceState[]{next.mAttributionSourceState} : new AttributionSourceState[0];
    }

    AttributionSource(Parcel in) {
        this(AttributionSourceState.CREATOR.createFromParcel(in));
        if (!Binder.isDirectlyHandlingTransaction()) {
            throw new SecurityException("AttributionSource should be unparceled during a binder transaction for proper verification.");
        }
        enforceCallingUidAndPid();
    }

    public AttributionSource(AttributionSourceState attributionSourceState) {
        this.mAttributionSourceState = attributionSourceState;
    }

    public AttributionSource withNextAttributionSource(AttributionSource next) {
        return new AttributionSource(getUid(), getPackageName(), getAttributionTag(), this.mAttributionSourceState.renouncedPermissions, next);
    }

    public AttributionSource withPackageName(String packageName) {
        return new AttributionSource(getUid(), packageName, getAttributionTag(), this.mAttributionSourceState.renouncedPermissions, getNext());
    }

    public AttributionSource withToken(Binder token) {
        return new AttributionSource(getUid(), getPackageName(), getAttributionTag(), token, this.mAttributionSourceState.renouncedPermissions, getNext());
    }

    public AttributionSourceState asState() {
        return this.mAttributionSourceState;
    }

    public ScopedParcelState asScopedParcelState() {
        return new ScopedParcelState(this);
    }

    public static AttributionSource myAttributionSource() {
        AttributionSource globalSource = ActivityThread.currentAttributionSource();
        if (globalSource != null) {
            return globalSource;
        }
        int uid = Process.myUid();
        if (uid == 0) {
            uid = 1000;
        }
        try {
            return new Builder(uid).setPackageName(AppGlobals.getPackageManager().getPackagesForUid(uid)[0]).build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to resolve AttributionSource");
        }
    }

    /* loaded from: classes.dex */
    public static class ScopedParcelState implements AutoCloseable {
        private final Parcel mParcel;

        public Parcel getParcel() {
            return this.mParcel;
        }

        public ScopedParcelState(AttributionSource attributionSource) {
            Parcel obtain = Parcel.obtain();
            this.mParcel = obtain;
            attributionSource.writeToParcel(obtain, 0);
            obtain.setDataPosition(0);
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            this.mParcel.recycle();
        }
    }

    private void enforceCallingUidAndPid() {
        enforceCallingUid();
        enforceCallingPid();
    }

    public void enforceCallingUid() {
        if (!checkCallingUid()) {
            throw new SecurityException("Calling uid: " + Binder.getCallingUid() + " doesn't match source uid: " + this.mAttributionSourceState.uid);
        }
    }

    public boolean checkCallingUid() {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 0 && UserHandle.getAppId(callingUid) != 1000 && callingUid != this.mAttributionSourceState.uid) {
            return false;
        }
        return true;
    }

    public void enforceCallingPid() {
        if (!checkCallingPid()) {
            throw new SecurityException("Calling pid: " + Binder.getCallingPid() + " doesn't match source pid: " + this.mAttributionSourceState.pid);
        }
    }

    private boolean checkCallingPid() {
        int callingPid = Binder.getCallingPid();
        if (this.mAttributionSourceState.pid != -1 && callingPid != this.mAttributionSourceState.pid) {
            return false;
        }
        return true;
    }

    public String toString() {
        if (Build.IS_DEBUGGABLE) {
            return "AttributionSource { uid = " + this.mAttributionSourceState.uid + ", packageName = " + this.mAttributionSourceState.packageName + ", attributionTag = " + this.mAttributionSourceState.attributionTag + ", token = " + this.mAttributionSourceState.token + ", next = " + ((this.mAttributionSourceState.next == null || this.mAttributionSourceState.next.length <= 0) ? null : this.mAttributionSourceState.next[0]) + " }";
        }
        return super.toString();
    }

    public int getNextUid() {
        if (this.mAttributionSourceState.next != null && this.mAttributionSourceState.next.length > 0) {
            return this.mAttributionSourceState.next[0].uid;
        }
        return -1;
    }

    public String getNextPackageName() {
        if (this.mAttributionSourceState.next != null && this.mAttributionSourceState.next.length > 0) {
            return this.mAttributionSourceState.next[0].packageName;
        }
        return null;
    }

    public String getNextAttributionTag() {
        if (this.mAttributionSourceState.next != null && this.mAttributionSourceState.next.length > 0) {
            return this.mAttributionSourceState.next[0].attributionTag;
        }
        return null;
    }

    public IBinder getNextToken() {
        if (this.mAttributionSourceState.next != null && this.mAttributionSourceState.next.length > 0) {
            return this.mAttributionSourceState.next[0].token;
        }
        return null;
    }

    public boolean isTrusted(Context context) {
        return this.mAttributionSourceState.token != null && ((PermissionManager) context.getSystemService(PermissionManager.class)).isRegisteredAttributionSource(this);
    }

    @SystemApi
    public Set<String> getRenouncedPermissions() {
        if (this.mRenouncedPermissionsCached == null) {
            if (this.mAttributionSourceState.renouncedPermissions != null) {
                this.mRenouncedPermissionsCached = new ArraySet(this.mAttributionSourceState.renouncedPermissions);
            } else {
                this.mRenouncedPermissionsCached = Collections.emptySet();
            }
        }
        return this.mRenouncedPermissionsCached;
    }

    public int getUid() {
        return this.mAttributionSourceState.uid;
    }

    public String getPackageName() {
        return this.mAttributionSourceState.packageName;
    }

    public String getAttributionTag() {
        return this.mAttributionSourceState.attributionTag;
    }

    public IBinder getToken() {
        return this.mAttributionSourceState.token;
    }

    public AttributionSource getNext() {
        if (this.mNextCached == null && this.mAttributionSourceState.next != null && this.mAttributionSourceState.next.length > 0) {
            this.mNextCached = new AttributionSource(this.mAttributionSourceState.next[0]);
        }
        return this.mNextCached;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttributionSource that = (AttributionSource) o;
        if (this.mAttributionSourceState.uid == that.mAttributionSourceState.uid && Objects.equals(this.mAttributionSourceState.packageName, that.mAttributionSourceState.packageName) && Objects.equals(this.mAttributionSourceState.attributionTag, that.mAttributionSourceState.attributionTag) && Objects.equals(this.mAttributionSourceState.token, that.mAttributionSourceState.token) && Arrays.equals(this.mAttributionSourceState.renouncedPermissions, that.mAttributionSourceState.renouncedPermissions) && Objects.equals(getNext(), that.getNext())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mAttributionSourceState.uid), this.mAttributionSourceState.packageName, this.mAttributionSourceState.attributionTag, this.mAttributionSourceState.token, Integer.valueOf(Arrays.hashCode(this.mAttributionSourceState.renouncedPermissions)), getNext());
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mAttributionSourceState.writeToParcel(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private final AttributionSourceState mAttributionSourceState;
        private long mBuilderFieldsSet;

        public Builder(int uid) {
            AttributionSourceState attributionSourceState = new AttributionSourceState();
            this.mAttributionSourceState = attributionSourceState;
            this.mBuilderFieldsSet = 0L;
            attributionSourceState.uid = uid;
        }

        public Builder(AttributionSource current) {
            AttributionSourceState attributionSourceState = new AttributionSourceState();
            this.mAttributionSourceState = attributionSourceState;
            this.mBuilderFieldsSet = 0L;
            if (current == null) {
                throw new IllegalArgumentException("current AttributionSource can not be null");
            }
            attributionSourceState.uid = current.getUid();
            attributionSourceState.packageName = current.getPackageName();
            attributionSourceState.attributionTag = current.getAttributionTag();
            attributionSourceState.token = current.getToken();
            attributionSourceState.renouncedPermissions = current.mAttributionSourceState.renouncedPermissions;
        }

        public Builder setPackageName(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mAttributionSourceState.packageName = value;
            return this;
        }

        public Builder setAttributionTag(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mAttributionSourceState.attributionTag = value;
            return this;
        }

        @SystemApi
        public Builder setRenouncedPermissions(Set<String> value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mAttributionSourceState.renouncedPermissions = value != null ? (String[]) value.toArray(new String[0]) : null;
            return this;
        }

        public Builder setNext(AttributionSource value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            AttributionSourceState attributionSourceState = this.mAttributionSourceState;
            attributionSourceState.next = value != null ? new AttributionSourceState[]{value.mAttributionSourceState} : attributionSourceState.next;
            return this;
        }

        public AttributionSource build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 64;
            this.mBuilderFieldsSet = j;
            if ((j & 2) == 0) {
                this.mAttributionSourceState.packageName = null;
            }
            if ((this.mBuilderFieldsSet & 4) == 0) {
                this.mAttributionSourceState.attributionTag = null;
            }
            if ((this.mBuilderFieldsSet & 8) == 0) {
                this.mAttributionSourceState.renouncedPermissions = null;
            }
            if ((this.mBuilderFieldsSet & 16) == 0) {
                this.mAttributionSourceState.next = null;
            }
            this.mAttributionSourceState.token = AttributionSource.sDefaultToken;
            if (this.mAttributionSourceState.next == null) {
                this.mAttributionSourceState.next = new AttributionSourceState[0];
            }
            return new AttributionSource(this.mAttributionSourceState);
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 64) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
