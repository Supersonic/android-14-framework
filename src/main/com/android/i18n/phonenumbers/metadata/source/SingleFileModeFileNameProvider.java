package com.android.i18n.phonenumbers.metadata.source;
/* loaded from: classes.dex */
public final class SingleFileModeFileNameProvider implements PhoneMetadataFileNameProvider {
    private final String phoneMetadataFileName;

    public SingleFileModeFileNameProvider(String phoneMetadataFileName) {
        this.phoneMetadataFileName = phoneMetadataFileName;
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.PhoneMetadataFileNameProvider
    public String getFor(Object key) {
        return this.phoneMetadataFileName;
    }
}
