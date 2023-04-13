package android.net.vcn.persistablebundleutils;

import android.net.InetAddresses;
import android.net.ipsec.ike.IkeDerAsn1DnIdentification;
import android.net.ipsec.ike.IkeFqdnIdentification;
import android.net.ipsec.ike.IkeIdentification;
import android.net.ipsec.ike.IkeIpv4AddrIdentification;
import android.net.ipsec.ike.IkeIpv6AddrIdentification;
import android.net.ipsec.ike.IkeKeyIdIdentification;
import android.net.ipsec.ike.IkeRfc822AddrIdentification;
import android.p008os.PersistableBundle;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.Objects;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes2.dex */
public final class IkeIdentificationUtils {
    private static final String DER_ASN1_DN_KEY = "DER_ASN1_DN_KEY";
    private static final String FQDN_KEY = "FQDN_KEY";
    private static final int ID_TYPE_DER_ASN1_DN = 1;
    private static final int ID_TYPE_FQDN = 2;
    private static final int ID_TYPE_IPV4_ADDR = 3;
    private static final int ID_TYPE_IPV6_ADDR = 4;
    private static final String ID_TYPE_KEY = "ID_TYPE_KEY";
    private static final int ID_TYPE_KEY_ID = 5;
    private static final int ID_TYPE_RFC822_ADDR = 6;
    private static final String IP4_ADDRESS_KEY = "IP4_ADDRESS_KEY";
    private static final String IP6_ADDRESS_KEY = "IP6_ADDRESS_KEY";
    private static final String KEY_ID_KEY = "KEY_ID_KEY";
    private static final String RFC822_ADDRESS_KEY = "RFC822_ADDRESS_KEY";

    public static PersistableBundle toPersistableBundle(IkeIdentification ikeId) {
        if (ikeId instanceof IkeDerAsn1DnIdentification) {
            PersistableBundle result = createPersistableBundle(1);
            IkeDerAsn1DnIdentification id = (IkeDerAsn1DnIdentification) ikeId;
            result.putPersistableBundle(DER_ASN1_DN_KEY, PersistableBundleUtils.fromByteArray(id.derAsn1Dn.getEncoded()));
            return result;
        } else if (ikeId instanceof IkeFqdnIdentification) {
            PersistableBundle result2 = createPersistableBundle(2);
            IkeFqdnIdentification id2 = (IkeFqdnIdentification) ikeId;
            result2.putString(FQDN_KEY, id2.fqdn);
            return result2;
        } else if (ikeId instanceof IkeIpv4AddrIdentification) {
            PersistableBundle result3 = createPersistableBundle(3);
            IkeIpv4AddrIdentification id3 = (IkeIpv4AddrIdentification) ikeId;
            result3.putString(IP4_ADDRESS_KEY, id3.ipv4Address.getHostAddress());
            return result3;
        } else if (ikeId instanceof IkeIpv6AddrIdentification) {
            PersistableBundle result4 = createPersistableBundle(4);
            IkeIpv6AddrIdentification id4 = (IkeIpv6AddrIdentification) ikeId;
            result4.putString(IP6_ADDRESS_KEY, id4.ipv6Address.getHostAddress());
            return result4;
        } else if (ikeId instanceof IkeKeyIdIdentification) {
            PersistableBundle result5 = createPersistableBundle(5);
            IkeKeyIdIdentification id5 = (IkeKeyIdIdentification) ikeId;
            result5.putPersistableBundle(KEY_ID_KEY, PersistableBundleUtils.fromByteArray(id5.keyId));
            return result5;
        } else if (ikeId instanceof IkeRfc822AddrIdentification) {
            PersistableBundle result6 = createPersistableBundle(6);
            IkeRfc822AddrIdentification id6 = (IkeRfc822AddrIdentification) ikeId;
            result6.putString(RFC822_ADDRESS_KEY, id6.rfc822Name);
            return result6;
        } else {
            throw new IllegalStateException("Unrecognized IkeIdentification subclass");
        }
    }

    private static PersistableBundle createPersistableBundle(int idType) {
        PersistableBundle result = new PersistableBundle();
        result.putInt(ID_TYPE_KEY, idType);
        return result;
    }

    public static IkeIdentification fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle was null");
        int idType = in.getInt(ID_TYPE_KEY);
        switch (idType) {
            case 1:
                PersistableBundle dnBundle = in.getPersistableBundle(DER_ASN1_DN_KEY);
                Objects.requireNonNull(dnBundle, "ASN1 DN was null");
                return new IkeDerAsn1DnIdentification(new X500Principal(PersistableBundleUtils.toByteArray(dnBundle)));
            case 2:
                return new IkeFqdnIdentification(in.getString(FQDN_KEY));
            case 3:
                String v4AddressStr = in.getString(IP4_ADDRESS_KEY);
                Objects.requireNonNull(v4AddressStr, "IPv4 address was null");
                return new IkeIpv4AddrIdentification((Inet4Address) InetAddresses.parseNumericAddress(v4AddressStr));
            case 4:
                String v6AddressStr = in.getString(IP6_ADDRESS_KEY);
                Objects.requireNonNull(v6AddressStr, "IPv6 address was null");
                return new IkeIpv6AddrIdentification((Inet6Address) InetAddresses.parseNumericAddress(v6AddressStr));
            case 5:
                PersistableBundle keyIdBundle = in.getPersistableBundle(KEY_ID_KEY);
                Objects.requireNonNull(in, "Key ID was null");
                return new IkeKeyIdIdentification(PersistableBundleUtils.toByteArray(keyIdBundle));
            case 6:
                return new IkeRfc822AddrIdentification(in.getString(RFC822_ADDRESS_KEY));
            default:
                throw new IllegalStateException("Unrecognized IKE ID type: " + idType);
        }
    }
}
