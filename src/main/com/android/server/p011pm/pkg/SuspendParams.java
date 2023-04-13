package com.android.server.p011pm.pkg;

import android.content.pm.SuspendDialogInfo;
import android.os.BaseBundle;
import android.os.PersistableBundle;
import android.util.Slog;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.SuspendParams */
/* loaded from: classes2.dex */
public final class SuspendParams {
    public final PersistableBundle appExtras;
    public final SuspendDialogInfo dialogInfo;
    public final PersistableBundle launcherExtras;

    public SuspendParams(SuspendDialogInfo suspendDialogInfo, PersistableBundle persistableBundle, PersistableBundle persistableBundle2) {
        this.dialogInfo = suspendDialogInfo;
        this.appExtras = persistableBundle;
        this.launcherExtras = persistableBundle2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SuspendParams) {
            SuspendParams suspendParams = (SuspendParams) obj;
            return Objects.equals(this.dialogInfo, suspendParams.dialogInfo) && BaseBundle.kindofEquals(this.appExtras, suspendParams.appExtras) && BaseBundle.kindofEquals(this.launcherExtras, suspendParams.launcherExtras);
        }
        return false;
    }

    public int hashCode() {
        int hashCode = Objects.hashCode(this.dialogInfo) * 31;
        PersistableBundle persistableBundle = this.appExtras;
        int size = (hashCode + (persistableBundle != null ? persistableBundle.size() : 0)) * 31;
        PersistableBundle persistableBundle2 = this.launcherExtras;
        return size + (persistableBundle2 != null ? persistableBundle2.size() : 0);
    }

    public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        if (this.dialogInfo != null) {
            typedXmlSerializer.startTag((String) null, "dialog-info");
            this.dialogInfo.saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "dialog-info");
        }
        if (this.appExtras != null) {
            typedXmlSerializer.startTag((String) null, "app-extras");
            try {
                this.appExtras.saveToXml(typedXmlSerializer);
            } catch (XmlPullParserException e) {
                Slog.e("FrameworkPackageUserState", "Exception while trying to write appExtras. Will be lost on reboot", e);
            }
            typedXmlSerializer.endTag((String) null, "app-extras");
        }
        if (this.launcherExtras != null) {
            typedXmlSerializer.startTag((String) null, "launcher-extras");
            try {
                this.launcherExtras.saveToXml(typedXmlSerializer);
            } catch (XmlPullParserException e2) {
                Slog.e("FrameworkPackageUserState", "Exception while trying to write launcherExtras. Will be lost on reboot", e2);
            }
            typedXmlSerializer.endTag((String) null, "launcher-extras");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x0085 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0059 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static SuspendParams restoreFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException {
        char c;
        int depth = typedXmlPullParser.getDepth();
        SuspendDialogInfo suspendDialogInfo = null;
        PersistableBundle persistableBundle = null;
        PersistableBundle persistableBundle2 = null;
        while (true) {
            try {
                int next = typedXmlPullParser.next();
                if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                    break;
                } else if (next != 3 && next != 4) {
                    String name = typedXmlPullParser.getName();
                    int hashCode = name.hashCode();
                    if (hashCode == -538220657) {
                        if (name.equals("app-extras")) {
                            c = 1;
                            if (c != 0) {
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    } else if (hashCode != -22768109) {
                        if (hashCode == 1627485488 && name.equals("launcher-extras")) {
                            c = 2;
                            if (c != 0) {
                                suspendDialogInfo = SuspendDialogInfo.restoreFromXml(typedXmlPullParser);
                            } else if (c == 1) {
                                persistableBundle = PersistableBundle.restoreFromXml(typedXmlPullParser);
                            } else if (c == 2) {
                                persistableBundle2 = PersistableBundle.restoreFromXml(typedXmlPullParser);
                            } else {
                                Slog.w("FrameworkPackageUserState", "Unknown tag " + typedXmlPullParser.getName() + " in SuspendParams. Ignoring");
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    } else {
                        if (name.equals("dialog-info")) {
                            c = 0;
                            if (c != 0) {
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    }
                }
            } catch (XmlPullParserException e) {
                Slog.e("FrameworkPackageUserState", "Exception while trying to parse SuspendParams, some fields may default", e);
            }
        }
        return new SuspendParams(suspendDialogInfo, persistableBundle, persistableBundle2);
    }

    public SuspendDialogInfo getDialogInfo() {
        return this.dialogInfo;
    }

    public PersistableBundle getAppExtras() {
        return this.appExtras;
    }

    public PersistableBundle getLauncherExtras() {
        return this.launcherExtras;
    }
}
