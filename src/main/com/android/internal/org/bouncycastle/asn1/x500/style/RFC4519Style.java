package com.android.internal.org.bouncycastle.asn1.x500.style;

import android.app.blob.XmlTags;
import android.provider.Telephony;
import android.view.View;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERIA5String;
import com.android.internal.org.bouncycastle.asn1.DERPrintableString;
import com.android.internal.org.bouncycastle.asn1.x500.RDN;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle;
import java.util.Hashtable;
/* loaded from: classes4.dex */
public class RFC4519Style extends AbstractX500NameStyle {
    private static final Hashtable DefaultLookUp;
    private static final Hashtable DefaultSymbols;
    public static final X500NameStyle INSTANCE;
    public static final ASN1ObjectIdentifier businessCategory;

    /* renamed from: c */
    public static final ASN1ObjectIdentifier f611c;

    /* renamed from: cn */
    public static final ASN1ObjectIdentifier f612cn;

    /* renamed from: dc */
    public static final ASN1ObjectIdentifier f613dc;
    public static final ASN1ObjectIdentifier description;
    public static final ASN1ObjectIdentifier destinationIndicator;
    public static final ASN1ObjectIdentifier distinguishedName;
    public static final ASN1ObjectIdentifier dnQualifier;
    public static final ASN1ObjectIdentifier enhancedSearchGuide;
    public static final ASN1ObjectIdentifier facsimileTelephoneNumber;
    public static final ASN1ObjectIdentifier generationQualifier;
    public static final ASN1ObjectIdentifier givenName;
    public static final ASN1ObjectIdentifier houseIdentifier;
    public static final ASN1ObjectIdentifier initials;
    public static final ASN1ObjectIdentifier internationalISDNNumber;

    /* renamed from: l */
    public static final ASN1ObjectIdentifier f614l;
    public static final ASN1ObjectIdentifier member;
    public static final ASN1ObjectIdentifier name;

    /* renamed from: o */
    public static final ASN1ObjectIdentifier f615o;

    /* renamed from: ou */
    public static final ASN1ObjectIdentifier f616ou;
    public static final ASN1ObjectIdentifier owner;
    public static final ASN1ObjectIdentifier physicalDeliveryOfficeName;
    public static final ASN1ObjectIdentifier postOfficeBox;
    public static final ASN1ObjectIdentifier postalAddress;
    public static final ASN1ObjectIdentifier postalCode;
    public static final ASN1ObjectIdentifier preferredDeliveryMethod;
    public static final ASN1ObjectIdentifier registeredAddress;
    public static final ASN1ObjectIdentifier roleOccupant;
    public static final ASN1ObjectIdentifier searchGuide;
    public static final ASN1ObjectIdentifier seeAlso;
    public static final ASN1ObjectIdentifier serialNumber;

    /* renamed from: sn */
    public static final ASN1ObjectIdentifier f617sn;

    /* renamed from: st */
    public static final ASN1ObjectIdentifier f618st;
    public static final ASN1ObjectIdentifier street;
    public static final ASN1ObjectIdentifier telephoneNumber;
    public static final ASN1ObjectIdentifier teletexTerminalIdentifier;
    public static final ASN1ObjectIdentifier telexNumber;
    public static final ASN1ObjectIdentifier title;
    public static final ASN1ObjectIdentifier uid;
    public static final ASN1ObjectIdentifier uniqueMember;
    public static final ASN1ObjectIdentifier userPassword;
    public static final ASN1ObjectIdentifier x121Address;
    public static final ASN1ObjectIdentifier x500UniqueIdentifier;
    protected final Hashtable defaultSymbols = copyHashTable(DefaultSymbols);
    protected final Hashtable defaultLookUp = copyHashTable(DefaultLookUp);

