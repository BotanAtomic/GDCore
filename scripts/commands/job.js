importClass(org.graviton.game.job.JobTemplate);

commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "job";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        var job = player.getEntityFactory().getJobTemplate(parseInt(data[1]));
        if (job !== null)
            java.lang.System.err.println("Name = " + job.getName());

    }

}));