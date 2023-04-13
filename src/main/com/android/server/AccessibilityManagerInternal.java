package com.android.server;

import android.util.ArraySet;
import android.util.SparseArray;
import android.view.inputmethod.EditorInfo;
import com.android.internal.inputmethod.IAccessibilityInputMethodSession;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
/* loaded from: classes.dex */
public abstract class AccessibilityManagerInternal {
    public static final AccessibilityManagerInternal NOP = new AccessibilityManagerInternal() { // from class: com.android.server.AccessibilityManagerInternal.1
        @Override // com.android.server.AccessibilityManagerInternal
        public void bindInput() {
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void createImeSession(ArraySet<Integer> arraySet) {
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public boolean isTouchExplorationEnabled(int i) {
            return false;
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void performSystemAction(int i) {
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void setImeSessionEnabled(SparseArray<IAccessibilityInputMethodSession> sparseArray, boolean z) {
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void startInput(IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, EditorInfo editorInfo, boolean z) {
        }

        @Override // com.android.server.AccessibilityManagerInternal
        public void unbindInput() {
        }
    };

    public abstract void bindInput();

    public abstract void createImeSession(ArraySet<Integer> arraySet);

    public abstract boolean isTouchExplorationEnabled(int i);

    public abstract void performSystemAction(int i);

    public abstract void setImeSessionEnabled(SparseArray<IAccessibilityInputMethodSession> sparseArray, boolean z);

    public abstract void startInput(IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, EditorInfo editorInfo, boolean z);

    public abstract void unbindInput();

    public static AccessibilityManagerInternal get() {
        AccessibilityManagerInternal accessibilityManagerInternal = (AccessibilityManagerInternal) LocalServices.getService(AccessibilityManagerInternal.class);
        return accessibilityManagerInternal != null ? accessibilityManagerInternal : NOP;
    }
}
