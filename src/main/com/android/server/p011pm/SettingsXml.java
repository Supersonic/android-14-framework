package com.android.server.p011pm;

import android.util.Slog;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.SettingsXml */
/* loaded from: classes2.dex */
public class SettingsXml {

    /* renamed from: com.android.server.pm.SettingsXml$ChildSection */
    /* loaded from: classes2.dex */
    public interface ChildSection extends ReadSection {
        boolean moveToNext();

        boolean moveToNext(String str);
    }

    /* renamed from: com.android.server.pm.SettingsXml$ReadSection */
    /* loaded from: classes2.dex */
    public interface ReadSection extends AutoCloseable {
        ChildSection children();

        boolean getBoolean(String str);

        boolean getBoolean(String str, boolean z);

        int getInt(String str);

        int getInt(String str, int i);

        String getName();

        String getString(String str);
    }

    /* renamed from: com.android.server.pm.SettingsXml$WriteSection */
    /* loaded from: classes2.dex */
    public interface WriteSection extends AutoCloseable {
        WriteSection attribute(String str, int i) throws IOException;

        WriteSection attribute(String str, String str2) throws IOException;

        WriteSection attribute(String str, boolean z) throws IOException;

        @Override // java.lang.AutoCloseable
        void close() throws IOException;

        void finish() throws IOException;

        WriteSection startSection(String str) throws IOException;
    }

    public static Serializer serializer(TypedXmlSerializer typedXmlSerializer) {
        return new Serializer(typedXmlSerializer);
    }

    public static ReadSection parser(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        return new ReadSectionImpl(typedXmlPullParser);
    }

    /* renamed from: com.android.server.pm.SettingsXml$Serializer */
    /* loaded from: classes2.dex */
    public static class Serializer implements AutoCloseable {
        public final WriteSectionImpl mWriteSection;
        public final TypedXmlSerializer mXmlSerializer;

        public Serializer(TypedXmlSerializer typedXmlSerializer) {
            this.mXmlSerializer = typedXmlSerializer;
            this.mWriteSection = new WriteSectionImpl(typedXmlSerializer);
        }

        public WriteSection startSection(String str) throws IOException {
            return this.mWriteSection.startSection(str);
        }

        @Override // java.lang.AutoCloseable
        public void close() throws IOException {
            this.mWriteSection.closeCompletely();
            this.mXmlSerializer.flush();
        }
    }

    /* renamed from: com.android.server.pm.SettingsXml$ReadSectionImpl */
    /* loaded from: classes2.dex */
    public static class ReadSectionImpl implements ChildSection {
        public final Stack<Integer> mDepthStack = new Stack<>();
        public final InputStream mInput = null;
        public final TypedXmlPullParser mParser;

        public ReadSectionImpl(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            this.mParser = typedXmlPullParser;
            moveToFirstTag();
        }

        public final void moveToFirstTag() throws IOException, XmlPullParserException {
            int next;
            if (this.mParser.getEventType() == 2) {
                return;
            }
            do {
                next = this.mParser.next();
                if (next == 2) {
                    return;
                }
            } while (next != 1);
        }

        @Override // com.android.server.p011pm.SettingsXml.ReadSection
        public String getName() {
            return this.mParser.getName();
        }

        @Override // com.android.server.p011pm.SettingsXml.ReadSection
        public String getString(String str) {
            return this.mParser.getAttributeValue((String) null, str);
        }

        @Override // com.android.server.p011pm.SettingsXml.ReadSection
        public boolean getBoolean(String str) {
            return getBoolean(str, false);
        }

        @Override // com.android.server.p011pm.SettingsXml.ReadSection
        public boolean getBoolean(String str, boolean z) {
            return this.mParser.getAttributeBoolean((String) null, str, z);
        }

        @Override // com.android.server.p011pm.SettingsXml.ReadSection
        public int getInt(String str) {
            return getInt(str, -1);
        }

        @Override // com.android.server.p011pm.SettingsXml.ReadSection
        public int getInt(String str, int i) {
            return this.mParser.getAttributeInt((String) null, str, i);
        }

        @Override // com.android.server.p011pm.SettingsXml.ReadSection
        public ChildSection children() {
            this.mDepthStack.push(Integer.valueOf(this.mParser.getDepth()));
            return this;
        }

        @Override // com.android.server.p011pm.SettingsXml.ChildSection
        public boolean moveToNext() {
            return moveToNextInternal(null);
        }

        @Override // com.android.server.p011pm.SettingsXml.ChildSection
        public boolean moveToNext(String str) {
            return moveToNextInternal(str);
        }

        public final boolean moveToNextInternal(String str) {
            try {
                int intValue = this.mDepthStack.peek().intValue();
                boolean z = false;
                while (!z) {
                    int next = this.mParser.next();
                    if (next == 1 || (next == 3 && this.mParser.getDepth() <= intValue)) {
                        break;
                    } else if (next == 2 && (str == null || str.equals(this.mParser.getName()))) {
                        z = true;
                    }
                }
                if (!z) {
                    this.mDepthStack.pop();
                }
                return z;
            } catch (Exception unused) {
                return false;
            }
        }

        @Override // java.lang.AutoCloseable
        public void close() throws Exception {
            if (this.mDepthStack.isEmpty()) {
                Slog.wtf("SettingsXml", "Children depth stack was not empty, data may have been lost", new Exception());
            }
            InputStream inputStream = this.mInput;
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /* renamed from: com.android.server.pm.SettingsXml$WriteSectionImpl */
    /* loaded from: classes2.dex */
    public static class WriteSectionImpl implements WriteSection {
        public final Stack<String> mTagStack;
        public final TypedXmlSerializer mXmlSerializer;

        public WriteSectionImpl(TypedXmlSerializer typedXmlSerializer) {
            this.mTagStack = new Stack<>();
            this.mXmlSerializer = typedXmlSerializer;
        }

        @Override // com.android.server.p011pm.SettingsXml.WriteSection
        public WriteSection startSection(String str) throws IOException {
            this.mXmlSerializer.startTag((String) null, str);
            this.mTagStack.push(str);
            return this;
        }

        @Override // com.android.server.p011pm.SettingsXml.WriteSection
        public WriteSection attribute(String str, String str2) throws IOException {
            if (str2 != null) {
                this.mXmlSerializer.attribute((String) null, str, str2);
            }
            return this;
        }

        @Override // com.android.server.p011pm.SettingsXml.WriteSection
        public WriteSection attribute(String str, int i) throws IOException {
            if (i != -1) {
                this.mXmlSerializer.attributeInt((String) null, str, i);
            }
            return this;
        }

        @Override // com.android.server.p011pm.SettingsXml.WriteSection
        public WriteSection attribute(String str, boolean z) throws IOException {
            if (z) {
                this.mXmlSerializer.attributeBoolean((String) null, str, z);
            }
            return this;
        }

        @Override // com.android.server.p011pm.SettingsXml.WriteSection
        public void finish() throws IOException {
            close();
        }

        @Override // com.android.server.p011pm.SettingsXml.WriteSection, java.lang.AutoCloseable
        public void close() throws IOException {
            this.mXmlSerializer.endTag((String) null, this.mTagStack.pop());
        }

        public final void closeCompletely() throws IOException {
            if (this.mTagStack != null) {
                while (!this.mTagStack.isEmpty()) {
                    close();
                }
            }
        }
    }
}
