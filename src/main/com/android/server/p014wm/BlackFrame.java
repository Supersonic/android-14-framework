package com.android.server.p014wm;

import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import java.io.PrintWriter;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.BlackFrame */
/* loaded from: classes2.dex */
public class BlackFrame {
    public final BlackSurface[] mBlackSurfaces;
    public final Rect mInnerRect;
    public final Rect mOuterRect;
    public final Supplier<SurfaceControl.Transaction> mTransactionFactory;

    /* renamed from: com.android.server.wm.BlackFrame$BlackSurface */
    /* loaded from: classes2.dex */
    public static class BlackSurface {
        public final int layer;
        public final int left;
        public final SurfaceControl surface;
        public final int top;

        public BlackSurface(SurfaceControl.Transaction transaction, int i, int i2, int i3, int i4, int i5, DisplayContent displayContent, SurfaceControl surfaceControl) throws Surface.OutOfResourcesException {
            this.left = i2;
            this.top = i3;
            this.layer = i;
            SurfaceControl build = displayContent.makeOverlay().setName("BlackSurface").setColorLayer().setParent(surfaceControl).setCallsite("BlackSurface").build();
            this.surface = build;
            transaction.setWindowCrop(build, i4 - i2, i5 - i3);
            transaction.setAlpha(build, 1.0f);
            transaction.setLayer(build, i);
            transaction.setPosition(build, i2, i3);
            transaction.show(build);
            if (ProtoLogCache.WM_SHOW_SURFACE_ALLOC_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_SURFACE_ALLOC, 152914409, 4, (String) null, new Object[]{String.valueOf(build), Long.valueOf(i)});
            }
        }
    }

    public void printTo(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("Outer: ");
        this.mOuterRect.printShortString(printWriter);
        printWriter.print(" / Inner: ");
        this.mInnerRect.printShortString(printWriter);
        printWriter.println();
        int i = 0;
        while (true) {
            BlackSurface[] blackSurfaceArr = this.mBlackSurfaces;
            if (i >= blackSurfaceArr.length) {
                return;
            }
            BlackSurface blackSurface = blackSurfaceArr[i];
            printWriter.print(str);
            printWriter.print("#");
            printWriter.print(i);
            printWriter.print(": ");
            printWriter.print(blackSurface.surface);
            printWriter.print(" left=");
            printWriter.print(blackSurface.left);
            printWriter.print(" top=");
            printWriter.println(blackSurface.top);
            i++;
        }
    }

    public BlackFrame(Supplier<SurfaceControl.Transaction> supplier, SurfaceControl.Transaction transaction, Rect rect, Rect rect2, int i, DisplayContent displayContent, boolean z, SurfaceControl surfaceControl) throws Surface.OutOfResourcesException {
        BlackSurface[] blackSurfaceArr = new BlackSurface[4];
        this.mBlackSurfaces = blackSurfaceArr;
        this.mTransactionFactory = supplier;
        this.mOuterRect = new Rect(rect);
        this.mInnerRect = new Rect(rect2);
        try {
            int i2 = rect.top;
            int i3 = rect2.top;
            if (i2 < i3) {
                blackSurfaceArr[0] = new BlackSurface(transaction, i, rect.left, i2, rect2.right, i3, displayContent, surfaceControl);
            }
            int i4 = rect.left;
            int i5 = rect2.left;
            if (i4 < i5) {
                blackSurfaceArr[1] = new BlackSurface(transaction, i, i4, rect2.top, i5, rect.bottom, displayContent, surfaceControl);
            }
            int i6 = rect.bottom;
            int i7 = rect2.bottom;
            if (i6 > i7) {
                blackSurfaceArr[2] = new BlackSurface(transaction, i, rect2.left, i7, rect.right, i6, displayContent, surfaceControl);
            }
            int i8 = rect.right;
            int i9 = rect2.right;
            if (i8 > i9) {
                blackSurfaceArr[3] = new BlackSurface(transaction, i, i9, rect.top, i8, rect2.bottom, displayContent, surfaceControl);
            }
        } catch (Throwable th) {
            kill();
            throw th;
        }
    }

    public void kill() {
        SurfaceControl.Transaction transaction = this.mTransactionFactory.get();
        int i = 0;
        while (true) {
            BlackSurface[] blackSurfaceArr = this.mBlackSurfaces;
            if (i < blackSurfaceArr.length) {
                BlackSurface blackSurface = blackSurfaceArr[i];
                if (blackSurface != null) {
                    if (ProtoLogCache.WM_SHOW_SURFACE_ALLOC_enabled) {
                        ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_SURFACE_ALLOC, 51200510, 0, (String) null, new Object[]{String.valueOf(blackSurface.surface)});
                    }
                    transaction.remove(this.mBlackSurfaces[i].surface);
                    this.mBlackSurfaces[i] = null;
                }
                i++;
            } else {
                transaction.apply();
                return;
            }
        }
    }
}
