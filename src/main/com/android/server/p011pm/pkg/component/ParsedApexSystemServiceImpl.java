package com.android.server.p011pm.pkg.component;

import android.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Parcelling;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.pkg.component.ParsedApexSystemServiceImpl */
/* loaded from: classes2.dex */
public class ParsedApexSystemServiceImpl implements ParsedApexSystemService, Parcelable {
    public static final Parcelable.Creator<ParsedApexSystemServiceImpl> CREATOR;
    public static Parcelling<String> sParcellingForJarPath;
    public static Parcelling<String> sParcellingForMaxSdkVersion;
    public static Parcelling<String> sParcellingForMinSdkVersion;
    public static Parcelling<String> sParcellingForName;
    public int initOrder;
    public String jarPath;
    public String maxSdkVersion;
    public String minSdkVersion;
    public String name;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ParsedApexSystemServiceImpl() {
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedApexSystemService
    public String getName() {
        return this.name;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedApexSystemService
    public String getJarPath() {
        return this.jarPath;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedApexSystemService
    public String getMinSdkVersion() {
        return this.minSdkVersion;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedApexSystemService
    public String getMaxSdkVersion() {
        return this.maxSdkVersion;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedApexSystemService
    public int getInitOrder() {
        return this.initOrder;
    }

    public ParsedApexSystemServiceImpl setName(String str) {
        this.name = str;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, str);
        return this;
    }

    public ParsedApexSystemServiceImpl setJarPath(String str) {
        this.jarPath = str;
        return this;
    }

    public ParsedApexSystemServiceImpl setMinSdkVersion(String str) {
        this.minSdkVersion = str;
        return this;
    }

    public ParsedApexSystemServiceImpl setMaxSdkVersion(String str) {
        this.maxSdkVersion = str;
        return this;
    }

    public ParsedApexSystemServiceImpl setInitOrder(int i) {
        this.initOrder = i;
        return this;
    }

    static {
        Parcelling<String> parcelling = Parcelling.Cache.get(Parcelling.BuiltIn.ForInternedString.class);
        sParcellingForName = parcelling;
        if (parcelling == null) {
            sParcellingForName = Parcelling.Cache.put(new Parcelling.BuiltIn.ForInternedString());
        }
        Parcelling<String> parcelling2 = Parcelling.Cache.get(Parcelling.BuiltIn.ForInternedString.class);
        sParcellingForJarPath = parcelling2;
        if (parcelling2 == null) {
            sParcellingForJarPath = Parcelling.Cache.put(new Parcelling.BuiltIn.ForInternedString());
        }
        Parcelling<String> parcelling3 = Parcelling.Cache.get(Parcelling.BuiltIn.ForInternedString.class);
        sParcellingForMinSdkVersion = parcelling3;
        if (parcelling3 == null) {
            sParcellingForMinSdkVersion = Parcelling.Cache.put(new Parcelling.BuiltIn.ForInternedString());
        }
        Parcelling<String> parcelling4 = Parcelling.Cache.get(Parcelling.BuiltIn.ForInternedString.class);
        sParcellingForMaxSdkVersion = parcelling4;
        if (parcelling4 == null) {
            sParcellingForMaxSdkVersion = Parcelling.Cache.put(new Parcelling.BuiltIn.ForInternedString());
        }
        CREATOR = new Parcelable.Creator<ParsedApexSystemServiceImpl>() { // from class: com.android.server.pm.pkg.component.ParsedApexSystemServiceImpl.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ParsedApexSystemServiceImpl[] newArray(int i) {
                return new ParsedApexSystemServiceImpl[i];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ParsedApexSystemServiceImpl createFromParcel(Parcel parcel) {
                return new ParsedApexSystemServiceImpl(parcel);
            }
        };
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        byte b = this.jarPath != null ? (byte) 2 : (byte) 0;
        if (this.minSdkVersion != null) {
            b = (byte) (b | 4);
        }
        if (this.maxSdkVersion != null) {
            b = (byte) (b | 8);
        }
        parcel.writeByte(b);
        sParcellingForName.parcel(this.name, parcel, i);
        sParcellingForJarPath.parcel(this.jarPath, parcel, i);
        sParcellingForMinSdkVersion.parcel(this.minSdkVersion, parcel, i);
        sParcellingForMaxSdkVersion.parcel(this.maxSdkVersion, parcel, i);
        parcel.writeInt(this.initOrder);
    }

    public ParsedApexSystemServiceImpl(Parcel parcel) {
        parcel.readByte();
        String str = (String) sParcellingForName.unparcel(parcel);
        int readInt = parcel.readInt();
        this.name = str;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, str);
        this.jarPath = (String) sParcellingForJarPath.unparcel(parcel);
        this.minSdkVersion = (String) sParcellingForMinSdkVersion.unparcel(parcel);
        this.maxSdkVersion = (String) sParcellingForMaxSdkVersion.unparcel(parcel);
        this.initOrder = readInt;
    }
}
