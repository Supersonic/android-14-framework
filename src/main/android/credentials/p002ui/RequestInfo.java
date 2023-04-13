package android.credentials.p002ui;

import android.annotation.NonNull;
import android.credentials.CreateCredentialRequest;
import android.credentials.GetCredentialRequest;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.credentials.ui.RequestInfo */
/* loaded from: classes.dex */
public final class RequestInfo implements Parcelable {
    public static final Parcelable.Creator<RequestInfo> CREATOR = new Parcelable.Creator<RequestInfo>() { // from class: android.credentials.ui.RequestInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RequestInfo createFromParcel(Parcel in) {
            return new RequestInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RequestInfo[] newArray(int size) {
            return new RequestInfo[size];
        }
    };
    public static final String EXTRA_REQUEST_INFO = "android.credentials.ui.extra.REQUEST_INFO";
    public static final String TYPE_CREATE = "android.credentials.ui.TYPE_CREATE";
    public static final String TYPE_GET = "android.credentials.ui.TYPE_GET";
    public static final String TYPE_UNDEFINED = "android.credentials.ui.TYPE_UNDEFINED";
    private final String mAppPackageName;
    private final CreateCredentialRequest mCreateCredentialRequest;
    private final GetCredentialRequest mGetCredentialRequest;
    private final IBinder mToken;
    private final String mType;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.credentials.ui.RequestInfo$RequestType */
    /* loaded from: classes.dex */
    public @interface RequestType {
    }

    public static RequestInfo newCreateRequestInfo(IBinder token, CreateCredentialRequest createCredentialRequest, String appPackageName) {
        return new RequestInfo(token, TYPE_CREATE, appPackageName, createCredentialRequest, null);
    }

    public static RequestInfo newGetRequestInfo(IBinder token, GetCredentialRequest getCredentialRequest, String appPackageName) {
        return new RequestInfo(token, TYPE_GET, appPackageName, null, getCredentialRequest);
    }

    public IBinder getToken() {
        return this.mToken;
    }

    public String getType() {
        return this.mType;
    }

    public String getAppPackageName() {
        return this.mAppPackageName;
    }

    public CreateCredentialRequest getCreateCredentialRequest() {
        return this.mCreateCredentialRequest;
    }

    public GetCredentialRequest getGetCredentialRequest() {
        return this.mGetCredentialRequest;
    }

    private RequestInfo(IBinder token, String type, String appPackageName, CreateCredentialRequest createCredentialRequest, GetCredentialRequest getCredentialRequest) {
        this.mToken = token;
        this.mType = type;
        this.mAppPackageName = appPackageName;
        this.mCreateCredentialRequest = createCredentialRequest;
        this.mGetCredentialRequest = getCredentialRequest;
    }

    private RequestInfo(Parcel in) {
        IBinder token = in.readStrongBinder();
        String type = in.readString8();
        String appPackageName = in.readString8();
        CreateCredentialRequest createCredentialRequest = (CreateCredentialRequest) in.readTypedObject(CreateCredentialRequest.CREATOR);
        GetCredentialRequest getCredentialRequest = (GetCredentialRequest) in.readTypedObject(GetCredentialRequest.CREATOR);
        this.mToken = token;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) token);
        this.mType = type;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) type);
        this.mAppPackageName = appPackageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) appPackageName);
        this.mCreateCredentialRequest = createCredentialRequest;
        this.mGetCredentialRequest = getCredentialRequest;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.mToken);
        dest.writeString8(this.mType);
        dest.writeString8(this.mAppPackageName);
        dest.writeTypedObject(this.mCreateCredentialRequest, flags);
        dest.writeTypedObject(this.mGetCredentialRequest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
