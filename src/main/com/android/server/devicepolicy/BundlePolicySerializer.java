package com.android.server.devicepolicy;

import android.app.admin.BundlePolicyValue;
import android.app.admin.PackagePolicyKey;
import android.app.admin.PolicyKey;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class BundlePolicySerializer extends PolicySerializer<Bundle> {
    @Override // com.android.server.devicepolicy.PolicySerializer
    public void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, Bundle bundle) throws IOException {
        Objects.requireNonNull(bundle);
        Objects.requireNonNull(policyKey);
        if (!(policyKey instanceof PackagePolicyKey)) {
            throw new IllegalArgumentException("policyKey is not of type PackagePolicyKey");
        }
        String packageToRestrictionsFileName = packageToRestrictionsFileName(((PackagePolicyKey) policyKey).getPackageName(), bundle);
        writeApplicationRestrictionsLAr(packageToRestrictionsFileName, bundle);
        typedXmlSerializer.attribute((String) null, str, packageToRestrictionsFileName);
    }

    @Override // com.android.server.devicepolicy.PolicySerializer
    public BundlePolicyValue readFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        return new BundlePolicyValue(readApplicationRestrictions(typedXmlPullParser.getAttributeValue((String) null, str)));
    }

    public static String packageToRestrictionsFileName(String str, Bundle bundle) {
        return "AppRestrictions_" + str + Objects.hash(bundle) + ".xml";
    }

    @GuardedBy({"mAppRestrictionsLock"})
    public static Bundle readApplicationRestrictions(String str) {
        return readApplicationRestrictions(new AtomicFile(new File(Environment.getDataSystemDirectory(), str)));
    }

    @GuardedBy({"mAppRestrictionsLock"})
    @VisibleForTesting
    public static Bundle readApplicationRestrictions(AtomicFile atomicFile) {
        TypedXmlPullParser resolvePullParser;
        Bundle bundle = new Bundle();
        ArrayList arrayList = new ArrayList();
        if (atomicFile.getBaseFile().exists()) {
            FileInputStream fileInputStream = null;
            try {
                try {
                    fileInputStream = atomicFile.openRead();
                    resolvePullParser = Xml.resolvePullParser(fileInputStream);
                    XmlUtils.nextElement(resolvePullParser);
                } catch (IOException | XmlPullParserException e) {
                    Slog.w("DevicePolicyEngine", "Error parsing " + atomicFile.getBaseFile(), e);
                }
                if (resolvePullParser.getEventType() != 2) {
                    Slog.e("DevicePolicyEngine", "Unable to read restrictions file " + atomicFile.getBaseFile());
                    return bundle;
                }
                while (resolvePullParser.next() != 1) {
                    readEntry(bundle, arrayList, resolvePullParser);
                }
                return bundle;
            } finally {
                IoUtils.closeQuietly((AutoCloseable) null);
            }
        }
        return bundle;
    }

    public static void readEntry(Bundle bundle, ArrayList<String> arrayList, TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        if (typedXmlPullParser.getEventType() == 2 && typedXmlPullParser.getName().equals("entry")) {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "key");
            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "type");
            int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "m", -1);
            if (attributeInt != -1) {
                arrayList.clear();
                while (attributeInt > 0) {
                    int next = typedXmlPullParser.next();
                    if (next == 1) {
                        break;
                    } else if (next == 2 && typedXmlPullParser.getName().equals("value")) {
                        arrayList.add(typedXmlPullParser.nextText().trim());
                        attributeInt--;
                    }
                }
                String[] strArr = new String[arrayList.size()];
                arrayList.toArray(strArr);
                bundle.putStringArray(attributeValue, strArr);
            } else if ("B".equals(attributeValue2)) {
                bundle.putBundle(attributeValue, readBundleEntry(typedXmlPullParser, arrayList));
            } else if ("BA".equals(attributeValue2)) {
                int depth = typedXmlPullParser.getDepth();
                ArrayList arrayList2 = new ArrayList();
                while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                    arrayList2.add(readBundleEntry(typedXmlPullParser, arrayList));
                }
                bundle.putParcelableArray(attributeValue, (Parcelable[]) arrayList2.toArray(new Bundle[arrayList2.size()]));
            } else {
                String trim = typedXmlPullParser.nextText().trim();
                if ("b".equals(attributeValue2)) {
                    bundle.putBoolean(attributeValue, Boolean.parseBoolean(trim));
                } else if ("i".equals(attributeValue2)) {
                    bundle.putInt(attributeValue, Integer.parseInt(trim));
                } else {
                    bundle.putString(attributeValue, trim);
                }
            }
        }
    }

    public static Bundle readBundleEntry(TypedXmlPullParser typedXmlPullParser, ArrayList<String> arrayList) throws IOException, XmlPullParserException {
        Bundle bundle = new Bundle();
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            readEntry(bundle, arrayList, typedXmlPullParser);
        }
        return bundle;
    }

    public static void writeApplicationRestrictionsLAr(String str, Bundle bundle) {
        writeApplicationRestrictionsLAr(bundle, new AtomicFile(new File(Environment.getDataSystemDirectory(), str)));
    }

    public static void writeApplicationRestrictionsLAr(Bundle bundle, AtomicFile atomicFile) {
        FileOutputStream startWrite;
        FileOutputStream fileOutputStream = null;
        try {
            startWrite = atomicFile.startWrite();
        } catch (Exception e) {
            e = e;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            resolveSerializer.startTag((String) null, "restrictions");
            writeBundle(bundle, resolveSerializer);
            resolveSerializer.endTag((String) null, "restrictions");
            resolveSerializer.endDocument();
            atomicFile.finishWrite(startWrite);
        } catch (Exception e2) {
            e = e2;
            fileOutputStream = startWrite;
            atomicFile.failWrite(fileOutputStream);
            Slog.e("DevicePolicyEngine", "Error writing application restrictions list", e);
        }
    }

    public static void writeBundle(Bundle bundle, TypedXmlSerializer typedXmlSerializer) throws IOException {
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            typedXmlSerializer.startTag((String) null, "entry");
            typedXmlSerializer.attribute((String) null, "key", str);
            if (obj instanceof Boolean) {
                typedXmlSerializer.attribute((String) null, "type", "b");
                typedXmlSerializer.text(obj.toString());
            } else if (obj instanceof Integer) {
                typedXmlSerializer.attribute((String) null, "type", "i");
                typedXmlSerializer.text(obj.toString());
            } else if (obj == null || (obj instanceof String)) {
                typedXmlSerializer.attribute((String) null, "type", "s");
                typedXmlSerializer.text(obj != null ? (String) obj : "");
            } else if (obj instanceof Bundle) {
                typedXmlSerializer.attribute((String) null, "type", "B");
                writeBundle((Bundle) obj, typedXmlSerializer);
            } else {
                int i = 0;
                if (obj instanceof Parcelable[]) {
                    typedXmlSerializer.attribute((String) null, "type", "BA");
                    Parcelable[] parcelableArr = (Parcelable[]) obj;
                    int length = parcelableArr.length;
                    while (i < length) {
                        Parcelable parcelable = parcelableArr[i];
                        if (!(parcelable instanceof Bundle)) {
                            throw new IllegalArgumentException("bundle-array can only hold Bundles");
                        }
                        typedXmlSerializer.startTag((String) null, "entry");
                        typedXmlSerializer.attribute((String) null, "type", "B");
                        writeBundle((Bundle) parcelable, typedXmlSerializer);
                        typedXmlSerializer.endTag((String) null, "entry");
                        i++;
                    }
                    continue;
                } else {
                    typedXmlSerializer.attribute((String) null, "type", "sa");
                    String[] strArr = (String[]) obj;
                    typedXmlSerializer.attributeInt((String) null, "m", strArr.length);
                    int length2 = strArr.length;
                    while (i < length2) {
                        String str2 = strArr[i];
                        typedXmlSerializer.startTag((String) null, "value");
                        if (str2 == null) {
                            str2 = "";
                        }
                        typedXmlSerializer.text(str2);
                        typedXmlSerializer.endTag((String) null, "value");
                        i++;
                    }
                }
            }
            typedXmlSerializer.endTag((String) null, "entry");
        }
    }
}
