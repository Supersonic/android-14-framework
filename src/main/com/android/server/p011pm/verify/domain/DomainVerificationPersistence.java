package com.android.server.p011pm.verify.domain;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.SettingsXml;
import com.android.server.p011pm.verify.domain.models.DomainVerificationInternalUserState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationPkgState;
import com.android.server.p011pm.verify.domain.models.DomainVerificationStateMap;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationPersistence */
/* loaded from: classes2.dex */
public class DomainVerificationPersistence {
    public static void writeToXml(TypedXmlSerializer typedXmlSerializer, DomainVerificationStateMap<DomainVerificationPkgState> domainVerificationStateMap, ArrayMap<String, DomainVerificationPkgState> arrayMap, ArrayMap<String, DomainVerificationPkgState> arrayMap2, int i, Function<String, String> function) throws IOException {
        SettingsXml.Serializer serializer = SettingsXml.serializer(typedXmlSerializer);
        try {
            SettingsXml.WriteSection startSection = serializer.startSection("domain-verifications");
            ArraySet arraySet = new ArraySet();
            int size = domainVerificationStateMap.size();
            for (int i2 = 0; i2 < size; i2++) {
                arraySet.add(domainVerificationStateMap.valueAt(i2));
            }
            int size2 = arrayMap.size();
            for (int i3 = 0; i3 < size2; i3++) {
                arraySet.add(arrayMap.valueAt(i3));
            }
            SettingsXml.WriteSection startSection2 = serializer.startSection("active");
            try {
                writePackageStates(startSection2, arraySet, i, function);
                if (startSection2 != null) {
                    startSection2.close();
                }
                startSection2 = serializer.startSection("restored");
                try {
                    writePackageStates(startSection2, arrayMap2.values(), i, function);
                    if (startSection2 != null) {
                        startSection2.close();
                    }
                    if (startSection != null) {
                        startSection.close();
                    }
                    serializer.close();
                } finally {
                }
            } finally {
            }
        } catch (Throwable th) {
            if (serializer != null) {
                try {
                    serializer.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static void writePackageStates(SettingsXml.WriteSection writeSection, Collection<DomainVerificationPkgState> collection, int i, Function<String, String> function) throws IOException {
        if (collection.isEmpty()) {
            return;
        }
        for (DomainVerificationPkgState domainVerificationPkgState : collection) {
            writePkgStateToXml(writeSection, domainVerificationPkgState, i, function);
        }
    }

    public static ReadResult readFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        ArrayMap arrayMap = new ArrayMap();
        ArrayMap arrayMap2 = new ArrayMap();
        SettingsXml.ChildSection children = SettingsXml.parser(typedXmlPullParser).children();
        while (children.moveToNext()) {
            String name = children.getName();
            name.hashCode();
            if (name.equals("active")) {
                readPackageStates(children, arrayMap);
            } else if (name.equals("restored")) {
                readPackageStates(children, arrayMap2);
            }
        }
        return new ReadResult(arrayMap, arrayMap2);
    }

    public static void readPackageStates(SettingsXml.ReadSection readSection, ArrayMap<String, DomainVerificationPkgState> arrayMap) {
        SettingsXml.ChildSection children = readSection.children();
        while (children.moveToNext("package-state")) {
            DomainVerificationPkgState createPkgStateFromXml = createPkgStateFromXml(children);
            if (createPkgStateFromXml != null) {
                arrayMap.put(createPkgStateFromXml.getPackageName(), createPkgStateFromXml);
            }
        }
    }

    public static DomainVerificationPkgState createPkgStateFromXml(SettingsXml.ReadSection readSection) {
        String string = readSection.getString("packageName");
        String string2 = readSection.getString("id");
        boolean z = readSection.getBoolean("hasAutoVerifyDomains");
        String string3 = readSection.getString("signature");
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
            return null;
        }
        UUID fromString = UUID.fromString(string2);
        ArrayMap arrayMap = new ArrayMap();
        SparseArray sparseArray = new SparseArray();
        SettingsXml.ChildSection children = readSection.children();
        while (children.moveToNext()) {
            String name = children.getName();
            name.hashCode();
            if (name.equals("user-states")) {
                readUserStates(children, sparseArray);
            } else if (name.equals("state")) {
                readDomainStates(children, arrayMap);
            }
        }
        return new DomainVerificationPkgState(string, fromString, z, arrayMap, sparseArray, string3);
    }

    public static void readUserStates(SettingsXml.ReadSection readSection, SparseArray<DomainVerificationInternalUserState> sparseArray) {
        SettingsXml.ChildSection children = readSection.children();
        while (children.moveToNext("user-state")) {
            DomainVerificationInternalUserState createUserStateFromXml = createUserStateFromXml(children);
            if (createUserStateFromXml != null) {
                sparseArray.put(createUserStateFromXml.getUserId(), createUserStateFromXml);
            }
        }
    }

    public static void readDomainStates(SettingsXml.ReadSection readSection, ArrayMap<String, Integer> arrayMap) {
        SettingsXml.ChildSection children = readSection.children();
        while (children.moveToNext("domain")) {
            arrayMap.put(children.getString("name"), Integer.valueOf(children.getInt("state", 0)));
        }
    }

    public static void writePkgStateToXml(SettingsXml.WriteSection writeSection, DomainVerificationPkgState domainVerificationPkgState, int i, Function<String, String> function) throws IOException {
        String packageName = domainVerificationPkgState.getPackageName();
        String apply = function == null ? null : function.apply(packageName);
        if (apply == null) {
            apply = domainVerificationPkgState.getBackupSignatureHash();
        }
        SettingsXml.WriteSection attribute = writeSection.startSection("package-state").attribute("packageName", packageName).attribute("id", domainVerificationPkgState.getId().toString()).attribute("hasAutoVerifyDomains", domainVerificationPkgState.isHasAutoVerifyDomains()).attribute("signature", apply);
        try {
            writeStateMap(writeSection, domainVerificationPkgState.getStateMap());
            writeUserStates(writeSection, i, domainVerificationPkgState.getUserStates());
            if (attribute != null) {
                attribute.close();
            }
        } catch (Throwable th) {
            if (attribute != null) {
                try {
                    attribute.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static void writeUserStates(SettingsXml.WriteSection writeSection, int i, SparseArray<DomainVerificationInternalUserState> sparseArray) throws IOException {
        int size = sparseArray.size();
        if (size == 0) {
            return;
        }
        SettingsXml.WriteSection startSection = writeSection.startSection("user-states");
        try {
            if (i == -1) {
                for (int i2 = 0; i2 < size; i2++) {
                    writeUserStateToXml(startSection, sparseArray.valueAt(i2));
                }
            } else {
                DomainVerificationInternalUserState domainVerificationInternalUserState = sparseArray.get(i);
                if (domainVerificationInternalUserState != null) {
                    writeUserStateToXml(startSection, domainVerificationInternalUserState);
                }
            }
            if (startSection != null) {
                startSection.close();
            }
        } catch (Throwable th) {
            if (startSection != null) {
                try {
                    startSection.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static void writeStateMap(SettingsXml.WriteSection writeSection, ArrayMap<String, Integer> arrayMap) throws IOException {
        if (arrayMap.isEmpty()) {
            return;
        }
        SettingsXml.WriteSection startSection = writeSection.startSection("state");
        try {
            int size = arrayMap.size();
            for (int i = 0; i < size; i++) {
                startSection.startSection("domain").attribute("name", arrayMap.keyAt(i)).attribute("state", arrayMap.valueAt(i).intValue()).finish();
            }
            if (startSection != null) {
                startSection.close();
            }
        } catch (Throwable th) {
            if (startSection != null) {
                try {
                    startSection.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static DomainVerificationInternalUserState createUserStateFromXml(SettingsXml.ReadSection readSection) {
        int i = readSection.getInt("userId");
        if (i == -1) {
            return null;
        }
        boolean z = readSection.getBoolean("allowLinkHandling", false);
        ArraySet arraySet = new ArraySet();
        SettingsXml.ChildSection children = readSection.children();
        while (children.moveToNext("enabled-hosts")) {
            readEnabledHosts(children, arraySet);
        }
        return new DomainVerificationInternalUserState(i, arraySet, z);
    }

    public static void readEnabledHosts(SettingsXml.ReadSection readSection, ArraySet<String> arraySet) {
        SettingsXml.ChildSection children = readSection.children();
        while (children.moveToNext("host")) {
            String string = children.getString("name");
            if (!TextUtils.isEmpty(string)) {
                arraySet.add(string);
            }
        }
    }

    public static void writeUserStateToXml(SettingsXml.WriteSection writeSection, DomainVerificationInternalUserState domainVerificationInternalUserState) throws IOException {
        SettingsXml.WriteSection attribute = writeSection.startSection("user-state").attribute("userId", domainVerificationInternalUserState.getUserId()).attribute("allowLinkHandling", domainVerificationInternalUserState.isLinkHandlingAllowed());
        try {
            ArraySet<String> enabledHosts = domainVerificationInternalUserState.getEnabledHosts();
            if (!enabledHosts.isEmpty()) {
                SettingsXml.WriteSection startSection = attribute.startSection("enabled-hosts");
                int size = enabledHosts.size();
                for (int i = 0; i < size; i++) {
                    startSection.startSection("host").attribute("name", enabledHosts.valueAt(i)).finish();
                }
                if (startSection != null) {
                    startSection.close();
                }
            }
            if (attribute != null) {
                attribute.close();
            }
        } catch (Throwable th) {
            if (attribute != null) {
                try {
                    attribute.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* renamed from: com.android.server.pm.verify.domain.DomainVerificationPersistence$ReadResult */
    /* loaded from: classes2.dex */
    public static class ReadResult {
        public final ArrayMap<String, DomainVerificationPkgState> active;
        public final ArrayMap<String, DomainVerificationPkgState> restored;

        public ReadResult(ArrayMap<String, DomainVerificationPkgState> arrayMap, ArrayMap<String, DomainVerificationPkgState> arrayMap2) {
            this.active = arrayMap;
            this.restored = arrayMap2;
        }
    }
}
