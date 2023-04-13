package android.view.contentcapture;

import android.app.compat.CompatChanges;
import android.graphics.Insets;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.DebugUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.autofill.AutofillId;
import android.view.contentcapture.ViewNode;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public abstract class ContentCaptureSession implements AutoCloseable {
    public static final int FLUSH_REASON_FORCE_FLUSH = 8;
    public static final int FLUSH_REASON_FULL = 1;
    public static final int FLUSH_REASON_IDLE_TIMEOUT = 5;
    public static final int FLUSH_REASON_SESSION_CONNECTED = 7;
    public static final int FLUSH_REASON_SESSION_FINISHED = 4;
    public static final int FLUSH_REASON_SESSION_STARTED = 3;
    public static final int FLUSH_REASON_TEXT_CHANGE_TIMEOUT = 6;
    public static final int FLUSH_REASON_VIEW_ROOT_ENTERED = 2;
    public static final int FLUSH_REASON_VIEW_TREE_APPEARED = 10;
    public static final int FLUSH_REASON_VIEW_TREE_APPEARING = 9;
    private static final int INITIAL_CHILDREN_CAPACITY = 5;
    static final long NOTIFY_NODES_DISAPPEAR_NOW_SENDS_TREE_EVENTS = 258825825;
    public static final int STATE_ACTIVE = 2;
    public static final int STATE_BY_APP = 64;
    public static final int STATE_DISABLED = 4;
    public static final int STATE_DUPLICATED_ID = 8;
    public static final int STATE_FLAG_SECURE = 32;
    public static final int STATE_INTERNAL_ERROR = 256;
    public static final int STATE_NOT_WHITELISTED = 512;
    public static final int STATE_NO_RESPONSE = 128;
    public static final int STATE_NO_SERVICE = 16;
    public static final int STATE_SERVICE_DIED = 1024;
    public static final int STATE_SERVICE_RESURRECTED = 4096;
    public static final int STATE_SERVICE_UPDATING = 2048;
    public static final int STATE_WAITING_FOR_SERVER = 1;
    public static final int UNKNOWN_STATE = 0;
    private ArrayList<ContentCaptureSession> mChildren;
    private ContentCaptureContext mClientContext;
    private ContentCaptureSessionId mContentCaptureSessionId;
    private boolean mDestroyed;
    protected final int mId;
    private final Object mLock;
    private int mState;
    private static final String TAG = ContentCaptureSession.class.getSimpleName();
    private static final SecureRandom ID_GENERATOR = new SecureRandom();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface FlushReason {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void flush(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract MainContentCaptureSession getMainCaptureSession();

    abstract void internalNotifySessionPaused();

    abstract void internalNotifySessionResumed();

    abstract void internalNotifyViewAppeared(ViewNode.ViewStructureImpl viewStructureImpl);

    abstract void internalNotifyViewDisappeared(AutofillId autofillId);

    abstract void internalNotifyViewInsetsChanged(Insets insets);

    abstract void internalNotifyViewTextChanged(AutofillId autofillId, CharSequence charSequence);

    public abstract void internalNotifyViewTreeEvent(boolean z);

    abstract ContentCaptureSession newChild(ContentCaptureContext contentCaptureContext);

    abstract void onDestroy();

    abstract void updateContentCaptureContext(ContentCaptureContext contentCaptureContext);

    /* JADX INFO: Access modifiers changed from: protected */
    public ContentCaptureSession() {
        this(getRandomSessionId());
    }

    public ContentCaptureSession(int id) {
        this.mLock = new Object();
        this.mState = 0;
        Preconditions.checkArgument(id != 0);
        this.mId = id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContentCaptureSession(ContentCaptureContext initialContext) {
        this();
        this.mClientContext = (ContentCaptureContext) Objects.requireNonNull(initialContext);
    }

    public final ContentCaptureSessionId getContentCaptureSessionId() {
        if (this.mContentCaptureSessionId == null) {
            this.mContentCaptureSessionId = new ContentCaptureSessionId(this.mId);
        }
        return this.mContentCaptureSessionId;
    }

    public int getId() {
        return this.mId;
    }

    public final ContentCaptureSession createContentCaptureSession(ContentCaptureContext context) {
        ContentCaptureSession child = newChild(context);
        if (ContentCaptureHelper.sDebug) {
            Log.m112d(TAG, "createContentCaptureSession(" + context + ": parent=" + this.mId + ", child=" + child.mId);
        }
        synchronized (this.mLock) {
            if (this.mChildren == null) {
                this.mChildren = new ArrayList<>(5);
            }
            this.mChildren.add(child);
        }
        return child;
    }

    public final void setContentCaptureContext(ContentCaptureContext context) {
        if (isContentCaptureEnabled()) {
            this.mClientContext = context;
            updateContentCaptureContext(context);
        }
    }

    public final ContentCaptureContext getContentCaptureContext() {
        return this.mClientContext;
    }

    public final void destroy() {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                if (ContentCaptureHelper.sDebug) {
                    Log.m112d(TAG, "destroy(" + this.mId + "): already destroyed");
                }
                return;
            }
            this.mDestroyed = true;
            if (ContentCaptureHelper.sVerbose) {
                Log.m106v(TAG, "destroy(): state=" + getStateAsString(this.mState) + ", mId=" + this.mId);
            }
            ArrayList<ContentCaptureSession> arrayList = this.mChildren;
            if (arrayList != null) {
                int numberChildren = arrayList.size();
                if (ContentCaptureHelper.sVerbose) {
                    Log.m106v(TAG, "Destroying " + numberChildren + " children first");
                }
                for (int i = 0; i < numberChildren; i++) {
                    ContentCaptureSession child = this.mChildren.get(i);
                    try {
                        child.destroy();
                    } catch (Exception e) {
                        Log.m104w(TAG, "exception destroying child session #" + i + ": " + e);
                    }
                }
            }
            onDestroy();
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        destroy();
    }

    public final void notifyViewAppeared(ViewStructure node) {
        Objects.requireNonNull(node);
        if (isContentCaptureEnabled()) {
            if (!(node instanceof ViewNode.ViewStructureImpl)) {
                throw new IllegalArgumentException("Invalid node class: " + node.getClass());
            }
            internalNotifyViewAppeared((ViewNode.ViewStructureImpl) node);
        }
    }

    public final void notifyViewDisappeared(AutofillId id) {
        Objects.requireNonNull(id);
        if (isContentCaptureEnabled()) {
            internalNotifyViewDisappeared(id);
        }
    }

    public final void notifyViewsAppeared(List<ViewStructure> appearedNodes) {
        Preconditions.checkCollectionElementsNotNull(appearedNodes, "appearedNodes");
        if (isContentCaptureEnabled()) {
            for (int i = 0; i < appearedNodes.size(); i++) {
                ViewStructure v = appearedNodes.get(i);
                if (!(v instanceof ViewNode.ViewStructureImpl)) {
                    throw new IllegalArgumentException("Invalid class: " + v.getClass());
                }
            }
            internalNotifyViewTreeEvent(true);
            for (int i2 = 0; i2 < appearedNodes.size(); i2++) {
                internalNotifyViewAppeared((ViewNode.ViewStructureImpl) appearedNodes.get(i2));
            }
            internalNotifyViewTreeEvent(false);
        }
    }

    public final void notifyViewsDisappeared(AutofillId hostId, long[] virtualIds) {
        Preconditions.checkArgument(hostId.isNonVirtual(), "hostId cannot be virtual: %s", hostId);
        Preconditions.checkArgument(!ArrayUtils.isEmpty(virtualIds), "virtual ids cannot be empty");
        if (isContentCaptureEnabled()) {
            if (CompatChanges.isChangeEnabled(NOTIFY_NODES_DISAPPEAR_NOW_SENDS_TREE_EVENTS)) {
                internalNotifyViewTreeEvent(true);
            }
            for (long id : virtualIds) {
                internalNotifyViewDisappeared(new AutofillId(hostId, id, this.mId));
            }
            if (CompatChanges.isChangeEnabled(NOTIFY_NODES_DISAPPEAR_NOW_SENDS_TREE_EVENTS)) {
                internalNotifyViewTreeEvent(false);
            }
        }
    }

    public final void notifyViewTextChanged(AutofillId id, CharSequence text) {
        Objects.requireNonNull(id);
        if (isContentCaptureEnabled()) {
            internalNotifyViewTextChanged(id, text);
        }
    }

    public final void notifyViewInsetsChanged(Insets viewInsets) {
        Objects.requireNonNull(viewInsets);
        if (isContentCaptureEnabled()) {
            internalNotifyViewInsetsChanged(viewInsets);
        }
    }

    public final void notifySessionResumed() {
        if (isContentCaptureEnabled()) {
            internalNotifySessionResumed();
        }
    }

    public final void notifySessionPaused() {
        if (isContentCaptureEnabled()) {
            internalNotifySessionPaused();
        }
    }

    public final ViewStructure newViewStructure(View view) {
        return new ViewNode.ViewStructureImpl(view);
    }

    public AutofillId newAutofillId(AutofillId hostId, long virtualChildId) {
        Objects.requireNonNull(hostId);
        Preconditions.checkArgument(hostId.isNonVirtual(), "hostId cannot be virtual: %s", hostId);
        return new AutofillId(hostId, virtualChildId, this.mId);
    }

    public final ViewStructure newVirtualViewStructure(AutofillId parentId, long virtualId) {
        return new ViewNode.ViewStructureImpl(parentId, virtualId, this.mId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isContentCaptureEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = !this.mDestroyed;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("id: ");
        pw.println(this.mId);
        if (this.mClientContext != null) {
            pw.print(prefix);
            this.mClientContext.dump(pw);
            pw.println();
        }
        synchronized (this.mLock) {
            pw.print(prefix);
            pw.print("destroyed: ");
            pw.println(this.mDestroyed);
            ArrayList<ContentCaptureSession> arrayList = this.mChildren;
            if (arrayList != null && !arrayList.isEmpty()) {
                String prefix2 = prefix + "  ";
                int numberChildren = this.mChildren.size();
                pw.print(prefix);
                pw.print("number children: ");
                pw.println(numberChildren);
                for (int i = 0; i < numberChildren; i++) {
                    ContentCaptureSession child = this.mChildren.get(i);
                    pw.print(prefix);
                    pw.print(i);
                    pw.println(": ");
                    child.dump(prefix2, pw);
                }
            }
        }
    }

    public String toString() {
        return Integer.toString(this.mId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getStateAsString(int state) {
        return state + " (" + (state == 0 ? "UNKNOWN" : DebugUtils.flagsToString(ContentCaptureSession.class, "STATE_", state)) + NavigationBarInflaterView.KEY_CODE_END;
    }

    public static String getFlushReasonAsString(int reason) {
        switch (reason) {
            case 1:
                return "FULL";
            case 2:
                return "VIEW_ROOT";
            case 3:
                return "STARTED";
            case 4:
                return "FINISHED";
            case 5:
                return "IDLE";
            case 6:
                return "TEXT_CHANGE";
            case 7:
                return "CONNECTED";
            case 8:
                return "FORCE_FLUSH";
            case 9:
                return "VIEW_TREE_APPEARING";
            case 10:
                return "VIEW_TREE_APPEARED";
            default:
                return "UNKOWN-" + reason;
        }
    }

    private static int getRandomSessionId() {
        int id;
        do {
            id = ID_GENERATOR.nextInt();
        } while (id == 0);
        return id;
    }
}
