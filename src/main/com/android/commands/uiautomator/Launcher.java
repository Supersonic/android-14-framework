package com.android.commands.uiautomator;

import android.os.Process;
import java.util.Arrays;
/* loaded from: classes.dex */
public class Launcher {
    private static Command[] COMMANDS;
    private static Command HELP_COMMAND;

    /* loaded from: classes.dex */
    public static abstract class Command {
        private String mName;

        public abstract String detailedOptions();

        public abstract void run(String[] strArr);

        public abstract String shortHelp();

        public Command(String name) {
            this.mName = name;
        }

        public String name() {
            return this.mName;
        }
    }

    public static void main(String[] args) {
        Command command;
        Process.setArgV0("uiautomator");
        if (args.length >= 1 && (command = findCommand(args[0])) != null) {
            String[] args2 = new String[0];
            if (args.length > 1) {
                args2 = (String[]) Arrays.copyOfRange(args, 1, args.length);
            }
            command.run(args2);
            return;
        }
        HELP_COMMAND.run(args);
    }

    private static Command findCommand(String name) {
        Command[] commandArr;
        for (Command command : COMMANDS) {
            if (command.name().equals(name)) {
                return command;
            }
        }
        return null;
    }

    static {
        Command command = new Command("help") { // from class: com.android.commands.uiautomator.Launcher.1
            @Override // com.android.commands.uiautomator.Launcher.Command
            public void run(String[] args) {
                Command[] commandArr;
                System.err.println("Usage: uiautomator <subcommand> [options]\n");
                System.err.println("Available subcommands:\n");
                for (Command command2 : Launcher.COMMANDS) {
                    String shortHelp = command2.shortHelp();
                    String detailedOptions = command2.detailedOptions();
                    if (shortHelp == null) {
                        shortHelp = "";
                    }
                    if (detailedOptions == null) {
                        detailedOptions = "";
                    }
                    System.err.println(String.format("%s: %s", command2.name(), shortHelp));
                    System.err.println(detailedOptions);
                }
            }

            @Override // com.android.commands.uiautomator.Launcher.Command
            public String detailedOptions() {
                return null;
            }

            @Override // com.android.commands.uiautomator.Launcher.Command
            public String shortHelp() {
                return "displays help message";
            }
        };
        HELP_COMMAND = command;
        COMMANDS = new Command[]{command, new RunTestCommand(), new DumpCommand(), new EventsCommand()};
    }
}
