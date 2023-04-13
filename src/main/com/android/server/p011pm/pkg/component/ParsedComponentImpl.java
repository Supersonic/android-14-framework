package com.android.server.p011pm.pkg.component;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.CollectionUtils;
import com.android.server.p011pm.parsing.pkg.PackageImpl;
import com.android.server.p011pm.pkg.parsing.ParsingUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.pkg.component.ParsedComponentImpl */
/* loaded from: classes2.dex */
public abstract class ParsedComponentImpl implements ParsedComponent, Parcelable {
    public int banner;
    public ComponentName componentName;
    public int descriptionRes;
    public int flags;
    public int icon;
    public List<ParsedIntentInfoImpl> intents;
    public int labelRes;
    public int logo;
    public Map<String, PackageManager.Property> mProperties;
    public Bundle metaData;
    public String name;
    public CharSequence nonLocalizedLabel;
    public String packageName;

    public int describeContents() {
        return 0;
    }

    public ParsedComponentImpl() {
        this.intents = Collections.emptyList();
        this.mProperties = Collections.emptyMap();
    }

    public ParsedComponentImpl(ParsedComponent parsedComponent) {
        this.intents = Collections.emptyList();
        this.mProperties = Collections.emptyMap();
        this.metaData = parsedComponent.getMetaData();
        this.name = parsedComponent.getName();
        this.icon = parsedComponent.getIcon();
        this.labelRes = parsedComponent.getLabelRes();
        this.nonLocalizedLabel = parsedComponent.getNonLocalizedLabel();
        this.logo = parsedComponent.getLogo();
        this.banner = parsedComponent.getBanner();
        this.descriptionRes = parsedComponent.getDescriptionRes();
        this.flags = parsedComponent.getFlags();
        this.packageName = parsedComponent.getPackageName();
        this.componentName = parsedComponent.getComponentName();
        this.intents = new ArrayList(((ParsedComponentImpl) parsedComponent).intents);
        ArrayMap arrayMap = new ArrayMap();
        this.mProperties = arrayMap;
        arrayMap.putAll(parsedComponent.getProperties());
    }

    public void addIntent(ParsedIntentInfoImpl parsedIntentInfoImpl) {
        this.intents = CollectionUtils.add(this.intents, parsedIntentInfoImpl);
    }

    public void addProperty(PackageManager.Property property) {
        this.mProperties = CollectionUtils.add(this.mProperties, property.getName(), property);
    }

    public ParsedComponentImpl setName(String str) {
        this.name = TextUtils.safeIntern(str);
        return this;
    }

    public void setPackageName(String str) {
        this.packageName = TextUtils.safeIntern(str);
        this.componentName = null;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public ComponentName getComponentName() {
        if (this.componentName == null) {
            this.componentName = new ComponentName(getPackageName(), getName());
        }
        return this.componentName;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public Bundle getMetaData() {
        Bundle bundle = this.metaData;
        return bundle == null ? Bundle.EMPTY : bundle;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public List<ParsedIntentInfo> getIntents() {
        return new ArrayList(this.intents);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(getIcon());
        parcel.writeInt(getLabelRes());
        parcel.writeCharSequence(getNonLocalizedLabel());
        parcel.writeInt(getLogo());
        parcel.writeInt(getBanner());
        parcel.writeInt(getDescriptionRes());
        parcel.writeInt(getFlags());
        PackageImpl.sForInternedString.parcel(this.packageName, parcel, i);
        parcel.writeTypedList(this.intents);
        parcel.writeBundle(this.metaData);
        parcel.writeMap(this.mProperties);
    }

    public ParsedComponentImpl(Parcel parcel) {
        this.intents = Collections.emptyList();
        this.mProperties = Collections.emptyMap();
        ClassLoader classLoader = Object.class.getClassLoader();
        this.name = parcel.readString();
        this.icon = parcel.readInt();
        this.labelRes = parcel.readInt();
        this.nonLocalizedLabel = parcel.readCharSequence();
        this.logo = parcel.readInt();
        this.banner = parcel.readInt();
        this.descriptionRes = parcel.readInt();
        this.flags = parcel.readInt();
        this.packageName = PackageImpl.sForInternedString.unparcel(parcel);
        this.intents = ParsingUtils.createTypedInterfaceList(parcel, ParsedIntentInfoImpl.CREATOR);
        this.metaData = parcel.readBundle(classLoader);
        this.mProperties = parcel.readHashMap(classLoader);
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public String getName() {
        return this.name;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public int getIcon() {
        return this.icon;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public int getLabelRes() {
        return this.labelRes;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public CharSequence getNonLocalizedLabel() {
        return this.nonLocalizedLabel;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public int getLogo() {
        return this.logo;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public int getBanner() {
        return this.banner;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public int getDescriptionRes() {
        return this.descriptionRes;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public int getFlags() {
        return this.flags;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public String getPackageName() {
        return this.packageName;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedComponent
    public Map<String, PackageManager.Property> getProperties() {
        return this.mProperties;
    }

    public ParsedComponentImpl setIcon(int i) {
        this.icon = i;
        return this;
    }

    public ParsedComponentImpl setLabelRes(int i) {
        this.labelRes = i;
        return this;
    }

    public ParsedComponentImpl setNonLocalizedLabel(CharSequence charSequence) {
        this.nonLocalizedLabel = charSequence;
        return this;
    }

    public ParsedComponentImpl setLogo(int i) {
        this.logo = i;
        return this;
    }

    public ParsedComponentImpl setBanner(int i) {
        this.banner = i;
        return this;
    }

    public ParsedComponentImpl setDescriptionRes(int i) {
        this.descriptionRes = i;
        return this;
    }

    public ParsedComponentImpl setFlags(int i) {
        this.flags = i;
        return this;
    }

    public ParsedComponentImpl setMetaData(Bundle bundle) {
        this.metaData = bundle;
        return this;
    }
}
