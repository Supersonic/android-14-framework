package android.webkit;

import android.annotation.SystemApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.StrictMode;
import android.print.PrintDocumentAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View$InspectionCompanion$$ExternalSyntheticLambda0;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.ViewStructure;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.view.textclassifier.TextClassifier;
import android.view.translation.TranslationCapability;
import android.view.translation.ViewTranslationRequest;
import android.view.translation.ViewTranslationResponse;
import android.widget.AbsoluteLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class WebView extends AbsoluteLayout implements ViewTreeObserver.OnGlobalFocusChangeListener, ViewGroup.OnHierarchyChangeListener, ViewDebug.HierarchyHandler {
    private static final String LOGTAG = "WebView";
    public static final int RENDERER_PRIORITY_BOUND = 1;
    public static final int RENDERER_PRIORITY_IMPORTANT = 2;
    public static final int RENDERER_PRIORITY_WAIVED = 0;
    public static final String SCHEME_GEO = "geo:0,0?q=";
    public static final String SCHEME_MAILTO = "mailto:";
    public static final String SCHEME_TEL = "tel:";
    private static volatile boolean sEnforceThreadChecking = false;
    private FindListenerDistributor mFindListener;
    private WebViewProvider mProvider;
    private final Looper mWebViewThread;

    /* loaded from: classes4.dex */
    public interface FindListener {
        void onFindResultReceived(int i, int i2, boolean z);
    }

    @Deprecated
    /* loaded from: classes4.dex */
    public interface PictureListener {
        @Deprecated
        void onNewPicture(WebView webView, Picture picture);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface RendererPriority {
    }

    /* loaded from: classes4.dex */
    public static abstract class VisualStateCallback {
        public abstract void onComplete(long j);
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<WebView> {
        private int mContentHeightId;
        private int mFaviconId;
        private int mOriginalUrlId;
        private int mProgressId;
        private boolean mPropertiesMapped = false;
        private int mRendererPriorityWaivedWhenNotVisibleId;
        private int mRendererRequestedPriorityId;
        private int mTitleId;
        private int mUrlId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mContentHeightId = propertyMapper.mapInt("contentHeight", 0);
            this.mFaviconId = propertyMapper.mapObject("favicon", 0);
            this.mOriginalUrlId = propertyMapper.mapObject("originalUrl", 0);
            this.mProgressId = propertyMapper.mapInt(Notification.CATEGORY_PROGRESS, 0);
            this.mRendererPriorityWaivedWhenNotVisibleId = propertyMapper.mapBoolean("rendererPriorityWaivedWhenNotVisible", 0);
            SparseArray<String> rendererRequestedPriorityEnumMapping = new SparseArray<>();
            rendererRequestedPriorityEnumMapping.put(0, "waived");
            rendererRequestedPriorityEnumMapping.put(1, "bound");
            rendererRequestedPriorityEnumMapping.put(2, "important");
            Objects.requireNonNull(rendererRequestedPriorityEnumMapping);
            this.mRendererRequestedPriorityId = propertyMapper.mapIntEnum("rendererRequestedPriority", 0, new View$InspectionCompanion$$ExternalSyntheticLambda0(rendererRequestedPriorityEnumMapping));
            this.mTitleId = propertyMapper.mapObject("title", 0);
            this.mUrlId = propertyMapper.mapObject("url", 0);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(WebView node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readInt(this.mContentHeightId, node.getContentHeight());
            propertyReader.readObject(this.mFaviconId, node.getFavicon());
            propertyReader.readObject(this.mOriginalUrlId, node.getOriginalUrl());
            propertyReader.readInt(this.mProgressId, node.getProgress());
            propertyReader.readBoolean(this.mRendererPriorityWaivedWhenNotVisibleId, node.getRendererPriorityWaivedWhenNotVisible());
            propertyReader.readIntEnum(this.mRendererRequestedPriorityId, node.getRendererRequestedPriority());
            propertyReader.readObject(this.mTitleId, node.getTitle());
            propertyReader.readObject(this.mUrlId, node.getUrl());
        }
    }

    /* loaded from: classes4.dex */
    public class WebViewTransport {
        private WebView mWebview;

        public WebViewTransport() {
        }

        public synchronized void setWebView(WebView webview) {
            this.mWebview = webview;
        }

        public synchronized WebView getWebView() {
            return this.mWebview;
        }
    }

    /* loaded from: classes4.dex */
    public static class HitTestResult {
        @Deprecated
        public static final int ANCHOR_TYPE = 1;
        public static final int EDIT_TEXT_TYPE = 9;
        public static final int EMAIL_TYPE = 4;
        public static final int GEO_TYPE = 3;
        @Deprecated
        public static final int IMAGE_ANCHOR_TYPE = 6;
        public static final int IMAGE_TYPE = 5;
        public static final int PHONE_TYPE = 2;
        public static final int SRC_ANCHOR_TYPE = 7;
        public static final int SRC_IMAGE_ANCHOR_TYPE = 8;
        public static final int UNKNOWN_TYPE = 0;
        private String mExtra;
        private int mType = 0;

        @SystemApi
        public void setType(int type) {
            this.mType = type;
        }

        @SystemApi
        public void setExtra(String extra) {
            this.mExtra = extra;
        }

        public int getType() {
            return this.mType;
        }

        public String getExtra() {
            return this.mExtra;
        }
    }

    public WebView(Context context) {
        this(context, null);
    }

    public WebView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842885);
    }

    public WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr, defStyleRes, null, false);
    }

    @Deprecated
    public WebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        this(context, attrs, defStyleAttr, 0, null, privateBrowsing);
    }

    protected WebView(Context context, AttributeSet attrs, int defStyleAttr, Map<String, Object> javaScriptInterfaces, boolean privateBrowsing) {
        this(context, attrs, defStyleAttr, 0, javaScriptInterfaces, privateBrowsing);
    }

    protected WebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Map<String, Object> javaScriptInterfaces, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Looper myLooper = Looper.myLooper();
        this.mWebViewThread = myLooper;
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(1);
        }
        if (getImportantForContentCapture() == 0) {
            setImportantForContentCapture(1);
        }
        if (context == null) {
            throw new IllegalArgumentException("Invalid context argument");
        }
        if (myLooper == null) {
            throw new RuntimeException("WebView cannot be initialized on a thread that has no Looper.");
        }
        sEnforceThreadChecking = context.getApplicationInfo().targetSdkVersion >= 18;
        checkThread();
        ensureProviderCreated();
        this.mProvider.init(javaScriptInterfaces, privateBrowsing);
        CookieSyncManager.setGetInstanceIsAllowed();
    }

    @Deprecated
    public void setHorizontalScrollbarOverlay(boolean overlay) {
    }

    @Deprecated
    public void setVerticalScrollbarOverlay(boolean overlay) {
    }

    @Deprecated
    public boolean overlayHorizontalScrollbar() {
        return true;
    }

    @Deprecated
    public boolean overlayVerticalScrollbar() {
        return false;
    }

    @Deprecated
    public int getVisibleTitleHeight() {
        checkThread();
        return this.mProvider.getVisibleTitleHeight();
    }

    public SslCertificate getCertificate() {
        checkThread();
        return this.mProvider.getCertificate();
    }

    @Deprecated
    public void setCertificate(SslCertificate certificate) {
        checkThread();
        this.mProvider.setCertificate(certificate);
    }

    @Deprecated
    public void savePassword(String host, String username, String password) {
        checkThread();
        this.mProvider.savePassword(host, username, password);
    }

    @Deprecated
    public void setHttpAuthUsernamePassword(String host, String realm, String username, String password) {
        checkThread();
        this.mProvider.setHttpAuthUsernamePassword(host, realm, username, password);
    }

    @Deprecated
    public String[] getHttpAuthUsernamePassword(String host, String realm) {
        checkThread();
        return this.mProvider.getHttpAuthUsernamePassword(host, realm);
    }

    public void destroy() {
        checkThread();
        this.mProvider.destroy();
    }

    @Deprecated
    public static void enablePlatformNotifications() {
    }

    @Deprecated
    public static void disablePlatformNotifications() {
    }

    public static void freeMemoryForTests() {
        getFactory().getStatics().freeMemoryForTests();
    }

    public void setNetworkAvailable(boolean networkUp) {
        checkThread();
        this.mProvider.setNetworkAvailable(networkUp);
    }

    public WebBackForwardList saveState(Bundle outState) {
        checkThread();
        return this.mProvider.saveState(outState);
    }

    @Deprecated
    public boolean savePicture(Bundle b, File dest) {
        checkThread();
        return this.mProvider.savePicture(b, dest);
    }

    @Deprecated
    public boolean restorePicture(Bundle b, File src) {
        checkThread();
        return this.mProvider.restorePicture(b, src);
    }

    public WebBackForwardList restoreState(Bundle inState) {
        checkThread();
        return this.mProvider.restoreState(inState);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        checkThread();
        this.mProvider.loadUrl(url, additionalHttpHeaders);
    }

    public void loadUrl(String url) {
        checkThread();
        this.mProvider.loadUrl(url);
    }

    public void postUrl(String url, byte[] postData) {
        checkThread();
        if (URLUtil.isNetworkUrl(url)) {
            this.mProvider.postUrl(url, postData);
        } else {
            this.mProvider.loadUrl(url);
        }
    }

    public void loadData(String data, String mimeType, String encoding) {
        checkThread();
        this.mProvider.loadData(data, mimeType, encoding);
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        checkThread();
        this.mProvider.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        checkThread();
        this.mProvider.evaluateJavaScript(script, resultCallback);
    }

    public void saveWebArchive(String filename) {
        checkThread();
        this.mProvider.saveWebArchive(filename);
    }

    public void saveWebArchive(String basename, boolean autoname, ValueCallback<String> callback) {
        checkThread();
        this.mProvider.saveWebArchive(basename, autoname, callback);
    }

    public void stopLoading() {
        checkThread();
        this.mProvider.stopLoading();
    }

    public void reload() {
        checkThread();
        this.mProvider.reload();
    }

    public boolean canGoBack() {
        checkThread();
        return this.mProvider.canGoBack();
    }

    public void goBack() {
        checkThread();
        this.mProvider.goBack();
    }

    public boolean canGoForward() {
        checkThread();
        return this.mProvider.canGoForward();
    }

    public void goForward() {
        checkThread();
        this.mProvider.goForward();
    }

    public boolean canGoBackOrForward(int steps) {
        checkThread();
        return this.mProvider.canGoBackOrForward(steps);
    }

    public void goBackOrForward(int steps) {
        checkThread();
        this.mProvider.goBackOrForward(steps);
    }

    public boolean isPrivateBrowsingEnabled() {
        checkThread();
        return this.mProvider.isPrivateBrowsingEnabled();
    }

    public boolean pageUp(boolean top) {
        checkThread();
        return this.mProvider.pageUp(top);
    }

    public boolean pageDown(boolean bottom) {
        checkThread();
        return this.mProvider.pageDown(bottom);
    }

    public void postVisualStateCallback(long requestId, VisualStateCallback callback) {
        checkThread();
        this.mProvider.insertVisualStateCallback(requestId, callback);
    }

    @Deprecated
    public void clearView() {
        checkThread();
        this.mProvider.clearView();
    }

    @Deprecated
    public Picture capturePicture() {
        checkThread();
        return this.mProvider.capturePicture();
    }

    @Deprecated
    public PrintDocumentAdapter createPrintDocumentAdapter() {
        checkThread();
        return this.mProvider.createPrintDocumentAdapter("default");
    }

    public PrintDocumentAdapter createPrintDocumentAdapter(String documentName) {
        checkThread();
        return this.mProvider.createPrintDocumentAdapter(documentName);
    }

    @ViewDebug.ExportedProperty(category = TextClassifier.WIDGET_TYPE_WEBVIEW)
    @Deprecated
    public float getScale() {
        checkThread();
        return this.mProvider.getScale();
    }

    public void setInitialScale(int scaleInPercent) {
        checkThread();
        this.mProvider.setInitialScale(scaleInPercent);
    }

    public void invokeZoomPicker() {
        checkThread();
        this.mProvider.invokeZoomPicker();
    }

    public HitTestResult getHitTestResult() {
        checkThread();
        return this.mProvider.getHitTestResult();
    }

    public void requestFocusNodeHref(Message hrefMsg) {
        checkThread();
        this.mProvider.requestFocusNodeHref(hrefMsg);
    }

    public void requestImageRef(Message msg) {
        checkThread();
        this.mProvider.requestImageRef(msg);
    }

    @ViewDebug.ExportedProperty(category = TextClassifier.WIDGET_TYPE_WEBVIEW)
    public String getUrl() {
        checkThread();
        return this.mProvider.getUrl();
    }

    @ViewDebug.ExportedProperty(category = TextClassifier.WIDGET_TYPE_WEBVIEW)
    public String getOriginalUrl() {
        checkThread();
        return this.mProvider.getOriginalUrl();
    }

    @ViewDebug.ExportedProperty(category = TextClassifier.WIDGET_TYPE_WEBVIEW)
    public String getTitle() {
        checkThread();
        return this.mProvider.getTitle();
    }

    public Bitmap getFavicon() {
        checkThread();
        return this.mProvider.getFavicon();
    }

    public String getTouchIconUrl() {
        return this.mProvider.getTouchIconUrl();
    }

    public int getProgress() {
        checkThread();
        return this.mProvider.getProgress();
    }

    @ViewDebug.ExportedProperty(category = TextClassifier.WIDGET_TYPE_WEBVIEW)
    public int getContentHeight() {
        checkThread();
        return this.mProvider.getContentHeight();
    }

    @ViewDebug.ExportedProperty(category = TextClassifier.WIDGET_TYPE_WEBVIEW)
    public int getContentWidth() {
        return this.mProvider.getContentWidth();
    }

    public void pauseTimers() {
        checkThread();
        this.mProvider.pauseTimers();
    }

    public void resumeTimers() {
        checkThread();
        this.mProvider.resumeTimers();
    }

    public void onPause() {
        checkThread();
        this.mProvider.onPause();
    }

    public void onResume() {
        checkThread();
        this.mProvider.onResume();
    }

    public boolean isPaused() {
        return this.mProvider.isPaused();
    }

    @Deprecated
    public void freeMemory() {
        checkThread();
        this.mProvider.freeMemory();
    }

    public void clearCache(boolean includeDiskFiles) {
        checkThread();
        this.mProvider.clearCache(includeDiskFiles);
    }

    public void clearFormData() {
        checkThread();
        this.mProvider.clearFormData();
    }

    public void clearHistory() {
        checkThread();
        this.mProvider.clearHistory();
    }

    public void clearSslPreferences() {
        checkThread();
        this.mProvider.clearSslPreferences();
    }

    public static void clearClientCertPreferences(Runnable onCleared) {
        getFactory().getStatics().clearClientCertPreferences(onCleared);
    }

    public static void startSafeBrowsing(Context context, ValueCallback<Boolean> callback) {
        getFactory().getStatics().initSafeBrowsing(context, callback);
    }

    public static void setSafeBrowsingWhitelist(List<String> hosts, ValueCallback<Boolean> callback) {
        getFactory().getStatics().setSafeBrowsingWhitelist(hosts, callback);
    }

    public static Uri getSafeBrowsingPrivacyPolicyUrl() {
        return getFactory().getStatics().getSafeBrowsingPrivacyPolicyUrl();
    }

    public WebBackForwardList copyBackForwardList() {
        checkThread();
        return this.mProvider.copyBackForwardList();
    }

    public void setFindListener(FindListener listener) {
        checkThread();
        setupFindListenerIfNeeded();
        this.mFindListener.mUserFindListener = listener;
    }

    public void findNext(boolean forward) {
        checkThread();
        this.mProvider.findNext(forward);
    }

    @Deprecated
    public int findAll(String find) {
        checkThread();
        StrictMode.noteSlowCall("findAll blocks UI: prefer findAllAsync");
        return this.mProvider.findAll(find);
    }

    public void findAllAsync(String find) {
        checkThread();
        this.mProvider.findAllAsync(find);
    }

    @Deprecated
    public boolean showFindDialog(String text, boolean showIme) {
        checkThread();
        return this.mProvider.showFindDialog(text, showIme);
    }

    @Deprecated
    public static String findAddress(String addr) {
        if (addr == null) {
            throw new NullPointerException("addr is null");
        }
        return FindAddress.findAddress(addr);
    }

    public static void enableSlowWholeDocumentDraw() {
        getFactory().getStatics().enableSlowWholeDocumentDraw();
    }

    public void clearMatches() {
        checkThread();
        this.mProvider.clearMatches();
    }

    public void documentHasImages(Message response) {
        checkThread();
        this.mProvider.documentHasImages(response);
    }

    public void setWebViewClient(WebViewClient client) {
        checkThread();
        this.mProvider.setWebViewClient(client);
    }

    public WebViewClient getWebViewClient() {
        checkThread();
        return this.mProvider.getWebViewClient();
    }

    public WebViewRenderProcess getWebViewRenderProcess() {
        checkThread();
        return this.mProvider.getWebViewRenderProcess();
    }

    public void setWebViewRenderProcessClient(Executor executor, WebViewRenderProcessClient webViewRenderProcessClient) {
        checkThread();
        this.mProvider.setWebViewRenderProcessClient(executor, webViewRenderProcessClient);
    }

    public void setWebViewRenderProcessClient(WebViewRenderProcessClient webViewRenderProcessClient) {
        checkThread();
        this.mProvider.setWebViewRenderProcessClient(null, webViewRenderProcessClient);
    }

    public WebViewRenderProcessClient getWebViewRenderProcessClient() {
        checkThread();
        return this.mProvider.getWebViewRenderProcessClient();
    }

    public void setDownloadListener(DownloadListener listener) {
        checkThread();
        this.mProvider.setDownloadListener(listener);
    }

    public void setWebChromeClient(WebChromeClient client) {
        checkThread();
        this.mProvider.setWebChromeClient(client);
    }

    public WebChromeClient getWebChromeClient() {
        checkThread();
        return this.mProvider.getWebChromeClient();
    }

    @Deprecated
    public void setPictureListener(PictureListener listener) {
        checkThread();
        this.mProvider.setPictureListener(listener);
    }

    public void addJavascriptInterface(Object object, String name) {
        checkThread();
        this.mProvider.addJavascriptInterface(object, name);
    }

    public void removeJavascriptInterface(String name) {
        checkThread();
        this.mProvider.removeJavascriptInterface(name);
    }

    public WebMessagePort[] createWebMessageChannel() {
        checkThread();
        return this.mProvider.createWebMessageChannel();
    }

    public void postWebMessage(WebMessage message, Uri targetOrigin) {
        checkThread();
        this.mProvider.postMessageToMainFrame(message, targetOrigin);
    }

    public WebSettings getSettings() {
        checkThread();
        return this.mProvider.getSettings();
    }

    public static void setWebContentsDebuggingEnabled(boolean enabled) {
        getFactory().getStatics().setWebContentsDebuggingEnabled(enabled);
    }

    @Deprecated
    public static synchronized PluginList getPluginList() {
        PluginList pluginList;
        synchronized (WebView.class) {
            pluginList = new PluginList();
        }
        return pluginList;
    }

    public static void setDataDirectorySuffix(String suffix) {
        WebViewFactory.setDataDirectorySuffix(suffix);
    }

    public static void disableWebView() {
        WebViewFactory.disableWebView();
    }

    @Deprecated
    public void refreshPlugins(boolean reloadOpenPages) {
        checkThread();
    }

    @Deprecated
    public void emulateShiftHeld() {
        checkThread();
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    @Deprecated
    public void onChildViewAdded(View parent, View child) {
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    @Deprecated
    public void onChildViewRemoved(View p, View child) {
    }

    @Override // android.view.ViewTreeObserver.OnGlobalFocusChangeListener
    @Deprecated
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
    }

    @Deprecated
    public void setMapTrackballToArrowKeys(boolean setMap) {
        checkThread();
        this.mProvider.setMapTrackballToArrowKeys(setMap);
    }

    public void flingScroll(int vx, int vy) {
        checkThread();
        this.mProvider.flingScroll(vx, vy);
    }

    @Deprecated
    public View getZoomControls() {
        checkThread();
        return this.mProvider.getZoomControls();
    }

    @Deprecated
    public boolean canZoomIn() {
        checkThread();
        return this.mProvider.canZoomIn();
    }

    @Deprecated
    public boolean canZoomOut() {
        checkThread();
        return this.mProvider.canZoomOut();
    }

    public void zoomBy(float zoomFactor) {
        checkThread();
        if (zoomFactor < 0.01d) {
            throw new IllegalArgumentException("zoomFactor must be greater than 0.01.");
        }
        if (zoomFactor > 100.0d) {
            throw new IllegalArgumentException("zoomFactor must be less than 100.");
        }
        this.mProvider.zoomBy(zoomFactor);
    }

    public boolean zoomIn() {
        checkThread();
        return this.mProvider.zoomIn();
    }

    public boolean zoomOut() {
        checkThread();
        return this.mProvider.zoomOut();
    }

    @Deprecated
    public void debugDump() {
        checkThread();
    }

    @Override // android.view.ViewDebug.HierarchyHandler
    public void dumpViewHierarchyWithProperties(BufferedWriter out, int level) {
        this.mProvider.dumpViewHierarchyWithProperties(out, level);
    }

    @Override // android.view.ViewDebug.HierarchyHandler
    public View findHierarchyView(String className, int hashCode) {
        return this.mProvider.findHierarchyView(className, hashCode);
    }

    public void setRendererPriorityPolicy(int rendererRequestedPriority, boolean waivedWhenNotVisible) {
        this.mProvider.setRendererPriorityPolicy(rendererRequestedPriority, waivedWhenNotVisible);
    }

    public int getRendererRequestedPriority() {
        return this.mProvider.getRendererRequestedPriority();
    }

    public boolean getRendererPriorityWaivedWhenNotVisible() {
        return this.mProvider.getRendererPriorityWaivedWhenNotVisible();
    }

    public void setTextClassifier(TextClassifier textClassifier) {
        this.mProvider.setTextClassifier(textClassifier);
    }

    public TextClassifier getTextClassifier() {
        return this.mProvider.getTextClassifier();
    }

    public static ClassLoader getWebViewClassLoader() {
        return getFactory().getWebViewClassLoader();
    }

    public Looper getWebViewLooper() {
        return this.mWebViewThread;
    }

    @SystemApi
    public WebViewProvider getWebViewProvider() {
        return this.mProvider;
    }

    @SystemApi
    /* loaded from: classes4.dex */
    public class PrivateAccess {
        public PrivateAccess() {
        }

        public int super_getScrollBarStyle() {
            return WebView.super.getScrollBarStyle();
        }

        public void super_scrollTo(int scrollX, int scrollY) {
            WebView.super.scrollTo(scrollX, scrollY);
        }

        public void super_computeScroll() {
            WebView.super.computeScroll();
        }

        public boolean super_onHoverEvent(MotionEvent event) {
            return WebView.super.onHoverEvent(event);
        }

        public boolean super_performAccessibilityAction(int action, Bundle arguments) {
            return WebView.super.performAccessibilityActionInternal(action, arguments);
        }

        public boolean super_performLongClick() {
            return WebView.super.performLongClick();
        }

        public boolean super_setFrame(int left, int top, int right, int bottom) {
            return WebView.super.setFrame(left, top, right, bottom);
        }

        public boolean super_dispatchKeyEvent(KeyEvent event) {
            return WebView.super.dispatchKeyEvent(event);
        }

        public boolean super_onGenericMotionEvent(MotionEvent event) {
            return WebView.super.onGenericMotionEvent(event);
        }

        public boolean super_requestFocus(int direction, Rect previouslyFocusedRect) {
            return WebView.super.requestFocus(direction, previouslyFocusedRect);
        }

        public void super_setLayoutParams(ViewGroup.LayoutParams params) {
            WebView.super.setLayoutParams(params);
        }

        public void super_startActivityForResult(Intent intent, int requestCode) {
            WebView.super.startActivityForResult(intent, requestCode);
        }

        public WindowInsets super_onApplyWindowInsets(WindowInsets insets) {
            return WebView.super.onApplyWindowInsets(insets);
        }

        public void overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            WebView.this.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }

        public void awakenScrollBars(int duration) {
            WebView.this.awakenScrollBars(duration);
        }

        public void awakenScrollBars(int duration, boolean invalidate) {
            WebView.this.awakenScrollBars(duration, invalidate);
        }

        public float getVerticalScrollFactor() {
            return WebView.this.getVerticalScrollFactor();
        }

        public float getHorizontalScrollFactor() {
            return WebView.this.getHorizontalScrollFactor();
        }

        public void setMeasuredDimension(int measuredWidth, int measuredHeight) {
            WebView.this.setMeasuredDimension(measuredWidth, measuredHeight);
        }

        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            WebView.this.onScrollChanged(l, t, oldl, oldt);
        }

        public int getHorizontalScrollbarHeight() {
            return WebView.this.getHorizontalScrollbarHeight();
        }

        public void super_onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
            WebView.super.onDrawVerticalScrollBar(canvas, scrollBar, l, t, r, b);
        }

        public void setScrollXRaw(int scrollX) {
            WebView.this.mScrollX = scrollX;
        }

        public void setScrollYRaw(int scrollY) {
            WebView.this.mScrollY = scrollY;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFindDialogFindListener(FindListener listener) {
        checkThread();
        setupFindListenerIfNeeded();
        this.mFindListener.mFindDialogFindListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyFindDialogDismissed() {
        checkThread();
        this.mProvider.notifyFindDialogDismissed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class FindListenerDistributor implements FindListener {
        private FindListener mFindDialogFindListener;
        private FindListener mUserFindListener;

        private FindListenerDistributor() {
        }

        @Override // android.webkit.WebView.FindListener
        public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
            FindListener findListener = this.mFindDialogFindListener;
            if (findListener != null) {
                findListener.onFindResultReceived(activeMatchOrdinal, numberOfMatches, isDoneCounting);
            }
            FindListener findListener2 = this.mUserFindListener;
            if (findListener2 != null) {
                findListener2.onFindResultReceived(activeMatchOrdinal, numberOfMatches, isDoneCounting);
            }
        }
    }

    private void setupFindListenerIfNeeded() {
        if (this.mFindListener == null) {
            FindListenerDistributor findListenerDistributor = new FindListenerDistributor();
            this.mFindListener = findListenerDistributor;
            this.mProvider.setFindListener(findListenerDistributor);
        }
    }

    private void ensureProviderCreated() {
        checkThread();
        if (this.mProvider == null) {
            this.mProvider = getFactory().createWebView(this, new PrivateAccess());
        }
    }

    private static WebViewFactoryProvider getFactory() {
        return WebViewFactory.getProvider();
    }

    private void checkThread() {
        if (this.mWebViewThread != null && Looper.myLooper() != this.mWebViewThread) {
            Throwable throwable = new Throwable("A WebView method was called on thread '" + Thread.currentThread().getName() + "'. All WebView methods must be called on the same thread. (Expected Looper " + this.mWebViewThread + " called on " + Looper.myLooper() + ", FYI main Looper is " + Looper.getMainLooper() + NavigationBarInflaterView.KEY_CODE_END);
            Log.m104w(LOGTAG, Log.getStackTraceString(throwable));
            StrictMode.onWebViewMethodCalledOnWrongThread(throwable);
            if (sEnforceThreadChecking) {
                throw new RuntimeException(throwable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mProvider.getViewDelegate().onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindowInternal() {
        this.mProvider.getViewDelegate().onDetachedFromWindow();
        super.onDetachedFromWindowInternal();
    }

    @Override // android.view.View
    public void onMovedToDisplay(int displayId, Configuration config) {
        this.mProvider.getViewDelegate().onMovedToDisplay(displayId, config);
    }

    @Override // android.view.View
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        this.mProvider.getViewDelegate().setLayoutParams(params);
    }

    @Override // android.view.View
    public void setOverScrollMode(int mode) {
        super.setOverScrollMode(mode);
        ensureProviderCreated();
        this.mProvider.getViewDelegate().setOverScrollMode(mode);
    }

    @Override // android.view.View
    public void setScrollBarStyle(int style) {
        this.mProvider.getViewDelegate().setScrollBarStyle(style);
        super.setScrollBarStyle(style);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeHorizontalScrollRange() {
        return this.mProvider.getScrollDelegate().computeHorizontalScrollRange();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeHorizontalScrollOffset() {
        return this.mProvider.getScrollDelegate().computeHorizontalScrollOffset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollRange() {
        return this.mProvider.getScrollDelegate().computeVerticalScrollRange();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollOffset() {
        return this.mProvider.getScrollDelegate().computeVerticalScrollOffset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollExtent() {
        return this.mProvider.getScrollDelegate().computeVerticalScrollExtent();
    }

    @Override // android.view.View
    public void computeScroll() {
        this.mProvider.getScrollDelegate().computeScroll();
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        return this.mProvider.getViewDelegate().onHoverEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return this.mProvider.getViewDelegate().onTouchEvent(event);
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        return this.mProvider.getViewDelegate().onGenericMotionEvent(event);
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent event) {
        return this.mProvider.getViewDelegate().onTrackballEvent(event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mProvider.getViewDelegate().onKeyDown(keyCode, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mProvider.getViewDelegate().onKeyUp(keyCode, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return this.mProvider.getViewDelegate().onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityNodeProvider provider = this.mProvider.getViewDelegate().getAccessibilityNodeProvider();
        return provider == null ? super.getAccessibilityNodeProvider() : provider;
    }

    @Override // android.widget.AbsoluteLayout, android.view.ViewGroup
    @Deprecated
    public boolean shouldDelayChildPressedState() {
        return this.mProvider.getViewDelegate().shouldDelayChildPressedState();
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return WebView.class.getName();
    }

    @Override // android.view.View
    public void onProvideVirtualStructure(ViewStructure structure) {
        this.mProvider.getViewDelegate().onProvideVirtualStructure(structure);
    }

    @Override // android.view.View
    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        this.mProvider.getViewDelegate().onProvideAutofillVirtualStructure(structure, flags);
    }

    @Override // android.view.View
    public void onProvideContentCaptureStructure(ViewStructure structure, int flags) {
        this.mProvider.getViewDelegate().onProvideContentCaptureStructure(structure, flags);
    }

    @Override // android.view.View
    public void autofill(SparseArray<AutofillValue> values) {
        this.mProvider.getViewDelegate().autofill(values);
    }

    @Override // android.view.View
    public boolean isVisibleToUserForAutofill(int virtualId) {
        return this.mProvider.getViewDelegate().isVisibleToUserForAutofill(virtualId);
    }

    @Override // android.view.View
    public void onCreateVirtualViewTranslationRequests(long[] virtualIds, int[] supportedFormats, Consumer<ViewTranslationRequest> requestsCollector) {
        this.mProvider.getViewDelegate().onCreateVirtualViewTranslationRequests(virtualIds, supportedFormats, requestsCollector);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchCreateViewTranslationRequest(Map<AutofillId, long[]> viewIds, int[] supportedFormats, TranslationCapability capability, List<ViewTranslationRequest> requests) {
        super.dispatchCreateViewTranslationRequest(viewIds, supportedFormats, capability, requests);
        this.mProvider.getViewDelegate().dispatchCreateViewTranslationRequest(viewIds, supportedFormats, capability, requests);
    }

    @Override // android.view.View
    public void onVirtualViewTranslationResponses(LongSparseArray<ViewTranslationResponse> response) {
        this.mProvider.getViewDelegate().onVirtualViewTranslationResponses(response);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        this.mProvider.getViewDelegate().onInitializeAccessibilityNodeInfo(info);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        super.onInitializeAccessibilityEventInternal(event);
        this.mProvider.getViewDelegate().onInitializeAccessibilityEvent(event);
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        return this.mProvider.getViewDelegate().performAccessibilityAction(action, arguments);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        this.mProvider.getViewDelegate().onDrawVerticalScrollBar(canvas, scrollBar, l, t, r, b);
    }

    @Override // android.view.View
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        this.mProvider.getViewDelegate().onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        this.mProvider.getViewDelegate().onWindowVisibilityChanged(visibility);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        this.mProvider.getViewDelegate().onDraw(canvas);
    }

    @Override // android.view.View
    public boolean performLongClick() {
        return this.mProvider.getViewDelegate().performLongClick();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        this.mProvider.getViewDelegate().onConfigurationChanged(newConfig);
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mProvider.getViewDelegate().onCreateInputConnection(outAttrs);
    }

    @Override // android.view.View
    public boolean onDragEvent(DragEvent event) {
        return this.mProvider.getViewDelegate().onDragEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        ensureProviderCreated();
        this.mProvider.getViewDelegate().onVisibilityChanged(changedView, visibility);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        this.mProvider.getViewDelegate().onWindowFocusChanged(hasWindowFocus);
        super.onWindowFocusChanged(hasWindowFocus);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        this.mProvider.getViewDelegate().onFocusChanged(focused, direction, previouslyFocusedRect);
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int left, int top, int right, int bottom) {
        return this.mProvider.getViewDelegate().setFrame(left, top, right, bottom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        this.mProvider.getViewDelegate().onSizeChanged(w, h, ow, oh);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        this.mProvider.getViewDelegate().onScrollChanged(l, t, oldl, oldt);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mProvider.getViewDelegate().dispatchKeyEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return this.mProvider.getViewDelegate().requestFocus(direction, previouslyFocusedRect);
    }

    @Override // android.widget.AbsoluteLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mProvider.getViewDelegate().onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        return this.mProvider.getViewDelegate().requestChildRectangleOnScreen(child, rect, immediate);
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.mProvider.getViewDelegate().setBackgroundColor(color);
    }

    @Override // android.view.View
    public void setLayerType(int layerType, Paint paint) {
        super.setLayerType(layerType, paint);
        this.mProvider.getViewDelegate().setLayerType(layerType, paint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        this.mProvider.getViewDelegate().preDispatchDraw(canvas);
        super.dispatchDraw(canvas);
    }

    @Override // android.view.View
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        this.mProvider.getViewDelegate().onStartTemporaryDetach();
    }

    @Override // android.view.View
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        this.mProvider.getViewDelegate().onFinishTemporaryDetach();
    }

    @Override // android.view.View
    public Handler getHandler() {
        return this.mProvider.getViewDelegate().getHandler(super.getHandler());
    }

    @Override // android.view.ViewGroup, android.view.View
    public View findFocus() {
        return this.mProvider.getViewDelegate().findFocus(super.findFocus());
    }

    public static PackageInfo getCurrentWebViewPackage() {
        PackageInfo webviewPackage = WebViewFactory.getLoadedPackageInfo();
        if (webviewPackage != null) {
            return webviewPackage;
        }
        IWebViewUpdateService service = WebViewFactory.getUpdateService();
        if (service == null) {
            return null;
        }
        try {
            return service.getCurrentWebViewPackage();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.view.View
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mProvider.getViewDelegate().onActivityResult(requestCode, resultCode, data);
    }

    @Override // android.view.View
    public boolean onCheckIsTextEditor() {
        return this.mProvider.getViewDelegate().onCheckIsTextEditor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void encodeProperties(ViewHierarchyEncoder encoder) {
        super.encodeProperties(encoder);
        checkThread();
        encoder.addProperty("webview:contentHeight", this.mProvider.getContentHeight());
        encoder.addProperty("webview:contentWidth", this.mProvider.getContentWidth());
        encoder.addProperty("webview:scale", this.mProvider.getScale());
        encoder.addProperty("webview:title", this.mProvider.getTitle());
        encoder.addProperty("webview:url", this.mProvider.getUrl());
        encoder.addProperty("webview:originalUrl", this.mProvider.getOriginalUrl());
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        WindowInsets result = this.mProvider.getViewDelegate().onApplyWindowInsets(insets);
        return result == null ? super.onApplyWindowInsets(insets) : result;
    }
}
