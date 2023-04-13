package android.credentials.p002ui;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.credentials.ui.BaseDialogResult */
/* loaded from: classes.dex */
public class BaseDialogResult implements Parcelable {
    public static final Parcelable.Creator<BaseDialogResult> CREATOR = new Parcelable.Creator<BaseDialogResult>() { // from class: android.credentials.ui.BaseDialogResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BaseDialogResult createFromParcel(Parcel in) {
            return new BaseDialogResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BaseDialogResult[] newArray(int size) {
            return new BaseDialogResult[size];
        }
    };
    private static final String EXTRA_BASE_RESULT = "android.credentials.ui.extra.BASE_RESULT";
    public static final int RESULT_CODE_CANCELED_AND_LAUNCHED_SETTINGS = 1;
    public static final int RESULT_CODE_DATA_PARSING_FAILURE = 3;
    public static final int RESULT_CODE_DIALOG_COMPLETE_WITH_SELECTION = 2;
    public static final int RESULT_CODE_DIALOG_USER_CANCELED = 0;
    private final IBinder mRequestToken;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.credentials.ui.BaseDialogResult$ResultCode */
    /* loaded from: classes.dex */
    public @interface ResultCode {
    }

    public static BaseDialogResult fromResultData(Bundle resultData) {
        return (BaseDialogResult) resultData.getParcelable(EXTRA_BASE_RESULT, BaseDialogResult.class);
    }

    public static void addToBundle(BaseDialogResult result, Bundle bundle) {
        bundle.putParcelable(EXTRA_BASE_RESULT, result);
    }

    public BaseDialogResult(IBinder requestToken) {
        this.mRequestToken = requestToken;
    }

    public IBinder getRequestToken() {
        return this.mRequestToken;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseDialogResult(Parcel in) {
        IBinder requestToken = in.readStrongBinder();
        this.mRequestToken = requestToken;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) requestToken);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.mRequestToken);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
