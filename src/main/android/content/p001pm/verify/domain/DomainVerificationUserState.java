package android.content.p001pm.verify.domain;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.util.ArrayMap;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Parcelling;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
/* renamed from: android.content.pm.verify.domain.DomainVerificationUserState */
/* loaded from: classes.dex */
public final class DomainVerificationUserState implements Parcelable {
    public static final Parcelable.Creator<DomainVerificationUserState> CREATOR;
    public static final int DOMAIN_STATE_NONE = 0;
    public static final int DOMAIN_STATE_SELECTED = 1;
    public static final int DOMAIN_STATE_VERIFIED = 2;
    static Parcelling<UUID> sParcellingForIdentifier;
    private final Map<String, Integer> mHostToStateMap;
    private final UUID mIdentifier;
    private final boolean mLinkHandlingAllowed;
    private final String mPackageName;
    private final UserHandle mUser;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.verify.domain.DomainVerificationUserState$DomainState */
    /* loaded from: classes.dex */
    public @interface DomainState {
    }

    private void parcelHostToStateMap(Parcel dest, int flags) {
        DomainVerificationUtils.writeHostMap(dest, this.mHostToStateMap);
    }

    private Map<String, Integer> unparcelHostToStateMap(Parcel in) {
        return DomainVerificationUtils.readHostMap(in, new ArrayMap(), DomainVerificationUserState.class.getClassLoader());
    }

    @SystemApi
    public UUID getIdentifier() {
        return this.mIdentifier;
    }

    public static String domainStateToString(int value) {
        switch (value) {
            case 0:
                return "DOMAIN_STATE_NONE";
            case 1:
                return "DOMAIN_STATE_SELECTED";
            case 2:
                return "DOMAIN_STATE_VERIFIED";
            default:
                return Integer.toHexString(value);
        }
    }

    public DomainVerificationUserState(UUID identifier, String packageName, UserHandle user, boolean linkHandlingAllowed, Map<String, Integer> hostToStateMap) {
        this.mIdentifier = identifier;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) identifier);
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mUser = user;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) user);
        this.mLinkHandlingAllowed = linkHandlingAllowed;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) Boolean.valueOf(linkHandlingAllowed));
        this.mHostToStateMap = hostToStateMap;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hostToStateMap);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public boolean isLinkHandlingAllowed() {
        return this.mLinkHandlingAllowed;
    }

    public Map<String, Integer> getHostToStateMap() {
        return this.mHostToStateMap;
    }

    public String toString() {
        return "DomainVerificationUserState { identifier = " + this.mIdentifier + ", packageName = " + this.mPackageName + ", user = " + this.mUser + ", linkHandlingAllowed = " + this.mLinkHandlingAllowed + ", hostToStateMap = " + this.mHostToStateMap + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainVerificationUserState that = (DomainVerificationUserState) o;
        if (Objects.equals(this.mIdentifier, that.mIdentifier) && Objects.equals(this.mPackageName, that.mPackageName) && Objects.equals(this.mUser, that.mUser) && this.mLinkHandlingAllowed == that.mLinkHandlingAllowed && Objects.equals(this.mHostToStateMap, that.mHostToStateMap)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mIdentifier);
        return (((((((_hash * 31) + Objects.hashCode(this.mPackageName)) * 31) + Objects.hashCode(this.mUser)) * 31) + Boolean.hashCode(this.mLinkHandlingAllowed)) * 31) + Objects.hashCode(this.mHostToStateMap);
    }

    static {
        Parcelling<UUID> parcelling = Parcelling.Cache.get(Parcelling.BuiltIn.ForUUID.class);
        sParcellingForIdentifier = parcelling;
        if (parcelling == null) {
            sParcellingForIdentifier = Parcelling.Cache.put(new Parcelling.BuiltIn.ForUUID());
        }
        CREATOR = new Parcelable.Creator<DomainVerificationUserState>() { // from class: android.content.pm.verify.domain.DomainVerificationUserState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DomainVerificationUserState[] newArray(int size) {
                return new DomainVerificationUserState[size];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DomainVerificationUserState createFromParcel(Parcel in) {
                return new DomainVerificationUserState(in);
            }
        };
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mLinkHandlingAllowed ? (byte) (0 | 8) : (byte) 0;
        dest.writeByte(flg);
        sParcellingForIdentifier.parcel(this.mIdentifier, dest, flags);
        dest.writeString(this.mPackageName);
        dest.writeTypedObject(this.mUser, flags);
        parcelHostToStateMap(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    DomainVerificationUserState(Parcel in) {
        byte flg = in.readByte();
        boolean linkHandlingAllowed = (flg & 8) != 0;
        UUID identifier = sParcellingForIdentifier.unparcel(in);
        String packageName = in.readString();
        UserHandle user = (UserHandle) in.readTypedObject(UserHandle.CREATOR);
        Map<String, Integer> hostToStateMap = unparcelHostToStateMap(in);
        this.mIdentifier = identifier;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) identifier);
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mUser = user;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) user);
        this.mLinkHandlingAllowed = linkHandlingAllowed;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) Boolean.valueOf(linkHandlingAllowed));
        this.mHostToStateMap = hostToStateMap;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hostToStateMap);
    }

    @Deprecated
    private void __metadata() {
    }
}
