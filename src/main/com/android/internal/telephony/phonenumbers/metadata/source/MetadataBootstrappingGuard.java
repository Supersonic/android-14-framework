package com.android.internal.telephony.phonenumbers.metadata.source;

import com.android.internal.telephony.phonenumbers.metadata.source.MetadataContainer;
/* loaded from: classes.dex */
public interface MetadataBootstrappingGuard<T extends MetadataContainer> {
    T getOrBootstrap(String str);
}
