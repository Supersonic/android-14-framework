package com.android.i18n.phonenumbers.metadata.init;

import com.android.i18n.phonenumbers.MetadataLoader;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public final class ClassPathResourceMetadataLoader implements MetadataLoader {
    private static final Logger logger = Logger.getLogger(ClassPathResourceMetadataLoader.class.getName());

    @Override // com.android.i18n.phonenumbers.MetadataLoader
    public InputStream loadMetadata(String metadataFileName) {
        InputStream inputStream = ClassPathResourceMetadataLoader.class.getResourceAsStream(metadataFileName);
        if (inputStream == null) {
            logger.log(Level.WARNING, String.format("File %s not found", metadataFileName));
        }
        return inputStream;
    }
}
