package kingsheep.team.greedy;

import kingsheep.Simulator;
import kingsheep.Type;

public class Wolf extends GreedyCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    protected void think(Type map[][]) {
        char[] objectives = new char[1];

        if(alive){
            objectives[0] = '3';
            move = getGreedyAction(map, objectives);

            if (move == null){
                move = Move.WAIT;
            }

        }else {
            move = Move.WAIT;
        }
    }
}
