package kingsheep.team.swulls;

import kingsheep.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wolf extends SwullsCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    @Override
    protected void think(Type map[][]) {

        if (alive) {
            move = getMove(map, getObjectives());
        } else {
            move = Move.WAIT;
        }

    }

    @Override
    protected char[] getObjectives() {
        char[] objectives = new char[1];
//        if (type.equals(Type.WOLF1))
            objectives[0] = '3';
//        else
//            objectives[0] = '1';

        return objectives;
    }

    @Override
    protected boolean isSquareSafe(int x, int y) {
        return true;
    }

    @Override
    protected List<String> findObjectives() {
        List<Type> objectiveList = Arrays.asList(objectives);
        List<String> objectivePositions = new ArrayList<String>();
        for (int iy = 0; iy < map.length; iy++) {
            for (int ix = 0; ix < map[iy].length; ix++) {
                if (objectiveList.contains(map[iy][ix])) {
                    objectivePositions.add(iy + "_" + ix + "_" + 10);
                    return objectivePositions;
                }
            }
        }
        return objectivePositions;
    }
}
