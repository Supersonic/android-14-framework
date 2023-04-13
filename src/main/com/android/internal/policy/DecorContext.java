package com.android.internal.policy;

import android.content.AutofillOptions;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.contentcapture.ContentCaptureManager;
import java.lang.ref.WeakReference;
/* loaded from: classes4.dex */
public class DecorContext extends ContextThemeWrapper {
    private ContentCaptureManager mContentCaptureManager;
    private WeakReference<Context> mContext;
    private PhoneWindow mPhoneWindow;
    private Resources mResources;

    public DecorContext(Context baseContext, PhoneWindow phoneWindow) {
        super((Context) null, (Resources.Theme) null);
        Context displayContext;
        setPhoneWindow(phoneWindow);
        Display display = phoneWindow.getContext().getDisplayNoVerify();
        if (display.getDisplayId() == 0) {
            displayContext = baseContext.createConfigurationContext(Configuration.EMPTY);
            displayContext.updateDisplay(0);
        } else {
            displayContext = baseContext.createDisplayContext(display);
        }
        attachBaseContext(displayContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPhoneWindow(PhoneWindow phoneWindow) {
        this.mPhoneWindow = phoneWindow;
        Context context = phoneWindow.getContext();
        this.mContext = new WeakReference<>(context);
        this.mResources = context.getResources();
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        if (Context.WINDOW_SERVICE.equals(name)) {
            return this.mPhoneWindow.getWindowManager();
        }
        Context context = this.mContext.get();
        if (Context.CONTENT_CAPTURE_MANAGER_SERVICE.equals(name)) {
            if (context != null && this.mContentCaptureManager == null) {
                this.mContentCaptureManager = (ContentCaptureManager) context.getSystemService(name);
            }
            return this.mContentCaptureManager;
        } else if (Context.DISPLAY_SERVICE.equals(name)) {
            return super.getSystemService(name);
        } else {
            return context != null ? context.getSystemService(name) : super.getSystemService(name);
        }
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        Context context = this.mContext.get();
        if (context != null) {
            this.mResources = context.getResources();
        }
        return this.mResources;
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public AssetManager getAssets() {
        return this.mResources.getAssets();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public AutofillOptions getAutofillOptions() {
        Context context = this.mContext.get();
        if (context != null) {
            return context.getAutofillOptions();
        }
        return null;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public ContentCaptureOptions getContentCaptureOptions() {
        Context context = this.mContext.get();
        if (context != null) {
            return context.getContentCaptureOptions();
        }
        return null;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public boolean isUiContext() {
        Context context = this.mContext.get();
        if (context != null) {
            return context.isUiContext();
        }
        return false;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public boolean isConfigurationContext() {
        Context context = this.mContext.get();
        if (context != null) {
            return context.isConfigurationContext();
        }
        return false;
    }
}
