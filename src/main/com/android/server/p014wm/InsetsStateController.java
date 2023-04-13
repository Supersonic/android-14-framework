package com.android.server.p014wm;

import android.os.Trace;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.InsetsSource;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.WindowInsets;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.p014wm.InsetsStateController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
/* renamed from: com.android.server.wm.InsetsStateController */
/* loaded from: classes2.dex */
public class InsetsStateController {
    public final DisplayContent mDisplayContent;
    public final InsetsState mLastState = new InsetsState();
    public final InsetsState mState = new InsetsState();
    public final SparseArray<WindowContainerInsetsSourceProvider> mProviders = new SparseArray<>();
    public final ArrayMap<InsetsControlTarget, ArrayList<InsetsSourceProvider>> mControlTargetProvidersMap = new ArrayMap<>();
    public final SparseArray<InsetsControlTarget> mIdControlTargetMap = new SparseArray<>();
    public final SparseArray<InsetsControlTarget> mIdFakeControlTargetMap = new SparseArray<>();
    public final ArraySet<InsetsControlTarget> mPendingControlChanged = new ArraySet<>();
    public final Consumer<WindowState> mDispatchInsetsChanged = new Consumer() { // from class: com.android.server.wm.InsetsStateController$$ExternalSyntheticLambda3
        @Override // java.util.function.Consumer
        public final void accept(Object obj) {
            InsetsStateController.lambda$new$0((WindowState) obj);
        }
    };
    public final InsetsControlTarget mEmptyImeControlTarget = new C18781();

    public static /* synthetic */ void lambda$new$0(WindowState windowState) {
        if (windowState.isReadyToDispatchInsetsState()) {
            windowState.notifyInsetsChanged();
        }
    }

    /* renamed from: com.android.server.wm.InsetsStateController$1 */
    /* loaded from: classes2.dex */
    public class C18781 implements InsetsControlTarget {
        public C18781() {
        }

        @Override // com.android.server.p014wm.InsetsControlTarget
        public void notifyInsetsControlChanged() {
            InsetsSourceControl[] controlsForDispatch = InsetsStateController.this.getControlsForDispatch(this);
            if (controlsForDispatch == null) {
                return;
            }
            for (InsetsSourceControl insetsSourceControl : controlsForDispatch) {
                if (insetsSourceControl.getType() == WindowInsets.Type.ime()) {
                    InsetsStateController.this.mDisplayContent.mWmService.f1164mH.post(new Runnable() { // from class: com.android.server.wm.InsetsStateController$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            InsetsStateController.C18781.lambda$notifyInsetsControlChanged$0();
                        }
                    });
                }
            }
        }

