package android.credentials.p002ui;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.List;
/* renamed from: android.credentials.ui.CreateCredentialProviderData */
/* loaded from: classes.dex */
public final class CreateCredentialProviderData extends ProviderData implements Parcelable {
    public static final Parcelable.Creator<CreateCredentialProviderData> CREATOR = new Parcelable.Creator<CreateCredentialProviderData>() { // from class: android.credentials.ui.CreateCredentialProviderData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CreateCredentialProviderData createFromParcel(Parcel in) {
            return new CreateCredentialProviderData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CreateCredentialProviderData[] newArray(int size) {
            return new CreateCredentialProviderData[size];
        }
    };
    private final Entry mRemoteEntry;
    private final List<Entry> mSaveEntries;

    public CreateCredentialProviderData(String providerFlattenedComponentName, List<Entry> saveEntries, Entry remoteEntry) {
        super(providerFlattenedComponentName);
        this.mSaveEntries = saveEntries;
        this.mRemoteEntry = remoteEntry;
    }

    public List<Entry> getSaveEntries() {
        return this.mSaveEntries;
    }

    public Entry getRemoteEntry() {
        return this.mRemoteEntry;
    }

    private CreateCredentialProviderData(Parcel in) {
        super(in);
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, Entry.CREATOR);
        this.mSaveEntries = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
        Entry remoteEntry = (Entry) in.readTypedObject(Entry.CREATOR);
        this.mRemoteEntry = remoteEntry;
    }

    @Override // android.credentials.p002ui.ProviderData, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.mSaveEntries);
        dest.writeTypedObject(this.mRemoteEntry, flags);
    }

    @Override // android.credentials.p002ui.ProviderData, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* renamed from: android.credentials.ui.CreateCredentialProviderData$Builder */
    /* loaded from: classes.dex */
    public static final class Builder {
        private String mProviderFlattenedComponentName;
        private List<Entry> mSaveEntries = new ArrayList();
        private Entry mRemoteEntry = null;

        public Builder(String providerFlattenedComponentName) {
            this.mProviderFlattenedComponentName = providerFlattenedComponentName;
        }

        public Builder setSaveEntries(List<Entry> credentialEntries) {
            this.mSaveEntries = credentialEntries;
            return this;
        }

        public Builder setRemoteEntry(Entry remoteEntry) {
            this.mRemoteEntry = remoteEntry;
            return this;
        }

        public CreateCredentialProviderData build() {
            return new CreateCredentialProviderData(this.mProviderFlattenedComponentName, this.mSaveEntries, this.mRemoteEntry);
        }
    }
}
