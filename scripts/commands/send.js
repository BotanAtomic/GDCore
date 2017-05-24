importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.client.player.Player);


commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "send";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        player.send(data[1]);
        player.send(org.graviton.network.game.protocol.MessageFormatter.greenConsoleMessage("Successfully send '" + data[1] + "' to client"));
    }

}));