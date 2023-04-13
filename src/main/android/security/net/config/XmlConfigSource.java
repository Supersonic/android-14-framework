package android.security.net.config;

import android.app.slice.SliceProvider;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.security.net.config.NetworkSecurityConfig;
import android.util.ArraySet;
import android.util.Base64;
import android.util.Pair;
import com.android.internal.org.bouncycastle.cms.CMSAttributeTableGenerator;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes3.dex */
public class XmlConfigSource implements ConfigSource {
    private static final int CONFIG_BASE = 0;
    private static final int CONFIG_DEBUG = 2;
    private static final int CONFIG_DOMAIN = 1;
    private final ApplicationInfo mApplicationInfo;
    private Context mContext;
    private final boolean mDebugBuild;
    private NetworkSecurityConfig mDefaultConfig;
    private Set<Pair<Domain, NetworkSecurityConfig>> mDomainMap;
    private boolean mInitialized;
    private final Object mLock = new Object();
    private final int mResourceId;

    public XmlConfigSource(Context context, int resourceId, ApplicationInfo info) {
        this.mContext = context;
        this.mResourceId = resourceId;
        ApplicationInfo applicationInfo = new ApplicationInfo(info);
        this.mApplicationInfo = applicationInfo;
        this.mDebugBuild = (applicationInfo.flags & 2) != 0;
    }

    @Override // android.security.net.config.ConfigSource
    public Set<Pair<Domain, NetworkSecurityConfig>> getPerDomainConfigs() {
        ensureInitialized();
        return this.mDomainMap;
    }

    @Override // android.security.net.config.ConfigSource
    public NetworkSecurityConfig getDefaultConfig() {
        ensureInitialized();
        return this.mDefaultConfig;
    }

    private static final String getConfigString(int configType) {
        switch (configType) {
            case 0:
                return "base-config";
            case 1:
                return "domain-config";
            case 2:
                return "debug-overrides";
            default:
                throw new IllegalArgumentException("Unknown config type: " + configType);
        }
    }

