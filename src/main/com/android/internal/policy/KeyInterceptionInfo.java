package com.android.internal.policy;
/* loaded from: classes4.dex */
public class KeyInterceptionInfo {
    public final int layoutParamsPrivateFlags;
    public final int layoutParamsType;
    public final String windowTitle;

    public KeyInterceptionInfo(int type, int flags, String title) {
        this.layoutParamsType = type;
        this.layoutParamsPrivateFlags = flags;
        this.windowTitle = title;
    }
}
