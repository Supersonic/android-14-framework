package com.android.internal.org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public interface CMSProcessable {
    Object getContent();

    void write(OutputStream outputStream) throws IOException, CMSException;
}