    private void ensureInitialized() {
        synchronized (this.mLock) {
            if (this.mInitialized) {
                return;
            }
            try {
                XmlResourceParser parser = this.mContext.getResources().getXml(this.mResourceId);
                try {
                    parseNetworkSecurityConfig(parser);
                    this.mContext = null;
                    this.mInitialized = true;
                    if (parser != null) {
                        parser.close();
                    }
                } catch (Throwable th) {
                    if (parser != null) {
                        try {
                            parser.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Resources.NotFoundException | ParserException | IOException | XmlPullParserException e) {
                throw new RuntimeException("Failed to parse XML configuration from " + this.mContext.getResources().getResourceEntryName(this.mResourceId), e);
            }
        }
    }

    private Pin parsePin(XmlResourceParser parser) throws IOException, XmlPullParserException, ParserException {
        String digestAlgorithm = parser.getAttributeValue(null, CMSAttributeTableGenerator.DIGEST);
        if (!Pin.isSupportedDigestAlgorithm(digestAlgorithm)) {
            throw new ParserException(parser, "Unsupported pin digest algorithm: " + digestAlgorithm);
        }
        if (parser.next() != 4) {
            throw new ParserException(parser, "Missing pin digest");
        }
        String digest = parser.getText().trim();
        try {
            byte[] decodedDigest = Base64.decode(digest, 0);
            int expectedLength = Pin.getDigestLength(digestAlgorithm);
            if (decodedDigest.length != expectedLength) {
                throw new ParserException(parser, "digest length " + decodedDigest.length + " does not match expected length for " + digestAlgorithm + " of " + expectedLength);
            }
            if (parser.next() != 3) {
                throw new ParserException(parser, "pin contains additional elements");
            }
            return new Pin(digestAlgorithm, decodedDigest);
        } catch (IllegalArgumentException e) {
            throw new ParserException(parser, "Invalid pin digest", e);
        }
    }

    private PinSet parsePinSet(XmlResourceParser parser) throws IOException, XmlPullParserException, ParserException {
        String expirationDate = parser.getAttributeValue(null, "expiration");
        long expirationTimestampMilis = Long.MAX_VALUE;
        if (expirationDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                Date date = sdf.parse(expirationDate);
                if (date == null) {
                    throw new ParserException(parser, "Invalid expiration date in pin-set");
                }
                expirationTimestampMilis = date.getTime();
            } catch (ParseException e) {
                throw new ParserException(parser, "Invalid expiration date in pin-set", e);
            }
        }
        int outerDepth = parser.getDepth();
        Set<Pin> pins = new ArraySet<>();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            String tagName = parser.getName();
            if (tagName.equals(SliceProvider.METHOD_PIN)) {
                pins.add(parsePin(parser));
            } else {
                XmlUtils.skipCurrentTag(parser);
            }
        }
        return new PinSet(pins, expirationTimestampMilis);
    }

    private Domain parseDomain(XmlResourceParser parser, Set<String> seenDomains) throws IOException, XmlPullParserException, ParserException {
        boolean includeSubdomains = parser.getAttributeBooleanValue(null, "includeSubdomains", false);
        if (parser.next() != 4) {
            throw new ParserException(parser, "Domain name missing");
        }
        String domain = parser.getText().trim().toLowerCase(Locale.US);
        if (parser.next() != 3) {
            throw new ParserException(parser, "domain contains additional elements");
        }
        if (!seenDomains.add(domain)) {
            throw new ParserException(parser, domain + " has already been specified");
        }
        return new Domain(domain, includeSubdomains);
    }

    private CertificatesEntryRef parseCertificatesEntry(XmlResourceParser parser, boolean defaultOverridePins) throws IOException, XmlPullParserException, ParserException {
        CertificateSource source;
        boolean overridePins = parser.getAttributeBooleanValue(null, "overridePins", defaultOverridePins);
        int sourceId = parser.getAttributeResourceValue(null, "src", -1);
        String sourceString = parser.getAttributeValue(null, "src");
        if (sourceString == null) {
            throw new ParserException(parser, "certificates element missing src attribute");
        }
        if (sourceId != -1) {
            source = new ResourceCertificateSource(sourceId, this.mContext);
        } else if ("system".equals(sourceString)) {
            source = SystemCertificateSource.getInstance();
        } else if ("user".equals(sourceString)) {
            source = UserCertificateSource.getInstance();
        } else if ("wfa".equals(sourceString)) {
            source = WfaCertificateSource.getInstance();
        } else {
            throw new ParserException(parser, "Unknown certificates src. Should be one of system|user|@resourceVal");
        }
        XmlUtils.skipCurrentTag(parser);
        return new CertificatesEntryRef(source, overridePins);
    }

    private Collection<CertificatesEntryRef> parseTrustAnchors(XmlResourceParser parser, boolean defaultOverridePins) throws IOException, XmlPullParserException, ParserException {
        int outerDepth = parser.getDepth();
        List<CertificatesEntryRef> anchors = new ArrayList<>();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            String tagName = parser.getName();
            if (tagName.equals("certificates")) {
                anchors.add(parseCertificatesEntry(parser, defaultOverridePins));
            } else {
                XmlUtils.skipCurrentTag(parser);
            }
        }
        return anchors;
    }

