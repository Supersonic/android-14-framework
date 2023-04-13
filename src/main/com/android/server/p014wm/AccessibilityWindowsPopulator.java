package com.android.server.p014wm;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IWindow;
import android.view.InputWindowHandle;
import android.view.MagnificationSpec;
import android.view.WindowInfo;
import android.window.WindowInfosListener;
import com.android.internal.annotations.GuardedBy;
import com.android.server.p014wm.AccessibilityWindowsPopulator;
import com.android.server.p014wm.utils.RegionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.AccessibilityWindowsPopulator */
/* loaded from: classes2.dex */
public final class AccessibilityWindowsPopulator extends WindowInfosListener {
    public static final String TAG = AccessibilityWindowsPopulator.class.getSimpleName();
    public static final float[] sTempFloats = new float[9];
    public final AccessibilityController mAccessibilityController;
    public final Handler mHandler;
    public final WindowManagerService mService;
    @GuardedBy({"mLock"})
    public final SparseArray<List<InputWindowHandle>> mInputWindowHandlesOnDisplays = new SparseArray<>();
    @GuardedBy({"mLock"})
    public final SparseArray<Matrix> mMagnificationSpecInverseMatrix = new SparseArray<>();
    @GuardedBy({"mLock"})
    public final SparseArray<WindowInfosListener.DisplayInfo> mDisplayInfos = new SparseArray<>();
    public final SparseArray<MagnificationSpec> mCurrentMagnificationSpec = new SparseArray<>();
    @GuardedBy({"mLock"})
    public final SparseArray<MagnificationSpec> mPreviousMagnificationSpec = new SparseArray<>();
    @GuardedBy({"mLock"})
    public final List<InputWindowHandle> mVisibleWindows = new ArrayList();
    @GuardedBy({"mLock"})
    public boolean mWindowsNotificationEnabled = false;
    @GuardedBy({"mLock"})
    public final Map<IBinder, Matrix> mWindowsTransformMatrixMap = new HashMap();
    public final Object mLock = new Object();
    public final Matrix mTempMatrix1 = new Matrix();
    public final Matrix mTempMatrix2 = new Matrix();
    public final float[] mTempFloat1 = new float[9];
    public final float[] mTempFloat2 = new float[9];
    public final float[] mTempFloat3 = new float[9];

    public AccessibilityWindowsPopulator(WindowManagerService windowManagerService, AccessibilityController accessibilityController) {
        this.mService = windowManagerService;
        this.mAccessibilityController = accessibilityController;
        this.mHandler = new MyHandler(windowManagerService.f1164mH.getLooper());
    }

    public void populateVisibleWindowsOnScreenLocked(int i, List<AccessibilityWindow> list) {
        Matrix matrix = new Matrix();
        Matrix matrix2 = new Matrix();
        synchronized (this.mLock) {
            List<InputWindowHandle> list2 = this.mInputWindowHandlesOnDisplays.get(i);
            if (list2 == null) {
                list.clear();
                return;
            }
            matrix.set(this.mMagnificationSpecInverseMatrix.get(i));
            WindowInfosListener.DisplayInfo displayInfo = this.mDisplayInfos.get(i);
            if (displayInfo != null) {
                matrix2.set(displayInfo.mTransform);
            } else {
                String str = TAG;
                Slog.w(str, "The displayInfo of this displayId (" + i + ") called back from the surface fligner is null");
            }
            ShellRoot shellRoot = this.mService.mRoot.getDisplayContent(i).mShellRoots.get(1);
            IBinder accessibilityWindowToken = shellRoot != null ? shellRoot.getAccessibilityWindowToken() : null;
            for (InputWindowHandle inputWindowHandle : list2) {
                list.add(AccessibilityWindow.initializeData(this.mService, inputWindowHandle, matrix, accessibilityWindowToken, matrix2));
            }
        }
    }

