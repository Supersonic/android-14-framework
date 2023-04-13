package com.android.server.p011pm;

import android.content.pm.Signature;
import android.content.pm.SigningDetails;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.PackageSignatures */
/* loaded from: classes2.dex */
public class PackageSignatures {
    public SigningDetails mSigningDetails;

    public PackageSignatures(PackageSignatures packageSignatures) {
        if (packageSignatures != null && packageSignatures.mSigningDetails != SigningDetails.UNKNOWN) {
            this.mSigningDetails = new SigningDetails(packageSignatures.mSigningDetails);
        } else {
            this.mSigningDetails = SigningDetails.UNKNOWN;
        }
    }

    public PackageSignatures() {
        this.mSigningDetails = SigningDetails.UNKNOWN;
    }

    public void writeXml(TypedXmlSerializer typedXmlSerializer, String str, ArrayList<Signature> arrayList) throws IOException {
        if (this.mSigningDetails.getSignatures() == null) {
            return;
        }
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.attributeInt((String) null, "count", this.mSigningDetails.getSignatures().length);
        typedXmlSerializer.attributeInt((String) null, "schemeVersion", this.mSigningDetails.getSignatureSchemeVersion());
        writeCertsListXml(typedXmlSerializer, arrayList, this.mSigningDetails.getSignatures(), false);
        if (this.mSigningDetails.getPastSigningCertificates() != null) {
            typedXmlSerializer.startTag((String) null, "pastSigs");
            typedXmlSerializer.attributeInt((String) null, "count", this.mSigningDetails.getPastSigningCertificates().length);
            writeCertsListXml(typedXmlSerializer, arrayList, this.mSigningDetails.getPastSigningCertificates(), true);
            typedXmlSerializer.endTag((String) null, "pastSigs");
        }
        typedXmlSerializer.endTag((String) null, str);
    }

