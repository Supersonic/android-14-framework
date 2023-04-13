package com.android.server.policy;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public final class GlobalKeyManager {
    public final SparseArray<GlobalKeyAction> mKeyMapping = new SparseArray<>();
    public boolean mBeganFromNonInteractive = false;

    public GlobalKeyManager(Context context) {
        loadGlobalKeys(context);
    }

    public boolean handleGlobalKey(Context context, int i, KeyEvent keyEvent) {
        GlobalKeyAction globalKeyAction;
        if (this.mKeyMapping.size() <= 0 || (globalKeyAction = this.mKeyMapping.get(i)) == null) {
            return false;
        }
        context.sendBroadcastAsUser(new GlobalKeyIntent(globalKeyAction.mComponentName, keyEvent, this.mBeganFromNonInteractive).getIntent(), UserHandle.CURRENT, null);
        if (keyEvent.getAction() == 1) {
            this.mBeganFromNonInteractive = false;
        }
        return true;
    }

    public boolean shouldHandleGlobalKey(int i) {
        return this.mKeyMapping.get(i) != null;
    }

    public boolean shouldDispatchFromNonInteractive(int i) {
        GlobalKeyAction globalKeyAction = this.mKeyMapping.get(i);
        if (globalKeyAction == null) {
            return false;
        }
        return globalKeyAction.mDispatchWhenNonInteractive;
    }

    public void setBeganFromNonInteractive() {
        this.mBeganFromNonInteractive = true;
    }

    /* loaded from: classes2.dex */
    public class GlobalKeyAction {
        public final ComponentName mComponentName;
        public final boolean mDispatchWhenNonInteractive;

        public GlobalKeyAction(String str, String str2) {
            this.mComponentName = ComponentName.unflattenFromString(str);
            this.mDispatchWhenNonInteractive = Boolean.parseBoolean(str2);
        }
    }

    public final void loadGlobalKeys(Context context) {
        try {
            XmlResourceParser xml = context.getResources().getXml(18284553);
            try {
                XmlUtils.beginDocument(xml, "global_keys");
                if (1 == xml.getAttributeIntValue(null, "version", 0)) {
                    while (true) {
                        XmlUtils.nextElement(xml);
                        String name = xml.getName();
                        if (name == null) {
                            break;
                        } else if ("key".equals(name)) {
                            String attributeValue = xml.getAttributeValue(null, "keyCode");
                            String attributeValue2 = xml.getAttributeValue(null, "component");
                            String attributeValue3 = xml.getAttributeValue(null, "dispatchWhenNonInteractive");
                            if (attributeValue != null && attributeValue2 != null) {
                                int keyCodeFromString = KeyEvent.keyCodeFromString(attributeValue);
                                if (keyCodeFromString != 0) {
                                    this.mKeyMapping.put(keyCodeFromString, new GlobalKeyAction(attributeValue2, attributeValue3));
                                } else {
                                    Log.wtf("GlobalKeyManager", "Global keys entry does not map to a valid key code: " + attributeValue);
                                }
                            }
                            Log.wtf("GlobalKeyManager", "Failed to parse global keys entry: " + xml.getText());
                        }
                    }
                }
                xml.close();
            } catch (Throwable th) {
                if (xml != null) {
                    try {
                        xml.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Resources.NotFoundException e) {
            Log.wtf("GlobalKeyManager", "global keys file not found", e);
        } catch (IOException e2) {
            Log.e("GlobalKeyManager", "I/O exception reading global keys file", e2);
        } catch (XmlPullParserException e3) {
            Log.wtf("GlobalKeyManager", "XML parser exception reading global keys file", e3);
        }
    }

    public void dump(String str, PrintWriter printWriter) {
        int size = this.mKeyMapping.size();
        if (size == 0) {
            printWriter.print(str);
            printWriter.println("mKeyMapping.size=0");
            return;
        }
        printWriter.print(str);
        printWriter.println("mKeyMapping={");
        for (int i = 0; i < size; i++) {
            printWriter.print("  ");
            printWriter.print(str);
            printWriter.print(KeyEvent.keyCodeToString(this.mKeyMapping.keyAt(i)));
            printWriter.print("=");
            printWriter.print(this.mKeyMapping.valueAt(i).mComponentName.flattenToString());
            printWriter.print(",dispatchWhenNonInteractive=");
            printWriter.println(this.mKeyMapping.valueAt(i).mDispatchWhenNonInteractive);
        }
        printWriter.print(str);
        printWriter.println("}");
    }
}