    public void onWindowInfosChanged(final InputWindowHandle[] inputWindowHandleArr, final WindowInfosListener.DisplayInfo[] displayInfoArr) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.wm.AccessibilityWindowsPopulator$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AccessibilityWindowsPopulator.this.lambda$onWindowInfosChanged$0(inputWindowHandleArr, displayInfoArr);
            }
        });
    }

    /* renamed from: onWindowInfosChangedInternal */
    public final void lambda$onWindowInfosChanged$0(InputWindowHandle[] inputWindowHandleArr, WindowInfosListener.DisplayInfo[] displayInfoArr) {
        ArrayList arrayList = new ArrayList();
        for (InputWindowHandle inputWindowHandle : inputWindowHandleArr) {
            int i = inputWindowHandle.inputConfig;
            boolean z = true;
            boolean z2 = (i & 2) == 0;
            boolean z3 = (i & 65536) == 0;
            boolean z4 = !inputWindowHandle.touchableRegion.isEmpty();
            z = (inputWindowHandle.frameBottom == inputWindowHandle.frameTop || inputWindowHandle.frameLeft == inputWindowHandle.frameRight) ? false : false;
            if (z2 && z3 && z4 && z) {
                arrayList.add(inputWindowHandle);
            }
        }
        HashMap<IBinder, Matrix> windowsTransformMatrix = getWindowsTransformMatrix(arrayList);
        synchronized (this.mLock) {
            this.mWindowsTransformMatrixMap.clear();
            this.mWindowsTransformMatrixMap.putAll(windowsTransformMatrix);
            this.mVisibleWindows.clear();
            this.mVisibleWindows.addAll(arrayList);
            this.mDisplayInfos.clear();
            for (WindowInfosListener.DisplayInfo displayInfo : displayInfoArr) {
                this.mDisplayInfos.put(displayInfo.mDisplayId, displayInfo);
            }
            if (this.mWindowsNotificationEnabled) {
                if (!this.mHandler.hasMessages(3)) {
                    this.mHandler.sendEmptyMessageDelayed(3, 450L);
                }
                populateVisibleWindowHandlesAndNotifyWindowsChangeIfNeeded();
            }
        }
    }

    public final HashMap<IBinder, Matrix> getWindowsTransformMatrix(List<InputWindowHandle> list) {
        HashMap<IBinder, Matrix> hashMap;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                hashMap = new HashMap<>();
                for (InputWindowHandle inputWindowHandle : list) {
                    IWindow window = inputWindowHandle.getWindow();
                    WindowState windowState = window != null ? this.mService.mWindowMap.get(window.asBinder()) : null;
                    if (windowState != null && windowState.shouldMagnify()) {
                        Matrix matrix = new Matrix();
                        windowState.getTransformationMatrix(sTempFloats, matrix);
                        hashMap.put(window.asBinder(), matrix);
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return hashMap;
    }

    public void setWindowsNotification(boolean z) {
        synchronized (this.mLock) {
            if (this.mWindowsNotificationEnabled == z) {
                return;
            }
            this.mWindowsNotificationEnabled = z;
            if (z) {
                Pair register = register();
                lambda$onWindowInfosChanged$0((InputWindowHandle[]) register.first, (WindowInfosListener.DisplayInfo[]) register.second);
            } else {
                unregister();
                releaseResources();
            }
        }
    }

    public void setMagnificationSpec(int i, MagnificationSpec magnificationSpec) {
        synchronized (this.mLock) {
            MagnificationSpec magnificationSpec2 = this.mCurrentMagnificationSpec.get(i);
            if (magnificationSpec2 == null) {
                MagnificationSpec magnificationSpec3 = new MagnificationSpec();
                magnificationSpec3.setTo(magnificationSpec);
                this.mCurrentMagnificationSpec.put(i, magnificationSpec3);
                return;
            }
            MagnificationSpec magnificationSpec4 = this.mPreviousMagnificationSpec.get(i);
            if (magnificationSpec4 == null) {
                magnificationSpec4 = new MagnificationSpec();
                this.mPreviousMagnificationSpec.put(i, magnificationSpec4);
            }
            magnificationSpec4.setTo(magnificationSpec2);
            magnificationSpec2.setTo(magnificationSpec);
        }
    }

    @GuardedBy({"mLock"})
    public final void populateVisibleWindowHandlesAndNotifyWindowsChangeIfNeeded() {
        SparseArray<List<InputWindowHandle>> sparseArray = new SparseArray<>();
        for (InputWindowHandle inputWindowHandle : this.mVisibleWindows) {
            List<InputWindowHandle> list = sparseArray.get(inputWindowHandle.displayId);
            if (list == null) {
                list = new ArrayList<>();
                sparseArray.put(inputWindowHandle.displayId, list);
            }
            list.add(inputWindowHandle);
        }
        findMagnificationSpecInverseMatrixIfNeeded(sparseArray);
        ArrayList arrayList = new ArrayList();
        getDisplaysForWindowsChanged(arrayList, sparseArray, this.mInputWindowHandlesOnDisplays);
        this.mInputWindowHandlesOnDisplays.clear();
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            this.mInputWindowHandlesOnDisplays.put(keyAt, sparseArray.get(keyAt));
        }
        if (!arrayList.isEmpty()) {
            if (this.mHandler.hasMessages(1)) {
                return;
            }
            this.mHandler.obtainMessage(1, arrayList).sendToTarget();
            return;
        }
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(2, 35L);
    }

    @GuardedBy({"mLock"})
    public static void getDisplaysForWindowsChanged(List<Integer> list, SparseArray<List<InputWindowHandle>> sparseArray, SparseArray<List<InputWindowHandle>> sparseArray2) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            if (hasWindowsChanged(sparseArray.get(keyAt), sparseArray2.get(keyAt))) {
                list.add(Integer.valueOf(keyAt));
            }
        }
    }

    @GuardedBy({"mLock"})
    public static boolean hasWindowsChanged(List<InputWindowHandle> list, List<InputWindowHandle> list2) {
        if (list2 == null || list2.size() != list.size()) {
            return true;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            IWindow window = list.get(i).getWindow();
            IWindow window2 = list2.get(i).getWindow();
            boolean z = window != null;
            boolean z2 = window2 != null;
            if (z != z2) {
                return true;
            }
            if (z && z2 && !window.asBinder().equals(window2.asBinder())) {
                return true;
            }
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final void findMagnificationSpecInverseMatrixIfNeeded(SparseArray<List<InputWindowHandle>> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            List<InputWindowHandle> list = sparseArray.get(keyAt);
            MagnificationSpec magnificationSpec = this.mCurrentMagnificationSpec.get(keyAt);
            if (magnificationSpec != null) {
                MagnificationSpec magnificationSpec2 = new MagnificationSpec();
                magnificationSpec2.setTo(magnificationSpec);
                MagnificationSpec magnificationSpec3 = this.mPreviousMagnificationSpec.get(keyAt);
                if (magnificationSpec3 == null) {
                    Matrix matrix = new Matrix();
                    generateInverseMatrix(magnificationSpec2, matrix);
                    this.mMagnificationSpecInverseMatrix.put(keyAt, matrix);
                } else {
                    MagnificationSpec magnificationSpec4 = new MagnificationSpec();
                    magnificationSpec4.setTo(magnificationSpec3);
                    generateInverseMatrixBasedOnProperMagnificationSpecForDisplay(list, magnificationSpec2, magnificationSpec4);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void generateInverseMatrixBasedOnProperMagnificationSpecForDisplay(List<InputWindowHandle> list, MagnificationSpec magnificationSpec, MagnificationSpec magnificationSpec2) {
        for (int size = list.size() - 1; size >= 0; size--) {
            Matrix matrix = this.mTempMatrix2;
            InputWindowHandle inputWindowHandle = list.get(size);
            if (getWindowTransformMatrix(inputWindowHandle.getWindow() != null ? inputWindowHandle.getWindow().asBinder() : null, matrix)) {
                generateMagnificationSpecInverseMatrix(inputWindowHandle, magnificationSpec, magnificationSpec2, matrix);
                return;
            }
        }
    }

    @GuardedBy({"mLock"})
    public final boolean getWindowTransformMatrix(IBinder iBinder, Matrix matrix) {
        Matrix matrix2 = iBinder != null ? this.mWindowsTransformMatrixMap.get(iBinder) : null;
        if (matrix2 == null) {
            return false;
        }
        matrix.set(matrix2);
        return true;
    }

    @GuardedBy({"mLock"})
    public final void generateMagnificationSpecInverseMatrix(InputWindowHandle inputWindowHandle, MagnificationSpec magnificationSpec, MagnificationSpec magnificationSpec2, Matrix matrix) {
        float[] fArr = this.mTempFloat1;
        computeIdentityMatrix(inputWindowHandle, magnificationSpec, matrix, fArr);
        float[] fArr2 = this.mTempFloat2;
        computeIdentityMatrix(inputWindowHandle, magnificationSpec2, matrix, fArr2);
        Matrix matrix2 = new Matrix();
        if (selectProperMagnificationSpecByComparingIdentityDegree(fArr, fArr2)) {
            generateInverseMatrix(magnificationSpec, matrix2);
            this.mPreviousMagnificationSpec.remove(inputWindowHandle.displayId);
            if (magnificationSpec.isNop()) {
                this.mCurrentMagnificationSpec.remove(inputWindowHandle.displayId);
                this.mMagnificationSpecInverseMatrix.remove(inputWindowHandle.displayId);
                return;
            }
        } else {
            generateInverseMatrix(magnificationSpec2, matrix2);
        }
        this.mMagnificationSpecInverseMatrix.put(inputWindowHandle.displayId, matrix2);
    }

    @GuardedBy({"mLock"})
    public final void computeIdentityMatrix(InputWindowHandle inputWindowHandle, MagnificationSpec magnificationSpec, Matrix matrix, float[] fArr) {
        Matrix matrix2 = this.mTempMatrix1;
        transformMagnificationSpecToMatrix(magnificationSpec, matrix2);
        Matrix matrix3 = new Matrix(inputWindowHandle.transform);
        matrix3.preConcat(matrix2);
        matrix3.preConcat(matrix);
        matrix3.getValues(fArr);
    }

    @GuardedBy({"mLock"})
    public final boolean selectProperMagnificationSpecByComparingIdentityDegree(float[] fArr, float[] fArr2) {
        float[] fArr3 = this.mTempFloat3;
        Matrix.IDENTITY_MATRIX.getValues(fArr3);
        float abs = Math.abs(fArr3[0] - fArr[0]);
        float abs2 = Math.abs(fArr3[0] - fArr2[0]);
        float abs3 = Math.abs(fArr3[2] - fArr[2]);
        return Float.compare(abs2, abs) > 0 || (Float.compare(abs2, abs) == 0 && Float.compare(Math.abs(fArr3[2] - fArr2[2]) + Math.abs(fArr3[5] - fArr2[5]), abs3 + Math.abs(fArr3[5] - fArr[5])) > 0);
    }

    @GuardedBy({"mLock"})
    public static void generateInverseMatrix(MagnificationSpec magnificationSpec, Matrix matrix) {
        matrix.reset();
        Matrix matrix2 = new Matrix();
        transformMagnificationSpecToMatrix(magnificationSpec, matrix2);
        if (matrix2.invert(matrix)) {
            return;
        }
        String str = TAG;
        Slog.e(str, "Can't inverse the magnification spec matrix with the magnification spec = " + magnificationSpec);
        matrix.reset();
    }

    @GuardedBy({"mLock"})
    public static void transformMagnificationSpecToMatrix(MagnificationSpec magnificationSpec, Matrix matrix) {
        matrix.reset();
        float f = magnificationSpec.scale;
        matrix.postScale(f, f);
        matrix.postTranslate(magnificationSpec.offsetX, magnificationSpec.offsetY);
    }

    public final void notifyWindowsChanged(List<Integer> list) {
        this.mHandler.removeMessages(3);
        for (int i = 0; i < list.size(); i++) {
            this.mAccessibilityController.performComputeChangedWindowsNot(list.get(i).intValue(), false);
        }
    }

    public final void forceUpdateWindows() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            for (int i = 0; i < this.mInputWindowHandlesOnDisplays.size(); i++) {
                arrayList.add(Integer.valueOf(this.mInputWindowHandlesOnDisplays.keyAt(i)));
            }
        }
        notifyWindowsChanged(arrayList);
    }

    @GuardedBy({"mLock"})
    public final void releaseResources() {
        this.mInputWindowHandlesOnDisplays.clear();
        this.mMagnificationSpecInverseMatrix.clear();
        this.mVisibleWindows.clear();
        this.mDisplayInfos.clear();
        this.mCurrentMagnificationSpec.clear();
        this.mPreviousMagnificationSpec.clear();
        this.mWindowsTransformMatrixMap.clear();
        this.mWindowsNotificationEnabled = false;
        this.mHandler.removeCallbacksAndMessages(null);
    }

    /* renamed from: com.android.server.wm.AccessibilityWindowsPopulator$MyHandler */
    /* loaded from: classes2.dex */
    public class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper, null, false);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                AccessibilityWindowsPopulator.this.notifyWindowsChanged((List) message.obj);
            } else if (i == 2) {
                AccessibilityWindowsPopulator.this.forceUpdateWindows();
            } else if (i != 3) {
            } else {
                Slog.w(AccessibilityWindowsPopulator.TAG, "Windows change within in 2 frames continuously over 500 ms and notify windows changed immediately");
                AccessibilityWindowsPopulator.this.mHandler.removeMessages(2);
                AccessibilityWindowsPopulator.this.forceUpdateWindows();
            }
        }
    }

    /* renamed from: com.android.server.wm.AccessibilityWindowsPopulator$AccessibilityWindow */
    /* loaded from: classes2.dex */
    public static class AccessibilityWindow {
        public int mDisplayId;
        public boolean mIgnoreDuetoRecentsAnimation;
        public int mInputConfig;
        public boolean mIsFocused;
        public boolean mIsPIPMenu;
        public int mPrivateFlags;
        public boolean mShouldMagnify;
        public final Region mTouchableRegionInScreen = new Region();
        public final Region mTouchableRegionInWindow = new Region();
        public int mType;
        public IWindow mWindow;
        public WindowInfo mWindowInfo;

        public static AccessibilityWindow initializeData(WindowManagerService windowManagerService, InputWindowHandle inputWindowHandle, Matrix matrix, IBinder iBinder, Matrix matrix2) {
            IWindow window = inputWindowHandle.getWindow();
            WindowState windowState = window != null ? windowManagerService.mWindowMap.get(window.asBinder()) : null;
            AccessibilityWindow accessibilityWindow = new AccessibilityWindow();
            accessibilityWindow.mWindow = window;
            accessibilityWindow.mDisplayId = inputWindowHandle.displayId;
            accessibilityWindow.mInputConfig = inputWindowHandle.inputConfig;
            accessibilityWindow.mType = inputWindowHandle.layoutParamsType;
            boolean z = true;
            accessibilityWindow.mIsPIPMenu = window != null && window.asBinder().equals(iBinder);
            accessibilityWindow.mPrivateFlags = windowState != null ? windowState.mAttrs.privateFlags : 0;
            accessibilityWindow.mIsFocused = windowState != null && windowState.isFocused();
            accessibilityWindow.mShouldMagnify = windowState == null || windowState.shouldMagnify();
            RecentsAnimationController recentsAnimationController = windowManagerService.getRecentsAnimationController();
            if (windowState == null || recentsAnimationController == null || !recentsAnimationController.shouldIgnoreForAccessibility(windowState)) {
                z = false;
            }
            accessibilityWindow.mIgnoreDuetoRecentsAnimation = z;
            getTouchableRegionInWindow(accessibilityWindow.mShouldMagnify, inputWindowHandle.touchableRegion, accessibilityWindow.mTouchableRegionInWindow, new Rect(inputWindowHandle.frameLeft, inputWindowHandle.frameTop, inputWindowHandle.frameRight, inputWindowHandle.frameBottom), matrix, matrix2);
            getUnMagnifiedTouchableRegion(accessibilityWindow.mShouldMagnify, inputWindowHandle.touchableRegion, accessibilityWindow.mTouchableRegionInScreen, matrix, matrix2);
            accessibilityWindow.mWindowInfo = windowState != null ? windowState.getWindowInfo() : getWindowInfoForWindowlessWindows(accessibilityWindow);
            Matrix matrix3 = new Matrix();
            inputWindowHandle.transform.invert(matrix3);
            matrix3.postConcat(matrix2);
            matrix3.getValues(accessibilityWindow.mWindowInfo.mTransformMatrix);
            Matrix matrix4 = new Matrix();
            if (accessibilityWindow.shouldMagnify() && matrix != null && !matrix.isIdentity()) {
                if (matrix.invert(matrix4)) {
                    matrix4.getValues(AccessibilityWindowsPopulator.sTempFloats);
                    MagnificationSpec magnificationSpec = accessibilityWindow.mWindowInfo.mMagnificationSpec;
                    magnificationSpec.scale = AccessibilityWindowsPopulator.sTempFloats[0];
                    magnificationSpec.offsetX = AccessibilityWindowsPopulator.sTempFloats[2];
                    magnificationSpec.offsetY = AccessibilityWindowsPopulator.sTempFloats[5];
                } else {
                    Slog.w(AccessibilityWindowsPopulator.TAG, "can't find spec");
                }
            }
            return accessibilityWindow;
        }

        public void getTouchableRegionInScreen(Region region) {
            region.set(this.mTouchableRegionInScreen);
        }

        public void getTouchableRegionInWindow(Region region) {
            region.set(this.mTouchableRegionInWindow);
        }

        public int getType() {
            return this.mType;
        }

        public WindowInfo getWindowInfo() {
            return this.mWindowInfo;
        }

        public boolean shouldMagnify() {
            return this.mShouldMagnify;
        }

        public boolean isFocused() {
            return this.mIsFocused;
        }

        public boolean ignoreRecentsAnimationForAccessibility() {
            return this.mIgnoreDuetoRecentsAnimation;
        }

        public boolean isTrustedOverlay() {
            return (this.mInputConfig & 256) != 0;
        }

        public boolean isTouchable() {
            return (this.mInputConfig & 8) == 0;
        }

        public boolean isUntouchableNavigationBar() {
            if (this.mType != 2019) {
                return false;
            }
            return this.mTouchableRegionInScreen.isEmpty();
        }

        public boolean isPIPMenu() {
            return this.mIsPIPMenu;
        }

        public static void getTouchableRegionInWindow(boolean z, Region region, Region region2, Rect rect, Matrix matrix, Matrix matrix2) {
            Region region3 = new Region();
            region3.set(region);
            region3.op(rect, Region.Op.INTERSECT);
            getUnMagnifiedTouchableRegion(z, region3, region2, matrix, matrix2);
        }

        public static void getUnMagnifiedTouchableRegion(boolean z, Region region, final Region region2, final Matrix matrix, final Matrix matrix2) {
            if ((!z || matrix.isIdentity()) && matrix2.isIdentity()) {
                region2.set(region);
            } else {
                RegionUtils.forEachRect(region, new Consumer() { // from class: com.android.server.wm.AccessibilityWindowsPopulator$AccessibilityWindow$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AccessibilityWindowsPopulator.AccessibilityWindow.lambda$getUnMagnifiedTouchableRegion$0(matrix2, matrix, region2, (Rect) obj);
                    }
                });
            }
        }

        public static /* synthetic */ void lambda$getUnMagnifiedTouchableRegion$0(Matrix matrix, Matrix matrix2, Region region, Rect rect) {
            RectF rectF = new RectF(rect);
            matrix.mapRect(rectF);
            matrix2.mapRect(rectF);
            region.union(new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        }

        public static WindowInfo getWindowInfoForWindowlessWindows(AccessibilityWindow accessibilityWindow) {
            WindowInfo obtain = WindowInfo.obtain();
            obtain.displayId = accessibilityWindow.mDisplayId;
            obtain.type = accessibilityWindow.mType;
            IWindow iWindow = accessibilityWindow.mWindow;
            obtain.token = iWindow != null ? iWindow.asBinder() : null;
            obtain.hasFlagWatchOutsideTouch = (accessibilityWindow.mInputConfig & 512) != 0;
            obtain.inPictureInPicture = accessibilityWindow.mIsPIPMenu;
            return obtain;
        }

        public String toString() {
            IWindow iWindow = this.mWindow;
            String obj = iWindow != null ? iWindow.asBinder().toString() : "(no window token)";
            return "A11yWindow=[" + obj + ", displayId=" + this.mDisplayId + ", inputConfig=0x" + Integer.toHexString(this.mInputConfig) + ", type=" + this.mType + ", privateFlag=0x" + Integer.toHexString(this.mPrivateFlags) + ", focused=" + this.mIsFocused + ", shouldMagnify=" + this.mShouldMagnify + ", ignoreDuetoRecentsAnimation=" + this.mIgnoreDuetoRecentsAnimation + ", isTrustedOverlay=" + isTrustedOverlay() + ", regionInScreen=" + this.mTouchableRegionInScreen + ", touchableRegion=" + this.mTouchableRegionInWindow + ", isPIPMenu=" + this.mIsPIPMenu + ", windowInfo=" + this.mWindowInfo + "]";
        }
    }
}
