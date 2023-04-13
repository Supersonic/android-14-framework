package com.android.internal.telephony.phonenumbers.metadata.source;
/* loaded from: classes.dex */
public final class SingleFileModeFileNameProvider implements PhoneMetadataFileNameProvider {
    private final String phoneMetadataFileName;

    public SingleFileModeFileNameProvider(String str) {
        this.phoneMetadataFileName = str;
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.PhoneMetadataFileNameProvider
    public String getFor(Object obj) {
        return this.phoneMetadataFileName;
    }
}