    public final void writeCertsListXml(TypedXmlSerializer typedXmlSerializer, ArrayList<Signature> arrayList, Signature[] signatureArr, boolean z) throws IOException {
        for (Signature signature : signatureArr) {
            typedXmlSerializer.startTag((String) null, "cert");
            int hashCode = signature.hashCode();
            int size = arrayList.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                Signature signature2 = arrayList.get(i);
                if (signature2.hashCode() == hashCode && signature2.equals(signature)) {
                    typedXmlSerializer.attributeInt((String) null, "index", i);
                    break;
                }
                i++;
            }
            if (i >= size) {
                arrayList.add(signature);
                typedXmlSerializer.attributeInt((String) null, "index", size);
                signature.writeToXmlAttributeBytesHex(typedXmlSerializer, null, "key");
            }
            if (z) {
                typedXmlSerializer.attributeInt((String) null, "flags", signature.getFlags());
            }
            typedXmlSerializer.endTag((String) null, "cert");
        }
    }

    public void readXml(TypedXmlPullParser typedXmlPullParser, ArrayList<Signature> arrayList) throws IOException, XmlPullParserException {
        SigningDetails.Builder builder = new SigningDetails.Builder();
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "count", -1);
        if (attributeInt == -1) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> has no count at " + typedXmlPullParser.getPositionDescription());
            XmlUtils.skipCurrentTag(typedXmlPullParser);
            return;
        }
        int attributeInt2 = typedXmlPullParser.getAttributeInt((String) null, "schemeVersion", 0);
        if (attributeInt2 == 0) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> has no schemeVersion at " + typedXmlPullParser.getPositionDescription());
        }
        builder.setSignatureSchemeVersion(attributeInt2);
        ArrayList<Signature> arrayList2 = new ArrayList<>();
        int readCertsListXml = readCertsListXml(typedXmlPullParser, arrayList, arrayList2, attributeInt, false, builder);
        builder.setSignatures((Signature[]) arrayList2.toArray(new Signature[arrayList2.size()]));
        if (readCertsListXml < attributeInt) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> count does not match number of  <cert> entries" + typedXmlPullParser.getPositionDescription());
        }
        try {
            this.mSigningDetails = builder.build();
        } catch (CertificateException unused) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <sigs> unable to convert certificate(s) to public key(s).");
            this.mSigningDetails = SigningDetails.UNKNOWN;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x010c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int readCertsListXml(TypedXmlPullParser typedXmlPullParser, ArrayList<Signature> arrayList, ArrayList<Signature> arrayList2, int i, boolean z, SigningDetails.Builder builder) throws IOException, XmlPullParserException {
        boolean z2;
        byte[] attributeBytesHex;
        int i2;
        int i3;
        String str;
        ArrayList<Signature> arrayList3;
        TypedXmlPullParser typedXmlPullParser2 = typedXmlPullParser;
        int depth = typedXmlPullParser.getDepth();
        SigningDetails.Builder builder2 = builder;
        int i4 = 0;
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            }
            if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if (name.equals("cert")) {
                    if (i4 < i) {
                        int attributeInt = typedXmlPullParser2.getAttributeInt((String) null, "index", -1);
                        if (attributeInt != -1) {
                            try {
                                attributeBytesHex = typedXmlPullParser2.getAttributeBytesHex((String) null, "key", (byte[]) null);
                            } catch (NumberFormatException unused) {
                                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + attributeInt + " is not a number at " + typedXmlPullParser.getPositionDescription());
                            } catch (IllegalArgumentException e) {
                                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + attributeInt + " has an invalid signature at " + typedXmlPullParser.getPositionDescription() + ": " + e.getMessage());
                            }
                            if (attributeBytesHex == null) {
                                if (attributeInt >= 0 && attributeInt < arrayList.size()) {
                                    Signature signature = arrayList.get(attributeInt);
                                    if (signature == null) {
                                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + attributeInt + " is not defined at " + typedXmlPullParser.getPositionDescription());
                                    } else if (z) {
                                        arrayList2.add(new Signature(signature));
                                    } else {
                                        arrayList2.add(signature);
                                    }
                                } else {
                                    PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> index " + attributeInt + " is out of bounds at " + typedXmlPullParser.getPositionDescription());
                                }
                                z2 = false;
                                if (z) {
                                    int attributeInt2 = typedXmlPullParser2.getAttributeInt((String) null, "flags", -1);
                                    if (attributeInt2 == -1) {
                                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> has no flags at " + typedXmlPullParser.getPositionDescription());
                                    } else if (z2) {
                                        try {
                                            arrayList2.get(arrayList2.size() - 1).setFlags(attributeInt2);
                                        } catch (NumberFormatException unused2) {
                                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> flags " + attributeInt2 + " is not a number at " + typedXmlPullParser.getPositionDescription());
                                        }
                                    } else {
                                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: signature not available at index " + i4 + " to set flags at " + typedXmlPullParser.getPositionDescription());
                                    }
                                }
                            } else {
                                Signature signature2 = new Signature(attributeBytesHex);
                                while (arrayList.size() < attributeInt) {
                                    arrayList.add(null);
                                }
                                arrayList.add(signature2);
                                arrayList2.add(signature2);
                            }
                            z2 = true;
                            if (z) {
                            }
                        } else {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <cert> has no index at " + typedXmlPullParser.getPositionDescription());
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: too many <cert> tags, expected " + i + " at " + typedXmlPullParser.getPositionDescription());
                    }
                    i4++;
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                } else {
                    if (!name.equals("pastSigs")) {
                        i2 = i4;
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <sigs>: " + typedXmlPullParser.getName());
                        XmlUtils.skipCurrentTag(typedXmlPullParser);
                    } else if (!z) {
                        int attributeInt3 = typedXmlPullParser2.getAttributeInt((String) null, "count", -1);
                        if (attributeInt3 == -1) {
                            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <pastSigs> has no count at " + typedXmlPullParser.getPositionDescription());
                            XmlUtils.skipCurrentTag(typedXmlPullParser);
                        } else {
                            try {
                                arrayList3 = new ArrayList<>();
                                i3 = 5;
                                str = " is not a number at ";
                                i2 = i4;
                            } catch (NumberFormatException unused3) {
                                i3 = 5;
                                str = " is not a number at ";
                                i2 = i4;
                            }
                            try {
                                int readCertsListXml = readCertsListXml(typedXmlPullParser, arrayList, arrayList3, attributeInt3, true, builder2);
                                builder2 = builder2.setPastSigningCertificates((Signature[]) arrayList3.toArray(new Signature[arrayList3.size()]));
                                if (readCertsListXml < attributeInt3) {
                                    PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <pastSigs> count does not match number of <cert> entries " + typedXmlPullParser.getPositionDescription());
                                }
                            } catch (NumberFormatException unused4) {
                                PackageManagerService.reportSettingsProblem(i3, "Error in package manager settings: <pastSigs> count " + attributeInt3 + str + typedXmlPullParser.getPositionDescription());
                                i4 = i2;
                                typedXmlPullParser2 = typedXmlPullParser;
                            }
                        }
                    } else {
                        i2 = i4;
                        PackageManagerService.reportSettingsProblem(5, "<pastSigs> encountered multiple times under the same <sigs> at " + typedXmlPullParser.getPositionDescription());
                        XmlUtils.skipCurrentTag(typedXmlPullParser);
                    }
                    i4 = i2;
                }
                typedXmlPullParser2 = typedXmlPullParser;
            }
            typedXmlPullParser2 = typedXmlPullParser;
            i4 = i4;
        }
        return i4;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("PackageSignatures{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" version:");
        sb.append(this.mSigningDetails.getSignatureSchemeVersion());
        sb.append(", signatures:[");
        if (this.mSigningDetails.getSignatures() != null) {
            for (int i = 0; i < this.mSigningDetails.getSignatures().length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(Integer.toHexString(this.mSigningDetails.getSignatures()[i].hashCode()));
            }
        }
        sb.append("]");
        sb.append(", past signatures:[");
        if (this.mSigningDetails.getPastSigningCertificates() != null) {
            for (int i2 = 0; i2 < this.mSigningDetails.getPastSigningCertificates().length; i2++) {
                if (i2 > 0) {
                    sb.append(", ");
                }
                sb.append(Integer.toHexString(this.mSigningDetails.getPastSigningCertificates()[i2].hashCode()));
                sb.append(" flags: ");
                sb.append(Integer.toHexString(this.mSigningDetails.getPastSigningCertificates()[i2].getFlags()));
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
