package com.android.commands.uinput;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.SparseArray;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import src.com.android.commands.uinput.InputAbsInfo;
/* loaded from: classes.dex */
public class Event {
    private static final int ABS_CNT = 64;
    public static final String COMMAND_DELAY = "delay";
    public static final String COMMAND_INJECT = "inject";
    public static final String COMMAND_REGISTER = "register";
    private static final String TAG = "UinputEvent";
    private SparseArray<InputAbsInfo> mAbsInfo;
    private Bus mBus;
    private String mCommand;
    private SparseArray<int[]> mConfiguration;
    private int mDuration;
    private int mFfEffectsMax = 0;
    private int mId;
    private int[] mInjections;
    private String mInputport;
    private String mName;
    private int mPid;
    private int mVid;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Bus {
        USB(3),
        BLUETOOTH(5);
        
        private final int mValue;

        Bus(int value) {
            this.mValue = value;
        }

        int getValue() {
            return this.mValue;
        }
    }

    public int getId() {
        return this.mId;
    }

    public String getCommand() {
        return this.mCommand;
    }

    public String getName() {
        return this.mName;
    }

    public int getVendorId() {
        return this.mVid;
    }

    public int getProductId() {
        return this.mPid;
    }

    public int getBus() {
        return this.mBus.getValue();
    }

    public int[] getInjections() {
        return this.mInjections;
    }

