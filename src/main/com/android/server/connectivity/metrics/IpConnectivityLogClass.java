package com.android.server.connectivity.metrics;
/* loaded from: classes5.dex */
public final class IpConnectivityLogClass {
    public static final int BLUETOOTH = 1;
    public static final int CELLULAR = 2;
    public static final int ETHERNET = 3;
    public static final int LOWPAN = 9;
    public static final int MULTIPLE = 6;
    public static final int NONE = 5;
    public static final int UNKNOWN = 0;
    public static final int WIFI = 4;
    public static final int WIFI_NAN = 8;
    public static final int WIFI_P2P = 7;

    /* loaded from: classes5.dex */
    public final class NetworkId {
        public static final long NETWORK_ID = 1120986464257L;

        public NetworkId() {
        }
    }

    /* loaded from: classes5.dex */
    public final class Pair {
        public static final long KEY = 1120986464257L;
        public static final long VALUE = 1120986464258L;

        public Pair() {
        }
    }

    /* loaded from: classes5.dex */
    public final class DefaultNetworkEvent {
        public static final long DEFAULT_NETWORK_DURATION_MS = 1112396529669L;
        public static final int DISCONNECT = 3;
        public static final int DUAL = 3;
        public static final long FINAL_SCORE = 1112396529672L;
        public static final long INITIAL_SCORE = 1112396529671L;
        public static final int INVALIDATION = 2;
        public static final int IPV4 = 1;
        public static final int IPV6 = 2;
        public static final long IP_SUPPORT = 1159641169929L;
        public static final long NETWORK_ID = 1146756268033L;
        public static final int NONE = 0;
        public static final long NO_DEFAULT_NETWORK_DURATION_MS = 1112396529670L;
        public static final int OUTSCORED = 1;
        public static final long PREVIOUS_DEFAULT_NETWORK_LINK_LAYER = 1159641169930L;
        public static final long PREVIOUS_NETWORK_ID = 1146756268034L;
        public static final long PREVIOUS_NETWORK_IP_SUPPORT = 1159641169923L;
        public static final long TRANSPORT_TYPES = 2220498092036L;
        public static final int UNKNOWN = 0;
        public static final long VALIDATION_DURATION_MS = 1112396529675L;

