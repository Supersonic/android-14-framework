package com.android.server.accessibility;

import android.graphics.Region;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.IWindow;
import android.view.WindowInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityWindowAttributes;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.accessibility.IAccessibilityInteractionConnection;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.accessibility.AccessibilitySecurityPolicy;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class AccessibilityWindowManager {
    public static int sNextWindowId;
    public final AccessibilityEventSender mAccessibilityEventSender;
    public final AccessibilitySecurityPolicy.AccessibilityUserManager mAccessibilityUserManager;
    public final Handler mHandler;
    public boolean mHasProxy;
    public final Object mLock;
    public RemoteAccessibilityConnection mPictureInPictureActionReplacingConnection;
    public final AccessibilitySecurityPolicy mSecurityPolicy;
    public int mTopFocusedDisplayId;
    public IBinder mTopFocusedWindowToken;
    public boolean mTouchInteractionInProgress;
    public final AccessibilityTraceManager mTraceManager;
    public final WindowManagerInternal mWindowManagerInternal;
    public final SparseArray<RemoteAccessibilityConnection> mGlobalInteractionConnections = new SparseArray<>();
    public final SparseArray<IBinder> mGlobalWindowTokens = new SparseArray<>();
    public final SparseArray<SparseArray<RemoteAccessibilityConnection>> mInteractionConnections = new SparseArray<>();
    public final SparseArray<SparseArray<IBinder>> mWindowTokens = new SparseArray<>();
    public int mActiveWindowId = -1;
    public int mTopFocusedWindowId = -1;
    public int mAccessibilityFocusedWindowId = -1;
    public long mAccessibilityFocusNodeId = 2147483647L;
    public int mAccessibilityFocusedDisplayId = -1;
    public final SparseArray<DisplayWindowsObserver> mDisplayWindowsObservers = new SparseArray<>();
    public final ArrayMap<IBinder, IBinder> mHostEmbeddedMap = new ArrayMap<>();
    public final SparseArray<IBinder> mWindowIdMap = new SparseArray<>();
    public final SparseArray<AccessibilityWindowAttributes> mWindowAttributes = new SparseArray<>();

    /* loaded from: classes.dex */
    public interface AccessibilityEventSender {
        void sendAccessibilityEventForCurrentUserLocked(AccessibilityEvent accessibilityEvent);
    }

    public void setAccessibilityWindowAttributes(int i, int i2, int i3, AccessibilityWindowAttributes accessibilityWindowAttributes) {
        synchronized (this.mLock) {
            if (getWindowTokenForUserAndWindowIdLocked(this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i3), i2) == null) {
                return;
            }
            this.mWindowAttributes.put(i2, accessibilityWindowAttributes);
            boolean z = findWindowInfoByIdLocked(i2) != null;
            if (z) {
                this.mWindowManagerInternal.computeWindowsForAccessibility(i);
            }
        }
    }

    public boolean windowIdBelongsToDisplayType(int i, int i2) {
        boolean z = true;
        if (this.mHasProxy && (i2 & 3) != 3) {
            synchronized (this.mLock) {
                int size = this.mDisplayWindowsObservers.size();
                for (int i3 = 0; i3 < size; i3++) {
                    DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i3);
                    if (valueAt != null && valueAt.findA11yWindowInfoByIdLocked(i) != null) {
                        if (valueAt.mIsProxy) {
                            if ((i2 & 2) != 0) {
                            }
                            z = false;
                        } else {
                            if ((i2 & 1) != 0) {
                            }
                            z = false;
                        }
                        return z;
                    }
                }
                return false;
            }
        }
        return true;
    }

    /* loaded from: classes.dex */
    public final class DisplayWindowsObserver implements WindowManagerInternal.WindowsForAccessibilityCallback {
        public final int mDisplayId;
        public boolean mHasWatchOutsideTouchWindow;
        public boolean mIsProxy;
        public int mProxyDisplayAccessibilityFocusedWindow;
        public List<AccessibilityWindowInfo> mWindows;
        public final SparseArray<AccessibilityWindowInfo> mA11yWindowInfoById = new SparseArray<>();
        public final SparseArray<WindowInfo> mWindowInfoById = new SparseArray<>();
        public final List<WindowInfo> mCachedWindowInfos = new ArrayList();
        public boolean mTrackingWindows = false;

        public final int getTypeForWindowManagerWindowType(int i) {
            if (i != 1 && i != 2 && i != 3 && i != 4 && i != 1005) {
                if (i != 2017 && i != 2024) {
                    if (i == 2032) {
                        return 4;
                    }
                    if (i == 2034) {
                        return 5;
                    }
                    if (i != 2036 && i != 2019 && i != 2020) {
                        switch (i) {
                            case 1000:
                            case 1001:
                            case 1002:
                            case 1003:
                                break;
                            default:
                                switch (i) {
                                    case 2000:
                                    case 2001:
                                    case 2003:
                                        break;
                                    case 2002:
                                        break;
                                    default:
                                        switch (i) {
                                            case 2005:
                                            case 2007:
                                            case 2012:
                                                break;
                                            case 2006:
                                            case 2008:
                                            case 2009:
                                            case 2010:
                                                break;
                                            case 2011:
                                                return 2;
                                            default:
                                                switch (i) {
                                                    case 2038:
                                                    case 2040:
                                                    case 2041:
                                                        break;
                                                    case 2039:
                                                        return 6;
                                                    default:
                                                        return -1;
                                                }
                                        }
                                }
                        }
                    }
                }
                return 3;
            }
            return 1;
        }

        public DisplayWindowsObserver(int i) {
            this.mDisplayId = i;
        }

        public void startTrackingWindowsLocked() {
            if (this.mTrackingWindows) {
                return;
            }
            this.mTrackingWindows = true;
            if (AccessibilityWindowManager.this.traceWMEnabled()) {
                AccessibilityWindowManager accessibilityWindowManager = AccessibilityWindowManager.this;
                accessibilityWindowManager.logTraceWM("setWindowsForAccessibilityCallback", "displayId=" + this.mDisplayId + ";callback=" + this);
            }
            AccessibilityWindowManager.this.mWindowManagerInternal.setWindowsForAccessibilityCallback(this.mDisplayId, this);
        }

        public void stopTrackingWindowsLocked() {
            if (this.mTrackingWindows) {
                if (AccessibilityWindowManager.this.traceWMEnabled()) {
                    AccessibilityWindowManager accessibilityWindowManager = AccessibilityWindowManager.this;
                    accessibilityWindowManager.logTraceWM("setWindowsForAccessibilityCallback", "displayId=" + this.mDisplayId + ";callback=null");
                }
                AccessibilityWindowManager.this.mWindowManagerInternal.setWindowsForAccessibilityCallback(this.mDisplayId, null);
                this.mTrackingWindows = false;
                clearWindowsLocked();
            }
        }

        public boolean isTrackingWindowsLocked() {
            return this.mTrackingWindows;
        }

        public List<AccessibilityWindowInfo> getWindowListLocked() {
            return this.mWindows;
        }

        public AccessibilityWindowInfo findA11yWindowInfoByIdLocked(int i) {
            return this.mA11yWindowInfoById.get(i);
        }

        public WindowInfo findWindowInfoByIdLocked(int i) {
            return this.mWindowInfoById.get(i);
        }

        public AccessibilityWindowInfo getPictureInPictureWindowLocked() {
            List<AccessibilityWindowInfo> list = this.mWindows;
            if (list != null) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    AccessibilityWindowInfo accessibilityWindowInfo = this.mWindows.get(i);
                    if (accessibilityWindowInfo.isInPictureInPictureMode()) {
                        return accessibilityWindowInfo;
                    }
                }
                return null;
            }
            return null;
        }

        public boolean setActiveWindowLocked(int i) {
            List<AccessibilityWindowInfo> list = this.mWindows;
            if (list != null) {
                int size = list.size();
                boolean z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    AccessibilityWindowInfo accessibilityWindowInfo = this.mWindows.get(i2);
                    if (accessibilityWindowInfo.getId() == i) {
                        z = true;
                        accessibilityWindowInfo.setActive(true);
                    } else {
                        accessibilityWindowInfo.setActive(false);
                    }
                }
                return z;
            }
            return false;
        }

        public boolean setAccessibilityFocusedWindowLocked(int i) {
            List<AccessibilityWindowInfo> list = this.mWindows;
            if (list != null) {
                int size = list.size();
                boolean z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    AccessibilityWindowInfo accessibilityWindowInfo = this.mWindows.get(i2);
                    if (accessibilityWindowInfo.getId() == i) {
                        z = true;
                        accessibilityWindowInfo.setAccessibilityFocused(true);
                    } else {
                        accessibilityWindowInfo.setAccessibilityFocused(false);
                    }
                }
                return z;
            }
            return false;
        }

        public boolean computePartialInteractiveRegionForWindowLocked(int i, boolean z, Region region) {
            List<AccessibilityWindowInfo> list = this.mWindows;
            boolean z2 = false;
            if (list == null) {
                return false;
            }
            int size = list.size();
            Region region2 = new Region();
            Region region3 = null;
            for (int i2 = size - 1; i2 >= 0; i2--) {
                AccessibilityWindowInfo accessibilityWindowInfo = this.mWindows.get(i2);
                if (region3 == null) {
                    if (accessibilityWindowInfo.getId() == i) {
                        accessibilityWindowInfo.getRegionInScreen(region2);
                        region.set(region2);
                        region3 = region;
                        if (!z) {
                        }
                        z2 = true;
                    }
                } else {
                    if (accessibilityWindowInfo.getType() != 4) {
                        accessibilityWindowInfo.getRegionInScreen(region2);
                        if (!region3.op(region2, Region.Op.DIFFERENCE)) {
                        }
                        z2 = true;
                    }
                }
            }
            return z2;
        }

        public List<Integer> getWatchOutsideTouchWindowIdLocked(int i) {
            WindowInfo windowInfo = this.mWindowInfoById.get(i);
            if (windowInfo != null && this.mHasWatchOutsideTouchWindow) {
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < this.mWindowInfoById.size(); i2++) {
                    WindowInfo valueAt = this.mWindowInfoById.valueAt(i2);
                    if (valueAt != null && valueAt.layer < windowInfo.layer && valueAt.hasFlagWatchOutsideTouch) {
                        arrayList.add(Integer.valueOf(this.mWindowInfoById.keyAt(i2)));
                    }
                }
                return arrayList;
            }
            return Collections.emptyList();
        }

        @Override // com.android.server.p014wm.WindowManagerInternal.WindowsForAccessibilityCallback
        public void onWindowsForAccessibilityChanged(boolean z, int i, IBinder iBinder, List<WindowInfo> list) {
            synchronized (AccessibilityWindowManager.this.mLock) {
                updateWindowsByWindowAttributesLocked(list);
                if (shouldUpdateWindowsLocked(z, list)) {
                    AccessibilityWindowManager.this.mTopFocusedDisplayId = i;
                    AccessibilityWindowManager.this.mTopFocusedWindowToken = iBinder;
                    cacheWindows(list);
                    updateWindowsLocked(AccessibilityWindowManager.this.mAccessibilityUserManager.getCurrentUserIdLocked(), list);
                    AccessibilityWindowManager.this.mLock.notifyAll();
                }
            }
        }

        public final void updateWindowsByWindowAttributesLocked(List<WindowInfo> list) {
            for (int size = list.size() - 1; size >= 0; size--) {
                WindowInfo windowInfo = list.get(size);
                IBinder iBinder = windowInfo.token;
                AccessibilityWindowManager accessibilityWindowManager = AccessibilityWindowManager.this;
                updateWindowWithWindowAttributes(windowInfo, (AccessibilityWindowAttributes) AccessibilityWindowManager.this.mWindowAttributes.get(accessibilityWindowManager.findWindowIdLocked(accessibilityWindowManager.mAccessibilityUserManager.getCurrentUserIdLocked(), iBinder)));
            }
        }

        public final void updateWindowWithWindowAttributes(WindowInfo windowInfo, AccessibilityWindowAttributes accessibilityWindowAttributes) {
            if (accessibilityWindowAttributes == null) {
                return;
            }
            windowInfo.title = accessibilityWindowAttributes.getWindowTitle();
            windowInfo.locales = accessibilityWindowAttributes.getLocales();
        }

        public final boolean shouldUpdateWindowsLocked(boolean z, List<WindowInfo> list) {
            int size;
            if (!z && this.mCachedWindowInfos.size() == (size = list.size())) {
                if (!this.mCachedWindowInfos.isEmpty() || !list.isEmpty()) {
                    for (int i = 0; i < size; i++) {
                        if (windowChangedNoLayer(this.mCachedWindowInfos.get(i), list.get(i))) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return true;
        }

        public final void cacheWindows(List<WindowInfo> list) {
            for (int size = this.mCachedWindowInfos.size() - 1; size >= 0; size--) {
                this.mCachedWindowInfos.remove(size).recycle();
            }
            int size2 = list.size();
            for (int i = 0; i < size2; i++) {
                this.mCachedWindowInfos.add(WindowInfo.obtain(list.get(i)));
            }
        }

        public final boolean windowChangedNoLayer(WindowInfo windowInfo, WindowInfo windowInfo2) {
            List list;
            if (windowInfo == windowInfo2) {
                return false;
            }
            if (windowInfo != null && windowInfo2 != null && windowInfo.type == windowInfo2.type && windowInfo.focused == windowInfo2.focused) {
                IBinder iBinder = windowInfo.token;
                if (iBinder == null) {
                    if (windowInfo2.token != null) {
                        return true;
                    }
                } else if (!iBinder.equals(windowInfo2.token)) {
                    return true;
                }
                IBinder iBinder2 = windowInfo.parentToken;
                if (iBinder2 == null) {
                    if (windowInfo2.parentToken != null) {
                        return true;
                    }
                } else if (!iBinder2.equals(windowInfo2.parentToken)) {
                    return true;
                }
                IBinder iBinder3 = windowInfo.activityToken;
                if (iBinder3 == null) {
                    if (windowInfo2.activityToken != null) {
                        return true;
                    }
                } else if (!iBinder3.equals(windowInfo2.activityToken)) {
                    return true;
                }
                if (windowInfo.regionInScreen.equals(windowInfo2.regionInScreen)) {
                    List list2 = windowInfo.childTokens;
                    return ((list2 == null || (list = windowInfo2.childTokens) == null || list2.equals(list)) && TextUtils.equals(windowInfo.title, windowInfo2.title) && windowInfo.accessibilityIdOfAnchor == windowInfo2.accessibilityIdOfAnchor && windowInfo.inPictureInPicture == windowInfo2.inPictureInPicture && windowInfo.hasFlagWatchOutsideTouch == windowInfo2.hasFlagWatchOutsideTouch && windowInfo.displayId == windowInfo2.displayId && windowInfo.taskId == windowInfo2.taskId && Arrays.equals(windowInfo.mTransformMatrix, windowInfo2.mTransformMatrix)) ? false : true;
                }
                return true;
            }
            return true;
        }

        public final void clearWindowsLocked() {
            List<WindowInfo> emptyList = Collections.emptyList();
            int i = AccessibilityWindowManager.this.mActiveWindowId;
            updateWindowsLocked(AccessibilityWindowManager.this.mAccessibilityUserManager.getCurrentUserIdLocked(), emptyList);
            AccessibilityWindowManager.this.mActiveWindowId = i;
            this.mWindows = null;
        }

        public final void updateWindowsLocked(int i, List<WindowInfo> list) {
            int i2;
            int i3;
            boolean z;
            AccessibilityWindowInfo accessibilityWindowInfo;
            int i4 = i;
            if (this.mWindows == null) {
                this.mWindows = new ArrayList();
            }
            ArrayList arrayList = new ArrayList(this.mWindows);
            SparseArray<AccessibilityWindowInfo> clone = this.mA11yWindowInfoById.clone();
            this.mWindows.clear();
            this.mA11yWindowInfoById.clear();
            for (int i5 = 0; i5 < this.mWindowInfoById.size(); i5++) {
                this.mWindowInfoById.valueAt(i5).recycle();
            }
            this.mWindowInfoById.clear();
            this.mHasWatchOutsideTouchWindow = false;
            int size = list.size();
            boolean z2 = this.mDisplayId == AccessibilityWindowManager.this.mTopFocusedDisplayId;
            boolean z3 = this.mDisplayId == AccessibilityWindowManager.this.mAccessibilityFocusedDisplayId || (this.mIsProxy && this.mProxyDisplayAccessibilityFocusedWindow != -1);
            if (z2) {
                if (size > 0) {
                    AccessibilityWindowManager accessibilityWindowManager = AccessibilityWindowManager.this;
                    accessibilityWindowManager.mTopFocusedWindowId = accessibilityWindowManager.findWindowIdLocked(i4, accessibilityWindowManager.mTopFocusedWindowToken);
                } else {
                    AccessibilityWindowManager.this.mTopFocusedWindowId = -1;
                }
                if (!AccessibilityWindowManager.this.mTouchInteractionInProgress) {
                    AccessibilityWindowManager.this.mActiveWindowId = -1;
                }
            }
            if (this.mIsProxy) {
                i2 = this.mProxyDisplayAccessibilityFocusedWindow;
            } else {
                i2 = AccessibilityWindowManager.this.mAccessibilityFocusedWindowId;
            }
            boolean z4 = z3 && i2 != -1;
            if (size > 0) {
                int i6 = 0;
                boolean z5 = false;
                boolean z6 = true;
                while (i6 < size) {
                    WindowInfo windowInfo = list.get(i6);
                    if (this.mTrackingWindows) {
                        accessibilityWindowInfo = populateReportedWindowLocked(i4, windowInfo, clone);
                        if (accessibilityWindowInfo == null) {
                            z5 = true;
                        }
                    } else {
                        accessibilityWindowInfo = null;
                    }
                    if (accessibilityWindowInfo != null) {
                        accessibilityWindowInfo.setLayer((size - 1) - accessibilityWindowInfo.getLayer());
                        int id = accessibilityWindowInfo.getId();
                        if (accessibilityWindowInfo.isFocused() && z2) {
                            if (!AccessibilityWindowManager.this.mTouchInteractionInProgress) {
                                AccessibilityWindowManager.this.mActiveWindowId = id;
                                accessibilityWindowInfo.setActive(true);
                            } else if (id == AccessibilityWindowManager.this.mActiveWindowId) {
                                z6 = false;
                            }
                        }
                        if (!this.mHasWatchOutsideTouchWindow && windowInfo.hasFlagWatchOutsideTouch) {
                            this.mHasWatchOutsideTouchWindow = true;
                        }
                        this.mWindows.add(accessibilityWindowInfo);
                        this.mA11yWindowInfoById.put(id, accessibilityWindowInfo);
                        this.mWindowInfoById.put(id, WindowInfo.obtain(windowInfo));
                    }
                    i6++;
                    i4 = i;
                }
                int size2 = this.mWindows.size();
                if (z5) {
                    for (int i7 = 0; i7 < size2; i7++) {
                        this.mWindows.get(i7).setLayer((size2 - 1) - i7);
                    }
                }
                if (z2) {
                    if (AccessibilityWindowManager.this.mTouchInteractionInProgress && z6) {
                        AccessibilityWindowManager accessibilityWindowManager2 = AccessibilityWindowManager.this;
                        accessibilityWindowManager2.mActiveWindowId = accessibilityWindowManager2.mTopFocusedWindowId;
                    }
                    for (int i8 = 0; i8 < size2; i8++) {
                        AccessibilityWindowInfo accessibilityWindowInfo2 = this.mWindows.get(i8);
                        if (accessibilityWindowInfo2.getId() == AccessibilityWindowManager.this.mActiveWindowId) {
                            accessibilityWindowInfo2.setActive(true);
                        }
                    }
                }
                if (z3) {
                    for (int i9 = 0; i9 < size2; i9++) {
                        AccessibilityWindowInfo accessibilityWindowInfo3 = this.mWindows.get(i9);
                        if (accessibilityWindowInfo3.getId() == i2) {
                            i3 = 1;
                            accessibilityWindowInfo3.setAccessibilityFocused(true);
                            z = false;
                            break;
                        }
                    }
                }
            }
            i3 = 1;
            z = z4;
            sendEventsForChangedWindowsLocked(arrayList, clone);
            for (int size3 = arrayList.size() - i3; size3 >= 0; size3--) {
                arrayList.remove(size3).recycle();
            }
            if (z) {
                AccessibilityWindowManager.this.clearAccessibilityFocusLocked(i2);
            }
        }

        public final void sendEventsForChangedWindowsLocked(List<AccessibilityWindowInfo> list, SparseArray<AccessibilityWindowInfo> sparseArray) {
            ArrayList arrayList = new ArrayList();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                AccessibilityWindowInfo accessibilityWindowInfo = list.get(i);
                if (this.mA11yWindowInfoById.get(accessibilityWindowInfo.getId()) == null) {
                    arrayList.add(AccessibilityEvent.obtainWindowsChangedEvent(this.mDisplayId, accessibilityWindowInfo.getId(), 2));
                }
            }
            int size2 = this.mWindows.size();
            for (int i2 = 0; i2 < size2; i2++) {
                AccessibilityWindowInfo accessibilityWindowInfo2 = this.mWindows.get(i2);
                AccessibilityWindowInfo accessibilityWindowInfo3 = sparseArray.get(accessibilityWindowInfo2.getId());
                if (accessibilityWindowInfo3 == null) {
                    arrayList.add(AccessibilityEvent.obtainWindowsChangedEvent(this.mDisplayId, accessibilityWindowInfo2.getId(), 1));
                } else {
                    int differenceFrom = accessibilityWindowInfo2.differenceFrom(accessibilityWindowInfo3);
                    if (differenceFrom != 0) {
                        arrayList.add(AccessibilityEvent.obtainWindowsChangedEvent(this.mDisplayId, accessibilityWindowInfo2.getId(), differenceFrom));
                    }
                }
            }
            int size3 = arrayList.size();
            for (int i3 = 0; i3 < size3; i3++) {
                AccessibilityWindowManager.this.mAccessibilityEventSender.sendAccessibilityEventForCurrentUserLocked((AccessibilityEvent) arrayList.get(i3));
            }
        }

        public final AccessibilityWindowInfo populateReportedWindowLocked(int i, WindowInfo windowInfo, SparseArray<AccessibilityWindowInfo> sparseArray) {
            int findWindowIdLocked = AccessibilityWindowManager.this.findWindowIdLocked(i, windowInfo.token);
            if (findWindowIdLocked < 0) {
                return null;
            }
            if (AccessibilityWindowManager.this.mHostEmbeddedMap.size() <= 0 || !isEmbeddedHierarchyWindowsLocked(findWindowIdLocked)) {
                AccessibilityWindowInfo obtain = AccessibilityWindowInfo.obtain();
                obtain.setId(findWindowIdLocked);
                obtain.setType(getTypeForWindowManagerWindowType(windowInfo.type));
                obtain.setLayer(windowInfo.layer);
                obtain.setFocused(windowInfo.focused);
                obtain.setRegionInScreen(windowInfo.regionInScreen);
                obtain.setTitle(windowInfo.title);
                obtain.setAnchorId(windowInfo.accessibilityIdOfAnchor);
                obtain.setPictureInPicture(windowInfo.inPictureInPicture);
                obtain.setDisplayId(windowInfo.displayId);
                obtain.setTaskId(windowInfo.taskId);
                obtain.setLocales(windowInfo.locales);
                int findWindowIdLocked2 = AccessibilityWindowManager.this.findWindowIdLocked(i, windowInfo.parentToken);
                if (findWindowIdLocked2 >= 0) {
                    obtain.setParentId(findWindowIdLocked2);
                }
                List list = windowInfo.childTokens;
                if (list != null) {
                    int size = list.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        int findWindowIdLocked3 = AccessibilityWindowManager.this.findWindowIdLocked(i, (IBinder) windowInfo.childTokens.get(i2));
                        if (findWindowIdLocked3 >= 0) {
                            obtain.addChild(findWindowIdLocked3);
                        }
                    }
                }
                AccessibilityWindowInfo accessibilityWindowInfo = sparseArray.get(findWindowIdLocked);
                if (accessibilityWindowInfo == null) {
                    obtain.setTransitionTimeMillis(SystemClock.uptimeMillis());
                } else {
                    Region region = new Region();
                    accessibilityWindowInfo.getRegionInScreen(region);
                    if (region.equals(windowInfo.regionInScreen)) {
                        obtain.setTransitionTimeMillis(accessibilityWindowInfo.getTransitionTimeMillis());
                    } else {
                        obtain.setTransitionTimeMillis(SystemClock.uptimeMillis());
                    }
                }
                return obtain;
            }
            return null;
        }

        public final boolean isEmbeddedHierarchyWindowsLocked(int i) {
            IBinder iBinder = (IBinder) AccessibilityWindowManager.this.mWindowIdMap.get(i);
            if (iBinder == null) {
                return false;
            }
            for (int i2 = 0; i2 < AccessibilityWindowManager.this.mHostEmbeddedMap.size(); i2++) {
                if (((IBinder) AccessibilityWindowManager.this.mHostEmbeddedMap.keyAt(i2)).equals(iBinder)) {
                    return true;
                }
            }
            return false;
        }

        public void dumpLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.append("Global Info [ ");
            printWriter.println("Top focused display Id = " + AccessibilityWindowManager.this.mTopFocusedDisplayId);
            printWriter.println("     Active Window Id = " + AccessibilityWindowManager.this.mActiveWindowId);
            printWriter.println("     Top Focused Window Id = " + AccessibilityWindowManager.this.mTopFocusedWindowId);
            printWriter.println("     Accessibility Focused Window Id = " + AccessibilityWindowManager.this.mAccessibilityFocusedWindowId + " ]");
            if (this.mIsProxy) {
                printWriter.println("Proxy accessibility focused window = " + this.mProxyDisplayAccessibilityFocusedWindow);
            }
            printWriter.println();
            List<AccessibilityWindowInfo> list = this.mWindows;
            if (list != null) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    if (i == 0) {
                        printWriter.append("Display[");
                        printWriter.append((CharSequence) Integer.toString(this.mDisplayId));
                        printWriter.append("] : ");
                        printWriter.println();
                    }
                    if (i > 0) {
                        printWriter.append(',');
                        printWriter.println();
                    }
                    printWriter.append("A11yWindow[");
                    AccessibilityWindowInfo accessibilityWindowInfo = this.mWindows.get(i);
                    printWriter.append((CharSequence) accessibilityWindowInfo.toString());
                    printWriter.append(']');
                    printWriter.println();
                    WindowInfo findWindowInfoByIdLocked = findWindowInfoByIdLocked(accessibilityWindowInfo.getId());
                    if (findWindowInfoByIdLocked != null) {
                        printWriter.append("WindowInfo[");
                        printWriter.append((CharSequence) findWindowInfoByIdLocked.toString());
                        printWriter.append("]");
                        printWriter.println();
                    }
                }
                printWriter.println();
            }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public final class RemoteAccessibilityConnection implements IBinder.DeathRecipient {
        public final IAccessibilityInteractionConnection mConnection;
        public final String mPackageName;
        public final int mUid;
        public final int mUserId;
        public final int mWindowId;

        public RemoteAccessibilityConnection(int i, IAccessibilityInteractionConnection iAccessibilityInteractionConnection, String str, int i2, int i3) {
            this.mWindowId = i;
            this.mPackageName = str;
            this.mUid = i2;
            this.mUserId = i3;
            this.mConnection = iAccessibilityInteractionConnection;
        }

        public int getUid() {
            return this.mUid;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public IAccessibilityInteractionConnection getRemote() {
            return this.mConnection;
        }

        public void linkToDeath() throws RemoteException {
            this.mConnection.asBinder().linkToDeath(this, 0);
        }

        public void unlinkToDeath() {
            this.mConnection.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            unlinkToDeath();
            synchronized (AccessibilityWindowManager.this.mLock) {
                AccessibilityWindowManager.this.removeAccessibilityInteractionConnectionLocked(this.mWindowId, this.mUserId);
            }
        }
    }

    public AccessibilityWindowManager(Object obj, Handler handler, WindowManagerInternal windowManagerInternal, AccessibilityEventSender accessibilityEventSender, AccessibilitySecurityPolicy accessibilitySecurityPolicy, AccessibilitySecurityPolicy.AccessibilityUserManager accessibilityUserManager, AccessibilityTraceManager accessibilityTraceManager) {
        this.mLock = obj;
        this.mHandler = handler;
        this.mWindowManagerInternal = windowManagerInternal;
        this.mAccessibilityEventSender = accessibilityEventSender;
        this.mSecurityPolicy = accessibilitySecurityPolicy;
        this.mAccessibilityUserManager = accessibilityUserManager;
        this.mTraceManager = accessibilityTraceManager;
    }

    public void startTrackingWindows(int i, boolean z) {
        synchronized (this.mLock) {
            DisplayWindowsObserver displayWindowsObserver = this.mDisplayWindowsObservers.get(i);
            if (displayWindowsObserver == null) {
                displayWindowsObserver = new DisplayWindowsObserver(i);
            }
            if (z && !displayWindowsObserver.mIsProxy) {
                displayWindowsObserver.mIsProxy = true;
                this.mHasProxy = true;
            }
            if (displayWindowsObserver.isTrackingWindowsLocked()) {
                return;
            }
            displayWindowsObserver.startTrackingWindowsLocked();
            this.mDisplayWindowsObservers.put(i, displayWindowsObserver);
        }
    }

    public void stopTrackingWindows(int i) {
        synchronized (this.mLock) {
            DisplayWindowsObserver displayWindowsObserver = this.mDisplayWindowsObservers.get(i);
            if (displayWindowsObserver != null) {
                displayWindowsObserver.stopTrackingWindowsLocked();
                this.mDisplayWindowsObservers.remove(i);
            }
            resetHasProxyIfNeededLocked();
        }
    }

    public void stopTrackingDisplayProxy(int i) {
        synchronized (this.mLock) {
            DisplayWindowsObserver displayWindowsObserver = this.mDisplayWindowsObservers.get(i);
            if (displayWindowsObserver != null) {
                displayWindowsObserver.mIsProxy = false;
            }
            resetHasProxyIfNeededLocked();
        }
    }

    public final void resetHasProxyIfNeededLocked() {
        int size = this.mDisplayWindowsObservers.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i);
            if (valueAt != null && valueAt.mIsProxy) {
                z = true;
            }
        }
        this.mHasProxy = z;
    }

    public boolean isTrackingWindowsLocked() {
        return this.mDisplayWindowsObservers.size() > 0;
    }

    public boolean isTrackingWindowsLocked(int i) {
        DisplayWindowsObserver displayWindowsObserver = this.mDisplayWindowsObservers.get(i);
        if (displayWindowsObserver != null) {
            return displayWindowsObserver.isTrackingWindowsLocked();
        }
        return false;
    }

    public List<AccessibilityWindowInfo> getWindowListLocked(int i) {
        DisplayWindowsObserver displayWindowsObserver = this.mDisplayWindowsObservers.get(i);
        if (displayWindowsObserver != null) {
            return displayWindowsObserver.getWindowListLocked();
        }
        return null;
    }

    public int addAccessibilityInteractionConnection(IWindow iWindow, IBinder iBinder, IAccessibilityInteractionConnection iAccessibilityInteractionConnection, String str, int i) throws RemoteException {
        int i2;
        boolean isTrackingWindowsLocked;
        IBinder asBinder = iWindow.asBinder();
        if (traceWMEnabled()) {
            logTraceWM("getDisplayIdForWindow", "token=" + asBinder);
        }
        int displayIdForWindow = this.mWindowManagerInternal.getDisplayIdForWindow(asBinder);
        synchronized (this.mLock) {
            int resolveCallingUserIdEnforcingPermissionsLocked = this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(i);
            int uid = UserHandle.getUid(resolveCallingUserIdEnforcingPermissionsLocked, UserHandle.getCallingAppId());
            String resolveValidReportedPackageLocked = this.mSecurityPolicy.resolveValidReportedPackageLocked(str, UserHandle.getCallingAppId(), resolveCallingUserIdEnforcingPermissionsLocked, Binder.getCallingPid());
            i2 = sNextWindowId;
            sNextWindowId = i2 + 1;
            if (this.mSecurityPolicy.isCallerInteractingAcrossUsers(i)) {
                RemoteAccessibilityConnection remoteAccessibilityConnection = new RemoteAccessibilityConnection(i2, iAccessibilityInteractionConnection, resolveValidReportedPackageLocked, uid, -1);
                remoteAccessibilityConnection.linkToDeath();
                this.mGlobalInteractionConnections.put(i2, remoteAccessibilityConnection);
                this.mGlobalWindowTokens.put(i2, asBinder);
            } else {
                RemoteAccessibilityConnection remoteAccessibilityConnection2 = new RemoteAccessibilityConnection(i2, iAccessibilityInteractionConnection, resolveValidReportedPackageLocked, uid, resolveCallingUserIdEnforcingPermissionsLocked);
                remoteAccessibilityConnection2.linkToDeath();
                getInteractionConnectionsForUserLocked(resolveCallingUserIdEnforcingPermissionsLocked).put(i2, remoteAccessibilityConnection2);
                getWindowTokensForUserLocked(resolveCallingUserIdEnforcingPermissionsLocked).put(i2, asBinder);
            }
            isTrackingWindowsLocked = isTrackingWindowsLocked(displayIdForWindow);
            registerIdLocked(iBinder, i2);
        }
        if (isTrackingWindowsLocked) {
            if (traceWMEnabled()) {
                logTraceWM("computeWindowsForAccessibility", "displayId=" + displayIdForWindow);
            }
            this.mWindowManagerInternal.computeWindowsForAccessibility(displayIdForWindow);
        }
        if (traceWMEnabled()) {
            logTraceWM("setAccessibilityIdToSurfaceMetadata", "token=" + asBinder + ";windowId=" + i2);
        }
        this.mWindowManagerInternal.setAccessibilityIdToSurfaceMetadata(asBinder, i2);
        return i2;
    }

    public void removeAccessibilityInteractionConnection(IWindow iWindow) {
        synchronized (this.mLock) {
            this.mSecurityPolicy.resolveCallingUserIdEnforcingPermissionsLocked(UserHandle.getCallingUserId());
            IBinder asBinder = iWindow.asBinder();
            int removeAccessibilityInteractionConnectionInternalLocked = removeAccessibilityInteractionConnectionInternalLocked(asBinder, this.mGlobalWindowTokens, this.mGlobalInteractionConnections);
            if (removeAccessibilityInteractionConnectionInternalLocked >= 0) {
                onAccessibilityInteractionConnectionRemovedLocked(removeAccessibilityInteractionConnectionInternalLocked, asBinder);
                return;
            }
            int size = this.mWindowTokens.size();
            for (int i = 0; i < size; i++) {
                int keyAt = this.mWindowTokens.keyAt(i);
                int removeAccessibilityInteractionConnectionInternalLocked2 = removeAccessibilityInteractionConnectionInternalLocked(asBinder, getWindowTokensForUserLocked(keyAt), getInteractionConnectionsForUserLocked(keyAt));
                if (removeAccessibilityInteractionConnectionInternalLocked2 >= 0) {
                    onAccessibilityInteractionConnectionRemovedLocked(removeAccessibilityInteractionConnectionInternalLocked2, asBinder);
                    return;
                }
            }
        }
    }

    public RemoteAccessibilityConnection getConnectionLocked(int i, int i2) {
        RemoteAccessibilityConnection remoteAccessibilityConnection = this.mGlobalInteractionConnections.get(i2);
        if (remoteAccessibilityConnection == null && isValidUserForInteractionConnectionsLocked(i)) {
            remoteAccessibilityConnection = getInteractionConnectionsForUserLocked(i).get(i2);
        }
        if (remoteAccessibilityConnection == null || remoteAccessibilityConnection.getRemote() == null) {
            return null;
        }
        return remoteAccessibilityConnection;
    }

    public final int removeAccessibilityInteractionConnectionInternalLocked(IBinder iBinder, SparseArray<IBinder> sparseArray, SparseArray<RemoteAccessibilityConnection> sparseArray2) {
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            if (sparseArray.valueAt(i) == iBinder) {
                int keyAt = sparseArray.keyAt(i);
                sparseArray.removeAt(i);
                sparseArray2.get(keyAt).unlinkToDeath();
                sparseArray2.remove(keyAt);
                return keyAt;
            }
        }
        return -1;
    }

    public final void removeAccessibilityInteractionConnectionLocked(int i, int i2) {
        IBinder iBinder;
        IBinder iBinder2;
        if (i2 == -1) {
            iBinder2 = this.mGlobalWindowTokens.get(i);
            this.mGlobalWindowTokens.remove(i);
            this.mGlobalInteractionConnections.remove(i);
        } else {
            if (isValidUserForWindowTokensLocked(i2)) {
                iBinder = getWindowTokensForUserLocked(i2).get(i);
                getWindowTokensForUserLocked(i2).remove(i);
            } else {
                iBinder = null;
            }
            if (isValidUserForInteractionConnectionsLocked(i2)) {
                getInteractionConnectionsForUserLocked(i2).remove(i);
            }
            iBinder2 = iBinder;
        }
        onAccessibilityInteractionConnectionRemovedLocked(i, iBinder2);
    }

    public final void onAccessibilityInteractionConnectionRemovedLocked(int i, IBinder iBinder) {
        if (!isTrackingWindowsLocked() && i >= 0 && this.mActiveWindowId == i) {
            this.mActiveWindowId = -1;
        }
        if (iBinder != null) {
            if (traceWMEnabled()) {
                logTraceWM("setAccessibilityIdToSurfaceMetadata", "token=" + iBinder + ";windowId=AccessibilityWindowInfo.UNDEFINED_WINDOW_ID");
            }
            this.mWindowManagerInternal.setAccessibilityIdToSurfaceMetadata(iBinder, -1);
        }
        unregisterIdLocked(i);
        this.mWindowAttributes.remove(i);
    }

    public IBinder getWindowTokenForUserAndWindowIdLocked(int i, int i2) {
        IBinder iBinder = this.mGlobalWindowTokens.get(i2);
        return (iBinder == null && isValidUserForWindowTokensLocked(i)) ? getWindowTokensForUserLocked(i).get(i2) : iBinder;
    }

    public int getWindowOwnerUserId(IBinder iBinder) {
        if (traceWMEnabled()) {
            logTraceWM("getWindowOwnerUserId", "token=" + iBinder);
        }
        return this.mWindowManagerInternal.getWindowOwnerUserId(iBinder);
    }

    public int findWindowIdLocked(int i, IBinder iBinder) {
        int indexOfValue;
        int indexOfValue2 = this.mGlobalWindowTokens.indexOfValue(iBinder);
        if (indexOfValue2 >= 0) {
            return this.mGlobalWindowTokens.keyAt(indexOfValue2);
        }
        if (!isValidUserForWindowTokensLocked(i) || (indexOfValue = getWindowTokensForUserLocked(i).indexOfValue(iBinder)) < 0) {
            return -1;
        }
        return getWindowTokensForUserLocked(i).keyAt(indexOfValue);
    }

    public void associateEmbeddedHierarchyLocked(IBinder iBinder, IBinder iBinder2) {
        associateLocked(iBinder2, iBinder);
    }

    public void disassociateEmbeddedHierarchyLocked(IBinder iBinder) {
        disassociateLocked(iBinder);
    }

    public int resolveParentWindowIdLocked(int i) {
        int windowIdLocked;
        IBinder tokenLocked = getTokenLocked(i);
        return (tokenLocked == null || (windowIdLocked = getWindowIdLocked(resolveTopParentTokenLocked(tokenLocked))) == -1) ? i : windowIdLocked;
    }

    public final IBinder resolveTopParentTokenLocked(IBinder iBinder) {
        IBinder hostTokenLocked = getHostTokenLocked(iBinder);
        return hostTokenLocked == null ? iBinder : resolveTopParentTokenLocked(hostTokenLocked);
    }

    public boolean computePartialInteractiveRegionForWindowLocked(int i, Region region) {
        int resolveParentWindowIdLocked = resolveParentWindowIdLocked(i);
        DisplayWindowsObserver displayWindowObserverByWindowIdLocked = getDisplayWindowObserverByWindowIdLocked(resolveParentWindowIdLocked);
        if (displayWindowObserverByWindowIdLocked != null) {
            return displayWindowObserverByWindowIdLocked.computePartialInteractiveRegionForWindowLocked(resolveParentWindowIdLocked, resolveParentWindowIdLocked != i, region);
        }
        return false;
    }

    public void updateActiveAndAccessibilityFocusedWindowLocked(int i, int i2, long j, int i3, int i4) {
        if (i3 == 32) {
            synchronized (this.mLock) {
                if (!isTrackingWindowsLocked()) {
                    int findFocusedWindowId = findFocusedWindowId(i);
                    this.mTopFocusedWindowId = findFocusedWindowId;
                    if (i2 == findFocusedWindowId) {
                        this.mActiveWindowId = i2;
                    }
                }
            }
        } else if (i3 == 128) {
            synchronized (this.mLock) {
                if (this.mTouchInteractionInProgress && this.mActiveWindowId != i2) {
                    setActiveWindowLocked(i2);
                }
            }
        } else if (i3 == 32768) {
            synchronized (this.mLock) {
                if (this.mHasProxy && setProxyFocusLocked(i2)) {
                    return;
                }
                int i5 = this.mAccessibilityFocusedWindowId;
                if (i5 != i2) {
                    clearAccessibilityFocusLocked(i5);
                    setAccessibilityFocusedWindowLocked(i2);
                }
                this.mAccessibilityFocusNodeId = j;
            }
        } else if (i3 != 65536) {
        } else {
            synchronized (this.mLock) {
                if (this.mHasProxy && clearProxyFocusLocked(i2, i4)) {
                    return;
                }
                if (this.mAccessibilityFocusNodeId == j) {
                    this.mAccessibilityFocusNodeId = 2147483647L;
                }
                if (this.mAccessibilityFocusNodeId == 2147483647L && this.mAccessibilityFocusedWindowId == i2 && i4 != 64) {
                    this.mAccessibilityFocusedWindowId = -1;
                    this.mAccessibilityFocusedDisplayId = -1;
                }
            }
        }
    }

    public void onTouchInteractionStart() {
        synchronized (this.mLock) {
            this.mTouchInteractionInProgress = true;
        }
    }

    public void onTouchInteractionEnd() {
        synchronized (this.mLock) {
            this.mTouchInteractionInProgress = false;
            int i = this.mActiveWindowId;
            setActiveWindowLocked(this.mTopFocusedWindowId);
            if (i != this.mActiveWindowId && this.mAccessibilityFocusedWindowId == i && accessibilityFocusOnlyInActiveWindowLocked()) {
                clearAccessibilityFocusLocked(i);
            }
        }
    }

    public int getActiveWindowId(int i) {
        if (this.mActiveWindowId == -1 && !this.mTouchInteractionInProgress) {
            this.mActiveWindowId = findFocusedWindowId(i);
        }
        return this.mActiveWindowId;
    }

    public final void setActiveWindowLocked(int i) {
        DisplayWindowsObserver displayWindowObserverByWindowIdLocked;
        if (this.mActiveWindowId != i) {
            ArrayList<AccessibilityEvent> arrayList = new ArrayList(2);
            int i2 = this.mActiveWindowId;
            if (i2 != -1 && (displayWindowObserverByWindowIdLocked = getDisplayWindowObserverByWindowIdLocked(i2)) != null) {
                arrayList.add(AccessibilityEvent.obtainWindowsChangedEvent(displayWindowObserverByWindowIdLocked.mDisplayId, this.mActiveWindowId, 32));
            }
            this.mActiveWindowId = i;
            int size = this.mDisplayWindowsObservers.size();
            for (int i3 = 0; i3 < size; i3++) {
                DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i3);
                if (valueAt != null && valueAt.setActiveWindowLocked(i)) {
                    arrayList.add(AccessibilityEvent.obtainWindowsChangedEvent(valueAt.mDisplayId, i, 32));
                }
            }
            for (AccessibilityEvent accessibilityEvent : arrayList) {
                this.mAccessibilityEventSender.sendAccessibilityEventForCurrentUserLocked(accessibilityEvent);
            }
        }
    }

    public final void setAccessibilityFocusedWindowLocked(int i) {
        int i2;
        if (this.mAccessibilityFocusedWindowId != i) {
            ArrayList<AccessibilityEvent> arrayList = new ArrayList(2);
            int i3 = this.mAccessibilityFocusedDisplayId;
            if (i3 != -1 && (i2 = this.mAccessibilityFocusedWindowId) != -1) {
                arrayList.add(AccessibilityEvent.obtainWindowsChangedEvent(i3, i2, 128));
            }
            this.mAccessibilityFocusedWindowId = i;
            int size = this.mDisplayWindowsObservers.size();
            for (int i4 = 0; i4 < size; i4++) {
                DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i4);
                if (valueAt != null && valueAt.setAccessibilityFocusedWindowLocked(i)) {
                    this.mAccessibilityFocusedDisplayId = valueAt.mDisplayId;
                    arrayList.add(AccessibilityEvent.obtainWindowsChangedEvent(valueAt.mDisplayId, i, 128));
                }
            }
            for (AccessibilityEvent accessibilityEvent : arrayList) {
                this.mAccessibilityEventSender.sendAccessibilityEventForCurrentUserLocked(accessibilityEvent);
            }
        }
    }

    public AccessibilityWindowInfo findA11yWindowInfoByIdLocked(int i) {
        int resolveParentWindowIdLocked = resolveParentWindowIdLocked(i);
        DisplayWindowsObserver displayWindowObserverByWindowIdLocked = getDisplayWindowObserverByWindowIdLocked(resolveParentWindowIdLocked);
        if (displayWindowObserverByWindowIdLocked != null) {
            return displayWindowObserverByWindowIdLocked.findA11yWindowInfoByIdLocked(resolveParentWindowIdLocked);
        }
        return null;
    }

    public WindowInfo findWindowInfoByIdLocked(int i) {
        int resolveParentWindowIdLocked = resolveParentWindowIdLocked(i);
        DisplayWindowsObserver displayWindowObserverByWindowIdLocked = getDisplayWindowObserverByWindowIdLocked(resolveParentWindowIdLocked);
        if (displayWindowObserverByWindowIdLocked != null) {
            return displayWindowObserverByWindowIdLocked.findWindowInfoByIdLocked(resolveParentWindowIdLocked);
        }
        return null;
    }

    public int getFocusedWindowId(int i) {
        return getFocusedWindowId(i, -1);
    }

    public int getFocusedWindowId(int i, int i2) {
        if (i2 == -1 || i2 == 0 || !this.mHasProxy) {
            return getDefaultFocus(i);
        }
        DisplayWindowsObserver displayWindowsObserver = this.mDisplayWindowsObservers.get(i2);
        if (displayWindowsObserver != null && displayWindowsObserver.mIsProxy) {
            return getProxyFocus(i, displayWindowsObserver);
        }
        return getDefaultFocus(i);
    }

    public final int getDefaultFocus(int i) {
        if (i == 1) {
            return this.mTopFocusedWindowId;
        }
        if (i == 2) {
            return this.mAccessibilityFocusedWindowId;
        }
        return -1;
    }

    public final int getProxyFocus(int i, DisplayWindowsObserver displayWindowsObserver) {
        if (i == 1) {
            return this.mTopFocusedWindowId;
        }
        if (i == 2) {
            return displayWindowsObserver.mProxyDisplayAccessibilityFocusedWindow;
        }
        return -1;
    }

    public AccessibilityWindowInfo getPictureInPictureWindowLocked() {
        int size = this.mDisplayWindowsObservers.size();
        AccessibilityWindowInfo accessibilityWindowInfo = null;
        for (int i = 0; i < size; i++) {
            DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i);
            if (valueAt != null && (accessibilityWindowInfo = valueAt.getPictureInPictureWindowLocked()) != null) {
                break;
            }
        }
        return accessibilityWindowInfo;
    }

    public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection iAccessibilityInteractionConnection) throws RemoteException {
        synchronized (this.mLock) {
            RemoteAccessibilityConnection remoteAccessibilityConnection = this.mPictureInPictureActionReplacingConnection;
            if (remoteAccessibilityConnection != null) {
                remoteAccessibilityConnection.unlinkToDeath();
                this.mPictureInPictureActionReplacingConnection = null;
            }
            if (iAccessibilityInteractionConnection != null) {
                RemoteAccessibilityConnection remoteAccessibilityConnection2 = new RemoteAccessibilityConnection(-3, iAccessibilityInteractionConnection, "foo.bar.baz", 1000, -1);
                this.mPictureInPictureActionReplacingConnection = remoteAccessibilityConnection2;
                remoteAccessibilityConnection2.linkToDeath();
            }
        }
    }

    public RemoteAccessibilityConnection getPictureInPictureActionReplacingConnection() {
        return this.mPictureInPictureActionReplacingConnection;
    }

    public void notifyOutsideTouch(int i, int i2) {
        int i3;
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            DisplayWindowsObserver displayWindowObserverByWindowIdLocked = getDisplayWindowObserverByWindowIdLocked(i2);
            if (displayWindowObserverByWindowIdLocked != null) {
                List<Integer> watchOutsideTouchWindowIdLocked = displayWindowObserverByWindowIdLocked.getWatchOutsideTouchWindowIdLocked(i2);
                for (int i4 = 0; i4 < watchOutsideTouchWindowIdLocked.size(); i4++) {
                    arrayList.add(getConnectionLocked(i, watchOutsideTouchWindowIdLocked.get(i4).intValue()));
                }
            }
        }
        for (i3 = 0; i3 < arrayList.size(); i3++) {
            RemoteAccessibilityConnection remoteAccessibilityConnection = (RemoteAccessibilityConnection) arrayList.get(i3);
            if (remoteAccessibilityConnection != null) {
                if (traceIntConnEnabled()) {
                    logTraceIntConn("notifyOutsideTouch");
                }
                try {
                    remoteAccessibilityConnection.getRemote().notifyOutsideTouch();
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public int getDisplayIdByUserIdAndWindowIdLocked(int i, int i2) {
        IBinder windowTokenForUserAndWindowIdLocked = getWindowTokenForUserAndWindowIdLocked(i, i2);
        if (traceWMEnabled()) {
            logTraceWM("getDisplayIdForWindow", "token=" + windowTokenForUserAndWindowIdLocked);
        }
        return this.mWindowManagerInternal.getDisplayIdForWindow(windowTokenForUserAndWindowIdLocked);
    }

    public ArrayList<Integer> getDisplayListLocked(int i) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        int size = this.mDisplayWindowsObservers.size();
        for (int i2 = 0; i2 < size; i2++) {
            DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i2);
            if (valueAt != null) {
                if (!valueAt.mIsProxy && (i & 1) != 0) {
                    arrayList.add(Integer.valueOf(valueAt.mDisplayId));
                } else if (valueAt.mIsProxy && (i & 2) != 0) {
                    arrayList.add(Integer.valueOf(valueAt.mDisplayId));
                }
            }
        }
        return arrayList;
    }

    public boolean accessibilityFocusOnlyInActiveWindowLocked() {
        return !isTrackingWindowsLocked();
    }

    public final int findFocusedWindowId(int i) {
        int findWindowIdLocked;
        if (traceWMEnabled()) {
            logTraceWM("getFocusedWindowToken", "");
        }
        IBinder focusedWindowTokenFromWindowStates = this.mWindowManagerInternal.getFocusedWindowTokenFromWindowStates();
        synchronized (this.mLock) {
            findWindowIdLocked = findWindowIdLocked(i, focusedWindowTokenFromWindowStates);
        }
        return findWindowIdLocked;
    }

    public final boolean isValidUserForInteractionConnectionsLocked(int i) {
        return this.mInteractionConnections.indexOfKey(i) >= 0;
    }

    public final boolean isValidUserForWindowTokensLocked(int i) {
        return this.mWindowTokens.indexOfKey(i) >= 0;
    }

    public final SparseArray<RemoteAccessibilityConnection> getInteractionConnectionsForUserLocked(int i) {
        SparseArray<RemoteAccessibilityConnection> sparseArray = this.mInteractionConnections.get(i);
        if (sparseArray == null) {
            SparseArray<RemoteAccessibilityConnection> sparseArray2 = new SparseArray<>();
            this.mInteractionConnections.put(i, sparseArray2);
            return sparseArray2;
        }
        return sparseArray;
    }

    public final SparseArray<IBinder> getWindowTokensForUserLocked(int i) {
        SparseArray<IBinder> sparseArray = this.mWindowTokens.get(i);
        if (sparseArray == null) {
            SparseArray<IBinder> sparseArray2 = new SparseArray<>();
            this.mWindowTokens.put(i, sparseArray2);
            return sparseArray2;
        }
        return sparseArray;
    }

    public final void clearAccessibilityFocusLocked(int i) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityWindowManager$$ExternalSyntheticLambda0
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityWindowManager) obj).clearAccessibilityFocusMainThread(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
            }
        }, this, Integer.valueOf(this.mAccessibilityUserManager.getCurrentUserIdLocked()), Integer.valueOf(i)));
    }

    public final void clearAccessibilityFocusMainThread(int i, int i2) {
        synchronized (this.mLock) {
            RemoteAccessibilityConnection connectionLocked = getConnectionLocked(i, i2);
            if (connectionLocked == null) {
                return;
            }
            if (traceIntConnEnabled()) {
                logTraceIntConn("notifyOutsideTouch");
            }
            try {
                connectionLocked.getRemote().clearAccessibilityFocus();
            } catch (RemoteException unused) {
            }
        }
    }

    public final DisplayWindowsObserver getDisplayWindowObserverByWindowIdLocked(int i) {
        int size = this.mDisplayWindowsObservers.size();
        for (int i2 = 0; i2 < size; i2++) {
            DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i2);
            if (valueAt != null && valueAt.findWindowInfoByIdLocked(i) != null) {
                return this.mDisplayWindowsObservers.get(valueAt.mDisplayId);
            }
        }
        return null;
    }

    public final boolean traceWMEnabled() {
        return this.mTraceManager.isA11yTracingEnabledForTypes(512L);
    }

    public final void logTraceWM(String str, String str2) {
        AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
        accessibilityTraceManager.logTrace("WindowManagerInternal." + str, 512L, str2);
    }

    public final boolean traceIntConnEnabled() {
        return this.mTraceManager.isA11yTracingEnabledForTypes(16L);
    }

    public final void logTraceIntConn(String str) {
        AccessibilityTraceManager accessibilityTraceManager = this.mTraceManager;
        accessibilityTraceManager.logTrace("AccessibilityWindowManager." + str, 16L);
    }

    public void associateLocked(IBinder iBinder, IBinder iBinder2) {
        this.mHostEmbeddedMap.put(iBinder, iBinder2);
    }

    public void disassociateLocked(IBinder iBinder) {
        this.mHostEmbeddedMap.remove(iBinder);
        for (int size = this.mHostEmbeddedMap.size() - 1; size >= 0; size--) {
            if (this.mHostEmbeddedMap.valueAt(size).equals(iBinder)) {
                this.mHostEmbeddedMap.removeAt(size);
            }
        }
    }

    public void registerIdLocked(IBinder iBinder, int i) {
        this.mWindowIdMap.put(i, iBinder);
    }

    public void unregisterIdLocked(int i) {
        IBinder iBinder = this.mWindowIdMap.get(i);
        if (iBinder == null) {
            return;
        }
        disassociateLocked(iBinder);
        this.mWindowIdMap.remove(i);
    }

    public IBinder getTokenLocked(int i) {
        return this.mWindowIdMap.get(i);
    }

    public int getWindowIdLocked(IBinder iBinder) {
        int indexOfValue = this.mWindowIdMap.indexOfValue(iBinder);
        return indexOfValue == -1 ? indexOfValue : this.mWindowIdMap.keyAt(indexOfValue);
    }

    public IBinder getHostTokenLocked(IBinder iBinder) {
        return this.mHostEmbeddedMap.get(iBinder);
    }

    public final boolean clearProxyFocusLocked(int i, int i2) {
        if (i2 == 64) {
            return false;
        }
        for (int i3 = 0; i3 < this.mDisplayWindowsObservers.size(); i3++) {
            DisplayWindowsObserver displayWindowsObserver = this.mDisplayWindowsObservers.get(i3);
            if (displayWindowsObserver != null && displayWindowsObserver.mWindows != null && displayWindowsObserver.mIsProxy) {
                int size = displayWindowsObserver.mWindows.size();
                for (int i4 = 0; i4 < size; i4++) {
                    if (((AccessibilityWindowInfo) displayWindowsObserver.mWindows.get(i4)).getId() == i) {
                        displayWindowsObserver.mProxyDisplayAccessibilityFocusedWindow = -1;
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    public final boolean setProxyFocusLocked(int i) {
        for (int i2 = 0; i2 < this.mDisplayWindowsObservers.size(); i2++) {
            DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i2);
            if (valueAt != null && valueAt.mIsProxy && valueAt.setAccessibilityFocusedWindowLocked(i)) {
                int i3 = valueAt.mProxyDisplayAccessibilityFocusedWindow;
                if (i3 == i) {
                    return true;
                }
                if (i3 != -1) {
                    clearAccessibilityFocusLocked(i3);
                    this.mAccessibilityEventSender.sendAccessibilityEventForCurrentUserLocked(AccessibilityEvent.obtainWindowsChangedEvent(valueAt.mDisplayId, i3, 128));
                }
                valueAt.mProxyDisplayAccessibilityFocusedWindow = i;
                this.mAccessibilityEventSender.sendAccessibilityEventForCurrentUserLocked(AccessibilityEvent.obtainWindowsChangedEvent(valueAt.mDisplayId, valueAt.mProxyDisplayAccessibilityFocusedWindow, 128));
                return true;
            }
        }
        return false;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int size = this.mDisplayWindowsObservers.size();
        for (int i = 0; i < size; i++) {
            DisplayWindowsObserver valueAt = this.mDisplayWindowsObservers.valueAt(i);
            if (valueAt != null) {
                valueAt.dumpLocked(fileDescriptor, printWriter, strArr);
            }
        }
        printWriter.println();
        printWriter.append("Window attributes:[");
        printWriter.append((CharSequence) this.mWindowAttributes.toString());
        printWriter.append("]");
        printWriter.println();
    }
}
