package android.service.selectiontoolbar;

import android.p008os.IBinder;
import android.service.selectiontoolbar.SelectionToolbarRenderService;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.selectiontoolbar.ShowInfo;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.UUID;
/* loaded from: classes3.dex */
public final class DefaultSelectionToolbarRenderService extends SelectionToolbarRenderService {
    private static final String TAG = "DefaultSelectionToolbarRenderService";
    private final SparseArray<Pair<Long, RemoteSelectionToolbar>> mToolbarCache = new SparseArray<>();

    private boolean canShowToolbar(int uid, ShowInfo showInfo) {
        return showInfo.getWidgetToken() != 0 || this.mToolbarCache.indexOfKey(uid) < 0;
    }

    @Override // android.service.selectiontoolbar.SelectionToolbarRenderService
    public void onShow(int callingUid, ShowInfo showInfo, SelectionToolbarRenderService.RemoteCallbackWrapper callbackWrapper) {
        long widgetToken;
        if (!canShowToolbar(callingUid, showInfo)) {
            Slog.m96e(TAG, "Do not allow multiple toolbar for the app.");
            callbackWrapper.onError(1);
            return;
        }
        if (showInfo.getWidgetToken() == 0) {
            widgetToken = UUID.randomUUID().getMostSignificantBits();
        } else {
            widgetToken = showInfo.getWidgetToken();
        }
        if (this.mToolbarCache.indexOfKey(callingUid) < 0) {
            RemoteSelectionToolbar toolbar = new RemoteSelectionToolbar(this, widgetToken, showInfo, callbackWrapper, new SelectionToolbarRenderService.TransferTouchListener() { // from class: android.service.selectiontoolbar.DefaultSelectionToolbarRenderService$$ExternalSyntheticLambda0
                @Override // android.service.selectiontoolbar.SelectionToolbarRenderService.TransferTouchListener
                public final void onTransferTouch(IBinder iBinder, IBinder iBinder2) {
                    DefaultSelectionToolbarRenderService.this.transferTouch(iBinder, iBinder2);
                }
            });
            this.mToolbarCache.put(callingUid, new Pair<>(Long.valueOf(widgetToken), toolbar));
        }
        Slog.m92v(TAG, "onShow() for " + widgetToken);
        Pair<Long, RemoteSelectionToolbar> toolbarPair = this.mToolbarCache.get(callingUid);
        if (toolbarPair.first.longValue() == widgetToken) {
            toolbarPair.second.show(showInfo);
        } else {
            Slog.m90w(TAG, "onShow() for unknown " + widgetToken);
        }
    }

    @Override // android.service.selectiontoolbar.SelectionToolbarRenderService
    public void onHide(long widgetToken) {
        RemoteSelectionToolbar toolbar = getRemoteSelectionToolbarByTokenLocked(widgetToken);
        if (toolbar != null) {
            Slog.m92v(TAG, "onHide() for " + widgetToken);
            toolbar.hide(widgetToken);
        }
    }

    @Override // android.service.selectiontoolbar.SelectionToolbarRenderService
    public void onDismiss(long widgetToken) {
        RemoteSelectionToolbar toolbar = getRemoteSelectionToolbarByTokenLocked(widgetToken);
        if (toolbar != null) {
            Slog.m92v(TAG, "onDismiss() for " + widgetToken);
            toolbar.dismiss(widgetToken);
            removeRemoteSelectionToolbarByTokenLocked(widgetToken);
        }
    }

    @Override // android.service.selectiontoolbar.SelectionToolbarRenderService
    public void onToolbarShowTimeout(int callingUid) {
        Slog.m90w(TAG, "onToolbarShowTimeout for callingUid = " + callingUid);
        Pair<Long, RemoteSelectionToolbar> toolbarPair = this.mToolbarCache.get(callingUid);
        if (toolbarPair != null) {
            RemoteSelectionToolbar remoteToolbar = toolbarPair.second;
            remoteToolbar.dismiss(toolbarPair.first.longValue());
            remoteToolbar.onToolbarShowTimeout();
            this.mToolbarCache.remove(callingUid);
        }
    }

    private RemoteSelectionToolbar getRemoteSelectionToolbarByTokenLocked(long widgetToken) {
        for (int i = 0; i < this.mToolbarCache.size(); i++) {
            Pair<Long, RemoteSelectionToolbar> toolbarPair = this.mToolbarCache.valueAt(i);
            if (toolbarPair.first.longValue() == widgetToken) {
                return toolbarPair.second;
            }
        }
        return null;
    }

    private void removeRemoteSelectionToolbarByTokenLocked(long widgetToken) {
        for (int i = 0; i < this.mToolbarCache.size(); i++) {
            Pair<Long, RemoteSelectionToolbar> toolbarPair = this.mToolbarCache.valueAt(i);
            if (toolbarPair.first.longValue() == widgetToken) {
                SparseArray<Pair<Long, RemoteSelectionToolbar>> sparseArray = this.mToolbarCache;
                sparseArray.remove(sparseArray.keyAt(i));
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        int size = this.mToolbarCache.size();
        pw.print("number selectionToolbar: ");
        pw.println(size);
        for (int i = 0; i < size; i++) {
            pw.print("#");
            pw.println(i);
            int callingUid = this.mToolbarCache.keyAt(i);
            pw.print("  ");
            pw.print("callingUid: ");
            pw.println(callingUid);
            Pair<Long, RemoteSelectionToolbar> toolbarPair = this.mToolbarCache.valueAt(i);
            RemoteSelectionToolbar selectionToolbar = toolbarPair.second;
            pw.print("  ");
            pw.print("selectionToolbar: ");
            selectionToolbar.dump("  ", pw);
            pw.println();
        }
    }
}
