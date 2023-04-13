package com.android.commands.monkey;

import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import com.android.commands.monkey.MonkeySourceNetworkVars;
import com.android.commands.monkey.MonkeySourceNetworkViews;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
/* loaded from: classes.dex */
public class MonkeySourceNetwork implements MonkeyEventSource {
    private static final Map<String, MonkeyCommand> COMMAND_MAP;
    private static final String DONE = "done";
    private static final String ERROR_STR = "ERROR";
    public static final int MONKEY_NETWORK_VERSION = 2;
    private static final String OK_STR = "OK";
    private static final String QUIT = "quit";
    private static final String TAG = "MonkeyStub";
    private static DeferredReturn deferredReturn;
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private ServerSocket serverSocket;

    /* renamed from: OK */
    public static final MonkeyCommandReturn f0OK = new MonkeyCommandReturn(true);
    public static final MonkeyCommandReturn ERROR = new MonkeyCommandReturn(false);
    public static final MonkeyCommandReturn EARG = new MonkeyCommandReturn(false, "Invalid Argument");
    private final CommandQueueImpl commandQueue = new CommandQueueImpl();
    private boolean started = false;

    /* loaded from: classes.dex */
    public interface CommandQueue {
        void enqueueEvent(MonkeyEvent monkeyEvent);
    }

    /* loaded from: classes.dex */
    public interface MonkeyCommand {
        MonkeyCommandReturn translateCommand(List<String> list, CommandQueue commandQueue);
    }

    /* loaded from: classes.dex */
    public static class MonkeyCommandReturn {
        private final String message;
        private final boolean success;

        public MonkeyCommandReturn(boolean success) {
            this.success = success;
            this.message = null;
        }