        public DefaultNetworkEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class IpReachabilityEvent {
        public static final long EVENT_TYPE = 1120986464258L;
        public static final long IF_NAME = 1138166333441L;

        public IpReachabilityEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class NetworkEvent {
        public static final long EVENT_TYPE = 1120986464258L;
        public static final long LATENCY_MS = 1120986464259L;
        public static final long NETWORK_ID = 1146756268033L;

        public NetworkEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class ValidationProbeEvent {
        public static final long LATENCY_MS = 1120986464258L;
        public static final long NETWORK_ID = 1146756268033L;
        public static final long PROBE_RESULT = 1120986464260L;
        public static final long PROBE_TYPE = 1120986464259L;

        public ValidationProbeEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class DNSLookupBatch {
        public static final long EVENT_TYPES = 2220498092034L;
        public static final long GETADDRINFO_ERRORS = 2246267895817L;
        public static final long GETADDRINFO_ERROR_COUNT = 1112396529671L;
        public static final long GETADDRINFO_QUERY_COUNT = 1112396529669L;
        public static final long GETHOSTBYNAME_ERRORS = 2246267895818L;
        public static final long GETHOSTBYNAME_ERROR_COUNT = 1112396529672L;
        public static final long GETHOSTBYNAME_QUERY_COUNT = 1112396529670L;
        public static final long LATENCIES_MS = 2220498092036L;
        public static final long NETWORK_ID = 1146756268033L;
        public static final long RETURN_CODES = 2220498092035L;

        public DNSLookupBatch() {
        }
    }

    /* loaded from: classes5.dex */
    public final class DNSLatencies {
        public static final long AAAA_COUNT = 1120986464261L;
        public static final long A_COUNT = 1120986464260L;
        public static final long LATENCIES_MS = 2220498092038L;
        public static final long QUERY_COUNT = 1120986464259L;
        public static final long RETURN_CODE = 1120986464258L;
        public static final long TYPE = 1120986464257L;

        public DNSLatencies() {
        }
    }

    /* loaded from: classes5.dex */
    public final class ConnectStatistics {
        public static final long CONNECT_BLOCKING_COUNT = 1120986464261L;
        public static final long CONNECT_COUNT = 1120986464257L;
        public static final long ERRNOS_COUNTERS = 2246267895812L;
        public static final long IPV6_ADDR_COUNT = 1120986464258L;
        public static final long LATENCIES_MS = 2220498092035L;
        public static final long NON_BLOCKING_LATENCIES_MS = 2220498092038L;

        public ConnectStatistics() {
        }
    }

    /* loaded from: classes5.dex */
    public final class DHCPEvent {
        public static final long DURATION_MS = 1120986464260L;
        public static final long ERROR_CODE = 1120986464259L;
        public static final long IF_NAME = 1138166333441L;
        public static final long STATE_TRANSITION = 1138166333442L;

        public DHCPEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class ApfProgramEvent {
        public static final long CURRENT_RAS = 1120986464259L;
        public static final long DROP_MULTICAST = 1133871366149L;
        public static final long EFFECTIVE_LIFETIME = 1112396529671L;
        public static final long FILTERED_RAS = 1120986464258L;
        public static final long HAS_IPV4_ADDR = 1133871366150L;
        public static final long LIFETIME = 1112396529665L;
        public static final long PROGRAM_LENGTH = 1120986464260L;

        public ApfProgramEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class ApfStatistics {
        public static final long DROPPED_RAS = 1120986464261L;
        public static final long DURATION_MS = 1112396529665L;
        public static final long HARDWARE_COUNTERS = 2246267895822L;
        public static final long MATCHING_RAS = 1120986464259L;
        public static final long MAX_PROGRAM_SIZE = 1120986464265L;
        public static final long PARSE_ERRORS = 1120986464263L;
        public static final long PROGRAM_UPDATES = 1120986464264L;
        public static final long PROGRAM_UPDATES_ALL = 1120986464266L;
        public static final long PROGRAM_UPDATES_ALLOWING_MULTICAST = 1120986464267L;
        public static final long RECEIVED_RAS = 1120986464258L;
        public static final long TOTAL_PACKET_DROPPED = 1120986464269L;
        public static final long TOTAL_PACKET_PROCESSED = 1120986464268L;
        public static final long ZERO_LIFETIME_RAS = 1120986464262L;

        public ApfStatistics() {
        }
    }

    /* loaded from: classes5.dex */
    public final class RaEvent {
        public static final long DNSSL_LIFETIME = 1112396529670L;
        public static final long PREFIX_PREFERRED_LIFETIME = 1112396529667L;
        public static final long PREFIX_VALID_LIFETIME = 1112396529666L;
        public static final long RDNSS_LIFETIME = 1112396529669L;
        public static final long ROUTER_LIFETIME = 1112396529665L;
        public static final long ROUTE_INFO_LIFETIME = 1112396529668L;

        public RaEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class IpProvisioningEvent {
        public static final long EVENT_TYPE = 1120986464258L;
        public static final long IF_NAME = 1138166333441L;
        public static final long LATENCY_MS = 1120986464259L;

        public IpProvisioningEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class NetworkStats {
        public static final long DURATION_MS = 1112396529665L;
        public static final long EVER_VALIDATED = 1133871366147L;
        public static final long IP_SUPPORT = 1159641169922L;
        public static final long NO_CONNECTIVITY_REPORTS = 1120986464261L;
        public static final long PORTAL_FOUND = 1133871366148L;
        public static final long VALIDATION_ATTEMPTS = 1120986464262L;
        public static final long VALIDATION_EVENTS = 2246267895815L;
        public static final long VALIDATION_STATES = 2246267895816L;

        public NetworkStats() {
        }
    }

    /* loaded from: classes5.dex */
    public final class WakeupStats {
        public static final long APPLICATION_WAKEUPS = 1112396529669L;
        public static final long DURATION_SEC = 1112396529665L;
        public static final long ETHERTYPE_COUNTS = 2246267895816L;
        public static final long IP_NEXT_HEADER_COUNTS = 2246267895817L;
        public static final long L2_BROADCAST_COUNT = 1112396529676L;
        public static final long L2_MULTICAST_COUNT = 1112396529675L;
        public static final long L2_UNICAST_COUNT = 1112396529674L;
        public static final long NON_APPLICATION_WAKEUPS = 1112396529670L;
        public static final long NO_UID_WAKEUPS = 1112396529671L;
        public static final long ROOT_WAKEUPS = 1112396529667L;
        public static final long SYSTEM_WAKEUPS = 1112396529668L;
        public static final long TOTAL_WAKEUPS = 1112396529666L;

        public WakeupStats() {
        }
    }

    /* loaded from: classes5.dex */
    public final class IpConnectivityEvent {
        public static final long APF_PROGRAM_EVENT = 1146756268041L;
        public static final long APF_STATISTICS = 1146756268042L;
        public static final long CONNECT_STATISTICS = 1146756268046L;
        public static final long DEFAULT_NETWORK_EVENT = 1146756268034L;
        public static final long DHCP_EVENT = 1146756268038L;
        public static final long DNS_LATENCIES = 1146756268045L;
        public static final long DNS_LOOKUP_BATCH = 1146756268037L;
        public static final long IF_NAME = 1138166333457L;
        public static final long IP_PROVISIONING_EVENT = 1146756268039L;
        public static final long IP_REACHABILITY_EVENT = 1146756268035L;
        public static final long LINK_LAYER = 1159641169935L;
        public static final long NETWORK_EVENT = 1146756268036L;
        public static final long NETWORK_ID = 1120986464272L;
        public static final long NETWORK_STATS = 1146756268051L;
        public static final long RA_EVENT = 1146756268043L;
        public static final long TIME_MS = 1112396529665L;
        public static final long TRANSPORTS = 1112396529682L;
        public static final long VALIDATION_PROBE_EVENT = 1146756268040L;
        public static final long WAKEUP_STATS = 1146756268052L;

        public IpConnectivityEvent() {
        }
    }

    /* loaded from: classes5.dex */
    public final class IpConnectivityLog {
        public static final long DROPPED_EVENTS = 1120986464258L;
        public static final long EVENTS = 2246267895809L;
        public static final long VERSION = 1120986464259L;

        public IpConnectivityLog() {
        }
    }
}
