package android.timezone;

import java.util.Objects;
/* loaded from: classes3.dex */
public final class TelephonyNetwork {
    private final com.android.i18n.timezone.TelephonyNetwork mDelegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TelephonyNetwork(com.android.i18n.timezone.TelephonyNetwork delegate) {
        this.mDelegate = (com.android.i18n.timezone.TelephonyNetwork) Objects.requireNonNull(delegate);
    }

    public String getMcc() {
        return this.mDelegate.getMcc();
    }

    public String getMnc() {
        return this.mDelegate.getMnc();
    }

    public String getCountryIsoCode() {
        return this.mDelegate.getCountryIsoCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TelephonyNetwork that = (TelephonyNetwork) o;
        return this.mDelegate.equals(that.mDelegate);
    }

    public int hashCode() {
        return Objects.hash(this.mDelegate);
    }

    public String toString() {
        return "TelephonyNetwork{mDelegate=" + this.mDelegate + '}';
    }
}
