package org.graviton.game.paths;

import javafx.util.Pair;
import org.graviton.game.look.enums.Orientation;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.*;

/**
 * Created by Botan on 04/03/2017. 19:11
 */
public class PathMaker {
    private final Map<Short, Node> openList = new HashMap<>();
    private final Map<Short, Node> closeList = new LinkedHashMap<>();

    private final short startCell, lastCell;
    private final AbstractMap map;

    public PathMaker(AbstractMap map, short startCell, short lastCell) {
        this.map = map;
        this.startCell = startCell;
        this.lastCell = lastCell;
    }

    public List<Cell> getDirectPath() {
        Node nodeStart = new Node(startCell, null);
        openList.put(startCell, nodeStart);

        while (!openList.isEmpty() && !closeList.containsKey(lastCell)) {
            Node currentNode = bestNode();

            addListClose(currentNode);

            for (Orientation orientation : Orientation.values()) {
                short cellId = Cells.getCellIdByOrientation(currentNode.getCellId(), orientation, map.getWidth()); //TODO ADJACENT
                Node node = new Node(cellId, currentNode);
                Cell cell = map.getCell(cellId);


                if (cell == null)
                    continue;


                if (openList.containsKey(cellId)) {
                    Node openNode = openList.get(cellId);
                        currentNode.setChildren(openNode);
                        openNode.setParent(currentNode);
                        openNode.setCountG(getCostG(node));
                        openNode.setHeretic(Cells.distanceBetween(map.getWidth(), cellId, lastCell) * 10);
                        openNode.setCountF(openNode.getCountG() + openNode.getHeretic());
                } else {
                    openList.put(cellId, node);
                    currentNode.setChildren(node);
                    node.setParent(currentNode);
                    node.setCountG(getCostG(node));
                    node.setHeretic(Cells.distanceBetween(map.getWidth(), cellId, lastCell) * 10);
                    node.setCountF(node.getCountG() + node.getHeretic());
                }
            }
        }

        return simplePath();
    }

    public List<Cell> getShortestPath() {
        Node nodeStart = new Node(startCell, null);
        openList.put(startCell, nodeStart);

        while (!openList.isEmpty() && !closeList.containsKey(lastCell)) {
            Node currentNode = bestNode();

         /**   if (currentNode.getCellId() == lastCell && Cells.cellAroundIsOccupied(map, currentNode.getCellId())) {
                System.err.println("Arround is occuped");
                return simplePath();
            }**/

            addListClose(currentNode);

            for (int loc0 = 0; loc0 < 4; loc0++) {
                short cellId = Cells.getCellIdByOrientation(currentNode.getCellId(), Orientation.ADJACENT[loc0], map.getWidth()); //TODO ADJACENT
                Node node = new Node(cellId, currentNode);
                Cell cell = map.getCells().get(cellId);


                if (cell == null || !cell.isWalkable() || !cell.isFree() || closeList.containsKey(cellId)) {
                    continue;
                }

                if (openList.containsKey(cellId)) {
                    Node openNode = openList.get(cellId);
                    if (openNode.getCountG() > getCostG(node)) {
                        currentNode.setChildren(openNode);
                        openNode.setParent(currentNode);
                        openNode.setCountG(getCostG(node));
                        openNode.setHeretic(Cells.distanceBetween(map.getWidth(), cellId, lastCell) * 10);
                        openNode.setCountF(openNode.getCountG() + openNode.getHeretic());
                    }
                } else {
                    openList.put(cellId, node);
                    currentNode.setChildren(node);
                    node.setParent(currentNode);
                    node.setCountG(getCostG(node));
                    node.setHeretic(Cells.distanceBetween(map.getWidth(), cellId, lastCell) * 10);
                    node.setCountF(node.getCountG() + node.getHeretic());
                }
            }
        }

        return simplePath();
    }

    private List<Cell> simplePath() {
        Node current = getLastNode(closeList);

        if (current == null)
            return null;

        List<Cell> path = new ArrayList<>();
        Map<Short, Cell> realPath = new HashMap<>();

        for (short index = (short) closeList.size(); current.getCellId() != startCell; index--) {
            if (current.getCellId() == startCell)
                break;

            realPath.put(index, map.getCells().get(current.getCellId()));
            current = current.getParent();
        }

        short index = -1;
        while (path.size() != realPath.size() && index < 80) {
            index++;
            if (realPath.get(index) == null)
                continue;
            path.add(realPath.get(index));
        }
        return path;
    }

    private Node getLastNode(Map<Short, Node> list) {
        return list.values().toArray(new Node[]{})[list.size() - 1];
    }


    private Node bestNode() {
        int bestCount = 150000;
        Node bestNode = null;
        for (Node node : openList.values())
            if (node.getCountF() < bestCount) {
                bestCount = node.getCountF();
                bestNode = node;
            }
        return bestNode;
    }

    private void addListClose(Node node) {
        if (openList.containsKey(node.getCellId()))
            openList.remove(node.getCellId());

        if (!closeList.containsKey(node.getCellId()))
            closeList.put(node.getCellId(), node);
    }

    private int getCostG(Node node) {
        int costG = 0;
        while (node.getCellId() == startCell) {
            node = node.getParent();
            costG += 10;
        }
        return costG;
    }

    public static Pair<String, Byte> compilePath(List<Cell> path, short firstCell, AbstractMap map) {
        StringBuilder compiledPath = new StringBuilder();

        short currentCellId = firstCell;

        for (Cell currentCell : path) {
            Orientation orientation = Cells.getOrientationByCells(currentCellId, currentCell.getId(), map);
            if (orientation != null) {
                if (path.indexOf(currentCell) != 0)
                    compiledPath.append(Cells.encode(currentCellId));
                compiledPath.append(Utils.HASH[orientation.ordinal()]);
            }
            currentCellId = currentCell.getId();
        }

        if (currentCellId != firstCell)
            compiledPath.append(Cells.encode(currentCellId));

        return new Pair<>(compiledPath.toString(), (byte) path.size());
    }

}
