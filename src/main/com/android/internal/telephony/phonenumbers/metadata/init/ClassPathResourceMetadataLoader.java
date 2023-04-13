package com.android.internal.telephony.phonenumbers.metadata.init;

import com.android.internal.telephony.phonenumbers.MetadataLoader;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public final class ClassPathResourceMetadataLoader implements MetadataLoader {
    private static final Logger logger = Logger.getLogger(ClassPathResourceMetadataLoader.class.getName());

    @Override // com.android.internal.telephony.phonenumbers.MetadataLoader
    public InputStream loadMetadata(String str) {
        InputStream resourceAsStream = ClassPathResourceMetadataLoader.class.getResourceAsStream(str);
        if (resourceAsStream == null) {
            logger.log(Level.WARNING, String.format("File %s not found", str));
        }
        return resourceAsStream;
    }
}
