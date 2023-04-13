package com.android.server.p008om;

import android.content.Context;
import android.content.om.FabricatedOverlay;
import android.content.om.IOverlayManager;
import android.content.om.OverlayIdentifier;
import android.content.om.OverlayInfo;
import android.content.om.OverlayManagerTransaction;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.util.Xml;
import com.android.modules.utils.TypedXmlPullParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.om.OverlayManagerShellCommand */
/* loaded from: classes2.dex */
public final class OverlayManagerShellCommand extends ShellCommand {
    public static final Map<String, Integer> TYPE_MAP = Map.of("color", 28, "string", 3, "drawable", -1);
    public final Context mContext;
    public final IOverlayManager mInterface;

    public OverlayManagerShellCommand(Context context, IOverlayManager iOverlayManager) {
        this.mContext = context;
        this.mInterface = iOverlayManager;
    }

    public int onCommand(String str) {
        char c;
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        try {
            switch (str.hashCode()) {
                case -1361113425:
                    if (str.equals("set-priority")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -1298848381:
                    if (str.equals("enable")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -1097094790:
                    if (str.equals("lookup")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -794624300:
                    if (str.equals("enable-exclusive")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 3322014:
                    if (str.equals("list")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 1671308008:
                    if (str.equals("disable")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 2016903117:
                    if (str.equals("fabricate")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    return runList();
                case 1:
                    return runEnableDisable(true);
                case 2:
                    return runEnableDisable(false);
                case 3:
                    return runEnableExclusive();
                case 4:
                    return runSetPriority();
                case 5:
                    return runLookup();
                case 6:
                    return runFabricate();
                default:
                    return handleDefaultCommands(str);
            }
        } catch (RemoteException e) {
            errPrintWriter.println("Remote exception: " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            errPrintWriter.println("Error: " + e2.getMessage());
            return -1;
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Overlay manager (overlay) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  dump [--verbose] [--user USER_ID] [[FIELD] PACKAGE[:NAME]]");
        outPrintWriter.println("    Print debugging information about the overlay manager.");
        outPrintWriter.println("    With optional parameters PACKAGE and NAME, limit output to the specified");
        outPrintWriter.println("    overlay or target. With optional parameter FIELD, limit output to");
        outPrintWriter.println("    the corresponding SettingsItem field. Field names are all lower case");
        outPrintWriter.println("    and omit the m prefix, i.e. 'userid' for SettingsItem.mUserId.");
        outPrintWriter.println("  list [--user USER_ID] [PACKAGE[:NAME]]");
        outPrintWriter.println("    Print information about target and overlay packages.");
        outPrintWriter.println("    Overlay packages are printed in priority order. With optional");
        outPrintWriter.println("    parameters PACKAGE and NAME, limit output to the specified overlay or");
        outPrintWriter.println("    target.");
        outPrintWriter.println("  enable [--user USER_ID] PACKAGE[:NAME]");
        outPrintWriter.println("    Enable overlay within or owned by PACKAGE with optional unique NAME.");
        outPrintWriter.println("  disable [--user USER_ID] PACKAGE[:NAME]");
        outPrintWriter.println("    Disable overlay within or owned by PACKAGE with optional unique NAME.");
        outPrintWriter.println("  enable-exclusive [--user USER_ID] [--category] PACKAGE");
        outPrintWriter.println("    Enable overlay within or owned by PACKAGE and disable all other overlays");
        outPrintWriter.println("    for its target package. If the --category option is given, only disables");
        outPrintWriter.println("    other overlays in the same category.");
        outPrintWriter.println("  set-priority [--user USER_ID] PACKAGE PARENT|lowest|highest");
        outPrintWriter.println("    Change the priority of the overlay to be just higher than");
        outPrintWriter.println("    the priority of PARENT If PARENT is the special keyword");
        outPrintWriter.println("    'lowest', change priority of PACKAGE to the lowest priority.");
        outPrintWriter.println("    If PARENT is the special keyword 'highest', change priority of");
        outPrintWriter.println("    PACKAGE to the highest priority.");
        outPrintWriter.println("  lookup [--user USER_ID] [--verbose] PACKAGE-TO-LOAD PACKAGE:TYPE/NAME");
        outPrintWriter.println("    Load a package and print the value of a given resource");
        outPrintWriter.println("    applying the current configuration and enabled overlays.");
        outPrintWriter.println("    For a more fine-grained alternative, use 'idmap2 lookup'.");
        outPrintWriter.println("  fabricate [--user USER_ID] [--target-name OVERLAYABLE] --target PACKAGE");
        outPrintWriter.println("            --name NAME [--file FILE] ");
        outPrintWriter.println("            PACKAGE:TYPE/NAME ENCODED-TYPE-ID/TYPE-NAME ENCODED-VALUE");
        outPrintWriter.println("    Create an overlay from a single resource. Caller must be root. Example:");
        outPrintWriter.println("      fabricate --target android --name LighterGray \\");
        outPrintWriter.println("                android:color/lighter_gray 0x1c 0xffeeeeee");
    }

    public final int runList() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        PrintWriter errPrintWriter = getErrPrintWriter();
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (!nextOption.equals("--user")) {
                    errPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
                i = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                String nextArg = getNextArg();
                if (nextArg != null) {
                    List overlayInfosForTarget = this.mInterface.getOverlayInfosForTarget(nextArg, i);
                    if (overlayInfosForTarget.isEmpty()) {
                        OverlayInfo overlayInfo = this.mInterface.getOverlayInfo(nextArg, i);
                        if (overlayInfo != null) {
                            printListOverlay(outPrintWriter, overlayInfo);
                        }
                        return 0;
                    }
                    outPrintWriter.println(nextArg);
                    int size = overlayInfosForTarget.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        printListOverlay(outPrintWriter, (OverlayInfo) overlayInfosForTarget.get(i2));
                    }
                    return 0;
                }
                Map allOverlays = this.mInterface.getAllOverlays(i);
                for (String str : allOverlays.keySet()) {
                    outPrintWriter.println(str);
                    List list = (List) allOverlays.get(str);
                    int size2 = list.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        printListOverlay(outPrintWriter, (OverlayInfo) list.get(i3));
                    }
                    outPrintWriter.println();
                }
                return 0;
            }
        }
    }

    public final void printListOverlay(PrintWriter printWriter, OverlayInfo overlayInfo) {
        int i = overlayInfo.state;
        printWriter.println(String.format("%s %s", i != 2 ? (i == 3 || i == 6) ? "[x]" : "---" : "[ ]", overlayInfo.getOverlayIdentifier()));
    }

    public final int runEnableDisable(boolean z) throws RemoteException {
        PrintWriter errPrintWriter = getErrPrintWriter();
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    errPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
            } else {
                this.mInterface.commit(new OverlayManagerTransaction.Builder().setEnabled(OverlayIdentifier.fromString(getNextArgRequired()), z, i).build());
                return 0;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0065, code lost:
        if (r6.equals("--target-name") == false) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runFabricate() throws RemoteException {
        PrintWriter errPrintWriter = getErrPrintWriter();
        if (Binder.getCallingUid() != 0) {
            errPrintWriter.println("Error: must be root to fabricate overlays through the shell");
            return 1;
        }
        String str = null;
        String str2 = "";
        String str3 = null;
        String str4 = "";
        String str5 = str4;
        while (true) {
            String nextOption = getNextOption();
            char c = 0;
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case -935414873:
                        break;
                    case 1045221602:
                        if (nextOption.equals("--config")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333013276:
                        if (nextOption.equals("--file")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333243947:
                        if (nextOption.equals("--name")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1519107889:
                        if (nextOption.equals("--target")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        str5 = getNextArgRequired();
                        break;
                    case 1:
                        str3 = getNextArgRequired();
                        break;
                    case 2:
                        str = getNextArgRequired();
                        break;
                    case 3:
                        str2 = getNextArgRequired();
                        break;
                    case 4:
                        UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    case 5:
                        str4 = getNextArgRequired();
                        break;
                    default:
                        errPrintWriter.println("Error: Unknown option: " + nextOption);
                        return 1;
                }
            } else if (str2.isEmpty()) {
                errPrintWriter.println("Error: Missing required arg '--name'");
                return 1;
            } else if (str4.isEmpty()) {
                errPrintWriter.println("Error: Missing required arg '--target'");
                return 1;
            } else if (str != null && getRemainingArgsCount() > 0) {
                errPrintWriter.println("Error: When passing --file don't pass resource name, type, and value as well");
                return 1;
            } else {
                FabricatedOverlay.Builder targetOverlayable = new FabricatedOverlay.Builder("com.android.shell", str2, str4).setTargetOverlayable(str5);
                if (str != null) {
                    int addOverlayValuesFromXml = addOverlayValuesFromXml(targetOverlayable, str4, str);
                    if (addOverlayValuesFromXml != 0) {
                        return addOverlayValuesFromXml;
                    }
                } else if (addOverlayValue(targetOverlayable, getNextArgRequired(), getNextArgRequired(), String.join(" ", peekRemainingArgs()), str3) != 0) {
                    return 1;
                }
                this.mInterface.commit(new OverlayManagerTransaction.Builder().registerFabricatedOverlay(targetOverlayable.build()).build());
                return 0;
            }
        }
    }

    public final int addOverlayValuesFromXml(FabricatedOverlay.Builder builder, String str, String str2) {
        PrintWriter errPrintWriter = getErrPrintWriter();
        File file = new File(str2);
        if (!file.exists()) {
            errPrintWriter.println("Error: File does not exist");
            return 1;
        } else if (!file.canRead()) {
            errPrintWriter.println("Error: File is unreadable");
            return 1;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(fileInputStream);
                while (true) {
                    int next = resolvePullParser.next();
                    if (next == 2 || next == 1) {
                        break;
                    }
                }
                resolvePullParser.require(2, (String) null, "overlay");
                while (true) {
                    int next2 = resolvePullParser.next();
                    if (next2 == 1) {
                        fileInputStream.close();
                        return 0;
                    } else if (next2 == 2) {
                        String name = resolvePullParser.getName();
                        if (!name.equals("item")) {
                            errPrintWriter.println(TextUtils.formatSimple("Error: Unexpected tag: %s at line %d", new Object[]{name, Integer.valueOf(resolvePullParser.getLineNumber())}));
                        } else if (!resolvePullParser.isEmptyElementTag()) {
                            errPrintWriter.println("Error: item tag must be empty");
                            fileInputStream.close();
                            return 1;
                        } else {
                            String attributeValue = resolvePullParser.getAttributeValue((String) null, "target");
                            if (TextUtils.isEmpty(attributeValue)) {
                                errPrintWriter.println("Error: target name missing at line " + resolvePullParser.getLineNumber());
                                fileInputStream.close();
                                return 1;
                            }
                            int indexOf = attributeValue.indexOf(47);
                            if (indexOf < 0) {
                                errPrintWriter.println("Error: target malformed, missing '/' at line " + resolvePullParser.getLineNumber());
                                fileInputStream.close();
                                return 1;
                            }
                            String substring = attributeValue.substring(0, indexOf);
                            String attributeValue2 = resolvePullParser.getAttributeValue((String) null, "value");
                            if (TextUtils.isEmpty(attributeValue2)) {
                                errPrintWriter.println("Error: value missing at line " + resolvePullParser.getLineNumber());
                                fileInputStream.close();
                                return 1;
                            }
                            String attributeValue3 = resolvePullParser.getAttributeValue((String) null, "config");
                            if (addOverlayValue(builder, str + ':' + attributeValue, substring, attributeValue2, attributeValue3) != 0) {
                                fileInputStream.close();
                                return 1;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            } catch (XmlPullParserException e2) {
                e2.printStackTrace();
                return 1;
            }
        }
    }

    public final int addOverlayValue(FabricatedOverlay.Builder builder, String str, String str2, String str3, String str4) {
        int parseUnsignedInt;
        int parseUnsignedInt2;
        String lowerCase = str2.toLowerCase(Locale.getDefault());
        Map<String, Integer> map = TYPE_MAP;
        if (map.containsKey(lowerCase)) {
            parseUnsignedInt = map.get(lowerCase).intValue();
        } else if (lowerCase.startsWith("0x")) {
            parseUnsignedInt = Integer.parseUnsignedInt(lowerCase.substring(2), 16);
        } else {
            parseUnsignedInt = Integer.parseUnsignedInt(lowerCase);
        }
        if (parseUnsignedInt == 3) {
            builder.setResourceValue(str, parseUnsignedInt, str3, str4);
            return 0;
        } else if (parseUnsignedInt < 0) {
            builder.setResourceValue(str, openFileForSystem(str3, "r"), str4);
            return 0;
        } else {
            if (str3.startsWith("0x")) {
                parseUnsignedInt2 = Integer.parseUnsignedInt(str3.substring(2), 16);
            } else {
                parseUnsignedInt2 = Integer.parseUnsignedInt(str3);
            }
            builder.setResourceValue(str, parseUnsignedInt, parseUnsignedInt2, str4);
            return 0;
        }
    }

    public final int runEnableExclusive() throws RemoteException {
        PrintWriter errPrintWriter = getErrPrintWriter();
        boolean z = false;
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--category")) {
                    z = true;
                } else if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    errPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
            } else {
                String nextArgRequired = getNextArgRequired();
                return z ? !this.mInterface.setEnabledExclusiveInCategory(nextArgRequired, i) ? 1 : 0 : !this.mInterface.setEnabledExclusive(nextArgRequired, true, i) ? 1 : 0;
            }
        }
    }

    public final int runSetPriority() throws RemoteException {
        PrintWriter errPrintWriter = getErrPrintWriter();
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    errPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
            } else {
                String nextArgRequired = getNextArgRequired();
                String nextArgRequired2 = getNextArgRequired();
                return "highest".equals(nextArgRequired2) ? !this.mInterface.setHighestPriority(nextArgRequired, i) ? 1 : 0 : "lowest".equals(nextArgRequired2) ? !this.mInterface.setLowestPriority(nextArgRequired, i) ? 1 : 0 : !this.mInterface.setPriority(nextArgRequired, nextArgRequired2, i) ? 1 : 0;
            }
        }
    }

    public final int runLookup() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        PrintWriter errPrintWriter = getErrPrintWriter();
        int i = 0;
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else if (!nextOption.equals("--verbose")) {
                    errPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                } else {
                    z = true;
                }
            } else {
                String nextArgRequired = getNextArgRequired();
                String nextArgRequired2 = getNextArgRequired();
                Matcher matcher = Pattern.compile("(.*?):(.*?)/(.*?)").matcher(nextArgRequired2);
                if (!matcher.matches()) {
                    errPrintWriter.println("Error: bad resource name, doesn't match package:type/name");
                    return 1;
                }
                try {
                    Resources resourcesForApplication = this.mContext.createContextAsUser(UserHandle.of(i), 0).getPackageManager().getResourcesForApplication(nextArgRequired);
                    AssetManager assets = resourcesForApplication.getAssets();
                    try {
                        assets.setResourceResolutionLoggingEnabled(true);
                        try {
                            TypedValue typedValue = new TypedValue();
                            resourcesForApplication.getValue(nextArgRequired2, typedValue, false);
                            CharSequence coerceToString = typedValue.coerceToString();
                            String lastResourceResolution = assets.getLastResourceResolution();
                            resourcesForApplication.getValue(nextArgRequired2, typedValue, true);
                            CharSequence coerceToString2 = typedValue.coerceToString();
                            if (z) {
                                outPrintWriter.println(lastResourceResolution);
                            }
                            if (coerceToString.equals(coerceToString2)) {
                                outPrintWriter.println(coerceToString);
                            } else {
                                outPrintWriter.println(((Object) coerceToString) + " -> " + ((Object) coerceToString2));
                            }
                            return 0;
                        } catch (Resources.NotFoundException unused) {
                            int identifier = resourcesForApplication.getIdentifier(matcher.group(3), matcher.group(2), matcher.group(1));
                            if (identifier == 0) {
                                throw new Resources.NotFoundException();
                            }
                            TypedArray obtainTypedArray = resourcesForApplication.obtainTypedArray(identifier);
                            if (z) {
                                outPrintWriter.println(assets.getLastResourceResolution());
                            }
                            TypedValue typedValue2 = new TypedValue();
                            for (int i2 = 0; i2 < obtainTypedArray.length(); i2++) {
                                obtainTypedArray.getValue(i2, typedValue2);
                                outPrintWriter.println(typedValue2.coerceToString());
                            }
                            obtainTypedArray.recycle();
                            return 0;
                        }
                    } catch (Resources.NotFoundException unused2) {
                        errPrintWriter.println("Error: failed to get the resource " + nextArgRequired2);
                        return 1;
                    } finally {
                        assets.setResourceResolutionLoggingEnabled(false);
                    }
                } catch (PackageManager.NameNotFoundException unused3) {
                    errPrintWriter.println(String.format("Error: failed to get resources for package %s for user %d", nextArgRequired, Integer.valueOf(i)));
                    return 1;
                }
            }
        }
    }
}
