package org.graviton.shell;


import com.google.inject.Inject;
import org.graviton.api.Manageable;
import org.graviton.script.ScriptProcessor;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.Date;
import java.util.Scanner;

/**
 * Created by Botan on 27/11/16. 23:02
 */

public class Shell extends Thread implements Runnable, Manageable {
    private final Scanner scanner = new Scanner(System.in);
    private final Date startTime = new Date();

    @Inject private ScriptProcessor scriptProcessor;

    public Shell() {
        super.setDaemon(true);
        super.start();
    }


    public void begin() {
        System.out.println("\nShell >");
        while (scanner.hasNext())
            parse(scanner.nextLine());
    }

    private void parse(String data) {
        String[] args = data.split(" ");

        switch (args[0].toLowerCase()) {
            case "uptime":
                Period period = new Interval(startTime.getTime(), new Date().getTime()).toPeriod();
                System.out.println("Uptime : " + period.getDays() + "d " + period.getHours() + "h " + period.getMinutes() + "m " + period.getSeconds() + "s");
                break;

            case "memory":
                double currentMemory = (((double) (Runtime.getRuntime().totalMemory() / 1024) / 1024)) - (((double) (Runtime.getRuntime().freeMemory() / 1024) / 1024));
                System.out.println("Current memory usage: " + Double.toString(currentMemory).substring(0, 4) + " Mb / " + Double.toString(currentMemory / 8).substring(0, 4) + " Mo");
                break;

            case "thread":
                System.out.println("Active thread count : " + Thread.activeCount());
                break;

            case "gc":
                System.gc();
                System.out.println("Garbage collector successfully executed");
                break;

            case "help":
                StringBuilder builder = new StringBuilder("List of command -> \n");
                builder.append("-uptime -> show program uptime\n");
                builder.append("-memory -> show current usage memory\n");
                builder.append("-thread -> show active thread count\n");
                builder.append("-gc -> execute garbage collector\n");
                builder.append("-execute {command} -> execute js code\n");
                break;

            case "execute":
                if (args.length > 1)
                    System.out.println(scriptProcessor.execute(data.substring(args[0].length() + 1)));
                else
                    System.err.println("Bad format : argument needed");
                break;

            default:
                System.err.println("Cannot find command '" + args[0] + "'");
                break;
        }
        System.out.println("Shell >");
    }

    @Override public byte index() {
        return 10;
    }
}
