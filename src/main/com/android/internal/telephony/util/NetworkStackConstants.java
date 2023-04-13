package com.android.internal.telephony.util;

import android.net.InetAddresses;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
/* loaded from: classes.dex */
public final class NetworkStackConstants {
    public static final int ARP_ETHER_IPV4_LEN = 42;
    public static final int ARP_HWTYPE_ETHER = 1;
    public static final int ARP_HWTYPE_RESERVED_HI = 65535;
    public static final int ARP_HWTYPE_RESERVED_LO = 0;
    public static final int ARP_PAYLOAD_LEN = 28;
    public static final int ARP_REPLY = 2;
    public static final int ARP_REQUEST = 1;
    public static final int DHCP4_CLIENT_PORT = 68;
    public static final int DHCP6_CLIENT_PORT = 546;
    public static final int DHCP6_OPTION_IAPREFIX = 26;
    public static final int DHCP6_OPTION_IA_PD = 25;
    public static final int DHCP6_SERVER_PORT = 547;
    public static final int ETHER_ADDR_LEN = 6;
    public static final int ETHER_DST_ADDR_OFFSET = 0;
    public static final int ETHER_HEADER_LEN = 14;
    public static final int ETHER_MTU = 1500;
    public static final int ETHER_SRC_ADDR_OFFSET = 6;
    public static final int ETHER_TYPE_ARP = 2054;
    public static final int ETHER_TYPE_IPV4 = 2048;
    public static final int ETHER_TYPE_IPV6 = 34525;
    public static final int ETHER_TYPE_LENGTH = 2;
    public static final int ETHER_TYPE_OFFSET = 12;
    public static final int ICMPV6_CHECKSUM_OFFSET = 2;
    public static final int ICMPV6_ECHO_REPLY_TYPE = 129;
    public static final int ICMPV6_ECHO_REQUEST_TYPE = 128;
    public static final int ICMPV6_HEADER_MIN_LEN = 4;
    public static final int ICMPV6_NA_HEADER_LEN = 24;
    public static final int ICMPV6_ND_OPTION_LENGTH_SCALING_FACTOR = 8;
    public static final int ICMPV6_ND_OPTION_MIN_LENGTH = 8;
    public static final int ICMPV6_ND_OPTION_MTU = 5;
    public static final int ICMPV6_ND_OPTION_PIO = 3;
    public static final int ICMPV6_ND_OPTION_PREF64 = 38;
    public static final int ICMPV6_ND_OPTION_RDNSS = 25;
    public static final int ICMPV6_ND_OPTION_SLLA = 1;
    public static final int ICMPV6_ND_OPTION_TLLA = 2;
    public static final int ICMPV6_NEIGHBOR_ADVERTISEMENT = 136;
    public static final int ICMPV6_NEIGHBOR_SOLICITATION = 135;
    public static final int ICMPV6_NS_HEADER_LEN = 24;
    public static final int ICMPV6_RA_HEADER_LEN = 16;
    public static final int ICMPV6_ROUTER_ADVERTISEMENT = 134;
    public static final int ICMPV6_ROUTER_SOLICITATION = 133;
    public static final int ICMPV6_RS_HEADER_LEN = 8;
    public static final int ICMP_CHECKSUM_OFFSET = 2;
    public static final int INFINITE_LEASE = -1;
    public static final int IPV4_ADDR_BITS = 32;
    public static final int IPV4_ADDR_LEN = 4;
    public static final int IPV4_CHECKSUM_OFFSET = 10;
    public static final int IPV4_CONFLICT_ANNOUNCE_NUM = 2;
    public static final int IPV4_CONFLICT_PROBE_NUM = 3;
    public static final int IPV4_DST_ADDR_OFFSET = 16;
    public static final int IPV4_FLAGS_OFFSET = 6;
    public static final int IPV4_FLAG_DF = 16384;
    public static final int IPV4_FLAG_MF = 8192;
    public static final int IPV4_FRAGMENT_MASK = 8191;
    public static final int IPV4_HEADER_MIN_LEN = 20;
    public static final int IPV4_IHL_MASK = 15;
    public static final int IPV4_LENGTH_OFFSET = 2;
    public static final int IPV4_MAX_MTU = 65535;
    public static final int IPV4_MIN_MTU = 68;
    public static final int IPV4_PROTOCOL_OFFSET = 9;
    public static final int IPV4_SRC_ADDR_OFFSET = 12;
    public static final int IPV6_ADDR_LEN = 16;
    public static final int IPV6_DST_ADDR_OFFSET = 24;
    public static final int IPV6_HEADER_LEN = 40;
    public static final int IPV6_LEN_OFFSET = 4;
    public static final int IPV6_MIN_MTU = 1280;
    public static final int IPV6_PROTOCOL_OFFSET = 6;
    public static final int IPV6_SRC_ADDR_OFFSET = 8;
    public static final int NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE = 536870912;
    public static final int NEIGHBOR_ADVERTISEMENT_FLAG_ROUTER = Integer.MIN_VALUE;
    public static final int NEIGHBOR_ADVERTISEMENT_FLAG_SOLICITED = 1073741824;
    public static final byte PIO_FLAG_AUTONOMOUS = 64;
    public static final byte PIO_FLAG_ON_LINK = Byte.MIN_VALUE;
    public static final byte ROUTER_ADVERTISEMENT_FLAG_MANAGED_ADDRESS = Byte.MIN_VALUE;
    public static final byte ROUTER_ADVERTISEMENT_FLAG_OTHER = 64;
    public static final int TAG_SYSTEM_DHCP = -511;
    public static final int TAG_SYSTEM_DHCP_SERVER = -509;
    public static final int TAG_SYSTEM_DNS = -126;
    public static final int TAG_SYSTEM_NEIGHBOR = -510;
    public static final int TAG_SYSTEM_PROBE = -127;
    public static final byte TCPHDR_ACK = 16;
    public static final byte TCPHDR_FIN = 1;
    public static final byte TCPHDR_PSH = 8;
    public static final byte TCPHDR_RST = 4;
    public static final byte TCPHDR_SYN = 2;
    public static final byte TCPHDR_URG = 32;
    public static final int TCP_CHECKSUM_OFFSET = 16;
    public static final int TCP_HEADER_MIN_LEN = 20;
    public static final String TEST_CAPTIVE_PORTAL_HTTPS_URL = "test_captive_portal_https_url";
    public static final String TEST_CAPTIVE_PORTAL_HTTP_URL = "test_captive_portal_http_url";
    public static final String TEST_URL_EXPIRATION_TIME = "test_url_expiration_time";
    public static final int UDP_CHECKSUM_OFFSET = 6;
    public static final int UDP_HEADER_LEN = 8;
    public static final int UDP_LENGTH_OFFSET = 4;
    public static final int VENDOR_SPECIFIC_IE_ID = 221;
    public static final byte[] ETHER_BROADCAST = {-1, -1, -1, -1, -1, -1};
    public static final Inet4Address IPV4_ADDR_ALL = makeInet4Address((byte) -1, (byte) -1, (byte) -1, (byte) -1);
    public static final Inet4Address IPV4_ADDR_ANY = makeInet4Address((byte) 0, (byte) 0, (byte) 0, (byte) 0);
    public static final Inet6Address IPV6_ADDR_ANY = makeInet6Address(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final Inet6Address IPV6_ADDR_ALL_NODES_MULTICAST = (Inet6Address) InetAddresses.parseNumericAddress("ff02::1");
    public static final Inet6Address IPV6_ADDR_ALL_ROUTERS_MULTICAST = (Inet6Address) InetAddresses.parseNumericAddress("ff02::2");
    public static final Inet6Address IPV6_ADDR_ALL_HOSTS_MULTICAST = (Inet6Address) InetAddresses.parseNumericAddress("ff02::3");
    public static final Inet6Address ALL_DHCP_RELAY_AGENTS_AND_SERVERS = (Inet6Address) InetAddresses.parseNumericAddress("ff02::1:2");

    private static Inet4Address makeInet4Address(byte b, byte b2, byte b3, byte b4) {
        try {
            return (Inet4Address) InetAddress.getByAddress(new byte[]{b, b2, b3, b4});
        } catch (UnknownHostException unused) {
            throw new IllegalArgumentException("addr must be 4 bytes: this should never happen");
        }
    }

    private static Inet6Address makeInet6Address(byte[] bArr) {
        try {
            return (Inet6Address) InetAddress.getByAddress(bArr);
        } catch (UnknownHostException unused) {
            throw new IllegalArgumentException("addr must be 16 bytes: this should never happen");
        }
    }

    private NetworkStackConstants() {
        throw new UnsupportedOperationException("This class is not to be instantiated");
    }
}
