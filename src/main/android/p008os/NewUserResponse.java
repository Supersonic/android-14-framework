package android.p008os;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.os.NewUserResponse */
/* loaded from: classes3.dex */
public final class NewUserResponse {
    private final int mOperationResult;
    private final UserHandle mUser;

    public NewUserResponse(UserHandle user, int operationResult) {
        this.mUser = user;
        this.mOperationResult = operationResult;
    }

    public boolean isSuccessful() {
        return this.mUser != null;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public int getOperationResult() {
        return this.mOperationResult;
    }

    public String toString() {
        return "NewUserResponse{mUser=" + this.mUser + ", mOperationResult=" + this.mOperationResult + '}';
    }
}
