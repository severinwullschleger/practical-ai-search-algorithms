package kingsheep.team.greedy;

import kingsheep.Simulator;
import kingsheep.Type;

public class Sheep extends GreedyCreature {
    private boolean noMoreFoodAvailable = false;
    private Move lastMove;
    private int counter = 0;

    public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    private boolean isSquareSafe(Type map[][], Move move){
        int x, y;

        if (move == Move.UP){
            x = this.x;
            y = this.y - 1;
        }else if (move == Move.DOWN){
            x = this.x;
            y = this.y + 1;
        }else if (move == Move.LEFT){
            x = this.x - 1;
            y = this.y;
        }else{
            x = this.x + 1;
            y = this.y;
        }

        if (!isCoordinateValid(map, y, x)){
            return false;
        }

        Type type = map[y][x];

        if(type == Type.FENCE || type == Type.WOLF2 || type == Type.SHEEP2){
            return false;
        }
        return true;
    }

    private boolean isCoordinateValid(Type map[][], int y, int x){
        try{
            Type type = map[y][x];
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
        return true;
    }

    private void fleeFromBadWolf(Type map[][]){

        if (isSquareSafe(map, lastMove)){
            move = lastMove;
            return;
        }

        if (isSquareSafe(map, Move.DOWN)){
            move = Move.DOWN;
        }else if (isSquareSafe(map, Move.RIGHT)){
            move = Move.RIGHT;
        }else if (isSquareSafe(map, Move.UP)){
            move = Move.UP;
        }else{
            move = Move.LEFT;
        }
    }

    protected void think(Type map[][]) {

        if(alive && !noMoreFoodAvailable){
            char[] objectives = new char[2];
            objectives[0] = 'r';
            objectives[1] = 'g';

            move = getGreedyAction(map, objectives);
            if (move == null){
                move = Move.WAIT;
            }

            if(move == Move.WAIT){
                noMoreFoodAvailable = true;
                fleeFromBadWolf(map);
            }

        }else{
            //focusing on escaping the wolf
            fleeFromBadWolf(map);
        }
        lastMove = move;

        ++counter;
    }
}
