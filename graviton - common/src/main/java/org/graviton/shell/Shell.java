package org.graviton.shell;


import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.Date;
import java.util.Scanner;

/**
 * Created by Botan on 27/11/16. 23:02
 */

public class Shell extends Thread implements Runnable {
    private final Scanner scanner = new Scanner(System.in);
    private final Date startTime = new Date();

    public Shell() {
        super.setDaemon(true);
        super.start();
    }

    public void begin() {
        System.out.println("\nCommand >");
        while (scanner.hasNext())
            parse(scanner.nextLine());
    }

    private void parse(String data) {
        switch (data.toLowerCase()) {
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
                break;

            default:
                System.err.println("Cannot find command '" + data + "'");
                break;
        }
        System.out.println("Command >");
    }

}
