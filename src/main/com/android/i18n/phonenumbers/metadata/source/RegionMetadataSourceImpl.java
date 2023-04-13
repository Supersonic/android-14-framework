package com.android.i18n.phonenumbers.metadata.source;

import com.android.i18n.phonenumbers.MetadataLoader;
import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.internal.GeoEntityUtility;
import com.android.i18n.phonenumbers.metadata.init.MetadataParser;
/* loaded from: classes.dex */
public final class RegionMetadataSourceImpl implements RegionMetadataSource {
    private final MetadataBootstrappingGuard<MapBackedMetadataContainer<String>> bootstrappingGuard;
    private final PhoneMetadataFileNameProvider phoneMetadataFileNameProvider;

    public RegionMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataBootstrappingGuard<MapBackedMetadataContainer<String>> bootstrappingGuard) {
        this.phoneMetadataFileNameProvider = phoneMetadataFileNameProvider;
        this.bootstrappingGuard = bootstrappingGuard;
    }

    public RegionMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataLoader metadataLoader, MetadataParser metadataParser) {
        this(phoneMetadataFileNameProvider, new BlockingMetadataBootstrappingGuard(metadataLoader, metadataParser, MapBackedMetadataContainer.byRegionCode()));
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.RegionMetadataSource
    public Phonemetadata.PhoneMetadata getMetadataForRegion(String regionCode) {
        if (!GeoEntityUtility.isGeoEntity(regionCode)) {
            throw new IllegalArgumentException(regionCode + " region code is a non-geo entity");
        }
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(regionCode)).getMetadataBy(regionCode);
    }
}
