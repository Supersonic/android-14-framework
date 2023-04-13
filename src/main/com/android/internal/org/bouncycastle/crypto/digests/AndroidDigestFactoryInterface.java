package com.android.internal.org.bouncycastle.crypto.digests;

import com.android.internal.org.bouncycastle.crypto.Digest;
/* loaded from: classes4.dex */
interface AndroidDigestFactoryInterface {
    Digest getMD5();

    Digest getSHA1();

    Digest getSHA224();

    Digest getSHA256();

    Digest getSHA384();

    Digest getSHA512();
}
