package com.android.server.input;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.icu.lang.UScript;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Toast;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.inputmethod.InputMethodSubtypeHandle;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import libcore.io.Streams;
/* loaded from: classes.dex */
public final class KeyboardLayoutManager implements InputManager.InputDeviceListener {
    public static final boolean DEBUG = Log.isLoggable("KeyboardLayoutManager", 3);
    public final Context mContext;
    public ImeInfo mCurrentImeInfo;
    @GuardedBy({"mDataStore"})
    public final PersistentDataStore mDataStore;
    public final Handler mHandler;
    public final NativeInputManagerService mNative;
    public Toast mSwitchedKeyboardLayoutToast;
    public final SparseArray<Set<String>> mConfiguredKeyboards = new SparseArray<>();
    @GuardedBy({"mKeyboardLayoutCache"})
    public final Map<String, String> mKeyboardLayoutCache = new ArrayMap();

    /* loaded from: classes.dex */
    public interface KeyboardLayoutVisitor {
        void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout);
    }

    public KeyboardLayoutManager(Context context, NativeInputManagerService nativeInputManagerService, PersistentDataStore persistentDataStore, Looper looper) {
        this.mContext = context;
        this.mNative = nativeInputManagerService;
        this.mDataStore = persistentDataStore;
        this.mHandler = new Handler(looper, new Handler.Callback() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda5
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean handleMessage;
                handleMessage = KeyboardLayoutManager.this.handleMessage(message);
                return handleMessage;
            }
        }, true);
    }

    public void systemRunning() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.input.KeyboardLayoutManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                KeyboardLayoutManager.this.updateKeyboardLayouts();
            }
        }, intentFilter, null, this.mHandler);
        this.mHandler.sendEmptyMessage(4);
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        Objects.requireNonNull(inputManager);
        inputManager.registerInputDeviceListener(this, this.mHandler);
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, inputManager.getInputDeviceIds()));
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int i) {
        onInputDeviceChanged(i);
        if (useNewSettingsUi()) {
            reloadKeyboardLayouts();
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
        this.mConfiguredKeyboards.remove(i);
        maybeUpdateNotification();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
        InputDevice inputDevice = getInputDevice(i);
        if (inputDevice == null || inputDevice.isVirtual() || !inputDevice.isFullKeyboard()) {
            return;
        }
        if (!useNewSettingsUi()) {
            synchronized (this.mDataStore) {
                if (getCurrentKeyboardLayoutForInputDevice(inputDevice.getIdentifier()) == null) {
                    String defaultKeyboardLayout = getDefaultKeyboardLayout(inputDevice);
                    if (defaultKeyboardLayout != null) {
                        setCurrentKeyboardLayoutForInputDevice(inputDevice.getIdentifier(), defaultKeyboardLayout);
                    } else {
                        this.mConfiguredKeyboards.put(inputDevice.getId(), new HashSet());
                    }
                }
            }
        } else {
            InputDeviceIdentifier identifier = inputDevice.getIdentifier();
            String layoutDescriptor = getLayoutDescriptor(identifier);
            HashSet hashSet = new HashSet();
            boolean z = false;
            for (ImeInfo imeInfo : getImeInfoListForLayoutMapping()) {
                String keyboardLayoutForInputDeviceInternal = getKeyboardLayoutForInputDeviceInternal(identifier, new ImeInfo(imeInfo.mUserId, imeInfo.mImeSubtypeHandle, imeInfo.mImeSubtype));
                if (keyboardLayoutForInputDeviceInternal == null) {
                    z = true;
                } else {
                    hashSet.add(keyboardLayoutForInputDeviceInternal);
                }
            }
            if (z) {
                hashSet.clear();
            }
            if (DEBUG) {
                Slog.d("KeyboardLayoutManager", "Layouts selected for input device: " + identifier + " -> selectedLayouts: " + hashSet);
            }
            this.mConfiguredKeyboards.set(inputDevice.getId(), hashSet);
            synchronized (this.mDataStore) {
                if (!this.mDataStore.setSelectedKeyboardLayouts(layoutDescriptor, hashSet)) {
                    this.mDataStore.saveIfNeeded();
                    return;
                }
                this.mDataStore.saveIfNeeded();
            }
        }
        maybeUpdateNotification();
    }

    public final String getDefaultKeyboardLayout(final InputDevice inputDevice) {
        final Locale locale = this.mContext.getResources().getConfiguration().locale;
        if (TextUtils.isEmpty(locale.getLanguage())) {
            return null;
        }
        final ArrayList<KeyboardLayout> arrayList = new ArrayList();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda0
            @Override // com.android.server.input.KeyboardLayoutManager.KeyboardLayoutVisitor
            public final void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout) {
                KeyboardLayoutManager.lambda$getDefaultKeyboardLayout$0(inputDevice, locale, arrayList, resources, i, keyboardLayout);
            }
        });
        if (arrayList.isEmpty()) {
            return null;
        }
        Collections.sort(arrayList);
        Iterator it = arrayList.iterator();
        while (true) {
            if (it.hasNext()) {
                KeyboardLayout keyboardLayout = (KeyboardLayout) it.next();
                LocaleList locales = keyboardLayout.getLocales();
                for (int i = 0; i < locales.size(); i++) {
                    Locale locale2 = locales.get(i);
                    if (locale2 != null && locale2.getCountry().equals(locale.getCountry()) && locale2.getVariant().equals(locale.getVariant())) {
                        return keyboardLayout.getDescriptor();
                    }
                }
            } else {
                for (KeyboardLayout keyboardLayout2 : arrayList) {
                    LocaleList locales2 = keyboardLayout2.getLocales();
                    for (int i2 = 0; i2 < locales2.size(); i2++) {
                        Locale locale3 = locales2.get(i2);
                        if (locale3 != null && locale3.getCountry().equals(locale.getCountry())) {
                            return keyboardLayout2.getDescriptor();
                        }
                    }
                }
                return ((KeyboardLayout) arrayList.get(0)).getDescriptor();
            }
        }
    }

    public static /* synthetic */ void lambda$getDefaultKeyboardLayout$0(InputDevice inputDevice, Locale locale, List list, Resources resources, int i, KeyboardLayout keyboardLayout) {
        if (keyboardLayout.getVendorId() == inputDevice.getVendorId() && keyboardLayout.getProductId() == inputDevice.getProductId()) {
            LocaleList locales = keyboardLayout.getLocales();
            for (int i2 = 0; i2 < locales.size(); i2++) {
                Locale locale2 = locales.get(i2);
                if (locale2 != null && isCompatibleLocale(locale, locale2)) {
                    list.add(keyboardLayout);
                    return;
                }
            }
        }
    }

    public static boolean isCompatibleLocale(Locale locale, Locale locale2) {
        if (locale.getLanguage().equals(locale2.getLanguage())) {
            return TextUtils.isEmpty(locale.getCountry()) || TextUtils.isEmpty(locale2.getCountry()) || locale.getCountry().equals(locale2.getCountry());
        }
        return false;
    }

    public final void updateKeyboardLayouts() {
        final HashSet hashSet = new HashSet();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda6
            @Override // com.android.server.input.KeyboardLayoutManager.KeyboardLayoutVisitor
            public final void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout) {
                KeyboardLayoutManager.lambda$updateKeyboardLayouts$1(hashSet, resources, i, keyboardLayout);
            }
        });
        synchronized (this.mDataStore) {
            this.mDataStore.removeUninstalledKeyboardLayouts(hashSet);
            this.mDataStore.saveIfNeeded();
        }
        synchronized (this.mKeyboardLayoutCache) {
            this.mKeyboardLayoutCache.clear();
        }
        reloadKeyboardLayouts();
    }

    public static /* synthetic */ void lambda$updateKeyboardLayouts$1(HashSet hashSet, Resources resources, int i, KeyboardLayout keyboardLayout) {
        hashSet.add(keyboardLayout.getDescriptor());
    }

    public KeyboardLayout[] getKeyboardLayouts() {
        final ArrayList arrayList = new ArrayList();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda7
            @Override // com.android.server.input.KeyboardLayoutManager.KeyboardLayoutVisitor
            public final void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout) {
                arrayList.add(keyboardLayout);
            }
        });
        return (KeyboardLayout[]) arrayList.toArray(new KeyboardLayout[0]);
    }

    public KeyboardLayout[] getKeyboardLayoutsForInputDevice(final InputDeviceIdentifier inputDeviceIdentifier) {
        if (useNewSettingsUi()) {
            return getKeyboardLayouts();
        }
        final String[] enabledKeyboardLayoutsForInputDevice = getEnabledKeyboardLayoutsForInputDevice(inputDeviceIdentifier);
        final ArrayList arrayList = new ArrayList(enabledKeyboardLayoutsForInputDevice.length);
        final ArrayList arrayList2 = new ArrayList();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() { // from class: com.android.server.input.KeyboardLayoutManager.2
            public boolean mHasSeenDeviceSpecificLayout;

            @Override // com.android.server.input.KeyboardLayoutManager.KeyboardLayoutVisitor
            public void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout) {
                String[] strArr;
                for (String str : enabledKeyboardLayoutsForInputDevice) {
                    if (str != null && str.equals(keyboardLayout.getDescriptor())) {
                        arrayList.add(keyboardLayout);
                        return;
                    }
                }
                if (keyboardLayout.getVendorId() == inputDeviceIdentifier.getVendorId() && keyboardLayout.getProductId() == inputDeviceIdentifier.getProductId()) {
                    if (!this.mHasSeenDeviceSpecificLayout) {
                        this.mHasSeenDeviceSpecificLayout = true;
                        arrayList2.clear();
                    }
                    arrayList2.add(keyboardLayout);
                } else if (keyboardLayout.getVendorId() == -1 && keyboardLayout.getProductId() == -1 && !this.mHasSeenDeviceSpecificLayout) {
                    arrayList2.add(keyboardLayout);
                }
            }
        });
        return (KeyboardLayout[]) Stream.concat(arrayList.stream(), arrayList2.stream()).toArray(new IntFunction() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda3
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                KeyboardLayout[] lambda$getKeyboardLayoutsForInputDevice$3;
                lambda$getKeyboardLayoutsForInputDevice$3 = KeyboardLayoutManager.lambda$getKeyboardLayoutsForInputDevice$3(i);
                return lambda$getKeyboardLayoutsForInputDevice$3;
            }
        });
    }

    public static /* synthetic */ KeyboardLayout[] lambda$getKeyboardLayoutsForInputDevice$3(int i) {
        return new KeyboardLayout[i];
    }

    public KeyboardLayout getKeyboardLayout(String str) {
        Objects.requireNonNull(str, "keyboardLayoutDescriptor must not be null");
        final KeyboardLayout[] keyboardLayoutArr = new KeyboardLayout[1];
        visitKeyboardLayout(str, new KeyboardLayoutVisitor() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda4
            @Override // com.android.server.input.KeyboardLayoutManager.KeyboardLayoutVisitor
            public final void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout) {
                KeyboardLayoutManager.lambda$getKeyboardLayout$4(keyboardLayoutArr, resources, i, keyboardLayout);
            }
        });
        if (keyboardLayoutArr[0] == null) {
            Slog.w("KeyboardLayoutManager", "Could not get keyboard layout with descriptor '" + str + "'.");
        }
        return keyboardLayoutArr[0];
    }

    public static /* synthetic */ void lambda$getKeyboardLayout$4(KeyboardLayout[] keyboardLayoutArr, Resources resources, int i, KeyboardLayout keyboardLayout) {
        keyboardLayoutArr[0] = keyboardLayout;
    }

    public final void visitAllKeyboardLayouts(KeyboardLayoutVisitor keyboardLayoutVisitor) {
        PackageManager packageManager = this.mContext.getPackageManager();
        for (ResolveInfo resolveInfo : packageManager.queryBroadcastReceiversAsUser(new Intent("android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS"), 786560, 0)) {
            visitKeyboardLayoutsInPackage(packageManager, resolveInfo.activityInfo, null, resolveInfo.priority, keyboardLayoutVisitor);
        }
    }

    public final void visitKeyboardLayout(String str, KeyboardLayoutVisitor keyboardLayoutVisitor) {
        KeyboardLayoutDescriptor parse = KeyboardLayoutDescriptor.parse(str);
        if (parse != null) {
            PackageManager packageManager = this.mContext.getPackageManager();
            try {
                visitKeyboardLayoutsInPackage(packageManager, packageManager.getReceiverInfo(new ComponentName(parse.packageName, parse.receiverName), 786560), parse.keyboardLayoutName, 0, keyboardLayoutVisitor);
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
    }

    public final void visitKeyboardLayoutsInPackage(PackageManager packageManager, ActivityInfo activityInfo, String str, int i, KeyboardLayoutVisitor keyboardLayoutVisitor) {
        String str2;
        int i2;
        int i3;
        TypedArray typedArray;
        Object obj = str;
        Bundle bundle = activityInfo.metaData;
        if (bundle == null) {
            return;
        }
        int i4 = bundle.getInt("android.hardware.input.metadata.KEYBOARD_LAYOUTS");
        if (i4 == 0) {
            Slog.w("KeyboardLayoutManager", "Missing meta-data 'android.hardware.input.metadata.KEYBOARD_LAYOUTS' on receiver " + activityInfo.packageName + "/" + activityInfo.name);
            return;
        }
        CharSequence loadLabel = activityInfo.loadLabel(packageManager);
        String charSequence = loadLabel != null ? loadLabel.toString() : "";
        ApplicationInfo applicationInfo = activityInfo.applicationInfo;
        int i5 = 1;
        int i6 = 0;
        int i7 = (applicationInfo.flags & 1) != 0 ? i : 0;
        try {
            Resources resourcesForApplication = packageManager.getResourcesForApplication(applicationInfo);
            XmlResourceParser xml = resourcesForApplication.getXml(i4);
            XmlUtils.beginDocument(xml, "keyboard-layouts");
            while (true) {
                XmlUtils.nextElement(xml);
                String name = xml.getName();
                if (name != null) {
                    if (name.equals("keyboard-layout")) {
                        TypedArray obtainAttributes = resourcesForApplication.obtainAttributes(xml, R.styleable.KeyboardLayout);
                        try {
                            String string = obtainAttributes.getString(i5);
                            String string2 = obtainAttributes.getString(i6);
                            int resourceId = obtainAttributes.getResourceId(2, i6);
                            LocaleList localesFromLanguageTags = getLocalesFromLanguageTags(obtainAttributes.getString(5));
                            int i8 = obtainAttributes.getInt(6, i6);
                            int i9 = obtainAttributes.getInt(4, -1);
                            int i10 = obtainAttributes.getInt(3, -1);
                            if (string != null && string2 != null && resourceId != 0) {
                                String format = KeyboardLayoutDescriptor.format(activityInfo.packageName, activityInfo.name, string);
                                try {
                                    if (obj != null && !string.equals(obj)) {
                                        str2 = charSequence;
                                        typedArray = obtainAttributes;
                                        i2 = i6;
                                        i3 = i5;
                                        typedArray.recycle();
                                    }
                                    keyboardLayoutVisitor.visitKeyboardLayout(resourcesForApplication, resourceId, new KeyboardLayout(format, string2, charSequence, i7, localesFromLanguageTags, i8, i9, i10));
                                    typedArray.recycle();
                                } catch (Throwable th) {
                                    th = th;
                                    typedArray.recycle();
                                    throw th;
                                }
                                str2 = charSequence;
                                typedArray = obtainAttributes;
                                i2 = i6;
                                i3 = i5;
                            }
                            str2 = charSequence;
                            typedArray = obtainAttributes;
                            i2 = i6;
                            i3 = i5;
                            Slog.w("KeyboardLayoutManager", "Missing required 'name', 'label' or 'keyboardLayout' attributes in keyboard layout resource from receiver " + activityInfo.packageName + "/" + activityInfo.name);
                            typedArray.recycle();
                        } catch (Throwable th2) {
                            th = th2;
                            typedArray = obtainAttributes;
                        }
                    } else {
                        str2 = charSequence;
                        i2 = i6;
                        i3 = i5;
                        Slog.w("KeyboardLayoutManager", "Skipping unrecognized element '" + name + "' in keyboard layout resource from receiver " + activityInfo.packageName + "/" + activityInfo.name);
                    }
                    charSequence = str2;
                    obj = str;
                    i6 = i2;
                    i5 = i3;
                } else {
                    xml.close();
                    return;
                }
            }
        } catch (Exception e) {
            Slog.w("KeyboardLayoutManager", "Could not parse keyboard layout resource from receiver " + activityInfo.packageName + "/" + activityInfo.name, e);
        }
    }

    public static LocaleList getLocalesFromLanguageTags(String str) {
        if (TextUtils.isEmpty(str)) {
            return LocaleList.getEmptyLocaleList();
        }
        return LocaleList.forLanguageTags(str.replace('|', ','));
    }

    public final String getLayoutDescriptor(InputDeviceIdentifier inputDeviceIdentifier) {
        Objects.requireNonNull(inputDeviceIdentifier, "identifier must not be null");
        Objects.requireNonNull(inputDeviceIdentifier.getDescriptor(), "descriptor must not be null");
        if (inputDeviceIdentifier.getVendorId() == 0 && inputDeviceIdentifier.getProductId() == 0) {
            return inputDeviceIdentifier.getDescriptor();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("vendor:");
        sb.append(inputDeviceIdentifier.getVendorId());
        sb.append(",product:");
        sb.append(inputDeviceIdentifier.getProductId());
        if (useNewSettingsUi()) {
            InputDevice inputDevice = getInputDevice(inputDeviceIdentifier);
            Objects.requireNonNull(inputDevice, "Input device must not be null");
            if (!TextUtils.isEmpty(inputDevice.getKeyboardLanguageTag())) {
                sb.append(",languageTag:");
                sb.append(inputDevice.getKeyboardLanguageTag());
            }
            if (!TextUtils.isEmpty(inputDevice.getKeyboardLayoutType())) {
                sb.append(",layoutType:");
                sb.append(inputDevice.getKeyboardLanguageTag());
            }
        }
        return sb.toString();
    }

    public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) {
        String currentKeyboardLayout;
        if (useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "getCurrentKeyboardLayoutForInputDevice API not supported");
            return null;
        }
        String layoutDescriptor = getLayoutDescriptor(inputDeviceIdentifier);
        synchronized (this.mDataStore) {
            currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(layoutDescriptor);
            if (currentKeyboardLayout == null && !layoutDescriptor.equals(inputDeviceIdentifier.getDescriptor())) {
                currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(inputDeviceIdentifier.getDescriptor());
            }
            if (DEBUG) {
                Slog.d("KeyboardLayoutManager", "getCurrentKeyboardLayoutForInputDevice() " + inputDeviceIdentifier.toString() + ": " + currentKeyboardLayout);
            }
        }
        return currentKeyboardLayout;
    }

    public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) {
        if (useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "setCurrentKeyboardLayoutForInputDevice API not supported");
            return;
        }
        Objects.requireNonNull(str, "keyboardLayoutDescriptor must not be null");
        String layoutDescriptor = getLayoutDescriptor(inputDeviceIdentifier);
        synchronized (this.mDataStore) {
            if (this.mDataStore.setCurrentKeyboardLayout(layoutDescriptor, str)) {
                if (DEBUG) {
                    Slog.d("KeyboardLayoutManager", "setCurrentKeyboardLayoutForInputDevice() " + inputDeviceIdentifier + " key: " + layoutDescriptor + " keyboardLayoutDescriptor: " + str);
                }
                this.mHandler.sendEmptyMessage(3);
            }
            this.mDataStore.saveIfNeeded();
        }
    }

    public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) {
        String[] keyboardLayouts;
        if (useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "getEnabledKeyboardLayoutsForInputDevice API not supported");
            return new String[0];
        }
        String layoutDescriptor = getLayoutDescriptor(inputDeviceIdentifier);
        synchronized (this.mDataStore) {
            keyboardLayouts = this.mDataStore.getKeyboardLayouts(layoutDescriptor);
            if ((keyboardLayouts == null || keyboardLayouts.length == 0) && !layoutDescriptor.equals(inputDeviceIdentifier.getDescriptor())) {
                keyboardLayouts = this.mDataStore.getKeyboardLayouts(inputDeviceIdentifier.getDescriptor());
            }
        }
        return keyboardLayouts;
    }

    public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) {
        if (useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "addKeyboardLayoutForInputDevice API not supported");
            return;
        }
        Objects.requireNonNull(str, "keyboardLayoutDescriptor must not be null");
        String layoutDescriptor = getLayoutDescriptor(inputDeviceIdentifier);
        synchronized (this.mDataStore) {
            String currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(layoutDescriptor);
            if (currentKeyboardLayout == null && !layoutDescriptor.equals(inputDeviceIdentifier.getDescriptor())) {
                currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(inputDeviceIdentifier.getDescriptor());
            }
            if (this.mDataStore.addKeyboardLayout(layoutDescriptor, str) && !Objects.equals(currentKeyboardLayout, this.mDataStore.getCurrentKeyboardLayout(layoutDescriptor))) {
                this.mHandler.sendEmptyMessage(3);
            }
            this.mDataStore.saveIfNeeded();
        }
    }

    public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) {
        if (useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "removeKeyboardLayoutForInputDevice API not supported");
            return;
        }
        Objects.requireNonNull(str, "keyboardLayoutDescriptor must not be null");
        String layoutDescriptor = getLayoutDescriptor(inputDeviceIdentifier);
        synchronized (this.mDataStore) {
            String currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(layoutDescriptor);
            if (currentKeyboardLayout == null && !layoutDescriptor.equals(inputDeviceIdentifier.getDescriptor())) {
                currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(inputDeviceIdentifier.getDescriptor());
            }
            boolean removeKeyboardLayout = this.mDataStore.removeKeyboardLayout(layoutDescriptor, str);
            if (!layoutDescriptor.equals(inputDeviceIdentifier.getDescriptor())) {
                removeKeyboardLayout |= this.mDataStore.removeKeyboardLayout(inputDeviceIdentifier.getDescriptor(), str);
            }
            if (removeKeyboardLayout && !Objects.equals(currentKeyboardLayout, this.mDataStore.getCurrentKeyboardLayout(layoutDescriptor))) {
                this.mHandler.sendEmptyMessage(3);
            }
            this.mDataStore.saveIfNeeded();
        }
    }

    public void switchKeyboardLayout(int i, int i2) {
        if (useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "switchKeyboardLayout API not supported");
        } else {
            this.mHandler.obtainMessage(2, i, i2).sendToTarget();
        }
    }

    public final void handleSwitchKeyboardLayout(int i, int i2) {
        boolean switchKeyboardLayout;
        String currentKeyboardLayout;
        KeyboardLayout keyboardLayout;
        InputDevice inputDevice = getInputDevice(i);
        if (inputDevice != null) {
            String layoutDescriptor = getLayoutDescriptor(inputDevice.getIdentifier());
            synchronized (this.mDataStore) {
                switchKeyboardLayout = this.mDataStore.switchKeyboardLayout(layoutDescriptor, i2);
                currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(layoutDescriptor);
                this.mDataStore.saveIfNeeded();
            }
            if (switchKeyboardLayout) {
                Toast toast = this.mSwitchedKeyboardLayoutToast;
                if (toast != null) {
                    toast.cancel();
                    this.mSwitchedKeyboardLayoutToast = null;
                }
                if (currentKeyboardLayout != null && (keyboardLayout = getKeyboardLayout(currentKeyboardLayout)) != null) {
                    Toast makeText = Toast.makeText(this.mContext, keyboardLayout.getLabel(), 0);
                    this.mSwitchedKeyboardLayoutToast = makeText;
                    makeText.show();
                }
                reloadKeyboardLayouts();
            }
        }
    }

    public String[] getKeyboardLayoutOverlay(InputDeviceIdentifier inputDeviceIdentifier) {
        String currentKeyboardLayoutForInputDevice;
        ImeInfo imeInfo;
        if (useNewSettingsUi()) {
            if (getInputDevice(inputDeviceIdentifier) == null || (imeInfo = this.mCurrentImeInfo) == null) {
                return null;
            }
            currentKeyboardLayoutForInputDevice = getKeyboardLayoutForInputDeviceInternal(inputDeviceIdentifier, imeInfo);
        } else {
            currentKeyboardLayoutForInputDevice = getCurrentKeyboardLayoutForInputDevice(inputDeviceIdentifier);
        }
        if (currentKeyboardLayoutForInputDevice == null) {
            return null;
        }
        final String[] strArr = new String[2];
        visitKeyboardLayout(currentKeyboardLayoutForInputDevice, new KeyboardLayoutVisitor() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda1
            @Override // com.android.server.input.KeyboardLayoutManager.KeyboardLayoutVisitor
            public final void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout) {
                KeyboardLayoutManager.lambda$getKeyboardLayoutOverlay$5(strArr, resources, i, keyboardLayout);
            }
        });
        if (strArr[0] == null) {
            Slog.w("KeyboardLayoutManager", "Could not get keyboard layout with descriptor '" + currentKeyboardLayoutForInputDevice + "'.");
            return null;
        }
        return strArr;
    }

    public static /* synthetic */ void lambda$getKeyboardLayoutOverlay$5(String[] strArr, Resources resources, int i, KeyboardLayout keyboardLayout) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resources.openRawResource(i));
            strArr[0] = keyboardLayout.getDescriptor();
            strArr[1] = Streams.readFully(inputStreamReader);
            inputStreamReader.close();
        } catch (Resources.NotFoundException | IOException unused) {
        }
    }

    public String getKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
        if (!useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "getKeyboardLayoutForInputDevice() API not supported");
            return null;
        }
        InputMethodSubtypeHandle of = InputMethodSubtypeHandle.of(inputMethodInfo, inputMethodSubtype);
        String keyboardLayoutForInputDeviceInternal = getKeyboardLayoutForInputDeviceInternal(inputDeviceIdentifier, new ImeInfo(i, of, inputMethodSubtype));
        if (DEBUG) {
            Slog.d("KeyboardLayoutManager", "getKeyboardLayoutForInputDevice() " + inputDeviceIdentifier.toString() + ", userId : " + i + ", subtypeHandle = " + of + " -> " + keyboardLayoutForInputDeviceInternal);
        }
        return keyboardLayoutForInputDeviceInternal;
    }

    public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype, String str) {
        if (!useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "setKeyboardLayoutForInputDevice() API not supported");
            return;
        }
        Objects.requireNonNull(str, "keyboardLayoutDescriptor must not be null");
        String createLayoutKey = createLayoutKey(inputDeviceIdentifier, i, InputMethodSubtypeHandle.of(inputMethodInfo, inputMethodSubtype));
        synchronized (this.mDataStore) {
            if (this.mDataStore.setKeyboardLayout(getLayoutDescriptor(inputDeviceIdentifier), createLayoutKey, str)) {
                if (DEBUG) {
                    Slog.d("KeyboardLayoutManager", "setKeyboardLayoutForInputDevice() " + inputDeviceIdentifier + " key: " + createLayoutKey + " keyboardLayoutDescriptor: " + str);
                }
                this.mHandler.sendEmptyMessage(3);
            }
            this.mDataStore.saveIfNeeded();
        }
    }

    public KeyboardLayout[] getKeyboardLayoutListForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
        if (!useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "getKeyboardLayoutListForInputDevice() API not supported");
            return new KeyboardLayout[0];
        }
        return getKeyboardLayoutListForInputDeviceInternal(inputDeviceIdentifier, new ImeInfo(i, InputMethodSubtypeHandle.of(inputMethodInfo, inputMethodSubtype), inputMethodSubtype));
    }

    public final KeyboardLayout[] getKeyboardLayoutListForInputDeviceInternal(final InputDeviceIdentifier inputDeviceIdentifier, ImeInfo imeInfo) {
        final String keyboardLayout;
        String canonicalizedLanguageTag;
        String createLayoutKey = createLayoutKey(inputDeviceIdentifier, imeInfo.mUserId, imeInfo.mImeSubtypeHandle);
        synchronized (this.mDataStore) {
            keyboardLayout = this.mDataStore.getKeyboardLayout(getLayoutDescriptor(inputDeviceIdentifier), createLayoutKey);
        }
        final ArrayList arrayList = new ArrayList();
        InputMethodSubtype inputMethodSubtype = imeInfo.mImeSubtype;
        if (inputMethodSubtype == null) {
            canonicalizedLanguageTag = "";
        } else {
            ULocale physicalKeyboardHintLanguageTag = inputMethodSubtype.getPhysicalKeyboardHintLanguageTag();
            if (physicalKeyboardHintLanguageTag != null) {
                canonicalizedLanguageTag = physicalKeyboardHintLanguageTag.toLanguageTag();
            } else {
                canonicalizedLanguageTag = imeInfo.mImeSubtype.getCanonicalizedLanguageTag();
            }
        }
        final String str = canonicalizedLanguageTag;
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() { // from class: com.android.server.input.KeyboardLayoutManager.3
            public boolean mDeviceSpecificLayoutAvailable;

            @Override // com.android.server.input.KeyboardLayoutManager.KeyboardLayoutVisitor
            public void visitKeyboardLayout(Resources resources, int i, KeyboardLayout keyboardLayout2) {
                if (keyboardLayout2.getVendorId() == inputDeviceIdentifier.getVendorId() && keyboardLayout2.getProductId() == inputDeviceIdentifier.getProductId()) {
                    if (!this.mDeviceSpecificLayoutAvailable) {
                        this.mDeviceSpecificLayoutAvailable = true;
                        arrayList.clear();
                    }
                    arrayList.add(keyboardLayout2);
                } else if (keyboardLayout2.getVendorId() == -1 && keyboardLayout2.getProductId() == -1 && !this.mDeviceSpecificLayoutAvailable && KeyboardLayoutManager.isLayoutCompatibleWithLanguageTag(keyboardLayout2, str)) {
                    arrayList.add(keyboardLayout2);
                } else if (keyboardLayout2.getDescriptor().equals(keyboardLayout)) {
                    arrayList.add(keyboardLayout2);
                }
            }
        });
        Collections.sort(arrayList);
        return (KeyboardLayout[]) arrayList.toArray(new KeyboardLayout[0]);
    }

    public void onInputMethodSubtypeChanged(int i, InputMethodSubtypeHandle inputMethodSubtypeHandle, InputMethodSubtype inputMethodSubtype) {
        if (!useNewSettingsUi()) {
            Slog.e("KeyboardLayoutManager", "onInputMethodSubtypeChanged() API not supported");
        } else if (inputMethodSubtypeHandle == null) {
            if (DEBUG) {
                Slog.d("KeyboardLayoutManager", "No InputMethod is running, ignoring change");
            }
        } else {
            ImeInfo imeInfo = this.mCurrentImeInfo;
            if (imeInfo != null && inputMethodSubtypeHandle.equals(imeInfo.mImeSubtypeHandle) && this.mCurrentImeInfo.mUserId == i) {
                return;
            }
            this.mCurrentImeInfo = new ImeInfo(i, inputMethodSubtypeHandle, inputMethodSubtype);
            this.mHandler.sendEmptyMessage(3);
            if (DEBUG) {
                Slog.d("KeyboardLayoutManager", "InputMethodSubtype changed: userId=" + i + " subtypeHandle=" + inputMethodSubtypeHandle);
            }
        }
    }

    public final String getKeyboardLayoutForInputDeviceInternal(InputDeviceIdentifier inputDeviceIdentifier, ImeInfo imeInfo) {
        String keyboardLayout;
        InputDevice inputDevice = getInputDevice(inputDeviceIdentifier);
        if (inputDevice == null || inputDevice.isVirtual() || !inputDevice.isFullKeyboard()) {
            return null;
        }
        String createLayoutKey = createLayoutKey(inputDeviceIdentifier, imeInfo.mUserId, imeInfo.mImeSubtypeHandle);
        synchronized (this.mDataStore) {
            keyboardLayout = this.mDataStore.getKeyboardLayout(getLayoutDescriptor(inputDeviceIdentifier), createLayoutKey);
        }
        if (keyboardLayout == null) {
            synchronized (this.mKeyboardLayoutCache) {
                if (this.mKeyboardLayoutCache.containsKey(createLayoutKey)) {
                    keyboardLayout = this.mKeyboardLayoutCache.get(createLayoutKey);
                } else {
                    String defaultKeyboardLayoutBasedOnImeInfo = getDefaultKeyboardLayoutBasedOnImeInfo(inputDevice, imeInfo, getKeyboardLayoutListForInputDeviceInternal(inputDeviceIdentifier, imeInfo));
                    this.mKeyboardLayoutCache.put(createLayoutKey, defaultKeyboardLayoutBasedOnImeInfo);
                    keyboardLayout = defaultKeyboardLayoutBasedOnImeInfo;
                }
            }
        }
        return keyboardLayout;
    }

    public static String getDefaultKeyboardLayoutBasedOnImeInfo(InputDevice inputDevice, ImeInfo imeInfo, KeyboardLayout[] keyboardLayoutArr) {
        String matchingLayoutForProvidedLanguageTagAndLayoutType;
        if (imeInfo.mImeSubtypeHandle == null) {
            return null;
        }
        Arrays.sort(keyboardLayoutArr);
        for (KeyboardLayout keyboardLayout : keyboardLayoutArr) {
            if (keyboardLayout.getVendorId() == inputDevice.getVendorId() && keyboardLayout.getProductId() == inputDevice.getProductId()) {
                if (DEBUG) {
                    Slog.d("KeyboardLayoutManager", "getDefaultKeyboardLayoutBasedOnImeInfo() : Layout found based on vendor and product Ids. " + inputDevice.getIdentifier() + " : " + keyboardLayout.getDescriptor());
                }
                return keyboardLayout.getDescriptor();
            }
        }
        String keyboardLanguageTag = inputDevice.getKeyboardLanguageTag();
        if (keyboardLanguageTag != null && (matchingLayoutForProvidedLanguageTagAndLayoutType = getMatchingLayoutForProvidedLanguageTagAndLayoutType(keyboardLayoutArr, keyboardLanguageTag, inputDevice.getKeyboardLayoutType())) != null) {
            if (DEBUG) {
                Slog.d("KeyboardLayoutManager", "getDefaultKeyboardLayoutBasedOnImeInfo() : Layout found based on HW information (Language tag and Layout type). " + inputDevice.getIdentifier() + " : " + matchingLayoutForProvidedLanguageTagAndLayoutType);
            }
            return matchingLayoutForProvidedLanguageTagAndLayoutType;
        }
        InputMethodSubtype inputMethodSubtype = imeInfo.mImeSubtype;
        if (inputMethodSubtype == null) {
            return null;
        }
        ULocale physicalKeyboardHintLanguageTag = inputMethodSubtype.getPhysicalKeyboardHintLanguageTag();
        String matchingLayoutForProvidedLanguageTagAndLayoutType2 = getMatchingLayoutForProvidedLanguageTagAndLayoutType(keyboardLayoutArr, physicalKeyboardHintLanguageTag != null ? physicalKeyboardHintLanguageTag.toLanguageTag() : inputMethodSubtype.getCanonicalizedLanguageTag(), inputMethodSubtype.getPhysicalKeyboardHintLayoutType());
        if (DEBUG) {
            Slog.d("KeyboardLayoutManager", "getDefaultKeyboardLayoutBasedOnImeInfo() : Layout found based on IME locale matching. " + inputDevice.getIdentifier() + " : " + matchingLayoutForProvidedLanguageTagAndLayoutType2);
        }
        return matchingLayoutForProvidedLanguageTagAndLayoutType2;
    }

    public static String getMatchingLayoutForProvidedLanguageTagAndLayoutType(KeyboardLayout[] keyboardLayoutArr, String str, String str2) {
        str2 = (str2 == null || !KeyboardLayout.isLayoutTypeValid(str2)) ? "undefined" : "undefined";
        ArrayList arrayList = new ArrayList();
        for (KeyboardLayout keyboardLayout : keyboardLayoutArr) {
            if (keyboardLayout.getLayoutType().equals(str2)) {
                arrayList.add(keyboardLayout);
            }
        }
        String matchingLayoutForProvidedLanguageTag = getMatchingLayoutForProvidedLanguageTag(arrayList, str);
        return matchingLayoutForProvidedLanguageTag != null ? matchingLayoutForProvidedLanguageTag : getMatchingLayoutForProvidedLanguageTag(Arrays.asList(keyboardLayoutArr), str);
    }

    public static String getMatchingLayoutForProvidedLanguageTag(List<KeyboardLayout> list, String str) {
        Locale forLanguageTag = Locale.forLanguageTag(str);
        String str2 = null;
        String str3 = null;
        for (KeyboardLayout keyboardLayout : list) {
            LocaleList locales = keyboardLayout.getLocales();
            for (int i = 0; i < locales.size(); i++) {
                Locale locale = locales.get(i);
                if (locale != null && locale.getLanguage().equals(forLanguageTag.getLanguage())) {
                    if (str3 == null) {
                        str3 = keyboardLayout.getDescriptor();
                    }
                    if (locale.getCountry().equals(forLanguageTag.getCountry())) {
                        if (str2 == null) {
                            str2 = keyboardLayout.getDescriptor();
                        }
                        if (locale.getVariant().equals(forLanguageTag.getVariant())) {
                            return keyboardLayout.getDescriptor();
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return str2 != null ? str2 : str3;
    }

    public final void reloadKeyboardLayouts() {
        if (DEBUG) {
            Slog.d("KeyboardLayoutManager", "Reloading keyboard layouts.");
        }
        this.mNative.reloadKeyboardLayouts();
    }

    public final void maybeUpdateNotification() {
        if (this.mConfiguredKeyboards.size() == 0) {
            hideKeyboardLayoutNotification();
            return;
        }
        for (int i = 0; i < this.mConfiguredKeyboards.size(); i++) {
            if (this.mConfiguredKeyboards.valueAt(i).isEmpty()) {
                showMissingKeyboardLayoutNotification();
                return;
            }
        }
        showConfiguredKeyboardLayoutNotification();
    }

    public final void showMissingKeyboardLayoutNotification() {
        Resources resources = this.mContext.getResources();
        String string = resources.getString(17041485);
        if (this.mConfiguredKeyboards.size() == 1) {
            InputDevice inputDevice = getInputDevice(this.mConfiguredKeyboards.keyAt(0));
            if (inputDevice == null) {
                return;
            }
            showKeyboardLayoutNotification(resources.getString(17041486, inputDevice.getName()), string, inputDevice);
            return;
        }
        showKeyboardLayoutNotification(resources.getString(17041488), string, null);
    }

    public final void showKeyboardLayoutNotification(String str, String str2, InputDevice inputDevice) {
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        if (notificationManager == null) {
            return;
        }
        Intent intent = new Intent("android.settings.HARD_KEYBOARD_SETTINGS");
        if (inputDevice != null) {
            intent.putExtra("input_device_identifier", (Parcelable) inputDevice.getIdentifier());
        }
        intent.setFlags(337641472);
        notificationManager.notifyAsUser(null, 19, new Notification.Builder(this.mContext, SystemNotificationChannels.PHYSICAL_KEYBOARD).setContentTitle(str).setContentText(str2).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent, 67108864, null, UserHandle.CURRENT)).setSmallIcon(17302854).setColor(this.mContext.getColor(17170460)).setAutoCancel(true).build(), UserHandle.ALL);
    }

    public final void hideKeyboardLayoutNotification() {
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        if (notificationManager == null) {
            return;
        }
        notificationManager.cancelAsUser(null, 19, UserHandle.ALL);
    }

    public final void showConfiguredKeyboardLayoutNotification() {
        Resources resources = this.mContext.getResources();
        if (this.mConfiguredKeyboards.size() != 1) {
            showKeyboardLayoutNotification(resources.getString(17040489), resources.getString(17040488), null);
            return;
        }
        InputDevice inputDevice = getInputDevice(this.mConfiguredKeyboards.keyAt(0));
        Set<String> valueAt = this.mConfiguredKeyboards.valueAt(0);
        if (inputDevice == null || valueAt == null || valueAt.isEmpty()) {
            return;
        }
        showKeyboardLayoutNotification(resources.getString(17040491, inputDevice.getName()), createConfiguredNotificationText(this.mContext, valueAt), inputDevice);
    }

    public final String createConfiguredNotificationText(Context context, Set<String> set) {
        Resources resources = context.getResources();
        final ArrayList arrayList = new ArrayList();
        set.forEach(new Consumer() { // from class: com.android.server.input.KeyboardLayoutManager$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                KeyboardLayoutManager.this.lambda$createConfiguredNotificationText$6(arrayList, (String) obj);
            }
        });
        Collections.sort(arrayList);
        int size = arrayList.size();
        if (size != 1) {
            if (size != 2) {
                if (size == 3) {
                    return resources.getString(17040492, arrayList.get(0), arrayList.get(1), arrayList.get(2));
                }
                return resources.getString(17040487, arrayList.get(0), arrayList.get(1), arrayList.get(2));
            }
            return resources.getString(17040493, arrayList.get(0), arrayList.get(1));
        }
        return resources.getString(17040490, arrayList.get(0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createConfiguredNotificationText$6(List list, String str) {
        list.add(getKeyboardLayout(str).getLabel());
    }

    public final boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            for (int i2 : (int[]) message.obj) {
                onInputDeviceAdded(i2);
            }
            return true;
        } else if (i == 2) {
            handleSwitchKeyboardLayout(message.arg1, message.arg2);
            return true;
        } else if (i == 3) {
            reloadKeyboardLayouts();
            return true;
        } else if (i != 4) {
            return false;
        } else {
            updateKeyboardLayouts();
            return true;
        }
    }

    public final boolean useNewSettingsUi() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_new_keyboard_ui");
    }

    public final InputDevice getInputDevice(int i) {
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        if (inputManager != null) {
            return inputManager.getInputDevice(i);
        }
        return null;
    }

    public final InputDevice getInputDevice(InputDeviceIdentifier inputDeviceIdentifier) {
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        if (inputManager != null) {
            return inputManager.getInputDeviceByDescriptor(inputDeviceIdentifier.getDescriptor());
        }
        return null;
    }

    public final List<ImeInfo> getImeInfoListForLayoutMapping() {
        ArrayList arrayList = new ArrayList();
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        Objects.requireNonNull(userManager);
        InputMethodManager inputMethodManager = (InputMethodManager) this.mContext.getSystemService(InputMethodManager.class);
        Objects.requireNonNull(inputMethodManager);
        for (UserHandle userHandle : userManager.getUserHandles(true)) {
            int identifier = userHandle.getIdentifier();
            for (InputMethodInfo inputMethodInfo : inputMethodManager.getEnabledInputMethodListAsUser(identifier)) {
                for (InputMethodSubtype inputMethodSubtype : inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true)) {
                    if (inputMethodSubtype.isSuitableForPhysicalKeyboardLayoutMapping()) {
                        arrayList.add(new ImeInfo(identifier, InputMethodSubtypeHandle.of(inputMethodInfo, inputMethodSubtype), inputMethodSubtype));
                    }
                }
            }
        }
        return arrayList;
    }

    public final String createLayoutKey(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodSubtypeHandle inputMethodSubtypeHandle) {
        Objects.requireNonNull(inputMethodSubtypeHandle, "subtypeHandle must not be null");
        return "layoutDescriptor:" + getLayoutDescriptor(inputDeviceIdentifier) + ",userId:" + i + ",subtypeHandle:" + inputMethodSubtypeHandle.toStringHandle();
    }

    public static boolean isLayoutCompatibleWithLanguageTag(KeyboardLayout keyboardLayout, String str) {
        int[] code;
        int[] code2 = UScript.getCode(Locale.forLanguageTag(str));
        if (code2.length == 0) {
            return true;
        }
        LocaleList locales = keyboardLayout.getLocales();
        if (locales.isEmpty()) {
            return true;
        }
        for (int i = 0; i < locales.size(); i++) {
            Locale locale = locales.get(i);
            if (locale != null && (code = UScript.getCode(locale)) != null && haveCommonValue(code, code2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean haveCommonValue(int[] iArr, int[] iArr2) {
        for (int i : iArr) {
            for (int i2 : iArr2) {
                if (i == i2) {
                    return true;
                }
            }
        }
        return false;
    }

    /* loaded from: classes.dex */
    public static final class KeyboardLayoutDescriptor {
        public String keyboardLayoutName;
        public String packageName;
        public String receiverName;

        public static String format(String str, String str2, String str3) {
            return str + "/" + str2 + "/" + str3;
        }

        public static KeyboardLayoutDescriptor parse(String str) {
            int i;
            int indexOf;
            int i2;
            int indexOf2 = str.indexOf(47);
            if (indexOf2 < 0 || (i = indexOf2 + 1) == str.length() || (indexOf = str.indexOf(47, i)) < indexOf2 + 2 || (i2 = indexOf + 1) == str.length()) {
                return null;
            }
            KeyboardLayoutDescriptor keyboardLayoutDescriptor = new KeyboardLayoutDescriptor();
            keyboardLayoutDescriptor.packageName = str.substring(0, indexOf2);
            keyboardLayoutDescriptor.receiverName = str.substring(i, indexOf);
            keyboardLayoutDescriptor.keyboardLayoutName = str.substring(i2);
            return keyboardLayoutDescriptor;
        }
    }

    /* loaded from: classes.dex */
    public static class ImeInfo {
        public InputMethodSubtype mImeSubtype;
        public InputMethodSubtypeHandle mImeSubtypeHandle;
        public int mUserId;

        public ImeInfo(int i, InputMethodSubtypeHandle inputMethodSubtypeHandle, InputMethodSubtype inputMethodSubtype) {
            this.mUserId = i;
            this.mImeSubtypeHandle = inputMethodSubtypeHandle;
            this.mImeSubtype = inputMethodSubtype;
        }
    }
}
