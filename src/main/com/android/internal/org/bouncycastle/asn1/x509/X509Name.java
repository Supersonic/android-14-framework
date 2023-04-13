package com.android.internal.org.bouncycastle.asn1.x509;

import android.app.blob.XmlTags;
import android.hardware.gnss.GnssSignalType;
import android.provider.Telephony;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.ASN1String;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERSet;
import com.android.internal.org.bouncycastle.asn1.DERUniversalString;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.util.Strings;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
/* loaded from: classes4.dex */
public class X509Name extends ASN1Object {
    public static final ASN1ObjectIdentifier BUSINESS_CATEGORY;

    /* renamed from: C */
    public static final ASN1ObjectIdentifier f625C;

    /* renamed from: CN */
    public static final ASN1ObjectIdentifier f626CN;
    public static final ASN1ObjectIdentifier COUNTRY_OF_CITIZENSHIP;
    public static final ASN1ObjectIdentifier COUNTRY_OF_RESIDENCE;
    public static final ASN1ObjectIdentifier DATE_OF_BIRTH;

    /* renamed from: DC */
    public static final ASN1ObjectIdentifier f627DC;
    public static final ASN1ObjectIdentifier DMD_NAME;
    public static final ASN1ObjectIdentifier DN_QUALIFIER;
    public static final Hashtable DefaultLookUp;
    public static boolean DefaultReverse;
    public static final Hashtable DefaultSymbols;

    /* renamed from: E */
    public static final ASN1ObjectIdentifier f628E;
    public static final ASN1ObjectIdentifier EmailAddress;
    private static final Boolean FALSE;
    public static final ASN1ObjectIdentifier GENDER;
    public static final ASN1ObjectIdentifier GENERATION;
    public static final ASN1ObjectIdentifier GIVENNAME;
    public static final ASN1ObjectIdentifier INITIALS;

    /* renamed from: L */
    public static final ASN1ObjectIdentifier f629L;
    public static final ASN1ObjectIdentifier NAME;
    public static final ASN1ObjectIdentifier NAME_AT_BIRTH;

    /* renamed from: O */
    public static final ASN1ObjectIdentifier f630O;
    public static final Hashtable OIDLookUp;

    /* renamed from: OU */
    public static final ASN1ObjectIdentifier f631OU;
    public static final ASN1ObjectIdentifier PLACE_OF_BIRTH;
    public static final ASN1ObjectIdentifier POSTAL_ADDRESS;
    public static final ASN1ObjectIdentifier POSTAL_CODE;
    public static final ASN1ObjectIdentifier PSEUDONYM;
    public static final Hashtable RFC1779Symbols;
    public static final Hashtable RFC2253Symbols;
    public static final ASN1ObjectIdentifier SERIALNUMBER;

    /* renamed from: SN */
    public static final ASN1ObjectIdentifier f632SN;

    /* renamed from: ST */
    public static final ASN1ObjectIdentifier f633ST;
    public static final ASN1ObjectIdentifier STREET;
    public static final ASN1ObjectIdentifier SURNAME;
    public static final Hashtable SymbolLookUp;

    /* renamed from: T */
    public static final ASN1ObjectIdentifier f634T;
    public static final ASN1ObjectIdentifier TELEPHONE_NUMBER;
    private static final Boolean TRUE;
    public static final ASN1ObjectIdentifier UID;
    public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER;
    public static final ASN1ObjectIdentifier UnstructuredAddress;
    public static final ASN1ObjectIdentifier UnstructuredName;
    private Vector added;
    private X509NameEntryConverter converter;
    private int hashCodeValue;
    private boolean isHashCodeCalculated;
    private Vector ordering;
    private ASN1Sequence seq;
    private Vector values;

