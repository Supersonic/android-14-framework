package com.android.server.p011pm.pkg.component;

import android.annotation.NonNull;
import android.content.IntentFilter;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.AnnotationValidations;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.pkg.component.ParsedIntentInfoImpl */
/* loaded from: classes2.dex */
public class ParsedIntentInfoImpl implements ParsedIntentInfo, Parcelable {
    public static final Parcelable.Creator<ParsedIntentInfoImpl> CREATOR = new Parcelable.Creator<ParsedIntentInfoImpl>() { // from class: com.android.server.pm.pkg.component.ParsedIntentInfoImpl.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedIntentInfoImpl[] newArray(int i) {
            return new ParsedIntentInfoImpl[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedIntentInfoImpl createFromParcel(Parcel parcel) {
            return new ParsedIntentInfoImpl(parcel);
        }
    };
    public boolean mHasDefault;
    public int mIcon;
    public IntentFilter mIntentFilter;
    public int mLabelRes;
    public CharSequence mNonLocalizedLabel;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ParsedIntentInfoImpl() {
        this.mIntentFilter = new IntentFilter();
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedIntentInfo
    public boolean isHasDefault() {
        return this.mHasDefault;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedIntentInfo
    public int getLabelRes() {
        return this.mLabelRes;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedIntentInfo
    public CharSequence getNonLocalizedLabel() {
        return this.mNonLocalizedLabel;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedIntentInfo
    public int getIcon() {
        return this.mIcon;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedIntentInfo
    public IntentFilter getIntentFilter() {
        return this.mIntentFilter;
    }

    public ParsedIntentInfoImpl setHasDefault(boolean z) {
        this.mHasDefault = z;
        return this;
    }

    public ParsedIntentInfoImpl setLabelRes(int i) {
        this.mLabelRes = i;
        return this;
    }

    public ParsedIntentInfoImpl setNonLocalizedLabel(CharSequence charSequence) {
        this.mNonLocalizedLabel = charSequence;
        return this;
    }

    public ParsedIntentInfoImpl setIcon(int i) {
        this.mIcon = i;
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        byte b = this.mHasDefault ? (byte) 1 : (byte) 0;
        if (this.mNonLocalizedLabel != null) {
            b = (byte) (b | 4);
        }
        parcel.writeByte(b);
        parcel.writeInt(this.mLabelRes);
        CharSequence charSequence = this.mNonLocalizedLabel;
        if (charSequence != null) {
            parcel.writeCharSequence(charSequence);
        }
        parcel.writeInt(this.mIcon);
        parcel.writeTypedObject(this.mIntentFilter, i);
    }

    public ParsedIntentInfoImpl(Parcel parcel) {
        this.mIntentFilter = new IntentFilter();
        byte readByte = parcel.readByte();
        boolean z = (readByte & 1) != 0;
        int readInt = parcel.readInt();
        CharSequence readCharSequence = (readByte & 4) == 0 ? null : parcel.readCharSequence();
        int readInt2 = parcel.readInt();
        IntentFilter intentFilter = (IntentFilter) parcel.readTypedObject(IntentFilter.CREATOR);
        this.mHasDefault = z;
        this.mLabelRes = readInt;
        this.mNonLocalizedLabel = readCharSequence;
        this.mIcon = readInt2;
        this.mIntentFilter = intentFilter;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, intentFilter);
    }
}
