package android.view.autofill;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.View;
import com.android.internal.accessibility.common.ShortcutConstants;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class AutofillId implements Parcelable {
    private static final int FLAG_HAS_SESSION = 4;
    private static final int FLAG_IS_VIRTUAL_INT = 1;
    private static final int FLAG_IS_VIRTUAL_LONG = 2;
    public static final int NO_SESSION = 0;
    private int mFlags;
    private int mSessionId;
    private final int mViewId;
    private final int mVirtualIntId;
    private final long mVirtualLongId;
    public static final AutofillId NO_AUTOFILL_ID = new AutofillId(0);
    public static final Parcelable.Creator<AutofillId> CREATOR = new Parcelable.Creator<AutofillId>() { // from class: android.view.autofill.AutofillId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutofillId createFromParcel(Parcel source) {
            int viewId = source.readInt();
            int flags = source.readInt();
            int sessionId = (flags & 4) != 0 ? source.readInt() : 0;
            if ((flags & 1) != 0) {
                return new AutofillId(flags, viewId, source.readInt(), sessionId);
            }
            if ((flags & 2) != 0) {
                return new AutofillId(flags, viewId, source.readLong(), sessionId);
            }
            return new AutofillId(flags, viewId, -1L, sessionId);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutofillId[] newArray(int size) {
            return new AutofillId[size];
        }
    };

    public AutofillId(int id) {
        this(0, id, -1L, 0);
    }

    public AutofillId(AutofillId hostId, int virtualChildId) {
        this(1, hostId.mViewId, virtualChildId, 0);
    }

    public AutofillId(int hostId, int virtualChildId) {
        this(1, hostId, virtualChildId, 0);
    }

    public AutofillId(AutofillId hostId, long virtualChildId, int sessionId) {
        this(6, hostId.mViewId, virtualChildId, sessionId);
    }

    private AutofillId(int flags, int parentId, long virtualChildId, int sessionId) {
        this.mFlags = flags;
        this.mViewId = parentId;
        this.mVirtualIntId = (flags & 1) != 0 ? (int) virtualChildId : -1;
        this.mVirtualLongId = (flags & 2) != 0 ? virtualChildId : -1L;
        this.mSessionId = sessionId;
    }

    public static AutofillId create(View host, int virtualId) {
        Objects.requireNonNull(host);
        return new AutofillId(host.getAutofillId(), virtualId);
    }

    public static AutofillId withoutSession(AutofillId id) {
        int i = id.mFlags;
        int flags = i & (-5);
        long virtualChildId = (i & 2) != 0 ? id.mVirtualLongId : id.mVirtualIntId;
        return new AutofillId(flags, id.mViewId, virtualChildId, 0);
    }

    public int getViewId() {
        return this.mViewId;
    }

    public int getVirtualChildIntId() {
        return this.mVirtualIntId;
    }

    public long getVirtualChildLongId() {
        return this.mVirtualLongId;
    }

    public boolean isVirtualInt() {
        return (this.mFlags & 1) != 0;
    }

    public boolean isVirtualLong() {
        return (this.mFlags & 2) != 0;
    }

    public boolean isNonVirtual() {
        return (isVirtualInt() || isVirtualLong()) ? false : true;
    }

    public boolean hasSession() {
        return (this.mFlags & 4) != 0;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public void setSessionId(int sessionId) {
        this.mFlags |= 4;
        this.mSessionId = sessionId;
    }

    public void resetSessionId() {
        this.mFlags &= -5;
        this.mSessionId = 0;
    }

    public int hashCode() {
        int result = (1 * 31) + this.mViewId;
        long j = this.mVirtualLongId;
        return (((((result * 31) + this.mVirtualIntId) * 31) + ((int) (j ^ (j >>> 32)))) * 31) + this.mSessionId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AutofillId other = (AutofillId) obj;
        if (this.mViewId == other.mViewId && this.mVirtualIntId == other.mVirtualIntId && this.mVirtualLongId == other.mVirtualLongId && this.mSessionId == other.mSessionId) {
            return true;
        }
        return false;
    }

    public boolean equalsIgnoreSession(AutofillId other) {
        if (this == other) {
            return true;
        }
        if (other != null && this.mViewId == other.mViewId && this.mVirtualIntId == other.mVirtualIntId && this.mVirtualLongId == other.mVirtualLongId) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder().append(this.mViewId);
        if (isVirtualInt()) {
            builder.append(ShortcutConstants.SERVICES_SEPARATOR).append(this.mVirtualIntId);
        } else if (isVirtualLong()) {
            builder.append(ShortcutConstants.SERVICES_SEPARATOR).append(this.mVirtualLongId);
        }
        if (hasSession()) {
            builder.append('@').append(this.mSessionId);
        }
        return builder.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mViewId);
        parcel.writeInt(this.mFlags);
        if (hasSession()) {
            parcel.writeInt(this.mSessionId);
        }
        if (isVirtualInt()) {
            parcel.writeInt(this.mVirtualIntId);
        } else if (isVirtualLong()) {
            parcel.writeLong(this.mVirtualLongId);
        }
    }
}
