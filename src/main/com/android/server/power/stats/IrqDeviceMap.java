package com.android.server.power.stats;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.LongSparseArray;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class IrqDeviceMap {
    public static LongSparseArray<IrqDeviceMap> sInstanceMap = new LongSparseArray<>(1);
    public final ArrayMap<String, List<String>> mSubsystemsForDevice = new ArrayMap<>();

    public IrqDeviceMap(XmlResourceParser xmlResourceParser) {
        try {
            try {
                XmlUtils.beginDocument(xmlResourceParser, "irq-device-map");
                ArraySet arraySet = new ArraySet();
                String str = null;
                while (true) {
                    int eventType = xmlResourceParser.getEventType();
                    if (eventType == 1) {
                        return;
                    }
                    if (eventType == 2 && xmlResourceParser.getName().equals("device")) {
                        str = xmlResourceParser.getAttributeValue(null, "name");
                    }
                    if (str != null && eventType == 3 && xmlResourceParser.getName().equals("device")) {
                        if (arraySet.size() > 0) {
                            this.mSubsystemsForDevice.put(str, Collections.unmodifiableList(new ArrayList(arraySet)));
                        }
                        arraySet.clear();
                        str = null;
                    }
                    if (str != null && eventType == 2 && xmlResourceParser.getName().equals("subsystem")) {
                        xmlResourceParser.next();
                        if (xmlResourceParser.getEventType() == 4) {
                            arraySet.add(xmlResourceParser.getText());
                        }
                    }
                    xmlResourceParser.next();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (XmlPullParserException e2) {
                throw new RuntimeException(e2);
            }
        } finally {
            xmlResourceParser.close();
        }
    }

    public static IrqDeviceMap getInstance(Context context, int i) {
        synchronized (IrqDeviceMap.class) {
            long j = i;
            int indexOfKey = sInstanceMap.indexOfKey(j);
            if (indexOfKey >= 0) {
                return sInstanceMap.valueAt(indexOfKey);
            }
            IrqDeviceMap irqDeviceMap = new IrqDeviceMap(context.getResources().getXml(i));
            synchronized (IrqDeviceMap.class) {
                sInstanceMap.put(j, irqDeviceMap);
            }
            return irqDeviceMap;
        }
    }

    public List<String> getSubsystemsForDevice(String str) {
        return this.mSubsystemsForDevice.get(str);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        LongSparseArray<IrqDeviceMap> longSparseArray;
        int indexOfValue;
        indentingPrintWriter.println("Irq device map:");
        indentingPrintWriter.increaseIndent();
        synchronized (IrqDeviceMap.class) {
            longSparseArray = sInstanceMap;
        }
        indentingPrintWriter.println("Loaded from xml resource: " + (longSparseArray.indexOfValue(this) >= 0 ? "0x" + Long.toHexString(longSparseArray.keyAt(indexOfValue)) : null));
        indentingPrintWriter.println("Map:");
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < this.mSubsystemsForDevice.size(); i++) {
            indentingPrintWriter.print(this.mSubsystemsForDevice.keyAt(i) + ": ");
            indentingPrintWriter.println(this.mSubsystemsForDevice.valueAt(i));
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }
}
