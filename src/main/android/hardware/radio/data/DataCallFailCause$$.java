package android.hardware.radio.data;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.security.keystore.KeyProperties;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface DataCallFailCause$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? KeyProperties.DIGEST_NONE : _aidl_v == 8 ? "OPERATOR_BARRED" : _aidl_v == 14 ? "NAS_SIGNALLING" : _aidl_v == 26 ? "INSUFFICIENT_RESOURCES" : _aidl_v == 27 ? "MISSING_UNKNOWN_APN" : _aidl_v == 28 ? "UNKNOWN_PDP_ADDRESS_TYPE" : _aidl_v == 29 ? "USER_AUTHENTICATION" : _aidl_v == 30 ? "ACTIVATION_REJECT_GGSN" : _aidl_v == 31 ? "ACTIVATION_REJECT_UNSPECIFIED" : _aidl_v == 32 ? "SERVICE_OPTION_NOT_SUPPORTED" : _aidl_v == 33 ? "SERVICE_OPTION_NOT_SUBSCRIBED" : _aidl_v == 34 ? "SERVICE_OPTION_OUT_OF_ORDER" : _aidl_v == 35 ? "NSAPI_IN_USE" : _aidl_v == 36 ? "REGULAR_DEACTIVATION" : _aidl_v == 37 ? "QOS_NOT_ACCEPTED" : _aidl_v == 38 ? "NETWORK_FAILURE" : _aidl_v == 39 ? "UMTS_REACTIVATION_REQ" : _aidl_v == 40 ? "FEATURE_NOT_SUPP" : _aidl_v == 41 ? "TFT_SEMANTIC_ERROR" : _aidl_v == 42 ? "TFT_SYTAX_ERROR" : _aidl_v == 43 ? "UNKNOWN_PDP_CONTEXT" : _aidl_v == 44 ? "FILTER_SEMANTIC_ERROR" : _aidl_v == 45 ? "FILTER_SYTAX_ERROR" : _aidl_v == 46 ? "PDP_WITHOUT_ACTIVE_TFT" : _aidl_v == 50 ? "ONLY_IPV4_ALLOWED" : _aidl_v == 51 ? "ONLY_IPV6_ALLOWED" : _aidl_v == 52 ? "ONLY_SINGLE_BEARER_ALLOWED" : _aidl_v == 53 ? "ESM_INFO_NOT_RECEIVED" : _aidl_v == 54 ? "PDN_CONN_DOES_NOT_EXIST" : _aidl_v == 55 ? "MULTI_CONN_TO_SAME_PDN_NOT_ALLOWED" : _aidl_v == 65 ? "MAX_ACTIVE_PDP_CONTEXT_REACHED" : _aidl_v == 66 ? "UNSUPPORTED_APN_IN_CURRENT_PLMN" : _aidl_v == 81 ? "INVALID_TRANSACTION_ID" : _aidl_v == 95 ? "MESSAGE_INCORRECT_SEMANTIC" : _aidl_v == 96 ? "INVALID_MANDATORY_INFO" : _aidl_v == 97 ? "MESSAGE_TYPE_UNSUPPORTED" : _aidl_v == 98 ? "MSG_TYPE_NONCOMPATIBLE_STATE" : _aidl_v == 99 ? "UNKNOWN_INFO_ELEMENT" : _aidl_v == 100 ? "CONDITIONAL_IE_ERROR" : _aidl_v == 101 ? "MSG_AND_PROTOCOL_STATE_UNCOMPATIBLE" : _aidl_v == 111 ? "PROTOCOL_ERRORS" : _aidl_v == 112 ? "APN_TYPE_CONFLICT" : _aidl_v == 113 ? "INVALID_PCSCF_ADDR" : _aidl_v == 114 ? "INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN" : _aidl_v == 115 ? "EMM_ACCESS_BARRED" : _aidl_v == 116 ? "EMERGENCY_IFACE_ONLY" : _aidl_v == 117 ? "IFACE_MISMATCH" : _aidl_v == 118 ? "COMPANION_IFACE_IN_USE" : _aidl_v == 119 ? "IP_ADDRESS_MISMATCH" : _aidl_v == 120 ? "IFACE_AND_POL_FAMILY_MISMATCH" : _aidl_v == 121 ? "EMM_ACCESS_BARRED_INFINITE_RETRY" : _aidl_v == 122 ? "AUTH_FAILURE_ON_EMERGENCY_CALL" : _aidl_v == 4097 ? "OEM_DCFAILCAUSE_1" : _aidl_v == 4098 ? "OEM_DCFAILCAUSE_2" : _aidl_v == 4099 ? "OEM_DCFAILCAUSE_3" : _aidl_v == 4100 ? "OEM_DCFAILCAUSE_4" : _aidl_v == 4101 ? "OEM_DCFAILCAUSE_5" : _aidl_v == 4102 ? "OEM_DCFAILCAUSE_6" : _aidl_v == 4103 ? "OEM_DCFAILCAUSE_7" : _aidl_v == 4104 ? "OEM_DCFAILCAUSE_8" : _aidl_v == 4105 ? "OEM_DCFAILCAUSE_9" : _aidl_v == 4106 ? "OEM_DCFAILCAUSE_10" : _aidl_v == 4107 ? "OEM_DCFAILCAUSE_11" : _aidl_v == 4108 ? "OEM_DCFAILCAUSE_12" : _aidl_v == 4109 ? "OEM_DCFAILCAUSE_13" : _aidl_v == 4110 ? "OEM_DCFAILCAUSE_14" : _aidl_v == 4111 ? "OEM_DCFAILCAUSE_15" : _aidl_v == -1 ? "VOICE_REGISTRATION_FAIL" : _aidl_v == -2 ? "DATA_REGISTRATION_FAIL" : _aidl_v == -3 ? "SIGNAL_LOST" : _aidl_v == -4 ? "PREF_RADIO_TECH_CHANGED" : _aidl_v == -5 ? "RADIO_POWER_OFF" : _aidl_v == -6 ? "TETHERED_CALL_ACTIVE" : _aidl_v == 65535 ? "ERROR_UNSPECIFIED" : _aidl_v == 25 ? "LLC_SNDCP" : _aidl_v == 48 ? "ACTIVATION_REJECTED_BCM_VIOLATION" : _aidl_v == 56 ? "COLLISION_WITH_NETWORK_INITIATED_REQUEST" : _aidl_v == 57 ? "ONLY_IPV4V6_ALLOWED" : _aidl_v == 58 ? "ONLY_NON_IP_ALLOWED" : _aidl_v == 59 ? "UNSUPPORTED_QCI_VALUE" : _aidl_v == 60 ? "BEARER_HANDLING_NOT_SUPPORTED" : _aidl_v == 123 ? "INVALID_DNS_ADDR" : _aidl_v == 124 ? "INVALID_PCSCF_OR_DNS_ADDRESS" : _aidl_v == 127 ? "CALL_PREEMPT_BY_EMERGENCY_APN" : _aidl_v == 128 ? "UE_INITIATED_DETACH_OR_DISCONNECT" : _aidl_v == 2000 ? "MIP_FA_REASON_UNSPECIFIED" : _aidl_v == 2001 ? "MIP_FA_ADMIN_PROHIBITED" : _aidl_v == 2002 ? "MIP_FA_INSUFFICIENT_RESOURCES" : _aidl_v == 2003 ? "MIP_FA_MOBILE_NODE_AUTHENTICATION_FAILURE" : _aidl_v == 2004 ? "MIP_FA_HOME_AGENT_AUTHENTICATION_FAILURE" : _aidl_v == 2005 ? "MIP_FA_REQUESTED_LIFETIME_TOO_LONG" : _aidl_v == 2006 ? "MIP_FA_MALFORMED_REQUEST" : _aidl_v == 2007 ? "MIP_FA_MALFORMED_REPLY" : _aidl_v == 2008 ? "MIP_FA_ENCAPSULATION_UNAVAILABLE" : _aidl_v == 2009 ? "MIP_FA_VJ_HEADER_COMPRESSION_UNAVAILABLE" : _aidl_v == 2010 ? "MIP_FA_REVERSE_TUNNEL_UNAVAILABLE" : _aidl_v == 2011 ? "MIP_FA_REVERSE_TUNNEL_IS_MANDATORY" : _aidl_v == 2012 ? "MIP_FA_DELIVERY_STYLE_NOT_SUPPORTED" : _aidl_v == 2013 ? "MIP_FA_MISSING_NAI" : _aidl_v == 2014 ? "MIP_FA_MISSING_HOME_AGENT" : _aidl_v == 2015 ? "MIP_FA_MISSING_HOME_ADDRESS" : _aidl_v == 2016 ? "MIP_FA_UNKNOWN_CHALLENGE" : _aidl_v == 2017 ? "MIP_FA_MISSING_CHALLENGE" : _aidl_v == 2018 ? "MIP_FA_STALE_CHALLENGE" : _aidl_v == 2019 ? "MIP_HA_REASON_UNSPECIFIED" : _aidl_v == 2020 ? "MIP_HA_ADMIN_PROHIBITED" : _aidl_v == 2021 ? "MIP_HA_INSUFFICIENT_RESOURCES" : _aidl_v == 2022 ? "MIP_HA_MOBILE_NODE_AUTHENTICATION_FAILURE" : _aidl_v == 2023 ? "MIP_HA_FOREIGN_AGENT_AUTHENTICATION_FAILURE" : _aidl_v == 2024 ? "MIP_HA_REGISTRATION_ID_MISMATCH" : _aidl_v == 2025 ? "MIP_HA_MALFORMED_REQUEST" : _aidl_v == 2026 ? "MIP_HA_UNKNOWN_HOME_AGENT_ADDRESS" : _aidl_v == 2027 ? "MIP_HA_REVERSE_TUNNEL_UNAVAILABLE" : _aidl_v == 2028 ? "MIP_HA_REVERSE_TUNNEL_IS_MANDATORY" : _aidl_v == 2029 ? "MIP_HA_ENCAPSULATION_UNAVAILABLE" : _aidl_v == 2030 ? "CLOSE_IN_PROGRESS" : _aidl_v == 2031 ? "NETWORK_INITIATED_TERMINATION" : _aidl_v == 2032 ? "MODEM_APP_PREEMPTED" : _aidl_v == 2033 ? "PDN_IPV4_CALL_DISALLOWED" : _aidl_v == 2034 ? "PDN_IPV4_CALL_THROTTLED" : _aidl_v == 2035 ? "PDN_IPV6_CALL_DISALLOWED" : _aidl_v == 2036 ? "PDN_IPV6_CALL_THROTTLED" : _aidl_v == 2037 ? "MODEM_RESTART" : _aidl_v == 2038 ? "PDP_PPP_NOT_SUPPORTED" : _aidl_v == 2039 ? "UNPREFERRED_RAT" : _aidl_v == 2040 ? "PHYSICAL_LINK_CLOSE_IN_PROGRESS" : _aidl_v == 2041 ? "APN_PENDING_HANDOVER" : _aidl_v == 2042 ? "PROFILE_BEARER_INCOMPATIBLE" : _aidl_v == 2043 ? "SIM_CARD_CHANGED" : _aidl_v == 2044 ? "LOW_POWER_MODE_OR_POWERING_DOWN" : _aidl_v == 2045 ? "APN_DISABLED" : _aidl_v == 2046 ? "MAX_PPP_INACTIVITY_TIMER_EXPIRED" : _aidl_v == 2047 ? "IPV6_ADDRESS_TRANSFER_FAILED" : _aidl_v == 2048 ? "TRAT_SWAP_FAILED" : _aidl_v == 2049 ? "EHRPD_TO_HRPD_FALLBACK" : _aidl_v == 2050 ? "MIP_CONFIG_FAILURE" : _aidl_v == 2051 ? "PDN_INACTIVITY_TIMER_EXPIRED" : _aidl_v == 2052 ? "MAX_IPV4_CONNECTIONS" : _aidl_v == 2053 ? "MAX_IPV6_CONNECTIONS" : _aidl_v == 2054 ? "APN_MISMATCH" : _aidl_v == 2055 ? "IP_VERSION_MISMATCH" : _aidl_v == 2056 ? "DUN_CALL_DISALLOWED" : _aidl_v == 2057 ? "INTERNAL_EPC_NONEPC_TRANSITION" : _aidl_v == 2058 ? "INTERFACE_IN_USE" : _aidl_v == 2059 ? "APN_DISALLOWED_ON_ROAMING" : _aidl_v == 2060 ? "APN_PARAMETERS_CHANGED" : _aidl_v == 2061 ? "NULL_APN_DISALLOWED" : _aidl_v == 2062 ? "THERMAL_MITIGATION" : _aidl_v == 2063 ? "DATA_SETTINGS_DISABLED" : _aidl_v == 2064 ? "DATA_ROAMING_SETTINGS_DISABLED" : _aidl_v == 2065 ? "DDS_SWITCHED" : _aidl_v == 2066 ? "FORBIDDEN_APN_NAME" : _aidl_v == 2067 ? "DDS_SWITCH_IN_PROGRESS" : _aidl_v == 2068 ? "CALL_DISALLOWED_IN_ROAMING" : _aidl_v == 2069 ? "NON_IP_NOT_SUPPORTED" : _aidl_v == 2070 ? "PDN_NON_IP_CALL_THROTTLED" : _aidl_v == 2071 ? "PDN_NON_IP_CALL_DISALLOWED" : _aidl_v == 2072 ? "CDMA_LOCK" : _aidl_v == 2073 ? "CDMA_INTERCEPT" : _aidl_v == 2074 ? "CDMA_REORDER" : _aidl_v == 2075 ? "CDMA_RELEASE_DUE_TO_SO_REJECTION" : _aidl_v == 2076 ? "CDMA_INCOMING_CALL" : _aidl_v == 2077 ? "CDMA_ALERT_STOP" : _aidl_v == 2078 ? "CHANNEL_ACQUISITION_FAILURE" : _aidl_v == 2079 ? "MAX_ACCESS_PROBE" : _aidl_v == 2080 ? "CONCURRENT_SERVICE_NOT_SUPPORTED_BY_BASE_STATION" : _aidl_v == 2081 ? "NO_RESPONSE_FROM_BASE_STATION" : _aidl_v == 2082 ? "REJECTED_BY_BASE_STATION" : _aidl_v == 2083 ? "CONCURRENT_SERVICES_INCOMPATIBLE" : _aidl_v == 2084 ? "NO_CDMA_SERVICE" : _aidl_v == 2085 ? "RUIM_NOT_PRESENT" : _aidl_v == 2086 ? "CDMA_RETRY_ORDER" : _aidl_v == 2087 ? "ACCESS_BLOCK" : _aidl_v == 2088 ? "ACCESS_BLOCK_ALL" : _aidl_v == 2089 ? "IS707B_MAX_ACCESS_PROBES" : _aidl_v == 2090 ? "THERMAL_EMERGENCY" : _aidl_v == 2091 ? "CONCURRENT_SERVICES_NOT_ALLOWED" : _aidl_v == 2092 ? "INCOMING_CALL_REJECTED" : _aidl_v == 2093 ? "NO_SERVICE_ON_GATEWAY" : _aidl_v == 2094 ? "NO_GPRS_CONTEXT" : _aidl_v == 2095 ? "ILLEGAL_MS" : _aidl_v == 2096 ? "ILLEGAL_ME" : _aidl_v == 2097 ? "GPRS_SERVICES_AND_NON_GPRS_SERVICES_NOT_ALLOWED" : _aidl_v == 2098 ? "GPRS_SERVICES_NOT_ALLOWED" : _aidl_v == 2099 ? "MS_IDENTITY_CANNOT_BE_DERIVED_BY_THE_NETWORK" : _aidl_v == 2100 ? "IMPLICITLY_DETACHED" : _aidl_v == 2101 ? "PLMN_NOT_ALLOWED" : _aidl_v == 2102 ? "LOCATION_AREA_NOT_ALLOWED" : _aidl_v == 2103 ? "GPRS_SERVICES_NOT_ALLOWED_IN_THIS_PLMN" : _aidl_v == 2104 ? "PDP_DUPLICATE" : _aidl_v == 2105 ? "UE_RAT_CHANGE" : _aidl_v == 2106 ? "CONGESTION" : _aidl_v == 2107 ? "NO_PDP_CONTEXT_ACTIVATED" : _aidl_v == 2108 ? "ACCESS_CLASS_DSAC_REJECTION" : _aidl_v == 2109 ? "PDP_ACTIVATE_MAX_RETRY_FAILED" : _aidl_v == 2110 ? "RADIO_ACCESS_BEARER_FAILURE" : _aidl_v == 2111 ? "ESM_UNKNOWN_EPS_BEARER_CONTEXT" : _aidl_v == 2112 ? "DRB_RELEASED_BY_RRC" : _aidl_v == 2113 ? "CONNECTION_RELEASED" : _aidl_v == 2114 ? "EMM_DETACHED" : _aidl_v == 2115 ? "EMM_ATTACH_FAILED" : _aidl_v == 2116 ? "EMM_ATTACH_STARTED" : _aidl_v == 2117 ? "LTE_NAS_SERVICE_REQUEST_FAILED" : _aidl_v == 2118 ? "DUPLICATE_BEARER_ID" : _aidl_v == 2119 ? "ESM_COLLISION_SCENARIOS" : _aidl_v == 2120 ? "ESM_BEARER_DEACTIVATED_TO_SYNC_WITH_NETWORK" : _aidl_v == 2121 ? "ESM_NW_ACTIVATED_DED_BEARER_WITH_ID_OF_DEF_BEARER" : _aidl_v == 2122 ? "ESM_BAD_OTA_MESSAGE" : _aidl_v == 2123 ? "ESM_DOWNLOAD_SERVER_REJECTED_THE_CALL" : _aidl_v == 2124 ? "ESM_CONTEXT_TRANSFERRED_DUE_TO_IRAT" : _aidl_v == 2125 ? "DS_EXPLICIT_DEACTIVATION" : _aidl_v == 2126 ? "ESM_LOCAL_CAUSE_NONE" : _aidl_v == 2127 ? "LTE_THROTTLING_NOT_REQUIRED" : _aidl_v == 2128 ? "ACCESS_CONTROL_LIST_CHECK_FAILURE" : _aidl_v == 2129 ? "SERVICE_NOT_ALLOWED_ON_PLMN" : _aidl_v == 2130 ? "EMM_T3417_EXPIRED" : _aidl_v == 2131 ? "EMM_T3417_EXT_EXPIRED" : _aidl_v == 2132 ? "RRC_UPLINK_DATA_TRANSMISSION_FAILURE" : _aidl_v == 2133 ? "RRC_UPLINK_DELIVERY_FAILED_DUE_TO_HANDOVER" : _aidl_v == 2134 ? "RRC_UPLINK_CONNECTION_RELEASE" : _aidl_v == 2135 ? "RRC_UPLINK_RADIO_LINK_FAILURE" : _aidl_v == 2136 ? "RRC_UPLINK_ERROR_REQUEST_FROM_NAS" : _aidl_v == 2137 ? "RRC_CONNECTION_ACCESS_STRATUM_FAILURE" : _aidl_v == 2138 ? "RRC_CONNECTION_ANOTHER_PROCEDURE_IN_PROGRESS" : _aidl_v == 2139 ? "RRC_CONNECTION_ACCESS_BARRED" : _aidl_v == 2140 ? "RRC_CONNECTION_CELL_RESELECTION" : _aidl_v == 2141 ? "RRC_CONNECTION_CONFIG_FAILURE" : _aidl_v == 2142 ? "RRC_CONNECTION_TIMER_EXPIRED" : _aidl_v == 2143 ? "RRC_CONNECTION_LINK_FAILURE" : _aidl_v == 2144 ? "RRC_CONNECTION_CELL_NOT_CAMPED" : _aidl_v == 2145 ? "RRC_CONNECTION_SYSTEM_INTERVAL_FAILURE" : _aidl_v == 2146 ? "RRC_CONNECTION_REJECT_BY_NETWORK" : _aidl_v == 2147 ? "RRC_CONNECTION_NORMAL_RELEASE" : _aidl_v == 2148 ? "RRC_CONNECTION_RADIO_LINK_FAILURE" : _aidl_v == 2149 ? "RRC_CONNECTION_REESTABLISHMENT_FAILURE" : _aidl_v == 2150 ? "RRC_CONNECTION_OUT_OF_SERVICE_DURING_CELL_REGISTER" : _aidl_v == 2151 ? "RRC_CONNECTION_ABORT_REQUEST" : _aidl_v == 2152 ? "RRC_CONNECTION_SYSTEM_INFORMATION_BLOCK_READ_ERROR" : _aidl_v == 2153 ? "NETWORK_INITIATED_DETACH_WITH_AUTO_REATTACH" : _aidl_v == 2154 ? "NETWORK_INITIATED_DETACH_NO_AUTO_REATTACH" : _aidl_v == 2155 ? "ESM_PROCEDURE_TIME_OUT" : _aidl_v == 2156 ? "INVALID_CONNECTION_ID" : _aidl_v == 2157 ? "MAXIMIUM_NSAPIS_EXCEEDED" : _aidl_v == 2158 ? "INVALID_PRIMARY_NSAPI" : _aidl_v == 2159 ? "CANNOT_ENCODE_OTA_MESSAGE" : _aidl_v == 2160 ? "RADIO_ACCESS_BEARER_SETUP_FAILURE" : _aidl_v == 2161 ? "PDP_ESTABLISH_TIMEOUT_EXPIRED" : _aidl_v == 2162 ? "PDP_MODIFY_TIMEOUT_EXPIRED" : _aidl_v == 2163 ? "PDP_INACTIVE_TIMEOUT_EXPIRED" : _aidl_v == 2164 ? "PDP_LOWERLAYER_ERROR" : _aidl_v == 2165 ? "PDP_MODIFY_COLLISION" : _aidl_v == 2166 ? "MAXINUM_SIZE_OF_L2_MESSAGE_EXCEEDED" : _aidl_v == 2166 ? "MAXIMUM_SIZE_OF_L2_MESSAGE_EXCEEDED" : _aidl_v == 2167 ? "NAS_REQUEST_REJECTED_BY_NETWORK" : _aidl_v == 2168 ? "RRC_CONNECTION_INVALID_REQUEST" : _aidl_v == 2169 ? "RRC_CONNECTION_TRACKING_AREA_ID_CHANGED" : _aidl_v == 2170 ? "RRC_CONNECTION_RF_UNAVAILABLE" : _aidl_v == 2171 ? "RRC_CONNECTION_ABORTED_DUE_TO_IRAT_CHANGE" : _aidl_v == 2172 ? "RRC_CONNECTION_RELEASED_SECURITY_NOT_ACTIVE" : _aidl_v == 2173 ? "RRC_CONNECTION_ABORTED_AFTER_HANDOVER" : _aidl_v == 2174 ? "RRC_CONNECTION_ABORTED_AFTER_IRAT_CELL_CHANGE" : _aidl_v == 2175 ? "RRC_CONNECTION_ABORTED_DURING_IRAT_CELL_CHANGE" : _aidl_v == 2176 ? "IMSI_UNKNOWN_IN_HOME_SUBSCRIBER_SERVER" : _aidl_v == 2177 ? "IMEI_NOT_ACCEPTED" : _aidl_v == 2178 ? "EPS_SERVICES_AND_NON_EPS_SERVICES_NOT_ALLOWED" : _aidl_v == 2179 ? "EPS_SERVICES_NOT_ALLOWED_IN_PLMN" : _aidl_v == 2180 ? "MSC_TEMPORARILY_NOT_REACHABLE" : _aidl_v == 2181 ? "CS_DOMAIN_NOT_AVAILABLE" : _aidl_v == 2182 ? "ESM_FAILURE" : _aidl_v == 2183 ? "MAC_FAILURE" : _aidl_v == 2184 ? "SYNCHRONIZATION_FAILURE" : _aidl_v == 2185 ? "UE_SECURITY_CAPABILITIES_MISMATCH" : _aidl_v == 2186 ? "SECURITY_MODE_REJECTED" : _aidl_v == 2187 ? "UNACCEPTABLE_NON_EPS_AUTHENTICATION" : _aidl_v == 2188 ? "CS_FALLBACK_CALL_ESTABLISHMENT_NOT_ALLOWED" : _aidl_v == 2189 ? "NO_EPS_BEARER_CONTEXT_ACTIVATED" : _aidl_v == 2190 ? "INVALID_EMM_STATE" : _aidl_v == 2191 ? "NAS_LAYER_FAILURE" : _aidl_v == 2192 ? "MULTIPLE_PDP_CALL_NOT_ALLOWED" : _aidl_v == 2193 ? "EMBMS_NOT_ENABLED" : _aidl_v == 2194 ? "IRAT_HANDOVER_FAILED" : _aidl_v == 2195 ? "EMBMS_REGULAR_DEACTIVATION" : _aidl_v == 2196 ? "TEST_LOOPBACK_REGULAR_DEACTIVATION" : _aidl_v == 2197 ? "LOWER_LAYER_REGISTRATION_FAILURE" : _aidl_v == 2198 ? "DATA_PLAN_EXPIRED" : _aidl_v == 2199 ? "UMTS_HANDOVER_TO_IWLAN" : _aidl_v == 2200 ? "EVDO_CONNECTION_DENY_BY_GENERAL_OR_NETWORK_BUSY" : _aidl_v == 2201 ? "EVDO_CONNECTION_DENY_BY_BILLING_OR_AUTHENTICATION_FAILURE" : _aidl_v == 2202 ? "EVDO_HDR_CHANGED" : _aidl_v == 2203 ? "EVDO_HDR_EXITED" : _aidl_v == 2204 ? "EVDO_HDR_NO_SESSION" : _aidl_v == 2205 ? "EVDO_USING_GPS_FIX_INSTEAD_OF_HDR_CALL" : _aidl_v == 2206 ? "EVDO_HDR_CONNECTION_SETUP_TIMEOUT" : _aidl_v == 2207 ? "FAILED_TO_ACQUIRE_COLOCATED_HDR" : _aidl_v == 2208 ? "OTASP_COMMIT_IN_PROGRESS" : _aidl_v == 2209 ? "NO_HYBRID_HDR_SERVICE" : _aidl_v == 2210 ? "HDR_NO_LOCK_GRANTED" : _aidl_v == 2211 ? "DBM_OR_SMS_IN_PROGRESS" : _aidl_v == 2212 ? "HDR_FADE" : _aidl_v == 2213 ? "HDR_ACCESS_FAILURE" : _aidl_v == 2214 ? "UNSUPPORTED_1X_PREV" : _aidl_v == 2215 ? "LOCAL_END" : _aidl_v == 2216 ? "NO_SERVICE" : _aidl_v == 2217 ? "FADE" : _aidl_v == 2218 ? "NORMAL_RELEASE" : _aidl_v == 2219 ? "ACCESS_ATTEMPT_ALREADY_IN_PROGRESS" : _aidl_v == 2220 ? "REDIRECTION_OR_HANDOFF_IN_PROGRESS" : _aidl_v == 2221 ? "EMERGENCY_MODE" : _aidl_v == 2222 ? "PHONE_IN_USE" : _aidl_v == 2223 ? "INVALID_MODE" : _aidl_v == 2224 ? "INVALID_SIM_STATE" : _aidl_v == 2225 ? "NO_COLLOCATED_HDR" : _aidl_v == 2226 ? "UE_IS_ENTERING_POWERSAVE_MODE" : _aidl_v == 2227 ? "DUAL_SWITCH" : _aidl_v == 2228 ? "PPP_TIMEOUT" : _aidl_v == 2229 ? "PPP_AUTH_FAILURE" : _aidl_v == 2230 ? "PPP_OPTION_MISMATCH" : _aidl_v == 2231 ? "PPP_PAP_FAILURE" : _aidl_v == 2232 ? "PPP_CHAP_FAILURE" : _aidl_v == 2233 ? "PPP_CLOSE_IN_PROGRESS" : _aidl_v == 2234 ? "LIMITED_TO_IPV4" : _aidl_v == 2235 ? "LIMITED_TO_IPV6" : _aidl_v == 2236 ? "VSNCP_TIMEOUT" : _aidl_v == 2237 ? "VSNCP_GEN_ERROR" : _aidl_v == 2238 ? "VSNCP_APN_UNAUTHORIZED" : _aidl_v == 2239 ? "VSNCP_PDN_LIMIT_EXCEEDED" : _aidl_v == 2240 ? "VSNCP_NO_PDN_GATEWAY_ADDRESS" : _aidl_v == 2241 ? "VSNCP_PDN_GATEWAY_UNREACHABLE" : _aidl_v == 2242 ? "VSNCP_PDN_GATEWAY_REJECT" : _aidl_v == 2243 ? "VSNCP_INSUFFICIENT_PARAMETERS" : _aidl_v == 2244 ? "VSNCP_RESOURCE_UNAVAILABLE" : _aidl_v == 2245 ? "VSNCP_ADMINISTRATIVELY_PROHIBITED" : _aidl_v == 2246 ? "VSNCP_PDN_ID_IN_USE" : _aidl_v == 2247 ? "VSNCP_SUBSCRIBER_LIMITATION" : _aidl_v == 2248 ? "VSNCP_PDN_EXISTS_FOR_THIS_APN" : _aidl_v == 2249 ? "VSNCP_RECONNECT_NOT_ALLOWED" : _aidl_v == 2250 ? "IPV6_PREFIX_UNAVAILABLE" : _aidl_v == 2251 ? "HANDOFF_PREFERENCE_CHANGED" : _aidl_v == 2252 ? "SLICE_REJECTED" : _aidl_v == 2253 ? "MATCH_ALL_RULE_NOT_ALLOWED" : _aidl_v == 2254 ? "ALL_MATCHING_RULES_FAILED" : Integer.toString(_aidl_v);
    }

    static String arrayToString(Object _aidl_v) {
        int[] iArr;
        if (_aidl_v == null) {
            return "null";
        }
        Class<?> _aidl_cls = _aidl_v.getClass();
        if (!_aidl_cls.isArray()) {
            throw new IllegalArgumentException("not an array: " + _aidl_v);
        }
        Class<?> comp = _aidl_cls.getComponentType();
        StringJoiner _aidl_sj = new StringJoiner(", ", NavigationBarInflaterView.SIZE_MOD_START, NavigationBarInflaterView.SIZE_MOD_END);
        if (comp.isArray()) {
            for (int _aidl_i = 0; _aidl_i < Array.getLength(_aidl_v); _aidl_i++) {
                _aidl_sj.add(arrayToString(Array.get(_aidl_v, _aidl_i)));
            }
        } else if (_aidl_cls != int[].class) {
            throw new IllegalArgumentException("wrong type: " + _aidl_cls);
        } else {
            for (int e : (int[]) _aidl_v) {
                _aidl_sj.add(toString(e));
            }
        }
        return _aidl_sj.toString();
    }
}