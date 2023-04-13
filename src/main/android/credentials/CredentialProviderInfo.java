package android.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class CredentialProviderInfo implements Parcelable {
    public static final Parcelable.Creator<CredentialProviderInfo> CREATOR = new Parcelable.Creator<CredentialProviderInfo>() { // from class: android.credentials.CredentialProviderInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialProviderInfo[] newArray(int size) {
            return new CredentialProviderInfo[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialProviderInfo createFromParcel(Parcel in) {
            return new CredentialProviderInfo(in);
        }
    };
    private final List<String> mCapabilities;
    private final boolean mIsEnabled;
    private final boolean mIsSystemProvider;
    private final CharSequence mOverrideLabel;
    private final ServiceInfo mServiceInfo;

    private CredentialProviderInfo(Builder builder) {
        ArrayList arrayList = new ArrayList();
        this.mCapabilities = arrayList;
        this.mServiceInfo = builder.mServiceInfo;
        arrayList.addAll(builder.mCapabilities);
        this.mIsSystemProvider = builder.mIsSystemProvider;
        this.mIsEnabled = builder.mIsEnabled;
        this.mOverrideLabel = builder.mOverrideLabel;
    }

    public boolean hasCapability(String credentialType) {
        return this.mCapabilities.contains(credentialType);
    }

    public ServiceInfo getServiceInfo() {
        return this.mServiceInfo;
    }

    public boolean isSystemProvider() {
        return this.mIsSystemProvider;
    }

    public Drawable getServiceIcon(Context context) {
        return this.mServiceInfo.loadIcon(context.getPackageManager());
    }

    public CharSequence getLabel(Context context) {
        CharSequence charSequence = this.mOverrideLabel;
        if (charSequence != null) {
            return charSequence;
        }
        return this.mServiceInfo.loadSafeLabel(context.getPackageManager());
    }

    public List<String> getCapabilities() {
        return Collections.unmodifiableList(this.mCapabilities);
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public ComponentName getComponentName() {
        return this.mServiceInfo.getComponentName();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mServiceInfo, flags);
        dest.writeBoolean(this.mIsSystemProvider);
        dest.writeStringList(this.mCapabilities);
        dest.writeBoolean(this.mIsEnabled);
        TextUtils.writeToParcel(this.mOverrideLabel, dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "CredentialProviderInfo {serviceInfo=" + this.mServiceInfo + ", isSystemProvider=" + this.mIsSystemProvider + ", isEnabled=" + this.mIsEnabled + ", overrideLabel=" + ((Object) this.mOverrideLabel) + ", capabilities=" + String.join(",", this.mCapabilities) + "}";
    }

    private CredentialProviderInfo(Parcel in) {
        ArrayList arrayList = new ArrayList();
        this.mCapabilities = arrayList;
        this.mServiceInfo = (ServiceInfo) in.readTypedObject(ServiceInfo.CREATOR);
        this.mIsSystemProvider = in.readBoolean();
        in.readStringList(arrayList);
        this.mIsEnabled = in.readBoolean();
        this.mOverrideLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private ServiceInfo mServiceInfo;
        private List<String> mCapabilities = new ArrayList();
        private boolean mIsSystemProvider = false;
        private boolean mIsEnabled = false;
        private CharSequence mOverrideLabel = null;

        public Builder(ServiceInfo serviceInfo) {
            this.mServiceInfo = serviceInfo;
        }

        public Builder setSystemProvider(boolean isSystemProvider) {
            this.mIsSystemProvider = isSystemProvider;
            return this;
        }

        public Builder setOverrideLabel(CharSequence overrideLabel) {
            this.mOverrideLabel = overrideLabel;
            return this;
        }

        public Builder addCapabilities(List<String> capabilities) {
            this.mCapabilities.addAll(capabilities);
            return this;
        }

        public Builder setEnabled(boolean isEnabled) {
            this.mIsEnabled = isEnabled;
            return this;
        }

        public CredentialProviderInfo build() {
            return new CredentialProviderInfo(this);
        }
    }
}
