package android.service.quickaccesswallet;
/* loaded from: classes3.dex */
public interface GetWalletCardsCallback {
    void onFailure(GetWalletCardsError getWalletCardsError);

    void onSuccess(GetWalletCardsResponse getWalletCardsResponse);
}
