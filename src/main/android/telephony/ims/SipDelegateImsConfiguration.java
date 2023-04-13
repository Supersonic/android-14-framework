package android.telephony.ims;

import android.annotation.SystemApi;
import android.net.InetAddresses;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.telephony.ims.SipDelegateConfiguration;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetSocketAddress;
@SystemApi
@Deprecated
/* loaded from: classes3.dex */
public final class SipDelegateImsConfiguration implements Parcelable {
    public static final Parcelable.Creator<SipDelegateImsConfiguration> CREATOR = new Parcelable.Creator<SipDelegateImsConfiguration>() { // from class: android.telephony.ims.SipDelegateImsConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDelegateImsConfiguration createFromParcel(Parcel source) {
            return new SipDelegateImsConfiguration(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDelegateImsConfiguration[] newArray(int size) {
            return new SipDelegateImsConfiguration[size];
        }
    };
    public static final String IPTYPE_IPV4 = "IPV4";
    public static final String IPTYPE_IPV6 = "IPV6";
    public static final String KEY_SIP_CONFIG_AUTHENTICATION_HEADER_STRING = "sip_config_auhentication_header_string";
    public static final String KEY_SIP_CONFIG_AUTHENTICATION_NONCE_STRING = "sip_config_authentication_nonce_string";
    public static final String KEY_SIP_CONFIG_CELLULAR_NETWORK_INFO_HEADER_STRING = "sip_config_cellular_network_info_header_string";
    public static final String KEY_SIP_CONFIG_HOME_DOMAIN_STRING = "sip_config_home_domain_string";
    public static final String KEY_SIP_CONFIG_IMEI_STRING = "sip_config_imei_string";
    public static final String KEY_SIP_CONFIG_IPTYPE_STRING = "sip_config_iptype_string";
    public static final String KEY_SIP_CONFIG_IS_COMPACT_FORM_ENABLED_BOOL = "sip_config_is_compact_form_enabled_bool";
    public static final String KEY_SIP_CONFIG_IS_GRUU_ENABLED_BOOL = "sip_config_is_gruu_enabled_bool";
    public static final String KEY_SIP_CONFIG_IS_IPSEC_ENABLED_BOOL = "sip_config_is_ipsec_enabled_bool";
    public static final String KEY_SIP_CONFIG_IS_KEEPALIVE_ENABLED_BOOL = "sip_config_is_keepalive_enabled_bool";
    public static final String KEY_SIP_CONFIG_IS_NAT_ENABLED_BOOL = "sip_config_is_nat_enabled_bool";
    public static final String KEY_SIP_CONFIG_MAX_PAYLOAD_SIZE_ON_UDP_INT = "sip_config_udp_max_payload_size_int";
    public static final String KEY_SIP_CONFIG_PATH_HEADER_STRING = "sip_config_path_header_string";
    public static final String KEY_SIP_CONFIG_P_ACCESS_NETWORK_INFO_HEADER_STRING = "sip_config_p_access_network_info_header_string";
    public static final String KEY_SIP_CONFIG_P_ASSOCIATED_URI_HEADER_STRING = "sip_config_p_associated_uri_header_string";
    public static final String KEY_SIP_CONFIG_P_LAST_ACCESS_NETWORK_INFO_HEADER_STRING = "sip_config_p_last_access_network_info_header_string";
    public static final String KEY_SIP_CONFIG_SECURITY_VERIFY_HEADER_STRING = "sip_config_security_verify_header_string";
    public static final String KEY_SIP_CONFIG_SERVER_DEFAULT_IPADDRESS_STRING = "sip_config_server_default_ipaddress_string";
    public static final String KEY_SIP_CONFIG_SERVER_DEFAULT_PORT_INT = "sip_config_server_default_port_int";
    public static final String KEY_SIP_CONFIG_SERVER_IPSEC_CLIENT_PORT_INT = "sip_config_server_ipsec_client_port_int";
    public static final String KEY_SIP_CONFIG_SERVER_IPSEC_OLD_CLIENT_PORT_INT = "sip_config_server_ipsec_old_client_port_int";
    public static final String KEY_SIP_CONFIG_SERVER_IPSEC_SERVER_PORT_INT = "sip_config_server_ipsec_server_port_int";
    public static final String KEY_SIP_CONFIG_SERVICE_ROUTE_HEADER_STRING = "sip_config_service_route_header_string";
    public static final String KEY_SIP_CONFIG_TRANSPORT_TYPE_STRING = "sip_config_protocol_type_string";
    public static final String KEY_SIP_CONFIG_UE_DEFAULT_IPADDRESS_STRING = "sip_config_ue_default_ipaddress_string";
    public static final String KEY_SIP_CONFIG_UE_DEFAULT_PORT_INT = "sip_config_ue_default_port_int";
    public static final String KEY_SIP_CONFIG_UE_IPSEC_CLIENT_PORT_INT = "sip_config_ue_ipsec_client_port_int";
    public static final String KEY_SIP_CONFIG_UE_IPSEC_OLD_CLIENT_PORT_INT = "sip_config_ue_ipsec_old_client_port_int";
    public static final String KEY_SIP_CONFIG_UE_IPSEC_SERVER_PORT_INT = "sip_config_ue_ipsec_server_port_int";
    public static final String KEY_SIP_CONFIG_UE_PRIVATE_USER_ID_STRING = "sip_config_ue_private_user_id_string";
    public static final String KEY_SIP_CONFIG_UE_PUBLIC_GRUU_STRING = "sip_config_ue_public_gruu_string";
    public static final String KEY_SIP_CONFIG_UE_PUBLIC_IPADDRESS_WITH_NAT_STRING = "sip_config_ue_public_ipaddress_with_nat_string";
    public static final String KEY_SIP_CONFIG_UE_PUBLIC_PORT_WITH_NAT_INT = "sip_config_ue_public_port_with_nat_int";
    public static final String KEY_SIP_CONFIG_UE_PUBLIC_USER_ID_STRING = "sip_config_ue_public_user_id_string";
    public static final String KEY_SIP_CONFIG_URI_USER_PART_STRING = "sip_config_uri_user_part_string";
    public static final String KEY_SIP_CONFIG_USER_AGENT_HEADER_STRING = "sip_config_sip_user_agent_header_string";
    public static final String SIP_TRANSPORT_TCP = "TCP";
    public static final String SIP_TRANSPORT_UDP = "UDP";
    private final PersistableBundle mBundle;
    private final long mVersion;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface BooleanConfigKey {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface IntConfigKey {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface StringConfigKey {
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final PersistableBundle mBundle;
        private final long mVersion;

        public Builder(int version) {
            this.mVersion = version;
            this.mBundle = new PersistableBundle();
        }

        public Builder(SipDelegateImsConfiguration config) {
            this.mVersion = config.getVersion() + 1;
            this.mBundle = config.copyBundle();
        }

        public Builder addString(String key, String value) {
            this.mBundle.putString(key, value);
            return this;
        }

        public Builder addInt(String key, int value) {
            this.mBundle.putInt(key, value);
            return this;
        }

        public Builder addBoolean(String key, boolean value) {
            this.mBundle.putBoolean(key, value);
            return this;
        }

        public SipDelegateImsConfiguration build() {
            return new SipDelegateImsConfiguration(this.mVersion, this.mBundle);
        }
    }

    private SipDelegateImsConfiguration(long version, PersistableBundle bundle) {
        this.mVersion = version;
        this.mBundle = bundle;
    }

    private SipDelegateImsConfiguration(Parcel source) {
        this.mVersion = source.readLong();
        this.mBundle = source.readPersistableBundle();
    }

    public boolean containsKey(String key) {
        return this.mBundle.containsKey(key);
    }

    public String getString(String key) {
        return this.mBundle.getString(key);
    }

    public int getInt(String key, int defaultValue) {
        if (!this.mBundle.containsKey(key)) {
            return defaultValue;
        }
        return this.mBundle.getInt(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (!this.mBundle.containsKey(key)) {
            return defaultValue;
        }
        return this.mBundle.getBoolean(key);
    }

    public PersistableBundle copyBundle() {
        return new PersistableBundle(this.mBundle);
    }

    public long getVersion() {
        return this.mVersion;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mVersion);
        dest.writePersistableBundle(this.mBundle);
    }

    public SipDelegateConfiguration toNewConfig() {
        int transportType;
        String transportTypeString = getString(KEY_SIP_CONFIG_TRANSPORT_TYPE_STRING);
        if (transportTypeString != null && transportTypeString.equals(SIP_TRANSPORT_UDP)) {
            transportType = 0;
        } else {
            transportType = 1;
        }
        SipDelegateConfiguration.Builder builder = new SipDelegateConfiguration.Builder(this.mVersion, transportType, getSocketAddr(getString(KEY_SIP_CONFIG_UE_DEFAULT_IPADDRESS_STRING), getInt(KEY_SIP_CONFIG_UE_DEFAULT_PORT_INT, -1)), getSocketAddr(getString(KEY_SIP_CONFIG_SERVER_DEFAULT_IPADDRESS_STRING), getInt(KEY_SIP_CONFIG_SERVER_DEFAULT_PORT_INT, -1)));
        builder.setSipCompactFormEnabled(getBoolean(KEY_SIP_CONFIG_IS_COMPACT_FORM_ENABLED_BOOL, false));
        builder.setSipKeepaliveEnabled(getBoolean(KEY_SIP_CONFIG_IS_KEEPALIVE_ENABLED_BOOL, false));
        builder.setMaxUdpPayloadSizeBytes(getInt(KEY_SIP_CONFIG_MAX_PAYLOAD_SIZE_ON_UDP_INT, -1));
        builder.setPublicUserIdentifier(getString(KEY_SIP_CONFIG_UE_PUBLIC_USER_ID_STRING));
        builder.setPrivateUserIdentifier(getString(KEY_SIP_CONFIG_UE_PRIVATE_USER_ID_STRING));
        builder.setHomeDomain(getString(KEY_SIP_CONFIG_HOME_DOMAIN_STRING));
        builder.setImei(getString(KEY_SIP_CONFIG_IMEI_STRING));
        builder.setSipAuthenticationHeader(getString(KEY_SIP_CONFIG_AUTHENTICATION_HEADER_STRING));
        builder.setSipAuthenticationNonce(getString(KEY_SIP_CONFIG_AUTHENTICATION_NONCE_STRING));
        builder.setSipServiceRouteHeader(getString(KEY_SIP_CONFIG_SERVICE_ROUTE_HEADER_STRING));
        builder.setSipPathHeader(getString(KEY_SIP_CONFIG_PATH_HEADER_STRING));
        builder.setSipUserAgentHeader(getString(KEY_SIP_CONFIG_USER_AGENT_HEADER_STRING));
        builder.setSipContactUserParameter(getString(KEY_SIP_CONFIG_URI_USER_PART_STRING));
        builder.setSipPaniHeader(getString(KEY_SIP_CONFIG_P_ACCESS_NETWORK_INFO_HEADER_STRING));
        builder.setSipPlaniHeader(getString(KEY_SIP_CONFIG_P_LAST_ACCESS_NETWORK_INFO_HEADER_STRING));
        builder.setSipCniHeader(getString(KEY_SIP_CONFIG_CELLULAR_NETWORK_INFO_HEADER_STRING));
        builder.setSipAssociatedUriHeader(getString(KEY_SIP_CONFIG_P_ASSOCIATED_URI_HEADER_STRING));
        if (getBoolean(KEY_SIP_CONFIG_IS_GRUU_ENABLED_BOOL, false)) {
            String uri = getString(KEY_SIP_CONFIG_UE_PUBLIC_GRUU_STRING);
            Uri gruuUri = null;
            if (!TextUtils.isEmpty(uri)) {
                gruuUri = Uri.parse(uri);
            }
            builder.setPublicGruuUri(gruuUri);
        }
        if (getBoolean(KEY_SIP_CONFIG_IS_IPSEC_ENABLED_BOOL, false)) {
            builder.setIpSecConfiguration(new SipDelegateConfiguration.IpSecConfiguration(getInt(KEY_SIP_CONFIG_UE_IPSEC_CLIENT_PORT_INT, -1), getInt(KEY_SIP_CONFIG_UE_IPSEC_SERVER_PORT_INT, -1), getInt(KEY_SIP_CONFIG_UE_IPSEC_OLD_CLIENT_PORT_INT, -1), getInt(KEY_SIP_CONFIG_SERVER_IPSEC_CLIENT_PORT_INT, -1), getInt(KEY_SIP_CONFIG_SERVER_IPSEC_SERVER_PORT_INT, -1), getInt(KEY_SIP_CONFIG_SERVER_IPSEC_OLD_CLIENT_PORT_INT, -1), getString(KEY_SIP_CONFIG_SECURITY_VERIFY_HEADER_STRING)));
        }
        if (getBoolean(KEY_SIP_CONFIG_IS_NAT_ENABLED_BOOL, false)) {
            builder.setNatSocketAddress(getSocketAddr(getString(KEY_SIP_CONFIG_UE_PUBLIC_IPADDRESS_WITH_NAT_STRING), getInt(KEY_SIP_CONFIG_UE_PUBLIC_PORT_WITH_NAT_INT, -1)));
        }
        return builder.build();
    }

    private InetSocketAddress getSocketAddr(String ipAddr, int port) {
        return new InetSocketAddress(InetAddresses.parseNumericAddress(ipAddr), port);
    }
}
