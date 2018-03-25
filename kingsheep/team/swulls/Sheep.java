package kingsheep.team.swulls;

import kingsheep.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sheep extends SwullsCreature {

    public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
        counter = 0;
//        if (type.equals(Type.SHEEP2))
//            opponentWolf = Type.WOLF1;
//        else
            opponentWolf = Type.WOLF2;
    }

    @Override
    protected void think(Type map[][]) {

        counter++;

        if (alive) {
            move = getMove(map, getObjectives());
        }

    }

    @Override
    protected char[] getObjectives() {
        char[] objectives = new char[2];
        objectives[0] = 'r';
        objectives[1] = 'g';
        return objectives;
    }

    @Override
    protected boolean isSquareSafe(int x, int y) {
        ArrayList<Type> surroundingSquares = new ArrayList<Type>(4);
        if (isCoordinateValid(map, y - 1, x)) surroundingSquares.add(map[y - 1][x]);
        if (isCoordinateValid(map, y + 1, x)) surroundingSquares.add(map[y + 1][x]);
        if (isCoordinateValid(map, y, x - 1)) surroundingSquares.add(map[y][x - 1]);
        if (isCoordinateValid(map, y, x + 1)) surroundingSquares.add(map[y][x + 1]);

        if (surroundingSquares.contains(opponentWolf))
            return false;

//        //handle bug if opponent wolf didnt move yet. (swap from P2 to P1)
//        if (counter <= 2
//                && (surroundingSquares.contains(Type.WOLF1)
//                    || surroundingSquares.contains(Type.WOLF2)))
//            return false;

        return true;
    }

    @Override
    protected List<String> findObjectives() {
        List<Type> objectiveList = Arrays.asList(objectives);
        List<String> objectivePositions = new ArrayList<String>();
        for (int iy = 0; iy < map.length; iy++) {
            for (int ix = 0; ix < map[iy].length; ix++) {
                if (objectiveList.contains(map[iy][ix])) {
                    if (map[iy][ix].equals(Type.GRASS))
                        objectivePositions.add(iy + "_" + ix + "_" + 1);
                    else if (map[iy][ix].equals(Type.RHUBARB))
                        objectivePositions.add(iy + "_" + ix + "_" + 5);
                    else
                        objectivePositions.add(iy + "_" + ix + "_" + 0);
                }
            }
        }
        return objectivePositions;
    }


}
