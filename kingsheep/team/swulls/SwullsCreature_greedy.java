package kingsheep.team.swulls;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.*;

/**
 * Created by kama on 01.03.16.
 */
public abstract class SwullsCreature_greedy extends Creature {

    private HashMap<String, Square> visitedSquares;
    private ArrayList<Square> squareQueue;
    private ArrayList<Square> squareQueueToAdd;
    private Type map[][];
    protected Type objective[];
    protected int counter;


    public SwullsCreature_greedy(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    public String getNickname() {
        return "Walti Ruedisueli";
    }

    protected Move getMove(Type map[][], char[] objective) {
        visitedSquares = new HashMap<String, Square>();
        squareQueue = new ArrayList<Square>();

        this.map = map;
        this.objective = new Type[objective.length];
        for (int i = 0; i < objective.length; ++i) {
            this.objective[i] = Type.getType(objective[i]);
        }

        Square root = new Square(map[y][x], x, y, this.objective, null, null);
        squareQueueToAdd = new ArrayList<Square>();
        return root.aStarThroughEnvironmentUntilObjectiveIsReached();
    }

    private void printMap(Type map[][]) {
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[0].length; ++j) {
                System.out.print(map[i][j].ordinal());
            }
            System.out.println("");
        }
        System.out.println("-------------------");
    }

    abstract protected char[] getObjectives();

    protected List<String> findObjectives(Type[] objectives) {
        List<Type> objectiveList = Arrays.asList(objectives);
        List<String> objectivePositions = new ArrayList<String>();
        for (int iy = 0; iy < map.length; iy++) {
            for (int ix = 0; ix < map.length; ix++) {
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

    protected boolean isCoordinateValid(Type map[][], int y, int x){
        try{
            Type type = map[y][x];
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
        return true;
    }

    class Square {
        Type squareType;
        private int x, y;
        private Type objective[];
        private Move howToGetHere;
        private Square gotHereFrom;

        protected Square(Type squareType, int x, int y, Type objective[], Move howToGetHere, Square gotHereFrom) {
            this.squareType = squareType;
            this.x = x;
            this.y = y;
            this.objective = objective;
            this.howToGetHere = howToGetHere;
            this.gotHereFrom = gotHereFrom;
        }

        protected Move breadthFirstThroughEnvironmentUntilObjectiveIsReached() {
            squareQueue.add(this);
            visitedSquares.put(this.getStringCoordinate(), this);

            while (squareQueue.size() > 0) {
                Iterator<Square> iter = squareQueue.iterator();
                while (iter.hasNext()) {
                    Square square = iter.next();

                    Move successMove = square.processSquareInQueue();
                    if (successMove != null) {
                        return successMove;
                    }
                }

                squareQueue = squareQueueToAdd;
                squareQueueToAdd = new ArrayList<Square>();
            }


            return Move.WAIT;
        }

        protected Move aStarThroughEnvironmentUntilObjectiveIsReached() {
            squareQueue.add(this);
            visitedSquares.put(this.getStringCoordinate(), this);

            Move successMove = null;
            boolean isFirstCalculation = true;
            int lowestCostsOverall = 0;

            // always calculate till fringe is empty
            while (squareQueue.size() > 0) {

                int lowestCosts = squareQueue.get(0).getTotalEstimatedCosts();
                Square lowestCostSquare = squareQueue.get(0);

                for (int i = 1; i < squareQueue.size(); i++) {
                    Square square = squareQueue.get(i);
                    int costs = squareQueue.get(i).getTotalEstimatedCosts();
                    if (costs < lowestCosts) {
                        lowestCostSquare = square;
                        lowestCosts = costs;
                    }
                }
                squareQueue.remove(lowestCostSquare);
                Move move = lowestCostSquare.processSquareInQueue();

                if (successMove == null || isFirstCalculation || lowestCosts < lowestCostsOverall) {
                    successMove = move;
                    lowestCostsOverall = lowestCosts;
                    isFirstCalculation = false;
                }

                squareQueue.addAll(squareQueueToAdd);
                squareQueueToAdd = new ArrayList<Square>();
            }
            if (successMove != null)
                return successMove;
            else
                return Move.WAIT;
        }

        private int getTotalEstimatedCosts() {
            return getRealCosts() + getEstimatedCosts();
        }

        private int getRealCosts() {
            if (gotHereFrom == null) {
                return 0;
            }
            return 1 + gotHereFrom.getRealCosts();
        }

        private int getEstimatedCosts() {
            List<String> objectivePositions = findObjectives(objective);
            if (objectivePositions.size() > 0) {
                String squarePos = y + "_" + x;
                return estimateCostsBetween(squarePos, objectivePositions);
            } else
                return 10000000;

//            String objectivePosition = findObjective(objectives);
//            if (objectivePosition != null) {
//                String squarePos = y + "_" + x;
//                return estimateCostsBetween(squarePos, objectivePosition);
//            }
//            else
//                return 10000000;
        }

        private int estimateCostsBetween(String squarePos, List<String> objectivePos) {
            ArrayList<Integer> distances = new ArrayList<Integer>(objectivePos.size());
            String[] squarePositions = squarePos.split("_");

            for (String op : objectivePos) {
                String[] objectivePositions = op.split("_");
                int yDistance = Math.abs(Integer.valueOf(squarePositions[0]) - Integer.valueOf(objectivePositions[0]));
                int xDistance = Math.abs(Integer.valueOf(squarePositions[1]) - Integer.valueOf(objectivePositions[1]));
                int reward = Integer.valueOf(objectivePositions[2]);
                distances.add(yDistance + xDistance - reward);
            }
            return Collections.min(distances);
        }

        protected Move processSquareInQueue() {
            if (isSquareContainingObjective()) {
                return getFirstMoveFromRoot();
            } else {
                addNotVisitedAccessibleNeighbourSquaresToQueue(this, x, y);
                return null;
            }
        }

        private boolean isSquareBeforeRootSquare() {

            //no action since this is the root square
            if (gotHereFrom == null) {
                return false;
            }
            if (gotHereFrom.gotHereFrom == null) {
                return true;
            }
            return false;
        }

        private void addNotVisitedAccessibleNeighbourSquaresToQueue(Square origin, int xPos, int yPos) {
            //Add all valid neighbour Squares
            try {
                addSquareToQueueIfAccessible(new Square(map[yPos - 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() - 1, objective, Move.UP, this));
                addSquareToQueueIfAccessible(new Square(map[yPos + 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() + 1, objective, Move.DOWN, this));
                addSquareToQueueIfAccessible(new Square(map[yPos][xPos - 1], origin.getXCoordinate() - 1, origin.getYCoordinate(), objective, Move.LEFT, this));
                addSquareToQueueIfAccessible(new Square(map[yPos][xPos + 1], origin.getXCoordinate() + 1, origin.getYCoordinate(), objective, Move.RIGHT, this));
            } catch (ArrayIndexOutOfBoundsException e) {
                //do not add square since it is outside of the play board
            }
        }

        private void addSquareToQueueIfAccessible(Square square) {
            if (square.isSquareVisitable()) {
                squareQueueToAdd.add(square);
                visitedSquares.put(square.getStringCoordinate(), square);
            }
        }

        protected Move getFirstMoveFromRoot() {
            if (isSquareBeforeRootSquare()) {
                return howToGetHere;
            } else {
                if (gotHereFrom == null) {
                    return null;
                }
                return gotHereFrom.getFirstMoveFromRoot();
            }
        }

        private String getStringCoordinate() {
            return Integer.toString(x) + "_" + Integer.toString(y);
        }

        private boolean isSquareVisitable() {
            if (squareType == Type.FENCE)
                return false;

            //Square not visitable if its close (one after root/source) and wolf stands on it
            if (gotHereFrom.gotHereFrom == null && (squareType == Type.WOLF1 || squareType == Type.WOLF2  ))
                return false;

            //check if a potential field is dangerous
            if ((type == Type.SHEEP2 || type == Type.SHEEP1)) {
                //if sheeps second step
                if (counter % 2 == 0){
                    if (gotHereFrom.gotHereFrom == null)
                        if (!isSquareSafe())
                            return false;
                }else {
                    if (gotHereFrom.gotHereFrom != null && gotHereFrom.gotHereFrom.gotHereFrom == null)
                        if (!isSquareSafe())
                            return false;
                }
            }


            if (visitedSquares.get(getStringCoordinate()) != null) {
                return false;
            }

            return true;
        }

        private boolean isSquareSafe() {
            ArrayList<Type> surroundingSquares = new ArrayList<Type>(4);
            if (isCoordinateValid(map, y-1, x)) surroundingSquares.add(map[y-1][x]);
            if (isCoordinateValid(map, y+1, x)) surroundingSquares.add(map[y+1][x]);
            if (isCoordinateValid(map, y, x-1)) surroundingSquares.add(map[y][x-1]);
            if (isCoordinateValid(map, y, x+1)) surroundingSquares.add(map[y][x+1]);

            if (type == Type.SHEEP1 && surroundingSquares.contains(Type.WOLF2))
                return false;
            if (type == Type.SHEEP2 && surroundingSquares.contains(Type.WOLF1))
                return false;

            //handle bug if opponent wolf didnt move yet. (swap from P2 to P1)
            if (counter <= 2
                && (type == Type.SHEEP1 || type == Type.SHEEP2)
                && (surroundingSquares.contains(Type.WOLF1)
                    || surroundingSquares.contains(Type.WOLF2)) )
                return false;

            return true;
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
    }
}
