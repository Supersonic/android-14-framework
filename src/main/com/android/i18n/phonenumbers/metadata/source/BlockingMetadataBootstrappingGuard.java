package com.android.i18n.phonenumbers.metadata.source;

import com.android.i18n.phonenumbers.MetadataLoader;
import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.metadata.init.MetadataParser;
import com.android.i18n.phonenumbers.metadata.source.MetadataContainer;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes.dex */
final class BlockingMetadataBootstrappingGuard<T extends MetadataContainer> implements MetadataBootstrappingGuard<T> {
    private final Map<String, String> loadedFiles = new ConcurrentHashMap();
    private final T metadataContainer;
    private final MetadataLoader metadataLoader;
    private final MetadataParser metadataParser;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BlockingMetadataBootstrappingGuard(MetadataLoader metadataLoader, MetadataParser metadataParser, T metadataContainer) {
        this.metadataLoader = metadataLoader;
        this.metadataParser = metadataParser;
        this.metadataContainer = metadataContainer;
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.MetadataBootstrappingGuard
    public T getOrBootstrap(String phoneMetadataFile) {
        if (!this.loadedFiles.containsKey(phoneMetadataFile)) {
            bootstrapMetadata(phoneMetadataFile);
        }
        return this.metadataContainer;
    }

    private synchronized void bootstrapMetadata(String phoneMetadataFile) {
        if (this.loadedFiles.containsKey(phoneMetadataFile)) {
            return;
        }
        Collection<Phonemetadata.PhoneMetadata> phoneMetadata = read(phoneMetadataFile);
        for (Phonemetadata.PhoneMetadata metadata : phoneMetadata) {
            this.metadataContainer.accept(metadata);
        }
        this.loadedFiles.put(phoneMetadataFile, phoneMetadataFile);
    }

    private Collection<Phonemetadata.PhoneMetadata> read(String phoneMetadataFile) {
        try {
            InputStream metadataStream = this.metadataLoader.loadMetadata(phoneMetadataFile);
            return this.metadataParser.parse(metadataStream);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalStateException("Failed to read file " + phoneMetadataFile, e);
        }
    }
}
