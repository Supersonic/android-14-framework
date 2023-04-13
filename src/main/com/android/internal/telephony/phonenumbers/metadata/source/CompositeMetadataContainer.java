package com.android.internal.telephony.phonenumbers.metadata.source;

import com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata;
import com.android.internal.telephony.phonenumbers.internal.GeoEntityUtility;
/* loaded from: classes.dex */
final class CompositeMetadataContainer implements MetadataContainer {
    private final MapBackedMetadataContainer<Integer> metadataByCountryCode = MapBackedMetadataContainer.byCountryCallingCode();
    private final MapBackedMetadataContainer<String> metadataByRegionCode = MapBackedMetadataContainer.byRegionCode();

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata$PhoneMetadata getMetadataBy(String str) {
        return this.metadataByRegionCode.getMetadataBy(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata$PhoneMetadata getMetadataBy(int i) {
        return this.metadataByCountryCode.getMetadataBy(Integer.valueOf(i));
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.MetadataContainer
    public void accept(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
        if (GeoEntityUtility.isGeoEntity(this.metadataByRegionCode.getKeyProvider().getKeyOf(phonemetadata$PhoneMetadata))) {
            this.metadataByRegionCode.accept(phonemetadata$PhoneMetadata);
        } else {
            this.metadataByCountryCode.accept(phonemetadata$PhoneMetadata);
        }
    }
}
