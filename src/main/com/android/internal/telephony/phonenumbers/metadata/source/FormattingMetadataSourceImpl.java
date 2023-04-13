package com.android.internal.telephony.phonenumbers.metadata.source;

import com.android.internal.telephony.phonenumbers.MetadataLoader;
import com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata;
import com.android.internal.telephony.phonenumbers.metadata.init.MetadataParser;
/* loaded from: classes.dex */
public final class FormattingMetadataSourceImpl implements FormattingMetadataSource {
    private final MetadataBootstrappingGuard<MapBackedMetadataContainer<Integer>> bootstrappingGuard;
    private final PhoneMetadataFileNameProvider phoneMetadataFileNameProvider;

    public FormattingMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataBootstrappingGuard<MapBackedMetadataContainer<Integer>> metadataBootstrappingGuard) {
        this.phoneMetadataFileNameProvider = phoneMetadataFileNameProvider;
        this.bootstrappingGuard = metadataBootstrappingGuard;
    }

    public FormattingMetadataSourceImpl(PhoneMetadataFileNameProvider phoneMetadataFileNameProvider, MetadataLoader metadataLoader, MetadataParser metadataParser) {
        this(phoneMetadataFileNameProvider, new BlockingMetadataBootstrappingGuard(metadataLoader, metadataParser, MapBackedMetadataContainer.byCountryCallingCode()));
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.FormattingMetadataSource
    public Phonemetadata$PhoneMetadata getFormattingMetadataForCountryCallingCode(int i) {
        return this.bootstrappingGuard.getOrBootstrap(this.phoneMetadataFileNameProvider.getFor(Integer.valueOf(i))).getMetadataBy(Integer.valueOf(i));
    }
}
