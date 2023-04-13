package com.android.server.graphics.fonts;

import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontUpdateRequest;
import android.graphics.fonts.FontVariationAxis;
import android.graphics.fonts.SystemFonts;
import android.os.Binder;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.ShellCommand;
import android.text.FontConfig;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.DumpUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.server.graphics.fonts.FontManagerService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class FontManagerShellCommand extends ShellCommand {
    public final FontManagerService mService;

    public FontManagerShellCommand(FontManagerService fontManagerService) {
        this.mService = fontManagerService;
    }

    public int onCommand(String str) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 0 && callingUid != 2000) {
            getErrPrintWriter().println("Only shell or root user can execute font command.");
            return 1;
        }
        return execCommand(this, str);
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Font service (font) commands");
        outPrintWriter.println("help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println();
        outPrintWriter.println("dump [family name]");
        outPrintWriter.println("    Dump all font files in the specified family name.");
        outPrintWriter.println("    Dump current system font configuration if no family name was specified.");
        outPrintWriter.println();
        outPrintWriter.println("update [font file path] [signature file path]");
        outPrintWriter.println("    Update installed font files with new font file.");
        outPrintWriter.println();
        outPrintWriter.println("update-family [family definition XML path]");
        outPrintWriter.println("    Update font families with the new definitions.");
        outPrintWriter.println();
        outPrintWriter.println("install-debug-cert [cert file path]");
        outPrintWriter.println("    Install debug certificate file. This command can be used only on userdebug");
        outPrintWriter.println("    or eng device with root user.");
        outPrintWriter.println();
        outPrintWriter.println("clear");
        outPrintWriter.println("    Remove all installed font files and reset to the initial state.");
        outPrintWriter.println();
        outPrintWriter.println("restart");
        outPrintWriter.println("    Restart FontManagerService emulating device reboot.");
        outPrintWriter.println("    WARNING: this is not a safe operation. Other processes may misbehave if");
        outPrintWriter.println("    they are using fonts updated by FontManagerService.");
        outPrintWriter.println("    This command exists merely for testing.");
        outPrintWriter.println();
        outPrintWriter.println("status");
        outPrintWriter.println("    Prints status of current system font configuration.");
    }

    public void dumpAll(IndentingPrintWriter indentingPrintWriter) {
        dumpFontConfig(indentingPrintWriter, this.mService.getSystemFontConfig());
    }

    public final void dumpSingleFontConfig(IndentingPrintWriter indentingPrintWriter, FontConfig.Font font) {
        StringBuilder sb = new StringBuilder();
        sb.append("style = ");
        sb.append(font.getStyle());
        sb.append(", path = ");
        sb.append(font.getFile().getAbsolutePath());
        if (font.getTtcIndex() != 0) {
            sb.append(", index = ");
            sb.append(font.getTtcIndex());
        }
        if (!font.getFontVariationSettings().isEmpty()) {
            sb.append(", axes = ");
            sb.append(font.getFontVariationSettings());
        }
        if (font.getFontFamilyName() != null) {
            sb.append(", fallback = ");
            sb.append(font.getFontFamilyName());
        }
        indentingPrintWriter.println(sb.toString());
        if (font.getOriginalFile() != null) {
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Font is updated from " + font.getOriginalFile());
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final void dumpFontConfig(IndentingPrintWriter indentingPrintWriter, FontConfig fontConfig) {
        List fontFamilies = fontConfig.getFontFamilies();
        indentingPrintWriter.println("Named Family List");
        indentingPrintWriter.increaseIndent();
        List namedFamilyLists = fontConfig.getNamedFamilyLists();
        for (int i = 0; i < namedFamilyLists.size(); i++) {
            FontConfig.NamedFamilyList namedFamilyList = (FontConfig.NamedFamilyList) namedFamilyLists.get(i);
            indentingPrintWriter.println("Named Family (" + namedFamilyList.getName() + ")");
            indentingPrintWriter.increaseIndent();
            List families = namedFamilyList.getFamilies();
            for (int i2 = 0; i2 < families.size(); i2++) {
                indentingPrintWriter.println("Family");
                List fontList = ((FontConfig.FontFamily) families.get(i2)).getFontList();
                indentingPrintWriter.increaseIndent();
                for (int i3 = 0; i3 < fontList.size(); i3++) {
                    dumpSingleFontConfig(indentingPrintWriter, (FontConfig.Font) fontList.get(i3));
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Dump Fallback Families");
        indentingPrintWriter.increaseIndent();
        int i4 = 0;
        for (int i5 = 0; i5 < fontFamilies.size(); i5++) {
            FontConfig.FontFamily fontFamily = (FontConfig.FontFamily) fontFamilies.get(i5);
            if (fontFamily.getName() == null) {
                StringBuilder sb = new StringBuilder("Fallback Family [");
                int i6 = i4 + 1;
                sb.append(i4);
                sb.append("]: lang=\"");
                sb.append(fontFamily.getLocaleList().toLanguageTags());
                sb.append("\"");
                if (fontFamily.getVariant() != 0) {
                    sb.append(", variant=");
                    int variant = fontFamily.getVariant();
                    if (variant == 1) {
                        sb.append("Compact");
                    } else if (variant == 2) {
                        sb.append("Elegant");
                    } else {
                        sb.append("Unknown");
                    }
                }
                indentingPrintWriter.println(sb.toString());
                List fontList2 = fontFamily.getFontList();
                indentingPrintWriter.increaseIndent();
                for (int i7 = 0; i7 < fontList2.size(); i7++) {
                    dumpSingleFontConfig(indentingPrintWriter, (FontConfig.Font) fontList2.get(i7));
                }
                indentingPrintWriter.decreaseIndent();
                i4 = i6;
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Dump Family Aliases");
        indentingPrintWriter.increaseIndent();
        List aliases = fontConfig.getAliases();
        for (int i8 = 0; i8 < aliases.size(); i8++) {
            FontConfig.Alias alias = (FontConfig.Alias) aliases.get(i8);
            indentingPrintWriter.println("alias = " + alias.getName() + ", reference = " + alias.getOriginal() + ", width = " + alias.getWeight());
        }
        indentingPrintWriter.decreaseIndent();
    }

    public final void dumpFallback(IndentingPrintWriter indentingPrintWriter, FontFamily[] fontFamilyArr) {
        for (FontFamily fontFamily : fontFamilyArr) {
            dumpFamily(indentingPrintWriter, fontFamily);
        }
    }

    public final void dumpFamily(IndentingPrintWriter indentingPrintWriter, FontFamily fontFamily) {
        StringBuilder sb = new StringBuilder("Family:");
        if (fontFamily.getLangTags() != null) {
            sb.append(" langTag = ");
            sb.append(fontFamily.getLangTags());
        }
        if (fontFamily.getVariant() != 0) {
            sb.append(" variant = ");
            int variant = fontFamily.getVariant();
            if (variant == 1) {
                sb.append("Compact");
            } else if (variant == 2) {
                sb.append("Elegant");
            } else {
                sb.append("UNKNOWN");
            }
        }
        indentingPrintWriter.println(sb.toString());
        for (int i = 0; i < fontFamily.getSize(); i++) {
            indentingPrintWriter.increaseIndent();
            try {
                dumpFont(indentingPrintWriter, fontFamily.getFont(i));
                indentingPrintWriter.decreaseIndent();
            } catch (Throwable th) {
                indentingPrintWriter.decreaseIndent();
                throw th;
            }
        }
    }

    public final void dumpFont(IndentingPrintWriter indentingPrintWriter, Font font) {
        File file = font.getFile();
        StringBuilder sb = new StringBuilder();
        sb.append(font.getStyle());
        sb.append(", path = ");
        sb.append(file == null ? "[Not a file]" : file.getAbsolutePath());
        if (font.getTtcIndex() != 0) {
            sb.append(", index = ");
            sb.append(font.getTtcIndex());
        }
        FontVariationAxis[] axes = font.getAxes();
        if (axes != null && axes.length != 0) {
            sb.append(", axes = \"");
            sb.append(FontVariationAxis.toFontVariationSettings(axes));
            sb.append("\"");
        }
        indentingPrintWriter.println(sb.toString());
    }

    public final void writeCommandResult(ShellCommand shellCommand, FontManagerService.SystemFontException systemFontException) {
        PrintWriter errPrintWriter = shellCommand.getErrPrintWriter();
        errPrintWriter.println(systemFontException.getErrorCode());
        errPrintWriter.println(systemFontException.getMessage());
        Slog.e("FontManagerShellCommand", "Command failed: " + Arrays.toString(shellCommand.getAllArgs()), systemFontException);
    }

    public final int dump(ShellCommand shellCommand) {
        if (DumpUtils.checkDumpPermission(this.mService.getContext(), "FontManagerShellCommand", shellCommand.getErrPrintWriter())) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(shellCommand.getOutPrintWriter(), "  ");
            String nextArg = shellCommand.getNextArg();
            FontConfig systemFontConfig = this.mService.getSystemFontConfig();
            if (nextArg == null) {
                dumpFontConfig(indentingPrintWriter, systemFontConfig);
                return 0;
            }
            FontFamily[] fontFamilyArr = (FontFamily[]) SystemFonts.buildSystemFallback(systemFontConfig).get(nextArg);
            if (fontFamilyArr == null) {
                indentingPrintWriter.println("Font Family \"" + nextArg + "\" not found");
                return 0;
            }
            dumpFallback(indentingPrintWriter, fontFamilyArr);
            return 0;
        }
        return 1;
    }

    public final int installCert(ShellCommand shellCommand) throws FontManagerService.SystemFontException {
        if (!Build.IS_USERDEBUG && !Build.IS_ENG) {
            throw new SecurityException("Only userdebug/eng device can add debug certificate");
        }
        if (Binder.getCallingUid() != 0) {
            throw new SecurityException("Only root can add debug certificate");
        }
        String nextArg = shellCommand.getNextArg();
        if (nextArg == null) {
            throw new FontManagerService.SystemFontException(-10008, "Cert file path argument is required.");
        }
        File file = new File(nextArg);
        if (!file.isFile()) {
            throw new FontManagerService.SystemFontException(-10008, "Cert file (" + file + ") is not found");
        }
        this.mService.addDebugCertificate(nextArg);
        this.mService.restart();
        shellCommand.getOutPrintWriter().println("Success");
        return 0;
    }

    public final int update(ShellCommand shellCommand) throws FontManagerService.SystemFontException {
        ParcelFileDescriptor openFileForSystem;
        ParcelFileDescriptor openFileForSystem2;
        String nextArg = shellCommand.getNextArg();
        if (nextArg == null) {
            throw new FontManagerService.SystemFontException(-10003, "Font file path argument is required.");
        }
        String nextArg2 = shellCommand.getNextArg();
        if (nextArg2 == null) {
            throw new FontManagerService.SystemFontException(-10003, "Signature file argument is required.");
        }
        try {
            openFileForSystem = shellCommand.openFileForSystem(nextArg, "r");
            openFileForSystem2 = shellCommand.openFileForSystem(nextArg2, "r");
        } catch (IOException e) {
            Slog.w("FontManagerShellCommand", "Error while closing files", e);
        }
        try {
            if (openFileForSystem != null) {
                if (openFileForSystem2 == null) {
                    throw new FontManagerService.SystemFontException(-10002, "Failed to open signature file");
                }
                try {
                    FileInputStream fileInputStream = new FileInputStream(openFileForSystem2.getFileDescriptor());
                    try {
                        int available = fileInputStream.available();
                        if (available > 8192) {
                            throw new FontManagerService.SystemFontException(-10005, "Signature file is too large");
                        }
                        byte[] bArr = new byte[available];
                        if (fileInputStream.read(bArr, 0, available) != available) {
                            throw new FontManagerService.SystemFontException(-10004, "Invalid read length");
                        }
                        fileInputStream.close();
                        this.mService.update(-1, Collections.singletonList(new FontUpdateRequest(openFileForSystem, bArr)));
                        openFileForSystem2.close();
                        openFileForSystem.close();
                        shellCommand.getOutPrintWriter().println("Success");
                        return 0;
                    } catch (Throwable th) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (IOException e2) {
                    throw new FontManagerService.SystemFontException(-10004, "Failed to read signature file.", e2);
                }
            }
            throw new FontManagerService.SystemFontException(-10001, "Failed to open font file");
        } catch (Throwable th3) {
            if (openFileForSystem2 != null) {
                try {
                    openFileForSystem2.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
            }
            throw th3;
        }
    }

    public final int updateFamily(ShellCommand shellCommand) throws FontManagerService.SystemFontException {
        String nextArg = shellCommand.getNextArg();
        if (nextArg == null) {
            throw new FontManagerService.SystemFontException(-10003, "XML file path argument is required.");
        }
        try {
            ParcelFileDescriptor openFileForSystem = shellCommand.openFileForSystem(nextArg, "r");
            List<FontUpdateRequest> parseFontFamilyUpdateXml = parseFontFamilyUpdateXml(new FileInputStream(openFileForSystem.getFileDescriptor()));
            openFileForSystem.close();
            this.mService.update(-1, parseFontFamilyUpdateXml);
            shellCommand.getOutPrintWriter().println("Success");
            return 0;
        } catch (IOException e) {
            throw new FontManagerService.SystemFontException(-10006, "Failed to open XML file.", e);
        }
    }

    public static List<FontUpdateRequest> parseFontFamilyUpdateXml(InputStream inputStream) throws FontManagerService.SystemFontException {
        try {
            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
            ArrayList arrayList = new ArrayList();
            while (true) {
                int next = resolvePullParser.next();
                if (next == 1) {
                    return arrayList;
                }
                if (next == 2) {
                    int depth = resolvePullParser.getDepth();
                    String name = resolvePullParser.getName();
                    if (depth == 1) {
                        if (!"fontFamilyUpdateRequest".equals(name)) {
                            throw new FontManagerService.SystemFontException(-10007, "Expected <fontFamilyUpdateRequest> but got: " + name);
                        }
                    } else if (depth != 2) {
                        continue;
                    } else if ("family".equals(name)) {
                        arrayList.add(new FontUpdateRequest(FontUpdateRequest.Family.readFromXml(resolvePullParser)));
                    } else {
                        throw new FontManagerService.SystemFontException(-10007, "Expected <family> but got: " + name);
                    }
                }
            }
        } catch (IOException | XmlPullParserException e) {
            throw new FontManagerService.SystemFontException(0, "Failed to parse xml", e);
        }
    }

    public final int clear(ShellCommand shellCommand) {
        this.mService.clearUpdates();
        shellCommand.getOutPrintWriter().println("Success");
        return 0;
    }

    public final int restart(ShellCommand shellCommand) {
        this.mService.restart();
        shellCommand.getOutPrintWriter().println("Success");
        return 0;
    }

    public final int status(ShellCommand shellCommand) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(shellCommand.getOutPrintWriter(), "  ");
        FontConfig systemFontConfig = this.mService.getSystemFontConfig();
        indentingPrintWriter.println("Current Version: " + systemFontConfig.getConfigVersion());
        LocalDateTime ofEpochSecond = LocalDateTime.ofEpochSecond(systemFontConfig.getLastModifiedTimeMillis(), 0, ZoneOffset.UTC);
        indentingPrintWriter.println("Last Modified Date: " + ofEpochSecond.format(DateTimeFormatter.ISO_DATE_TIME));
        Map<String, File> fontFileMap = this.mService.getFontFileMap();
        indentingPrintWriter.println("Number of updated font files: " + fontFileMap.size());
        return 0;
    }

    public final int execCommand(ShellCommand shellCommand, String str) {
        char c;
        if (str == null) {
            return shellCommand.handleDefaultCommands((String) null);
        }
        try {
            switch (str.hashCode()) {
                case -2084349744:
                    if (str.equals("install-debug-cert")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case -892481550:
                    if (str.equals("status")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -838846263:
                    if (str.equals("update")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 3095028:
                    if (str.equals("dump")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 94746189:
                    if (str.equals("clear")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1097506319:
                    if (str.equals("restart")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 1135462632:
                    if (str.equals("update-family")) {
                        c = 2;
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
                    return dump(shellCommand);
                case 1:
                    return update(shellCommand);
                case 2:
                    return updateFamily(shellCommand);
                case 3:
                    return clear(shellCommand);
                case 4:
                    return restart(shellCommand);
                case 5:
                    return status(shellCommand);
                case 6:
                    return installCert(shellCommand);
                default:
                    return shellCommand.handleDefaultCommands(str);
            }
        } catch (FontManagerService.SystemFontException e) {
            writeCommandResult(shellCommand, e);
            return 1;
        }
    }
}
