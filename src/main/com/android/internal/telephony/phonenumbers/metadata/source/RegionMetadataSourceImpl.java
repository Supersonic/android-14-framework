package com.android.internal.telephony.phonenumbers.metadata.source;

import com.android.internal.telephony.phonenumbers.MetadataLoader;
import com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata;
import com.android.internal.telephony.phonenumbers.internal.GeoEntityUtility;
import com.android.internal.telephony.phonenumbers.metadata.init.MetadataParser;
/* loaded from: classes.dex */
public final class RegionMetadataSourceImpl implements RegionMetadataSource {
    private final MetadataBootstrappingGuard<MapBackedMetadataContainer<String>> bootstrappingGuard;
    private final PhoneMetadataFileNameProvider phoneMetadataFileNameProvider;

    public RegionMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataBootstrappingGuard<MapBackedMetadataContainer<String>> metadataBootstrappingGuard) {
        this.phoneMetadataFileNameProvider = phoneMetadataFileNameProvider;
        this.bootstrappingGuard = metadataBootstrappingGuard;
    }

    public RegionMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataLoader metadataLoader, MetadataParser metadataParser) {
        this(phoneMetadataFileNameProvider, new BlockingMetadataBootstrappingGuard(metadataLoader, metadataParser, MapBackedMetadataContainer.byRegionCode()));
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.RegionMetadataSource
    public Phonemetadata$PhoneMetadata getMetadataForRegion(String str) {
        if (!GeoEntityUtility.isGeoEntity(str)) {
            throw new IllegalArgumentException(str + " region code is a non-geo entity");
        }
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(str)).getMetadataBy(str);
    }
}
