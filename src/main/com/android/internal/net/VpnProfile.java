package com.android.internal.net;

import android.net.Ikev2VpnProfile;
import android.net.ProxyInfo;
import android.net.Uri;
import android.net.ipsec.ike.IkeTunnelConnectionParams;
import android.net.vcn.persistablebundleutils.TunnelConnectionParamsUtils;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.HexDump;
import com.android.net.module.util.ProxyUtils;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class VpnProfile implements Cloneable, Parcelable {
    private static final String ENCODED_NULL_PROXY_INFO = "\u0000\u0000\u0000\u0000";
    static final String LIST_DELIMITER = ",";
    public static final int PROXY_MANUAL = 1;
    public static final int PROXY_NONE = 0;
    private static final String TAG = "VpnProfile";
    public static final int TYPE_IKEV2_FROM_IKE_TUN_CONN_PARAMS = 9;
    public static final int TYPE_IKEV2_IPSEC_PSK = 7;
    public static final int TYPE_IKEV2_IPSEC_RSA = 8;
    public static final int TYPE_IKEV2_IPSEC_USER_PASS = 6;
    public static final int TYPE_IPSEC_HYBRID_RSA = 5;
    public static final int TYPE_IPSEC_XAUTH_PSK = 3;
    public static final int TYPE_IPSEC_XAUTH_RSA = 4;
    public static final int TYPE_L2TP_IPSEC_PSK = 1;
    public static final int TYPE_L2TP_IPSEC_RSA = 2;
    public static final int TYPE_MAX = 9;
    public static final int TYPE_PPTP = 0;
    static final String VALUE_DELIMITER = "\u0000";
    public boolean areAuthParamsInline;
    public final boolean automaticIpVersionSelectionEnabled;
    public final boolean automaticNattKeepaliveTimerEnabled;
    public String dnsServers;
    public final boolean excludeLocalRoutes;
    public final IkeTunnelConnectionParams ikeTunConnParams;
    public String ipsecCaCert;
    public String ipsecIdentifier;
    public String ipsecSecret;
    public String ipsecServerCert;
    public String ipsecUserCert;
    public boolean isBypassable;
    public boolean isMetered;
    public final boolean isRestrictedToTestNetworks;
    public final String key;
    public String l2tpSecret;
    private List<String> mAllowedAlgorithms;
    public int maxMtu;
    public boolean mppe;
    public String name;
    public String password;
    public ProxyInfo proxy;
    public final boolean requiresInternetValidation;
    public String routes;
    public transient boolean saveLogin;
    public String searchDomains;
    public String server;
    public int type;
    public String username;
    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public static final Parcelable.Creator<VpnProfile> CREATOR = new Parcelable.Creator<VpnProfile>() { // from class: com.android.internal.net.VpnProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VpnProfile createFromParcel(Parcel in) {
            return new VpnProfile(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VpnProfile[] newArray(int size) {
            return new VpnProfile[size];
        }
    };

    public VpnProfile(String key) {
        this(key, false, false, false, null);
    }

    public VpnProfile(String key, boolean isRestrictedToTestNetworks) {
        this(key, isRestrictedToTestNetworks, false, false, null);
    }

    public VpnProfile(String key, boolean isRestrictedToTestNetworks, boolean excludeLocalRoutes, boolean requiresInternetValidation, IkeTunnelConnectionParams ikeTunConnParams) {
        this(key, isRestrictedToTestNetworks, excludeLocalRoutes, requiresInternetValidation, ikeTunConnParams, false, false);
    }

    public VpnProfile(String key, boolean isRestrictedToTestNetworks, boolean excludeLocalRoutes, boolean requiresInternetValidation, IkeTunnelConnectionParams ikeTunConnParams, boolean automaticNattKeepaliveTimerEnabled, boolean automaticIpVersionSelectionEnabled) {
        this.name = "";
        this.type = 0;
        this.server = "";
        this.username = "";
        this.password = "";
        this.dnsServers = "";
        this.searchDomains = "";
        this.routes = "";
        this.mppe = true;
        this.l2tpSecret = "";
        this.ipsecIdentifier = "";
        this.ipsecSecret = "";
        this.ipsecUserCert = "";
        this.ipsecCaCert = "";
        this.ipsecServerCert = "";
        this.proxy = null;
        this.mAllowedAlgorithms = new ArrayList();
        this.isBypassable = false;
        this.isMetered = false;
        this.maxMtu = 1360;
        this.areAuthParamsInline = false;
        this.saveLogin = false;
        this.key = key;
        this.isRestrictedToTestNetworks = isRestrictedToTestNetworks;
        this.excludeLocalRoutes = excludeLocalRoutes;
        this.requiresInternetValidation = requiresInternetValidation;
        this.ikeTunConnParams = ikeTunConnParams;
        this.automaticNattKeepaliveTimerEnabled = automaticNattKeepaliveTimerEnabled;
        this.automaticIpVersionSelectionEnabled = automaticIpVersionSelectionEnabled;
    }

    public VpnProfile(Parcel in) {
        boolean z;
        this.name = "";
        this.type = 0;
        this.server = "";
        this.username = "";
        this.password = "";
        this.dnsServers = "";
        this.searchDomains = "";
        this.routes = "";
        this.mppe = true;
        this.l2tpSecret = "";
        this.ipsecIdentifier = "";
        this.ipsecSecret = "";
        this.ipsecUserCert = "";
        this.ipsecCaCert = "";
        this.ipsecServerCert = "";
        this.proxy = null;
        this.mAllowedAlgorithms = new ArrayList();
        this.isBypassable = false;
        this.isMetered = false;
        this.maxMtu = 1360;
        this.areAuthParamsInline = false;
        this.saveLogin = false;
        this.key = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.server = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.dnsServers = in.readString();
        this.searchDomains = in.readString();
        this.routes = in.readString();
        if (in.readInt() == 0) {
            z = false;
        } else {
            z = true;
        }
        this.mppe = z;
        this.l2tpSecret = in.readString();
        this.ipsecIdentifier = in.readString();
        this.ipsecSecret = in.readString();
        this.ipsecUserCert = in.readString();
        this.ipsecCaCert = in.readString();
        this.ipsecServerCert = in.readString();
        this.saveLogin = in.readInt() != 0;
        this.proxy = (ProxyInfo) in.readParcelable(null, ProxyInfo.class);
        ArrayList arrayList = new ArrayList();
        this.mAllowedAlgorithms = arrayList;
        in.readList(arrayList, null, String.class);
        this.isBypassable = in.readBoolean();
        this.isMetered = in.readBoolean();
        this.maxMtu = in.readInt();
        this.areAuthParamsInline = in.readBoolean();
        this.isRestrictedToTestNetworks = in.readBoolean();
        this.excludeLocalRoutes = in.readBoolean();
        this.requiresInternetValidation = in.readBoolean();
        PersistableBundle bundle = (PersistableBundle) in.readParcelable(PersistableBundle.class.getClassLoader(), PersistableBundle.class);
        this.ikeTunConnParams = bundle != null ? TunnelConnectionParamsUtils.fromPersistableBundle(bundle) : null;
        this.automaticNattKeepaliveTimerEnabled = in.readBoolean();
        this.automaticIpVersionSelectionEnabled = in.readBoolean();
    }

    public List<String> getAllowedAlgorithms() {
        return Collections.unmodifiableList(this.mAllowedAlgorithms);
    }

    public void setAllowedAlgorithms(List<String> allowedAlgorithms) {
        this.mAllowedAlgorithms = allowedAlgorithms;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.key);
        out.writeString(this.name);
        out.writeInt(this.type);
        out.writeString(this.server);
        out.writeString(this.username);
        out.writeString(this.password);
        out.writeString(this.dnsServers);
        out.writeString(this.searchDomains);
        out.writeString(this.routes);
        out.writeInt(this.mppe ? 1 : 0);
        out.writeString(this.l2tpSecret);
        out.writeString(this.ipsecIdentifier);
        out.writeString(this.ipsecSecret);
        out.writeString(this.ipsecUserCert);
        out.writeString(this.ipsecCaCert);
        out.writeString(this.ipsecServerCert);
        out.writeInt(this.saveLogin ? 1 : 0);
        out.writeParcelable(this.proxy, flags);
        out.writeList(this.mAllowedAlgorithms);
        out.writeBoolean(this.isBypassable);
        out.writeBoolean(this.isMetered);
        out.writeInt(this.maxMtu);
        out.writeBoolean(this.areAuthParamsInline);
        out.writeBoolean(this.isRestrictedToTestNetworks);
        out.writeBoolean(this.excludeLocalRoutes);
        out.writeBoolean(this.requiresInternetValidation);
        IkeTunnelConnectionParams ikeTunnelConnectionParams = this.ikeTunConnParams;
        out.writeParcelable(ikeTunnelConnectionParams == null ? null : TunnelConnectionParamsUtils.toPersistableBundle(ikeTunnelConnectionParams), flags);
        out.writeBoolean(this.automaticNattKeepaliveTimerEnabled);
        out.writeBoolean(this.automaticIpVersionSelectionEnabled);
    }

    public static VpnProfile decode(String key, byte[] value) {
        boolean isRestrictedToTestNetworks;
        boolean excludeLocalRoutes;
        boolean requiresInternetValidation;
        IkeTunnelConnectionParams tempIkeTunConnParams;
        boolean automaticNattKeepaliveTimerEnabled;
        boolean automaticIpVersionSelectionEnabled;
        if (key == null) {
            return null;
        }
        try {
            try {
                String[] values = new String(value, StandardCharsets.UTF_8).split(VALUE_DELIMITER, -1);
                if (values.length >= 14 && ((values.length <= 19 || values.length >= 24) && ((values.length <= 28 || values.length >= 30) && values.length <= 30))) {
                    if (values.length >= 25) {
                        isRestrictedToTestNetworks = Boolean.parseBoolean(values[24]);
                    } else {
                        isRestrictedToTestNetworks = false;
                    }
                    if (values.length >= 26) {
                        excludeLocalRoutes = Boolean.parseBoolean(values[25]);
                    } else {
                        excludeLocalRoutes = false;
                    }
                    if (values.length >= 27) {
                        requiresInternetValidation = Boolean.parseBoolean(values[26]);
                    } else {
                        requiresInternetValidation = false;
                    }
                    if (values.length >= 28 && values[27].length() != 0) {
                        Parcel parcel = Parcel.obtain();
                        byte[] bytes = HexDump.hexStringToByteArray(values[27]);
                        parcel.unmarshall(bytes, 0, bytes.length);
                        parcel.setDataPosition(0);
                        PersistableBundle bundle = (PersistableBundle) parcel.readValue(PersistableBundle.class.getClassLoader());
                        IkeTunnelConnectionParams tempIkeTunConnParams2 = TunnelConnectionParamsUtils.fromPersistableBundle(bundle);
                        tempIkeTunConnParams = tempIkeTunConnParams2;
                    } else {
                        tempIkeTunConnParams = null;
                    }
                    if (values.length >= 30) {
                        boolean automaticNattKeepaliveTimerEnabled2 = Boolean.parseBoolean(values[28]);
                        automaticNattKeepaliveTimerEnabled = automaticNattKeepaliveTimerEnabled2;
                        automaticIpVersionSelectionEnabled = Boolean.parseBoolean(values[29]);
                    } else {
                        automaticNattKeepaliveTimerEnabled = false;
                        automaticIpVersionSelectionEnabled = false;
                    }
                    VpnProfile profile = new VpnProfile(key, isRestrictedToTestNetworks, excludeLocalRoutes, requiresInternetValidation, tempIkeTunConnParams, automaticNattKeepaliveTimerEnabled, automaticIpVersionSelectionEnabled);
                    profile.name = values[0];
                    boolean z = true;
                    int parseInt = Integer.parseInt(values[1]);
                    profile.type = parseInt;
                    if (parseInt >= 0 && parseInt <= 9) {
                        profile.server = values[2];
                        profile.username = values[3];
                        profile.password = values[4];
                        profile.dnsServers = values[5];
                        profile.searchDomains = values[6];
                        profile.routes = values[7];
                        profile.mppe = Boolean.parseBoolean(values[8]);
                        profile.l2tpSecret = values[9];
                        profile.ipsecIdentifier = values[10];
                        profile.ipsecSecret = values[11];
                        profile.ipsecUserCert = values[12];
                        profile.ipsecCaCert = values[13];
                        profile.ipsecServerCert = values.length > 14 ? values[14] : "";
                        if (values.length > 15) {
                            String host = values.length > 15 ? values[15] : "";
                            String port = values.length > 16 ? values[16] : "";
                            String exclList = values.length > 17 ? values[17] : "";
                            String pacFileUrl = values.length > 18 ? values[18] : "";
                            if (host.isEmpty() && port.isEmpty() && exclList.isEmpty()) {
                                if (!pacFileUrl.isEmpty()) {
                                    profile.proxy = ProxyInfo.buildPacProxy(Uri.parse(pacFileUrl));
                                }
                            }
                            profile.proxy = ProxyInfo.buildDirectProxy(host, port.isEmpty() ? 0 : Integer.parseInt(port), ProxyUtils.exclusionStringAsList(exclList));
                        }
                        if (values.length >= 24) {
                            profile.mAllowedAlgorithms = new ArrayList();
                            for (String algo : Arrays.asList(values[19].split(","))) {
                                profile.mAllowedAlgorithms.add(URLDecoder.decode(algo, DEFAULT_ENCODING));
                            }
                            profile.isBypassable = Boolean.parseBoolean(values[20]);
                            profile.isMetered = Boolean.parseBoolean(values[21]);
                            profile.maxMtu = Integer.parseInt(values[22]);
                            profile.areAuthParamsInline = Boolean.parseBoolean(values[23]);
                        }
                        if (profile.username.isEmpty() && profile.password.isEmpty()) {
                            z = false;
                        }
                        profile.saveLogin = z;
                        return profile;
                    }
                    return null;
                }
                return null;
            } catch (Exception e) {
                e = e;
                Log.m111d(TAG, "Got exception in decode.", e);
                return null;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public byte[] encode() {
        String str;
        StringBuilder builder = new StringBuilder(this.name);
        builder.append(VALUE_DELIMITER).append(this.type);
        builder.append(VALUE_DELIMITER).append(this.server);
        builder.append(VALUE_DELIMITER).append(this.saveLogin ? this.username : "");
        builder.append(VALUE_DELIMITER).append(this.saveLogin ? this.password : "");
        builder.append(VALUE_DELIMITER).append(this.dnsServers);
        builder.append(VALUE_DELIMITER).append(this.searchDomains);
        builder.append(VALUE_DELIMITER).append(this.routes);
        builder.append(VALUE_DELIMITER).append(this.mppe);
        builder.append(VALUE_DELIMITER).append(this.l2tpSecret);
        builder.append(VALUE_DELIMITER).append(this.ipsecIdentifier);
        builder.append(VALUE_DELIMITER).append(this.ipsecSecret);
        builder.append(VALUE_DELIMITER).append(this.ipsecUserCert);
        builder.append(VALUE_DELIMITER).append(this.ipsecCaCert);
        builder.append(VALUE_DELIMITER).append(this.ipsecServerCert);
        if (this.proxy != null) {
            builder.append(VALUE_DELIMITER).append(this.proxy.getHost() != null ? this.proxy.getHost() : "");
            builder.append(VALUE_DELIMITER).append(this.proxy.getPort());
            StringBuilder append = builder.append(VALUE_DELIMITER);
            if (ProxyUtils.exclusionListAsString(this.proxy.getExclusionList()) != null) {
                str = ProxyUtils.exclusionListAsString(this.proxy.getExclusionList());
            } else {
                str = "";
            }
            append.append(str);
            builder.append(VALUE_DELIMITER).append(this.proxy.getPacFileUrl().toString());
        } else {
            builder.append(ENCODED_NULL_PROXY_INFO);
        }
        List<String> encodedAlgoNames = new ArrayList<>();
        try {
            for (String algo : this.mAllowedAlgorithms) {
                encodedAlgoNames.add(URLEncoder.encode(algo, DEFAULT_ENCODING));
            }
            builder.append(VALUE_DELIMITER).append(String.join(",", encodedAlgoNames));
            builder.append(VALUE_DELIMITER).append(this.isBypassable);
            builder.append(VALUE_DELIMITER).append(this.isMetered);
            builder.append(VALUE_DELIMITER).append(this.maxMtu);
            builder.append(VALUE_DELIMITER).append(this.areAuthParamsInline);
            builder.append(VALUE_DELIMITER).append(this.isRestrictedToTestNetworks);
            builder.append(VALUE_DELIMITER).append(this.excludeLocalRoutes);
            builder.append(VALUE_DELIMITER).append(this.requiresInternetValidation);
            IkeTunnelConnectionParams ikeTunnelConnectionParams = this.ikeTunConnParams;
            if (ikeTunnelConnectionParams != null) {
                PersistableBundle bundle = TunnelConnectionParamsUtils.toPersistableBundle(ikeTunnelConnectionParams);
                Parcel parcel = Parcel.obtain();
                parcel.writeValue(bundle);
                byte[] bytes = parcel.marshall();
                builder.append(VALUE_DELIMITER).append(HexDump.toHexString(bytes));
            } else {
                builder.append(VALUE_DELIMITER).append("");
            }
            builder.append(VALUE_DELIMITER).append(this.automaticNattKeepaliveTimerEnabled);
            builder.append(VALUE_DELIMITER).append(this.automaticIpVersionSelectionEnabled);
            return builder.toString().getBytes(StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Failed to encode algorithms.", e);
        }
    }

    public static boolean isLegacyType(int type) {
        switch (type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                return false;
        }
    }

    private boolean isValidLockdownLegacyVpnProfile() {
        return isLegacyType(this.type) && isServerAddressNumeric() && hasDns() && areDnsAddressesNumeric();
    }

    private boolean isValidLockdownPlatformVpnProfile() {
        return Ikev2VpnProfile.isValidVpnProfile(this);
    }

    public boolean isValidLockdownProfile() {
        return isTypeValidForLockdown() && (isValidLockdownLegacyVpnProfile() || isValidLockdownPlatformVpnProfile());
    }

    public boolean isTypeValidForLockdown() {
        return this.type != 0;
    }

    public boolean isServerAddressNumeric() {
        try {
            InetAddress.parseNumericAddress(this.server);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean hasDns() {
        return !TextUtils.isEmpty(this.dnsServers);
    }

    public boolean areDnsAddressesNumeric() {
        String[] split;
        try {
            for (String dnsServer : this.dnsServers.split(" +")) {
                InetAddress.parseNumericAddress(dnsServer);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.key, Integer.valueOf(this.type), this.server, this.username, this.password, this.dnsServers, this.searchDomains, this.routes, Boolean.valueOf(this.mppe), this.l2tpSecret, this.ipsecIdentifier, this.ipsecSecret, this.ipsecUserCert, this.ipsecCaCert, this.ipsecServerCert, this.proxy, this.mAllowedAlgorithms, Boolean.valueOf(this.isBypassable), Boolean.valueOf(this.isMetered), Integer.valueOf(this.maxMtu), Boolean.valueOf(this.areAuthParamsInline), Boolean.valueOf(this.isRestrictedToTestNetworks), Boolean.valueOf(this.excludeLocalRoutes), Boolean.valueOf(this.requiresInternetValidation), this.ikeTunConnParams, Boolean.valueOf(this.automaticNattKeepaliveTimerEnabled), Boolean.valueOf(this.automaticIpVersionSelectionEnabled));
    }

    public boolean equals(Object obj) {
        if (obj instanceof VpnProfile) {
            VpnProfile other = (VpnProfile) obj;
            return Objects.equals(this.key, other.key) && Objects.equals(this.name, other.name) && this.type == other.type && Objects.equals(this.server, other.server) && Objects.equals(this.username, other.username) && Objects.equals(this.password, other.password) && Objects.equals(this.dnsServers, other.dnsServers) && Objects.equals(this.searchDomains, other.searchDomains) && Objects.equals(this.routes, other.routes) && this.mppe == other.mppe && Objects.equals(this.l2tpSecret, other.l2tpSecret) && Objects.equals(this.ipsecIdentifier, other.ipsecIdentifier) && Objects.equals(this.ipsecSecret, other.ipsecSecret) && Objects.equals(this.ipsecUserCert, other.ipsecUserCert) && Objects.equals(this.ipsecCaCert, other.ipsecCaCert) && Objects.equals(this.ipsecServerCert, other.ipsecServerCert) && Objects.equals(this.proxy, other.proxy) && Objects.equals(this.mAllowedAlgorithms, other.mAllowedAlgorithms) && this.isBypassable == other.isBypassable && this.isMetered == other.isMetered && this.maxMtu == other.maxMtu && this.areAuthParamsInline == other.areAuthParamsInline && this.isRestrictedToTestNetworks == other.isRestrictedToTestNetworks && this.excludeLocalRoutes == other.excludeLocalRoutes && this.requiresInternetValidation == other.requiresInternetValidation && Objects.equals(this.ikeTunConnParams, other.ikeTunConnParams) && this.automaticNattKeepaliveTimerEnabled == other.automaticNattKeepaliveTimerEnabled && this.automaticIpVersionSelectionEnabled == other.automaticIpVersionSelectionEnabled;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
