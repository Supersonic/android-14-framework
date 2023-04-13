package com.android.server.policy;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.RemoteException;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import com.android.internal.policy.IShortcutService;
import com.android.internal.util.XmlUtils;
import com.android.server.p014wm.StartingSurfaceController;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class ModifierShortcutManager {
    public static SparseArray<String> sApplicationLaunchKeyCategories;
    public final Context mContext;
    public final SparseArray<ShortcutInfo> mIntentShortcuts = new SparseArray<>();
    public final SparseArray<ShortcutInfo> mShiftShortcuts = new SparseArray<>();
    public LongSparseArray<IShortcutService> mShortcutKeyServices = new LongSparseArray<>();
    public boolean mSearchKeyShortcutPending = false;
    public boolean mConsumeSearchKeyUp = true;

    static {
        SparseArray<String> sparseArray = new SparseArray<>();
        sApplicationLaunchKeyCategories = sparseArray;
        sparseArray.append(64, "android.intent.category.APP_BROWSER");
        sApplicationLaunchKeyCategories.append(65, "android.intent.category.APP_EMAIL");
        sApplicationLaunchKeyCategories.append(207, "android.intent.category.APP_CONTACTS");
        sApplicationLaunchKeyCategories.append(208, "android.intent.category.APP_CALENDAR");
        sApplicationLaunchKeyCategories.append(209, "android.intent.category.APP_MUSIC");
        sApplicationLaunchKeyCategories.append(210, "android.intent.category.APP_CALCULATOR");
    }

    public ModifierShortcutManager(Context context) {
        this.mContext = context;
        loadShortcuts();
    }

    public final Intent getIntent(KeyCharacterMap keyCharacterMap, int i, int i2) {
        char lowerCase;
        boolean metaStateHasModifiers = KeyEvent.metaStateHasModifiers(i2, 1);
        if (metaStateHasModifiers || KeyEvent.metaStateHasNoModifiers(i2)) {
            SparseArray<ShortcutInfo> sparseArray = metaStateHasModifiers ? this.mShiftShortcuts : this.mIntentShortcuts;
            int i3 = keyCharacterMap.get(i, i2);
            ShortcutInfo shortcutInfo = i3 != 0 ? sparseArray.get(i3) : null;
            if (shortcutInfo == null && (lowerCase = Character.toLowerCase(keyCharacterMap.getDisplayLabel(i))) != 0) {
                shortcutInfo = sparseArray.get(lowerCase);
            }
            if (shortcutInfo != null) {
                return shortcutInfo.intent;
            }
            return null;
        }
        return null;
    }

    public final void loadShortcuts() {
        Intent intent;
        String str;
        ActivityInfo activityInfo;
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            XmlResourceParser xml = this.mContext.getResources().getXml(18284548);
            XmlUtils.beginDocument(xml, "bookmarks");
            while (true) {
                XmlUtils.nextElement(xml);
                boolean z = true;
                if (xml.getEventType() != 1 && "bookmark".equals(xml.getName())) {
                    String attributeValue = xml.getAttributeValue(null, "package");
                    String attributeValue2 = xml.getAttributeValue(null, "class");
                    String attributeValue3 = xml.getAttributeValue(null, "shortcut");
                    String attributeValue4 = xml.getAttributeValue(null, "category");
                    String attributeValue5 = xml.getAttributeValue(null, "shift");
                    if (TextUtils.isEmpty(attributeValue3)) {
                        Log.w(StartingSurfaceController.TAG, "Unable to get shortcut for: " + attributeValue + "/" + attributeValue2);
                    } else {
                        char charAt = attributeValue3.charAt(0);
                        if (attributeValue5 == null || !attributeValue5.equals("true")) {
                            z = false;
                        }
                        if (attributeValue != null && attributeValue2 != null) {
                            ComponentName componentName = new ComponentName(attributeValue, attributeValue2);
                            try {
                                activityInfo = packageManager.getActivityInfo(componentName, 794624);
                            } catch (PackageManager.NameNotFoundException unused) {
                                ComponentName componentName2 = new ComponentName(packageManager.canonicalToCurrentPackageNames(new String[]{attributeValue})[0], attributeValue2);
                                try {
                                    activityInfo = packageManager.getActivityInfo(componentName2, 794624);
                                    componentName = componentName2;
                                } catch (PackageManager.NameNotFoundException unused2) {
                                    Log.w(StartingSurfaceController.TAG, "Unable to add bookmark: " + attributeValue + "/" + attributeValue2 + " not found.");
                                }
                            }
                            intent = new Intent("android.intent.action.MAIN");
                            intent.addCategory("android.intent.category.LAUNCHER");
                            intent.setComponent(componentName);
                            str = activityInfo.loadLabel(packageManager).toString();
                        } else if (attributeValue4 != null) {
                            intent = Intent.makeMainSelectorActivity("android.intent.action.MAIN", attributeValue4);
                            str = "";
                        } else {
                            Log.w(StartingSurfaceController.TAG, "Unable to add bookmark for shortcut " + attributeValue3 + ": missing package/class or category attributes");
                        }
                        ShortcutInfo shortcutInfo = new ShortcutInfo(str, intent);
                        if (z) {
                            this.mShiftShortcuts.put(charAt, shortcutInfo);
                        } else {
                            this.mIntentShortcuts.put(charAt, shortcutInfo);
                        }
                    }
                }
                return;
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e(StartingSurfaceController.TAG, "Got exception parsing bookmarks.", e);
        }
    }

    public void registerShortcutKey(long j, IShortcutService iShortcutService) throws RemoteException {
        IShortcutService iShortcutService2 = this.mShortcutKeyServices.get(j);
        if (iShortcutService2 != null && iShortcutService2.asBinder().pingBinder()) {
            throw new RemoteException("Key already exists.");
        }
        this.mShortcutKeyServices.put(j, iShortcutService);
    }

    public final boolean handleShortcutService(int i, int i2) {
        long j = i;
        if ((i2 & IInstalld.FLAG_USE_QUOTA) != 0) {
            j |= 17592186044416L;
        }
        if ((i2 & 2) != 0) {
            j |= 8589934592L;
        }
        if ((i2 & 1) != 0) {
            j |= 4294967296L;
        }
        if ((65536 & i2) != 0) {
            j |= 281474976710656L;
        }
        IShortcutService iShortcutService = this.mShortcutKeyServices.get(j);
        if (iShortcutService != null) {
            try {
                iShortcutService.notifyShortcutKeyPressed(j);
                return true;
            } catch (RemoteException unused) {
                this.mShortcutKeyServices.delete(j);
                return true;
            }
        }
        return false;
    }

    public final boolean handleIntentShortcut(KeyCharacterMap keyCharacterMap, int i, int i2) {
        if (this.mSearchKeyShortcutPending) {
            if (!keyCharacterMap.isPrintingKey(i)) {
                return false;
            }
            this.mConsumeSearchKeyUp = true;
            this.mSearchKeyShortcutPending = false;
        } else if ((458752 & i2) == 0) {
            String str = sApplicationLaunchKeyCategories.get(i);
            if (str != null) {
                Intent makeMainSelectorActivity = Intent.makeMainSelectorActivity("android.intent.action.MAIN", str);
                makeMainSelectorActivity.setFlags(268435456);
                try {
                    this.mContext.startActivityAsUser(makeMainSelectorActivity, UserHandle.CURRENT);
                } catch (ActivityNotFoundException unused) {
                    Slog.w(StartingSurfaceController.TAG, "Dropping application launch key because the activity to which it is registered was not found: keyCode=" + KeyEvent.keyCodeToString(i) + ", category=" + str);
                }
                return true;
            }
            return false;
        } else {
            i2 &= -458753;
        }
        Intent intent = getIntent(keyCharacterMap, i, i2);
        if (intent != null) {
            intent.addFlags(268435456);
            try {
                this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
            } catch (ActivityNotFoundException unused2) {
                Slog.w(StartingSurfaceController.TAG, "Dropping shortcut key combination because the activity to which it is registered was not found: META+ or SEARCH" + KeyEvent.keyCodeToString(i));
            }
            return true;
        }
        return false;
    }

    public boolean interceptKey(KeyEvent keyEvent) {
        if (keyEvent.getRepeatCount() != 0) {
            return false;
        }
        int modifiers = keyEvent.getModifiers();
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 84) {
            if (keyEvent.getAction() == 0) {
                this.mSearchKeyShortcutPending = true;
                this.mConsumeSearchKeyUp = false;
            } else {
                this.mSearchKeyShortcutPending = false;
                if (this.mConsumeSearchKeyUp) {
                    this.mConsumeSearchKeyUp = false;
                    return true;
                }
            }
            return false;
        } else if (keyEvent.getAction() != 0) {
            return false;
        } else {
            return handleIntentShortcut(keyEvent.getKeyCharacterMap(), keyCode, modifiers) || handleShortcutService(keyCode, modifiers);
        }
    }

    /* loaded from: classes2.dex */
    public static final class ShortcutInfo {
        public final Intent intent;
        public final String title;

        public ShortcutInfo(String str, Intent intent) {
            this.title = str;
            this.intent = intent;
        }
    }
}
