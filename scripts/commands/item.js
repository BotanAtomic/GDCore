importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.client.player.Player);
importClass(org.graviton.game.items.template.ItemTemplate);

commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "item";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        var itemTemplate = player.getEntityFactory().getItemTemplate(parseInt(data[1]));
        if (itemTemplate != null) {
            var quantity = 1;
            if (data.length > 2) {
                quantity = parseInt(data[2]);
            }
            var item = itemTemplate.createMax(player.entityFactory().getNextItemId());
            item.setQuantity(quantity);

            var result = player.getInventory().addItem(item, true);

            if (result === null)
                player.send(org.graviton.network.game.protocol.ItemPacketFormatter.addItemMessage(item));

        }
    }

}));