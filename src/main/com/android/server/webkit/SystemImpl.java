package com.android.server.webkit;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.RemoteException;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.webkit.UserPackage;
import android.webkit.WebViewFactory;
import android.webkit.WebViewProviderInfo;
import android.webkit.WebViewZygote;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class SystemImpl implements SystemInterface {
    public static final String TAG = "SystemImpl";
    public final WebViewProviderInfo[] mWebViewProviderPackages;

    /* loaded from: classes2.dex */
    public static class LazyHolder {
        public static final SystemImpl INSTANCE = new SystemImpl();
    }

    @Override // com.android.server.webkit.SystemInterface
    public boolean isMultiProcessDefaultEnabled() {
        return true;
    }

    public static SystemImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    public SystemImpl() {
        XmlResourceParser xml;
        ArrayList arrayList = new ArrayList();
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                xml = AppGlobals.getInitialApplication().getResources().getXml(18284551);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException | XmlPullParserException e) {
            e = e;
        }
        try {
            XmlUtils.beginDocument(xml, "webviewproviders");
            int i = 0;
            int i2 = 0;
            while (true) {
                XmlUtils.nextElement(xml);
                String name = xml.getName();
                if (name != null) {
                    if (name.equals("webviewprovider")) {
                        String attributeValue = xml.getAttributeValue(null, "packageName");
                        if (attributeValue == null) {
                            throw new AndroidRuntimeException("WebView provider in framework resources missing package name");
                        }
                        String attributeValue2 = xml.getAttributeValue(null, "description");
                        if (attributeValue2 == null) {
                            throw new AndroidRuntimeException("WebView provider in framework resources missing description");
                        }
                        WebViewProviderInfo webViewProviderInfo = new WebViewProviderInfo(attributeValue, attributeValue2, "true".equals(xml.getAttributeValue(null, "availableByDefault")), "true".equals(xml.getAttributeValue(null, "isFallback")), readSignatures(xml));
                        if (webViewProviderInfo.isFallback) {
                            i2++;
                            if (!webViewProviderInfo.availableByDefault) {
                                throw new AndroidRuntimeException("Each WebView fallback package must be available by default.");
                            }
                            if (i2 > 1) {
                                throw new AndroidRuntimeException("There can be at most one WebView fallback package.");
                            }
                        }
                        i = webViewProviderInfo.availableByDefault ? i + 1 : i;
                        arrayList.add(webViewProviderInfo);
                    } else {
                        Log.e(TAG, "Found an element that is not a WebView provider");
                    }
                } else {
                    xml.close();
                    if (i == 0) {
                        throw new AndroidRuntimeException("There must be at least one WebView package that is available by default");
                    }
                    this.mWebViewProviderPackages = (WebViewProviderInfo[]) arrayList.toArray(new WebViewProviderInfo[arrayList.size()]);
                    return;
                }
            }
        } catch (IOException | XmlPullParserException e2) {
            e = e2;
            throw new AndroidRuntimeException("Error when parsing WebView config " + e);
        } catch (Throwable th2) {
            th = th2;
            xmlResourceParser = xml;
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    @Override // com.android.server.webkit.SystemInterface
    public WebViewProviderInfo[] getWebViewPackages() {
        return this.mWebViewProviderPackages;
    }

    @Override // com.android.server.webkit.SystemInterface
    public long getFactoryPackageVersion(String str) throws PackageManager.NameNotFoundException {
        return AppGlobals.getInitialApplication().getPackageManager().getPackageInfo(str, 2097152).getLongVersionCode();
    }

    public static String[] readSignatures(XmlResourceParser xmlResourceParser) throws IOException, XmlPullParserException {
        ArrayList arrayList = new ArrayList();
        int depth = xmlResourceParser.getDepth();
        while (XmlUtils.nextElementWithin(xmlResourceParser, depth)) {
            if (xmlResourceParser.getName().equals("signature")) {
                arrayList.add(xmlResourceParser.nextText());
            } else {
                Log.e(TAG, "Found an element in a webview provider that is not a signature");
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    @Override // com.android.server.webkit.SystemInterface
    public int onWebViewProviderChanged(PackageInfo packageInfo) {
        return WebViewFactory.onWebViewProviderChanged(packageInfo);
    }

    @Override // com.android.server.webkit.SystemInterface
    public String getUserChosenWebViewProvider(Context context) {
        return Settings.Global.getString(context.getContentResolver(), "webview_provider");
    }

    @Override // com.android.server.webkit.SystemInterface
    public void updateUserSetting(Context context, String str) {
        ContentResolver contentResolver = context.getContentResolver();
        if (str == null) {
            str = "";
        }
        Settings.Global.putString(contentResolver, "webview_provider", str);
    }

    @Override // com.android.server.webkit.SystemInterface
    public void killPackageDependents(String str) {
        try {
            ActivityManager.getService().killPackageDependents(str, -1);
        } catch (RemoteException unused) {
        }
    }

    @Override // com.android.server.webkit.SystemInterface
    public void enablePackageForAllUsers(Context context, String str, boolean z) {
        for (UserInfo userInfo : ((UserManager) context.getSystemService("user")).getUsers()) {
            enablePackageForUser(str, z, userInfo.id);
        }
    }

    public final void enablePackageForUser(String str, boolean z, int i) {
        try {
            AppGlobals.getPackageManager().setApplicationEnabledSetting(str, z ? 0 : 3, 0, i, (String) null);
        } catch (RemoteException | IllegalArgumentException e) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Tried to ");
            sb.append(z ? "enable " : "disable ");
            sb.append(str);
            sb.append(" for user ");
            sb.append(i);
            sb.append(": ");
            sb.append(e);
            Log.w(str2, sb.toString());
        }
    }

    @Override // com.android.server.webkit.SystemInterface
    public boolean systemIsDebuggable() {
        return Build.IS_DEBUGGABLE;
    }

    @Override // com.android.server.webkit.SystemInterface
    public PackageInfo getPackageInfoForProvider(WebViewProviderInfo webViewProviderInfo) throws PackageManager.NameNotFoundException {
        return AppGlobals.getInitialApplication().getPackageManager().getPackageInfo(webViewProviderInfo.packageName, 272630976);
    }

    @Override // com.android.server.webkit.SystemInterface
    public List<UserPackage> getPackageInfoForProviderAllUsers(Context context, WebViewProviderInfo webViewProviderInfo) {
        return UserPackage.getPackageInfosAllUsers(context, webViewProviderInfo.packageName, 272630976);
    }

    @Override // com.android.server.webkit.SystemInterface
    public int getMultiProcessSetting(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "webview_multiprocess", 0);
    }

    @Override // com.android.server.webkit.SystemInterface
    public void setMultiProcessSetting(Context context, int i) {
        Settings.Global.putInt(context.getContentResolver(), "webview_multiprocess", i);
    }

    @Override // com.android.server.webkit.SystemInterface
    public void notifyZygote(boolean z) {
        WebViewZygote.setMultiprocessEnabled(z);
    }

    @Override // com.android.server.webkit.SystemInterface
    public void ensureZygoteStarted() {
        WebViewZygote.getProcess();
    }
}
