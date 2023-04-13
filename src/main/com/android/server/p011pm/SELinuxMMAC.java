package com.android.server.p011pm;

import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.compat.PlatformCompat;
import com.android.server.p011pm.Policy;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.SharedUserApi;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.SELinuxMMAC */
/* loaded from: classes2.dex */
public final class SELinuxMMAC {
    public static List<File> sMacPermissions;
    public static List<Policy> sPolicies = new ArrayList();
    public static boolean sPolicyRead;

    static {
        ArrayList arrayList = new ArrayList();
        sMacPermissions = arrayList;
        arrayList.add(new File(Environment.getRootDirectory(), "/etc/selinux/plat_mac_permissions.xml"));
        File file = new File(Environment.getSystemExtDirectory(), "/etc/selinux/system_ext_mac_permissions.xml");
        if (file.exists()) {
            sMacPermissions.add(file);
        }
        File file2 = new File(Environment.getProductDirectory(), "/etc/selinux/product_mac_permissions.xml");
        if (file2.exists()) {
            sMacPermissions.add(file2);
        }
        File file3 = new File(Environment.getVendorDirectory(), "/etc/selinux/vendor_mac_permissions.xml");
        if (file3.exists()) {
            sMacPermissions.add(file3);
        }
        File file4 = new File(Environment.getOdmDirectory(), "/etc/selinux/odm_mac_permissions.xml");
        if (file4.exists()) {
            sMacPermissions.add(file4);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:74:0x007e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x007a A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean readInstallPolicy() {
        FileReader fileReader;
        char c;
        synchronized (sPolicies) {
            if (sPolicyRead) {
                return true;
            }
            ArrayList arrayList = new ArrayList();
            XmlPullParser newPullParser = Xml.newPullParser();
            int size = sMacPermissions.size();
            FileReader fileReader2 = null;
            int i = 0;
            while (i < size) {
                File file = sMacPermissions.get(i);
                try {
                    try {
                        fileReader = new FileReader(file);
                    } catch (Throwable th) {
                        th = th;
                    }
                } catch (IOException e) {
                    e = e;
                } catch (IllegalArgumentException | IllegalStateException | XmlPullParserException e2) {
                    e = e2;
                }
                try {
                    Slog.d("SELinuxMMAC", "Using policy file " + file);
                    newPullParser.setInput(fileReader);
                    newPullParser.nextTag();
                    newPullParser.require(2, null, "policy");
                    while (newPullParser.next() != 3) {
                        if (newPullParser.getEventType() == 2) {
                            String name = newPullParser.getName();
                            if (name.hashCode() == -902467798 && name.equals("signer")) {
                                c = 0;
                                if (c != 0) {
                                    arrayList.add(readSignerOrThrow(newPullParser));
                                } else {
                                    skip(newPullParser);
                                }
                            }
                            c = 65535;
                            if (c != 0) {
                            }
                        }
                    }
                    IoUtils.closeQuietly(fileReader);
                    i++;
                    fileReader2 = fileReader;
                } catch (IOException e3) {
                    e = e3;
                    fileReader2 = fileReader;
                    Slog.w("SELinuxMMAC", "Exception parsing " + file, e);
                    IoUtils.closeQuietly(fileReader2);
                    return false;
                } catch (IllegalArgumentException | IllegalStateException | XmlPullParserException e4) {
                    e = e4;
                    fileReader2 = fileReader;
                    Slog.w("SELinuxMMAC", "Exception @" + newPullParser.getPositionDescription() + " while parsing " + file + XmlUtils.STRING_ARRAY_SEPARATOR + e);
                    IoUtils.closeQuietly(fileReader2);
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    fileReader2 = fileReader;
                    IoUtils.closeQuietly(fileReader2);
                    throw th;
                }
            }
            PolicyComparator policyComparator = new PolicyComparator();
            Collections.sort(arrayList, policyComparator);
            if (policyComparator.foundDuplicate()) {
                Slog.w("SELinuxMMAC", "ERROR! Duplicate entries found parsing mac_permissions.xml files");
                return false;
            }
            synchronized (sPolicies) {
                sPolicies.clear();
                sPolicies.addAll(arrayList);
                sPolicyRead = true;
            }
            return true;
        }
    }

    public static Policy readSignerOrThrow(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        xmlPullParser.require(2, null, "signer");
        Policy.PolicyBuilder policyBuilder = new Policy.PolicyBuilder();
        String attributeValue = xmlPullParser.getAttributeValue(null, "signature");
        if (attributeValue != null) {
            policyBuilder.addSignature(attributeValue);
        }
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if ("seinfo".equals(name)) {
                    policyBuilder.setGlobalSeinfoOrThrow(xmlPullParser.getAttributeValue(null, "value"));
                    readSeinfo(xmlPullParser);
                } else if ("package".equals(name)) {
                    readPackageOrThrow(xmlPullParser, policyBuilder);
                } else if ("cert".equals(name)) {
                    policyBuilder.addSignature(xmlPullParser.getAttributeValue(null, "signature"));
                    readCert(xmlPullParser);
                } else {
                    skip(xmlPullParser);
                }
            }
        }
        return policyBuilder.build();
    }

