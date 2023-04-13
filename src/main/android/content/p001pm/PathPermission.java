package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PatternMatcher;
/* renamed from: android.content.pm.PathPermission */
/* loaded from: classes.dex */
public class PathPermission extends PatternMatcher {
    public static final Parcelable.Creator<PathPermission> CREATOR = new Parcelable.Creator<PathPermission>() { // from class: android.content.pm.PathPermission.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PathPermission createFromParcel(Parcel source) {
            return new PathPermission(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PathPermission[] newArray(int size) {
            return new PathPermission[size];
        }
    };
    private final String mReadPermission;
    private final String mWritePermission;

    public PathPermission(String pattern, int type, String readPermission, String writePermission) {
        super(pattern, type);
        this.mReadPermission = readPermission;
        this.mWritePermission = writePermission;
    }

    public String getReadPermission() {
        return this.mReadPermission;
    }

    public String getWritePermission() {
        return this.mWritePermission;
    }

    @Override // android.p008os.PatternMatcher, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mReadPermission);
        dest.writeString(this.mWritePermission);
    }

    public PathPermission(Parcel src) {
        super(src);
        this.mReadPermission = src.readString();
        this.mWritePermission = src.readString();
    }
}
