package android.accessibilityservice;

import android.accessibilityservice.util.AccessibilityUtils;
import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.AttributeSet;
import android.util.Xml;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class AccessibilityShortcutInfo {
    public static final String META_DATA = "android.accessibilityshortcut.target";
    private static final String TAG_ACCESSIBILITY_SHORTCUT = "accessibility-shortcut-target";
    private final ActivityInfo mActivityInfo;
    private final int mAnimatedImageRes;
    private final ComponentName mComponentName;
    private final int mDescriptionResId;
    private final int mHtmlDescriptionRes;
    private final int mIntroResId;
    private String mSettingsActivityName;
    private final int mSummaryResId;
    private String mTileServiceName;

    public AccessibilityShortcutInfo(Context context, ActivityInfo activityInfo) throws XmlPullParserException, IOException {
        PackageManager packageManager = context.getPackageManager();
        this.mComponentName = activityInfo.getComponentName();
        this.mActivityInfo = activityInfo;
        try {
            XmlResourceParser parser = activityInfo.loadXmlMetaData(packageManager, META_DATA);
            if (parser == null) {
                throw new XmlPullParserException("Meta-data accessibility-shortcut-target does not exist");
            }
            for (int type = 0; type != 1 && type != 2; type = parser.next()) {
            }
            String nodeName = parser.getName();
            if (!TAG_ACCESSIBILITY_SHORTCUT.equals(nodeName)) {
                throw new XmlPullParserException("Meta-data does not start withaccessibility-shortcut-target tag");
            }
            AttributeSet allAttributes = Xml.asAttributeSet(parser);
            Resources resources = packageManager.getResourcesForApplication(this.mActivityInfo.applicationInfo);
            TypedArray asAttributes = resources.obtainAttributes(allAttributes, C4057R.styleable.AccessibilityShortcutTarget);
            this.mDescriptionResId = asAttributes.getResourceId(0, 0);
            this.mSummaryResId = asAttributes.getResourceId(1, 0);
            this.mAnimatedImageRes = asAttributes.getResourceId(3, 0);
            this.mHtmlDescriptionRes = asAttributes.getResourceId(4, 0);
            this.mSettingsActivityName = asAttributes.getString(2);
            this.mTileServiceName = asAttributes.getString(5);
            this.mIntroResId = asAttributes.getResourceId(6, 0);
            asAttributes.recycle();
            if (parser != null) {
                parser.close();
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new XmlPullParserException("Unable to create context for: " + this.mActivityInfo.packageName);
        }
    }

    public ActivityInfo getActivityInfo() {
        return this.mActivityInfo;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public String loadSummary(PackageManager packageManager) {
        return loadResourceString(packageManager, this.mActivityInfo, this.mSummaryResId);
    }

    public String loadIntro(PackageManager packageManager) {
        return loadResourceString(packageManager, this.mActivityInfo, this.mIntroResId);
    }

    public String loadDescription(PackageManager packageManager) {
        return loadResourceString(packageManager, this.mActivityInfo, this.mDescriptionResId);
    }

    public int getAnimatedImageRes() {
        return this.mAnimatedImageRes;
    }

    public Drawable loadAnimatedImage(Context context) {
        if (this.mAnimatedImageRes == 0) {
            return null;
        }
        return AccessibilityUtils.loadSafeAnimatedImage(context, this.mActivityInfo.applicationInfo, this.mAnimatedImageRes);
    }

    public String loadHtmlDescription(PackageManager packageManager) {
        String htmlDescription = loadResourceString(packageManager, this.mActivityInfo, this.mHtmlDescriptionRes);
        if (htmlDescription != null) {
            return AccessibilityUtils.getFilteredHtmlText(htmlDescription);
        }
        return null;
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public String getTileServiceName() {
        return this.mTileServiceName;
    }

    private String loadResourceString(PackageManager packageManager, ActivityInfo activityInfo, int resId) {
        CharSequence text;
        if (resId == 0 || (text = packageManager.getText(activityInfo.packageName, resId, activityInfo.applicationInfo)) == null) {
            return null;
        }
        return text.toString().trim();
    }

    public int hashCode() {
        ComponentName componentName = this.mComponentName;
        return (componentName == null ? 0 : componentName.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityShortcutInfo other = (AccessibilityShortcutInfo) obj;
        ComponentName componentName = this.mComponentName;
        if (componentName == null) {
            if (other.mComponentName != null) {
                return false;
            }
        } else if (!componentName.equals(other.mComponentName)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AccessibilityShortcutInfo[");
        stringBuilder.append("activityInfo: ").append(this.mActivityInfo);
        stringBuilder.append(NavigationBarInflaterView.SIZE_MOD_END);
        return stringBuilder.toString();
    }
}
