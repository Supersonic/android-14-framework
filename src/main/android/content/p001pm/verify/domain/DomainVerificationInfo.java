package android.content.p001pm.verify.domain;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Parcelling;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
@SystemApi
/* renamed from: android.content.pm.verify.domain.DomainVerificationInfo */
/* loaded from: classes.dex */
public final class DomainVerificationInfo implements Parcelable {
    public static final Parcelable.Creator<DomainVerificationInfo> CREATOR;
    public static final int STATE_FIRST_VERIFIER_DEFINED = 1024;
    public static final int STATE_MODIFIABLE_UNVERIFIED = 3;
    public static final int STATE_MODIFIABLE_VERIFIED = 4;
    public static final int STATE_NO_RESPONSE = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_UNMODIFIABLE = 2;
    static Parcelling<UUID> sParcellingForIdentifier;
    private final Map<String, Integer> mHostToStateMap;
    private final UUID mIdentifier;
    private final String mPackageName;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.verify.domain.DomainVerificationInfo$State */
    /* loaded from: classes.dex */
    public @interface State {
    }

    private void parcelHostToStateMap(Parcel dest, int flags) {
        DomainVerificationUtils.writeHostMap(dest, this.mHostToStateMap);
    }

    private Map<String, Integer> unparcelHostToStateMap(Parcel in) {
        return DomainVerificationUtils.readHostMap(in, new ArrayMap(), DomainVerificationUserState.class.getClassLoader());
    }

    public static String stateToString(int value) {
        switch (value) {
            case 0:
                return "STATE_NO_RESPONSE";
            case 1:
                return "STATE_SUCCESS";
            case 2:
                return "STATE_UNMODIFIABLE";
            case 3:
                return "STATE_MODIFIABLE_UNVERIFIED";
            case 4:
                return "STATE_MODIFIABLE_VERIFIED";
            case 1024:
                return "STATE_FIRST_VERIFIER_DEFINED";
            default:
                return Integer.toHexString(value);
        }
    }

    public DomainVerificationInfo(UUID identifier, String packageName, Map<String, Integer> hostToStateMap) {
        this.mIdentifier = identifier;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) identifier);
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mHostToStateMap = hostToStateMap;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hostToStateMap);
    }

    public UUID getIdentifier() {
        return this.mIdentifier;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public Map<String, Integer> getHostToStateMap() {
        return this.mHostToStateMap;
    }

    public String toString() {
        return "DomainVerificationInfo { identifier = " + this.mIdentifier + ", packageName = " + this.mPackageName + ", hostToStateMap = " + this.mHostToStateMap + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainVerificationInfo that = (DomainVerificationInfo) o;
        if (Objects.equals(this.mIdentifier, that.mIdentifier) && Objects.equals(this.mPackageName, that.mPackageName) && Objects.equals(this.mHostToStateMap, that.mHostToStateMap)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mIdentifier);
        return (((_hash * 31) + Objects.hashCode(this.mPackageName)) * 31) + Objects.hashCode(this.mHostToStateMap);
    }

    static {
        Parcelling<UUID> parcelling = Parcelling.Cache.get(Parcelling.BuiltIn.ForUUID.class);
        sParcellingForIdentifier = parcelling;
        if (parcelling == null) {
            sParcellingForIdentifier = Parcelling.Cache.put(new Parcelling.BuiltIn.ForUUID());
        }
        CREATOR = new Parcelable.Creator<DomainVerificationInfo>() { // from class: android.content.pm.verify.domain.DomainVerificationInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DomainVerificationInfo[] newArray(int size) {
                return new DomainVerificationInfo[size];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DomainVerificationInfo createFromParcel(Parcel in) {
                return new DomainVerificationInfo(in);
            }
        };
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        sParcellingForIdentifier.parcel(this.mIdentifier, dest, flags);
        dest.writeString(this.mPackageName);
        parcelHostToStateMap(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    DomainVerificationInfo(Parcel in) {
        UUID identifier = sParcellingForIdentifier.unparcel(in);
        String packageName = in.readString();
        Map<String, Integer> hostToStateMap = unparcelHostToStateMap(in);
        this.mIdentifier = identifier;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) identifier);
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mHostToStateMap = hostToStateMap;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hostToStateMap);
    }

    @Deprecated
    private void __metadata() {
    }
}
