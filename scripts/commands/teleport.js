importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.client.player.Player);

commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "teleport";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        player.changeMap(parseInt(data[1]), 0);
    }

}));