importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.maps.cell.Cell);
importClass(org.graviton.utils.Cells);
importClass(org.graviton.game.creature.npc.Npc);
importClass(org.graviton.network.game.protocol.MessageFormatter);

commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "npc";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        player.send(MessageFormatter.whiteConsoleMessage(player.getGameMap().npcData()));
    }

}));