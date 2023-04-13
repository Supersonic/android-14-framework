package com.android.server.input;

import android.hardware.input.TouchCalibration;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class PersistentDataStore {
    public boolean mDirty;
    public Injector mInjector;
    public final HashMap<String, InputDeviceState> mInputDevices;
    public Map<Integer, Integer> mKeyRemapping;
    public boolean mLoaded;

    public PersistentDataStore() {
        this(new Injector());
    }

    @VisibleForTesting
    public PersistentDataStore(Injector injector) {
        this.mInputDevices = new HashMap<>();
        this.mKeyRemapping = new HashMap();
        this.mInjector = injector;
    }

    public void saveIfNeeded() {
        if (this.mDirty) {
            save();
            this.mDirty = false;
        }
    }

    public TouchCalibration getTouchCalibration(String str, int i) {
        InputDeviceState inputDeviceState = getInputDeviceState(str);
        if (inputDeviceState == null) {
            return TouchCalibration.IDENTITY;
        }
        TouchCalibration touchCalibration = inputDeviceState.getTouchCalibration(i);
        return touchCalibration == null ? TouchCalibration.IDENTITY : touchCalibration;
    }

    public boolean setTouchCalibration(String str, int i, TouchCalibration touchCalibration) {
        if (getOrCreateInputDeviceState(str).setTouchCalibration(i, touchCalibration)) {
            setDirty();
            return true;
        }
        return false;
    }

    public String getCurrentKeyboardLayout(String str) {
        InputDeviceState inputDeviceState = getInputDeviceState(str);
        if (inputDeviceState != null) {
            return inputDeviceState.getCurrentKeyboardLayout();
        }
        return null;
    }

    public boolean setCurrentKeyboardLayout(String str, String str2) {
        if (getOrCreateInputDeviceState(str).setCurrentKeyboardLayout(str2)) {
            setDirty();
            return true;
        }
        return false;
    }

    public String getKeyboardLayout(String str, String str2) {
        InputDeviceState inputDeviceState = getInputDeviceState(str);
        if (inputDeviceState != null) {
            return inputDeviceState.getKeyboardLayout(str2);
        }
        return null;
    }

    public boolean setKeyboardLayout(String str, String str2, String str3) {
        if (getOrCreateInputDeviceState(str).setKeyboardLayout(str2, str3)) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean setSelectedKeyboardLayouts(String str, Set<String> set) {
        if (getOrCreateInputDeviceState(str).setSelectedKeyboardLayouts(set)) {
            setDirty();
            return true;
        }
        return false;
    }

    public String[] getKeyboardLayouts(String str) {
        InputDeviceState inputDeviceState = getInputDeviceState(str);
        if (inputDeviceState == null) {
            return (String[]) ArrayUtils.emptyArray(String.class);
        }
        return inputDeviceState.getKeyboardLayouts();
    }

    public boolean addKeyboardLayout(String str, String str2) {
        if (getOrCreateInputDeviceState(str).addKeyboardLayout(str2)) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean removeKeyboardLayout(String str, String str2) {
        if (getOrCreateInputDeviceState(str).removeKeyboardLayout(str2)) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean switchKeyboardLayout(String str, int i) {
        InputDeviceState inputDeviceState = getInputDeviceState(str);
        if (inputDeviceState == null || !inputDeviceState.switchKeyboardLayout(i)) {
            return false;
        }
        setDirty();
        return true;
    }

    public boolean setKeyboardBacklightBrightness(String str, int i, int i2) {
        if (getOrCreateInputDeviceState(str).setKeyboardBacklightBrightness(i, i2)) {
            setDirty();
            return true;
        }
        return false;
    }

    public OptionalInt getKeyboardBacklightBrightness(String str, int i) {
        InputDeviceState inputDeviceState = getInputDeviceState(str);
        if (inputDeviceState == null) {
            return OptionalInt.empty();
        }
        return inputDeviceState.getKeyboardBacklightBrightness(i);
    }

    public boolean remapKey(int i, int i2) {
        loadIfNeeded();
        if (this.mKeyRemapping.getOrDefault(Integer.valueOf(i), -1).intValue() == i2) {
            return false;
        }
        this.mKeyRemapping.put(Integer.valueOf(i), Integer.valueOf(i2));
        setDirty();
        return true;
    }

    public boolean clearMappedKey(int i) {
        loadIfNeeded();
        if (this.mKeyRemapping.containsKey(Integer.valueOf(i))) {
            this.mKeyRemapping.remove(Integer.valueOf(i));
            setDirty();
            return true;
        }
        return true;
    }

    public Map<Integer, Integer> getKeyRemapping() {
        loadIfNeeded();
        return new HashMap(this.mKeyRemapping);
    }

    public boolean removeUninstalledKeyboardLayouts(Set<String> set) {
        boolean z = false;
        for (InputDeviceState inputDeviceState : this.mInputDevices.values()) {
            if (inputDeviceState.removeUninstalledKeyboardLayouts(set)) {
                z = true;
            }
        }
        if (z) {
            setDirty();
            return true;
        }
        return false;
    }

    public final InputDeviceState getInputDeviceState(String str) {
        loadIfNeeded();
        return this.mInputDevices.get(str);
    }

    public final InputDeviceState getOrCreateInputDeviceState(String str) {
        loadIfNeeded();
        InputDeviceState inputDeviceState = this.mInputDevices.get(str);
        if (inputDeviceState == null) {
            InputDeviceState inputDeviceState2 = new InputDeviceState();
            this.mInputDevices.put(str, inputDeviceState2);
            setDirty();
            return inputDeviceState2;
        }
        return inputDeviceState;
    }

    public final void loadIfNeeded() {
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
        this.mKeyRemapping.clear();
        this.mInputDevices.clear();
    }

    public final void load() {
        AutoCloseable autoCloseable;
        clearState();
        try {
            try {
                InputStream openRead = this.mInjector.openRead();
                try {
                    loadFromXml(Xml.resolvePullParser(openRead));
                } catch (IOException e) {
                    Slog.w("InputManager", "Failed to load input manager persistent store data.", e);
                    clearState();
                } catch (XmlPullParserException e2) {
                    Slog.w("InputManager", "Failed to load input manager persistent store data.", e2);
                    clearState();
                }
                IoUtils.closeQuietly(openRead);
            } catch (FileNotFoundException unused) {
            }
        } catch (Throwable th) {
            IoUtils.closeQuietly(autoCloseable);
            throw th;
        }
    }

    public final void save() {
        try {
            FileOutputStream startWrite = this.mInjector.startWrite();
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            saveToXml(resolveSerializer);
            resolveSerializer.flush();
            this.mInjector.finishWrite(startWrite, true);
        } catch (IOException e) {
            Slog.w("InputManager", "Failed to save input manager persistent store data.", e);
        }
    }

    public final void loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        XmlUtils.beginDocument(typedXmlPullParser, "input-manager-state");
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("key-remapping")) {
                loadKeyRemappingFromXml(typedXmlPullParser);
            } else if (typedXmlPullParser.getName().equals("input-devices")) {
                loadInputDevicesFromXml(typedXmlPullParser);
            }
        }
    }

    public final void loadInputDevicesFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("input-device")) {
                String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "descriptor");
                if (attributeValue == null) {
                    throw new XmlPullParserException("Missing descriptor attribute on input-device.");
                }
                if (this.mInputDevices.containsKey(attributeValue)) {
                    throw new XmlPullParserException("Found duplicate input device.");
                }
                InputDeviceState inputDeviceState = new InputDeviceState();
                inputDeviceState.loadFromXml(typedXmlPullParser);
                this.mInputDevices.put(attributeValue, inputDeviceState);
            }
        }
    }

    public final void loadKeyRemappingFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            if (typedXmlPullParser.getName().equals("remap")) {
                this.mKeyRemapping.put(Integer.valueOf(typedXmlPullParser.getAttributeInt((String) null, "from-key")), Integer.valueOf(typedXmlPullParser.getAttributeInt((String) null, "to-key")));
            }
        }
    }

    public final void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        typedXmlSerializer.startDocument((String) null, Boolean.TRUE);
        typedXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        typedXmlSerializer.startTag((String) null, "input-manager-state");
        typedXmlSerializer.startTag((String) null, "key-remapping");
        for (Integer num : this.mKeyRemapping.keySet()) {
            int intValue = num.intValue();
            int intValue2 = this.mKeyRemapping.get(Integer.valueOf(intValue)).intValue();
            typedXmlSerializer.startTag((String) null, "remap");
            typedXmlSerializer.attributeInt((String) null, "from-key", intValue);
            typedXmlSerializer.attributeInt((String) null, "to-key", intValue2);
            typedXmlSerializer.endTag((String) null, "remap");
        }
        typedXmlSerializer.endTag((String) null, "key-remapping");
        typedXmlSerializer.startTag((String) null, "input-devices");
        for (Map.Entry<String, InputDeviceState> entry : this.mInputDevices.entrySet()) {
            typedXmlSerializer.startTag((String) null, "input-device");
            typedXmlSerializer.attribute((String) null, "descriptor", entry.getKey());
            entry.getValue().saveToXml(typedXmlSerializer);
            typedXmlSerializer.endTag((String) null, "input-device");
        }
        typedXmlSerializer.endTag((String) null, "input-devices");
        typedXmlSerializer.endTag((String) null, "input-manager-state");
        typedXmlSerializer.endDocument();
    }

    /* loaded from: classes.dex */
    public static final class InputDeviceState {
        public static final String[] CALIBRATION_NAME = {"x_scale", "x_ymix", "x_offset", "y_xmix", "y_scale", "y_offset"};
        public String mCurrentKeyboardLayout;
        public final SparseIntArray mKeyboardBacklightBrightnessMap;
        public final Map<String, String> mKeyboardLayoutMap;
        public final ArrayList<String> mKeyboardLayouts;
        public Set<String> mSelectedKeyboardLayouts;
        public final TouchCalibration[] mTouchCalibration;

        public InputDeviceState() {
            this.mTouchCalibration = new TouchCalibration[4];
            this.mKeyboardLayouts = new ArrayList<>();
            this.mKeyboardBacklightBrightnessMap = new SparseIntArray();
            this.mKeyboardLayoutMap = new ArrayMap();
        }

        public TouchCalibration getTouchCalibration(int i) {
            try {
                return this.mTouchCalibration[i];
            } catch (ArrayIndexOutOfBoundsException e) {
                Slog.w("InputManager", "Cannot get touch calibration.", e);
                return null;
            }
        }

        public boolean setTouchCalibration(int i, TouchCalibration touchCalibration) {
            try {
                if (touchCalibration.equals(this.mTouchCalibration[i])) {
                    return false;
                }
                this.mTouchCalibration[i] = touchCalibration;
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                Slog.w("InputManager", "Cannot set touch calibration.", e);
                return false;
            }
        }

        public String getKeyboardLayout(String str) {
            return this.mKeyboardLayoutMap.get(str);
        }

        public boolean setKeyboardLayout(String str, String str2) {
            return !Objects.equals(this.mKeyboardLayoutMap.put(str, str2), str2);
        }

        public boolean setSelectedKeyboardLayouts(Set<String> set) {
            if (Objects.equals(this.mSelectedKeyboardLayouts, set)) {
                return false;
            }
            this.mSelectedKeyboardLayouts = new HashSet(set);
            return true;
        }

        public String getCurrentKeyboardLayout() {
            return this.mCurrentKeyboardLayout;
        }

        public boolean setCurrentKeyboardLayout(String str) {
            if (Objects.equals(this.mCurrentKeyboardLayout, str)) {
                return false;
            }
            addKeyboardLayout(str);
            this.mCurrentKeyboardLayout = str;
            return true;
        }

        public String[] getKeyboardLayouts() {
            if (this.mKeyboardLayouts.isEmpty()) {
                return (String[]) ArrayUtils.emptyArray(String.class);
            }
            ArrayList<String> arrayList = this.mKeyboardLayouts;
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }

        public boolean addKeyboardLayout(String str) {
            int binarySearch = Collections.binarySearch(this.mKeyboardLayouts, str);
            if (binarySearch >= 0) {
                return false;
            }
            this.mKeyboardLayouts.add((-binarySearch) - 1, str);
            if (this.mCurrentKeyboardLayout == null) {
                this.mCurrentKeyboardLayout = str;
            }
            return true;
        }

        public boolean removeKeyboardLayout(String str) {
            int binarySearch = Collections.binarySearch(this.mKeyboardLayouts, str);
            if (binarySearch < 0) {
                return false;
            }
            this.mKeyboardLayouts.remove(binarySearch);
            updateCurrentKeyboardLayoutIfRemoved(str, binarySearch);
            return true;
        }

        public boolean setKeyboardBacklightBrightness(int i, int i2) {
            if (this.mKeyboardBacklightBrightnessMap.get(i, -1) == i2) {
                return false;
            }
            this.mKeyboardBacklightBrightnessMap.put(i, i2);
            return true;
        }

        public OptionalInt getKeyboardBacklightBrightness(int i) {
            int i2 = this.mKeyboardBacklightBrightnessMap.get(i, -1);
            return i2 == -1 ? OptionalInt.empty() : OptionalInt.of(i2);
        }

        public final void updateCurrentKeyboardLayoutIfRemoved(String str, int i) {
            if (Objects.equals(this.mCurrentKeyboardLayout, str)) {
                if (!this.mKeyboardLayouts.isEmpty()) {
                    if (i == this.mKeyboardLayouts.size()) {
                        i = 0;
                    }
                    this.mCurrentKeyboardLayout = this.mKeyboardLayouts.get(i);
                    return;
                }
                this.mCurrentKeyboardLayout = null;
            }
        }

        public boolean switchKeyboardLayout(int i) {
            int i2;
            int size = this.mKeyboardLayouts.size();
            if (size < 2) {
                return false;
            }
            int binarySearch = Collections.binarySearch(this.mKeyboardLayouts, this.mCurrentKeyboardLayout);
            if (i > 0) {
                i2 = (binarySearch + 1) % size;
            } else {
                i2 = ((binarySearch + size) - 1) % size;
            }
            this.mCurrentKeyboardLayout = this.mKeyboardLayouts.get(i2);
            return true;
        }

        public boolean removeUninstalledKeyboardLayouts(Set<String> set) {
            int size = this.mKeyboardLayouts.size();
            boolean z = false;
            while (true) {
                int i = size - 1;
                if (size <= 0) {
                    break;
                }
                String str = this.mKeyboardLayouts.get(i);
                if (!set.contains(str)) {
                    Slog.i("InputManager", "Removing uninstalled keyboard layout " + str);
                    this.mKeyboardLayouts.remove(i);
                    updateCurrentKeyboardLayoutIfRemoved(str, i);
                    z = true;
                }
                size = i;
            }
            ArrayList<String> arrayList = new ArrayList();
            for (String str2 : this.mKeyboardLayoutMap.keySet()) {
                if (!set.contains(this.mKeyboardLayoutMap.get(str2))) {
                    arrayList.add(str2);
                }
            }
            if (arrayList.isEmpty()) {
                return z;
            }
            for (String str3 : arrayList) {
                this.mKeyboardLayoutMap.remove(str3);
            }
            return true;
        }

        public void loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            int stringToSurfaceRotation;
            int depth = typedXmlPullParser.getDepth();
            while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
                if (typedXmlPullParser.getName().equals("keyboard-layout")) {
                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "descriptor");
                    if (attributeValue == null) {
                        throw new XmlPullParserException("Missing descriptor attribute on keyboard-layout.");
                    }
                    String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "current");
                    if (this.mKeyboardLayouts.contains(attributeValue)) {
                        throw new XmlPullParserException("Found duplicate keyboard layout.");
                    }
                    this.mKeyboardLayouts.add(attributeValue);
                    if (attributeValue2 != null && attributeValue2.equals("true")) {
                        if (this.mCurrentKeyboardLayout != null) {
                            throw new XmlPullParserException("Found multiple current keyboard layouts.");
                        }
                        this.mCurrentKeyboardLayout = attributeValue;
                    }
                } else if (typedXmlPullParser.getName().equals("keyed-keyboard-layout")) {
                    String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "key");
                    if (attributeValue3 == null) {
                        throw new XmlPullParserException("Missing key attribute on keyed-keyboard-layout.");
                    }
                    String attributeValue4 = typedXmlPullParser.getAttributeValue((String) null, "layout");
                    if (attributeValue4 == null) {
                        throw new XmlPullParserException("Missing layout attribute on keyed-keyboard-layout.");
                    }
                    this.mKeyboardLayoutMap.put(attributeValue3, attributeValue4);
                } else if (typedXmlPullParser.getName().equals("selected-keyboard-layout")) {
                    String attributeValue5 = typedXmlPullParser.getAttributeValue((String) null, "layout");
                    if (attributeValue5 == null) {
                        throw new XmlPullParserException("Missing layout attribute on selected-keyboard-layout.");
                    }
                    if (this.mSelectedKeyboardLayouts == null) {
                        this.mSelectedKeyboardLayouts = new HashSet();
                    }
                    this.mSelectedKeyboardLayouts.add(attributeValue5);
                } else if (typedXmlPullParser.getName().equals("light-info")) {
                    this.mKeyboardBacklightBrightnessMap.put(typedXmlPullParser.getAttributeInt((String) null, "light-id"), typedXmlPullParser.getAttributeInt((String) null, "light-brightness"));
                } else if (typedXmlPullParser.getName().equals("calibration")) {
                    String attributeValue6 = typedXmlPullParser.getAttributeValue((String) null, "format");
                    String attributeValue7 = typedXmlPullParser.getAttributeValue((String) null, "rotation");
                    if (attributeValue6 == null) {
                        throw new XmlPullParserException("Missing format attribute on calibration.");
                    }
                    if (!attributeValue6.equals("affine")) {
                        throw new XmlPullParserException("Unsupported format for calibration.");
                    }
                    if (attributeValue7 != null) {
                        try {
                            stringToSurfaceRotation = stringToSurfaceRotation(attributeValue7);
                        } catch (IllegalArgumentException unused) {
                            throw new XmlPullParserException("Unsupported rotation for calibration.");
                        }
                    } else {
                        stringToSurfaceRotation = -1;
                    }
                    float[] affineTransform = TouchCalibration.IDENTITY.getAffineTransform();
                    int depth2 = typedXmlPullParser.getDepth();
                    while (XmlUtils.nextElementWithin(typedXmlPullParser, depth2)) {
                        String lowerCase = typedXmlPullParser.getName().toLowerCase();
                        String nextText = typedXmlPullParser.nextText();
                        int i = 0;
                        while (true) {
                            if (i < affineTransform.length) {
                                String[] strArr = CALIBRATION_NAME;
                                if (i >= strArr.length) {
                                    break;
                                } else if (lowerCase.equals(strArr[i])) {
                                    affineTransform[i] = Float.parseFloat(nextText);
                                    break;
                                } else {
                                    i++;
                                }
                            }
                        }
                    }
                    if (stringToSurfaceRotation == -1) {
                        int i2 = 0;
                        while (true) {
                            TouchCalibration[] touchCalibrationArr = this.mTouchCalibration;
                            if (i2 < touchCalibrationArr.length) {
                                touchCalibrationArr[i2] = new TouchCalibration(affineTransform[0], affineTransform[1], affineTransform[2], affineTransform[3], affineTransform[4], affineTransform[5]);
                                i2++;
                            }
                        }
                    } else {
                        this.mTouchCalibration[stringToSurfaceRotation] = new TouchCalibration(affineTransform[0], affineTransform[1], affineTransform[2], affineTransform[3], affineTransform[4], affineTransform[5]);
                    }
                } else {
                    continue;
                }
            }
            Collections.sort(this.mKeyboardLayouts);
            if (this.mCurrentKeyboardLayout != null || this.mKeyboardLayouts.isEmpty()) {
                return;
            }
            this.mCurrentKeyboardLayout = this.mKeyboardLayouts.get(0);
        }

        public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
            Iterator<String> it = this.mKeyboardLayouts.iterator();
            while (it.hasNext()) {
                String next = it.next();
                typedXmlSerializer.startTag((String) null, "keyboard-layout");
                typedXmlSerializer.attribute((String) null, "descriptor", next);
                if (next.equals(this.mCurrentKeyboardLayout)) {
                    typedXmlSerializer.attributeBoolean((String) null, "current", true);
                }
                typedXmlSerializer.endTag((String) null, "keyboard-layout");
            }
            for (String str : this.mKeyboardLayoutMap.keySet()) {
                typedXmlSerializer.startTag((String) null, "keyed-keyboard-layout");
                typedXmlSerializer.attribute((String) null, "key", str);
                typedXmlSerializer.attribute((String) null, "layout", this.mKeyboardLayoutMap.get(str));
                typedXmlSerializer.endTag((String) null, "keyed-keyboard-layout");
            }
            Set<String> set = this.mSelectedKeyboardLayouts;
            if (set != null) {
                for (String str2 : set) {
                    typedXmlSerializer.startTag((String) null, "selected-keyboard-layout");
                    typedXmlSerializer.attribute((String) null, "layout", str2);
                    typedXmlSerializer.endTag((String) null, "selected-keyboard-layout");
                }
            }
            for (int i = 0; i < this.mKeyboardBacklightBrightnessMap.size(); i++) {
                typedXmlSerializer.startTag((String) null, "light-info");
                typedXmlSerializer.attributeInt((String) null, "light-id", this.mKeyboardBacklightBrightnessMap.keyAt(i));
                typedXmlSerializer.attributeInt((String) null, "light-brightness", this.mKeyboardBacklightBrightnessMap.valueAt(i));
                typedXmlSerializer.endTag((String) null, "light-info");
            }
            int i2 = 0;
            while (true) {
                TouchCalibration[] touchCalibrationArr = this.mTouchCalibration;
                if (i2 >= touchCalibrationArr.length) {
                    return;
                }
                if (touchCalibrationArr[i2] != null) {
                    String surfaceRotationToString = surfaceRotationToString(i2);
                    float[] affineTransform = this.mTouchCalibration[i2].getAffineTransform();
                    typedXmlSerializer.startTag((String) null, "calibration");
                    typedXmlSerializer.attribute((String) null, "format", "affine");
                    typedXmlSerializer.attribute((String) null, "rotation", surfaceRotationToString);
                    for (int i3 = 0; i3 < affineTransform.length; i3++) {
                        String[] strArr = CALIBRATION_NAME;
                        if (i3 >= strArr.length) {
                            break;
                        }
                        typedXmlSerializer.startTag((String) null, strArr[i3]);
                        typedXmlSerializer.text(Float.toString(affineTransform[i3]));
                        typedXmlSerializer.endTag((String) null, strArr[i3]);
                    }
                    typedXmlSerializer.endTag((String) null, "calibration");
                }
                i2++;
            }
        }

        public static String surfaceRotationToString(int i) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i == 3) {
                            return "270";
                        }
                        throw new IllegalArgumentException("Unsupported surface rotation value" + i);
                    }
                    return "180";
                }
                return "90";
            }
            return "0";
        }

        public static int stringToSurfaceRotation(String str) {
            if ("0".equals(str)) {
                return 0;
            }
            if ("90".equals(str)) {
                return 1;
            }
            if ("180".equals(str)) {
                return 2;
            }
            if ("270".equals(str)) {
                return 3;
            }
            throw new IllegalArgumentException("Unsupported surface rotation string '" + str + "'");
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public final AtomicFile mAtomicFile = new AtomicFile(new File("/data/system/input-manager-state.xml"), "input-state");

        public InputStream openRead() throws FileNotFoundException {
            return this.mAtomicFile.openRead();
        }

        public FileOutputStream startWrite() throws IOException {
            return this.mAtomicFile.startWrite();
        }

        public void finishWrite(FileOutputStream fileOutputStream, boolean z) {
            if (z) {
                this.mAtomicFile.finishWrite(fileOutputStream);
            } else {
                this.mAtomicFile.failWrite(fileOutputStream);
            }
        }
    }
}
