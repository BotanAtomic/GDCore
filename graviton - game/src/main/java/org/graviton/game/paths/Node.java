package org.graviton.game.paths;

import lombok.Data;

/**
 * Created by Botan on 04/03/2017. 19:14
 */

@Data
class Node {
    private int countG = 0, countF = 0, heretic = 0;

    private short cellId;
    private Node parent,children;

    Node(short cellId, Node parent) {
        this.cellId = cellId;
        this.parent = parent;
    }

}
