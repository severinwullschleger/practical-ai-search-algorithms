package kingsheep.team.swulls;

import kingsheep.*;

public class Wolf extends SwullsCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    protected void think(Type map[][]) {

        if(alive){
            move = getMove(map, getObjectives());
        }else {
            move = Move.WAIT;
        }


        /*
		TODO
		YOUR WOLF CODE HERE
		
		BASE YOUR LOGIC ON THE INFORMATION FROM THE ARGUMENT map[][]
		
		YOUR CODE NEED TO BE DETERMINISTIC. 
		THAT MEANS, GIVEN A DETERMINISTIC OPPONENT AND MAP THE ACTIONS OF YOUR WOLF HAVE TO BE REPRODUCIBLE
		
		SET THE MOVE VARIABLE TO ONE TOF THE 5 VALUES
        move = Move.UP;
        move = Move.DOWN;
        move = Move.LEFT;
        move = Move.RIGHT;
        move = Move.WAIT;
		*/
    }

//    private Move getMove() {
//        fringe = new LinkedList<Square>();
//        fringe.push(getCurrentSquare(this));
//
//        while (fringe.size() > 0) {
//
//            Square s = getLowestCostSquare(fringe);
//            fringe.remove(s);
//            if (s.type.equals(objectives[0]))
//                return s.getNextMoveToGetHere();
//
//            map.addAvailableNeighborSquaresToTheFringe(s);
//        }
//
//        return getMove(map, );
//    }

    protected char[] getObjectives() {
        char[] objectives = new char[1];
        if (type.equals(Type.getType('2')))
            objectives[0] = '3';
        else
            objectives[0] = '1';

        return objectives;
    }
}
