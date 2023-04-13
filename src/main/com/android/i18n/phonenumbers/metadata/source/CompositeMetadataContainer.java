package com.android.i18n.phonenumbers.metadata.source;

import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.internal.GeoEntityUtility;
/* loaded from: classes.dex */
final class CompositeMetadataContainer implements MetadataContainer {
    private final MapBackedMetadataContainer<Integer> metadataByCountryCode = MapBackedMetadataContainer.byCountryCallingCode();
    private final MapBackedMetadataContainer<String> metadataByRegionCode = MapBackedMetadataContainer.byRegionCode();

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata.PhoneMetadata getMetadataBy(String regionCode) {
        return this.metadataByRegionCode.getMetadataBy(regionCode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata.PhoneMetadata getMetadataBy(int countryCallingCode) {
        return this.metadataByCountryCode.getMetadataBy(Integer.valueOf(countryCallingCode));
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.MetadataContainer
    public void accept(Phonemetadata.PhoneMetadata phoneMetadata) {
        String regionCode = this.metadataByRegionCode.getKeyProvider().getKeyOf(phoneMetadata);
        if (GeoEntityUtility.isGeoEntity(regionCode)) {
            this.metadataByRegionCode.accept(phoneMetadata);
        } else {
            this.metadataByCountryCode.accept(phoneMetadata);
        }
    }
}
