package com.android.server.sensorprivacy;

import android.os.Environment;
import android.os.Handler;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.IoThread;
import com.android.server.sensorprivacy.SensorPrivacyStateController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class AllSensorStateController {
    public static final String LOG_TAG = "AllSensorStateController";
    public static AllSensorStateController sInstance;
    public final AtomicFile mAtomicFile;
    public boolean mEnabled;
    public SensorPrivacyStateController.AllSensorPrivacyListener mListener;
    public Handler mListenerHandler;

    public void dumpLocked(DualDumpOutputStream dualDumpOutputStream) {
    }

    public static AllSensorStateController getInstance() {
        if (sInstance == null) {
            sInstance = new AllSensorStateController();
        }
        return sInstance;
    }

    public AllSensorStateController() {
        AtomicFile atomicFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "sensor_privacy.xml"));
        this.mAtomicFile = atomicFile;
        if (atomicFile.exists()) {
            try {
                FileInputStream openRead = atomicFile.openRead();
                TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
                while (true) {
                    if (resolvePullParser.getEventType() == 1) {
                        break;
                    }
                    String name = resolvePullParser.getName();
                    if ("all-sensor-privacy".equals(name)) {
                        this.mEnabled = XmlUtils.readBooleanAttribute(resolvePullParser, "enabled", false) | this.mEnabled;
                        break;
                    }
                    if ("sensor-privacy".equals(name)) {
                        this.mEnabled |= XmlUtils.readBooleanAttribute(resolvePullParser, "enabled", false);
                    }
                    if ("user".equals(name) && XmlUtils.readIntAttribute(resolvePullParser, "id", -1) == 0) {
                        this.mEnabled |= XmlUtils.readBooleanAttribute(resolvePullParser, "enabled");
                    }
                    XmlUtils.nextElement(resolvePullParser);
                }
                if (openRead != null) {
                    openRead.close();
                }
            } catch (IOException | XmlPullParserException e) {
                Log.e(LOG_TAG, "Caught an exception reading the state from storage: ", e);
                this.mEnabled = false;
            }
        }
    }

    public boolean getAllSensorStateLocked() {
        return this.mEnabled;
    }

    public void setAllSensorStateLocked(boolean z) {
        Handler handler;
        if (this.mEnabled != z) {
            this.mEnabled = z;
            final SensorPrivacyStateController.AllSensorPrivacyListener allSensorPrivacyListener = this.mListener;
            if (allSensorPrivacyListener == null || (handler = this.mListenerHandler) == null) {
                return;
            }
            Objects.requireNonNull(allSensorPrivacyListener);
            handler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.sensorprivacy.AllSensorStateController$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SensorPrivacyStateController.AllSensorPrivacyListener.this.onAllSensorPrivacyChanged(((Boolean) obj).booleanValue());
                }
            }, Boolean.valueOf(z)));
        }
    }

    public void setAllSensorPrivacyListenerLocked(Handler handler, SensorPrivacyStateController.AllSensorPrivacyListener allSensorPrivacyListener) {
        Objects.requireNonNull(handler);
        Objects.requireNonNull(allSensorPrivacyListener);
        if (this.mListener != null) {
            throw new IllegalStateException("Listener is already set");
        }
        this.mListener = allSensorPrivacyListener;
        this.mListenerHandler = handler;
    }

    public void schedulePersistLocked() {
        IoThread.getHandler().sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.sensorprivacy.AllSensorStateController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AllSensorStateController.this.persist(((Boolean) obj).booleanValue());
            }
        }, Boolean.valueOf(this.mEnabled)));
    }

    public final void persist(boolean z) {
        FileOutputStream startWrite;
        FileOutputStream fileOutputStream = null;
        try {
            startWrite = this.mAtomicFile.startWrite();
        } catch (IOException e) {
            e = e;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.startTag((String) null, "all-sensor-privacy");
            resolveSerializer.attributeBoolean((String) null, "enabled", z);
            resolveSerializer.endTag((String) null, "all-sensor-privacy");
            resolveSerializer.endDocument();
            this.mAtomicFile.finishWrite(startWrite);
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = startWrite;
            Log.e(LOG_TAG, "Caught an exception persisting the sensor privacy state: ", e);
            this.mAtomicFile.failWrite(fileOutputStream);
        }
    }
}
