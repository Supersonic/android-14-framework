package android.app;

import android.content.Intent;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.p008os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
@Deprecated
/* loaded from: classes.dex */
public class AliasActivity extends Activity {
    public final String ALIAS_META_DATA = "android.app.alias";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XmlResourceParser parser = null;
        try {
            try {
                try {
                    ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(), 128);
                    parser = ai.loadXmlMetaData(getPackageManager(), "android.app.alias");
                    if (parser == null) {
                        throw new RuntimeException("Alias requires a meta-data field android.app.alias");
                    }
                    Intent intent = parseAlias(parser);
                    if (intent == null) {
                        throw new RuntimeException("No <intent> tag found in alias description");
                    }
                    startActivity(intent);
                    finish();
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException("Error parsing alias", e);
                }
            } catch (IOException e2) {
                throw new RuntimeException("Error parsing alias", e2);
            } catch (XmlPullParserException e3) {
                throw new RuntimeException("Error parsing alias", e3);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    private Intent parseAlias(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        Intent intent = null;
        do {
            type = parser.next();
            if (type == 1) {
                break;
            }
        } while (type != 2);
        String nodeName = parser.getName();
        if (!"alias".equals(nodeName)) {
            throw new RuntimeException("Alias meta-data must start with <alias> tag; found" + nodeName + " at " + parser.getPositionDescription());
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int type2 = parser.next();
            if (type2 == 1 || (type2 == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type2 != 3 && type2 != 4) {
                if ("intent".equals(parser.getName())) {
                    Intent gotIntent = Intent.parseIntent(getResources(), parser, attrs);
                    if (intent == null) {
                        intent = gotIntent;
                    }
                } else {
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        return intent;
    }
}
