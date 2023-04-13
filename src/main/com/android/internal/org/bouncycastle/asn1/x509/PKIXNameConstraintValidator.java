package com.android.internal.org.bouncycastle.asn1.x509;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERIA5String;
import com.android.internal.org.bouncycastle.asn1.x500.RDN;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x500.style.IETFUtils;
import com.android.internal.org.bouncycastle.asn1.x500.style.RFC4519Style;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Integers;
import com.android.internal.org.bouncycastle.util.Strings;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* loaded from: classes4.dex */
public class PKIXNameConstraintValidator implements NameConstraintValidator {
    private Set permittedSubtreesDN;
    private Set permittedSubtreesDNS;
    private Set permittedSubtreesEmail;
    private Set permittedSubtreesIP;
    private Set permittedSubtreesOtherName;
    private Set permittedSubtreesURI;
    private Set excludedSubtreesDN = new HashSet();
    private Set excludedSubtreesDNS = new HashSet();
    private Set excludedSubtreesEmail = new HashSet();
    private Set excludedSubtreesURI = new HashSet();
    private Set excludedSubtreesIP = new HashSet();
    private Set excludedSubtreesOtherName = new HashSet();

    @Override // com.android.internal.org.bouncycastle.asn1.x509.NameConstraintValidator
    public void checkPermitted(GeneralName name) throws NameConstraintValidatorException {
        switch (name.getTagNo()) {
            case 0:
                checkPermittedOtherName(this.permittedSubtreesOtherName, OtherName.getInstance(name.getName()));
                return;
            case 1:
                checkPermittedEmail(this.permittedSubtreesEmail, extractNameAsString(name));
                return;
            case 2:
                checkPermittedDNS(this.permittedSubtreesDNS, extractNameAsString(name));
                return;
            case 3:
            case 5:
            default:
                return;
            case 4:
                checkPermittedDN(X500Name.getInstance(name.getName()));
                return;
            case 6:
                checkPermittedURI(this.permittedSubtreesURI, extractNameAsString(name));
                return;
            case 7:
                checkPermittedIP(this.permittedSubtreesIP, ASN1OctetString.getInstance(name.getName()).getOctets());
                return;
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x509.NameConstraintValidator
    public void checkExcluded(GeneralName name) throws NameConstraintValidatorException {
        switch (name.getTagNo()) {
            case 0:
                checkExcludedOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(name.getName()));
                return;
            case 1:
                checkExcludedEmail(this.excludedSubtreesEmail, extractNameAsString(name));
                return;
            case 2:
                checkExcludedDNS(this.excludedSubtreesDNS, extractNameAsString(name));
                return;
            case 3:
            case 5:
            default:
                return;
            case 4:
                checkExcludedDN(X500Name.getInstance(name.getName()));
                return;
            case 6:
                checkExcludedURI(this.excludedSubtreesURI, extractNameAsString(name));
                return;
            case 7:
                checkExcludedIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(name.getName()).getOctets());
                return;
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x509.NameConstraintValidator
    public void intersectPermittedSubtree(GeneralSubtree permitted) {
        intersectPermittedSubtree(new GeneralSubtree[]{permitted});
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x509.NameConstraintValidator
    public void intersectPermittedSubtree(GeneralSubtree[] permitted) {
        Map subtreesMap = new HashMap();
        for (int i = 0; i != permitted.length; i++) {
            GeneralSubtree subtree = permitted[i];
            Integer tagNo = Integers.valueOf(subtree.getBase().getTagNo());
            if (subtreesMap.get(tagNo) == null) {
                subtreesMap.put(tagNo, new HashSet());
            }
            ((Set) subtreesMap.get(tagNo)).add(subtree);
        }
        for (Map.Entry entry : subtreesMap.entrySet()) {
            int nameType = ((Integer) entry.getKey()).intValue();
            switch (nameType) {
                case 0:
                    this.permittedSubtreesOtherName = intersectOtherName(this.permittedSubtreesOtherName, (Set) entry.getValue());
                    break;
                case 1:
                    this.permittedSubtreesEmail = intersectEmail(this.permittedSubtreesEmail, (Set) entry.getValue());
                    break;
                case 2:
                    this.permittedSubtreesDNS = intersectDNS(this.permittedSubtreesDNS, (Set) entry.getValue());
                    break;
                case 3:
                case 5:
                default:
                    throw new IllegalStateException("Unknown tag encountered: " + nameType);
                case 4:
                    this.permittedSubtreesDN = intersectDN(this.permittedSubtreesDN, (Set) entry.getValue());
                    break;
                case 6:
                    this.permittedSubtreesURI = intersectURI(this.permittedSubtreesURI, (Set) entry.getValue());
                    break;
                case 7:
                    this.permittedSubtreesIP = intersectIP(this.permittedSubtreesIP, (Set) entry.getValue());
                    break;
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x509.NameConstraintValidator
    public void intersectEmptyPermittedSubtree(int nameType) {
        switch (nameType) {
            case 0:
                this.permittedSubtreesOtherName = new HashSet();
                return;
            case 1:
                this.permittedSubtreesEmail = new HashSet();
                return;
            case 2:
                this.permittedSubtreesDNS = new HashSet();
                return;
            case 3:
            case 5:
            default:
                throw new IllegalStateException("Unknown tag encountered: " + nameType);
            case 4:
                this.permittedSubtreesDN = new HashSet();
                return;
            case 6:
                this.permittedSubtreesURI = new HashSet();
                return;
            case 7:
                this.permittedSubtreesIP = new HashSet();
                return;
        }
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x509.NameConstraintValidator
    public void addExcludedSubtree(GeneralSubtree subtree) {
        GeneralName base = subtree.getBase();
        switch (base.getTagNo()) {
            case 0:
                this.excludedSubtreesOtherName = unionOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(base.getName()));
                return;
            case 1:
                this.excludedSubtreesEmail = unionEmail(this.excludedSubtreesEmail, extractNameAsString(base));
                return;
            case 2:
                this.excludedSubtreesDNS = unionDNS(this.excludedSubtreesDNS, extractNameAsString(base));
                return;
            case 3:
            case 5:
            default:
                throw new IllegalStateException("Unknown tag encountered: " + base.getTagNo());
            case 4:
                this.excludedSubtreesDN = unionDN(this.excludedSubtreesDN, (ASN1Sequence) base.getName().toASN1Primitive());
                return;
            case 6:
                this.excludedSubtreesURI = unionURI(this.excludedSubtreesURI, extractNameAsString(base));
                return;
            case 7:
                this.excludedSubtreesIP = unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(base.getName()).getOctets());
                return;
        }
    }

    public int hashCode() {
        return hashCollection(this.excludedSubtreesDN) + hashCollection(this.excludedSubtreesDNS) + hashCollection(this.excludedSubtreesEmail) + hashCollection(this.excludedSubtreesIP) + hashCollection(this.excludedSubtreesURI) + hashCollection(this.excludedSubtreesOtherName) + hashCollection(this.permittedSubtreesDN) + hashCollection(this.permittedSubtreesDNS) + hashCollection(this.permittedSubtreesEmail) + hashCollection(this.permittedSubtreesIP) + hashCollection(this.permittedSubtreesURI) + hashCollection(this.permittedSubtreesOtherName);
    }

    public boolean equals(Object o) {
        if (o instanceof PKIXNameConstraintValidator) {
            PKIXNameConstraintValidator constraintValidator = (PKIXNameConstraintValidator) o;
            return collectionsAreEqual(constraintValidator.excludedSubtreesDN, this.excludedSubtreesDN) && collectionsAreEqual(constraintValidator.excludedSubtreesDNS, this.excludedSubtreesDNS) && collectionsAreEqual(constraintValidator.excludedSubtreesEmail, this.excludedSubtreesEmail) && collectionsAreEqual(constraintValidator.excludedSubtreesIP, this.excludedSubtreesIP) && collectionsAreEqual(constraintValidator.excludedSubtreesURI, this.excludedSubtreesURI) && collectionsAreEqual(constraintValidator.excludedSubtreesOtherName, this.excludedSubtreesOtherName) && collectionsAreEqual(constraintValidator.permittedSubtreesDN, this.permittedSubtreesDN) && collectionsAreEqual(constraintValidator.permittedSubtreesDNS, this.permittedSubtreesDNS) && collectionsAreEqual(constraintValidator.permittedSubtreesEmail, this.permittedSubtreesEmail) && collectionsAreEqual(constraintValidator.permittedSubtreesIP, this.permittedSubtreesIP) && collectionsAreEqual(constraintValidator.permittedSubtreesURI, this.permittedSubtreesURI) && collectionsAreEqual(constraintValidator.permittedSubtreesOtherName, this.permittedSubtreesOtherName);
        }
        return false;
    }

    public void checkPermittedDN(X500Name dns) throws NameConstraintValidatorException {
        checkPermittedDN(this.permittedSubtreesDN, ASN1Sequence.getInstance(dns.toASN1Primitive()));
    }

    public void checkExcludedDN(X500Name dns) throws NameConstraintValidatorException {
        checkExcludedDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(dns));
    }

    private static boolean withinDNSubtree(ASN1Sequence dns, ASN1Sequence subtree) {
        if (subtree.size() >= 1 && subtree.size() <= dns.size()) {
            int start = 0;
            RDN subtreeRdnStart = RDN.getInstance(subtree.getObjectAt(0));
            for (int j = 0; j < dns.size(); j++) {
                start = j;
                if (IETFUtils.rDNAreEqual(subtreeRdnStart, RDN.getInstance(dns.getObjectAt(j)))) {
                    break;
                }
            }
            int j2 = subtree.size();
            if (j2 > dns.size() - start) {
                return false;
            }
            for (int j3 = 0; j3 < subtree.size(); j3++) {
                RDN subtreeRdn = RDN.getInstance(subtree.getObjectAt(j3));
                RDN dnsRdn = RDN.getInstance(dns.getObjectAt(start + j3));
                if (subtreeRdn.size() != dnsRdn.size() || !subtreeRdn.getFirst().getType().equals((ASN1Primitive) dnsRdn.getFirst().getType())) {
                    return false;
                }
                if (subtreeRdn.size() == 1 && subtreeRdn.getFirst().getType().equals((ASN1Primitive) RFC4519Style.serialNumber)) {
                    if (!dnsRdn.getFirst().getValue().toString().startsWith(subtreeRdn.getFirst().getValue().toString())) {
                        return false;
                    }
                } else if (!IETFUtils.rDNAreEqual(subtreeRdn, dnsRdn)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void checkPermittedDN(Set permitted, ASN1Sequence dns) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        if (permitted.isEmpty() && dns.size() == 0) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            ASN1Sequence subtree = (ASN1Sequence) it.next();
            if (withinDNSubtree(dns, subtree)) {
                return;
            }
        }
        throw new NameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
    }

    private void checkExcludedDN(Set excluded, ASN1Sequence dns) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            ASN1Sequence subtree = (ASN1Sequence) it.next();
            if (withinDNSubtree(dns, subtree)) {
                throw new NameConstraintValidatorException("Subject distinguished name is from an excluded subtree");
            }
        }
    }

    private Set intersectDN(Set permitted, Set dns) {
        Set intersect = new HashSet();
        Iterator it = dns.iterator();
        while (it.hasNext()) {
            ASN1Sequence dn = ASN1Sequence.getInstance(((GeneralSubtree) it.next()).getBase().getName().toASN1Primitive());
            if (permitted == null) {
                if (dn != null) {
                    intersect.add(dn);
                }
            } else {
                Iterator _iter = permitted.iterator();
                while (_iter.hasNext()) {
                    ASN1Sequence subtree = (ASN1Sequence) _iter.next();
                    if (withinDNSubtree(dn, subtree)) {
                        intersect.add(dn);
                    } else if (withinDNSubtree(subtree, dn)) {
                        intersect.add(subtree);
                    }
                }
            }
        }
        return intersect;
    }

    private Set unionDN(Set excluded, ASN1Sequence dn) {
        if (excluded.isEmpty()) {
            if (dn == null) {
                return excluded;
            }
            excluded.add(dn);
            return excluded;
        }
        Set intersect = new HashSet();
        for (Object obj : excluded) {
            ASN1Sequence subtree = ASN1Sequence.getInstance(obj);
            if (withinDNSubtree(dn, subtree)) {
                intersect.add(subtree);
            } else if (withinDNSubtree(subtree, dn)) {
                intersect.add(dn);
            } else {
                intersect.add(subtree);
                intersect.add(dn);
            }
        }
        return intersect;
    }

    private Set intersectOtherName(Set permitted, Set otherNames) {
        Set intersect = new HashSet();
        Iterator it = otherNames.iterator();
        while (it.hasNext()) {
            OtherName otName1 = OtherName.getInstance(((GeneralSubtree) it.next()).getBase().getName());
            if (permitted == null) {
                if (otName1 != null) {
                    intersect.add(otName1);
                }
            } else {
                for (Object obj : permitted) {
                    OtherName otName2 = OtherName.getInstance(obj);
                    intersectOtherName(otName1, otName2, intersect);
                }
            }
        }
        return intersect;
    }

    private void intersectOtherName(OtherName otName1, OtherName otName2, Set intersect) {
        if (otName1.equals(otName2)) {
            intersect.add(otName1);
        }
    }

    private Set unionOtherName(Set permitted, OtherName otherName) {
        Set union = permitted != null ? new HashSet(permitted) : new HashSet();
        union.add(otherName);
        return union;
    }

    private Set intersectEmail(Set permitted, Set emails) {
        Set intersect = new HashSet();
        Iterator it = emails.iterator();
        while (it.hasNext()) {
            String email = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (permitted == null) {
                if (email != null) {
                    intersect.add(email);
                }
            } else {
                Iterator it2 = permitted.iterator();
                while (it2.hasNext()) {
                    String _permitted = (String) it2.next();
                    intersectEmail(email, _permitted, intersect);
                }
            }
        }
        return intersect;
    }

    private Set unionEmail(Set excluded, String email) {
        if (excluded.isEmpty()) {
            if (email == null) {
                return excluded;
            }
            excluded.add(email);
            return excluded;
        }
        Set union = new HashSet();
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String _excluded = (String) it.next();
            unionEmail(_excluded, email, union);
        }
        return union;
    }

    private Set intersectIP(Set permitted, Set ips) {
        Set intersect = new HashSet();
        Iterator it = ips.iterator();
        while (it.hasNext()) {
            byte[] ip = ASN1OctetString.getInstance(((GeneralSubtree) it.next()).getBase().getName()).getOctets();
            if (permitted == null) {
                if (ip != null) {
                    intersect.add(ip);
                }
            } else {
                Iterator it2 = permitted.iterator();
                while (it2.hasNext()) {
                    byte[] _permitted = (byte[]) it2.next();
                    intersect.addAll(intersectIPRange(_permitted, ip));
                }
            }
        }
        return intersect;
    }

    private Set unionIP(Set excluded, byte[] ip) {
        if (excluded.isEmpty()) {
            if (ip == null) {
                return excluded;
            }
            excluded.add(ip);
            return excluded;
        }
        Set union = new HashSet();
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            byte[] _excluded = (byte[]) it.next();
            union.addAll(unionIPRange(_excluded, ip));
        }
        return union;
    }

    private Set unionIPRange(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        Set set = new HashSet();
        if (Arrays.areEqual(ipWithSubmask1, ipWithSubmask2)) {
            set.add(ipWithSubmask1);
        } else {
            set.add(ipWithSubmask1);
            set.add(ipWithSubmask2);
        }
        return set;
    }

    private Set intersectIPRange(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        if (ipWithSubmask1.length != ipWithSubmask2.length) {
            return Collections.EMPTY_SET;
        }
        byte[][] temp = extractIPsAndSubnetMasks(ipWithSubmask1, ipWithSubmask2);
        byte[] ip1 = temp[0];
        byte[] subnetmask1 = temp[1];
        byte[] ip2 = temp[2];
        byte[] subnetmask2 = temp[3];
        byte[][] minMax = minMaxIPs(ip1, subnetmask1, ip2, subnetmask2);
        byte[] max = min(minMax[1], minMax[3]);
        byte[] min = max(minMax[0], minMax[2]);
        if (compareTo(min, max) != 1) {
            byte[] ip = m68or(minMax[0], minMax[2]);
            byte[] subnetmask = m68or(subnetmask1, subnetmask2);
            return Collections.singleton(ipWithSubnetMask(ip, subnetmask));
        }
        return Collections.EMPTY_SET;
    }

    private byte[] ipWithSubnetMask(byte[] ip, byte[] subnetMask) {
        int ipLength = ip.length;
        byte[] temp = new byte[ipLength * 2];
        System.arraycopy(ip, 0, temp, 0, ipLength);
        System.arraycopy(subnetMask, 0, temp, ipLength, ipLength);
        return temp;
    }

    private byte[][] extractIPsAndSubnetMasks(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        int ipLength = ipWithSubmask1.length / 2;
        byte[] ip1 = new byte[ipLength];
        byte[] subnetmask1 = new byte[ipLength];
        System.arraycopy(ipWithSubmask1, 0, ip1, 0, ipLength);
        System.arraycopy(ipWithSubmask1, ipLength, subnetmask1, 0, ipLength);
        byte[] ip2 = new byte[ipLength];
        byte[] subnetmask2 = new byte[ipLength];
        System.arraycopy(ipWithSubmask2, 0, ip2, 0, ipLength);
        System.arraycopy(ipWithSubmask2, ipLength, subnetmask2, 0, ipLength);
        return new byte[][]{ip1, subnetmask1, ip2, subnetmask2};
    }

    private byte[][] minMaxIPs(byte[] ip1, byte[] subnetmask1, byte[] ip2, byte[] subnetmask2) {
        int ipLength = ip1.length;
        byte[] min1 = new byte[ipLength];
        byte[] max1 = new byte[ipLength];
        byte[] min2 = new byte[ipLength];
        byte[] max2 = new byte[ipLength];
        for (int i = 0; i < ipLength; i++) {
            min1[i] = (byte) (ip1[i] & subnetmask1[i]);
            max1[i] = (byte) ((ip1[i] & subnetmask1[i]) | (~subnetmask1[i]));
            min2[i] = (byte) (ip2[i] & subnetmask2[i]);
            max2[i] = (byte) ((ip2[i] & subnetmask2[i]) | (~subnetmask2[i]));
        }
        return new byte[][]{min1, max1, min2, max2};
    }

    private void checkPermittedEmail(Set permitted, String email) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (emailIsConstrained(email, str)) {
                return;
            }
        }
        if (email.length() == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("Subject email address is not from a permitted subtree.");
    }

    private void checkPermittedOtherName(Set permitted, OtherName name) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        for (Object obj : permitted) {
            OtherName str = OtherName.getInstance(obj);
            if (otherNameIsConstrained(name, str)) {
                return;
            }
        }
        throw new NameConstraintValidatorException("Subject OtherName is not from a permitted subtree.");
    }