    static {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier("2.5.4.6");
        f625C = aSN1ObjectIdentifier;
        ASN1ObjectIdentifier aSN1ObjectIdentifier2 = new ASN1ObjectIdentifier("2.5.4.10");
        f630O = aSN1ObjectIdentifier2;
        ASN1ObjectIdentifier aSN1ObjectIdentifier3 = new ASN1ObjectIdentifier("2.5.4.11");
        f631OU = aSN1ObjectIdentifier3;
        ASN1ObjectIdentifier aSN1ObjectIdentifier4 = new ASN1ObjectIdentifier("2.5.4.12");
        f634T = aSN1ObjectIdentifier4;
        ASN1ObjectIdentifier aSN1ObjectIdentifier5 = new ASN1ObjectIdentifier("2.5.4.3");
        f626CN = aSN1ObjectIdentifier5;
        ASN1ObjectIdentifier aSN1ObjectIdentifier6 = new ASN1ObjectIdentifier("2.5.4.5");
        f632SN = aSN1ObjectIdentifier6;
        ASN1ObjectIdentifier aSN1ObjectIdentifier7 = new ASN1ObjectIdentifier("2.5.4.9");
        STREET = aSN1ObjectIdentifier7;
        SERIALNUMBER = aSN1ObjectIdentifier6;
        ASN1ObjectIdentifier aSN1ObjectIdentifier8 = new ASN1ObjectIdentifier("2.5.4.7");
        f629L = aSN1ObjectIdentifier8;
        ASN1ObjectIdentifier aSN1ObjectIdentifier9 = new ASN1ObjectIdentifier("2.5.4.8");
        f633ST = aSN1ObjectIdentifier9;
        ASN1ObjectIdentifier aSN1ObjectIdentifier10 = new ASN1ObjectIdentifier("2.5.4.4");
        SURNAME = aSN1ObjectIdentifier10;
        ASN1ObjectIdentifier aSN1ObjectIdentifier11 = new ASN1ObjectIdentifier("2.5.4.42");
        GIVENNAME = aSN1ObjectIdentifier11;
        ASN1ObjectIdentifier aSN1ObjectIdentifier12 = new ASN1ObjectIdentifier("2.5.4.43");
        INITIALS = aSN1ObjectIdentifier12;
        ASN1ObjectIdentifier aSN1ObjectIdentifier13 = new ASN1ObjectIdentifier("2.5.4.44");
        GENERATION = aSN1ObjectIdentifier13;
        ASN1ObjectIdentifier aSN1ObjectIdentifier14 = new ASN1ObjectIdentifier("2.5.4.45");
        UNIQUE_IDENTIFIER = aSN1ObjectIdentifier14;
        ASN1ObjectIdentifier aSN1ObjectIdentifier15 = new ASN1ObjectIdentifier("2.5.4.15");
        BUSINESS_CATEGORY = aSN1ObjectIdentifier15;
        ASN1ObjectIdentifier aSN1ObjectIdentifier16 = new ASN1ObjectIdentifier("2.5.4.17");
        POSTAL_CODE = aSN1ObjectIdentifier16;
        ASN1ObjectIdentifier aSN1ObjectIdentifier17 = new ASN1ObjectIdentifier("2.5.4.46");
        DN_QUALIFIER = aSN1ObjectIdentifier17;
        ASN1ObjectIdentifier aSN1ObjectIdentifier18 = new ASN1ObjectIdentifier("2.5.4.65");
        PSEUDONYM = aSN1ObjectIdentifier18;
        ASN1ObjectIdentifier aSN1ObjectIdentifier19 = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.1");
        DATE_OF_BIRTH = aSN1ObjectIdentifier19;
        ASN1ObjectIdentifier aSN1ObjectIdentifier20 = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.2");
        PLACE_OF_BIRTH = aSN1ObjectIdentifier20;
        ASN1ObjectIdentifier aSN1ObjectIdentifier21 = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.3");
        GENDER = aSN1ObjectIdentifier21;
        ASN1ObjectIdentifier aSN1ObjectIdentifier22 = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.4");
        COUNTRY_OF_CITIZENSHIP = aSN1ObjectIdentifier22;
        ASN1ObjectIdentifier aSN1ObjectIdentifier23 = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.9.5");
        COUNTRY_OF_RESIDENCE = aSN1ObjectIdentifier23;
        ASN1ObjectIdentifier aSN1ObjectIdentifier24 = new ASN1ObjectIdentifier("1.3.36.8.3.14");
        NAME_AT_BIRTH = aSN1ObjectIdentifier24;
        ASN1ObjectIdentifier aSN1ObjectIdentifier25 = new ASN1ObjectIdentifier("2.5.4.16");
        POSTAL_ADDRESS = aSN1ObjectIdentifier25;
        DMD_NAME = new ASN1ObjectIdentifier("2.5.4.54");
        ASN1ObjectIdentifier aSN1ObjectIdentifier26 = X509ObjectIdentifiers.id_at_telephoneNumber;
        TELEPHONE_NUMBER = aSN1ObjectIdentifier26;
        ASN1ObjectIdentifier aSN1ObjectIdentifier27 = X509ObjectIdentifiers.id_at_name;
        NAME = aSN1ObjectIdentifier27;
        ASN1ObjectIdentifier aSN1ObjectIdentifier28 = PKCSObjectIdentifiers.pkcs_9_at_emailAddress;
        EmailAddress = aSN1ObjectIdentifier28;
        ASN1ObjectIdentifier aSN1ObjectIdentifier29 = PKCSObjectIdentifiers.pkcs_9_at_unstructuredName;
        UnstructuredName = aSN1ObjectIdentifier29;
        ASN1ObjectIdentifier aSN1ObjectIdentifier30 = PKCSObjectIdentifiers.pkcs_9_at_unstructuredAddress;
        UnstructuredAddress = aSN1ObjectIdentifier30;
        f628E = aSN1ObjectIdentifier28;
        ASN1ObjectIdentifier aSN1ObjectIdentifier31 = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");
        f627DC = aSN1ObjectIdentifier31;
        ASN1ObjectIdentifier aSN1ObjectIdentifier32 = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");
        UID = aSN1ObjectIdentifier32;
        DefaultReverse = false;
        Hashtable hashtable = new Hashtable();
        DefaultSymbols = hashtable;
        Hashtable hashtable2 = new Hashtable();
        RFC2253Symbols = hashtable2;
        Hashtable hashtable3 = new Hashtable();
        RFC1779Symbols = hashtable3;
        Hashtable hashtable4 = new Hashtable();
        DefaultLookUp = hashtable4;
        OIDLookUp = hashtable;
        SymbolLookUp = hashtable4;
        TRUE = Boolean.TRUE;
        FALSE = Boolean.FALSE;
        hashtable.put(aSN1ObjectIdentifier, GnssSignalType.CODE_TYPE_C);
        hashtable.put(aSN1ObjectIdentifier2, "O");
        hashtable.put(aSN1ObjectIdentifier4, "T");
        hashtable.put(aSN1ObjectIdentifier3, "OU");
        hashtable.put(aSN1ObjectIdentifier5, "CN");
        hashtable.put(aSN1ObjectIdentifier8, GnssSignalType.CODE_TYPE_L);
        hashtable.put(aSN1ObjectIdentifier9, "ST");
        hashtable.put(aSN1ObjectIdentifier6, "SERIALNUMBER");
        hashtable.put(aSN1ObjectIdentifier28, "E");
        hashtable.put(aSN1ObjectIdentifier31, "DC");
        hashtable.put(aSN1ObjectIdentifier32, "UID");
        hashtable.put(aSN1ObjectIdentifier7, "STREET");
        hashtable.put(aSN1ObjectIdentifier10, "SURNAME");
        hashtable.put(aSN1ObjectIdentifier11, "GIVENNAME");
        hashtable.put(aSN1ObjectIdentifier12, "INITIALS");
        hashtable.put(aSN1ObjectIdentifier13, "GENERATION");
        hashtable.put(aSN1ObjectIdentifier30, "unstructuredAddress");
        hashtable.put(aSN1ObjectIdentifier29, "unstructuredName");
        hashtable.put(aSN1ObjectIdentifier14, "UniqueIdentifier");
        hashtable.put(aSN1ObjectIdentifier17, "DN");
        hashtable.put(aSN1ObjectIdentifier18, "Pseudonym");
        hashtable.put(aSN1ObjectIdentifier25, "PostalAddress");
        hashtable.put(aSN1ObjectIdentifier24, "NameAtBirth");
        hashtable.put(aSN1ObjectIdentifier22, "CountryOfCitizenship");
        hashtable.put(aSN1ObjectIdentifier23, "CountryOfResidence");
        hashtable.put(aSN1ObjectIdentifier21, "Gender");
        hashtable.put(aSN1ObjectIdentifier20, "PlaceOfBirth");
        hashtable.put(aSN1ObjectIdentifier19, "DateOfBirth");
        hashtable.put(aSN1ObjectIdentifier16, "PostalCode");
        hashtable.put(aSN1ObjectIdentifier15, "BusinessCategory");
        hashtable.put(aSN1ObjectIdentifier26, "TelephoneNumber");
        hashtable.put(aSN1ObjectIdentifier27, "Name");
        hashtable2.put(aSN1ObjectIdentifier, GnssSignalType.CODE_TYPE_C);
        hashtable2.put(aSN1ObjectIdentifier2, "O");
        hashtable2.put(aSN1ObjectIdentifier3, "OU");
        hashtable2.put(aSN1ObjectIdentifier5, "CN");
        hashtable2.put(aSN1ObjectIdentifier8, GnssSignalType.CODE_TYPE_L);
        hashtable2.put(aSN1ObjectIdentifier9, "ST");
        hashtable2.put(aSN1ObjectIdentifier7, "STREET");
        hashtable2.put(aSN1ObjectIdentifier31, "DC");
        hashtable2.put(aSN1ObjectIdentifier32, "UID");
        hashtable3.put(aSN1ObjectIdentifier, GnssSignalType.CODE_TYPE_C);
        hashtable3.put(aSN1ObjectIdentifier2, "O");
        hashtable3.put(aSN1ObjectIdentifier3, "OU");
        hashtable3.put(aSN1ObjectIdentifier5, "CN");
        hashtable3.put(aSN1ObjectIdentifier8, GnssSignalType.CODE_TYPE_L);
        hashtable3.put(aSN1ObjectIdentifier9, "ST");
        hashtable3.put(aSN1ObjectIdentifier7, "STREET");
        hashtable4.put("c", aSN1ObjectIdentifier);
        hashtable4.put("o", aSN1ObjectIdentifier2);
        hashtable4.put("t", aSN1ObjectIdentifier4);
        hashtable4.put("ou", aSN1ObjectIdentifier3);
        hashtable4.put("cn", aSN1ObjectIdentifier5);
        hashtable4.put(XmlTags.TAG_LEASEE, aSN1ObjectIdentifier8);
        hashtable4.put(Telephony.BaseMmsColumns.STATUS, aSN1ObjectIdentifier9);
        hashtable4.put("sn", aSN1ObjectIdentifier6);
        hashtable4.put("serialnumber", aSN1ObjectIdentifier6);
        hashtable4.put("street", aSN1ObjectIdentifier7);
        hashtable4.put("emailaddress", aSN1ObjectIdentifier28);
        hashtable4.put("dc", aSN1ObjectIdentifier31);
        hashtable4.put("e", aSN1ObjectIdentifier28);
        hashtable4.put("uid", aSN1ObjectIdentifier32);
        hashtable4.put("surname", aSN1ObjectIdentifier10);
        hashtable4.put("givenname", aSN1ObjectIdentifier11);
        hashtable4.put("initials", aSN1ObjectIdentifier12);
        hashtable4.put("generation", aSN1ObjectIdentifier13);
        hashtable4.put("unstructuredaddress", aSN1ObjectIdentifier30);
        hashtable4.put("unstructuredname", aSN1ObjectIdentifier29);
        hashtable4.put("uniqueidentifier", aSN1ObjectIdentifier14);
        hashtable4.put("dn", aSN1ObjectIdentifier17);
        hashtable4.put("pseudonym", aSN1ObjectIdentifier18);
        hashtable4.put("postaladdress", aSN1ObjectIdentifier25);
        hashtable4.put("nameofbirth", aSN1ObjectIdentifier24);
        hashtable4.put("countryofcitizenship", aSN1ObjectIdentifier22);
        hashtable4.put("countryofresidence", aSN1ObjectIdentifier23);
        hashtable4.put("gender", aSN1ObjectIdentifier21);
        hashtable4.put("placeofbirth", aSN1ObjectIdentifier20);
        hashtable4.put("dateofbirth", aSN1ObjectIdentifier19);
        hashtable4.put("postalcode", aSN1ObjectIdentifier16);
        hashtable4.put("businesscategory", aSN1ObjectIdentifier15);
        hashtable4.put("telephonenumber", aSN1ObjectIdentifier26);
        hashtable4.put("name", aSN1ObjectIdentifier27);
    }