    static {
        ASN1ObjectIdentifier intern = new ASN1ObjectIdentifier("2.5.4.15").intern();
        businessCategory = intern;
        ASN1ObjectIdentifier intern2 = new ASN1ObjectIdentifier("2.5.4.6").intern();
        f611c = intern2;
        ASN1ObjectIdentifier intern3 = new ASN1ObjectIdentifier("2.5.4.3").intern();
        f612cn = intern3;
        ASN1ObjectIdentifier intern4 = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25").intern();
        f613dc = intern4;
        ASN1ObjectIdentifier intern5 = new ASN1ObjectIdentifier("2.5.4.13").intern();
        description = intern5;
        ASN1ObjectIdentifier intern6 = new ASN1ObjectIdentifier("2.5.4.27").intern();
        destinationIndicator = intern6;
        ASN1ObjectIdentifier intern7 = new ASN1ObjectIdentifier("2.5.4.49").intern();
        distinguishedName = intern7;
        ASN1ObjectIdentifier intern8 = new ASN1ObjectIdentifier("2.5.4.46").intern();
        dnQualifier = intern8;
        ASN1ObjectIdentifier intern9 = new ASN1ObjectIdentifier("2.5.4.47").intern();
        enhancedSearchGuide = intern9;
        ASN1ObjectIdentifier intern10 = new ASN1ObjectIdentifier("2.5.4.23").intern();
        facsimileTelephoneNumber = intern10;
        ASN1ObjectIdentifier intern11 = new ASN1ObjectIdentifier("2.5.4.44").intern();
        generationQualifier = intern11;
        ASN1ObjectIdentifier intern12 = new ASN1ObjectIdentifier("2.5.4.42").intern();
        givenName = intern12;
        ASN1ObjectIdentifier intern13 = new ASN1ObjectIdentifier("2.5.4.51").intern();
        houseIdentifier = intern13;
        ASN1ObjectIdentifier intern14 = new ASN1ObjectIdentifier("2.5.4.43").intern();
        initials = intern14;
        ASN1ObjectIdentifier intern15 = new ASN1ObjectIdentifier("2.5.4.25").intern();
        internationalISDNNumber = intern15;
        ASN1ObjectIdentifier intern16 = new ASN1ObjectIdentifier("2.5.4.7").intern();
        f614l = intern16;
        ASN1ObjectIdentifier intern17 = new ASN1ObjectIdentifier("2.5.4.31").intern();
        member = intern17;
        ASN1ObjectIdentifier intern18 = new ASN1ObjectIdentifier("2.5.4.41").intern();
        name = intern18;
        ASN1ObjectIdentifier intern19 = new ASN1ObjectIdentifier("2.5.4.10").intern();
        f615o = intern19;
        ASN1ObjectIdentifier intern20 = new ASN1ObjectIdentifier("2.5.4.11").intern();
        f616ou = intern20;
        ASN1ObjectIdentifier intern21 = new ASN1ObjectIdentifier("2.5.4.32").intern();
        owner = intern21;
        ASN1ObjectIdentifier intern22 = new ASN1ObjectIdentifier("2.5.4.19").intern();
        physicalDeliveryOfficeName = intern22;
        ASN1ObjectIdentifier intern23 = new ASN1ObjectIdentifier("2.5.4.16").intern();
        postalAddress = intern23;
        ASN1ObjectIdentifier intern24 = new ASN1ObjectIdentifier("2.5.4.17").intern();
        postalCode = intern24;
        ASN1ObjectIdentifier intern25 = new ASN1ObjectIdentifier("2.5.4.18").intern();
        postOfficeBox = intern25;
        ASN1ObjectIdentifier intern26 = new ASN1ObjectIdentifier("2.5.4.28").intern();
        preferredDeliveryMethod = intern26;
        ASN1ObjectIdentifier intern27 = new ASN1ObjectIdentifier("2.5.4.26").intern();
        registeredAddress = intern27;
        ASN1ObjectIdentifier intern28 = new ASN1ObjectIdentifier("2.5.4.33").intern();
        roleOccupant = intern28;
        ASN1ObjectIdentifier intern29 = new ASN1ObjectIdentifier("2.5.4.14").intern();
        searchGuide = intern29;
        ASN1ObjectIdentifier intern30 = new ASN1ObjectIdentifier("2.5.4.34").intern();
        seeAlso = intern30;
        ASN1ObjectIdentifier intern31 = new ASN1ObjectIdentifier("2.5.4.5").intern();
        serialNumber = intern31;
        ASN1ObjectIdentifier intern32 = new ASN1ObjectIdentifier("2.5.4.4").intern();
        f617sn = intern32;
        ASN1ObjectIdentifier intern33 = new ASN1ObjectIdentifier("2.5.4.8").intern();
        f618st = intern33;
        ASN1ObjectIdentifier intern34 = new ASN1ObjectIdentifier("2.5.4.9").intern();
        street = intern34;
        ASN1ObjectIdentifier intern35 = new ASN1ObjectIdentifier("2.5.4.20").intern();
        telephoneNumber = intern35;
        ASN1ObjectIdentifier intern36 = new ASN1ObjectIdentifier("2.5.4.22").intern();
        teletexTerminalIdentifier = intern36;
        ASN1ObjectIdentifier intern37 = new ASN1ObjectIdentifier("2.5.4.21").intern();
        telexNumber = intern37;
        ASN1ObjectIdentifier intern38 = new ASN1ObjectIdentifier("2.5.4.12").intern();
        title = intern38;
        ASN1ObjectIdentifier intern39 = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1").intern();
        uid = intern39;
        ASN1ObjectIdentifier intern40 = new ASN1ObjectIdentifier("2.5.4.50").intern();
        uniqueMember = intern40;
        ASN1ObjectIdentifier intern41 = new ASN1ObjectIdentifier("2.5.4.35").intern();
        userPassword = intern41;
        ASN1ObjectIdentifier intern42 = new ASN1ObjectIdentifier("2.5.4.24").intern();
        x121Address = intern42;
        ASN1ObjectIdentifier intern43 = new ASN1ObjectIdentifier("2.5.4.45").intern();
        x500UniqueIdentifier = intern43;
        Hashtable hashtable = new Hashtable();
        DefaultSymbols = hashtable;
        Hashtable hashtable2 = new Hashtable();
        DefaultLookUp = hashtable2;
        hashtable.put(intern, "businessCategory");
        hashtable.put(intern2, "c");
        hashtable.put(intern3, "cn");
        hashtable.put(intern4, "dc");
        hashtable.put(intern5, "description");
        hashtable.put(intern6, "destinationIndicator");
        hashtable.put(intern7, "distinguishedName");
        hashtable.put(intern8, "dnQualifier");
        hashtable.put(intern9, "enhancedSearchGuide");
        hashtable.put(intern10, "facsimileTelephoneNumber");
        hashtable.put(intern11, "generationQualifier");
        hashtable.put(intern12, "givenName");
        hashtable.put(intern13, "houseIdentifier");
        hashtable.put(intern14, "initials");
        hashtable.put(intern15, "internationalISDNNumber");
        hashtable.put(intern16, XmlTags.TAG_LEASEE);
        hashtable.put(intern17, "member");
        hashtable.put(intern18, "name");
        hashtable.put(intern19, "o");
        hashtable.put(intern20, "ou");
        hashtable.put(intern21, "owner");
        hashtable.put(intern22, "physicalDeliveryOfficeName");
        hashtable.put(intern23, View.AUTOFILL_HINT_POSTAL_ADDRESS);
        hashtable.put(intern24, View.AUTOFILL_HINT_POSTAL_CODE);
        hashtable.put(intern25, "postOfficeBox");
        hashtable.put(intern26, "preferredDeliveryMethod");
        hashtable.put(intern27, "registeredAddress");
        hashtable.put(intern28, "roleOccupant");
        hashtable.put(intern29, "searchGuide");
        hashtable.put(intern30, "seeAlso");
        hashtable.put(intern31, "serialNumber");
        hashtable.put(intern32, "sn");
        hashtable.put(intern33, Telephony.BaseMmsColumns.STATUS);
        hashtable.put(intern34, "street");
        hashtable.put(intern35, "telephoneNumber");
        hashtable.put(intern36, "teletexTerminalIdentifier");
        hashtable.put(intern37, "telexNumber");
        hashtable.put(intern38, "title");
        hashtable.put(intern39, "uid");
        hashtable.put(intern40, "uniqueMember");
        hashtable.put(intern41, "userPassword");
        hashtable.put(intern42, "x121Address");
        hashtable.put(intern43, "x500UniqueIdentifier");
        hashtable2.put("businesscategory", intern);
        hashtable2.put("c", intern2);
        hashtable2.put("cn", intern3);
        hashtable2.put("dc", intern4);
        hashtable2.put("description", intern5);
        hashtable2.put("destinationindicator", intern6);
        hashtable2.put("distinguishedname", intern7);
        hashtable2.put("dnqualifier", intern8);
        hashtable2.put("enhancedsearchguide", intern9);
        hashtable2.put("facsimiletelephonenumber", intern10);
        hashtable2.put("generationqualifier", intern11);
        hashtable2.put("givenname", intern12);
        hashtable2.put("houseidentifier", intern13);
        hashtable2.put("initials", intern14);
        hashtable2.put("internationalisdnnumber", intern15);
        hashtable2.put(XmlTags.TAG_LEASEE, intern16);
        hashtable2.put("member", intern17);
        hashtable2.put("name", intern18);
        hashtable2.put("o", intern19);
        hashtable2.put("ou", intern20);
        hashtable2.put("owner", intern21);
        hashtable2.put("physicaldeliveryofficename", intern22);
        hashtable2.put("postaladdress", intern23);
        hashtable2.put("postalcode", intern24);
        hashtable2.put("postofficebox", intern25);
        hashtable2.put("preferreddeliverymethod", intern26);
        hashtable2.put("registeredaddress", intern27);
        hashtable2.put("roleoccupant", intern28);
        hashtable2.put("searchguide", intern29);
        hashtable2.put("seealso", intern30);
        hashtable2.put("serialnumber", intern31);
        hashtable2.put("sn", intern32);
        hashtable2.put(Telephony.BaseMmsColumns.STATUS, intern33);
        hashtable2.put("street", intern34);
        hashtable2.put("telephonenumber", intern35);
        hashtable2.put("teletexterminalidentifier", intern36);
        hashtable2.put("telexnumber", intern37);
        hashtable2.put("title", intern38);
        hashtable2.put("uid", intern39);
        hashtable2.put("uniquemember", intern40);
        hashtable2.put("userpassword", intern41);
        hashtable2.put("x121address", intern42);
        hashtable2.put("x500uniqueidentifier", intern43);
        INSTANCE = new RFC4519Style();
    }

