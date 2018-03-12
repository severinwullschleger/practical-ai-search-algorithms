package kingsheep.team.swulls;

import kingsheep.*;

public class Wolf extends SwullsCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    protected void think(Type map[][]) {

        if (alive) {
            move = getMove(map, getObjectives());
        } else {
            move = Move.WAIT;
        }

    }

    protected char[] getObjectives() {
        char[] objectives = new char[1];
        if (type.equals(Type.WOLF1))
            objectives[0] = '3';
        else
            objectives[0] = '1';

        return objectives;
    }
}
