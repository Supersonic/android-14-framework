package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import java.io.IOException;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class CategoryFilter implements Filter {
    public static final FilterFactory FACTORY = new FilterFactory("category") { // from class: com.android.server.firewall.CategoryFilter.1
        @Override // com.android.server.firewall.FilterFactory
        public Filter newFilter(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
            String attributeValue = xmlPullParser.getAttributeValue(null, "name");
            if (attributeValue == null) {
                throw new XmlPullParserException("Category name must be specified.", xmlPullParser, null);
            }
            return new CategoryFilter(attributeValue);
        }
    };
    public final String mCategoryName;

    public CategoryFilter(String str) {
        this.mCategoryName = str;
    }

    @Override // com.android.server.firewall.Filter
    public boolean matches(IntentFirewall intentFirewall, ComponentName componentName, Intent intent, int i, int i2, String str, int i3) {
        Set<String> categories = intent.getCategories();
        if (categories == null) {
            return false;
        }
        return categories.contains(this.mCategoryName);
    }
}
