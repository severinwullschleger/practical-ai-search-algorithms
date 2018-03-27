package kingsheep.team.swulls;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.*;

/**
 * Created by kama on 01.03.16.
 */
public abstract class UzhShortNameCreature extends Creature {

    private HashMap<String, Square> visitedSquares;
    private ArrayList<Square> squareQueue;
    private ArrayList<Square> squareQueueToAdd;
    protected Type map[][];
    protected Type objectives[];
    protected int counter;
    protected Type opponentWolf;
    protected int cutOffDepth;
    protected boolean findingObjectiveMode;


    public UzhShortNameCreature(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    public String getNickname() {
        return "Walti Ruedisueli";
    }

    protected Move getMove(Type map[][], char[] objective) {
        visitedSquares = new HashMap<String, Square>();
        squareQueue = new ArrayList<Square>();
        cutOffDepth = 8;
        findingObjectiveMode = false;

        this.map = map;
        this.objectives = new Type[objective.length];
        for (int i = 0; i < objective.length; ++i) {
            this.objectives[i] = Type.getType(objective[i]);
        }

        Square root = new Square(map[y][x], x, y, this.objectives, null, null, 0);
        squareQueueToAdd = new ArrayList<Square>();
        return root.getMinMaxMove();
    }

    abstract protected char[] getObjectives();

    abstract protected boolean isSquareSafe(int x, int y);

    protected boolean isCoordinateValid(Type map[][], int y, int x) {
        try {
            Type type = map[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    protected abstract List<String> findObjectives();

    class Square {
        Type squareType;
        private int x, y;
        private Type objective[];
        private Move howToGetHere;
        private Square gotHereFrom;
        private int depth;

        protected Square(Type squareType, int x, int y, Type objective[], Move howToGetHere, Square gotHereFrom, int depth) {
            this.squareType = squareType;
            this.x = x;
            this.y = y;
            this.objective = objective;
            this.howToGetHere = howToGetHere;
            this.gotHereFrom = gotHereFrom;
            this.depth = depth;
        }

        protected Move getMinMaxMove() {
            processSquareInTree();
            return getMaximumValueMove();
        }

        private Move getMaximumValueMove() {
            int maxValue = 0;
            Move maxValueMove = null;
            for (Square s : squareQueue) {
                int value = s.getValue();
                if (maxValueMove == null || maxValue < value) {
                    maxValue = value;
                    maxValueMove = s.getFirstMoveFromPath();
                }
            }
            if (maxValue == cutOffDepth * -1) {
                List<String> objs = findObjectives();
                if (objs.size() == 0)
                    return maxValueMove;
                else
                    return getMoveToFarAwayObjective();
            }

            if (maxValueMove == null)
                return Move.WAIT;

            return maxValueMove;
        }

        private Move getMoveToFarAwayObjective() {
            findingObjectiveMode = true;
            squareQueue = new ArrayList<Square>();
            visitedSquares = new HashMap<String, Square>();
            return breadthFirstThroughEnvironmentUntilObjectiveIsReached();
        }

        protected Move processSquareInQueue(){
            if (isSquareContainingObjective()) {
                return getFirstMoveFromPath();
            }else{
                addAccessibleNeighbourSquaresToQueue(this, x, y);
                return null;
            }
        }

        private int getValue() {
            if (gotHereFrom == null)
                return 0;

            int reward = 0;
            if (Arrays.asList(objectives).contains(squareType)) {
                if (squareType.equals(Type.RHUBARB))
                    reward = 5;
                if (squareType.equals(Type.GRASS))
                    reward = 1;
                if (squareType.equals(Type.WOLF1) || squareType.equals(Type.WOLF2))
                    reward = 10;
            }

            return gotHereFrom.getValue() - 1 + reward;
        }

        protected void processSquareInTree() {
            if (this.depth < cutOffDepth) {
                addAccessibleNeighbourSquaresToQueue(this, x, y);
            }

        }

        private void addAccessibleNeighbourSquaresToQueue(Square origin, int xPos, int yPos) {
            //Add all valid neighbour Squares
            try {
                addPathToQueueIfAccessible(new Square(map[yPos][xPos], origin.getXCoordinate(), origin.getYCoordinate(), objective, Move.WAIT, this, this.depth + 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
            try {
                addPathToQueueIfAccessible(new Square(map[yPos][xPos - 1], origin.getXCoordinate() - 1, origin.getYCoordinate(), objective, Move.LEFT, this, this.depth + 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
            try {
                addPathToQueueIfAccessible(new Square(map[yPos][xPos + 1], origin.getXCoordinate() + 1, origin.getYCoordinate(), objective, Move.RIGHT, this, this.depth + 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
            try {
                addPathToQueueIfAccessible(new Square(map[yPos - 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() - 1, objective, Move.UP, this, this.depth + 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
            try {
                addPathToQueueIfAccessible(new Square(map[yPos + 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() + 1, objective, Move.DOWN, this, this.depth + 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
        }

        private void addPathToQueueIfAccessible(Square square) {
            if (!findingObjectiveMode) {
                if (square.isSquareVisitable()) {
                    //visitedSquares.put(square.getStringCoordinate(), square);
                    square.processSquareInTree();

                    if (square.depth == cutOffDepth)
                        squareQueue.add(square);
                }
            }
            else {
                if(square.isSquareVisitable()){
                    squareQueueToAdd.add(square);
                    visitedSquares.put(square.getStringCoordinate(), square);
                }
            }
        }

        private boolean isSquareVisitable() {
            if (squareType == Type.FENCE)
                return false;

            //otherwise loops are possible
            if (visitedSquares.get(getStringCoordinate()) != null) {
                return false;
            }

            //Square not visitable if its close (one after root/source) and wolf stands on it
            if ( gotHereFrom.gotHereFrom == null
                    && ( squareType == Type.WOLF1
                        || squareType == Type.WOLF2
                        || squareType.equals(Type.SHEEP2) ) )
//                        || (type.equals(Type.SHEEP1) && squareType.equals(Type.SHEEP2))
//                        || (type.equals(Type.SHEEP2) && squareType.equals(Type.SHEEP1)) ) )
                return false;

            //check if a potential field is dangerous
            if ((type == Type.SHEEP2 || type == Type.SHEEP1)) {
                //if sheeps second step
                if (counter % 2 == 0) {
                    if (gotHereFrom.gotHereFrom == null)
                        if (!isSquareSafe(x, y))
                            return false;
                } else {
                    if (gotHereFrom.gotHereFrom != null && gotHereFrom.gotHereFrom.gotHereFrom == null)
                        if (!isSquareSafe(x, y))
                            return false;
                }
            }

            return true;
        }

        protected Move getFirstMoveFromPath() {
            if (isSquareOneMoveAfterRoot()) {
                return howToGetHere;
            } else {
                if (gotHereFrom == null) {
                    return null;
                }
                return gotHereFrom.getFirstMoveFromPath();
            }
        }

        private boolean isSquareOneMoveAfterRoot() {

            //no action since this is the root square
            if (gotHereFrom == null) {
                return false;
            }
            if (gotHereFrom.gotHereFrom == null) {
                return true;
            }
            return false;
        }

        private String getStringCoordinate() {
            return Integer.toString(x) + "_" + Integer.toString(y);
        }

        private boolean isSquareContainingObjective() {
            for (int i = 0; i < objective.length; i++) {
                if (squareType == objective[i]) {
                    return true;
                }
            }
            return false;
        }

        protected int getXCoordinate() {
            return x;
        }

        protected int getYCoordinate() {
            return y;
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
    }
}
