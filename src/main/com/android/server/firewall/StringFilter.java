package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.PatternMatcher;
import java.io.IOException;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public abstract class StringFilter implements Filter {
    public final ValueProvider mValueProvider;
    public static final ValueProvider COMPONENT = new ValueProvider("component") { // from class: com.android.server.firewall.StringFilter.1
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            if (componentName != null) {
                return componentName.flattenToString();
            }
            return null;
        }
    };
    public static final ValueProvider COMPONENT_NAME = new ValueProvider("component-name") { // from class: com.android.server.firewall.StringFilter.2
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            if (componentName != null) {
                return componentName.getClassName();
            }
            return null;
        }
    };
    public static final ValueProvider COMPONENT_PACKAGE = new ValueProvider("component-package") { // from class: com.android.server.firewall.StringFilter.3
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            if (componentName != null) {
                return componentName.getPackageName();
            }
            return null;
        }
    };
    public static final FilterFactory ACTION = new ValueProvider("action") { // from class: com.android.server.firewall.StringFilter.4
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            return intent.getAction();
        }
    };
    public static final ValueProvider DATA = new ValueProvider("data") { // from class: com.android.server.firewall.StringFilter.5
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            Uri data = intent.getData();
            if (data != null) {
                return data.toString();
            }
            return null;
        }
    };
    public static final ValueProvider MIME_TYPE = new ValueProvider("mime-type") { // from class: com.android.server.firewall.StringFilter.6
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            return str;
        }
    };
    public static final ValueProvider SCHEME = new ValueProvider("scheme") { // from class: com.android.server.firewall.StringFilter.7
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getScheme();
            }
            return null;
        }
    };
    public static final ValueProvider SSP = new ValueProvider("scheme-specific-part") { // from class: com.android.server.firewall.StringFilter.8
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getSchemeSpecificPart();
            }
            return null;
        }
    };
    public static final ValueProvider HOST = new ValueProvider("host") { // from class: com.android.server.firewall.StringFilter.9
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getHost();
            }
            return null;
        }
    };
    public static final ValueProvider PATH = new ValueProvider("path") { // from class: com.android.server.firewall.StringFilter.10
        @Override // com.android.server.firewall.StringFilter.ValueProvider
        public String getValue(ComponentName componentName, Intent intent, String str) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getPath();
            }
            return null;
        }
    };

    public abstract boolean matchesValue(String str);

    public StringFilter(ValueProvider valueProvider) {
        this.mValueProvider = valueProvider;
    }

    public static StringFilter readFromXml(ValueProvider valueProvider, XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        StringFilter stringFilter = null;
        for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
            StringFilter filter = getFilter(valueProvider, xmlPullParser, i);
            if (filter != null) {
                if (stringFilter != null) {
                    throw new XmlPullParserException("Multiple string filter attributes found");
                }
                stringFilter = filter;
            }
        }
        return stringFilter == null ? new IsNullFilter(valueProvider, false) : stringFilter;
    }

    public static StringFilter getFilter(ValueProvider valueProvider, XmlPullParser xmlPullParser, int i) {
        String attributeName = xmlPullParser.getAttributeName(i);
        char charAt = attributeName.charAt(0);
        if (charAt == 'c') {
            if (attributeName.equals("contains")) {
                return new ContainsFilter(valueProvider, xmlPullParser.getAttributeValue(i));
            }
            return null;
        } else if (charAt == 'e') {
            if (attributeName.equals("equals")) {
                return new EqualsFilter(valueProvider, xmlPullParser.getAttributeValue(i));
            }
            return null;
        } else if (charAt == 'i') {
            if (attributeName.equals("isNull")) {
                return new IsNullFilter(valueProvider, xmlPullParser.getAttributeValue(i));
            }
            return null;
        } else if (charAt == 'p') {
            if (attributeName.equals("pattern")) {
                return new PatternStringFilter(valueProvider, xmlPullParser.getAttributeValue(i));
            }
            return null;
        } else if (charAt != 'r') {
            if (charAt == 's' && attributeName.equals("startsWith")) {
                return new StartsWithFilter(valueProvider, xmlPullParser.getAttributeValue(i));
            }
            return null;
        } else if (attributeName.equals("regex")) {
            return new RegexFilter(valueProvider, xmlPullParser.getAttributeValue(i));
        } else {
            return null;
        }
    }

    @Override // com.android.server.firewall.Filter
    public boolean matches(IntentFirewall intentFirewall, ComponentName componentName, Intent intent, int i, int i2, String str, int i3) {
        return matchesValue(this.mValueProvider.getValue(componentName, intent, str));
    }

    /* loaded from: classes.dex */
    public static abstract class ValueProvider extends FilterFactory {
        public abstract String getValue(ComponentName componentName, Intent intent, String str);

        public ValueProvider(String str) {
            super(str);
        }

        @Override // com.android.server.firewall.FilterFactory
        public Filter newFilter(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
            return StringFilter.readFromXml(this, xmlPullParser);
        }
    }

    /* loaded from: classes.dex */
    public static class EqualsFilter extends StringFilter {
        public final String mFilterValue;

        public EqualsFilter(ValueProvider valueProvider, String str) {
            super(valueProvider);
            this.mFilterValue = str;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String str) {
            return str != null && str.equals(this.mFilterValue);
        }
    }

    /* loaded from: classes.dex */
    public static class ContainsFilter extends StringFilter {
        public final String mFilterValue;

        public ContainsFilter(ValueProvider valueProvider, String str) {
            super(valueProvider);
            this.mFilterValue = str;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String str) {
            return str != null && str.contains(this.mFilterValue);
        }
    }

    /* loaded from: classes.dex */
    public static class StartsWithFilter extends StringFilter {
        public final String mFilterValue;

        public StartsWithFilter(ValueProvider valueProvider, String str) {
            super(valueProvider);
            this.mFilterValue = str;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String str) {
            return str != null && str.startsWith(this.mFilterValue);
        }
    }

    /* loaded from: classes.dex */
    public static class PatternStringFilter extends StringFilter {
        public final PatternMatcher mPattern;

        public PatternStringFilter(ValueProvider valueProvider, String str) {
            super(valueProvider);
            this.mPattern = new PatternMatcher(str, 2);
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String str) {
            return str != null && this.mPattern.match(str);
        }
    }

    /* loaded from: classes.dex */
    public static class RegexFilter extends StringFilter {
        public final Pattern mPattern;

        public RegexFilter(ValueProvider valueProvider, String str) {
            super(valueProvider);
            this.mPattern = Pattern.compile(str);
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String str) {
            return str != null && this.mPattern.matcher(str).matches();
        }
    }

    /* loaded from: classes.dex */
    public static class IsNullFilter extends StringFilter {
        public final boolean mIsNull;

        public IsNullFilter(ValueProvider valueProvider, String str) {
            super(valueProvider);
            this.mIsNull = Boolean.parseBoolean(str);
        }

        public IsNullFilter(ValueProvider valueProvider, boolean z) {
            super(valueProvider);
            this.mIsNull = z;
        }

        @Override // com.android.server.firewall.StringFilter
        public boolean matchesValue(String str) {
            return (str == null) == this.mIsNull;
        }
    }
}
