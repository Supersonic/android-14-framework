package com.android.server.p011pm.pkg.component;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.PackageImpl;
import libcore.util.EmptyArray;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.pkg.component.ParsedMainComponentImpl */
/* loaded from: classes2.dex */
public class ParsedMainComponentImpl extends ParsedComponentImpl implements ParsedMainComponent {
    public static final Parcelable.Creator<ParsedMainComponentImpl> CREATOR = new Parcelable.Creator<ParsedMainComponentImpl>() { // from class: com.android.server.pm.pkg.component.ParsedMainComponentImpl.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedMainComponentImpl createFromParcel(Parcel parcel) {
            return new ParsedMainComponentImpl(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedMainComponentImpl[] newArray(int i) {
            return new ParsedMainComponentImpl[i];
        }
    };
    public String[] attributionTags;
    public boolean directBootAware;
    public boolean enabled;
    public boolean exported;
    public int order;
    public String processName;
    public String splitName;

    @Override // com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ParsedMainComponentImpl() {
        this.enabled = true;
    }

    public ParsedMainComponentImpl(ParsedMainComponent parsedMainComponent) {
        super(parsedMainComponent);
        this.enabled = true;
        this.processName = parsedMainComponent.getProcessName();
        this.directBootAware = parsedMainComponent.isDirectBootAware();
        this.enabled = parsedMainComponent.isEnabled();
        this.exported = parsedMainComponent.isExported();
        this.order = parsedMainComponent.getOrder();
        this.splitName = parsedMainComponent.getSplitName();
        this.attributionTags = parsedMainComponent.getAttributionTags();
    }

    public ParsedMainComponentImpl setProcessName(String str) {
        this.processName = TextUtils.safeIntern(str);
        return this;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public String getClassName() {
        return getName();
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public String[] getAttributionTags() {
        String[] strArr = this.attributionTags;
        return strArr == null ? EmptyArray.STRING : strArr;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        PackageImpl.sForInternedString.parcel(this.processName, parcel, i);
        parcel.writeBoolean(this.directBootAware);
        parcel.writeBoolean(this.enabled);
        parcel.writeBoolean(this.exported);
        parcel.writeInt(this.order);
        parcel.writeString(this.splitName);
        parcel.writeString8Array(this.attributionTags);
    }

    public ParsedMainComponentImpl(Parcel parcel) {
        super(parcel);
        this.enabled = true;
        this.processName = PackageImpl.sForInternedString.unparcel(parcel);
        this.directBootAware = parcel.readBoolean();
        this.enabled = parcel.readBoolean();
        this.exported = parcel.readBoolean();
        this.order = parcel.readInt();
        this.splitName = parcel.readString();
        this.attributionTags = parcel.createString8Array();
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public String getProcessName() {
        return this.processName;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public boolean isDirectBootAware() {
        return this.directBootAware;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public boolean isExported() {
        return this.exported;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public int getOrder() {
        return this.order;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponent
    public String getSplitName() {
        return this.splitName;
    }

    public ParsedMainComponentImpl setDirectBootAware(boolean z) {
        this.directBootAware = z;
        return this;
    }

    public ParsedMainComponentImpl setEnabled(boolean z) {
        this.enabled = z;
        return this;
    }

    public ParsedMainComponentImpl setExported(boolean z) {
        this.exported = z;
        return this;
    }

    public ParsedMainComponentImpl setOrder(int i) {
        this.order = i;
        return this;
    }

    public ParsedMainComponentImpl setSplitName(String str) {
        this.splitName = str;
        return this;
    }

    public ParsedMainComponentImpl setAttributionTags(String... strArr) {
        this.attributionTags = strArr;
        return this;
    }
}
