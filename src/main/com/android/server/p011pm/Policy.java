package com.android.server.p011pm;

import android.content.pm.Signature;
import android.content.pm.SigningDetails;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* compiled from: SELinuxMMAC.java */
/* renamed from: com.android.server.pm.Policy */
/* loaded from: classes2.dex */
public final class Policy {
    public final Set<Signature> mCerts;
    public final Map<String, String> mPkgMap;
    public final String mSeinfo;

    public Policy(PolicyBuilder policyBuilder) {
        this.mSeinfo = policyBuilder.mSeinfo;
        this.mCerts = Collections.unmodifiableSet(policyBuilder.mCerts);
        this.mPkgMap = Collections.unmodifiableMap(policyBuilder.mPkgMap);
    }

    public Set<Signature> getSignatures() {
        return this.mCerts;
    }

    public boolean hasInnerPackages() {
        return !this.mPkgMap.isEmpty();
    }

    public Map<String, String> getInnerPackages() {
        return this.mPkgMap;
    }

    public boolean hasGlobalSeinfo() {
        return this.mSeinfo != null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Signature> it = this.mCerts.iterator();
        while (it.hasNext()) {
            sb.append("cert=" + it.next().toCharsString().substring(0, 11) + "... ");
        }
        if (this.mSeinfo != null) {
            sb.append("seinfo=" + this.mSeinfo);
        }
        for (String str : this.mPkgMap.keySet()) {
            sb.append(" " + str + "=" + this.mPkgMap.get(str));
        }
        return sb.toString();
    }

    public String getMatchedSeInfo(AndroidPackage androidPackage) {
        Signature[] signatureArr = (Signature[]) this.mCerts.toArray(new Signature[0]);
        if (androidPackage.getSigningDetails() == SigningDetails.UNKNOWN || Signature.areExactMatch(signatureArr, androidPackage.getSigningDetails().getSignatures()) || (signatureArr.length <= 1 && androidPackage.getSigningDetails().hasCertificate(signatureArr[0]))) {
            String str = this.mPkgMap.get(androidPackage.getPackageName());
            return str != null ? str : this.mSeinfo;
        }
        return null;
    }

    /* compiled from: SELinuxMMAC.java */
    /* renamed from: com.android.server.pm.Policy$PolicyBuilder */
    /* loaded from: classes2.dex */
    public static final class PolicyBuilder {
        public final Set<Signature> mCerts = new HashSet(2);
        public final Map<String, String> mPkgMap = new HashMap(2);
        public String mSeinfo;

        public PolicyBuilder addSignature(String str) {
            if (str == null) {
                throw new IllegalArgumentException("Invalid signature value " + str);
            }
            this.mCerts.add(new Signature(str));
            return this;
        }

        public PolicyBuilder setGlobalSeinfoOrThrow(String str) {
            if (!validateValue(str)) {
                throw new IllegalArgumentException("Invalid seinfo value " + str);
            }
            String str2 = this.mSeinfo;
            if (str2 != null && !str2.equals(str)) {
                throw new IllegalStateException("Duplicate seinfo tag found");
            }
            this.mSeinfo = str;
            return this;
        }

        public PolicyBuilder addInnerPackageMapOrThrow(String str, String str2) {
            if (!validateValue(str)) {
                throw new IllegalArgumentException("Invalid package name " + str);
            } else if (!validateValue(str2)) {
                throw new IllegalArgumentException("Invalid seinfo value " + str2);
            } else {
                String str3 = this.mPkgMap.get(str);
                if (str3 != null && !str3.equals(str2)) {
                    throw new IllegalStateException("Conflicting seinfo value found");
                }
                this.mPkgMap.put(str, str2);
                return this;
            }
        }

        public final boolean validateValue(String str) {
            return str != null && str.matches("\\A[\\.\\w]+\\z");
        }

        public Policy build() {
            Policy policy = new Policy(this);
            if (policy.mCerts.isEmpty()) {
                throw new IllegalStateException("Missing certs with signer tag. Expecting at least one.");
            }
            if ((policy.mSeinfo == null) ^ policy.mPkgMap.isEmpty()) {
                return policy;
            }
            throw new IllegalStateException("Only seinfo tag XOR package tags are allowed within a signer stanza.");
        }
    }
}
