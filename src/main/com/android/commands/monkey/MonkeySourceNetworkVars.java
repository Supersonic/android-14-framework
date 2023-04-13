package com.android.commands.monkey;

import android.hardware.display.DisplayManagerGlobal;
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Display;
import com.android.commands.monkey.MonkeySourceNetwork;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class MonkeySourceNetworkVars {
    private static final Map<String, VarGetter> VAR_MAP;

    /* loaded from: classes.dex */
    private interface VarGetter {
        String get();
    }

    /* loaded from: classes.dex */
    private static class StaticVarGetter implements VarGetter {
        private final String value;

        public StaticVarGetter(String value) {
            this.value = value;
        }

        @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
        public String get() {
            return this.value;
        }
    }

    static {
        TreeMap treeMap = new TreeMap();
        VAR_MAP = treeMap;
        treeMap.put("build.board", new StaticVarGetter(Build.BOARD));
        treeMap.put("build.brand", new StaticVarGetter(Build.BRAND));
        treeMap.put("build.device", new StaticVarGetter(Build.DEVICE));
        treeMap.put("build.display", new StaticVarGetter(Build.DISPLAY));
        treeMap.put("build.fingerprint", new StaticVarGetter(Build.FINGERPRINT));
        treeMap.put("build.host", new StaticVarGetter(Build.HOST));
        treeMap.put("build.id", new StaticVarGetter(Build.ID));
        treeMap.put("build.model", new StaticVarGetter(Build.MODEL));
        treeMap.put("build.product", new StaticVarGetter(Build.PRODUCT));
        treeMap.put("build.tags", new StaticVarGetter(Build.TAGS));
        treeMap.put("build.brand", new StaticVarGetter(Long.toString(Build.TIME)));
        treeMap.put("build.type", new StaticVarGetter(Build.TYPE));
        treeMap.put("build.user", new StaticVarGetter(Build.USER));
        treeMap.put("build.cpu_abi", new StaticVarGetter(Build.CPU_ABI));
        treeMap.put("build.manufacturer", new StaticVarGetter(Build.MANUFACTURER));
        treeMap.put("build.version.incremental", new StaticVarGetter(Build.VERSION.INCREMENTAL));
        treeMap.put("build.version.release", new StaticVarGetter(Build.VERSION.RELEASE_OR_CODENAME));
        treeMap.put("build.version.sdk", new StaticVarGetter(Integer.toString(Build.VERSION.SDK_INT)));
        treeMap.put("build.version.codename", new StaticVarGetter(Build.VERSION.CODENAME));
        Display display = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        treeMap.put("display.width", new StaticVarGetter(Integer.toString(display.getWidth())));
        treeMap.put("display.height", new StaticVarGetter(Integer.toString(display.getHeight())));
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        treeMap.put("display.density", new StaticVarGetter(Float.toString(dm.density)));
        treeMap.put("am.current.package", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.1
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                return Monkey.currentPackage;
            }
        });
        treeMap.put("am.current.action", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.2
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                if (Monkey.currentIntent == null) {
                    return null;
                }
                return Monkey.currentIntent.getAction();
            }
        });
        treeMap.put("am.current.comp.class", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.3
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                if (Monkey.currentIntent == null) {
                    return null;
                }
                return Monkey.currentIntent.getComponent().getClassName();
            }
        });
        treeMap.put("am.current.comp.package", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.4
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                if (Monkey.currentIntent == null) {
                    return null;
                }
                return Monkey.currentIntent.getComponent().getPackageName();
            }
        });
        treeMap.put("am.current.data", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.5
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                if (Monkey.currentIntent == null) {
                    return null;
                }
                return Monkey.currentIntent.getDataString();
            }
        });
        treeMap.put("am.current.categories", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.6
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                if (Monkey.currentIntent == null) {
                    return null;
                }
                StringBuffer sb = new StringBuffer();
                for (String cat : Monkey.currentIntent.getCategories()) {
                    sb.append(cat).append(" ");
                }
                return sb.toString();
            }
        });
        treeMap.put("clock.realtime", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.7
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                return Long.toString(SystemClock.elapsedRealtime());
            }
        });
        treeMap.put("clock.uptime", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.8
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                return Long.toString(SystemClock.uptimeMillis());
            }
        });
        treeMap.put("clock.millis", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.9
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                return Long.toString(System.currentTimeMillis());
            }
        });
        treeMap.put("monkey.version", new VarGetter() { // from class: com.android.commands.monkey.MonkeySourceNetworkVars.10
            @Override // com.android.commands.monkey.MonkeySourceNetworkVars.VarGetter
            public String get() {
                return Integer.toString(2);
            }
        });
    }

    /* loaded from: classes.dex */
    public static class ListVarCommand implements MonkeySourceNetwork.MonkeyCommand {
        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeySourceNetwork.MonkeyCommandReturn translateCommand(List<String> command, MonkeySourceNetwork.CommandQueue queue) {
            Set<String> keys = MonkeySourceNetworkVars.VAR_MAP.keySet();
            StringBuffer sb = new StringBuffer();
            for (String key : keys) {
                sb.append(key).append(" ");
            }
            return new MonkeySourceNetwork.MonkeyCommandReturn(true, sb.toString());
        }
    }

    /* loaded from: classes.dex */
    public static class GetVarCommand implements MonkeySourceNetwork.MonkeyCommand {
        @Override // com.android.commands.monkey.MonkeySourceNetwork.MonkeyCommand
        public MonkeySourceNetwork.MonkeyCommandReturn translateCommand(List<String> command, MonkeySourceNetwork.CommandQueue queue) {
            if (command.size() == 2) {
                VarGetter getter = (VarGetter) MonkeySourceNetworkVars.VAR_MAP.get(command.get(1));
                if (getter == null) {
                    return new MonkeySourceNetwork.MonkeyCommandReturn(false, "unknown var");
                }
                return new MonkeySourceNetwork.MonkeyCommandReturn(true, getter.get());
            }
            return MonkeySourceNetwork.EARG;
        }
    }
}
