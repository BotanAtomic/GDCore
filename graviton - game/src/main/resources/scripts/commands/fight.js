importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(java.util.ArrayList);
importClass(org.graviton.game.client.player.Player);
importClass(org.graviton.game.creature.monster.Monster);
importClass(org.graviton.game.creature.monster.MonsterGroup);

commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "fight";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        var monsters = new ArrayList();

        data[1].split(',').forEach(function(entry) {
            var monster = player.getEntityFactory().getMonsterTemplate(parseInt(entry));

            if(monster != null)
                monsters.add(monster.getRandom().copy());

        });

        var monsterGroup = new MonsterGroup(0, player.getGameMap(), 0, monsters);

        player.getGameMap().getFightFactory().newMonsterFight(player, monsterGroup);
    }

}));