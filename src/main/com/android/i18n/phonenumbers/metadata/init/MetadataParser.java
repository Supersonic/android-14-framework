package com.android.i18n.phonenumbers.metadata.init;

import com.android.i18n.phonenumbers.Phonemetadata;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public final class MetadataParser {
    private static final Logger logger = Logger.getLogger(MetadataParser.class.getName());
    private final boolean strictMode;

    public static MetadataParser newLenientParser() {
        return new MetadataParser(false);
    }

    public static MetadataParser newStrictParser() {
        return new MetadataParser(true);
    }

    private MetadataParser(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public Collection<Phonemetadata.PhoneMetadata> parse(InputStream source) {
        if (source == null) {
            return handleNullSource();
        }
        try {
            try {
                ObjectInputStream ois = new ObjectInputStream(source);
                Phonemetadata.PhoneMetadataCollection phoneMetadataCollection = new Phonemetadata.PhoneMetadataCollection();
                phoneMetadataCollection.readExternal(ois);
                List<Phonemetadata.PhoneMetadata> phoneMetadata = phoneMetadataCollection.getMetadataList();
                if (phoneMetadata.isEmpty()) {
                    throw new IllegalStateException("Empty metadata");
                }
                List<Phonemetadata.PhoneMetadata> metadataList = phoneMetadataCollection.getMetadataList();
                close(ois);
                return metadataList;
            } catch (IOException e) {
                throw new IllegalStateException("Unable to parse metadata file", e);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                close(null);
            } else {
                close(source);
            }
            throw th;
        }
    }

    private List<Phonemetadata.PhoneMetadata> handleNullSource() {
        if (this.strictMode) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        return Collections.emptyList();
    }

    private void close(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing input stream (ignored)", (Throwable) e);
        }
    }
}