    public static X509Name getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static X509Name getInstance(Object obj) {
        if (obj instanceof X509Name) {
            return (X509Name) obj;
        }
        if (obj instanceof X500Name) {
            return new X509Name(ASN1Sequence.getInstance(((X500Name) obj).toASN1Primitive()));
        }
        if (obj != null) {
            return new X509Name(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    protected X509Name() {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
    }

    public X509Name(ASN1Sequence seq) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.seq = seq;
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1Set set = ASN1Set.getInstance(((ASN1Encodable) e.nextElement()).toASN1Primitive());
            int i = 0;
            while (i < set.size()) {
                ASN1Sequence s = ASN1Sequence.getInstance(set.getObjectAt(i).toASN1Primitive());
                if (s.size() != 2) {
                    throw new IllegalArgumentException("badly sized pair");
                }
                this.ordering.addElement(ASN1ObjectIdentifier.getInstance(s.getObjectAt(0)));
                ASN1Encodable value = s.getObjectAt(1);
                if ((value instanceof ASN1String) && !(value instanceof DERUniversalString)) {
                    String v = ((ASN1String) value).getString();
                    if (v.length() > 0 && v.charAt(0) == '#') {
                        this.values.addElement("\\" + v);
                    } else {
                        this.values.addElement(v);
                    }
                } else {
                    try {
                        this.values.addElement("#" + bytesToString(Hex.encode(value.toASN1Primitive().getEncoded(ASN1Encoding.DER))));
                    } catch (IOException e2) {
                        throw new IllegalArgumentException("cannot encode value");
                    }
                }
                this.added.addElement(i != 0 ? TRUE : FALSE);
                i++;
            }
        }
    }

    public X509Name(Hashtable attributes) {
        this((Vector) null, attributes);
    }

    public X509Name(Vector ordering, Hashtable attributes) {
        this(ordering, attributes, new X509DefaultEntryConverter());
    }

    public X509Name(Vector ordering, Hashtable attributes, X509NameEntryConverter converter) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.converter = converter;
        if (ordering != null) {
            for (int i = 0; i != ordering.size(); i++) {
                this.ordering.addElement(ordering.elementAt(i));
                this.added.addElement(FALSE);
            }
        } else {
            Enumeration e = attributes.keys();
            while (e.hasMoreElements()) {
                this.ordering.addElement(e.nextElement());
                this.added.addElement(FALSE);
            }
        }
        for (int i2 = 0; i2 != this.ordering.size(); i2++) {
            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) this.ordering.elementAt(i2);
            if (attributes.get(oid) == null) {
                throw new IllegalArgumentException("No attribute for object id - " + oid.getId() + " - passed to distinguished name");
            }
            this.values.addElement(attributes.get(oid));
        }
    }

    public X509Name(Vector oids, Vector values) {
        this(oids, values, new X509DefaultEntryConverter());
    }

    public X509Name(Vector oids, Vector values, X509NameEntryConverter converter) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.converter = converter;
        if (oids.size() != values.size()) {
            throw new IllegalArgumentException("oids vector must be same length as values.");
        }
        for (int i = 0; i < oids.size(); i++) {
            this.ordering.addElement(oids.elementAt(i));
            this.values.addElement(values.elementAt(i));
            this.added.addElement(FALSE);
        }
    }

    public X509Name(String dirName) {
        this(DefaultReverse, DefaultLookUp, dirName);
    }

    public X509Name(String dirName, X509NameEntryConverter converter) {
        this(DefaultReverse, DefaultLookUp, dirName, converter);
    }

    public X509Name(boolean reverse, String dirName) {
        this(reverse, DefaultLookUp, dirName);
    }

    public X509Name(boolean reverse, String dirName, X509NameEntryConverter converter) {
        this(reverse, DefaultLookUp, dirName, converter);
    }

    public X509Name(boolean reverse, Hashtable lookUp, String dirName) {
        this(reverse, lookUp, dirName, new X509DefaultEntryConverter());
    }

    private ASN1ObjectIdentifier decodeOID(String name, Hashtable lookUp) {
        String name2 = name.trim();
        if (Strings.toUpperCase(name2).startsWith("OID.")) {
            return new ASN1ObjectIdentifier(name2.substring(4));
        }
        if (name2.charAt(0) >= '0' && name2.charAt(0) <= '9') {
            return new ASN1ObjectIdentifier(name2);
        }
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) lookUp.get(Strings.toLowerCase(name2));
        if (oid == null) {
            throw new IllegalArgumentException("Unknown object id - " + name2 + " - passed to distinguished name");
        }
        return oid;
    }

    private String unescape(String elt) {
        if (elt.length() == 0 || (elt.indexOf(92) < 0 && elt.indexOf(34) < 0)) {
            return elt.trim();
        }
        char[] elts = elt.toCharArray();
        boolean escaped = false;
        boolean quoted = false;
        StringBuffer buf = new StringBuffer(elt.length());
        int start = 0;
        if (elts[0] == '\\' && elts[1] == '#') {
            start = 2;
            buf.append("\\#");
        }
        boolean nonWhiteSpaceEncountered = false;
        int lastEscaped = 0;
        for (int i = start; i != elts.length; i++) {
            char c = elts[i];
            if (c != ' ') {
                nonWhiteSpaceEncountered = true;
            }
            if (c == '\"') {
                if (!escaped) {
                    quoted = !quoted;
                } else {
                    buf.append(c);
                }
                escaped = false;
            } else if (c == '\\' && !escaped && !quoted) {
                escaped = true;
                lastEscaped = buf.length();
            } else if (c != ' ' || escaped || nonWhiteSpaceEncountered) {
                buf.append(c);
                escaped = false;
            }
        }
        if (buf.length() > 0) {
            while (buf.charAt(buf.length() - 1) == ' ' && lastEscaped != buf.length() - 1) {
                buf.setLength(buf.length() - 1);
            }
        }
        return buf.toString();
    }

    public X509Name(boolean reverse, Hashtable lookUp, String dirName, X509NameEntryConverter converter) {
        this.converter = null;
        this.ordering = new Vector();
        this.values = new Vector();
        this.added = new Vector();
        this.converter = converter;
        X509NameTokenizer nTok = new X509NameTokenizer(dirName);
        while (nTok.hasMoreTokens()) {
            String token = nTok.nextToken();
            if (token.indexOf(43) > 0) {
                X509NameTokenizer pTok = new X509NameTokenizer(token, '+');
                addEntry(lookUp, pTok.nextToken(), FALSE);
                while (pTok.hasMoreTokens()) {
                    addEntry(lookUp, pTok.nextToken(), TRUE);
                }
            } else {
                addEntry(lookUp, token, FALSE);
            }
        }
        if (reverse) {
            Vector o = new Vector();
            Vector v = new Vector();
            Vector a = new Vector();
            int count = 1;
            for (int i = 0; i < this.ordering.size(); i++) {
                if (((Boolean) this.added.elementAt(i)).booleanValue()) {
                    o.insertElementAt(this.ordering.elementAt(i), count);
                    v.insertElementAt(this.values.elementAt(i), count);
                    a.insertElementAt(this.added.elementAt(i), count);
                    count++;
                } else {
                    o.insertElementAt(this.ordering.elementAt(i), 0);
                    v.insertElementAt(this.values.elementAt(i), 0);
                    a.insertElementAt(this.added.elementAt(i), 0);
                    count = 1;
                }
            }
            this.ordering = o;
            this.values = v;
            this.added = a;
        }
    }

    private void addEntry(Hashtable lookUp, String token, Boolean isAdded) {
        X509NameTokenizer vTok = new X509NameTokenizer(token, '=');
        String name = vTok.nextToken();
        if (!vTok.hasMoreTokens()) {
            throw new IllegalArgumentException("badly formatted directory string");
        }
        String value = vTok.nextToken();
        ASN1ObjectIdentifier oid = decodeOID(name, lookUp);
        this.ordering.addElement(oid);
        this.values.addElement(unescape(value));
        this.added.addElement(isAdded);
    }

    public Vector getOIDs() {
        Vector v = new Vector();
        for (int i = 0; i != this.ordering.size(); i++) {
            v.addElement(this.ordering.elementAt(i));
        }
        return v;
    }

    public Vector getValues() {
        Vector v = new Vector();
        for (int i = 0; i != this.values.size(); i++) {
            v.addElement(this.values.elementAt(i));
        }
        return v;
    }

    public Vector getValues(ASN1ObjectIdentifier oid) {
        Vector v = new Vector();
        for (int i = 0; i != this.values.size(); i++) {
            if (this.ordering.elementAt(i).equals(oid)) {
                String val = (String) this.values.elementAt(i);
                if (val.length() > 2 && val.charAt(0) == '\\' && val.charAt(1) == '#') {
                    v.addElement(val.substring(1));
                } else {
                    v.addElement(val);
                }
            }
        }
        return v;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        if (this.seq == null) {
            ASN1EncodableVector vec = new ASN1EncodableVector();
            ASN1EncodableVector sVec = new ASN1EncodableVector();
            ASN1ObjectIdentifier lstOid = null;
            for (int i = 0; i != this.ordering.size(); i++) {
                ASN1EncodableVector v = new ASN1EncodableVector(2);
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) this.ordering.elementAt(i);
                v.add(oid);
                String str = (String) this.values.elementAt(i);
                v.add(this.converter.getConvertedValue(oid, str));
                if (lstOid == null || ((Boolean) this.added.elementAt(i)).booleanValue()) {
                    sVec.add(new DERSequence(v));
                } else {
                    vec.add(new DERSet(sVec));
                    sVec = new ASN1EncodableVector();
                    sVec.add(new DERSequence(v));
                }
                lstOid = oid;
            }
            vec.add(new DERSet(sVec));
            this.seq = new DERSequence(vec);
        }
        return this.seq;
    }

    public boolean equals(Object obj, boolean inOrder) {
        if (inOrder) {
            if (obj == this) {
                return true;
            }
            if ((obj instanceof X509Name) || (obj instanceof ASN1Sequence)) {
                ASN1Primitive derO = ((ASN1Encodable) obj).toASN1Primitive();
                if (toASN1Primitive().equals(derO)) {
                    return true;
                }
                try {
                    X509Name other = getInstance(obj);
                    int orderingSize = this.ordering.size();
                    if (orderingSize != other.ordering.size()) {
                        return false;
                    }
                    for (int i = 0; i < orderingSize; i++) {
                        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) this.ordering.elementAt(i);
                        ASN1ObjectIdentifier oOid = (ASN1ObjectIdentifier) other.ordering.elementAt(i);
                        if (!oid.equals((ASN1Primitive) oOid)) {
                            return false;
                        }
                        String value = (String) this.values.elementAt(i);
                        String oValue = (String) other.values.elementAt(i);
                        if (!equivalentStrings(value, oValue)) {
                            return false;
                        }
                    }
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
            return false;
        }
        return equals(obj);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        if (this.isHashCodeCalculated) {
            return this.hashCodeValue;
        }
        this.isHashCodeCalculated = true;
        for (int i = 0; i != this.ordering.size(); i++) {
            String value = (String) this.values.elementAt(i);
            String value2 = stripInternalSpaces(canonicalize(value));
            int hashCode = this.hashCodeValue ^ this.ordering.elementAt(i).hashCode();
            this.hashCodeValue = hashCode;
            this.hashCodeValue = hashCode ^ value2.hashCode();
        }
        int i2 = this.hashCodeValue;
        return i2;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object
    public boolean equals(Object obj) {
        int start;
        int end;
        int delta;
        if (obj == this) {
            return true;
        }
        if ((obj instanceof X509Name) || (obj instanceof ASN1Sequence)) {
            ASN1Primitive derO = ((ASN1Encodable) obj).toASN1Primitive();
            if (toASN1Primitive().equals(derO)) {
                return true;
            }
            try {
                X509Name other = getInstance(obj);
                int orderingSize = this.ordering.size();
                if (orderingSize != other.ordering.size()) {
                    return false;
                }
                boolean[] indexes = new boolean[orderingSize];
                if (this.ordering.elementAt(0).equals(other.ordering.elementAt(0))) {
                    start = 0;
                    end = orderingSize;
                    delta = 1;
                } else {
                    start = orderingSize - 1;
                    end = -1;
                    delta = -1;
                }
                for (int i = start; i != end; i += delta) {
                    boolean found = false;
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) this.ordering.elementAt(i);
                    String value = (String) this.values.elementAt(i);
                    int j = 0;
                    while (true) {
                        if (j >= orderingSize) {
                            break;
                        }
                        if (!indexes[j]) {
                            ASN1ObjectIdentifier oOid = (ASN1ObjectIdentifier) other.ordering.elementAt(j);
                            if (oid.equals((ASN1Primitive) oOid)) {
                                String oValue = (String) other.values.elementAt(j);
                                if (equivalentStrings(value, oValue)) {
                                    indexes[j] = true;
                                    found = true;
                                    break;
                                }
                            } else {
                                continue;
                            }
                        }
                        j++;
                    }
                    if (!found) {
                        return false;
                    }
                }
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    private boolean equivalentStrings(String s1, String s2) {
        String value = canonicalize(s1);
        String oValue = canonicalize(s2);
        if (!value.equals(oValue) && !stripInternalSpaces(value).equals(stripInternalSpaces(oValue))) {
            return false;
        }
        return true;
    }

    private String canonicalize(String s) {
        String value = Strings.toLowerCase(s.trim());
        if (value.length() > 0 && value.charAt(0) == '#') {
            ASN1Primitive obj = decodeObject(value);
            if (obj instanceof ASN1String) {
                return Strings.toLowerCase(((ASN1String) obj).getString().trim());
            }
            return value;
        }
        return value;
    }

    private ASN1Primitive decodeObject(String oValue) {
        try {
            return ASN1Primitive.fromByteArray(Hex.decodeStrict(oValue, 1, oValue.length() - 1));
        } catch (IOException e) {
            throw new IllegalStateException("unknown encoding in name: " + e);
        }
    }

    private String stripInternalSpaces(String str) {
        StringBuffer res = new StringBuffer();
        if (str.length() != 0) {
            char c1 = str.charAt(0);
            res.append(c1);
            for (int k = 1; k < str.length(); k++) {
                char c2 = str.charAt(k);
                if (c1 != ' ' || c2 != ' ') {
                    res.append(c2);
                }
                c1 = c2;
            }
        }
        return res.toString();
    }

    private void appendValue(StringBuffer buf, Hashtable oidSymbols, ASN1ObjectIdentifier oid, String value) {
        String sym = (String) oidSymbols.get(oid);
        if (sym != null) {
            buf.append(sym);
        } else {
            buf.append(oid.getId());
        }
        buf.append('=');
        int start = buf.length();
        buf.append(value);
        int end = buf.length();
        if (value.length() >= 2 && value.charAt(0) == '\\' && value.charAt(1) == '#') {
            start += 2;
        }
        while (start < end && buf.charAt(start) == ' ') {
            buf.insert(start, "\\");
            start += 2;
            end++;
        }
        while (true) {
            end--;
            if (end <= start || buf.charAt(end) != ' ') {
                break;
            }
            buf.insert(end, '\\');
        }
        while (start <= end) {
            switch (buf.charAt(start)) {
                case '\"':
                case '+':
                case ',':
                case ';':
                case '<':
                case '=':
                case '>':
                case '\\':
                    buf.insert(start, "\\");
                    start += 2;
                    end++;
                    break;
                default:
                    start++;
                    break;
            }
        }
    }

    public String toString(boolean reverse, Hashtable oidSymbols) {
        StringBuffer buf = new StringBuffer();
        Vector components = new Vector();
        boolean first = true;
        StringBuffer ava = null;
        for (int i = 0; i < this.ordering.size(); i++) {
            if (((Boolean) this.added.elementAt(i)).booleanValue()) {
                ava.append('+');
                appendValue(ava, oidSymbols, (ASN1ObjectIdentifier) this.ordering.elementAt(i), (String) this.values.elementAt(i));
            } else {
                ava = new StringBuffer();
                appendValue(ava, oidSymbols, (ASN1ObjectIdentifier) this.ordering.elementAt(i), (String) this.values.elementAt(i));
                components.addElement(ava);
            }
        }
        if (reverse) {
            for (int i2 = components.size() - 1; i2 >= 0; i2--) {
                if (first) {
                    first = false;
                } else {
                    buf.append(',');
                }
                buf.append(components.elementAt(i2).toString());
            }
        } else {
            for (int i3 = 0; i3 < components.size(); i3++) {
                if (first) {
                    first = false;
                } else {
                    buf.append(',');
                }
                buf.append(components.elementAt(i3).toString());
            }
        }
        return buf.toString();
    }

    private String bytesToString(byte[] data) {
        char[] cs = new char[data.length];
        for (int i = 0; i != cs.length; i++) {
            cs[i] = (char) (data[i] & 255);
        }
        return new String(cs);
    }

    public String toString() {
        return toString(DefaultReverse, DefaultSymbols);
    }
}
