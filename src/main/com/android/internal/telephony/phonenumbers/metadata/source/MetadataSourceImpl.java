package com.android.internal.telephony.phonenumbers.metadata.source;

import com.android.internal.telephony.phonenumbers.MetadataLoader;
import com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata;
import com.android.internal.telephony.phonenumbers.internal.GeoEntityUtility;
import com.android.internal.telephony.phonenumbers.metadata.init.MetadataParser;
/* loaded from: classes.dex */
public final class MetadataSourceImpl implements MetadataSource {
    private final MetadataBootstrappingGuard<CompositeMetadataContainer> bootstrappingGuard;
    private final PhoneMetadataFileNameProvider phoneMetadataFileNameProvider;

    public MetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataBootstrappingGuard<CompositeMetadataContainer> metadataBootstrappingGuard) {
        this.phoneMetadataFileNameProvider = phoneMetadataFileNameProvider;
        this.bootstrappingGuard = metadataBootstrappingGuard;
    }

    public MetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataLoader metadataLoader, MetadataParser metadataParser) {
        this(phoneMetadataFileNameProvider, new BlockingMetadataBootstrappingGuard(metadataLoader, metadataParser, new CompositeMetadataContainer()));
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.NonGeographicalEntityMetadataSource
    public Phonemetadata$PhoneMetadata getMetadataForNonGeographicalRegion(int i) {
        if (GeoEntityUtility.isGeoEntity(i)) {
            throw new IllegalArgumentException(i + " calling code belongs to a geo entity");
        }
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(Integer.valueOf(i))).getMetadataBy(i);
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.RegionMetadataSource
    public Phonemetadata$PhoneMetadata getMetadataForRegion(String str) {
        if (!GeoEntityUtility.isGeoEntity(str)) {
            throw new IllegalArgumentException(str + " region code is a non-geo entity");
        }
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(str)).getMetadataBy(str);
    }
}
