package com.android.server.display;

import android.graphics.Point;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.WifiDisplay;
import android.os.Handler;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class PersistentDataStore {
    public float mBrightnessNitsForDefaultDisplay;
    public boolean mDirty;
    public final HashMap<String, DisplayState> mDisplayStates;
    public final Object mFileAccessLock;
    public BrightnessConfigurations mGlobalBrightnessConfigurations;
    public final Handler mHandler;
    public Injector mInjector;
    public boolean mLoaded;
    public ArrayList<WifiDisplay> mRememberedWifiDisplays;
    public final StableDeviceValues mStableDeviceValues;

    public PersistentDataStore() {
        this(new Injector());
    }

    @VisibleForTesting
    public PersistentDataStore(Injector injector) {
        this(injector, new Handler(BackgroundThread.getHandler().getLooper()));
    }

    @VisibleForTesting
    public PersistentDataStore(Injector injector, Handler handler) {
        this.mRememberedWifiDisplays = new ArrayList<>();
        this.mDisplayStates = new HashMap<>();
        this.mBrightnessNitsForDefaultDisplay = -1.0f;
        this.mStableDeviceValues = new StableDeviceValues();
        this.mGlobalBrightnessConfigurations = new BrightnessConfigurations();
        this.mFileAccessLock = new Object();
        this.mInjector = injector;
        this.mHandler = handler;
    }

    public void saveIfNeeded() {
        if (this.mDirty) {
            save();
            this.mDirty = false;
        }
    }

    public WifiDisplay getRememberedWifiDisplay(String str) {
        loadIfNeeded();
        int findRememberedWifiDisplay = findRememberedWifiDisplay(str);
        if (findRememberedWifiDisplay >= 0) {
            return this.mRememberedWifiDisplays.get(findRememberedWifiDisplay);
        }
        return null;
    }

    public WifiDisplay[] getRememberedWifiDisplays() {
        loadIfNeeded();
        ArrayList<WifiDisplay> arrayList = this.mRememberedWifiDisplays;
        return (WifiDisplay[]) arrayList.toArray(new WifiDisplay[arrayList.size()]);
    }

    public WifiDisplay applyWifiDisplayAlias(WifiDisplay wifiDisplay) {
        if (wifiDisplay != null) {
            loadIfNeeded();
            int findRememberedWifiDisplay = findRememberedWifiDisplay(wifiDisplay.getDeviceAddress());
            String deviceAlias = findRememberedWifiDisplay >= 0 ? this.mRememberedWifiDisplays.get(findRememberedWifiDisplay).getDeviceAlias() : null;
            if (!Objects.equals(wifiDisplay.getDeviceAlias(), deviceAlias)) {
                return new WifiDisplay(wifiDisplay.getDeviceAddress(), wifiDisplay.getDeviceName(), deviceAlias, wifiDisplay.isAvailable(), wifiDisplay.canConnect(), wifiDisplay.isRemembered());
            }
        }
        return wifiDisplay;
    }

    public WifiDisplay[] applyWifiDisplayAliases(WifiDisplay[] wifiDisplayArr) {
        if (wifiDisplayArr != null) {
            int length = wifiDisplayArr.length;
            WifiDisplay[] wifiDisplayArr2 = wifiDisplayArr;
            for (int i = 0; i < length; i++) {
                WifiDisplay applyWifiDisplayAlias = applyWifiDisplayAlias(wifiDisplayArr[i]);
                if (applyWifiDisplayAlias != wifiDisplayArr[i]) {
                    if (wifiDisplayArr2 == wifiDisplayArr) {
                        wifiDisplayArr2 = new WifiDisplay[length];
                        System.arraycopy(wifiDisplayArr, 0, wifiDisplayArr2, 0, length);
                    }
                    wifiDisplayArr2[i] = applyWifiDisplayAlias;
                }
            }
            return wifiDisplayArr2;
        }
        return wifiDisplayArr;
    }

    public boolean rememberWifiDisplay(WifiDisplay wifiDisplay) {
        loadIfNeeded();
        int findRememberedWifiDisplay = findRememberedWifiDisplay(wifiDisplay.getDeviceAddress());
        if (findRememberedWifiDisplay >= 0) {
            if (this.mRememberedWifiDisplays.get(findRememberedWifiDisplay).equals(wifiDisplay)) {
                return false;
            }
            this.mRememberedWifiDisplays.set(findRememberedWifiDisplay, wifiDisplay);
        } else {
            this.mRememberedWifiDisplays.add(wifiDisplay);
        }
        setDirty();
        return true;
    }

    public boolean forgetWifiDisplay(String str) {
        loadIfNeeded();
        int findRememberedWifiDisplay = findRememberedWifiDisplay(str);
        if (findRememberedWifiDisplay >= 0) {
            this.mRememberedWifiDisplays.remove(findRememberedWifiDisplay);
            setDirty();
            return true;
        }
        return false;
    }

    public final int findRememberedWifiDisplay(String str) {
        int size = this.mRememberedWifiDisplays.size();
        for (int i = 0; i < size; i++) {
            if (this.mRememberedWifiDisplays.get(i).getDeviceAddress().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public int getColorMode(DisplayDevice displayDevice) {
        DisplayState displayState;
        if (displayDevice.hasStableUniqueId() && (displayState = getDisplayState(displayDevice.getUniqueId(), false)) != null) {
            return displayState.getColorMode();
        }
        return -1;
    }

    public boolean setColorMode(DisplayDevice displayDevice, int i) {
        if (displayDevice.hasStableUniqueId() && getDisplayState(displayDevice.getUniqueId(), true).setColorMode(i)) {
            setDirty();
            return true;
        }
        return false;
    }

    public float getBrightness(DisplayDevice displayDevice) {
        DisplayState displayState;
        if (displayDevice == null || !displayDevice.hasStableUniqueId() || (displayState = getDisplayState(displayDevice.getUniqueId(), false)) == null) {
            return Float.NaN;
        }
        return displayState.getBrightness();
    }

    public boolean setBrightness(DisplayDevice displayDevice, float f) {
        String uniqueId = displayDevice.getUniqueId();
        if (displayDevice.hasStableUniqueId() && uniqueId != null && getDisplayState(uniqueId, true).setBrightness(f)) {
            setDirty();
            return true;
        }
        return false;
    }

    public float getBrightnessNitsForDefaultDisplay() {
        return this.mBrightnessNitsForDefaultDisplay;
    }

    public boolean setBrightnessNitsForDefaultDisplay(float f) {
        if (f != this.mBrightnessNitsForDefaultDisplay) {
            this.mBrightnessNitsForDefaultDisplay = f;
            setDirty();
            return true;
        }
        return false;
    }

    public boolean setUserPreferredRefreshRate(DisplayDevice displayDevice, float f) {
        String uniqueId = displayDevice.getUniqueId();
        if (displayDevice.hasStableUniqueId() && uniqueId != null && getDisplayState(displayDevice.getUniqueId(), true).setRefreshRate(f)) {
            setDirty();
            return true;
        }
        return false;
    }

    public float getUserPreferredRefreshRate(DisplayDevice displayDevice) {
        DisplayState displayState;
        if (displayDevice == null || !displayDevice.hasStableUniqueId() || (displayState = getDisplayState(displayDevice.getUniqueId(), false)) == null) {
            return Float.NaN;
        }
        return displayState.getRefreshRate();
    }

    public boolean setUserPreferredResolution(DisplayDevice displayDevice, int i, int i2) {
        String uniqueId = displayDevice.getUniqueId();
        if (displayDevice.hasStableUniqueId() && uniqueId != null && getDisplayState(displayDevice.getUniqueId(), true).setResolution(i, i2)) {
            setDirty();
            return true;
        }
        return false;
    }

    public Point getUserPreferredResolution(DisplayDevice displayDevice) {
        DisplayState displayState;
        if (displayDevice == null || !displayDevice.hasStableUniqueId() || (displayState = getDisplayState(displayDevice.getUniqueId(), false)) == null) {
            return null;
        }
        return displayState.getResolution();
    }

    public Point getStableDisplaySize() {
        loadIfNeeded();
        return this.mStableDeviceValues.getDisplaySize();
    }

    public void setStableDisplaySize(Point point) {
        loadIfNeeded();
        if (this.mStableDeviceValues.setDisplaySize(point)) {
            setDirty();
        }
    }

    public void setBrightnessConfigurationForUser(BrightnessConfiguration brightnessConfiguration, int i, String str) {
        loadIfNeeded();
        if (this.mGlobalBrightnessConfigurations.setBrightnessConfigurationForUser(brightnessConfiguration, i, str)) {
            setDirty();
        }
    }

    public boolean setBrightnessConfigurationForDisplayLocked(BrightnessConfiguration brightnessConfiguration, DisplayDevice displayDevice, int i, String str) {
        if (displayDevice != null && displayDevice.hasStableUniqueId() && getDisplayState(displayDevice.getUniqueId(), true).setBrightnessConfiguration(brightnessConfiguration, i, str)) {
            setDirty();
            return true;
        }
        return false;
    }

    public BrightnessConfiguration getBrightnessConfigurationForDisplayLocked(String str, int i) {
        loadIfNeeded();
        DisplayState displayState = this.mDisplayStates.get(str);
        if (displayState != null) {
            return displayState.getBrightnessConfiguration(i);
        }
        return null;
    }

    public BrightnessConfiguration getBrightnessConfiguration(int i) {
        loadIfNeeded();
        return this.mGlobalBrightnessConfigurations.getBrightnessConfiguration(i);
    }

    public final DisplayState getDisplayState(String str, boolean z) {
        loadIfNeeded();
        DisplayState displayState = this.mDisplayStates.get(str);
        if (displayState == null && z) {
            DisplayState displayState2 = new DisplayState();
            this.mDisplayStates.put(str, displayState2);
            setDirty();
            return displayState2;
        }
        return displayState;
    }

    public void loadIfNeeded() {
        if (this.mLoaded) {
            return;
        }
        load();
        this.mLoaded = true;
    }

    public final void setDirty() {
        this.mDirty = true;
    }

    public final void clearState() {
        this.mRememberedWifiDisplays.clear();
    }

    public final void load() {
        synchronized (this.mFileAccessLock) {
            clearState();
            try {
                InputStream openRead = this.mInjector.openRead();
                try {
                    loadFromXml(Xml.resolvePullParser(openRead));
                } catch (IOException e) {
                    Slog.w("DisplayManager.PersistentDataStore", "Failed to load display manager persistent store data.", e);
                    clearState();
                } catch (XmlPullParserException e2) {
                    Slog.w("DisplayManager.PersistentDataStore", "Failed to load display manager persistent store data.", e2);
                    clearState();
                }
                IoUtils.closeQuietly(openRead);
            } catch (FileNotFoundException unused) {
            }
        }
    }

    public final void save() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(byteArrayOutputStream);
            saveToXml(resolveSerializer);
            resolveSerializer.flush();
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler.post(new Runnable() { // from class: com.android.server.display.PersistentDataStore$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PersistentDataStore.this.lambda$save$0(byteArrayOutputStream);
                }
            });
        } catch (IOException e) {
            Slog.w("DisplayManager.PersistentDataStore", "Failed to process the XML serializer.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$save$0(ByteArrayOutputStream byteArrayOutputStream) {
        Injector injector;
        synchronized (this.mFileAccessLock) {
            OutputStream outputStream = null;
            try {
                outputStream = this.mInjector.startWrite();
                byteArrayOutputStream.writeTo(outputStream);
                outputStream.flush();
                injector = this.mInjector;
            } catch (IOException e) {
                Slog.w("DisplayManager.PersistentDataStore", "Failed to save display manager persistent store data.", e);
                if (outputStream != null) {
                    injector = this.mInjector;
                }
            }
            injector.finishWrite(outputStream, true);
        }
    }

    public final void loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        XmlUtils.beginDocument(typedXmlPullParser, "display-manager-state");
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("remembered-wifi-displays")) {
                loadRememberedWifiDisplaysFromXml(typedXmlPullParser);
            }
            if (typedXmlPullParser.getName().equals("display-states")) {
                loadDisplaysFromXml(typedXmlPullParser);
            }
            if (typedXmlPullParser.getName().equals("stable-device-values")) {
                this.mStableDeviceValues.loadFromXml(typedXmlPullParser);
            }
            if (typedXmlPullParser.getName().equals("brightness-configurations")) {
                this.mGlobalBrightnessConfigurations.loadFromXml(typedXmlPullParser);
            }
            if (typedXmlPullParser.getName().equals("brightness-nits-for-default-display")) {
                this.mBrightnessNitsForDefaultDisplay = Float.parseFloat(typedXmlPullParser.nextText());
            }
        }
    }

    public final void loadRememberedWifiDisplaysFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("wifi-display")) {
                String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "deviceAddress");
                String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "deviceName");
                String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "deviceAlias");
                if (attributeValue == null || attributeValue2 == null) {
                    throw new XmlPullParserException("Missing deviceAddress or deviceName attribute on wifi-display.");
                }
                if (findRememberedWifiDisplay(attributeValue) >= 0) {
                    throw new XmlPullParserException("Found duplicate wifi display device address.");
                }
                this.mRememberedWifiDisplays.add(new WifiDisplay(attributeValue, attributeValue2, attributeValue3, false, false, false));
            }
        }
    }

    public final void loadDisplaysFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("display")) {
                String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "unique-id");
                if (attributeValue == null) {
                    throw new XmlPullParserException("Missing unique-id attribute on display.");
                }
                if (this.mDisplayStates.containsKey(attributeValue)) {
                    throw new XmlPullParserException("Found duplicate display.");
                }
                DisplayState displayState = new DisplayState();
                displayState.loadFromXml(typedXmlPullParser);
                this.mDisplayStates.put(attributeValue, displayState);
            }
        }
    }

    public final void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        typedXmlSerializer.startDocument((String) null, Boolean.TRUE);
        typedXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        typedXmlSerializer.startTag((String) null, "display-manager-state");
        typedXmlSerializer.startTag((String) null, "remembered-wifi-displays");
        Iterator<WifiDisplay> it = this.mRememberedWifiDisplays.iterator();
        while (it.hasNext()) {
            WifiDisplay next = it.next();
            typedXmlSerializer.startTag((String) null, "wifi-display");
            typedXmlSerializer.attribute((String) null, "deviceAddress", next.getDeviceAddress());
            typedXmlSerializer.attribute((String) null, "deviceName", next.getDeviceName());
            if (next.getDeviceAlias() != null) {
                typedXmlSerializer.attribute((String) null, "deviceAlias", next.getDeviceAlias());
            }
            typedXmlSerializer.endTag((String) null, "wifi-display");
        }
        typedXmlSerializer.endTag((String) null, "remembered-wifi-displays");
        typedXmlSerializer.startTag((String) null, "display-states");
        for (Map.Entry<String, DisplayState> entry : this.mDisplayStates.entrySet()) {
            typedXmlSerializer.startTag((String) null, "display");
            typedXmlSerializer.attribute((String) null, "unique-id", entry.getKey());
            entry.getValue().saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "display");
        }
        typedXmlSerializer.endTag((String) null, "display-states");
        typedXmlSerializer.startTag((String) null, "stable-device-values");
        this.mStableDeviceValues.saveToXml(typedXmlSerializer);
        typedXmlSerializer.endTag((String) null, "stable-device-values");
        typedXmlSerializer.startTag((String) null, "brightness-configurations");
        this.mGlobalBrightnessConfigurations.saveToXml(typedXmlSerializer);
        typedXmlSerializer.endTag((String) null, "brightness-configurations");
        typedXmlSerializer.startTag((String) null, "brightness-nits-for-default-display");
        typedXmlSerializer.text(Float.toString(this.mBrightnessNitsForDefaultDisplay));
        typedXmlSerializer.endTag((String) null, "brightness-nits-for-default-display");
        typedXmlSerializer.endTag((String) null, "display-manager-state");
        typedXmlSerializer.endDocument();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("PersistentDataStore");
        printWriter.println("  mLoaded=" + this.mLoaded);
        printWriter.println("  mDirty=" + this.mDirty);
        printWriter.println("  RememberedWifiDisplays:");
        Iterator<WifiDisplay> it = this.mRememberedWifiDisplays.iterator();
        int i = 0;
        int i2 = 0;
        while (it.hasNext()) {
            printWriter.println("    " + i2 + ": " + it.next());
            i2++;
        }
        printWriter.println("  DisplayStates:");
        for (Map.Entry<String, DisplayState> entry : this.mDisplayStates.entrySet()) {
            printWriter.println("    " + i + ": " + entry.getKey());
            entry.getValue().dump(printWriter, "      ");
            i++;
        }
        printWriter.println("  StableDeviceValues:");
        this.mStableDeviceValues.dump(printWriter, "      ");
        printWriter.println("  GlobalBrightnessConfigurations:");
        this.mGlobalBrightnessConfigurations.dump(printWriter, "      ");
        printWriter.println("  mBrightnessNitsForDefaultDisplay=" + this.mBrightnessNitsForDefaultDisplay);
    }

    /* loaded from: classes.dex */
    public static final class DisplayState {
        public float mBrightness;
        public int mColorMode;
        public BrightnessConfigurations mDisplayBrightnessConfigurations;
        public int mHeight;
        public float mRefreshRate;
        public int mWidth;

        public DisplayState() {
            this.mBrightness = Float.NaN;
            this.mDisplayBrightnessConfigurations = new BrightnessConfigurations();
        }

        public boolean setColorMode(int i) {
            if (i == this.mColorMode) {
                return false;
            }
            this.mColorMode = i;
            return true;
        }

        public int getColorMode() {
            return this.mColorMode;
        }

        public boolean setBrightness(float f) {
            if (f == this.mBrightness) {
                return false;
            }
            this.mBrightness = f;
            return true;
        }

        public float getBrightness() {
            return this.mBrightness;
        }

        public boolean setBrightnessConfiguration(BrightnessConfiguration brightnessConfiguration, int i, String str) {
            this.mDisplayBrightnessConfigurations.setBrightnessConfigurationForUser(brightnessConfiguration, i, str);
            return true;
        }

        public BrightnessConfiguration getBrightnessConfiguration(int i) {
            return (BrightnessConfiguration) this.mDisplayBrightnessConfigurations.mConfigurations.get(i);
        }

        public boolean setResolution(int i, int i2) {
            if (i == this.mWidth && i2 == this.mHeight) {
                return false;
            }
            this.mWidth = i;
            this.mHeight = i2;
            return true;
        }

        public Point getResolution() {
            return new Point(this.mWidth, this.mHeight);
        }

        public boolean setRefreshRate(float f) {
            if (f == this.mRefreshRate) {
                return false;
            }
            this.mRefreshRate = f;
            return true;
        }

        public float getRefreshRate() {
            return this.mRefreshRate;
        }

        public void loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            int depth = typedXmlPullParser.getDepth();
            while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                String name = typedXmlPullParser.getName();
                name.hashCode();
                char c = 65535;
                switch (name.hashCode()) {
                    case -1377859227:
                        if (name.equals("resolution-width")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1321967815:
                        if (name.equals("brightness-configurations")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -945778443:
                        if (name.equals("brightness-value")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -196957848:
                        if (name.equals("resolution-height")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -92443502:
                        if (name.equals("refresh-rate")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1243304397:
                        if (name.equals("color-mode")) {
                            c = 5;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        this.mWidth = Integer.parseInt(typedXmlPullParser.nextText());
                        break;
                    case 1:
                        this.mDisplayBrightnessConfigurations.loadFromXml(typedXmlPullParser);
                        break;
                    case 2:
                        try {
                            this.mBrightness = Float.parseFloat(typedXmlPullParser.nextText());
                            break;
                        } catch (NumberFormatException unused) {
                            this.mBrightness = Float.NaN;
                            break;
                        }
                    case 3:
                        this.mHeight = Integer.parseInt(typedXmlPullParser.nextText());
                        break;
                    case 4:
                        this.mRefreshRate = Float.parseFloat(typedXmlPullParser.nextText());
                        break;
                    case 5:
                        this.mColorMode = Integer.parseInt(typedXmlPullParser.nextText());
                        break;
                }
            }
        }

        public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
            typedXmlSerializer.startTag((String) null, "color-mode");
            typedXmlSerializer.text(Integer.toString(this.mColorMode));
            typedXmlSerializer.endTag((String) null, "color-mode");
            typedXmlSerializer.startTag((String) null, "brightness-value");
            if (!Float.isNaN(this.mBrightness)) {
                typedXmlSerializer.text(Float.toString(this.mBrightness));
            }
            typedXmlSerializer.endTag((String) null, "brightness-value");
            typedXmlSerializer.startTag((String) null, "brightness-configurations");
            this.mDisplayBrightnessConfigurations.saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "brightness-configurations");
            typedXmlSerializer.startTag((String) null, "resolution-width");
            typedXmlSerializer.text(Integer.toString(this.mWidth));
            typedXmlSerializer.endTag((String) null, "resolution-width");
            typedXmlSerializer.startTag((String) null, "resolution-height");
            typedXmlSerializer.text(Integer.toString(this.mHeight));
            typedXmlSerializer.endTag((String) null, "resolution-height");
            typedXmlSerializer.startTag((String) null, "refresh-rate");
            typedXmlSerializer.text(Float.toString(this.mRefreshRate));
            typedXmlSerializer.endTag((String) null, "refresh-rate");
        }

        public void dump(PrintWriter printWriter, String str) {
            printWriter.println(str + "ColorMode=" + this.mColorMode);
            printWriter.println(str + "BrightnessValue=" + this.mBrightness);
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("DisplayBrightnessConfigurations: ");
            printWriter.println(sb.toString());
            this.mDisplayBrightnessConfigurations.dump(printWriter, str);
            printWriter.println(str + "Resolution=" + this.mWidth + " " + this.mHeight);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append("RefreshRate=");
            sb2.append(this.mRefreshRate);
            printWriter.println(sb2.toString());
        }
    }

    /* loaded from: classes.dex */
    public static final class StableDeviceValues {
        public int mHeight;
        public int mWidth;

        public StableDeviceValues() {
        }

        public final Point getDisplaySize() {
            return new Point(this.mWidth, this.mHeight);
        }

        public boolean setDisplaySize(Point point) {
            int i = this.mWidth;
            int i2 = point.x;
            if (i == i2 && this.mHeight == point.y) {
                return false;
            }
            this.mWidth = i2;
            this.mHeight = point.y;
            return true;
        }

        public void loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            int depth = typedXmlPullParser.getDepth();
            while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                String name = typedXmlPullParser.getName();
                name.hashCode();
                if (name.equals("stable-display-height")) {
                    this.mHeight = loadIntValue(typedXmlPullParser);
                } else if (name.equals("stable-display-width")) {
                    this.mWidth = loadIntValue(typedXmlPullParser);
                }
            }
        }

        public static int loadIntValue(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            try {
                return Integer.parseInt(typedXmlPullParser.nextText());
            } catch (NumberFormatException unused) {
                return 0;
            }
        }

        public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
            if (this.mWidth <= 0 || this.mHeight <= 0) {
                return;
            }
            typedXmlSerializer.startTag((String) null, "stable-display-width");
            typedXmlSerializer.text(Integer.toString(this.mWidth));
            typedXmlSerializer.endTag((String) null, "stable-display-width");
            typedXmlSerializer.startTag((String) null, "stable-display-height");
            typedXmlSerializer.text(Integer.toString(this.mHeight));
            typedXmlSerializer.endTag((String) null, "stable-display-height");
        }

        public void dump(PrintWriter printWriter, String str) {
            printWriter.println(str + "StableDisplayWidth=" + this.mWidth);
            printWriter.println(str + "StableDisplayHeight=" + this.mHeight);
        }
    }

    /* loaded from: classes.dex */
    public static final class BrightnessConfigurations {
        public final SparseArray<BrightnessConfiguration> mConfigurations = new SparseArray<>();
        public final SparseLongArray mTimeStamps = new SparseLongArray();
        public final SparseArray<String> mPackageNames = new SparseArray<>();

        public final boolean setBrightnessConfigurationForUser(BrightnessConfiguration brightnessConfiguration, int i, String str) {
            BrightnessConfiguration brightnessConfiguration2 = this.mConfigurations.get(i);
            if (brightnessConfiguration2 != brightnessConfiguration) {
                if (brightnessConfiguration2 == null || !brightnessConfiguration2.equals(brightnessConfiguration)) {
                    if (brightnessConfiguration != null) {
                        if (str == null) {
                            this.mPackageNames.remove(i);
                        } else {
                            this.mPackageNames.put(i, str);
                        }
                        this.mTimeStamps.put(i, System.currentTimeMillis());
                        this.mConfigurations.put(i, brightnessConfiguration);
                        return true;
                    }
                    this.mPackageNames.remove(i);
                    this.mTimeStamps.delete(i);
                    this.mConfigurations.remove(i);
                    return true;
                }
                return false;
            }
            return false;
        }

        public BrightnessConfiguration getBrightnessConfiguration(int i) {
            return this.mConfigurations.get(i);
        }

        public void loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            int i;
            int depth = typedXmlPullParser.getDepth();
            while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                if ("brightness-configuration".equals(typedXmlPullParser.getName())) {
                    try {
                        i = typedXmlPullParser.getAttributeInt((String) null, "user-serial");
                    } catch (NumberFormatException e) {
                        Slog.e("DisplayManager.PersistentDataStore", "Failed to read in brightness configuration", e);
                        i = -1;
                    }
                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "package-name");
                    long attributeLong = typedXmlPullParser.getAttributeLong((String) null, "timestamp", -1L);
                    try {
                        BrightnessConfiguration loadFromXml = BrightnessConfiguration.loadFromXml(typedXmlPullParser);
                        if (i >= 0 && loadFromXml != null) {
                            this.mConfigurations.put(i, loadFromXml);
                            if (attributeLong != -1) {
                                this.mTimeStamps.put(i, attributeLong);
                            }
                            if (attributeValue != null) {
                                this.mPackageNames.put(i, attributeValue);
                            }
                        }
                    } catch (IllegalArgumentException e2) {
                        Slog.e("DisplayManager.PersistentDataStore", "Failed to load brightness configuration!", e2);
                    }
                }
            }
        }

        public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
            for (int i = 0; i < this.mConfigurations.size(); i++) {
                int keyAt = this.mConfigurations.keyAt(i);
                BrightnessConfiguration valueAt = this.mConfigurations.valueAt(i);
                typedXmlSerializer.startTag((String) null, "brightness-configuration");
                typedXmlSerializer.attributeInt((String) null, "user-serial", keyAt);
                String str = this.mPackageNames.get(keyAt);
                if (str != null) {
                    typedXmlSerializer.attribute((String) null, "package-name", str);
                }
                long j = this.mTimeStamps.get(keyAt, -1L);
                if (j != -1) {
                    typedXmlSerializer.attributeLong((String) null, "timestamp", j);
                }
                valueAt.saveToXml(typedXmlSerializer);
                typedXmlSerializer.endTag((String) null, "brightness-configuration");
            }
        }

        public void dump(PrintWriter printWriter, String str) {
            for (int i = 0; i < this.mConfigurations.size(); i++) {
                int keyAt = this.mConfigurations.keyAt(i);
                long j = this.mTimeStamps.get(keyAt, -1L);
                String str2 = this.mPackageNames.get(keyAt);
                printWriter.println(str + "User " + keyAt + com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                if (j != -1) {
                    printWriter.println(str + "  set at: " + TimeUtils.formatForLogging(j));
                }
                if (str2 != null) {
                    printWriter.println(str + "  set by: " + str2);
                }
                printWriter.println(str + "  " + this.mConfigurations.valueAt(i));
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public final AtomicFile mAtomicFile = new AtomicFile(new File("/data/system/display-manager-state.xml"), "display-state");

        public InputStream openRead() throws FileNotFoundException {
            return this.mAtomicFile.openRead();
        }

        public OutputStream startWrite() throws IOException {
            return this.mAtomicFile.startWrite();
        }

        public void finishWrite(OutputStream outputStream, boolean z) {
            if (!(outputStream instanceof FileOutputStream)) {
                throw new IllegalArgumentException("Unexpected OutputStream as argument: " + outputStream);
            }
            FileOutputStream fileOutputStream = (FileOutputStream) outputStream;
            if (z) {
                this.mAtomicFile.finishWrite(fileOutputStream);
            } else {
                this.mAtomicFile.failWrite(fileOutputStream);
            }
        }
    }
}
