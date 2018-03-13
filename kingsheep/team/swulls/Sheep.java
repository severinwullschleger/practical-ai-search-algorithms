package kingsheep.team.swulls;

import kingsheep.*;

public class Sheep extends SwullsCreature {

    private boolean noMoreFoodAvailable = false;
    private Move lastMove;

    public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
        counter = 0;
    }

    @Override
    protected void think(Type map[][]) {

        counter++;

        if(alive && !noMoreFoodAvailable){

            move = getMove(map, getObjectives());
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
    }

    @Override
    protected char[] getObjectives() {
        char[] objectives = new char[2];
        objectives[0] = 'r';
        objectives[1] = 'g';
        return objectives;
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
        }else if (move == Move.RIGHT) {
            x = this.x + 1;
            y = this.y;
        }else {
            x = this.x;
            y = this.y;
        }

        if (!isCoordinateValid(map, y, x)){
            return false;
        }

        Type type = map[y][x];

        if(type == Type.FENCE )//|| type == Type.WOLF2 || type == Type.SHEEP2)
            return false;
        else if (this.type == Type.SHEEP1 && type == Type.WOLF2)
            return false;
        else if (this.type == Type.SHEEP2 && type == Type.WOLF1)
            return false;
        else
            return true;
    }
}
