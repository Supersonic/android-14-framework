package android.inputmethodservice;

import android.content.Context;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
interface InputMethodServiceInternal {
    Context getContext();

    default void exposeContent(InputContentInfo inputContentInfo, InputConnection inputConnection) {
    }

    default void notifyUserActionIfNecessary() {
    }

    default void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
    }

    default void triggerServiceDump(String where, byte[] icProto) {
    }

    default boolean isServiceDestroyed() {
        return false;
    }
}
