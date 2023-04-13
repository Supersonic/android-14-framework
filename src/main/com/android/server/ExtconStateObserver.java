package com.android.server;

import android.os.FileUtils;
import android.os.UEventObserver;
import com.android.server.ExtconUEventObserver;
import java.io.File;
import java.io.IOException;
/* loaded from: classes.dex */
public abstract class ExtconStateObserver<S> extends ExtconUEventObserver {
    public abstract S parseState(ExtconUEventObserver.ExtconInfo extconInfo, String str);

    public abstract void updateState(ExtconUEventObserver.ExtconInfo extconInfo, String str, S s);

    public S parseStateFromFile(ExtconUEventObserver.ExtconInfo extconInfo) throws IOException {
        return parseState(extconInfo, FileUtils.readTextFile(new File(extconInfo.getStatePath()), 0, null).trim());
    }

    @Override // com.android.server.ExtconUEventObserver
    public void onUEvent(ExtconUEventObserver.ExtconInfo extconInfo, UEventObserver.UEvent uEvent) {
        String str = uEvent.get("NAME");
        S parseState = parseState(extconInfo, uEvent.get("STATE"));
        if (parseState != null) {
            updateState(extconInfo, str, parseState);
        }
    }
}
