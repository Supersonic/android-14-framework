package com.android.commands.incident.sections;

import android.util.proto.ProtoOutputStream;
import com.android.commands.incident.ExecutionException;
import com.android.commands.incident.IncidentHelper;
import com.android.commands.incident.Section;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class PersistLogSection implements Section {
    private static final String DEFAULT_BUFFER = "main";
    private static final String LOG_DIR = "/data/misc/logd/";
    private static final Map<Character, Integer> LOG_PRIORITY_MAP;
    private static final Map<String, Long> SECTION_NAME_TO_ID;
    private static final String TAG = "IH_PersistLog";
    private long mCurrFieldId;
    private Map<Integer, Long> mEpochTimeCache = new HashMap();
    private long mMaxBytes = Long.MAX_VALUE;
    private ProtoOutputStream mProto;
    private static final Pattern LOG_FILE_RE = Pattern.compile("logcat(\\.\\d+)?");
    private static final Pattern BUFFER_BEGIN_RE = Pattern.compile("--------- (?:beginning of|switch to) (.*)");

    static {
        HashMap hashMap = new HashMap();
        SECTION_NAME_TO_ID = hashMap;
        HashMap hashMap2 = new HashMap();
        LOG_PRIORITY_MAP = hashMap2;
        hashMap.put(DEFAULT_BUFFER, 2246267895809L);
        hashMap.put("radio", 2246267895810L);
        hashMap.put("events", 2246267895811L);
        hashMap.put("system", 2246267895812L);
        hashMap.put("crash", 2246267895813L);
        hashMap.put("kernel", 2246267895816L);
        hashMap2.put('V', 2);
        hashMap2.put('D', 3);
        hashMap2.put('I', 4);
        hashMap2.put('W', 5);
        hashMap2.put('E', 6);
        hashMap2.put('F', 7);
        hashMap2.put('S', 8);
    }

    @Override // com.android.commands.incident.Section
    public void run(InputStream in, OutputStream out, List<String> args) throws ExecutionException {
        parseArgs(args);
        Path logDirPath = Paths.get(LOG_DIR, new String[0]);
        if (!Files.exists(logDirPath, new LinkOption[0])) {
            IncidentHelper.log(5, TAG, "Skip dump. " + logDirPath + " does not exist.");
        } else if (!Files.isReadable(logDirPath)) {
            IncidentHelper.log(5, TAG, "Skip dump. " + logDirPath + " is not readable.");
        } else {
            this.mProto = new ProtoOutputStream(out);
            setCurrentSection(DEFAULT_BUFFER);
            final Matcher logFileRe = LOG_FILE_RE.matcher("");
            try {
                try {
                    Stream<File> stream = Files.list(logDirPath).map(new Function() { // from class: com.android.commands.incident.sections.PersistLogSection$$ExternalSyntheticLambda1
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            return ((Path) obj).toFile();
                        }
                    }).filter(new Predicate() { // from class: com.android.commands.incident.sections.PersistLogSection$$ExternalSyntheticLambda2
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            return PersistLogSection.lambda$run$0(logFileRe, (File) obj);
                        }
                    }).sorted(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.commands.incident.sections.PersistLogSection$$ExternalSyntheticLambda3
                        @Override // java.util.function.ToLongFunction
                        public final long applyAsLong(Object obj) {
                            return ((File) obj).lastModified();
                        }
                    }).reversed());
                    try {
                        List<File> filesToProcess = new ArrayList<>();
                        long sumBytes = 0;
                        for (File file : stream) {
                            sumBytes += file.length();
                            if (sumBytes > this.mMaxBytes) {
                                break;
                            }
                            filesToProcess.add(file);
                        }
                        IncidentHelper.log(4, TAG, "Limit # log files to " + filesToProcess.size());
                        filesToProcess.stream().sorted(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.commands.incident.sections.PersistLogSection$$ExternalSyntheticLambda3
                            @Override // java.util.function.ToLongFunction
                            public final long applyAsLong(Object obj) {
                                return ((File) obj).lastModified();
                            }
                        })).forEachOrdered(new Consumer() { // from class: com.android.commands.incident.sections.PersistLogSection$$ExternalSyntheticLambda4
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                PersistLogSection.this.processFile((File) obj);
                            }
                        });
                        if (stream != null) {
                            stream.close();
                        }
                        this.mProto.flush();
                        IncidentHelper.log(3, TAG, "Bytes written: " + this.mProto.getBytes().length);
                    } catch (Throwable th) {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    this.mProto.flush();
                    throw th3;
                }
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$run$0(Matcher logFileRe, File f) {
        return (f.isDirectory() || match(logFileRe, f.getName()) == null) ? false : true;
    }

    private void parseArgs(List<String> args) {
        Iterator<String> iter = args.iterator();
        while (iter.hasNext()) {
            String arg = iter.next();
            if ("--limit".equals(arg) && iter.hasNext()) {
                String sizeStr = iter.next().toLowerCase();
                if (sizeStr.endsWith("mb")) {
                    this.mMaxBytes = Long.parseLong(sizeStr.replace("mb", "")) * 1024 * 1024;
                } else if (sizeStr.endsWith("kb")) {
                    this.mMaxBytes = Long.parseLong(sizeStr.replace("kb", "")) * 1024;
                } else {
                    this.mMaxBytes = Long.parseLong(sizeStr);
                }
            } else {
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processFile(File file) {
        Matcher bufferBeginRe = BUFFER_BEGIN_RE.matcher("");
        try {
            BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                Matcher m = match(bufferBeginRe, line);
                if (m != null) {
                    setCurrentSection(m.group(1));
                } else {
                    parseLine(line);
                }
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            IncidentHelper.log(5, TAG, "Error reading \"" + file + "\": " + e.getMessage());
        }
        IncidentHelper.log(3, TAG, "Finished reading " + file);
    }

    private void setCurrentSection(String sectionName) {
        Map<String, Long> map = SECTION_NAME_TO_ID;
        Long sectionId = map.get(sectionName);
        if (sectionId == null) {
            IncidentHelper.log(5, TAG, "Section does not exist: " + sectionName);
            sectionId = map.get(DEFAULT_BUFFER);
        }
        this.mCurrFieldId = sectionId.longValue();
    }

    private void parseLine(String line) {
        long token = this.mProto.start(this.mCurrFieldId);
        try {
            this.mProto.write(1116691496961L, getEpochSec(line));
            this.mProto.write(1116691496962L, parseInt(line, 15, 21) * 1000);
            int start = nextNonBlank(line, 21);
            int end = line.indexOf(32, start + 1);
            this.mProto.write(1120986464261L, parseInt(line, start, end));
            int start2 = nextNonBlank(line, end);
            int end2 = line.indexOf(32, start2 + 1);
            this.mProto.write(1120986464262L, parseInt(line, start2, end2));
            int start3 = nextNonBlank(line, end2);
            char priority = line.charAt(start3);
            this.mProto.write(1159641169923L, LOG_PRIORITY_MAP.getOrDefault(Character.valueOf(priority), 1).intValue());
            int start4 = nextNonBlank(line, start3 + 1);
            int end3 = line.indexOf(": ", start4);
            this.mProto.write(1138166333447L, line.substring(start4, end3).trim());
            this.mProto.write(1138166333448L, line.substring(Math.min(end3 + 2, line.length())));
        } catch (RuntimeException e) {
            this.mProto.write(1116691496961L, System.currentTimeMillis() / 1000);
            this.mProto.write(1159641169923L, 6);
            this.mProto.write(1138166333447L, TAG);
            this.mProto.write(1138166333448L, "Error parsing \"" + line + "\": " + e.getMessage());
        }
        this.mProto.end(token);
    }

    private static int nextNonBlank(String line, int start) {
        for (int i = start; i < line.length(); i++) {
            if (line.charAt(i) != ' ') {
                return i;
            }
        }
        return -1;
    }

    private long getEpochSec(String line) {
        final int month = (getDigit(line, 0) * 10) + getDigit(line, 1);
        final int day = (getDigit(line, 3) * 10) + getDigit(line, 4);
        int mmdd = (month * 100) + day;
        long epochSecBase = this.mEpochTimeCache.computeIfAbsent(Integer.valueOf(mmdd), new Function() { // from class: com.android.commands.incident.sections.PersistLogSection$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return PersistLogSection.lambda$getEpochSec$1(month, day, (Integer) obj);
            }
        }).longValue();
        int hh = (getDigit(line, 6) * 10) + getDigit(line, 7);
        int mm = (getDigit(line, 9) * 10) + getDigit(line, 10);
        int ss = (getDigit(line, 12) * 10) + getDigit(line, 13);
        return (hh * 3600) + epochSecBase + (mm * 60) + ss;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Long lambda$getEpochSec$1(int month, int day, Integer key) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2, ((month + 12) - 1) % 12);
        calendar.set(5, day);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            calendar.roll(1, -1);
        }
        return Long.valueOf(calendar.getTimeInMillis() / 1000);
    }

    private static int parseInt(String line, int start, int end) {
        int num = 0;
        for (int i = start; i < end; i++) {
            num = (num * 10) + getDigit(line, i);
        }
        return num;
    }

    private static int getDigit(String str, int pos) {
        int digit = str.charAt(pos) - '0';
        if (digit < 0 || digit > 9) {
            throw new NumberFormatException("'" + str.charAt(pos) + "' is not a digit.");
        }
        return digit;
    }

    private static Matcher match(Matcher matcher, String text) {
        matcher.reset(text);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }
}
