package android.content.res;

import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public interface XmlResourceParser extends XmlPullParser, AttributeSet, AutoCloseable {
    void close();

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    String getAttributeNamespace(int i);
}
