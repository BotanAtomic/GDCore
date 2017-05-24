importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.client.player.Player);
importClass(org.graviton.network.game.protocol.MessageFormatter);


commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "level";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        var level = parseInt(data[1]);

        if(level <= player.getLevel()) {
            player.send(MessageFormatter.redConsoleMessage("You must choose a level higher than your"));
            return;
        }
        if(level > 200) {
            player.send(MessageFormatter.redConsoleMessage("You must choose a level less than or equal to 200"));
            return;
        }

        while (player.getLevel() < level)
            player.upgrade();

        player.upLevel(false);
        player.send(MessageFormatter.greenConsoleMessage("Successfully upgraded to level " + level));
    }

}));