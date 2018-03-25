package kingsheep.team.swulls;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;
import kingsheep.team.greedy.GreedyCreature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by kama on 04.03.16.
 */
public abstract class SwullsCreature_bckup extends Creature {

    protected Map map;
    protected LinkedList<Square> fringe;
    protected Type[] objectives;

    protected int usedCosts;

    public SwullsCreature_bckup(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
        usedCosts = 0;
        objectives = getObjectives();
    }

    protected abstract Type[] getObjectives();

    public String getNickname(){
        //TODO change this to any nickname you like. This should not be your swulls. That way you can stay anonymous on the ranking list.
        return "Walti_Ruedisueli";
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Square getCurrentSquare(Creature creature) {
        return new Square(map, creature.type, creature.x, creature.y, Move.WAIT);
    }

    public int getUsedCosts() {
        return usedCosts;
    }

    class Square {
        Map map;
        Type type;
        int x;
        int y;
        ArrayList<Move> movesToComeHere;


        public Square(Map map, Type type, int x, int y, Move move) {
            this.map = map;
            this.type = type;
            this.x = x;
            this.y = y;
            this.movesToComeHere = new ArrayList<Move>();
            movesToComeHere.add(move);
        }

        public int getTotalCosts() {
            return usedCosts + map.estimateCostsFromSquareToObjective(this, objectives);
        }

        public Move getNextMoveToGetHere() {
            return movesToComeHere.get(0);
        }

        protected boolean isSquareVisitable(){
            if (type == Type.FENCE){
                return false;
            }

            return true;
        }
    }

    class Map {
        private Type[][] map;

        public Map(Type[][] map) {
            this.map = map;
        }

        public int estimateCostsFromSquareToObjective(Square square, Type[] objectives) {
            String objectivePosition = find(objectives);
            String squarePos = square.y + "_" + square.x;
            return estimateCostsBetween(squarePos, objectivePosition);
        }

        private int estimateCostsBetween(String squarePos, String objectivePos) {
            String[] squarePositions = squarePos.split("_");
            String[] objectivePositions = objectivePos.split("_");

            int yDistance = Math.abs(Integer.valueOf(squarePositions[0]) - Integer.valueOf(objectivePositions[0]));
            int xDistance = Math.abs(Integer.valueOf(squarePositions[1]) - Integer.valueOf(objectivePositions[1]));
            return yDistance + xDistance;
        }

        private String find(Type[] objectives) {
            Type objective = objectives[0];

            for (int iy = 0; iy < map.length; iy++) {
                for (int ix = 0; ix < map.length; ix++) {
                    if (map[iy][ix].equals(objective)) {
                        return iy + "_" + ix;
                    }
                }
            }
            return null;
        }

        public void addAvailableNeighborSquaresToTheFringe(Square s) {
            try {
                addSquareToQueueIfAccessible(new Square(this, map[s.y - 1][s.x], s.x, s.y - 1, Move.UP));
                addSquareToQueueIfAccessible(new Square(this, map[s.y + 1][s.x], s.x, s.y + 1, Move.DOWN));
                addSquareToQueueIfAccessible(new Square(this, map[s.y][s.x - 1], s.x - 1, s.y, Move.LEFT));
                addSquareToQueueIfAccessible(new Square(this, map[s.y][s.x + 1], s.x + 1, s.y, Move.RIGHT));

            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
        }

        private void addSquareToQueueIfAccessible(Square square) {
            if (square.isSquareVisitable()) {
                fringe.add(square);
            }
        }
    }

    private Move getSecureMove(Type[][] map) {
        Move move = Move.WAIT;

        if ( (type.equals(Type.SHEEP1) && map[y-1][x].equals(Type.WOLF2))
                || (type.equals(Type.SHEEP2) && map[y-1][x].equals(Type.WOLF1)) )
            move = Move.DOWN;

        if ( (type.equals(Type.SHEEP1) && map[y+1][x].equals(Type.WOLF2))
                || (type.equals(Type.SHEEP2) && map[y+1][x].equals(Type.WOLF1)) )
            move = Move.UP;

        if ( (type.equals(Type.SHEEP1) && map[y][x-1].equals(Type.WOLF2))
                || (type.equals(Type.SHEEP2) && map[y][x-1].equals(Type.WOLF1)) )
            move = Move.RIGHT;

        if ( (type.equals(Type.SHEEP1) && map[y][x+1].equals(Type.WOLF2))
                || (type.equals(Type.SHEEP2) && map[y][x+1].equals(Type.WOLF1)) )
            move = Move.LEFT;

        return move;
    }
}
