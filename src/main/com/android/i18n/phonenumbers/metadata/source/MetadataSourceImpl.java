package com.android.i18n.phonenumbers.metadata.source;

import com.android.i18n.phonenumbers.MetadataLoader;
import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.internal.GeoEntityUtility;
import com.android.i18n.phonenumbers.metadata.init.MetadataParser;
/* loaded from: classes.dex */
public final class MetadataSourceImpl implements MetadataSource {
    private final MetadataBootstrappingGuard<CompositeMetadataContainer> bootstrappingGuard;
    private final PhoneMetadataFileNameProvider phoneMetadataFileNameProvider;

    public MetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataBootstrappingGuard<CompositeMetadataContainer> bootstrappingGuard) {
        this.phoneMetadataFileNameProvider = phoneMetadataFileNameProvider;
        this.bootstrappingGuard = bootstrappingGuard;
    }

    public MetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataLoader metadataLoader, MetadataParser metadataParser) {
        this(phoneMetadataFileNameProvider, new BlockingMetadataBootstrappingGuard(metadataLoader, metadataParser, new CompositeMetadataContainer()));
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.NonGeographicalEntityMetadataSource
    public Phonemetadata.PhoneMetadata getMetadataForNonGeographicalRegion(int countryCallingCode) {
        if (GeoEntityUtility.isGeoEntity(countryCallingCode)) {
            throw new IllegalArgumentException(countryCallingCode + " calling code belongs to a geo entity");
        }
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(Integer.valueOf(countryCallingCode))).getMetadataBy(countryCallingCode);
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.RegionMetadataSource
    public Phonemetadata.PhoneMetadata getMetadataForRegion(String regionCode) {
        if (!GeoEntityUtility.isGeoEntity(regionCode)) {
            throw new IllegalArgumentException(regionCode + " region code is a non-geo entity");
        }
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(regionCode)).getMetadataBy(regionCode);
    }
}
