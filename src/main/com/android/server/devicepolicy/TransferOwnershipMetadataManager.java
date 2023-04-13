package com.android.server.devicepolicy;

import android.content.ComponentName;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class TransferOwnershipMetadataManager {
    public static final String TAG = "com.android.server.devicepolicy.TransferOwnershipMetadataManager";
    @VisibleForTesting
    static final String TAG_ADMIN_TYPE = "admin-type";
    @VisibleForTesting
    static final String TAG_SOURCE_COMPONENT = "source-component";
    @VisibleForTesting
    static final String TAG_TARGET_COMPONENT = "target-component";
    @VisibleForTesting
    static final String TAG_USER_ID = "user-id";
    public final Injector mInjector;

    public TransferOwnershipMetadataManager() {
        this(new Injector());
    }

    @VisibleForTesting
    public TransferOwnershipMetadataManager(Injector injector) {
        this.mInjector = injector;
    }

    public boolean saveMetadataFile(Metadata metadata) {
        FileOutputStream startWrite;
        File file = new File(this.mInjector.getOwnerTransferMetadataDir(), "owner-transfer-metadata.xml");
        AtomicFile atomicFile = new AtomicFile(file);
        FileOutputStream fileOutputStream = null;
        try {
            startWrite = atomicFile.startWrite();
        } catch (IOException e) {
            e = e;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            insertSimpleTag(resolveSerializer, TAG_USER_ID, Integer.toString(metadata.userId));
            insertSimpleTag(resolveSerializer, TAG_SOURCE_COMPONENT, metadata.sourceComponent.flattenToString());
            insertSimpleTag(resolveSerializer, TAG_TARGET_COMPONENT, metadata.targetComponent.flattenToString());
            insertSimpleTag(resolveSerializer, TAG_ADMIN_TYPE, metadata.adminType);
            resolveSerializer.endDocument();
            atomicFile.finishWrite(startWrite);
            return true;
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = startWrite;
            String str = TAG;
            Slog.e(str, "Caught exception while trying to save Owner Transfer Params to file " + file, e);
            file.delete();
            atomicFile.failWrite(fileOutputStream);
            return false;
        }
    }

    public final void insertSimpleTag(TypedXmlSerializer typedXmlSerializer, String str, String str2) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.text(str2);
        typedXmlSerializer.endTag((String) null, str);
    }

    public Metadata loadMetadataFile() {
        File file = new File(this.mInjector.getOwnerTransferMetadataDir(), "owner-transfer-metadata.xml");
        if (file.exists()) {
            String str = TAG;
            Slog.d(str, "Loading TransferOwnershipMetadataManager from " + file);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Metadata parseMetadataFile = parseMetadataFile(Xml.resolvePullParser(fileInputStream));
                fileInputStream.close();
                return parseMetadataFile;
            } catch (IOException | IllegalArgumentException | XmlPullParserException e) {
                String str2 = TAG;
                Slog.e(str2, "Caught exception while trying to load the owner transfer params from file " + file, e);
                return null;
            }
        }
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x004e, code lost:
        if (r5.equals(com.android.server.devicepolicy.TransferOwnershipMetadataManager.TAG_USER_ID) == false) goto L19;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Metadata parseMetadataFile(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        String str = null;
        int i = 0;
        String str2 = null;
        String str3 = null;
        while (true) {
            int next = typedXmlPullParser.next();
            char c = 1;
            if (next != 1 && (next != 3 || typedXmlPullParser.getDepth() > depth)) {
                if (next != 3 && next != 4) {
                    String name = typedXmlPullParser.getName();
                    name.hashCode();
                    switch (name.hashCode()) {
                        case -337219647:
                            if (name.equals(TAG_TARGET_COMPONENT)) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case -147180963:
                            break;
                        case 281362891:
                            if (name.equals(TAG_SOURCE_COMPONENT)) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 641951480:
                            if (name.equals(TAG_ADMIN_TYPE)) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                            typedXmlPullParser.next();
                            str2 = typedXmlPullParser.getText();
                            continue;
                        case 1:
                            typedXmlPullParser.next();
                            i = Integer.parseInt(typedXmlPullParser.getText());
                            continue;
                        case 2:
                            typedXmlPullParser.next();
                            str = typedXmlPullParser.getText();
                            continue;
                        case 3:
                            typedXmlPullParser.next();
                            str3 = typedXmlPullParser.getText();
                            continue;
                    }
                }
            }
        }
        return new Metadata(str, str2, i, str3);
    }

    public void deleteMetadataFile() {
        new File(this.mInjector.getOwnerTransferMetadataDir(), "owner-transfer-metadata.xml").delete();
    }

    public boolean metadataFileExists() {
        return new File(this.mInjector.getOwnerTransferMetadataDir(), "owner-transfer-metadata.xml").exists();
    }

    /* loaded from: classes.dex */
    public static class Metadata {
        public final String adminType;
        public final ComponentName sourceComponent;
        public final ComponentName targetComponent;
        public final int userId;

        public Metadata(ComponentName componentName, ComponentName componentName2, int i, String str) {
            this.sourceComponent = componentName;
            this.targetComponent = componentName2;
            Objects.requireNonNull(componentName);
            Objects.requireNonNull(componentName2);
            Preconditions.checkStringNotEmpty(str);
            this.userId = i;
            this.adminType = str;
        }

        public Metadata(String str, String str2, int i, String str3) {
            this(unflattenComponentUnchecked(str), unflattenComponentUnchecked(str2), i, str3);
        }

        public static ComponentName unflattenComponentUnchecked(String str) {
            Objects.requireNonNull(str);
            return ComponentName.unflattenFromString(str);
        }

        public boolean equals(Object obj) {
            if (obj instanceof Metadata) {
                Metadata metadata = (Metadata) obj;
                return this.userId == metadata.userId && this.sourceComponent.equals(metadata.sourceComponent) && this.targetComponent.equals(metadata.targetComponent) && TextUtils.equals(this.adminType, metadata.adminType);
            }
            return false;
        }

        public int hashCode() {
            return ((((((this.userId + 31) * 31) + this.sourceComponent.hashCode()) * 31) + this.targetComponent.hashCode()) * 31) + this.adminType.hashCode();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public File getOwnerTransferMetadataDir() {
            return Environment.getDataSystemDirectory();
        }
    }
}
