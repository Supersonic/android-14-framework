package com.android.uiautomator.testrunner;

import android.app.ActivityThread;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.uiautomator.core.UiDevice;
import java.util.List;
import junit.framework.TestCase;
@Deprecated
/* loaded from: classes.dex */
public class UiAutomatorTestCase extends TestCase {
    private static final String DISABLE_IME = "disable_ime";
    private static final int NOT_A_SUBTYPE_ID = -1;
    private static final String STUB_IME_PACKAGE = "com.android.testing.stubime";
    private IAutomationSupport mAutomationSupport;
    private Bundle mParams;
    private boolean mShouldDisableIme = false;
    private UiDevice mUiDevice;

    @Override // junit.framework.TestCase
    protected void setUp() throws Exception {
        super.setUp();
        boolean equals = "true".equals(this.mParams.getString(DISABLE_IME));
        this.mShouldDisableIme = equals;
        if (equals) {
            setStubIme();
        }
    }

    @Override // junit.framework.TestCase
    protected void tearDown() throws Exception {
        if (this.mShouldDisableIme) {
            restoreActiveIme();
        }
        super.tearDown();
    }

    public UiDevice getUiDevice() {
        return this.mUiDevice;
    }

    public Bundle getParams() {
        return this.mParams;
    }

    public IAutomationSupport getAutomationSupport() {
        return this.mAutomationSupport;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUiDevice(UiDevice uiDevice) {
        this.mUiDevice = uiDevice;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setParams(Bundle params) {
        this.mParams = params;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAutomationSupport(IAutomationSupport automationSupport) {
        this.mAutomationSupport = automationSupport;
    }

    public void sleep(long ms) {
        SystemClock.sleep(ms);
    }

    private void setStubIme() {
        Context context = ActivityThread.currentApplication();
        if (context == null) {
            throw new RuntimeException("ActivityThread.currentApplication() is null.");
        }
        InputMethodManager im = (InputMethodManager) context.getSystemService("input_method");
        List<InputMethodInfo> infos = im.getInputMethodList();
        String id = null;
        for (InputMethodInfo info : infos) {
            if (STUB_IME_PACKAGE.equals(info.getComponent().getPackageName())) {
                id = info.getId();
            }
        }
        if (id == null) {
            throw new RuntimeException(String.format("Required testing fixture missing: IME package (%s)", STUB_IME_PACKAGE));
        }
        if (context.checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != 0) {
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        Settings.Secure.putInt(resolver, "selected_input_method_subtype", -1);
        Settings.Secure.putString(resolver, "default_input_method", id);
    }

    private void restoreActiveIme() {
    }
}
