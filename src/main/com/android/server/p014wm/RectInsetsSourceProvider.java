package com.android.server.p014wm;

import android.graphics.Rect;
import android.view.InsetsSource;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.RectInsetsSourceProvider */
/* loaded from: classes2.dex */
public class RectInsetsSourceProvider extends InsetsSourceProvider {
    @Override // com.android.server.p014wm.InsetsSourceProvider
    public /* bridge */ /* synthetic */ void dump(PrintWriter printWriter, String str) {
        super.dump(printWriter, str);
    }

    public RectInsetsSourceProvider(InsetsSource insetsSource, InsetsStateController insetsStateController, DisplayContent displayContent) {
        super(insetsSource, insetsStateController, displayContent);
    }

    public void setRect(Rect rect) {
        this.mSource.setFrame(rect);
        this.mSource.setVisible(true);
    }
}
