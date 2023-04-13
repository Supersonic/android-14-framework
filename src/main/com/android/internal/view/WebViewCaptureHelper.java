package com.android.internal.view;

import android.graphics.Rect;
import android.p008os.CancellationSignal;
import android.util.MathUtils;
import android.webkit.WebView;
import com.android.internal.view.ScrollCaptureViewHelper;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class WebViewCaptureHelper implements ScrollCaptureViewHelper<WebView> {
    private static final String TAG = "WebViewScrollCapture";
    private int mOriginScrollX;
    private int mOriginScrollY;
    private final Rect mRequestWebViewLocal = new Rect();
    private final Rect mWebViewBounds = new Rect();

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public /* bridge */ /* synthetic */ void onScrollRequested(WebView webView, Rect rect, Rect rect2, CancellationSignal cancellationSignal, Consumer consumer) {
        onScrollRequested2(webView, rect, rect2, cancellationSignal, (Consumer<ScrollCaptureViewHelper.ScrollResult>) consumer);
    }

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public boolean onAcceptSession(WebView view) {
        return view.isVisibleToUser() && ((float) view.getContentHeight()) * view.getScale() > ((float) view.getHeight());
    }

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public void onPrepareForStart(WebView view, Rect scrollBounds) {
        this.mOriginScrollX = view.getScrollX();
        this.mOriginScrollY = view.getScrollY();
    }

    /* renamed from: onScrollRequested  reason: avoid collision after fix types in other method */
    public void onScrollRequested2(WebView view, Rect scrollBounds, Rect requestRect, CancellationSignal cancellationSignal, Consumer<ScrollCaptureViewHelper.ScrollResult> resultConsumer) {
        int scrollDelta = view.getScrollY() - this.mOriginScrollY;
        ScrollCaptureViewHelper.ScrollResult result = new ScrollCaptureViewHelper.ScrollResult();
        result.requestedArea = new Rect(requestRect);
        result.availableArea = new Rect();
        result.scrollDelta = scrollDelta;
        this.mWebViewBounds.set(0, 0, view.getWidth(), view.getHeight());
        if (!view.isVisibleToUser()) {
            resultConsumer.accept(result);
        }
        this.mRequestWebViewLocal.set(requestRect);
        this.mRequestWebViewLocal.offset(0, -scrollDelta);
        int upLimit = Math.min(0, -view.getScrollY());
        int contentHeightPx = (int) (view.getContentHeight() * view.getScale());
        int downLimit = Math.max(0, (contentHeightPx - view.getHeight()) - view.getScrollY());
        int scrollToCenter = this.mRequestWebViewLocal.centerY() - this.mWebViewBounds.centerY();
        int scrollMovement = MathUtils.constrain(scrollToCenter, upLimit, downLimit);
        view.scrollBy(this.mOriginScrollX, scrollMovement);
        int scrollDelta2 = view.getScrollY() - this.mOriginScrollY;
        this.mRequestWebViewLocal.offset(0, -scrollMovement);
        result.scrollDelta = scrollDelta2;
        if (this.mRequestWebViewLocal.intersect(this.mWebViewBounds)) {
            result.availableArea = new Rect(this.mRequestWebViewLocal);
            result.availableArea.offset(0, result.scrollDelta);
        }
        resultConsumer.accept(result);
    }

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public void onPrepareForEnd(WebView view) {
        view.scrollTo(this.mOriginScrollX, this.mOriginScrollY);
    }
}
