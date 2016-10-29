package org.graviton.core;


import com.google.inject.Guice;
import org.graviton.core.injector.modules.ConfigurationModule;
import org.graviton.core.injector.modules.DatabaseModule;

/**
 * Created by Botan on 29/10/2016 : 03:09
 */
public class Main {

    private static String ASCII_HEADER = "                 _____                     _  _                \n                / ____|                   (_)| |               \n               | |  __  _ __  __ _ __   __ _ | |_  ___   _ __  \n               | | |_ || '__|/ _` |\\ \\ / /| || __|/ _ \\ | '_ \\ \n               | |__| || |  | (_| | \\ V / | || |_| (_) || | | |\n                \\_____||_|   \\__,_|  \\_/  |_| \\__|\\___/ |_| |_|\n";

    public static void main(String[] args) {
        buildHeader();
        Guice.createInjector(new ConfigurationModule(), new DatabaseModule());
    }

    private static void buildHeader() {
        System.out.println(ASCII_HEADER);
        System.out.println();

        /** OS informations **/
        System.out.println("OS name : " + System.getProperty("os.name"));
        System.out.println("OS architecture : " + System.getProperty("os.arch"));
        System.out.println("OS version : " + System.getProperty("os.version"));

        /** Java informations **/
        System.out.println();

        System.out.println("Java version : " + System.getProperty("java.version"));
        System.out.println("Java vendor : " + System.getProperty("java.vendor"));
        System.out.println("Java home : " + System.getProperty("java.home"));
        System.out.println("Java Virtual Machine version : " + System.getProperty("java.vm.version"));

        /** Machine informations **/
        System.out.println();

        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores) : " + Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory : " + Runtime.getRuntime().freeMemory() / 1000 + " MB");

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory : " + (maxMemory == Long.MAX_VALUE ? "unlimited" : maxMemory / 1000 + " MB"));

        /* Total memory currently available to the JVM */
        System.out.println("Total memory available to JVM : " + Runtime.getRuntime().totalMemory() / 1000 + " MB\n");
    }

}
