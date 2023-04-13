package com.android.internal.util;

import com.android.modules.utils.BinaryXmlPullParser;
import com.android.modules.utils.FastDataInput;
import java.io.InputStream;
/* loaded from: classes3.dex */
public class ArtBinaryXmlPullParser extends BinaryXmlPullParser {
    @Override // com.android.modules.utils.BinaryXmlPullParser
    protected FastDataInput obtainFastDataInput(InputStream is) {
        return ArtFastDataInput.obtain(is);
    }
}
