importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.client.player.Player);

commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "join";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        var target = player.getEntityFactory().getPlayerRepository().find(data[1]);
        if(target == null)
            player.send(org.graviton.network.game.protocol.MessageFormatter.redConsoleMessage("Cannot find player named <b>" + data[1] + "</b>"));
        else
            player.changeMap(target.getGameMap(), target.getCell().getId());
    }

}));