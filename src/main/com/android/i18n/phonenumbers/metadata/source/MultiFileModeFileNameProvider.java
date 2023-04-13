package com.android.i18n.phonenumbers.metadata.source;

import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class MultiFileModeFileNameProvider implements PhoneMetadataFileNameProvider {
    private static final Pattern ALPHANUMERIC = Pattern.compile("^[\\p{L}\\p{N}]+$");
    private final String phoneMetadataFileNamePrefix;

    public MultiFileModeFileNameProvider(String phoneMetadataFileNameBase) {
        this.phoneMetadataFileNamePrefix = phoneMetadataFileNameBase + "_";
    }

    @Override // com.android.i18n.phonenumbers.metadata.source.PhoneMetadataFileNameProvider
    public String getFor(Object key) {
        String keyAsString = key.toString();
        if (!ALPHANUMERIC.matcher(keyAsString).matches()) {
            throw new IllegalArgumentException("Invalid key: " + keyAsString);
        }
        return this.phoneMetadataFileNamePrefix + key;
    }
}
