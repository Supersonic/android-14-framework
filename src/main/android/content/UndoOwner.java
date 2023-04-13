package android.content;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
/* loaded from: classes.dex */
public class UndoOwner {
    Object mData;
    final UndoManager mManager;
    int mOpCount;
    int mSavedIdx;
    int mStateSeq;
    final String mTag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UndoOwner(String tag, UndoManager manager) {
        if (tag == null) {
            throw new NullPointerException("tag can't be null");
        }
        if (manager == null) {
            throw new NullPointerException("manager can't be null");
        }
        this.mTag = tag;
        this.mManager = manager;
    }

    public String getTag() {
        return this.mTag;
    }

    public Object getData() {
        return this.mData;
    }

    public String toString() {
        return "UndoOwner:[mTag=" + this.mTag + " mManager=" + this.mManager + " mData=" + this.mData + " mData=" + this.mData + " mOpCount=" + this.mOpCount + " mStateSeq=" + this.mStateSeq + " mSavedIdx=" + this.mSavedIdx + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