        public MonkeyCommandReturn(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        boolean hasMessage() {
            return this.message != null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getMessage() {
            return this.message;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean wasSuccessful() {
            return this.success;
        }
    }

    static {
        HashMap hashMap = new HashMap();
        COMMAND_MAP = hashMap;
        hashMap.put("flip", new FlipCommand());
        hashMap.put("touch", new TouchCommand());
        hashMap.put("trackball", new TrackballCommand());
        hashMap.put("key", new KeyCommand());
        hashMap.put("sleep", new SleepCommand());
        hashMap.put("wake", new WakeCommand());
        hashMap.put("tap", new TapCommand());
        hashMap.put("press", new PressCommand());
        hashMap.put("type", new TypeCommand());
        hashMap.put("listvar", new MonkeySourceNetworkVars.ListVarCommand());
        hashMap.put("getvar", new MonkeySourceNetworkVars.GetVarCommand());
        hashMap.put("listviews", new MonkeySourceNetworkViews.ListViewsCommand());
        hashMap.put("queryview", new MonkeySourceNetworkViews.QueryViewCommand());
        hashMap.put("getrootview", new MonkeySourceNetworkViews.GetRootViewCommand());
        hashMap.put("getviewswithtext", new MonkeySourceNetworkViews.GetViewsWithTextCommand());
        hashMap.put("deferreturn", new DeferReturnCommand());
    }

    /* loaded from: classes.dex */
    private static class FlipCommand implements MonkeyCommand {
        private FlipCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() > 1) {
                String direction = command.get(1);
                if ("open".equals(direction)) {
                    queue.enqueueEvent(new MonkeyFlipEvent(true));
                    return MonkeySourceNetwork.f0OK;
                } else if ("close".equals(direction)) {
                    queue.enqueueEvent(new MonkeyFlipEvent(false));
                    return MonkeySourceNetwork.f0OK;
                }
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* loaded from: classes.dex */
    private static class TouchCommand implements MonkeyCommand {
        private TouchCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() == 4) {
                String actionName = command.get(1);
                try {
                    int x = Integer.parseInt(command.get(2));
                    int y = Integer.parseInt(command.get(3));
                    int action = -1;
                    if ("down".equals(actionName)) {
                        action = 0;
                    } else if ("up".equals(actionName)) {
                        action = 1;
                    } else if ("move".equals(actionName)) {
                        action = 2;
                    }
                    if (action == -1) {
                        Log.e(MonkeySourceNetwork.TAG, "Got a bad action: " + actionName);
                        return MonkeySourceNetwork.EARG;
                    }
                    queue.enqueueEvent(new MonkeyTouchEvent(action).addPointer(0, x, y));
                    return MonkeySourceNetwork.f0OK;
                } catch (NumberFormatException e) {
                    Log.e(MonkeySourceNetwork.TAG, "Got something that wasn't a number", e);
                    return MonkeySourceNetwork.EARG;
                }
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* loaded from: classes.dex */
    private static class TrackballCommand implements MonkeyCommand {
        private TrackballCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() == 3) {
                try {
                    int dx = Integer.parseInt(command.get(1));
                    int dy = Integer.parseInt(command.get(2));
                    queue.enqueueEvent(new MonkeyTrackballEvent(2).addPointer(0, dx, dy));
                    return MonkeySourceNetwork.f0OK;
                } catch (NumberFormatException e) {
                    Log.e(MonkeySourceNetwork.TAG, "Got something that wasn't a number", e);
                    return MonkeySourceNetwork.EARG;
                }
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* loaded from: classes.dex */
    private static class KeyCommand implements MonkeyCommand {
        private KeyCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() == 3) {
                int keyCode = MonkeySourceNetwork.getKeyCode(command.get(2));
                if (keyCode < 0) {
                    Log.e(MonkeySourceNetwork.TAG, "Can't find keyname: " + command.get(2));
                    return MonkeySourceNetwork.EARG;
                }
                Log.d(MonkeySourceNetwork.TAG, "keycode: " + keyCode);
                int action = -1;
                if ("down".equals(command.get(1))) {
                    action = 0;
                } else if ("up".equals(command.get(1))) {
                    action = 1;
                }
                if (action == -1) {
                    Log.e(MonkeySourceNetwork.TAG, "got unknown action.");
                    return MonkeySourceNetwork.EARG;
                }
                queue.enqueueEvent(new MonkeyKeyEvent(action, keyCode));
                return MonkeySourceNetwork.f0OK;
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getKeyCode(String keyName) {
        try {
            return Integer.parseInt(keyName);
        } catch (NumberFormatException e) {
            int keyCode = MonkeySourceRandom.getKeyCode(keyName);
            if (keyCode == 0) {
                int keyCode2 = MonkeySourceRandom.getKeyCode("KEYCODE_" + keyName.toUpperCase());
                if (keyCode2 == 0) {
                    return -1;
                }
                return keyCode2;
            }
            return keyCode;
        }
    }

    /* loaded from: classes.dex */
    private static class SleepCommand implements MonkeyCommand {
        private SleepCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() == 2) {
                String sleepStr = command.get(1);
                try {
                    int sleep = Integer.parseInt(sleepStr);
                    queue.enqueueEvent(new MonkeyThrottleEvent(sleep));
                    return MonkeySourceNetwork.f0OK;
                } catch (NumberFormatException e) {
                    Log.e(MonkeySourceNetwork.TAG, "Not a number: " + sleepStr, e);
                    return MonkeySourceNetwork.EARG;
                }
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* loaded from: classes.dex */
    private static class TypeCommand implements MonkeyCommand {
        private TypeCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() == 2) {
                String str = command.get(1);
                char[] chars = str.toString().toCharArray();
                KeyCharacterMap keyCharacterMap = KeyCharacterMap.load(-1);
                KeyEvent[] events = keyCharacterMap.getEvents(chars);
                for (KeyEvent event : events) {
                    queue.enqueueEvent(new MonkeyKeyEvent(event));
                }
                return MonkeySourceNetwork.f0OK;
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* loaded from: classes.dex */
    private static class WakeCommand implements MonkeyCommand {
        private WakeCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (!MonkeySourceNetwork.wake()) {
                return MonkeySourceNetwork.ERROR;
            }
            return MonkeySourceNetwork.f0OK;
        }
    }

    /* loaded from: classes.dex */
    private static class TapCommand implements MonkeyCommand {
        private TapCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() == 3) {
                try {
                    int x = Integer.parseInt(command.get(1));
                    int y = Integer.parseInt(command.get(2));
                    queue.enqueueEvent(new MonkeyTouchEvent(0).addPointer(0, x, y));
                    queue.enqueueEvent(new MonkeyTouchEvent(1).addPointer(0, x, y));
                    return MonkeySourceNetwork.f0OK;
                } catch (NumberFormatException e) {
                    Log.e(MonkeySourceNetwork.TAG, "Got something that wasn't a number", e);
                    return MonkeySourceNetwork.EARG;
                }
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* loaded from: classes.dex */
    private static class PressCommand implements MonkeyCommand {
        private PressCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() == 2) {
                int keyCode = MonkeySourceNetwork.getKeyCode(command.get(1));
                if (keyCode < 0) {
                    Log.e(MonkeySourceNetwork.TAG, "Can't find keyname: " + command.get(1));
                    return MonkeySourceNetwork.EARG;
                }
                queue.enqueueEvent(new MonkeyKeyEvent(0, keyCode));
                queue.enqueueEvent(new MonkeyKeyEvent(1, keyCode));
                return MonkeySourceNetwork.f0OK;
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* loaded from: classes.dex */
    private static class DeferReturnCommand implements MonkeyCommand {
        private DeferReturnCommand() {
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeyCommandReturn translateCommand(List<String> command, CommandQueue queue) {
            if (command.size() > 3) {
                String event = command.get(1);
                if (!event.equals("screenchange")) {
                    return MonkeySourceNetwork.EARG;
                }
                long timeout = Long.parseLong(command.get(2));
                MonkeyCommand deferredCommand = (MonkeyCommand) MonkeySourceNetwork.COMMAND_MAP.get(command.get(3));
                if (deferredCommand != null) {
                    List<String> parts = command.subList(3, command.size());
                    MonkeyCommandReturn ret = deferredCommand.translateCommand(parts, queue);
                    MonkeySourceNetwork.deferredReturn = new DeferredReturn(1, ret, timeout);
                    return MonkeySourceNetwork.f0OK;
                }
            }
            return MonkeySourceNetwork.EARG;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean wake() {
        IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        try {
            pm.wakeUp(SystemClock.uptimeMillis(), 0, "Monkey", (String) null);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Got remote exception", e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CommandQueueImpl implements CommandQueue {
        private final Queue<MonkeyEvent> queuedEvents;

        private CommandQueueImpl() {
            this.queuedEvents = new LinkedList();
        }

        @Override // com.android.commands.monkey.MonkeySourceNetwork.CommandQueue
        public void enqueueEvent(MonkeyEvent e) {
            this.queuedEvents.offer(e);
        }

        public MonkeyEvent getNextQueuedEvent() {
            return this.queuedEvents.poll();
        }
    }

    /* loaded from: classes.dex */
    private static class DeferredReturn {
        public static final int ON_WINDOW_STATE_CHANGE = 1;
        private MonkeyCommandReturn deferredReturn;
        private int event;
        private long timeout;

        public DeferredReturn(int event, MonkeyCommandReturn deferredReturn, long timeout) {
            this.event = event;
            this.deferredReturn = deferredReturn;
            this.timeout = timeout;
        }

        public MonkeyCommandReturn waitForEvent() {
            switch (this.event) {
                case 1:
                    try {
                        synchronized (MonkeySourceNetworkViews.class) {
                            MonkeySourceNetworkViews.class.wait(this.timeout);
                        }
                        break;
                    } catch (InterruptedException e) {
                        Log.d(MonkeySourceNetwork.TAG, "Deferral interrupted: " + e.getMessage());
                        break;
                    }
            }
            return this.deferredReturn;
        }
    }

    public MonkeySourceNetwork(int port) throws IOException {
        this.serverSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
    }

    private void startServer() throws IOException {
        this.clientSocket = this.serverSocket.accept();
        MonkeySourceNetworkViews.setup();
        wake();
        this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.output = new PrintWriter(this.clientSocket.getOutputStream(), true);
    }

    private void stopServer() throws IOException {
        this.clientSocket.close();
        MonkeySourceNetworkViews.teardown();
        this.input.close();
        this.output.close();
        this.started = false;
    }

    private static String replaceQuotedChars(String input) {
        return input.replace("\\\"", "\"");
    }

    private static List<String> commandLineSplit(String line) {
        ArrayList<String> result = new ArrayList<>();
        StringTokenizer tok = new StringTokenizer(line);
        boolean insideQuote = false;
        StringBuffer quotedWord = new StringBuffer();
        while (tok.hasMoreTokens()) {
            String cur = tok.nextToken();
            if (!insideQuote && cur.startsWith("\"")) {
                quotedWord.append(replaceQuotedChars(cur));
                insideQuote = true;
            } else if (insideQuote) {
                if (cur.endsWith("\"")) {
                    insideQuote = false;
                    quotedWord.append(" ").append(replaceQuotedChars(cur));
                    String word = quotedWord.toString();
                    result.add(word.substring(1, word.length() - 1));
                } else {
                    quotedWord.append(" ").append(replaceQuotedChars(cur));
                }
            } else {
                result.add(replaceQuotedChars(cur));
            }
        }
        return result;
    }

    private void translateCommand(String commandLine) {
        MonkeyCommand command;
        Log.d(TAG, "translateCommand: " + commandLine);
        List<String> parts = commandLineSplit(commandLine);
        if (parts.size() > 0 && (command = COMMAND_MAP.get(parts.get(0))) != null) {
            MonkeyCommandReturn ret = command.translateCommand(parts, this.commandQueue);
            handleReturn(ret);
        }
    }

    private void handleReturn(MonkeyCommandReturn ret) {
        if (ret.wasSuccessful()) {
            if (ret.hasMessage()) {
                returnOk(ret.getMessage());
            } else {
                returnOk();
            }
        } else if (ret.hasMessage()) {
            returnError(ret.getMessage());
        } else {
            returnError();
        }
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public MonkeyEvent getNextEvent() {
        if (!this.started) {
            try {
                startServer();
                this.started = true;
            } catch (IOException e) {
                Log.e(TAG, "Got IOException from server", e);
                return null;
            }
        }
        while (true) {
            try {
                MonkeyEvent queuedEvent = this.commandQueue.getNextQueuedEvent();
                if (queuedEvent != null) {
                    return queuedEvent;
                }
                if (deferredReturn != null) {
                    Log.d(TAG, "Waiting for event");
                    MonkeyCommandReturn ret = deferredReturn.waitForEvent();
                    deferredReturn = null;
                    handleReturn(ret);
                }
                String command = this.input.readLine();
                if (command == null) {
                    Log.d(TAG, "Connection dropped.");
                    command = DONE;
                }
                if (DONE.equals(command)) {
                    try {
                        stopServer();
                        return new MonkeyNoopEvent();
                    } catch (IOException e2) {
                        Log.e(TAG, "Got IOException shutting down!", e2);
                        return null;
                    }
                } else if (QUIT.equals(command)) {
                    Log.d(TAG, "Quit requested");
                    returnOk();
                    return null;
                } else if (!command.startsWith("#")) {
                    translateCommand(command);
                }
            } catch (IOException e3) {
                Log.e(TAG, "Exception: ", e3);
                return null;
            }
        }
    }

    private void returnError() {
        this.output.println(ERROR_STR);
    }

    private void returnError(String msg) {
        this.output.print(ERROR_STR);
        this.output.print(":");
        this.output.println(msg);
    }

    private void returnOk() {
        this.output.println(OK_STR);
    }

    private void returnOk(String returnValue) {
        this.output.print(OK_STR);
        this.output.print(":");
        this.output.println(returnValue);
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public void setVerbose(int verbose) {
    }

    @Override // com.android.commands.monkey.MonkeyEventSource
    public boolean validate() {
        return true;
    }
}
