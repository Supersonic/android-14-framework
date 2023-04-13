package com.android.server.companion;

import android.os.Environment;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.util.FunctionalUtils;
import java.io.File;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class DataStoreUtils {
    public static boolean isStartOfTag(XmlPullParser xmlPullParser, String str) throws XmlPullParserException {
        return xmlPullParser.getEventType() == 2 && str.equals(xmlPullParser.getName());
    }

    public static boolean isEndOfTag(XmlPullParser xmlPullParser, String str) throws XmlPullParserException {
        return xmlPullParser.getEventType() == 3 && str.equals(xmlPullParser.getName());
    }

    public static AtomicFile createStorageFileForUser(int i, String str) {
        return new AtomicFile(getBaseStorageFileForUser(i, str));
    }

    public static File getBaseStorageFileForUser(int i, String str) {
        return new File(Environment.getDataSystemDeDirectory(i), str);
    }

    public static void writeToFileSafely(AtomicFile atomicFile, FunctionalUtils.ThrowingConsumer<FileOutputStream> throwingConsumer) {
        try {
            atomicFile.write(throwingConsumer);
        } catch (Exception e) {
            Slog.e("CDM_DataStoreUtils", "Error while writing to file " + atomicFile, e);
        }
    }
}
