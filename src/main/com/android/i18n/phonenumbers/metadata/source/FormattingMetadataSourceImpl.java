package com.android.i18n.phonenumbers.metadata.source;

import com.android.i18n.phonenumbers.MetadataLoader;
import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.metadata.init.MetadataParser;
/* loaded from: classes.dex */
public final class FormattingMetadataSourceImpl implements FormattingMetadataSource {
    private final MetadataBootstrappingGuard<MapBackedMetadataContainer<Integer>> bootstrappingGuard;
    private final PhoneMetadataFileNameProvider phoneMetadataFileNameProvider;

    public FormattingMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataBootstrappingGuard<MapBackedMetadataContainer<Integer>> bootstrappingGuard) {
        this.phoneMetadataFileNameProvider = phoneMetadataFileNameProvider;
        this.bootstrappingGuard = bootstrappingGuard;
    }

    public FormattingMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataLoader metadataLoader, MetadataParser metadataParser) {
        this(phoneMetadataFileNameProvider, new BlockingMetadataBootstrappingGuard(metadataLoader, metadataParser, MapBackedMetadataContainer.byCountryCallingCode()));
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.FormattingMetadataSource
    public Phonemetadata.PhoneMetadata getFormattingMetadataForCountryCallingCode(int countryCallingCode) {
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(Integer.valueOf(countryCallingCode))).getMetadataBy(Integer.valueOf(countryCallingCode));
    }
}
