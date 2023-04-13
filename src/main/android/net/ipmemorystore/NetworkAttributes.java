package android.net.ipmemorystore;

import android.net.networkstack.aidl.quirks.IPv6ProvisioningLossQuirk;
import com.android.internal.annotations.VisibleForTesting;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class NetworkAttributes {
    private static final boolean DBG = true;
    private static final float NULL_MATCH_WEIGHT = 0.25f;
    @VisibleForTesting
    public static final float TOTAL_WEIGHT = 850.0f;
    private static final float TOTAL_WEIGHT_CUTOFF = 520.0f;
    private static final float WEIGHT_ASSIGNEDV4ADDR = 300.0f;
    private static final float WEIGHT_ASSIGNEDV4ADDREXPIRY = 0.0f;
    private static final float WEIGHT_CLUSTER = 300.0f;
    private static final float WEIGHT_DNSADDRESSES = 200.0f;
    private static final float WEIGHT_MTU = 50.0f;
    private static final float WEIGHT_V6PROVLOSSQUIRK = 0.0f;
    public final Inet4Address assignedV4Address;
    public final Long assignedV4AddressExpiry;
    public final String cluster;
    public final List<InetAddress> dnsAddresses;
    public final IPv6ProvisioningLossQuirk ipv6ProvisioningLossQuirk;
    public final Integer mtu;

    @VisibleForTesting
    public NetworkAttributes(Inet4Address inet4Address, Long l, String str, List<InetAddress> list, Integer num, IPv6ProvisioningLossQuirk iPv6ProvisioningLossQuirk) {
        if (num != null && num.intValue() < 0) {
            throw new IllegalArgumentException("MTU can't be negative");
        }
        if (l != null && l.longValue() <= 0) {
            throw new IllegalArgumentException("lease expiry can't be negative or zero");
        }
        this.assignedV4Address = inet4Address;
        this.assignedV4AddressExpiry = l;
        this.cluster = str;
        this.dnsAddresses = list == null ? null : Collections.unmodifiableList(new ArrayList(list));
        this.mtu = num;
        this.ipv6ProvisioningLossQuirk = iPv6ProvisioningLossQuirk;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public NetworkAttributes(NetworkAttributesParcelable networkAttributesParcelable) {
        this(r2, r3, r0, r5, r1 >= 0 ? Integer.valueOf(r1) : null, IPv6ProvisioningLossQuirk.fromStableParcelable(networkAttributesParcelable.ipv6ProvisioningLossQuirk));
        Inet4Address inet4Address = (Inet4Address) getByAddressOrNull(networkAttributesParcelable.assignedV4Address);
        long j = networkAttributesParcelable.assignedV4AddressExpiry;
        Long valueOf = j > 0 ? Long.valueOf(j) : null;
        String str = networkAttributesParcelable.cluster;
        List<InetAddress> blobArrayToInetAddressList = blobArrayToInetAddressList(networkAttributesParcelable.dnsAddresses);
        int i = networkAttributesParcelable.mtu;
    }

    private static InetAddress getByAddressOrNull(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        try {
            return InetAddress.getByAddress(bArr);
        } catch (UnknownHostException unused) {
            return null;
        }
    }

    private static List<InetAddress> blobArrayToInetAddressList(Blob[] blobArr) {
        if (blobArr == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(blobArr.length);
        for (Blob blob : blobArr) {
            InetAddress byAddressOrNull = getByAddressOrNull(blob.data);
            if (byAddressOrNull != null) {
                arrayList.add(byAddressOrNull);
            }
        }
        return arrayList;
    }

    private static Blob[] inetAddressListToBlobArray(List<InetAddress> list) {
        if (list == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            InetAddress inetAddress = list.get(i);
            if (inetAddress != null) {
                Blob blob = new Blob();
                blob.data = inetAddress.getAddress();
                arrayList.add(blob);
            }
        }
        return (Blob[]) arrayList.toArray(new Blob[0]);
    }

    public NetworkAttributesParcelable toParcelable() {
        NetworkAttributesParcelable networkAttributesParcelable = new NetworkAttributesParcelable();
        Inet4Address inet4Address = this.assignedV4Address;
        networkAttributesParcelable.assignedV4Address = inet4Address == null ? null : inet4Address.getAddress();
        Long l = this.assignedV4AddressExpiry;
        networkAttributesParcelable.assignedV4AddressExpiry = l == null ? 0L : l.longValue();
        networkAttributesParcelable.cluster = this.cluster;
        networkAttributesParcelable.dnsAddresses = inetAddressListToBlobArray(this.dnsAddresses);
        Integer num = this.mtu;
        networkAttributesParcelable.mtu = num == null ? -1 : num.intValue();
        IPv6ProvisioningLossQuirk iPv6ProvisioningLossQuirk = this.ipv6ProvisioningLossQuirk;
        networkAttributesParcelable.ipv6ProvisioningLossQuirk = iPv6ProvisioningLossQuirk != null ? iPv6ProvisioningLossQuirk.toStableParcelable() : null;
        return networkAttributesParcelable;
    }

    private float samenessContribution(float f, Object obj, Object obj2) {
        return obj == null ? obj2 == null ? NULL_MATCH_WEIGHT * f : WEIGHT_ASSIGNEDV4ADDREXPIRY : Objects.equals(obj, obj2) ? f : WEIGHT_ASSIGNEDV4ADDREXPIRY;
    }

    public float getNetworkGroupSamenessConfidence(NetworkAttributes networkAttributes) {
        float samenessContribution = samenessContribution(300.0f, this.assignedV4Address, networkAttributes.assignedV4Address) + samenessContribution(WEIGHT_ASSIGNEDV4ADDREXPIRY, this.assignedV4AddressExpiry, networkAttributes.assignedV4AddressExpiry) + samenessContribution(300.0f, this.cluster, networkAttributes.cluster) + samenessContribution(WEIGHT_DNSADDRESSES, this.dnsAddresses, networkAttributes.dnsAddresses) + samenessContribution(WEIGHT_MTU, this.mtu, networkAttributes.mtu) + samenessContribution(WEIGHT_ASSIGNEDV4ADDREXPIRY, this.ipv6ProvisioningLossQuirk, networkAttributes.ipv6ProvisioningLossQuirk);
        return samenessContribution < TOTAL_WEIGHT_CUTOFF ? samenessContribution / 1040.0f : (((samenessContribution - TOTAL_WEIGHT_CUTOFF) / 330.0f) / 2.0f) + 0.5f;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private Inet4Address mAssignedAddress;
        private Long mAssignedAddressExpiry;
        private String mCluster;
        private List<InetAddress> mDnsAddresses;
        private IPv6ProvisioningLossQuirk mIpv6ProvLossQuirk;
        private Integer mMtu;

        public Builder() {
        }

        public Builder(NetworkAttributes networkAttributes) {
            this.mAssignedAddress = networkAttributes.assignedV4Address;
            this.mAssignedAddressExpiry = networkAttributes.assignedV4AddressExpiry;
            this.mCluster = networkAttributes.cluster;
            this.mDnsAddresses = new ArrayList(networkAttributes.dnsAddresses);
            this.mMtu = networkAttributes.mtu;
            this.mIpv6ProvLossQuirk = networkAttributes.ipv6ProvisioningLossQuirk;
        }

        public Builder setAssignedV4Address(Inet4Address inet4Address) {
            this.mAssignedAddress = inet4Address;
            return this;
        }

        public Builder setAssignedV4AddressExpiry(Long l) {
            if (l != null && l.longValue() <= 0) {
                throw new IllegalArgumentException("lease expiry can't be negative or zero");
            }
            this.mAssignedAddressExpiry = l;
            return this;
        }

        public Builder setCluster(String str) {
            this.mCluster = str;
            return this;
        }

        public Builder setDnsAddresses(List<InetAddress> list) {
            if (list != null) {
                for (InetAddress inetAddress : list) {
                    if (inetAddress == null) {
                        throw new IllegalArgumentException("Null DNS address");
                    }
                }
            }
            this.mDnsAddresses = list;
            return this;
        }

        public Builder setMtu(Integer num) {
            if (num == null || num.intValue() >= 0) {
                this.mMtu = num;
                return this;
            }
            throw new IllegalArgumentException("MTU can't be negative");
        }

        public Builder setIpv6ProvLossQuirk(IPv6ProvisioningLossQuirk iPv6ProvisioningLossQuirk) {
            this.mIpv6ProvLossQuirk = iPv6ProvisioningLossQuirk;
            return this;
        }

        public NetworkAttributes build() {
            return new NetworkAttributes(this.mAssignedAddress, this.mAssignedAddressExpiry, this.mCluster, this.mDnsAddresses, this.mMtu, this.mIpv6ProvLossQuirk);
        }
    }

    public boolean isEmpty() {
        return this.assignedV4Address == null && this.assignedV4AddressExpiry == null && this.cluster == null && this.dnsAddresses == null && this.mtu == null && this.ipv6ProvisioningLossQuirk == null;
    }

    public boolean equals(Object obj) {
        if (obj instanceof NetworkAttributes) {
            NetworkAttributes networkAttributes = (NetworkAttributes) obj;
            return Objects.equals(this.assignedV4Address, networkAttributes.assignedV4Address) && Objects.equals(this.assignedV4AddressExpiry, networkAttributes.assignedV4AddressExpiry) && Objects.equals(this.cluster, networkAttributes.cluster) && Objects.equals(this.dnsAddresses, networkAttributes.dnsAddresses) && Objects.equals(this.mtu, networkAttributes.mtu) && Objects.equals(this.ipv6ProvisioningLossQuirk, networkAttributes.ipv6ProvisioningLossQuirk);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.assignedV4Address, this.assignedV4AddressExpiry, this.cluster, this.dnsAddresses, this.mtu, this.ipv6ProvisioningLossQuirk);
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(" ", "{", "}");
        ArrayList arrayList = new ArrayList();
        if (this.assignedV4Address != null) {
            stringJoiner.add("assignedV4Addr :");
            stringJoiner.add(this.assignedV4Address.toString());
        } else {
            arrayList.add("assignedV4Addr");
        }
        if (this.assignedV4AddressExpiry != null) {
            stringJoiner.add("assignedV4AddressExpiry :");
            stringJoiner.add(this.assignedV4AddressExpiry.toString());
        } else {
            arrayList.add("assignedV4AddressExpiry");
        }
        if (this.cluster != null) {
            stringJoiner.add("cluster :");
            stringJoiner.add(this.cluster);
        } else {
            arrayList.add("cluster");
        }
        if (this.dnsAddresses != null) {
            stringJoiner.add("dnsAddr : [");
            for (InetAddress inetAddress : this.dnsAddresses) {
                stringJoiner.add(inetAddress.getHostAddress());
            }
            stringJoiner.add("]");
        } else {
            arrayList.add("dnsAddr");
        }
        if (this.mtu != null) {
            stringJoiner.add("mtu :");
            stringJoiner.add(this.mtu.toString());
        } else {
            arrayList.add("mtu");
        }
        if (this.ipv6ProvisioningLossQuirk != null) {
            stringJoiner.add("ipv6ProvisioningLossQuirk : [");
            stringJoiner.add(this.ipv6ProvisioningLossQuirk.toString());
            stringJoiner.add("]");
        } else {
            arrayList.add("ipv6ProvisioningLossQuirk");
        }
        if (!arrayList.isEmpty()) {
            stringJoiner.add("; Null fields : [");
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                stringJoiner.add((String) it.next());
            }
            stringJoiner.add("]");
        }
        return stringJoiner.toString();
    }
}