    public static void readPackageOrThrow(XmlPullParser xmlPullParser, Policy.PolicyBuilder policyBuilder) throws IOException, XmlPullParserException {
        xmlPullParser.require(2, null, "package");
        String attributeValue = xmlPullParser.getAttributeValue(null, "name");
        while (xmlPullParser.next() != 3) {
            if (xmlPullParser.getEventType() == 2) {
                if ("seinfo".equals(xmlPullParser.getName())) {
                    policyBuilder.addInnerPackageMapOrThrow(attributeValue, xmlPullParser.getAttributeValue(null, "value"));
                    readSeinfo(xmlPullParser);
                } else {
                    skip(xmlPullParser);
                }
            }
        }
    }

    public static void readCert(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        xmlPullParser.require(2, null, "cert");
        xmlPullParser.nextTag();
    }

    public static void readSeinfo(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        xmlPullParser.require(2, null, "seinfo");
        xmlPullParser.nextTag();
    }

    public static void skip(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        if (xmlPullParser.getEventType() != 2) {
            throw new IllegalStateException();
        }
        int i = 1;
        while (i != 0) {
            int next = xmlPullParser.next();
            if (next == 2) {
                i++;
            } else if (next == 3) {
                i--;
            }
        }
    }

    public static int getTargetSdkVersionForSeInfo(AndroidPackage androidPackage, SharedUserApi sharedUserApi, PlatformCompat platformCompat) {
        if (sharedUserApi != null && sharedUserApi.getPackages().size() != 0) {
            return sharedUserApi.getSeInfoTargetSdkVersion();
        }
        ApplicationInfo generateAppInfoWithoutState = AndroidPackageUtils.generateAppInfoWithoutState(androidPackage);
        if (platformCompat.isChangeEnabledInternal(143539591L, generateAppInfoWithoutState)) {
            return Math.max((int) FrameworkStatsLog.WIFI_BYTES_TRANSFER, androidPackage.getTargetSdkVersion());
        }
        if (platformCompat.isChangeEnabledInternal(168782947L, generateAppInfoWithoutState)) {
            return Math.max(30, androidPackage.getTargetSdkVersion());
        }
        return androidPackage.getTargetSdkVersion();
    }

    public static String getSeInfo(PackageState packageState, AndroidPackage androidPackage, SharedUserApi sharedUserApi, PlatformCompat platformCompat) {
        boolean isPrivileged;
        int targetSdkVersionForSeInfo = getTargetSdkVersionForSeInfo(androidPackage, sharedUserApi, platformCompat);
        if (sharedUserApi != null) {
            isPrivileged = packageState.isPrivileged() | sharedUserApi.isPrivileged();
        } else {
            isPrivileged = packageState.isPrivileged();
        }
        return getSeInfo(androidPackage, isPrivileged, targetSdkVersionForSeInfo);
    }

    public static String getSeInfo(AndroidPackage androidPackage, boolean z, int i) {
        String str;
        synchronized (sPolicies) {
            str = null;
            if (sPolicyRead) {
                Iterator<Policy> it = sPolicies.iterator();
                while (it.hasNext() && (str = it.next().getMatchedSeInfo(androidPackage)) == null) {
                }
            }
        }
        if (str == null) {
            str = "default";
        }
        if (z) {
            str = str + ":privapp";
        }
        return str + ":targetSdkVersion=" + i;
    }
}
