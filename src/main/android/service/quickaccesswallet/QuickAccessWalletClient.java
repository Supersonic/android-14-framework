package android.service.quickaccesswallet;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import java.io.Closeable;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public interface QuickAccessWalletClient extends Closeable {

    /* loaded from: classes3.dex */
    public interface OnWalletCardsRetrievedCallback {
        void onWalletCardRetrievalError(GetWalletCardsError getWalletCardsError);

        void onWalletCardsRetrieved(GetWalletCardsResponse getWalletCardsResponse);
    }

    /* loaded from: classes3.dex */
    public interface WalletPendingIntentCallback {
        void onWalletPendingIntentRetrieved(PendingIntent pendingIntent);
    }

    /* loaded from: classes3.dex */
    public interface WalletServiceEventListener {
        void onWalletServiceEvent(WalletServiceEvent walletServiceEvent);
    }

    void addWalletServiceEventListener(WalletServiceEventListener walletServiceEventListener);

    void addWalletServiceEventListener(Executor executor, WalletServiceEventListener walletServiceEventListener);

    Intent createWalletIntent();

    Intent createWalletSettingsIntent();

    void disconnect();

    Drawable getLogo();

    CharSequence getServiceLabel();

    CharSequence getShortcutLongLabel();

    CharSequence getShortcutShortLabel();

    Drawable getTileIcon();

    void getWalletCards(GetWalletCardsRequest getWalletCardsRequest, OnWalletCardsRetrievedCallback onWalletCardsRetrievedCallback);

    void getWalletCards(Executor executor, GetWalletCardsRequest getWalletCardsRequest, OnWalletCardsRetrievedCallback onWalletCardsRetrievedCallback);

    void getWalletPendingIntent(Executor executor, WalletPendingIntentCallback walletPendingIntentCallback);

    boolean isWalletFeatureAvailable();

    boolean isWalletFeatureAvailableWhenDeviceLocked();

    boolean isWalletServiceAvailable();

    void notifyWalletDismissed();

    void removeWalletServiceEventListener(WalletServiceEventListener walletServiceEventListener);

    void selectWalletCard(SelectWalletCardRequest selectWalletCardRequest);

    static QuickAccessWalletClient create(Context context) {
        return create(context, null);
    }

    static QuickAccessWalletClient create(Context context, Executor bgExecutor) {
        return new QuickAccessWalletClientImpl(context, bgExecutor);
    }
}
