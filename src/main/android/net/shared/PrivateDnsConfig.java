package android.net.shared;

import android.net.PrivateDnsConfigParcel;
import android.text.TextUtils;
import com.android.internal.util.jobs.XmlUtils;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class PrivateDnsConfig {
    public final String hostname;
    public final InetAddress[] ips;
    public final boolean useTls;

    public PrivateDnsConfig() {
        this(false);
    }

    public PrivateDnsConfig(boolean z) {
        this.useTls = z;
        this.hostname = "";
        this.ips = new InetAddress[0];
    }

    public PrivateDnsConfig(String str, InetAddress[] inetAddressArr) {
        boolean z = !TextUtils.isEmpty(str);
        this.useTls = z;
        this.hostname = z ? str : "";
        this.ips = inetAddressArr == null ? new InetAddress[0] : inetAddressArr;
    }

    public PrivateDnsConfig(PrivateDnsConfig privateDnsConfig) {
        this.useTls = privateDnsConfig.useTls;
        this.hostname = privateDnsConfig.hostname;
        this.ips = privateDnsConfig.ips;
    }

    public boolean inStrictMode() {
        return this.useTls && !TextUtils.isEmpty(this.hostname);
    }

    public String toString() {
        return PrivateDnsConfig.class.getSimpleName() + "{" + this.useTls + XmlUtils.STRING_ARRAY_SEPARATOR + this.hostname + "/" + Arrays.toString(this.ips) + "}";
    }

    public PrivateDnsConfigParcel toParcel() {
        PrivateDnsConfigParcel privateDnsConfigParcel = new PrivateDnsConfigParcel();
        privateDnsConfigParcel.hostname = this.hostname;
        privateDnsConfigParcel.ips = (String[]) ParcelableUtil.toParcelableArray(Arrays.asList(this.ips), new InitialConfiguration$$ExternalSyntheticLambda1(), String.class);
        return privateDnsConfigParcel;
    }

    public static PrivateDnsConfig fromParcel(PrivateDnsConfigParcel privateDnsConfigParcel) {
        String[] strArr = privateDnsConfigParcel.ips;
        ArrayList fromParcelableArray = ParcelableUtil.fromParcelableArray(strArr, new InitialConfiguration$$ExternalSyntheticLambda0());
        return new PrivateDnsConfig(privateDnsConfigParcel.hostname, (InetAddress[]) fromParcelableArray.toArray(new InetAddress[strArr.length]));
    }
}
