package org.graviton.network.game.protocol;

import org.graviton.game.job.Job;

/**
 * Created by Botan on 30/06/17. 23:41
 */
public class JobPacketFormatter {

    public static String statisticsJobMessage(Job job) {
        return "JX|" + job.getJobTemplate().getId() + ";" + job.getLevel() + ";" + job.compileExperience() + ";";
    }

    public static String startJobMessage(Job job) {
        return "JS".concat(job.statisticsPacket());
    }

    public static String jobToolMessage(short jobId) {
        return jobId > 0 ? "OT" + jobId : "OT";
    }

    public static String jobLevelMessage(short jobId, byte level) {
        return "JN" + jobId + "|" + level;
    }

    public static String emptyCraftMessage() {
        return "Ea4";
    }

    public static String interruptCraftMessage() {
        return "EcEI";
    }

    public static String errorChanceCraftMessage() {
        return "EcEF";
    }


    public static String badIOMessage(int playerId) {
        return "IO" + playerId + "|-";
    }

    public static String successIOMessage(int playerId, short item) {
        return "IO" + playerId + "|+" + item;
    }

    public static String createCraftResultMessage(short item) {
        return "EcK;+" + item;
    }

    public static String craftRepetitionMessage(short count) {
        return "EA" + count;
    }

    public static String craftResultMessage(boolean broken) {
        return broken ? "Ea2" : "Ea1";
    }

    public static String addCraftItemMessage(int itemId, short result) {
        return "EmKO+" + itemId + "|1|" + result+ "|";
    }

    public static String addItemMessage(int itemId, short template) {
        return "EMKO+" + itemId + "|" + template;
    }

    public static String removeItemMessage(int itemId, short quantity) {
        return "EMKO" + (quantity <= 0 ? "-" + itemId : "+" + itemId + "|" + quantity);
    }
}
