package com.android.server.p011pm.pkg.component;

import android.content.ComponentName;
import android.content.pm.PathPermission;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PatternMatcher;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.CollectionUtils;
import com.android.server.p011pm.parsing.pkg.PackageImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.pkg.component.ParsedProviderImpl */
/* loaded from: classes2.dex */
public class ParsedProviderImpl extends ParsedMainComponentImpl implements ParsedProvider {
    public static final Parcelable.Creator<ParsedProviderImpl> CREATOR = new Parcelable.Creator<ParsedProviderImpl>() { // from class: com.android.server.pm.pkg.component.ParsedProviderImpl.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedProviderImpl createFromParcel(Parcel parcel) {
            return new ParsedProviderImpl(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedProviderImpl[] newArray(int i) {
            return new ParsedProviderImpl[i];
        }
    };
    public String authority;
    public boolean forceUriPermissions;
    public boolean grantUriPermissions;
    public int initOrder;
    public boolean multiProcess;
    public List<PathPermission> pathPermissions;
    public String readPermission;
    public boolean syncable;
    public List<PatternMatcher> uriPermissionPatterns;
    public String writePermission;

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponentImpl, com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ParsedProviderImpl(ParsedProvider parsedProvider) {
        super(parsedProvider);
        this.uriPermissionPatterns = Collections.emptyList();
        this.pathPermissions = Collections.emptyList();
        this.authority = parsedProvider.getAuthority();
        this.syncable = parsedProvider.isSyncable();
        this.readPermission = parsedProvider.getReadPermission();
        this.writePermission = parsedProvider.getWritePermission();
        this.grantUriPermissions = parsedProvider.isGrantUriPermissions();
        this.forceUriPermissions = parsedProvider.isForceUriPermissions();
        this.multiProcess = parsedProvider.isMultiProcess();
        this.initOrder = parsedProvider.getInitOrder();
        this.uriPermissionPatterns = new ArrayList(parsedProvider.getUriPermissionPatterns());
        this.pathPermissions = new ArrayList(parsedProvider.getPathPermissions());
    }

    public ParsedProviderImpl setReadPermission(String str) {
        this.readPermission = TextUtils.isEmpty(str) ? null : str.intern();
        return this;
    }

    public ParsedProviderImpl setWritePermission(String str) {
        this.writePermission = TextUtils.isEmpty(str) ? null : str.intern();
        return this;
    }

    public ParsedProviderImpl addUriPermissionPattern(PatternMatcher patternMatcher) {
        this.uriPermissionPatterns = CollectionUtils.add(this.uriPermissionPatterns, patternMatcher);
        return this;
    }

    public ParsedProviderImpl addPathPermission(PathPermission pathPermission) {
        this.pathPermissions = CollectionUtils.add(this.pathPermissions, pathPermission);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Provider{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        ComponentName.appendShortString(sb, getPackageName(), getName());
        sb.append('}');
        return sb.toString();
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponentImpl, com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.authority);
        parcel.writeBoolean(this.syncable);
        PackageImpl.sForInternedString.parcel(this.readPermission, parcel, i);
        PackageImpl.sForInternedString.parcel(this.writePermission, parcel, i);
        parcel.writeBoolean(this.grantUriPermissions);
        parcel.writeBoolean(this.forceUriPermissions);
        parcel.writeBoolean(this.multiProcess);
        parcel.writeInt(this.initOrder);
        parcel.writeTypedList(this.uriPermissionPatterns, i);
        parcel.writeTypedList(this.pathPermissions, i);
    }

    public ParsedProviderImpl() {
        this.uriPermissionPatterns = Collections.emptyList();
        this.pathPermissions = Collections.emptyList();
    }

    public ParsedProviderImpl(Parcel parcel) {
        super(parcel);
        this.uriPermissionPatterns = Collections.emptyList();
        this.pathPermissions = Collections.emptyList();
        this.authority = parcel.readString();
        this.syncable = parcel.readBoolean();
        this.readPermission = PackageImpl.sForInternedString.unparcel(parcel);
        this.writePermission = PackageImpl.sForInternedString.unparcel(parcel);
        this.grantUriPermissions = parcel.readBoolean();
        this.forceUriPermissions = parcel.readBoolean();
        this.multiProcess = parcel.readBoolean();
        this.initOrder = parcel.readInt();
        this.uriPermissionPatterns = parcel.createTypedArrayList(PatternMatcher.CREATOR);
        this.pathPermissions = parcel.createTypedArrayList(PathPermission.CREATOR);
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public String getAuthority() {
        return this.authority;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public boolean isSyncable() {
        return this.syncable;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public String getReadPermission() {
        return this.readPermission;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public String getWritePermission() {
        return this.writePermission;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public boolean isGrantUriPermissions() {
        return this.grantUriPermissions;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public boolean isForceUriPermissions() {
        return this.forceUriPermissions;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public boolean isMultiProcess() {
        return this.multiProcess;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public int getInitOrder() {
        return this.initOrder;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public List<PatternMatcher> getUriPermissionPatterns() {
        return this.uriPermissionPatterns;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedProvider
    public List<PathPermission> getPathPermissions() {
        return this.pathPermissions;
    }

    public ParsedProviderImpl setAuthority(String str) {
        this.authority = str;
        return this;
    }

    public ParsedProviderImpl setSyncable(boolean z) {
        this.syncable = z;
        return this;
    }

    public ParsedProviderImpl setGrantUriPermissions(boolean z) {
        this.grantUriPermissions = z;
        return this;
    }

    public ParsedProviderImpl setForceUriPermissions(boolean z) {
        this.forceUriPermissions = z;
        return this;
    }

    public ParsedProviderImpl setMultiProcess(boolean z) {
        this.multiProcess = z;
        return this;
    }

    public ParsedProviderImpl setInitOrder(int i) {
        this.initOrder = i;
        return this;
    }
}
