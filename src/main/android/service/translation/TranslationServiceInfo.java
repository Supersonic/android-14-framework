package android.service.translation;

import android.Manifest;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.C4057R;
import java.io.IOException;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes3.dex */
public final class TranslationServiceInfo {
    private static final String TAG = "TranslationServiceInfo";
    private static final String XML_TAG_SERVICE = "translation-service";
    private final ServiceInfo mServiceInfo;
    private final String mSettingsActivity;

    private static ServiceInfo getServiceInfoOrThrow(ComponentName comp, boolean isTemp, int userId) throws PackageManager.NameNotFoundException {
        int flags = isTemp ? 128 : 128 | 1048576;
        ServiceInfo si = null;
        try {
            si = AppGlobals.getPackageManager().getServiceInfo(comp, flags, userId);
        } catch (RemoteException e) {
        }
        if (si == null) {
            throw new PackageManager.NameNotFoundException("Could not get serviceInfo for " + (isTemp ? " (temp)" : "(default system)") + " " + comp.flattenToShortString());
        }
        return si;
    }

    public ServiceInfo getServiceInfo() {
        return this.mServiceInfo;
    }

    public String getSettingsActivity() {
        return this.mSettingsActivity;
    }

    public TranslationServiceInfo(Context context, ComponentName comp, boolean isTemporaryService, int userId) throws PackageManager.NameNotFoundException {
        this(context, getServiceInfoOrThrow(comp, isTemporaryService, userId));
    }

    private TranslationServiceInfo(Context context, ServiceInfo si) {
        if (!Manifest.C0000permission.BIND_TRANSLATION_SERVICE.equals(si.permission)) {
            Slog.m90w(TAG, "TranslationServiceInfo from '" + si.packageName + "' does not require permission " + Manifest.C0000permission.BIND_TRANSLATION_SERVICE);
            throw new SecurityException("Service does not require permission android.permission.BIND_TRANSLATION_SERVICE");
        }
        this.mServiceInfo = si;
        XmlResourceParser parser = si.loadXmlMetaData(context.getPackageManager(), TranslationService.SERVICE_META_DATA);
        if (parser == null) {
            this.mSettingsActivity = null;
            return;
        }
        String settingsActivity = null;
        try {
            Resources resources = context.getPackageManager().getResourcesForApplication(si.applicationInfo);
            for (int type = 0; type != 1 && type != 2; type = parser.next()) {
            }
            if (XML_TAG_SERVICE.equals(parser.getName())) {
                AttributeSet allAttributes = Xml.asAttributeSet(parser);
                TypedArray afsAttributes = resources.obtainAttributes(allAttributes, C4057R.styleable.TranslationService);
                settingsActivity = afsAttributes.getString(0);
                if (afsAttributes != null) {
                    afsAttributes.recycle();
                }
            } else {
                Log.m110e(TAG, "Meta-data does not start with translation-service tag");
            }
        } catch (PackageManager.NameNotFoundException | IOException | XmlPullParserException e) {
            Log.m109e(TAG, "Error parsing auto fill service meta-data", e);
        }
        this.mSettingsActivity = settingsActivity;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(NavigationBarInflaterView.SIZE_MOD_START).append(this.mServiceInfo);
        builder.append(", settings:").append(this.mSettingsActivity);
        return builder.toString();
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("Component: ");
        pw.println(getServiceInfo().getComponentName());
        pw.print(prefix);
        pw.print("Settings: ");
        pw.println(this.mSettingsActivity);
    }
}
