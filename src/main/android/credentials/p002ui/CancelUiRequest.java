package android.credentials.p002ui;

import android.annotation.NonNull;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* renamed from: android.credentials.ui.CancelUiRequest */
/* loaded from: classes.dex */
public final class CancelUiRequest implements Parcelable {
    public static final Parcelable.Creator<CancelUiRequest> CREATOR = new Parcelable.Creator<CancelUiRequest>() { // from class: android.credentials.ui.CancelUiRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CancelUiRequest createFromParcel(Parcel in) {
            return new CancelUiRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CancelUiRequest[] newArray(int size) {
            return new CancelUiRequest[size];
        }
    };
    public static final String EXTRA_CANCEL_UI_REQUEST = "android.credentials.ui.extra.EXTRA_CANCEL_UI_REQUEST";
    private final IBinder mToken;

    public IBinder getToken() {
        return this.mToken;
    }

    public CancelUiRequest(IBinder token) {
        this.mToken = token;
    }

    private CancelUiRequest(Parcel in) {
        IBinder readStrongBinder = in.readStrongBinder();
        this.mToken = readStrongBinder;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) readStrongBinder);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.mToken);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
