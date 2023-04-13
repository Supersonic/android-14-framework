package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class NotFilter implements Filter {
    public static final FilterFactory FACTORY = new FilterFactory("not") { // from class: com.android.server.firewall.NotFilter.1
        @Override // com.android.server.firewall.FilterFactory
        public Filter newFilter(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
            int depth = xmlPullParser.getDepth();
            Filter filter = null;
            while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
                Filter parseFilter = IntentFirewall.parseFilter(xmlPullParser);
                if (filter != null) {
                    throw new XmlPullParserException("<not> tag can only contain a single child filter.", xmlPullParser, null);
                }
                filter = parseFilter;
            }
            return new NotFilter(filter);
        }
    };
    public final Filter mChild;

    public NotFilter(Filter filter) {
        this.mChild = filter;
    }

    @Override // com.android.server.firewall.Filter
    public boolean matches(IntentFirewall intentFirewall, ComponentName componentName, Intent intent, int i, int i2, String str, int i3) {
        return !this.mChild.matches(intentFirewall, componentName, intent, i, i2, str, i3);
    }
}
