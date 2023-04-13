package android.accounts;

import android.accounts.IAccountManagerResponse;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public class AccountManagerResponse implements Parcelable {
    public static final Parcelable.Creator<AccountManagerResponse> CREATOR = new Parcelable.Creator<AccountManagerResponse>() { // from class: android.accounts.AccountManagerResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccountManagerResponse createFromParcel(Parcel source) {
            return new AccountManagerResponse(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccountManagerResponse[] newArray(int size) {
            return new AccountManagerResponse[size];
        }
    };
    private IAccountManagerResponse mResponse;

    public AccountManagerResponse(IAccountManagerResponse response) {
        this.mResponse = response;
    }

    public AccountManagerResponse(Parcel parcel) {
        this.mResponse = IAccountManagerResponse.Stub.asInterface(parcel.readStrongBinder());
    }

    public void onResult(Bundle result) {
        try {
            this.mResponse.onResult(result);
        } catch (RemoteException e) {
        }
    }

    public void onError(int errorCode, String errorMessage) {
        try {
            this.mResponse.onError(errorCode, errorMessage);
        } catch (RemoteException e) {
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.mResponse.asBinder());
    }
}
