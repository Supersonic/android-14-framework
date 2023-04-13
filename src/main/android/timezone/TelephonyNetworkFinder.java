package android.timezone;

import java.util.Objects;
/* loaded from: classes3.dex */
public final class TelephonyNetworkFinder {
    private final com.android.i18n.timezone.TelephonyNetworkFinder mDelegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TelephonyNetworkFinder(com.android.i18n.timezone.TelephonyNetworkFinder delegate) {
        this.mDelegate = (com.android.i18n.timezone.TelephonyNetworkFinder) Objects.requireNonNull(delegate);
    }

    public TelephonyNetwork findNetworkByMccMnc(String mcc, String mnc) {
        Objects.requireNonNull(mcc);
        Objects.requireNonNull(mnc);
        com.android.i18n.timezone.TelephonyNetwork telephonyNetworkDelegate = this.mDelegate.findNetworkByMccMnc(mcc, mnc);
        if (telephonyNetworkDelegate != null) {
            return new TelephonyNetwork(telephonyNetworkDelegate);
        }
        return null;
    }
}
