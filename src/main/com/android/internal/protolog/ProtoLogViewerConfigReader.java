package com.android.internal.protolog;

import android.util.Slog;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes4.dex */
public class ProtoLogViewerConfigReader {
    private static final String TAG = "ProtoLogViewerConfigReader";
    private Map<Integer, String> mLogMessageMap = null;

    public synchronized String getViewerString(int messageHash) {
        Map<Integer, String> map = this.mLogMessageMap;
        if (map != null) {
            return map.get(Integer.valueOf(messageHash));
        }
        return null;
    }

    public synchronized void loadViewerConfig(PrintWriter pw, String viewerConfigFilename) {
        try {
            try {
                loadViewerConfig(new GZIPInputStream(new FileInputStream(viewerConfigFilename)));
                logAndPrintln(pw, "Loaded " + this.mLogMessageMap.size() + " log definitions from " + viewerConfigFilename);
            } catch (FileNotFoundException e) {
                logAndPrintln(pw, "Unable to load log definitions: File " + viewerConfigFilename + " not found." + e);
            }
        } catch (IOException e2) {
            logAndPrintln(pw, "Unable to load log definitions: IOException while reading " + viewerConfigFilename + ". " + e2);
        } catch (JSONException e3) {
            logAndPrintln(pw, "Unable to load log definitions: JSON parsing exception while reading " + viewerConfigFilename + ". " + e3);
        }
    }

    public synchronized void loadViewerConfig(InputStream viewerConfigInputStream) throws IOException, JSONException {
        if (this.mLogMessageMap != null) {
            return;
        }
        InputStreamReader config = new InputStreamReader(viewerConfigInputStream);
        BufferedReader reader = new BufferedReader(config);
        StringBuilder builder = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            builder.append(line).append('\n');
        }
        reader.close();
        JSONObject json = new JSONObject(builder.toString());
        JSONObject messages = json.getJSONObject("messages");
        this.mLogMessageMap = new TreeMap();
        Iterator it = messages.keys();
        while (it.hasNext()) {
            String key = it.next();
            try {
                int hash = Integer.parseInt(key);
                JSONObject val = messages.getJSONObject(key);
                String msg = val.getString("message");
                this.mLogMessageMap.put(Integer.valueOf(hash), msg);
            } catch (NumberFormatException e) {
            }
        }
    }

    public synchronized int knownViewerStringsNumber() {
        Map<Integer, String> map = this.mLogMessageMap;
        if (map != null) {
            return map.size();
        }
        return 0;
    }

    static void logAndPrintln(PrintWriter pw, String msg) {
        Slog.m94i(TAG, msg);
        if (pw != null) {
            pw.println(msg);
            pw.flush();
        }
    }
}
