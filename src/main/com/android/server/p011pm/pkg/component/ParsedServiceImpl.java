package com.android.server.p011pm.pkg.component;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.PackageImpl;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.pkg.component.ParsedServiceImpl */
/* loaded from: classes2.dex */
public class ParsedServiceImpl extends ParsedMainComponentImpl implements ParsedService {
    public static final Parcelable.Creator<ParsedServiceImpl> CREATOR = new Parcelable.Creator<ParsedServiceImpl>() { // from class: com.android.server.pm.pkg.component.ParsedServiceImpl.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedServiceImpl createFromParcel(Parcel parcel) {
            return new ParsedServiceImpl(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedServiceImpl[] newArray(int i) {
            return new ParsedServiceImpl[i];
        }
    };
    public int foregroundServiceType;
    public String permission;

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponentImpl, com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ParsedMainComponent setPermission(String str) {
        this.permission = TextUtils.isEmpty(str) ? null : str.intern();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Service{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        ComponentName.appendShortString(sb, getPackageName(), getName());
        sb.append('}');
        return sb.toString();
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponentImpl, com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.foregroundServiceType);
        PackageImpl.sForInternedString.parcel(this.permission, parcel, i);
    }

    public ParsedServiceImpl() {
    }

    public ParsedServiceImpl(Parcel parcel) {
        super(parcel);
        this.foregroundServiceType = parcel.readInt();
        this.permission = PackageImpl.sForInternedString.unparcel(parcel);
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedService
    public int getForegroundServiceType() {
        return this.foregroundServiceType;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedService
    public String getPermission() {
        return this.permission;
    }

    public ParsedServiceImpl setForegroundServiceType(int i) {
        this.foregroundServiceType = i;
        return this;
    }
}
