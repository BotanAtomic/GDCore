importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.client.player.Player);
importClass(org.graviton.network.game.protocol.PlayerPacketFormatter);


commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "kamas";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        player.getInventory().addKamas(parseInt(data[1]));
        player.send(PlayerPacketFormatter.asMessage(player));
    }

}));