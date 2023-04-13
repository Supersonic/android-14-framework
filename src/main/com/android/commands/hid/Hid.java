package com.android.commands.hid;

import android.util.Log;
import android.util.SparseArray;
import com.android.commands.hid.Event;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class Hid {
    private static final String TAG = "HID";
    private final SparseArray<Device> mDevices = new SparseArray<>();
    private final Event.Reader mReader;

    private static void usage() {
        error("Usage: hid [FILE]");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            System.exit(1);
        }
        InputStream stream = null;
        try {
            try {
                if (args[0].equals("-")) {
                    stream = System.in;
                } else {
                    File f = new File(args[0]);
                    stream = new FileInputStream(f);
                }
                new Hid(stream).run();
            } catch (Exception e) {
                error("HID injection failed.", e);
                System.exit(1);
            }
        } finally {
            IoUtils.closeQuietly(stream);
        }
    }

    private Hid(InputStream in) {
        try {
            this.mReader = new Event.Reader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void run() {
        while (true) {
            try {
                Event e = this.mReader.getNextEvent();
                if (e == null) {
                    break;
                }
                process(e);
            } catch (IOException ex) {
                error("Error reading in events.", ex);
            }
        }
        for (int i = 0; i < this.mDevices.size(); i++) {
            this.mDevices.valueAt(i).close();
        }
    }

    private void process(Event e) {
        int index = this.mDevices.indexOfKey(e.getId());
        if (index < 0) {
            if (Event.COMMAND_REGISTER.equals(e.getCommand())) {
                registerDevice(e);
                return;
            } else {
                Log.e(TAG, "Unknown device id specified. Ignoring event.");
                return;
            }
        }
        Device d = this.mDevices.valueAt(index);
        if (Event.COMMAND_DELAY.equals(e.getCommand())) {
            d.addDelay(e.getDuration());
        } else if (Event.COMMAND_REPORT.equals(e.getCommand())) {
            d.sendReport(e.getReport());
        } else if (Event.COMMAND_SET_GET_REPORT_RESPONSE.equals(e.getCommand())) {
            d.setGetReportResponse(e.getReport());
        } else if (!Event.COMMAND_SEND_SET_REPORT_REPLY.equals(e.getCommand())) {
            if (Event.COMMAND_REGISTER.equals(e.getCommand())) {
                error("Device id=" + e.getId() + " is already registered. Ignoring event.");
            } else {
                error("Unknown command \"" + e.getCommand() + "\". Ignoring event.");
            }
        } else {
            d.sendSetReportReply(e.getReply().booleanValue());
        }
    }

    private void registerDevice(Event e) {
        if (!Event.COMMAND_REGISTER.equals(e.getCommand())) {
            throw new IllegalStateException("Tried to send command \"" + e.getCommand() + "\" to an unregistered device!");
        }
        int id = e.getId();
        Device d = new Device(id, e.getName(), e.getVendorId(), e.getProductId(), e.getBus(), e.getDescriptor(), e.getReport(), e.getFeatureReports(), e.getOutputs());
        this.mDevices.append(id, d);
    }

    private static void error(String msg) {
        error(msg, null);
    }

    private static void error(String msg, Exception e) {
        Log.e(TAG, msg);
        if (e != null) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
}
