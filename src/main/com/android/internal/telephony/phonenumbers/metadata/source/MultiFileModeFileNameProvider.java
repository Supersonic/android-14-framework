package com.android.internal.telephony.phonenumbers.metadata.source;

import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class MultiFileModeFileNameProvider implements PhoneMetadataFileNameProvider {
    private static final Pattern ALPHANUMERIC = Pattern.compile("^[\\p{L}\\p{N}]+$");
    private final String phoneMetadataFileNamePrefix;

    public MultiFileModeFileNameProvider(String str) {
        this.phoneMetadataFileNamePrefix = str + "_";
    }

    @Override // com.android.internal.telephony.phonenumbers.metadata.source.PhoneMetadataFileNameProvider
    public String getFor(Object obj) {
        String obj2 = obj.toString();
        if (!ALPHANUMERIC.matcher(obj2).matches()) {
            throw new IllegalArgumentException("Invalid key: " + obj2);
        }
        return this.phoneMetadataFileNamePrefix + obj;
    }
}