    protected RFC4519Style() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.asn1.x500.style.AbstractX500NameStyle
    public ASN1Encodable encodeStringValue(ASN1ObjectIdentifier oid, String value) {
        if (oid.equals((ASN1Primitive) f613dc)) {
            return new DERIA5String(value);
        }
        if (oid.equals((ASN1Primitive) f611c) || oid.equals((ASN1Primitive) serialNumber) || oid.equals((ASN1Primitive) dnQualifier) || oid.equals((ASN1Primitive) telephoneNumber)) {
            return new DERPrintableString(value);
        }
        return super.encodeStringValue(oid, value);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public String oidToDisplayName(ASN1ObjectIdentifier oid) {
        return (String) DefaultSymbols.get(oid);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public String[] oidToAttrNames(ASN1ObjectIdentifier oid) {
        return IETFUtils.findAttrNamesForOID(oid, this.defaultLookUp);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public ASN1ObjectIdentifier attrNameToOID(String attrName) {
        return IETFUtils.decodeAttrName(attrName, this.defaultLookUp);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public RDN[] fromString(String dirName) {
        RDN[] tmp = IETFUtils.rDNsFromString(dirName, this);
        RDN[] res = new RDN[tmp.length];
        for (int i = 0; i != tmp.length; i++) {
            res[(res.length - i) - 1] = tmp[i];
        }
        return res;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public String toString(X500Name name2) {
        StringBuffer buf = new StringBuffer();
        boolean first = true;
        RDN[] rdns = name2.getRDNs();
        for (int i = rdns.length - 1; i >= 0; i--) {
            if (first) {
                first = false;
            } else {
                buf.append(',');
            }
            IETFUtils.appendRDN(buf, rdns[i], this.defaultSymbols);
        }
        return buf.toString();
    }
}
