package com.android.modules.utils;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Collections;
import java.util.List;
/* loaded from: classes5.dex */
public class StringParceledListSlice extends BaseParceledListSlice<String> {
    public static final Parcelable.ClassLoaderCreator<StringParceledListSlice> CREATOR = new Parcelable.ClassLoaderCreator<StringParceledListSlice>() { // from class: com.android.modules.utils.StringParceledListSlice.1
        @Override // android.p008os.Parcelable.Creator
        public StringParceledListSlice createFromParcel(Parcel in) {
            return new StringParceledListSlice(in, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.ClassLoaderCreator
        public StringParceledListSlice createFromParcel(Parcel in, ClassLoader loader) {
            return new StringParceledListSlice(in, loader);
        }

        @Override // android.p008os.Parcelable.Creator
        public StringParceledListSlice[] newArray(int size) {
            return new StringParceledListSlice[size];
        }
    };

    @Override // com.android.modules.utils.BaseParceledListSlice
    public /* bridge */ /* synthetic */ List<String> getList() {
        return super.getList();
    }

    @Override // com.android.modules.utils.BaseParceledListSlice
    public /* bridge */ /* synthetic */ void setInlineCountLimit(int i) {
        super.setInlineCountLimit(i);
    }

    @Override // com.android.modules.utils.BaseParceledListSlice, android.p008os.Parcelable
    public /* bridge */ /* synthetic */ void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
    }

    public StringParceledListSlice(List<String> list) {
        super(list);
    }

    private StringParceledListSlice(Parcel in, ClassLoader loader) {
        super(in, loader);
    }

    public static StringParceledListSlice emptyList() {
        return new StringParceledListSlice(Collections.emptyList());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.modules.utils.BaseParceledListSlice
    public void writeElement(String parcelable, Parcel reply, int callFlags) {
        reply.writeString(parcelable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.modules.utils.BaseParceledListSlice
    public void writeParcelableCreator(String parcelable, Parcel dest) {
    }

    @Override // com.android.modules.utils.BaseParceledListSlice
    protected Parcelable.Creator<?> readParcelableCreator(Parcel from, ClassLoader loader) {
        return Parcel.STRING_CREATOR;
    }
}
