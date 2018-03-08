package kingsheep.team.greedy;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by kama on 01.03.16.
 */
public abstract class GreedyCreature extends Creature {

    private HashMap<String, Square> visitedSquares;
    private ArrayList<Square> squareQueue;
    private ArrayList<Square> squareQueueToAdd;
    private Type map[][];
    private Type objective[];


    public GreedyCreature(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    public String getNickname(){
        return "Mr. Greedy";
    }

    protected Move getGreedyAction(Type map[][], char[] objective){
        visitedSquares = new HashMap<String, Square>();
        squareQueue = new ArrayList<Square>();

        this.map = map;
        this.objective = new Type[objective.length];
        for (int i = 0; i< objective.length; ++i) {
            this.objective[i] = Type.getType(objective[i]);
        }

        Square root = new Square(map[y][x], x, y, this.objective, null, null);
        squareQueueToAdd = new ArrayList<Square>();
        return root.breadthFirstThroughEnvironmentUntilObjectiveIsReached();
    }

    private void printMap(Type map[][]){
        for (int i = 0; i < map.length; ++i)
        {
            for (int j = 0; j < map[0].length; ++j)
            {
                System.out.print(map[i][j].ordinal());
            }
            System.out.println("");
        }
        System.out.println("-------------------");
    }

    class Square{
        Type type;
        private int x, y;
        private Type objective[];
        private Move howToGetHere;
        private Square gotHereFrom;

        protected Square(Type type, int x, int y, Type objective[], Move howToGetHere, Square gotHereFrom){
            this.type = type;
            this.x = x;
            this.y = y;
            this.objective = objective;
            this.howToGetHere = howToGetHere;
            this.gotHereFrom = gotHereFrom;
        }

        protected Move breadthFirstThroughEnvironmentUntilObjectiveIsReached(){
            squareQueue.add(this);
            visitedSquares.put(this.getStringCoordinate(), this);

            while (squareQueue.size() > 0){
                Iterator<Square> iter = squareQueue.iterator();
                while (iter.hasNext()){
                    Square square = iter.next();

                    Move successMove = square.processSquareInQueue();
                    if (successMove != null){
                        return successMove;
                    }
                }

                squareQueue = squareQueueToAdd;
                squareQueueToAdd = new ArrayList<Square>();
            }


            return Move.WAIT;
        }

        protected Move processSquareInQueue(){
            if (isSquareContainingObjective()) {
                return getFirstMoveFromRoot();
            }else{
                addNotVisitedAccessibleNeighbourSquaresToQueue(this, x, y);
                return null;
            }
        }

        private boolean isSquareBeforeRootSquare(){

            //no action since this is the root square
            if (gotHereFrom == null){
                return false;
            }
            if (gotHereFrom.gotHereFrom == null){
                return true;
            }
            return false;
        }

        private void addNotVisitedAccessibleNeighbourSquaresToQueue(Square origin, int xPos, int yPos){
            //Add all valid neighbour Squares
            try{
                addSquareToQueueIfAccessible(new Square(map[yPos - 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() - 1, objective, Move.UP, this));
                addSquareToQueueIfAccessible(new Square(map[yPos + 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() + 1, objective, Move.DOWN, this));
                addSquareToQueueIfAccessible(new Square(map[yPos][xPos - 1], origin.getXCoordinate() - 1, origin.getYCoordinate(), objective, Move.LEFT, this));
                addSquareToQueueIfAccessible(new Square(map[yPos][xPos + 1], origin.getXCoordinate() + 1, origin.getYCoordinate(), objective, Move.RIGHT, this));
            }catch (ArrayIndexOutOfBoundsException e){
                //do not add square since it is outside of the play board
            }
        }

        private void addSquareToQueueIfAccessible(Square square){
            if(square.isSquareVisitable()){
                squareQueueToAdd.add(square);
                visitedSquares.put(square.getStringCoordinate(), square);
            }
        }

        protected Move getFirstMoveFromRoot(){
            if (isSquareBeforeRootSquare()) {
                return howToGetHere;
            }else{
                if (gotHereFrom == null){
                    return null;
                }
                return gotHereFrom.getFirstMoveFromRoot();
            }
        }

        private String getStringCoordinate(){
            return Integer.toString(x) + "_" + Integer.toString(y);
        }

        private boolean isSquareVisitable(){
            if (type == Type.FENCE){
                return false;
            }

            if (visitedSquares.get(getStringCoordinate()) != null){
                return false;
            }

            return true;
        }

        private boolean isSquareContainingObjective(){
            for (int i = 0; i < objective.length; i++){
                if (type == objective[i]){
                    return true;
                }
            }
            return false;
        }

        protected int getXCoordinate(){
            return x;
        }

        protected int getYCoordinate(){
            return y;
        }
    }
}
