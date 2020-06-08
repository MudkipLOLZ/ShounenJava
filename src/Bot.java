import java.util.ArrayList;

public class Bot extends inGameEntity {
    private inGameEntity target;
    private int mentalDelayCount;
    boolean left,right,up,down,light,special;

    public Bot(Fighter dude, int xIn, int yIn) {
        super(dude, xIn, yIn);
        mentalDelayCount = 40;
    }
    public void findTarget(ArrayList<inGameEntity> entities){//finds a random target
        while (true) {
            int index = (int) (Math.random() * entities.size());
            target = entities.get(index);
            if(this!=target){//reroll the target if it targeted itself
                break;
            }
        }
    }
    public void think(){//Ai for the bot
        mentalDelayCount--;
        if(mentalDelayCount<=0){//rethink every 25/60 seconds
            left=false;
            right=false;
            up=false;
            down = false;
            light = false;
            special = false;
            if(target.getX()-30>getX()){//the target is to the right of the bot
                right = true;
                if(!isGrounded()&&getyVel()>1&&target.getY()-5<getY()){//if the bot is falling at a extraordinary rate,
                                                                        //he should jump
                    up = true;
                    if(!canJump()){
                        special = true;//recovery if he cant jump
                    }
                }
            }else if(target.getX()+30<getX()){//target is to the left of the bot
                left = true;
                if(!isGrounded()&&getyVel()>3&&target.getY()-5<getY()){
                    up = true;
                    if(!canJump()){
                        special = true;//recovery if he cant jump
                    }
                }
            }else if (target.getY()+10<getY()){//the target is above the bot
                up = true;
            }if(Math.hypot(Math.abs((getX()-target.getX())),Math.abs((getY()-target.getY())))<80){//the target is within range of an attack (hopefully)
                double r = Math.random();//choose aither a light or heavy attack to do
                if(r>0.5){
                    light = true;
                }else{
                    special = true;
                }
            }
            mentalDelayCount = 25;
        }

        processInputs(up,down,left,right,light,special);
    }
}
