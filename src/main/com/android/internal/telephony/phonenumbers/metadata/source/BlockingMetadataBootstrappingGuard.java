package com.android.internal.telephony.phonenumbers.metadata.source;

import com.android.internal.telephony.phonenumbers.MetadataLoader;
import com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata;
import com.android.internal.telephony.phonenumbers.metadata.init.MetadataParser;
import com.android.internal.telephony.phonenumbers.metadata.source.MetadataContainer;
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
    public BlockingMetadataBootstrappingGuard(MetadataLoader metadataLoader, MetadataParser metadataParser, T t) {
        this.metadataLoader = metadataLoader;
        this.metadataParser = metadataParser;
        this.metadataContainer = t;
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.MetadataBootstrappingGuard
    public T getOrBootstrap(String str) {
        if (!this.loadedFiles.containsKey(str)) {
            bootstrapMetadata(str);
        }
        return this.metadataContainer;
    }

    private synchronized void bootstrapMetadata(String str) {
        if (this.loadedFiles.containsKey(str)) {
            return;
        }
        for (Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata : read(str)) {
            this.metadataContainer.accept(phonemetadata$PhoneMetadata);
        }
        this.loadedFiles.put(str, str);
    }

    private Collection<Phonemetadata$PhoneMetadata> read(String str) {
        try {
            return this.metadataParser.parse(this.metadataLoader.loadMetadata(str));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalStateException("Failed to read file " + str, e);
        }
    }
}