        public static /* synthetic */ void lambda$notifyInsetsControlChanged$0() {
            InputMethodManagerInternal.get().removeImeSurface();
        }
    }

    public InsetsStateController(DisplayContent displayContent) {
        this.mDisplayContent = displayContent;
    }

    public InsetsState getRawInsetsState() {
        return this.mState;
    }

    public InsetsSourceControl[] getControlsForDispatch(InsetsControlTarget insetsControlTarget) {
        ArrayList<InsetsSourceProvider> arrayList = this.mControlTargetProvidersMap.get(insetsControlTarget);
        if (arrayList == null) {
            return null;
        }
        int size = arrayList.size();
        InsetsSourceControl[] insetsSourceControlArr = new InsetsSourceControl[size];
        for (int i = 0; i < size; i++) {
            insetsSourceControlArr[i] = arrayList.get(i).getControl(insetsControlTarget);
        }
        return insetsSourceControlArr;
    }

    public SparseArray<WindowContainerInsetsSourceProvider> getSourceProviders() {
        return this.mProviders;
    }

    public WindowContainerInsetsSourceProvider getOrCreateSourceProvider(int i, int i2) {
        WindowContainerInsetsSourceProvider windowContainerInsetsSourceProvider;
        WindowContainerInsetsSourceProvider windowContainerInsetsSourceProvider2 = this.mProviders.get(i);
        if (windowContainerInsetsSourceProvider2 != null) {
            return windowContainerInsetsSourceProvider2;
        }
        InsetsSource orCreateSource = this.mState.getOrCreateSource(i, i2);
        if (i == InsetsSource.ID_IME) {
            windowContainerInsetsSourceProvider = new ImeInsetsSourceProvider(orCreateSource, this, this.mDisplayContent);
        } else {
            windowContainerInsetsSourceProvider = new WindowContainerInsetsSourceProvider(orCreateSource, this, this.mDisplayContent);
        }
        this.mProviders.put(i, windowContainerInsetsSourceProvider);
        return windowContainerInsetsSourceProvider;
    }

    public ImeInsetsSourceProvider getImeSourceProvider() {
        return (ImeInsetsSourceProvider) getOrCreateSourceProvider(InsetsSource.ID_IME, WindowInsets.Type.ime());
    }

    public void removeSourceProvider(int i) {
        if (i != InsetsSource.ID_IME) {
            this.mState.removeSource(i);
            this.mProviders.remove(i);
        }
    }

    public void onPostLayout() {
        Trace.traceBegin(32L, "ISC.onPostLayout");
        for (int size = this.mProviders.size() - 1; size >= 0; size--) {
            this.mProviders.valueAt(size).onPostLayout();
        }
        if (!this.mLastState.equals(this.mState)) {
            this.mLastState.set(this.mState, true);
            notifyInsetsChanged();
        }
        Trace.traceEnd(32L);
    }

    public void updateAboveInsetsState(boolean z) {
        InsetsState insetsState = new InsetsState();
        insetsState.set(this.mState, WindowInsets.Type.displayCutout() | WindowInsets.Type.systemGestures() | WindowInsets.Type.mandatorySystemGestures());
        ArraySet<WindowState> arraySet = new ArraySet<>();
        this.mDisplayContent.updateAboveInsetsState(insetsState, new SparseArray<>(), arraySet);
        if (z) {
            for (int size = arraySet.size() - 1; size >= 0; size--) {
                this.mDispatchInsetsChanged.accept(arraySet.valueAt(size));
            }
        }
    }

    public void onDisplayFramesUpdated(boolean z) {
        final ArrayList arrayList = new ArrayList();
        this.mDisplayContent.forAllWindows(new Consumer() { // from class: com.android.server.wm.InsetsStateController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InsetsStateController.this.lambda$onDisplayFramesUpdated$1(arrayList, (WindowState) obj);
            }
        }, true);
        if (z) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                this.mDispatchInsetsChanged.accept((WindowState) arrayList.get(size));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDisplayFramesUpdated$1(ArrayList arrayList, WindowState windowState) {
        windowState.mAboveInsetsState.set(this.mState, WindowInsets.Type.displayCutout());
        arrayList.add(windowState);
    }

    public void onInsetsModified(InsetsControlTarget insetsControlTarget) {
        boolean z = false;
        for (int size = this.mProviders.size() - 1; size >= 0; size--) {
            z |= this.mProviders.valueAt(size).updateClientVisibility(insetsControlTarget);
        }
        if (z) {
            notifyInsetsChanged();
            this.mDisplayContent.updateSystemGestureExclusion();
            this.mDisplayContent.updateKeepClearAreas();
            this.mDisplayContent.getDisplayPolicy().updateSystemBarAttributes();
        }
    }

    public int getFakeControllingTypes(InsetsControlTarget insetsControlTarget) {
        int i = 0;
        for (int size = this.mProviders.size() - 1; size >= 0; size--) {
            WindowContainerInsetsSourceProvider valueAt = this.mProviders.valueAt(size);
            if (insetsControlTarget == valueAt.getFakeControlTarget()) {
                i |= valueAt.getSource().getType();
            }
        }
        return i;
    }

    public void onImeControlTargetChanged(InsetsControlTarget insetsControlTarget) {
        if (insetsControlTarget == null) {
            insetsControlTarget = this.mEmptyImeControlTarget;
        }
        onControlTargetChanged(getImeSourceProvider(), insetsControlTarget, false);
        if (ProtoLogCache.WM_DEBUG_IME_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_IME, 1658605381, 0, (String) null, new Object[]{String.valueOf(insetsControlTarget != null ? insetsControlTarget.getWindow() : "null")});
        }
        notifyPendingInsetsControlChanged();
    }

    public void onBarControlTargetChanged(InsetsControlTarget insetsControlTarget, InsetsControlTarget insetsControlTarget2, InsetsControlTarget insetsControlTarget3, InsetsControlTarget insetsControlTarget4) {
        for (int size = this.mProviders.size() - 1; size >= 0; size--) {
            WindowContainerInsetsSourceProvider valueAt = this.mProviders.valueAt(size);
            int type = valueAt.getSource().getType();
            if (type == WindowInsets.Type.statusBars()) {
                onControlTargetChanged(valueAt, insetsControlTarget, false);
                onControlTargetChanged(valueAt, insetsControlTarget2, true);
            } else if (type == WindowInsets.Type.navigationBars()) {
                onControlTargetChanged(valueAt, insetsControlTarget3, false);
                onControlTargetChanged(valueAt, insetsControlTarget4, true);
            }
        }
        notifyPendingInsetsControlChanged();
    }

    public void notifyControlRevoked(InsetsControlTarget insetsControlTarget, InsetsSourceProvider insetsSourceProvider) {
        removeFromControlMaps(insetsControlTarget, insetsSourceProvider, false);
    }

    public final void onControlTargetChanged(InsetsSourceProvider insetsSourceProvider, InsetsControlTarget insetsControlTarget, boolean z) {
        InsetsControlTarget insetsControlTarget2;
        if (z) {
            insetsControlTarget2 = this.mIdFakeControlTargetMap.get(insetsSourceProvider.getSource().getId());
        } else {
            insetsControlTarget2 = this.mIdControlTargetMap.get(insetsSourceProvider.getSource().getId());
        }
        if (insetsControlTarget != insetsControlTarget2 && insetsSourceProvider.isControllable()) {
            if (z) {
                insetsSourceProvider.updateFakeControlTarget(insetsControlTarget);
            } else {
                insetsSourceProvider.updateControlForTarget(insetsControlTarget, false);
                insetsControlTarget = insetsSourceProvider.getControlTarget();
                if (insetsControlTarget == insetsControlTarget2) {
                    return;
                }
            }
            if (insetsControlTarget2 != null) {
                removeFromControlMaps(insetsControlTarget2, insetsSourceProvider, z);
                this.mPendingControlChanged.add(insetsControlTarget2);
            }
            if (insetsControlTarget != null) {
                addToControlMaps(insetsControlTarget, insetsSourceProvider, z);
                this.mPendingControlChanged.add(insetsControlTarget);
            }
        }
    }

    public final void removeFromControlMaps(InsetsControlTarget insetsControlTarget, InsetsSourceProvider insetsSourceProvider, boolean z) {
        ArrayList<InsetsSourceProvider> arrayList = this.mControlTargetProvidersMap.get(insetsControlTarget);
        if (arrayList == null) {
            return;
        }
        arrayList.remove(insetsSourceProvider);
        if (arrayList.isEmpty()) {
            this.mControlTargetProvidersMap.remove(insetsControlTarget);
        }
        if (z) {
            this.mIdFakeControlTargetMap.remove(insetsSourceProvider.getSource().getId());
        } else {
            this.mIdControlTargetMap.remove(insetsSourceProvider.getSource().getId());
        }
    }

    public final void addToControlMaps(InsetsControlTarget insetsControlTarget, InsetsSourceProvider insetsSourceProvider, boolean z) {
        this.mControlTargetProvidersMap.computeIfAbsent(insetsControlTarget, new Function() { // from class: com.android.server.wm.InsetsStateController$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ArrayList lambda$addToControlMaps$2;
                lambda$addToControlMaps$2 = InsetsStateController.lambda$addToControlMaps$2((InsetsControlTarget) obj);
                return lambda$addToControlMaps$2;
            }
        }).add(insetsSourceProvider);
        if (z) {
            this.mIdFakeControlTargetMap.put(insetsSourceProvider.getSource().getId(), insetsControlTarget);
        } else {
            this.mIdControlTargetMap.put(insetsSourceProvider.getSource().getId(), insetsControlTarget);
        }
    }

    public static /* synthetic */ ArrayList lambda$addToControlMaps$2(InsetsControlTarget insetsControlTarget) {
        return new ArrayList();
    }

    public void notifyControlChanged(InsetsControlTarget insetsControlTarget) {
        this.mPendingControlChanged.add(insetsControlTarget);
        notifyPendingInsetsControlChanged();
    }

    public final void notifyPendingInsetsControlChanged() {
        if (this.mPendingControlChanged.isEmpty()) {
            return;
        }
        this.mDisplayContent.mWmService.mAnimator.addAfterPrepareSurfacesRunnable(new Runnable() { // from class: com.android.server.wm.InsetsStateController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                InsetsStateController.this.lambda$notifyPendingInsetsControlChanged$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyPendingInsetsControlChanged$3() {
        for (int size = this.mProviders.size() - 1; size >= 0; size--) {
            this.mProviders.valueAt(size).onSurfaceTransactionApplied();
        }
        ArraySet arraySet = new ArraySet();
        for (int size2 = this.mPendingControlChanged.size() - 1; size2 >= 0; size2--) {
            InsetsControlTarget valueAt = this.mPendingControlChanged.valueAt(size2);
            valueAt.notifyInsetsControlChanged();
            if (this.mControlTargetProvidersMap.containsKey(valueAt)) {
                arraySet.add(valueAt);
            }
        }
        this.mPendingControlChanged.clear();
        for (int size3 = arraySet.size() - 1; size3 >= 0; size3--) {
            onInsetsModified((InsetsControlTarget) arraySet.valueAt(size3));
        }
        arraySet.clear();
    }

    public void notifyInsetsChanged() {
        this.mDisplayContent.notifyInsetsChanged(this.mDispatchInsetsChanged);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "WindowInsetsStateController");
        String str2 = str + "  ";
        this.mState.dump(str2, printWriter);
        printWriter.println(str2 + "Control map:");
        for (int size = this.mControlTargetProvidersMap.size() + (-1); size >= 0; size--) {
            InsetsControlTarget keyAt = this.mControlTargetProvidersMap.keyAt(size);
            printWriter.print(str2 + "  ");
            printWriter.print(keyAt);
            printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            ArrayList<InsetsSourceProvider> valueAt = this.mControlTargetProvidersMap.valueAt(size);
            for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                InsetsSourceProvider insetsSourceProvider = valueAt.get(size2);
                if (insetsSourceProvider != null) {
                    printWriter.print(str2 + "    ");
                    if (keyAt == insetsSourceProvider.getFakeControlTarget()) {
                        printWriter.print("(fake) ");
                    }
                    printWriter.println(insetsSourceProvider.getControl(keyAt));
                }
            }
        }
        if (this.mControlTargetProvidersMap.isEmpty()) {
            printWriter.print(str2 + "  none");
        }
        printWriter.println(str2 + "InsetsSourceProviders:");
        for (int size3 = this.mProviders.size() + (-1); size3 >= 0; size3 += -1) {
            this.mProviders.valueAt(size3).dump(printWriter, str2 + "  ");
        }
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, int i) {
        for (int size = this.mProviders.size() - 1; size >= 0; size--) {
            WindowContainerInsetsSourceProvider valueAt = this.mProviders.valueAt(size);
            valueAt.dumpDebug(protoOutputStream, valueAt.getSource().getType() == WindowInsets.Type.ime() ? 1146756268063L : 2246267895843L, i);
        }
    }
}
