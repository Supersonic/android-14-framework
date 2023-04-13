package com.android.server.firewall;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public abstract class FilterFactory {
    public final String mTag;

    public abstract Filter newFilter(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException;

    public FilterFactory(String str) {
        str.getClass();
        this.mTag = str;
    }

    public String getTagName() {
        return this.mTag;
    }
}
