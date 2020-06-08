import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.ArrayList;

public class Platform {
    private final Rectangle platform;
    private final double leftEdge,rightEdge,topEdge,botEdge;
    public Platform(int x ,int y, int width, int height){
        platform = new Rectangle(x,y,width,height);
        leftEdge = platform.getX();
        rightEdge = platform.getMaxX();
        topEdge = platform.getY();
        botEdge = platform.getMaxY();
    }
    private void topCollide(inGameEntity dude){
        dude.setY((int) topEdge+1);
        if(dude.isStunned()){//if the player is knocked back
            dude.setyVel(dude.getyVel()*-1);//reverse the vertical velocity
        }
        else{
            dude.setGrounded(true);
            dude.setyVel(0);
        }
    }
    private void botCollide(inGameEntity dude){
        dude.setY((int) botEdge+dude.getCurrentImg().getHeight());
        if(dude.isStunned()){//if the player is knocked back
            dude.setyVel(-dude.getyVel());//reverse the vertical velocity
        }
        else {
            dude.setyVel(0);
        }
    }
    private void sideCollide(inGameEntity dude){
        dude.displaceSide(platform);
        if(dude.isStunned()){
            dude.setxVel(-dude.getxVel());
        }else{
            dude.setxVel(0);
        }
    }
    public int getWidth(){
        return (int) platform.getWidth();
    }
    public int getX(){
        return (int) platform.getX();
    }
    public int getY(){
        return (int) platform.getY();
    }
    public void updateCollision(inGameEntity dude){//checks entity collision, and moved the player based on whether it hits the top, bottom, or side of the rectangle
        int dudeTop = dude.getY();
        int dudeBot = dude.getY()+dude.getCurrentImg().getHeight();
        int dudeRight = dude.getX()+dude.getWidth()/2;
        int dudeLeft = dude.getX()-dude.getWidth()/2;
        final int left = 0;
        final int right = 1;
        final int top = 2;
        final int bottom =3;
        ArrayList<Integer> possibilities = new ArrayList<>();
        for(int i=0;i<4;i++){
            possibilities.add(i);
        }
        Rectangle playerRect = new Rectangle(dudeLeft,dudeTop,dude.getWidth(),dude.getCurrentImg().getHeight());
        Rectangle overlap = platform.intersection(playerRect);
        //check for collision in general (if the dude overlaps any part of the rectangle);
        if (platform.contains(overlap)){
            //based on the ratio of the length and the width of the overlap, we can determine if it hits the side or the top and bottom
            //NOTE: this will only work properly if the velocity of the entity isn't a very high amount,
            //and if the entity's picture isn't super skinny.
            if(platform.getHeight()==2){//for semi permeable platforms
                if(dude.getyVel()>=0){
                    topCollide(dude);
                }
            }
            else if(overlap.getWidth()>overlap.getHeight()){//top of bottom collision
                if (overlap.getY()==platform.getY()){
                    topCollide(dude);
                }else{
                    botCollide(dude);
                }
            }else{
                sideCollide(dude);
            }
            //now we have to eliminate some impossible cases based on the entity's velocity
//            if(dude.getxVel()>0){//cannot impact the right wall if the dude is moving right
//                possibilities.remove(right);
//            }
//            if(dude.getxVel()<0){//can't collide with left wall if he's moving left
//                possibilities.remove(left);
//            }
//            if(dude.getyVel()>0){//can't hit the bottom if he's moving down
//                possibilities.remove(bottom);
//            }
//            if(dude.getyVel()<0){//can't land on the top if he's moving up
//                possibilities.remove(top);
//            }
//            //now that we eliminated the possibilities, there can only be 1 or 2 possible sides hit
//            if(possibilities.size()==1){
//                if(possibilities.get(0)==top){
//                    topCollide(dude);
//                }else if(possibilities.get(0)==bottom){
//                    botCollide(dude);
//                }else{
//                    sideCollide(dude);
//                }
//            }
        }
    }

}
