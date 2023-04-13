package com.android.internal.org.bouncycastle.crypto.util;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.nist.NISTNamedCurves;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.p019ec.CustomNamedCurves;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECNamedDomainParameters;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.util.Strings;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class SSHNamedCurves {
    private static final Map<String, ASN1ObjectIdentifier> oidMap = Collections.unmodifiableMap(new HashMap<String, ASN1ObjectIdentifier>() { // from class: com.android.internal.org.bouncycastle.crypto.util.SSHNamedCurves.1
        {
            put("nistp256", SECObjectIdentifiers.secp256r1);
            put("nistp384", SECObjectIdentifiers.secp384r1);
            put("nistp521", SECObjectIdentifiers.secp521r1);
            put("nistk163", SECObjectIdentifiers.sect163k1);
            put("nistp192", SECObjectIdentifiers.secp192r1);
            put("nistp224", SECObjectIdentifiers.secp224r1);
            put("nistk233", SECObjectIdentifiers.sect233k1);
            put("nistb233", SECObjectIdentifiers.sect233r1);
            put("nistk283", SECObjectIdentifiers.sect283k1);
            put("nistk409", SECObjectIdentifiers.sect409k1);
            put("nistb409", SECObjectIdentifiers.sect409r1);
            put("nistt571", SECObjectIdentifiers.sect571k1);
        }
    });
    private static final Map<String, String> curveNameToSSHName = Collections.unmodifiableMap(new HashMap<String, String>() { // from class: com.android.internal.org.bouncycastle.crypto.util.SSHNamedCurves.2
        {
            String[][] curves = {new String[]{"secp256r1", "nistp256"}, new String[]{"secp384r1", "nistp384"}, new String[]{"secp521r1", "nistp521"}, new String[]{"sect163k1", "nistk163"}, new String[]{"secp192r1", "nistp192"}, new String[]{"secp224r1", "nistp224"}, new String[]{"sect233k1", "nistk233"}, new String[]{"sect233r1", "nistb233"}, new String[]{"sect283k1", "nistk283"}, new String[]{"sect409k1", "nistk409"}, new String[]{"sect409r1", "nistb409"}, new String[]{"sect571k1", "nistt571"}};
            for (int i = 0; i != curves.length; i++) {
                String[] item = curves[i];
                put(item[0], item[1]);
            }
        }
    });
    private static HashMap<ECCurve, String> curveMap = new HashMap<ECCurve, String>() { // from class: com.android.internal.org.bouncycastle.crypto.util.SSHNamedCurves.3
        {
            Enumeration<Object> e = CustomNamedCurves.getNames();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                X9ECParameters parameters = CustomNamedCurves.getByName(name);
                put(parameters.getCurve(), name);
            }
        }
    };
    private static final Map<ASN1ObjectIdentifier, String> oidToName = Collections.unmodifiableMap(new HashMap<ASN1ObjectIdentifier, String>() { // from class: com.android.internal.org.bouncycastle.crypto.util.SSHNamedCurves.4
        {
            for (String key : SSHNamedCurves.oidMap.keySet()) {
                put((ASN1ObjectIdentifier) SSHNamedCurves.oidMap.get(key), key);
            }
        }
    });

    public static ASN1ObjectIdentifier getByName(String sshName) {
        return oidMap.get(sshName);
    }

    public static X9ECParameters getParameters(String sshName) {
        return NISTNamedCurves.getByOID(oidMap.get(Strings.toLowerCase(sshName)));
    }

    public static X9ECParameters getParameters(ASN1ObjectIdentifier oid) {
        return NISTNamedCurves.getByOID(oid);
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        return oidToName.get(oid);
    }

    public static String getNameForParameters(ECDomainParameters parameters) {
        if (parameters instanceof ECNamedDomainParameters) {
            return getName(((ECNamedDomainParameters) parameters).getName());
        }
        return getNameForParameters(parameters.getCurve());
    }

    public static String getNameForParameters(ECCurve curve) {
        return curveNameToSSHName.get(curveMap.get(curve));
    }
}
