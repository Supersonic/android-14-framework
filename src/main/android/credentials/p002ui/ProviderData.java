package android.credentials.p002ui;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* renamed from: android.credentials.ui.ProviderData */
/* loaded from: classes.dex */
public abstract class ProviderData implements Parcelable {
    public static final String EXTRA_DISABLED_PROVIDER_DATA_LIST = "android.credentials.ui.extra.DISABLED_PROVIDER_DATA_LIST";
    public static final String EXTRA_ENABLED_PROVIDER_DATA_LIST = "android.credentials.ui.extra.ENABLED_PROVIDER_DATA_LIST";
    private final String mProviderFlattenedComponentName;

    public ProviderData(String providerFlattenedComponentName) {
        this.mProviderFlattenedComponentName = providerFlattenedComponentName;
    }

    public String getProviderFlattenedComponentName() {
        return this.mProviderFlattenedComponentName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ProviderData(Parcel in) {
        String providerFlattenedComponentName = in.readString8();
        this.mProviderFlattenedComponentName = providerFlattenedComponentName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) providerFlattenedComponentName);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mProviderFlattenedComponentName);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
