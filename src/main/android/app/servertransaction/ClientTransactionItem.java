package android.app.servertransaction;

import android.p008os.Parcelable;
/* loaded from: classes.dex */
public abstract class ClientTransactionItem implements BaseClientRequest, Parcelable {
    public int getPostExecutionState() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldHaveDefinedPreExecutionState() {
        return true;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
