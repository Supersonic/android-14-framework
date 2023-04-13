package android.webkit;

import android.app.Fragment;
import android.p008os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
@Deprecated
/* loaded from: classes4.dex */
public class WebViewFragment extends Fragment {
    private boolean mIsWebViewAvailable;
    private WebView mWebView;

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView webView = this.mWebView;
        if (webView != null) {
            webView.destroy();
        }
        WebView webView2 = new WebView(getContext());
        this.mWebView = webView2;
        this.mIsWebViewAvailable = true;
        return webView2;
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        this.mWebView.onPause();
    }

    @Override // android.app.Fragment
    public void onResume() {
        this.mWebView.onResume();
        super.onResume();
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        this.mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        WebView webView = this.mWebView;
        if (webView != null) {
            webView.destroy();
            this.mWebView = null;
        }
        super.onDestroy();
    }

    public WebView getWebView() {
        if (this.mIsWebViewAvailable) {
            return this.mWebView;
        }
        return null;
    }
}
