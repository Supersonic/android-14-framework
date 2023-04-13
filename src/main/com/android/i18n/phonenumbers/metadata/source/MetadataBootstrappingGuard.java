package com.android.i18n.phonenumbers.metadata.source;

import com.android.i18n.phonenumbers.metadata.source.MetadataContainer;
/* loaded from: classes.dex */
public interface MetadataBootstrappingGuard<T extends MetadataContainer> {
    T getOrBootstrap(String str);
}