    private List<Pair<NetworkSecurityConfig.Builder, Set<Domain>>> parseConfigEntry(XmlResourceParser parser, Set<String> seenDomains, NetworkSecurityConfig.Builder parentBuilder, int configType) throws IOException, XmlPullParserException, ParserException {
        XmlConfigSource xmlConfigSource = this;
        List<Pair<NetworkSecurityConfig.Builder, Set<Domain>>> builders = new ArrayList<>();
        NetworkSecurityConfig.Builder builder = new NetworkSecurityConfig.Builder();
        builder.setParent(parentBuilder);
        Set<Domain> domains = new ArraySet<>();
        boolean seenPinSet = false;
        boolean seenTrustAnchors = false;
        boolean z = false;
        boolean defaultOverridePins = configType == 2;
        parser.getName();
        int outerDepth = parser.getDepth();
        builders.add(new Pair<>(builder, domains));
        int i = 0;
        while (i < parser.getAttributeCount()) {
            String name = parser.getAttributeName(i);
            if ("hstsEnforced".equals(name)) {
                builder.setHstsEnforced(parser.getAttributeBooleanValue(i, z));
            } else if ("cleartextTrafficPermitted".equals(name)) {
                builder.setCleartextTrafficPermitted(parser.getAttributeBooleanValue(i, true));
            }
            i++;
            z = false;
        }
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            String tagName = parser.getName();
            if ("domain".equals(tagName)) {
                if (configType != 1) {
                    throw new ParserException(parser, "domain element not allowed in " + getConfigString(configType));
                }
                Domain domain = parseDomain(parser, seenDomains);
                domains.add(domain);
            } else if ("trust-anchors".equals(tagName)) {
                if (seenTrustAnchors) {
                    throw new ParserException(parser, "Multiple trust-anchor elements not allowed");
                }
                builder.addCertificatesEntryRefs(xmlConfigSource.parseTrustAnchors(parser, defaultOverridePins));
                seenTrustAnchors = true;
            } else if ("pin-set".equals(tagName)) {
                if (configType != 1) {
                    throw new ParserException(parser, "pin-set element not allowed in " + getConfigString(configType));
                }
                if (seenPinSet) {
                    throw new ParserException(parser, "Multiple pin-set elements not allowed");
                }
                builder.setPinSet(parsePinSet(parser));
                seenPinSet = true;
            } else if (!"domain-config".equals(tagName)) {
                XmlUtils.skipCurrentTag(parser);
            } else if (configType != 1) {
                throw new ParserException(parser, "Nested domain-config not allowed in " + getConfigString(configType));
            } else {
                builders.addAll(xmlConfigSource.parseConfigEntry(parser, seenDomains, builder, configType));
            }
            xmlConfigSource = this;
        }
        if (configType == 1 && domains.isEmpty()) {
            throw new ParserException(parser, "No domain elements in domain-config");
        }
        return builders;
    }

    private void addDebugAnchorsIfNeeded(NetworkSecurityConfig.Builder debugConfigBuilder, NetworkSecurityConfig.Builder builder) {
        if (debugConfigBuilder == null || !debugConfigBuilder.hasCertificatesEntryRefs() || !builder.hasCertificatesEntryRefs()) {
            return;
        }
        builder.addCertificatesEntryRefs(debugConfigBuilder.getCertificatesEntryRefs());
    }

    private void parseNetworkSecurityConfig(XmlResourceParser parser) throws IOException, XmlPullParserException, ParserException {
        Set<String> seenDomains = new ArraySet<>();
        List<Pair<NetworkSecurityConfig.Builder, Set<Domain>>> builders = new ArrayList<>();
        NetworkSecurityConfig.Builder baseConfigBuilder = null;
        NetworkSecurityConfig.Builder debugConfigBuilder = null;
        boolean seenDebugOverrides = false;
        boolean seenBaseConfig = false;
        XmlUtils.beginDocument(parser, "network-security-config");
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            if ("base-config".equals(parser.getName())) {
                if (seenBaseConfig) {
                    throw new ParserException(parser, "Only one base-config allowed");
                }
                seenBaseConfig = true;
                NetworkSecurityConfig.Builder baseConfigBuilder2 = parseConfigEntry(parser, seenDomains, null, 0).get(0).first;
                baseConfigBuilder = baseConfigBuilder2;
            } else if ("domain-config".equals(parser.getName())) {
                builders.addAll(parseConfigEntry(parser, seenDomains, baseConfigBuilder, 1));
            } else if ("debug-overrides".equals(parser.getName())) {
                if (seenDebugOverrides) {
                    throw new ParserException(parser, "Only one debug-overrides allowed");
                }
                if (this.mDebugBuild) {
                    NetworkSecurityConfig.Builder debugConfigBuilder2 = parseConfigEntry(parser, null, null, 2).get(0).first;
                    debugConfigBuilder = debugConfigBuilder2;
                } else {
                    XmlUtils.skipCurrentTag(parser);
                }
                seenDebugOverrides = true;
            } else {
                XmlUtils.skipCurrentTag(parser);
            }
        }
        if (this.mDebugBuild && debugConfigBuilder == null) {
            debugConfigBuilder = parseDebugOverridesResource();
        }
        NetworkSecurityConfig.Builder platformDefaultBuilder = NetworkSecurityConfig.getDefaultBuilder(this.mApplicationInfo);
        addDebugAnchorsIfNeeded(debugConfigBuilder, platformDefaultBuilder);
        if (baseConfigBuilder != null) {
            baseConfigBuilder.setParent(platformDefaultBuilder);
            addDebugAnchorsIfNeeded(debugConfigBuilder, baseConfigBuilder);
        } else {
            baseConfigBuilder = platformDefaultBuilder;
        }
        Set<Pair<Domain, NetworkSecurityConfig>> configs = new ArraySet<>();
        for (Pair<NetworkSecurityConfig.Builder, Set<Domain>> entry : builders) {
            NetworkSecurityConfig.Builder builder = (NetworkSecurityConfig.Builder) entry.first;
            Set<Domain> domains = entry.second;
            if (builder.getParent() == null) {
                builder.setParent(baseConfigBuilder);
            }
            addDebugAnchorsIfNeeded(debugConfigBuilder, builder);
            NetworkSecurityConfig config = builder.build();
            for (Domain domain : domains) {
                configs.add(new Pair<>(domain, config));
                seenDomains = seenDomains;
            }
        }
        this.mDefaultConfig = baseConfigBuilder.build();
        this.mDomainMap = configs;
    }

    private NetworkSecurityConfig.Builder parseDebugOverridesResource() throws IOException, XmlPullParserException, ParserException {
        Resources resources = this.mContext.getResources();
        String packageName = resources.getResourcePackageName(this.mResourceId);
        String entryName = resources.getResourceEntryName(this.mResourceId);
        int resId = resources.getIdentifier(entryName + "_debug", "xml", packageName);
        if (resId == 0) {
            return null;
        }
        NetworkSecurityConfig.Builder debugConfigBuilder = null;
        XmlResourceParser parser = resources.getXml(resId);
        try {
            XmlUtils.beginDocument(parser, "network-security-config");
            int outerDepth = parser.getDepth();
            boolean seenDebugOverrides = false;
            while (XmlUtils.nextElementWithin(parser, outerDepth)) {
                if ("debug-overrides".equals(parser.getName())) {
                    if (seenDebugOverrides) {
                        throw new ParserException(parser, "Only one debug-overrides allowed");
                    }
                    if (this.mDebugBuild) {
                        debugConfigBuilder = parseConfigEntry(parser, null, null, 2).get(0).first;
                    } else {
                        XmlUtils.skipCurrentTag(parser);
                    }
                    seenDebugOverrides = true;
                } else {
                    XmlUtils.skipCurrentTag(parser);
                }
            }
            if (parser != null) {
                parser.close();
            }
            return debugConfigBuilder;
        } catch (Throwable th) {
            if (parser != null) {
                try {
                    parser.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* loaded from: classes3.dex */
    public static class ParserException extends Exception {
        public ParserException(XmlPullParser parser, String message, Throwable cause) {
            super(message + " at: " + parser.getPositionDescription(), cause);
        }

        public ParserException(XmlPullParser parser, String message) {
            this(parser, message, null);
        }
    }
}