    public SparseArray<int[]> getConfiguration() {
        return this.mConfiguration;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public int getFfEffectsMax() {
        return this.mFfEffectsMax;
    }

    public SparseArray<InputAbsInfo> getAbsInfo() {
        return this.mAbsInfo;
    }

    public String getPort() {
        return this.mInputport;
    }

    public String toString() {
        return "Event{id=" + this.mId + ", command=" + this.mCommand + ", name=" + this.mName + ", vid=" + this.mVid + ", pid=" + this.mPid + ", bus=" + this.mBus + ", events=" + Arrays.toString(this.mInjections) + ", configuration=" + this.mConfiguration + ", duration=" + this.mDuration + ", ff_effects_max=" + this.mFfEffectsMax + ", port=" + this.mInputport + "}";
    }

    /* loaded from: classes.dex */
    private static class Builder {
        private Event mEvent = new Event();

        Builder() {
        }

        public void setId(int id) {
            this.mEvent.mId = id;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCommand(String command) {
            this.mEvent.mCommand = command;
        }

        public void setName(String name) {
            this.mEvent.mName = name;
        }

        public void setInjections(int[] events) {
            this.mEvent.mInjections = events;
        }

        public void setConfiguration(SparseArray<int[]> configuration) {
            this.mEvent.mConfiguration = configuration;
        }

        public void setVid(int vid) {
            this.mEvent.mVid = vid;
        }

        public void setPid(int pid) {
            this.mEvent.mPid = pid;
        }

        public void setBus(Bus bus) {
            this.mEvent.mBus = bus;
        }

        public void setDuration(int duration) {
            this.mEvent.mDuration = duration;
        }

        public void setFfEffectsMax(int ffEffectsMax) {
            this.mEvent.mFfEffectsMax = ffEffectsMax;
        }

        public void setAbsInfo(SparseArray<InputAbsInfo> absInfo) {
            this.mEvent.mAbsInfo = absInfo;
        }

        public void setInputport(String port) {
            this.mEvent.mInputport = port;
        }

        public Event build() {
            if (this.mEvent.mId == -1) {
                throw new IllegalStateException("No event id");
            }
            if (this.mEvent.mCommand == null) {
                throw new IllegalStateException("Event does not contain a command");
            }
            if (Event.COMMAND_REGISTER.equals(this.mEvent.mCommand)) {
                if (this.mEvent.mConfiguration == null) {
                    throw new IllegalStateException("Device registration is missing configuration");
                }
            } else if (Event.COMMAND_DELAY.equals(this.mEvent.mCommand)) {
                if (this.mEvent.mDuration <= 0) {
                    throw new IllegalStateException("Delay has missing or invalid duration");
                }
            } else if (Event.COMMAND_INJECT.equals(this.mEvent.mCommand)) {
                if (this.mEvent.mInjections == null) {
                    throw new IllegalStateException("Inject command is missing injection data");
                }
            } else {
                throw new IllegalStateException("Unknown command " + this.mEvent.mCommand);
            }
            return this.mEvent;
        }
    }

    /* loaded from: classes.dex */
    public static class Reader {
        private JsonReader mReader;

        public Reader(InputStreamReader in) {
            JsonReader jsonReader = new JsonReader(in);
            this.mReader = jsonReader;
            jsonReader.setLenient(true);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public Event getNextEvent() throws IOException {
            char c;
            Event e = null;
            while (e == null && this.mReader.peek() != JsonToken.END_DOCUMENT) {
                Builder eb = new Builder();
                try {
                    this.mReader.beginObject();
                    while (this.mReader.hasNext()) {
                        String name = this.mReader.nextName();
                        switch (name.hashCode()) {
                            case -1992012396:
                                if (name.equals("duration")) {
                                    c = '\n';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1291329255:
                                if (name.equals("events")) {
                                    c = 6;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3355:
                                if (name.equals("id")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 97920:
                                if (name.equals("bus")) {
                                    c = 5;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 110987:
                                if (name.equals("pid")) {
                                    c = 4;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 116753:
                                if (name.equals("vid")) {
                                    c = 3;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3373707:
                                if (name.equals("name")) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3446913:
                                if (name.equals("port")) {
                                    c = 11;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 950394699:
                                if (name.equals("command")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1167345576:
                                if (name.equals("ff_effects_max")) {
                                    c = '\b';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1713249371:
                                if (name.equals("abs_info")) {
                                    c = '\t';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1932752118:
                                if (name.equals("configuration")) {
                                    c = 7;
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
                                eb.setId(readInt());
                                break;
                            case 1:
                                eb.setCommand(this.mReader.nextString());
                                break;
                            case 2:
                                eb.setName(this.mReader.nextString());
                                break;
                            case 3:
                                eb.setVid(readInt());
                                break;
                            case 4:
                                eb.setPid(readInt());
                                break;
                            case 5:
                                eb.setBus(readBus());
                                break;
                            case 6:
                                int[] injections = readIntList().stream().mapToInt(new Event$Reader$$ExternalSyntheticLambda0()).toArray();
                                eb.setInjections(injections);
                                break;
                            case 7:
                                eb.setConfiguration(readConfiguration());
                                break;
                            case '\b':
                                eb.setFfEffectsMax(readInt());
                                break;
                            case '\t':
                                eb.setAbsInfo(readAbsInfoArray());
                                break;
                            case '\n':
                                eb.setDuration(readInt());
                                break;
                            case 11:
                                eb.setInputport(this.mReader.nextString());
                                break;
                            default:
                                this.mReader.skipValue();
                                break;
                        }
                    }
                    this.mReader.endObject();
                    e = eb.build();
                } catch (IllegalStateException ex) {
                    Event.error("Error reading in object, ignoring.", ex);
                    consumeRemainingElements();
                    this.mReader.endObject();
                }
            }
            return e;
        }

        private ArrayList<Integer> readIntList() throws IOException {
            ArrayList<Integer> data = new ArrayList<>();
            try {
                this.mReader.beginArray();
                while (this.mReader.hasNext()) {
                    data.add(Integer.decode(this.mReader.nextString()));
                }
                this.mReader.endArray();
                return data;
            } catch (IllegalStateException | NumberFormatException e) {
                consumeRemainingElements();
                this.mReader.endArray();
                throw new IllegalStateException("Encountered malformed data.", e);
            }
        }

        private byte[] readData() throws IOException {
            ArrayList<Integer> data = readIntList();
            byte[] rawData = new byte[data.size()];
            for (int i = 0; i < data.size(); i++) {
                int d = data.get(i).intValue();
                if ((d & 255) != d) {
                    throw new IllegalStateException("Invalid data, all values must be byte-sized");
                }
                rawData[i] = (byte) d;
            }
            return rawData;
        }

        private int readInt() throws IOException {
            String val = this.mReader.nextString();
            return Integer.decode(val).intValue();
        }

        private Bus readBus() throws IOException {
            String val = this.mReader.nextString();
            return Bus.valueOf(val.toUpperCase());
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private SparseArray<int[]> readConfiguration() throws IllegalStateException, IOException {
            char c;
            SparseArray<int[]> configuration = new SparseArray<>();
            try {
                this.mReader.beginArray();
                while (this.mReader.hasNext()) {
                    int type = 0;
                    IntStream data = null;
                    this.mReader.beginObject();
                    while (this.mReader.hasNext()) {
                        String name = this.mReader.nextName();
                        switch (name.hashCode()) {
                            case 3076010:
                                if (name.equals("data")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3575610:
                                if (name.equals("type")) {
                                    c = 0;
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
                                type = readInt();
                                break;
                            case 1:
                                data = readIntList().stream().mapToInt(new Event$Reader$$ExternalSyntheticLambda0());
                                break;
                            default:
                                consumeRemainingElements();
                                this.mReader.endObject();
                                throw new IllegalStateException("Invalid key in device configuration: " + name);
                        }
                    }
                    this.mReader.endObject();
                    if (data != null) {
                        int[] existing = configuration.get(type);
                        configuration.put(type, existing == null ? data.toArray() : IntStream.concat(IntStream.of(existing), data).toArray());
                    }
                }
                this.mReader.endArray();
                return configuration;
            } catch (IllegalStateException | NumberFormatException e) {
                consumeRemainingElements();
                this.mReader.endArray();
                throw new IllegalStateException("Encountered malformed data.", e);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private InputAbsInfo readAbsInfo() throws IllegalStateException, IOException {
            char c;
            InputAbsInfo absInfo = new InputAbsInfo();
            try {
                this.mReader.beginObject();
                while (this.mReader.hasNext()) {
                    String name = this.mReader.nextName();
                    switch (name.hashCode()) {
                        case -1600030548:
                            if (name.equals("resolution")) {
                                c = 5;
                                break;
                            }
                            c = 65535;
                            break;
                        case 3145593:
                            if (name.equals("flat")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        case 3155023:
                            if (name.equals("fuzz")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case 111972721:
                            if (name.equals("value")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case 844740128:
                            if (name.equals("maximum")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1064538126:
                            if (name.equals("minimum")) {
                                c = 1;
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
                            absInfo.value = readInt();
                            break;
                        case 1:
                            absInfo.minimum = readInt();
                            break;
                        case 2:
                            absInfo.maximum = readInt();
                            break;
                        case 3:
                            absInfo.fuzz = readInt();
                            break;
                        case 4:
                            absInfo.flat = readInt();
                            break;
                        case 5:
                            absInfo.resolution = readInt();
                            break;
                        default:
                            consumeRemainingElements();
                            this.mReader.endObject();
                            throw new IllegalStateException("Invalid key in abs info: " + name);
                    }
                }
                this.mReader.endObject();
                return absInfo;
            } catch (IllegalStateException | NumberFormatException e) {
                consumeRemainingElements();
                this.mReader.endObject();
                throw new IllegalStateException("Encountered malformed data.", e);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private SparseArray<InputAbsInfo> readAbsInfoArray() throws IllegalStateException, IOException {
            char c;
            SparseArray<InputAbsInfo> infoArray = new SparseArray<>();
            try {
                this.mReader.beginArray();
                while (this.mReader.hasNext()) {
                    int type = 0;
                    InputAbsInfo absInfo = null;
                    this.mReader.beginObject();
                    while (this.mReader.hasNext()) {
                        String name = this.mReader.nextName();
                        switch (name.hashCode()) {
                            case 3059181:
                                if (name.equals("code")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3237038:
                                if (name.equals("info")) {
                                    c = 1;
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
                                type = readInt();
                                break;
                            case 1:
                                absInfo = readAbsInfo();
                                break;
                            default:
                                consumeRemainingElements();
                                this.mReader.endObject();
                                throw new IllegalStateException("Invalid key in abs info array: " + name);
                        }
                    }
                    this.mReader.endObject();
                    if (absInfo != null) {
                        infoArray.put(type, absInfo);
                    }
                }
                this.mReader.endArray();
                return infoArray;
            } catch (IllegalStateException | NumberFormatException e) {
                consumeRemainingElements();
                this.mReader.endArray();
                throw new IllegalStateException("Encountered malformed data.", e);
            }
        }

        private void consumeRemainingElements() throws IOException {
            while (this.mReader.hasNext()) {
                this.mReader.skipValue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void error(String msg, Exception e) {
        System.out.println(msg);
        Log.e(TAG, msg);
        if (e != null) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
