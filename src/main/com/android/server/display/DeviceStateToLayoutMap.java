package com.android.server.display;

import android.os.Environment;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayAddress;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.display.config.layout.Display;
import com.android.server.display.config.layout.Layouts;
import com.android.server.display.config.layout.XmlParser;
import com.android.server.display.layout.DisplayIdProducer;
import com.android.server.display.layout.Layout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class DeviceStateToLayoutMap {
    public final DisplayIdProducer mIdProducer;
    public final SparseArray<Layout> mLayoutMap;

    public DeviceStateToLayoutMap(DisplayIdProducer displayIdProducer) {
        this(displayIdProducer, Environment.buildPath(Environment.getVendorDirectory(), new String[]{"etc/displayconfig/display_layout_configuration.xml"}));
    }

    public DeviceStateToLayoutMap(DisplayIdProducer displayIdProducer, File file) {
        this.mLayoutMap = new SparseArray<>();
        this.mIdProducer = displayIdProducer;
        loadLayoutsFromConfig(file);
        createLayout(-1);
    }

    public void dumpLocked(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("DeviceStateToLayoutMap:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Registered Layouts:");
        for (int i = 0; i < this.mLayoutMap.size(); i++) {
            indentingPrintWriter.println("state(" + this.mLayoutMap.keyAt(i) + "): " + this.mLayoutMap.valueAt(i));
        }
    }

    public Layout get(int i) {
        Layout layout = this.mLayoutMap.get(i);
        return layout == null ? this.mLayoutMap.get(-1) : layout;
    }

    public int size() {
        return this.mLayoutMap.size();
    }

    @VisibleForTesting
    public void loadLayoutsFromConfig(File file) {
        if (file.exists()) {
            Slog.i("DeviceStateToLayoutMap", "Loading display layouts from " + file);
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                Layouts read = XmlParser.read(bufferedInputStream);
                if (read == null) {
                    Slog.i("DeviceStateToLayoutMap", "Display layout config not found: " + file);
                    bufferedInputStream.close();
                    return;
                }
                for (com.android.server.display.config.layout.Layout layout : read.getLayout()) {
                    Layout createLayout = createLayout(layout.getState().intValue());
                    for (Display display : layout.getDisplay()) {
                        Layout.Display createDisplayLocked = createLayout.createDisplayLocked(DisplayAddress.fromPhysicalDisplayId(display.getAddress().longValue()), display.isDefaultDisplay(), display.isEnabled(), display.getDisplayGroup(), this.mIdProducer, display.getBrightnessThrottlingMapId(), 0);
                        if ("front".equals(display.getPosition())) {
                            createDisplayLocked.setPosition(0);
                        } else if ("rear".equals(display.getPosition())) {
                            createDisplayLocked.setPosition(1);
                        } else {
                            createDisplayLocked.setPosition(-1);
                        }
                        createDisplayLocked.setRefreshRateZoneId(display.getRefreshRateZoneId());
                        createDisplayLocked.setRefreshRateThermalThrottlingMapId(display.getRefreshRateThermalThrottlingMapId());
                    }
                }
                bufferedInputStream.close();
            } catch (IOException | DatatypeConfigurationException | XmlPullParserException e) {
                Slog.e("DeviceStateToLayoutMap", "Encountered an error while reading/parsing display layout config file: " + file, e);
            }
        }
    }

    public final Layout createLayout(int i) {
        if (this.mLayoutMap.contains(i)) {
            Slog.e("DeviceStateToLayoutMap", "Attempted to create a second layout for state " + i);
            return null;
        }
        Layout layout = new Layout();
        this.mLayoutMap.append(i, layout);
        return layout;
    }
}
