importClass(org.graviton.database.repository.CommandRepository);
importClass(org.graviton.game.command.api.AbstractCommand);
importClass(org.graviton.game.maps.cell.Cell);
importClass(org.graviton.utils.Cells);

commandRepository.register(JavaAdapter(AbstractCommand, {
    name: function () {
        return "cell";
    },

    description: function () {
        return "";
    },

    apply: function (player, data) {
        var args = data[1];

        if (args.equals("distance")) {
            var firstCell = data[2];
            var secondCell = data[3];

            java.lang.System.err.println("Distance => " + Cells.distanceBetween(player.getMap().getWidth(), firstCell, secondCell));
        } else if (args.equals("los")) {
            var firstCell = data[2];
            var secondCell = data[3];
            var result = Cells.checkLineOfSide(player.getMap(), parseInt(firstCell), parseInt(secondCell), true);
            java.lang.System.err.println("Good ? => " + result);
        } else if (args.equals("arround")) {
            var cell = data[2];
            var limit = data[3];
            var result = Cells.showArroundCell(player.getMap(), parseInt(cell), parseInt(limit));
            java.lang.System.err.println("show arround cell of " + cell + " with limit = " + limit);
        } else {
            var cell = player.getMap().getCell(args);
            if (cell != null) {
                java.lang.System.err.println("LOS => " + cell.isLineOfSight());
                java.lang.System.err.println("Type => " + cell.getMovementType().name());
                java.lang.System.err.println("groundLevel => " + cell.getGroundLevel());
                java.lang.System.err.println("groundSlope => " + cell.getGroundSlope());
                player.send("Gf" + player.getId() + "|" + args)
            }
        }

    }

}));