    private void checkExcludedOtherName(Set excluded, OtherName name) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        for (Object obj : excluded) {
            OtherName str = OtherName.getInstance(obj);
            if (otherNameIsConstrained(name, str)) {
                throw new NameConstraintValidatorException("OtherName is from an excluded subtree.");
            }
        }
    }

    private void checkExcludedEmail(Set excluded, String email) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (emailIsConstrained(email, str)) {
                throw new NameConstraintValidatorException("Email address is from an excluded subtree.");
            }
        }
    }

    private void checkPermittedIP(Set permitted, byte[] ip) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            byte[] ipWithSubnet = (byte[]) it.next();
            if (isIPConstrained(ip, ipWithSubnet)) {
                return;
            }
        }
        if (ip.length == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("IP is not from a permitted subtree.");
    }

    private void checkExcludedIP(Set excluded, byte[] ip) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            byte[] ipWithSubnet = (byte[]) it.next();
            if (isIPConstrained(ip, ipWithSubnet)) {
                throw new NameConstraintValidatorException("IP is from an excluded subtree.");
            }
        }
    }

    private boolean isIPConstrained(byte[] ip, byte[] constraint) {
        int ipLength = ip.length;
        if (ipLength != constraint.length / 2) {
            return false;
        }
        byte[] subnetMask = new byte[ipLength];
        System.arraycopy(constraint, ipLength, subnetMask, 0, ipLength);
        byte[] permittedSubnetAddress = new byte[ipLength];
        byte[] ipSubnetAddress = new byte[ipLength];
        for (int i = 0; i < ipLength; i++) {
            permittedSubnetAddress[i] = (byte) (constraint[i] & subnetMask[i]);
            ipSubnetAddress[i] = (byte) (ip[i] & subnetMask[i]);
        }
        return Arrays.areEqual(permittedSubnetAddress, ipSubnetAddress);
    }

    private boolean otherNameIsConstrained(OtherName name, OtherName constraint) {
        if (constraint.equals(name)) {
            return true;
        }
        return false;
    }

    private boolean emailIsConstrained(String email, String constraint) {
        String sub = email.substring(email.indexOf(64) + 1);
        if (constraint.indexOf(64) != -1) {
            if (email.equalsIgnoreCase(constraint) || sub.equalsIgnoreCase(constraint.substring(1))) {
                return true;
            }
        } else if (constraint.charAt(0) != '.') {
            if (sub.equalsIgnoreCase(constraint)) {
                return true;
            }
        } else if (withinDomain(sub, constraint)) {
            return true;
        }
        return false;
    }

    private boolean withinDomain(String testDomain, String domain) {
        String tempDomain = domain;
        if (tempDomain.startsWith(MediaMetrics.SEPARATOR)) {
            tempDomain = tempDomain.substring(1);
        }
        String[] domainParts = Strings.split(tempDomain, '.');
        String[] testDomainParts = Strings.split(testDomain, '.');
        if (testDomainParts.length <= domainParts.length) {
            return false;
        }
        int d = testDomainParts.length - domainParts.length;
        for (int i = -1; i < domainParts.length; i++) {
            if (i == -1) {
                if (testDomainParts[i + d].equals("")) {
                    return false;
                }
            } else if (!domainParts[i].equalsIgnoreCase(testDomainParts[i + d])) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x000d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void checkPermittedDNS(Set permitted, String dns) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (withinDomain(dns, str) || dns.equalsIgnoreCase(str)) {
                return;
            }
            while (it.hasNext()) {
            }
        }
        if (dns.length() == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("DNS is not from a permitted subtree.");
    }

    /* JADX WARN: Removed duplicated region for block: B:8:0x0011  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void checkExcludedDNS(Set excluded, String dns) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (withinDomain(dns, str) || dns.equalsIgnoreCase(str)) {
                throw new NameConstraintValidatorException("DNS is from an excluded subtree.");
            }
            while (it.hasNext()) {
            }
        }
    }

    private void unionEmail(String email1, String email2, Set union) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    union.add(email1);
                    return;
                }
                union.add(email1);
                union.add(email2);
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(_sub, email2)) {
                    union.add(email2);
                    return;
                }
                union.add(email1);
                union.add(email2);
            } else if (_sub.equalsIgnoreCase(email2)) {
                union.add(email2);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email1.startsWith(MediaMetrics.SEPARATOR)) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    union.add(email1);
                    return;
                }
                union.add(email1);
                union.add(email2);
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    union.add(email2);
                } else if (withinDomain(email2, email1)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (withinDomain(email2, email1)) {
                union.add(email1);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email1.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                union.add(email1);
                return;
            }
            union.add(email1);
            union.add(email2);
        } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
            if (withinDomain(email1, email2)) {
                union.add(email2);
                return;
            }
            union.add(email1);
            union.add(email2);
        } else if (email1.equalsIgnoreCase(email2)) {
            union.add(email1);
        } else {
            union.add(email1);
            union.add(email2);
        }
    }

    private void unionURI(String email1, String email2, Set union) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    union.add(email1);
                    return;
                }
                union.add(email1);
                union.add(email2);
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(_sub, email2)) {
                    union.add(email2);
                    return;
                }
                union.add(email1);
                union.add(email2);
            } else if (_sub.equalsIgnoreCase(email2)) {
                union.add(email2);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email1.startsWith(MediaMetrics.SEPARATOR)) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    union.add(email1);
                    return;
                }
                union.add(email1);
                union.add(email2);
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    union.add(email2);
                } else if (withinDomain(email2, email1)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (withinDomain(email2, email1)) {
                union.add(email1);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email1.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                union.add(email1);
                return;
            }
            union.add(email1);
            union.add(email2);
        } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
            if (withinDomain(email1, email2)) {
                union.add(email2);
                return;
            }
            union.add(email1);
            union.add(email2);
        } else if (email1.equalsIgnoreCase(email2)) {
            union.add(email1);
        } else {
            union.add(email1);
            union.add(email2);
        }
    }

    private Set intersectDNS(Set permitted, Set dnss) {
        Set intersect = new HashSet();
        Iterator it = dnss.iterator();
        while (it.hasNext()) {
            String dns = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (permitted == null) {
                if (dns != null) {
                    intersect.add(dns);
                }
            } else {
                Iterator _iter = permitted.iterator();
                while (_iter.hasNext()) {
                    String _permitted = (String) _iter.next();
                    if (withinDomain(_permitted, dns)) {
                        intersect.add(_permitted);
                    } else if (withinDomain(dns, _permitted)) {
                        intersect.add(dns);
                    }
                }
            }
        }
        return intersect;
    }

    private Set unionDNS(Set excluded, String dns) {
        if (excluded.isEmpty()) {
            if (dns == null) {
                return excluded;
            }
            excluded.add(dns);
            return excluded;
        }
        Set union = new HashSet();
        Iterator _iter = excluded.iterator();
        while (_iter.hasNext()) {
            String _permitted = (String) _iter.next();
            if (withinDomain(_permitted, dns)) {
                union.add(dns);
            } else if (withinDomain(dns, _permitted)) {
                union.add(_permitted);
            } else {
                union.add(_permitted);
                union.add(dns);
            }
        }
        return union;
    }

    private void intersectEmail(String email1, String email2, Set intersect) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                }
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(_sub, email2)) {
                    intersect.add(email1);
                }
            } else if (_sub.equalsIgnoreCase(email2)) {
                intersect.add(email1);
            }
        } else if (email1.startsWith(MediaMetrics.SEPARATOR)) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    intersect.add(email2);
                }
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                } else if (withinDomain(email2, email1)) {
                    intersect.add(email2);
                }
            } else if (withinDomain(email2, email1)) {
                intersect.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email2.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                intersect.add(email2);
            }
        } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
            if (withinDomain(email1, email2)) {
                intersect.add(email1);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            intersect.add(email1);
        }
    }

    private void checkExcludedURI(Set excluded, String uri) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (isUriConstrained(uri, str)) {
                throw new NameConstraintValidatorException("URI is from an excluded subtree.");
            }
        }
    }

    private Set intersectURI(Set permitted, Set uris) {
        Set intersect = new HashSet();
        Iterator it = uris.iterator();
        while (it.hasNext()) {
            String uri = extractNameAsString(((GeneralSubtree) it.next()).getBase());
            if (permitted == null) {
                if (uri != null) {
                    intersect.add(uri);
                }
            } else {
                Iterator _iter = permitted.iterator();
                while (_iter.hasNext()) {
                    String _permitted = (String) _iter.next();
                    intersectURI(_permitted, uri, intersect);
                }
            }
        }
        return intersect;
    }

    private Set unionURI(Set excluded, String uri) {
        if (excluded.isEmpty()) {
            if (uri == null) {
                return excluded;
            }
            excluded.add(uri);
            return excluded;
        }
        Set union = new HashSet();
        Iterator _iter = excluded.iterator();
        while (_iter.hasNext()) {
            String _excluded = (String) _iter.next();
            unionURI(_excluded, uri, union);
        }
        return union;
    }

    private void intersectURI(String email1, String email2, Set intersect) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                }
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(_sub, email2)) {
                    intersect.add(email1);
                }
            } else if (_sub.equalsIgnoreCase(email2)) {
                intersect.add(email1);
            }
        } else if (email1.startsWith(MediaMetrics.SEPARATOR)) {
            if (email2.indexOf(64) != -1) {
                String _sub2 = email2.substring(email1.indexOf(64) + 1);
                if (withinDomain(_sub2, email1)) {
                    intersect.add(email2);
                }
            } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
                if (withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                } else if (withinDomain(email2, email1)) {
                    intersect.add(email2);
                }
            } else if (withinDomain(email2, email1)) {
                intersect.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub3 = email2.substring(email2.indexOf(64) + 1);
            if (_sub3.equalsIgnoreCase(email1)) {
                intersect.add(email2);
            }
        } else if (email2.startsWith(MediaMetrics.SEPARATOR)) {
            if (withinDomain(email1, email2)) {
                intersect.add(email1);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            intersect.add(email1);
        }
    }

    private void checkPermittedURI(Set permitted, String uri) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (isUriConstrained(uri, str)) {
                return;
            }
        }
        if (uri.length() == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("URI is not from a permitted subtree.");
    }

    private boolean isUriConstrained(String uri, String constraint) {
        String host = extractHostFromURL(uri);
        return !constraint.startsWith(MediaMetrics.SEPARATOR) ? host.equalsIgnoreCase(constraint) : withinDomain(host, constraint);
    }

    private static String extractHostFromURL(String url) {
        String sub = url.substring(url.indexOf(58) + 1);
        if (sub.indexOf("//") != -1) {
            sub = sub.substring(sub.indexOf("//") + 2);
        }
        if (sub.lastIndexOf(58) != -1) {
            sub = sub.substring(0, sub.lastIndexOf(58));
        }
        String sub2 = sub.substring(sub.indexOf(58) + 1);
        String sub3 = sub2.substring(sub2.indexOf(64) + 1);
        if (sub3.indexOf(47) != -1) {
            return sub3.substring(0, sub3.indexOf(47));
        }
        return sub3;
    }

    private String extractNameAsString(GeneralName name) {
        return DERIA5String.getInstance(name.getName()).getString();
    }

    private static byte[] max(byte[] ip1, byte[] ip2) {
        for (int i = 0; i < ip1.length; i++) {
            if ((ip1[i] & 65535) > (65535 & ip2[i])) {
                return ip1;
            }
        }
        return ip2;
    }

    private static byte[] min(byte[] ip1, byte[] ip2) {
        for (int i = 0; i < ip1.length; i++) {
            if ((ip1[i] & 65535) < (65535 & ip2[i])) {
                return ip1;
            }
        }
        return ip2;
    }

    private static int compareTo(byte[] ip1, byte[] ip2) {
        if (Arrays.areEqual(ip1, ip2)) {
            return 0;
        }
        if (Arrays.areEqual(max(ip1, ip2), ip1)) {
            return 1;
        }
        return -1;
    }

    /* renamed from: or */
    private static byte[] m68or(byte[] ip1, byte[] ip2) {
        byte[] temp = new byte[ip1.length];
        for (int i = 0; i < ip1.length; i++) {
            temp[i] = (byte) (ip1[i] | ip2[i]);
        }
        return temp;
    }

    private int hashCollection(Collection coll) {
        if (coll == null) {
            return 0;
        }
        int hash = 0;
        for (Object o : coll) {
            if (o instanceof byte[]) {
                hash += Arrays.hashCode((byte[]) o);
            } else {
                hash += o.hashCode();
            }
        }
        return hash;
    }

    private boolean collectionsAreEqual(Collection coll1, Collection coll2) {
        if (coll1 == coll2) {
            return true;
        }
        if (coll1 == null || coll2 == null || coll1.size() != coll2.size()) {
            return false;
        }
        for (Object a : coll1) {
            Iterator it2 = coll2.iterator();
            boolean found = false;
            while (true) {
                if (it2.hasNext()) {
                    Object b = it2.next();
                    if (equals(a, b)) {
                        found = true;
                        continue;
                        break;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if ((o1 instanceof byte[]) && (o2 instanceof byte[])) {
            return Arrays.areEqual((byte[]) o1, (byte[]) o2);
        }
        return o1.equals(o2);
    }

    private String stringifyIP(byte[] ip) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < ip.length / 2; i++) {
            if (temp.length() > 0) {
                temp.append(MediaMetrics.SEPARATOR);
            }
            temp.append(Integer.toString(ip[i] & 255));
        }
        temp.append("/");
        boolean first = true;
        for (int i2 = ip.length / 2; i2 < ip.length; i2++) {
            if (first) {
                first = false;
            } else {
                temp.append(MediaMetrics.SEPARATOR);
            }
            temp.append(Integer.toString(ip[i2] & 255));
        }
        return temp.toString();
    }

    private String stringifyIPCollection(Set ips) {
        StringBuilder temp = new StringBuilder();
        temp.append(NavigationBarInflaterView.SIZE_MOD_START);
        Iterator it = ips.iterator();
        while (it.hasNext()) {
            if (temp.length() > 1) {
                temp.append(",");
            }
            temp.append(stringifyIP((byte[]) it.next()));
        }
        temp.append(NavigationBarInflaterView.SIZE_MOD_END);
        return temp.toString();
    }

    private String stringifyOtherNameCollection(Set otherNames) {
        StringBuilder temp = new StringBuilder();
        temp.append(NavigationBarInflaterView.SIZE_MOD_START);
        for (Object obj : otherNames) {
            if (temp.length() > 1) {
                temp.append(",");
            }
            OtherName name = OtherName.getInstance(obj);
            temp.append(name.getTypeID().getId());
            temp.append(":");
            try {
                temp.append(Hex.toHexString(name.getValue().toASN1Primitive().getEncoded()));
            } catch (IOException e) {
                temp.append(e.toString());
            }
        }
        temp.append(NavigationBarInflaterView.SIZE_MOD_END);
        return temp.toString();
    }

    private final void addLine(StringBuilder sb, String str) {
        sb.append(str).append(Strings.lineSeparator());
    }

    public String toString() {
        StringBuilder temp = new StringBuilder();
        addLine(temp, "permitted:");
        if (this.permittedSubtreesDN != null) {
            addLine(temp, "DN:");
            addLine(temp, this.permittedSubtreesDN.toString());
        }
        if (this.permittedSubtreesDNS != null) {
            addLine(temp, "DNS:");
            addLine(temp, this.permittedSubtreesDNS.toString());
        }
        if (this.permittedSubtreesEmail != null) {
            addLine(temp, "Email:");
            addLine(temp, this.permittedSubtreesEmail.toString());
        }
        if (this.permittedSubtreesURI != null) {
            addLine(temp, "URI:");
            addLine(temp, this.permittedSubtreesURI.toString());
        }
        if (this.permittedSubtreesIP != null) {
            addLine(temp, "IP:");
            addLine(temp, stringifyIPCollection(this.permittedSubtreesIP));
        }
        if (this.permittedSubtreesOtherName != null) {
            addLine(temp, "OtherName:");
            addLine(temp, stringifyOtherNameCollection(this.permittedSubtreesOtherName));
        }
        addLine(temp, "excluded:");
        if (!this.excludedSubtreesDN.isEmpty()) {
            addLine(temp, "DN:");
            addLine(temp, this.excludedSubtreesDN.toString());
        }
        if (!this.excludedSubtreesDNS.isEmpty()) {
            addLine(temp, "DNS:");
            addLine(temp, this.excludedSubtreesDNS.toString());
        }
        if (!this.excludedSubtreesEmail.isEmpty()) {
            addLine(temp, "Email:");
            addLine(temp, this.excludedSubtreesEmail.toString());
        }
        if (!this.excludedSubtreesURI.isEmpty()) {
            addLine(temp, "URI:");
            addLine(temp, this.excludedSubtreesURI.toString());
        }
        if (!this.excludedSubtreesIP.isEmpty()) {
            addLine(temp, "IP:");
            addLine(temp, stringifyIPCollection(this.excludedSubtreesIP));
        }
        if (!this.excludedSubtreesOtherName.isEmpty()) {
            addLine(temp, "OtherName:");
            addLine(temp, stringifyOtherNameCollection(this.excludedSubtreesOtherName));
        }
        return temp.toString();
    }
}
