package android.net.util;
/* loaded from: classes.dex */
public final class NetworkConstants {
    public static final int DNS_SERVER_PORT = 53;
    public static final byte[] ETHER_ADDR_BROADCAST;
    public static final int ETHER_MTU = 1500;

    /* renamed from: FF */
    public static final byte f15FF;
    public static final int ICMPV4_ECHO_REQUEST_TYPE = 8;
    public static final int ICMPV6_ECHO_REQUEST_TYPE = 128;
    public static final int ICMP_ECHO_DATA_OFFSET = 8;
    public static final int ICMP_ECHO_IDENTIFIER_OFFSET = 4;
    public static final int ICMP_ECHO_SEQUENCE_NUMBER_OFFSET = 6;
    public static final int ICMP_HEADER_CHECKSUM_OFFSET = 2;
    public static final int ICMP_HEADER_CODE_OFFSET = 1;
    public static final int ICMP_HEADER_TYPE_OFFSET = 0;
    public static final int IPV4_ADDR_BITS = 32;
    public static final int IPV6_ADDR_BITS = 128;
    public static final int IPV6_ADDR_LEN = 16;
    public static final int IPV6_MIN_MTU = 1280;
    public static final int RFC7421_PREFIX_LENGTH = 64;

    public static byte asByte(int i) {
        return (byte) i;
    }

    private NetworkConstants() {
        throw new RuntimeException("no instance permitted");
    }

    static {
        byte asByte = asByte(255);
        f15FF = asByte;
        ETHER_ADDR_BROADCAST = new byte[]{asByte, asByte, asByte, asByte, asByte, asByte};
    }
}
