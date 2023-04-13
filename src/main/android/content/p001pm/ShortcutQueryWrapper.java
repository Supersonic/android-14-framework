package android.content.p001pm;

import android.content.ComponentName;
import android.content.LocusId;
import android.content.p001pm.LauncherApps;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
/* renamed from: android.content.pm.ShortcutQueryWrapper */
/* loaded from: classes.dex */
public final class ShortcutQueryWrapper extends LauncherApps.ShortcutQuery implements Parcelable {
    public static final Parcelable.Creator<ShortcutQueryWrapper> CREATOR = new Parcelable.Creator<ShortcutQueryWrapper>() { // from class: android.content.pm.ShortcutQueryWrapper.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShortcutQueryWrapper[] newArray(int size) {
            return new ShortcutQueryWrapper[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShortcutQueryWrapper createFromParcel(Parcel in) {
            return new ShortcutQueryWrapper(in);
        }
    };

    public ShortcutQueryWrapper(LauncherApps.ShortcutQuery query) {
        this();
        this.mChangedSince = query.mChangedSince;
        this.mPackage = query.mPackage;
        this.mLocusIds = query.mLocusIds;
        this.mShortcutIds = query.mShortcutIds;
        this.mActivity = query.mActivity;
        this.mQueryFlags = query.mQueryFlags;
    }

    public long getChangedSince() {
        return this.mChangedSince;
    }

    public String getPackage() {
        return this.mPackage;
    }

    public List<LocusId> getLocusIds() {
        return this.mLocusIds;
    }

    public List<String> getShortcutIds() {
        return this.mShortcutIds;
    }

    public ComponentName getActivity() {
        return this.mActivity;
    }

    public int getQueryFlags() {
        return this.mQueryFlags;
    }

    public ShortcutQueryWrapper() {
    }

    public String toString() {
        return "ShortcutQueryWrapper {  }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mPackage != null ? (byte) (0 | 2) : (byte) 0;
        if (this.mShortcutIds != null) {
            flg = (byte) (flg | 4);
        }
        if (this.mLocusIds != null) {
            flg = (byte) (flg | 8);
        }
        if (this.mActivity != null) {
            flg = (byte) (flg | 16);
        }
        dest.writeByte(flg);
        dest.writeLong(this.mChangedSince);
        if (this.mPackage != null) {
            dest.writeString(this.mPackage);
        }
        if (this.mShortcutIds != null) {
            dest.writeStringList(this.mShortcutIds);
        }
        if (this.mLocusIds != null) {
            dest.writeParcelableList(this.mLocusIds, flags);
        }
        if (this.mActivity != null) {
            dest.writeTypedObject(this.mActivity, flags);
        }
        dest.writeInt(this.mQueryFlags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    ShortcutQueryWrapper(Parcel in) {
        byte flg = in.readByte();
        long changedSince = in.readLong();
        String pkg = (flg & 2) == 0 ? null : in.readString();
        List<String> shortcutIds = null;
        if ((flg & 4) != 0) {
            shortcutIds = new ArrayList<>();
            in.readStringList(shortcutIds);
        }
        ArrayList arrayList = null;
        if ((flg & 8) != 0) {
            arrayList = new ArrayList();
            in.readParcelableList(arrayList, LocusId.class.getClassLoader(), LocusId.class);
        }
        ComponentName activity = (flg & 16) == 0 ? null : (ComponentName) in.readTypedObject(ComponentName.CREATOR);
        int queryFlags = in.readInt();
        this.mChangedSince = changedSince;
        this.mPackage = pkg;
        this.mShortcutIds = shortcutIds;
        this.mLocusIds = arrayList;
        this.mActivity = activity;
        this.mQueryFlags = queryFlags;
        AnnotationValidations.validate((Class<? extends Annotation>) LauncherApps.ShortcutQuery.QueryFlags.class, (Annotation) null, this.mQueryFlags);
    }

    @Deprecated
    private void __metadata() {
    }
}
