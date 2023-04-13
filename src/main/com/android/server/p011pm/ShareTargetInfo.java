package com.android.server.p011pm;

import android.text.TextUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.ShareTargetInfo */
/* loaded from: classes2.dex */
public class ShareTargetInfo {
    public final String[] mCategories;
    public final String mTargetClass;
    public final TargetData[] mTargetData;

    /* renamed from: com.android.server.pm.ShareTargetInfo$TargetData */
    /* loaded from: classes2.dex */
    public static class TargetData {
        public final String mHost;
        public final String mMimeType;
        public final String mPath;
        public final String mPathPattern;
        public final String mPathPrefix;
        public final String mPort;
        public final String mScheme;

        public TargetData(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
            this.mScheme = str;
            this.mHost = str2;
            this.mPort = str3;
            this.mPath = str4;
            this.mPathPattern = str5;
            this.mPathPrefix = str6;
            this.mMimeType = str7;
        }

        public void toStringInner(StringBuilder sb) {
            if (!TextUtils.isEmpty(this.mScheme)) {
                sb.append(" scheme=");
                sb.append(this.mScheme);
            }
            if (!TextUtils.isEmpty(this.mHost)) {
                sb.append(" host=");
                sb.append(this.mHost);
            }
            if (!TextUtils.isEmpty(this.mPort)) {
                sb.append(" port=");
                sb.append(this.mPort);
            }
            if (!TextUtils.isEmpty(this.mPath)) {
                sb.append(" path=");
                sb.append(this.mPath);
            }
            if (!TextUtils.isEmpty(this.mPathPattern)) {
                sb.append(" pathPattern=");
                sb.append(this.mPathPattern);
            }
            if (!TextUtils.isEmpty(this.mPathPrefix)) {
                sb.append(" pathPrefix=");
                sb.append(this.mPathPrefix);
            }
            if (TextUtils.isEmpty(this.mMimeType)) {
                return;
            }
            sb.append(" mimeType=");
            sb.append(this.mMimeType);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            toStringInner(sb);
            return sb.toString();
        }
    }

    public ShareTargetInfo(TargetData[] targetDataArr, String str, String[] strArr) {
        this.mTargetData = targetDataArr;
        this.mTargetClass = str;
        this.mCategories = strArr;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("targetClass=");
        sb.append(this.mTargetClass);
        for (int i = 0; i < this.mTargetData.length; i++) {
            sb.append(" data={");
            this.mTargetData[i].toStringInner(sb);
            sb.append("}");
        }
        for (int i2 = 0; i2 < this.mCategories.length; i2++) {
            sb.append(" category=");
            sb.append(this.mCategories[i2]);
        }
        return sb.toString();
    }

    public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        typedXmlSerializer.startTag((String) null, "share-target");
        ShortcutService.writeAttr(typedXmlSerializer, "targetClass", this.mTargetClass);
        for (int i = 0; i < this.mTargetData.length; i++) {
            typedXmlSerializer.startTag((String) null, "data");
            ShortcutService.writeAttr(typedXmlSerializer, "scheme", this.mTargetData[i].mScheme);
            ShortcutService.writeAttr(typedXmlSerializer, "host", this.mTargetData[i].mHost);
            ShortcutService.writeAttr(typedXmlSerializer, "port", this.mTargetData[i].mPort);
            ShortcutService.writeAttr(typedXmlSerializer, "path", this.mTargetData[i].mPath);
            ShortcutService.writeAttr(typedXmlSerializer, "pathPattern", this.mTargetData[i].mPathPattern);
            ShortcutService.writeAttr(typedXmlSerializer, "pathPrefix", this.mTargetData[i].mPathPrefix);
            ShortcutService.writeAttr(typedXmlSerializer, "mimeType", this.mTargetData[i].mMimeType);
            typedXmlSerializer.endTag((String) null, "data");
        }
        for (int i2 = 0; i2 < this.mCategories.length; i2++) {
            typedXmlSerializer.startTag((String) null, "category");
            ShortcutService.writeAttr(typedXmlSerializer, "name", this.mCategories[i2]);
            typedXmlSerializer.endTag((String) null, "category");
        }
        typedXmlSerializer.endTag((String) null, "share-target");
    }

    public static ShareTargetInfo loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        String parseStringAttribute = ShortcutService.parseStringAttribute(typedXmlPullParser, "targetClass");
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next != 1) {
                if (next == 2) {
                    String name = typedXmlPullParser.getName();
                    name.hashCode();
                    if (name.equals("data")) {
                        arrayList.add(parseTargetData(typedXmlPullParser));
                    } else if (name.equals("category")) {
                        arrayList2.add(ShortcutService.parseStringAttribute(typedXmlPullParser, "name"));
                    }
                } else if (next == 3 && typedXmlPullParser.getName().equals("share-target")) {
                    break;
                }
            } else {
                break;
            }
        }
        if (arrayList.isEmpty() || parseStringAttribute == null || arrayList2.isEmpty()) {
            return null;
        }
        return new ShareTargetInfo((TargetData[]) arrayList.toArray(new TargetData[arrayList.size()]), parseStringAttribute, (String[]) arrayList2.toArray(new String[arrayList2.size()]));
    }

    public static TargetData parseTargetData(TypedXmlPullParser typedXmlPullParser) {
        return new TargetData(ShortcutService.parseStringAttribute(typedXmlPullParser, "scheme"), ShortcutService.parseStringAttribute(typedXmlPullParser, "host"), ShortcutService.parseStringAttribute(typedXmlPullParser, "port"), ShortcutService.parseStringAttribute(typedXmlPullParser, "path"), ShortcutService.parseStringAttribute(typedXmlPullParser, "pathPattern"), ShortcutService.parseStringAttribute(typedXmlPullParser, "pathPrefix"), ShortcutService.parseStringAttribute(typedXmlPullParser, "mimeType"));
    }
}
