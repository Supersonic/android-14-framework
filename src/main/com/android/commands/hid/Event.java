package com.android.commands.hid;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.SparseArray;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class Event {
    public static final String COMMAND_DELAY = "delay";
    public static final String COMMAND_REGISTER = "register";
    public static final String COMMAND_REPORT = "report";
    public static final String COMMAND_SEND_SET_REPORT_REPLY = "send_set_report_reply";
    public static final String COMMAND_SET_GET_REPORT_RESPONSE = "set_get_report_response";
    private static final String TAG = "HidEvent";
    private Bus mBus;
    private String mCommand;
    private byte[] mDescriptor;
    private int mDuration;
    private SparseArray<byte[]> mFeatureReports;
    private int mId;
    private String mName;
    private Map<ByteBuffer, byte[]> mOutputs;
    private int mPid;
    private Boolean mReply;
    private byte[] mReport;
    private int mVid;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Bus {
        USB(3),
        BLUETOOTH(5);
        
        private int mValue;

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

    public byte[] getDescriptor() {
        return this.mDescriptor;
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

    public byte[] getReport() {
        return this.mReport;
    }

    public SparseArray<byte[]> getFeatureReports() {
        return this.mFeatureReports;
    }

    public Map<ByteBuffer, byte[]> getOutputs() {
        return this.mOutputs;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public Boolean getReply() {
        return this.mReply;
    }

    public String toString() {
        return "Event{id=" + this.mId + ", command=" + String.valueOf(this.mCommand) + ", name=" + String.valueOf(this.mName) + ", descriptor=" + Arrays.toString(this.mDescriptor) + ", vid=" + this.mVid + ", pid=" + this.mPid + ", bus=" + this.mBus + ", report=" + Arrays.toString(this.mReport) + ", feature_reports=" + this.mFeatureReports.toString() + ", outputs=" + this.mOutputs.toString() + ", duration=" + this.mDuration + ", success=" + this.mReply.toString() + "}";
    }

    /* loaded from: classes.dex */
    private static class Builder {
        private Event mEvent = new Event();

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

        public void setDescriptor(byte[] descriptor) {
            this.mEvent.mDescriptor = descriptor;
        }

        public void setReport(byte[] report) {
            this.mEvent.mReport = report;
        }

        public void setFeatureReports(SparseArray<byte[]> reports) {
            this.mEvent.mFeatureReports = reports;
        }

        public void setOutputs(Map<ByteBuffer, byte[]> outputs) {
            this.mEvent.mOutputs = outputs;
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

        public void setReply(boolean success) {
            this.mEvent.mReply = Boolean.valueOf(success);
        }

        public Event build() {
            if (this.mEvent.mId == -1) {
                throw new IllegalStateException("No event id");
            }
            if (this.mEvent.mCommand == null) {
                throw new IllegalStateException("Event does not contain a command");
            }
            if (Event.COMMAND_REGISTER.equals(this.mEvent.mCommand) && this.mEvent.mDescriptor == null) {
                throw new IllegalStateException("Device registration is missing descriptor");
            }
            if (Event.COMMAND_SET_GET_REPORT_RESPONSE.equals(this.mEvent.mCommand) && this.mEvent.mReport == null) {
                throw new IllegalStateException("Report command is missing response data");
            }
            if (Event.COMMAND_SEND_SET_REPORT_REPLY.equals(this.mEvent.mCommand)) {
                if (this.mEvent.mReply == null) {
                    throw new IllegalStateException("Reply command is missing reply");
                }
            } else if (Event.COMMAND_DELAY.equals(this.mEvent.mCommand)) {
                if (this.mEvent.mDuration <= 0) {
                    throw new IllegalStateException("Delay has missing or invalid duration");
                }
            } else if (Event.COMMAND_REPORT.equals(this.mEvent.mCommand) && this.mEvent.mReport == null) {
                throw new IllegalStateException("Report command is missing report data");
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
                            case -1867169789:
                                if (name.equals("success")) {
                                    c = 11;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1106114670:
                                if (name.equals("outputs")) {
                                    c = '\t';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -934521548:
                                if (name.equals(Event.COMMAND_REPORT)) {
                                    c = 7;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -748366993:
                                if (name.equals("descriptor")) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -401007242:
                                if (name.equals("feature_reports")) {
                                    c = '\b';
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
                                    c = 6;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 110987:
                                if (name.equals("pid")) {
                                    c = 5;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 116753:
                                if (name.equals("vid")) {
                                    c = 4;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3373707:
                                if (name.equals("name")) {
                                    c = 3;
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
                                eb.setDescriptor(readData());
                                break;
                            case 3:
                                eb.setName(this.mReader.nextString());
                                break;
                            case 4:
                                eb.setVid(readInt());
                                break;
                            case 5:
                                eb.setPid(readInt());
                                break;
                            case 6:
                                eb.setBus(readBus());
                                break;
                            case 7:
                                eb.setReport(readData());
                                break;
                            case '\b':
                                eb.setFeatureReports(readFeatureReports());
                                break;
                            case '\t':
                                eb.setOutputs(readOutputs());
                                break;
                            case '\n':
                                eb.setDuration(readInt());
                                break;
                            case 11:
                                eb.setReply(readBool());
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

        private byte[] readData() throws IOException {
            ArrayList<Integer> data = new ArrayList<>();
            try {
                this.mReader.beginArray();
                while (this.mReader.hasNext()) {
                    data.add(Integer.decode(this.mReader.nextString()));
                }
                this.mReader.endArray();
                byte[] rawData = new byte[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    int d = data.get(i).intValue();
                    if ((d & 255) != d) {
                        throw new IllegalStateException("Invalid data, all values must be byte-sized");
                    }
                    rawData[i] = (byte) d;
                }
                return rawData;
            } catch (IllegalStateException | NumberFormatException e) {
                consumeRemainingElements();
                this.mReader.endArray();
                throw new IllegalStateException("Encountered malformed data.", e);
            }
        }

        private int readInt() throws IOException {
            String val = this.mReader.nextString();
            return Integer.decode(val).intValue();
        }

        private boolean readBool() throws IOException {
            String val = this.mReader.nextString();
            return Boolean.parseBoolean(val);
        }

        private Bus readBus() throws IOException {
            String val = this.mReader.nextString();
            return Bus.valueOf(val.toUpperCase());
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private SparseArray<byte[]> readFeatureReports() throws IllegalStateException, IOException {
            char c;
            SparseArray<byte[]> featureReports = new SparseArray<>();
            try {
                this.mReader.beginArray();
                while (this.mReader.hasNext()) {
                    int id = 0;
                    byte[] data = null;
                    this.mReader.beginObject();
                    while (this.mReader.hasNext()) {
                        String name = this.mReader.nextName();
                        switch (name.hashCode()) {
                            case 3355:
                                if (name.equals("id")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 3076010:
                                if (name.equals("data")) {
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
                                id = readInt();
                                break;
                            case 1:
                                data = readData();
                                break;
                            default:
                                consumeRemainingElements();
                                this.mReader.endObject();
                                throw new IllegalStateException("Invalid key in feature report: " + name);
                        }
                    }
                    this.mReader.endObject();
                    if (data != null) {
                        featureReports.put(id, data);
                    }
                }
                this.mReader.endArray();
                return featureReports;
            } catch (IllegalStateException | NumberFormatException e) {
                consumeRemainingElements();
                this.mReader.endArray();
                throw new IllegalStateException("Encountered malformed data.", e);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private Map<ByteBuffer, byte[]> readOutputs() throws IllegalStateException, IOException {
            char c;
            Map<ByteBuffer, byte[]> outputs = new HashMap<>();
            try {
                this.mReader.beginArray();
                while (this.mReader.hasNext()) {
                    byte[] output = null;
                    byte[] response = null;
                    this.mReader.beginObject();
                    while (this.mReader.hasNext()) {
                        String name = this.mReader.nextName();
                        switch (name.hashCode()) {
                            case -1724546052:
                                if (name.equals("description")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1005512447:
                                if (name.equals("output")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -340323263:
                                if (name.equals("response")) {
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
                                this.mReader.nextString();
                                break;
                            case 1:
                                output = readData();
                                break;
                            case 2:
                                response = readData();
                                break;
                            default:
                                consumeRemainingElements();
                                this.mReader.endObject();
                                throw new IllegalStateException("Invalid key in outputs: " + name);
                        }
                    }
                    this.mReader.endObject();
                    if (output != null) {
                        outputs.put(ByteBuffer.wrap(output), response);
                    }
                }
                this.mReader.endArray();
                return outputs;
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
