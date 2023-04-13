package com.android.internal.telephony.phonenumbers.metadata.source;

import com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes.dex */
final class MapBackedMetadataContainer<T> implements MetadataContainer {
    private final KeyProvider<T> keyProvider;
    private final ConcurrentMap<T, Phonemetadata$PhoneMetadata> metadataMap = new ConcurrentHashMap();

    /* loaded from: classes.dex */
    interface KeyProvider<T> {
        T getKeyOf(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MapBackedMetadataContainer<String> byRegionCode() {
        return new MapBackedMetadataContainer<>(new KeyProvider<String>() { // from class: com.android.internal.telephony.phonenumbers.metadata.source.MapBackedMetadataContainer.1
            @Override // com.android.internal.telephony.phonenumbers.metadata.source.MapBackedMetadataContainer.KeyProvider
            public String getKeyOf(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
                return phonemetadata$PhoneMetadata.getId();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MapBackedMetadataContainer<Integer> byCountryCallingCode() {
        return new MapBackedMetadataContainer<>(new KeyProvider<Integer>() { // from class: com.android.internal.telephony.phonenumbers.metadata.source.MapBackedMetadataContainer.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.internal.telephony.phonenumbers.metadata.source.MapBackedMetadataContainer.KeyProvider
            public Integer getKeyOf(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
                return Integer.valueOf(phonemetadata$PhoneMetadata.getCountryCode());
            }
        });
    }

    private MapBackedMetadataContainer(KeyProvider<T> keyProvider) {
        this.keyProvider = keyProvider;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata$PhoneMetadata getMetadataBy(T t) {
        if (t != null) {
            return this.metadataMap.get(t);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyProvider<T> getKeyProvider() {
        return this.keyProvider;
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.MetadataContainer
    public void accept(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
        this.metadataMap.put(this.keyProvider.getKeyOf(phonemetadata$PhoneMetadata), phonemetadata$PhoneMetadata);
    }
}
