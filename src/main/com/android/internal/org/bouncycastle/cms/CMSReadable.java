package com.android.internal.org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
interface CMSReadable {
    InputStream getInputStream() throws IOException, CMSException;
}
