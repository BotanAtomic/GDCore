importClass(org.graviton.game.job.JobTemplate);
importClass(org.graviton.game.job.action.JobActionGetter);
importClass(org.graviton.game.job.action.type.Harvest);
importClass(org.graviton.game.job.action.type.Craft);

var job = entityFactory.getJobTemplate(28);

job.setActionGetter(JavaAdapter(JobActionGetter, {

    get: function (job) {
        var jobActions = new ArrayList();
        
        if (job.getLevel() > 69)
            jobActions.add(new Harvest(54, job.getLevel(), -12,  45, job));
        
        if (job.getLevel() > 59)
            jobActions.add(new Harvest(58, job.getLevel(), -10, 40, job));
        
        if (job.getLevel() > 49) {
            jobActions.add(new Harvest(159, job.getLevel(), -8, 35, job));
            jobActions.add(new Harvest(52, job.getLevel(), -8, 35, job));
        }
        
        if (job.getLevel() > 39)
            jobActions.add(new Harvest(50, job.getLevel(), -6, 30, job));
        
        if (job.getLevel() > 29)
            jobActions.add(new Harvest(46, job.getLevel(), -4, 25, job));
        
        if (job.getLevel() > 19)
            jobActions.add(new Harvest(57, job.getLevel(), -2, 20, job));
        
        if (job.getLevel() > 9)
            jobActions.add(new Harvest(53, job.getLevel(), 0, 15, job));

        jobActions.add(new Harvest(45, job.getLevel(), 1, 10, job));
        jobActions.add(new Craft(47, 0, JobActionGetter.getChance(job.getLevel()), JobActionGetter.getMaxCase(job.getLevel()), job));
        jobActions.add(new Craft(122, 10, 100, 1, job));
        return jobActions;
    }


}));