importClass(org.graviton.game.job.JobTemplate);
importClass(org.graviton.game.job.action.JobActionGetter);
importClass(org.graviton.game.job.action.type.Harvest);
importClass(org.graviton.game.job.action.type.Craft);

var job = entityFactory.getJobTemplate(2);

job.setActionGetter(JavaAdapter(JobActionGetter, {

    get: function (job) {
        var jobActions = new ArrayList();

        if (job.getLevel() > 99)
            jobActions.add(new Harvest(158, job.getLevel(), -18, 75, job));

        if (job.getLevel() > 89)
            jobActions.add(new Harvest(35, job.getLevel(), -16, 70, job));

        if (job.getLevel() > 79) {
            jobActions.add(new Harvest(38, job.getLevel(), -14, 65, job));
            jobActions.add(new Harvest(155, job.getLevel(), -14, 65, job));
        }

        if (job.getLevel() > 74)
            jobActions.add(new Harvest(174, job.getLevel(), -13, 55, job));

        if (job.getLevel() > 69)
            jobActions.add(new Harvest(34, job.getLevel(), -12, 50, job));

        if (job.getLevel() > 59)
            jobActions.add(new Harvest(40, job.getLevel(), -10, 45, job));

        if (job.getLevel() > 49) {
            jobActions.add(new Harvest(33, job.getLevel(), -8, 40, job));
            jobActions.add(new Harvest(154, job.getLevel(), -8, 40, job));
        }

        if (job.getLevel() > 39)
            jobActions.add(new Harvest(37, job.getLevel(), -6, 35, job));

        if (job.getLevel() > 34) {
            jobActions.add(new Harvest(139, job.getLevel(), -5, 30, job));
            jobActions.add(new Harvest(141, job.getLevel(), -5, 30, job));
        }

        if (job.getLevel() > 29)
            jobActions.add(new Harvest(10, job.getLevel(), -4, 25, job));

        if (job.getLevel() > 19)
            jobActions.add(new Harvest(40, job.getLevel(), -2, 20, job));

        if (job.getLevel() > 9)
            jobActions.add(new Harvest(39, job.getLevel(), 0, 15, job));


        jobActions.add(new Harvest(6, job.getLevel(), 2, 10, job));
        jobActions.add(new Craft(101, 0, JobActionGetter.getChance(job.getLevel()), JobActionGetter.getMaxCase(job.getLevel()), job));
        return jobActions;
    }


}));