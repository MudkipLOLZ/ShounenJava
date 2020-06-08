import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class inGameEntity {
    private boolean grounded,canRecover;
    private Action action;
    private double x, y, xVel, yVel, frameNum;
    private int direction, jumpNum, stunCount, damage,lives,deathTimer;
    private Fighter character;
    private BufferedImage currentImg;
    private final int left = -1;
    private final int right = 1;
    private final double maxSpeed;
    public inGameEntity(Fighter dude, int xIn, int yIn) {
        character = dude;
        x = xIn;
        y = yIn;
        xVel = 0;
        yVel = 0;
        frameNum = 0;
        jumpNum = 2;
        grounded = false;
        canRecover = true;
        direction = left;
        action = character.getIdle();
        maxSpeed = 12 / character.getWalk().getIncrement();
        stunCount = 0;
        lives = 2;
    }

    public void animate() {//progresses the animation (changes the current image to the next in the animation) of the current action of the entity
        //assign an image to currentImage, then increase the frame number by one
        AffineTransform tx;
        AffineTransformOp op;
        currentImg = action.getAnimation().get((int) (getCurrentFrameNum()));
        //code for flipping images from https://stackoverflow.com/questions/9558981/flip-image-with-graphics2d
        if (direction == left) {//since all images are facing right, flip it horizontally if the entity is facing left
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-currentImg.getWidth(null), 0);
            op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            currentImg = op.filter(currentImg, null);
        }
        frameNum++;
        if (getCurrentFrameNum() >= action.getAnimation().size()) {//set the frame count to 0 at the end of the animation to loop
            if (!action.toString().equals("jump")) {
                frameNum = 0;
            } else {//stay at the end of the animation otherwise, for non looping animations like the jump
                frameNum = action.getIncrement() * action.getAnimation().size() - 1;
            }
            if (action.toString() != "walk") {
                if (isStunned()) {
                    setAction("knocked");
                } else if (grounded) {
                    setAction("idle");//once the end of the animation is reached, and is grounded,set the action to idle
                } else {
                    setAction("jump");
                }
            }
        }
    }

    public BufferedImage getCurrentImg() {
        return currentImg;
    }

    public void setAction(String newAction) {//set the action of the entity based on the string passed in
        if (!newAction.equals(action.toString())) {
            frameNum = 0;
        }
        if (newAction == "idle") {
            action = character.getIdle();
        } else if (newAction == "walk") {
            action = character.getWalk();
        } else if (newAction == "jump") {
            action = character.getJump();
        } else if (newAction == "knocked") {
            action = character.getKnocked();
        } else {
            if (newAction.endsWith("Air")) {
                if (newAction == "sAir") {
                    action = character.getsAir();
                    if(abs(xVel)<10){//prevent spamming for momentum
                        accelX(4*getDirection());
                    }
                } else if (newAction == "nAir") {
                    action = character.getnAir();
                }

            }else if (newAction=="recovery"){
                if(canRecover){
                    action = character.getRecovery();
                    setxVel(6*getDirection());
                    setyVel(-10);
                    canRecover = false;
                }
            }
                else {
                xVel = 0;
                if (newAction == "nSpecial") {
                    action = character.getnSpecial();
                } else if (newAction == "sSpecial") {
                    action = character.getsSpecial();
                } else if (newAction == "nLight") {
                    action = character.getnLight();
                } else if (newAction == "sLight") {
                    action = character.getsLight();
                }
            }
        }
    }

    public int getX() {
        return (int) x;
    }

    public double getMidX() {
        return x + getWidth();
    }

    public int getY() {
        return (int) y - currentImg.getHeight();
    }

    public String getAction() {
        return action.toString();
    }

    public void setDirection(String direc) {
        direction = direc == "left" ? left : right;
    }

    public void setxVel(double xVel) {
        this.xVel = xVel;
    }

    public void setyVel(double yVel) {
        this.yVel = yVel;
    }

    public void accelX(double amount) {
        xVel += amount;
        if ((action.toString().equals("walk") || action.toString().equals("jump")||action.toString().endsWith("Air")) && abs(xVel) > maxSpeed) {//prevents walking at greater than max speed
            xVel =maxSpeed*getDirection();
        }
    }

    public void accelY(double amount) {
        yVel += amount;
    }

    public void move() {
        x += xVel;
        y += yVel;
    }

    public void slowDown(double val) {
        if (abs(xVel) <= val) {
            xVel = 0;
        } else {
            xVel -= val * signum(xVel);
        }
    }

    public void applyDrag() {
        if (isStunned()) {
            slowDown(0.05);
            stunCount--;
        } else {
            slowDown(0.25);
        }
    }

    public int getTop() {
        return (int) y;
    }

    public int getWidth() {
        return currentImg.getWidth();
    }

    public int getxVel() {
        return (int) xVel;
    }

    public int getyVel() {
        return (int) yVel;
    }

    public boolean isStunned() {
        return stunCount > 0;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean state) {
        grounded = state;
        if (state) {
            jumpNum = 2;
            canRecover = true;
        }
    }

    public void setX(double xIn) {
        x = xIn + currentImg.getWidth() / 2;
    }

    public void setY(double yIn) {
        y = yIn;
    }

    public void resetJumps() {
        jumpNum = 2;
    }

    public void jump() {
        yVel = -9;
        grounded = false;
        action = character.getJump();
        frameNum = 0;
    }

    public void reduceJumps() {
        jumpNum--;
    }

    public void applyGravity() {
        if (isStunned()) {
            if (yVel < 3) {
                yVel += 0.1;
            }
        } else if (!grounded) {
            if (yVel < 10) {
                yVel += 0.3;
            }
        }
    }

    public boolean canJump() {
        return jumpNum > 0;
    }

    public void displaceSide(Rectangle plat) {
        if (x > plat.getX()) {
            setX(plat.getMaxX() + currentImg.getWidth());
        } else {
            setX(plat.getX() - currentImg.getWidth());
        }
    }

    public void goRight() {
        accelX(0.5);
        setDirection("right");
        if (getAction().equals("idle")) {
            setAction("walk");
        }
    }

    public void goLeft() {
        accelX(-0.5);
        setDirection("left");
        if (getAction().equals("idle")) {
            setAction("walk");
        }
    }

    public void draw(Graphics g) {
        g.drawImage(currentImg, (int) x - currentImg.getWidth() / 2, getY(), null);
    }

    public void drawOffset(Graphics g, int xOff, int yOff) {
        g.setColor(Color.black);
        g.drawImage(currentImg, (int) x - currentImg.getWidth() / 2 + xOff, getY() + yOff, null);
        //g.drawOval(pixel[0]*player.getDirection() + player.getX(), pixel[1] + player.getY()));
    }

    public boolean isAttacking() {//checks if the entity is using an attack right now
        return action.toString().startsWith("n") || action.toString().startsWith("s") || action.toString().startsWith("d")||action.toString().equals("recovery");
    }

    public void applyCollisions(inGameEntity enemy) {
        Double traj;
        Double magnitude;
        if (attackHits(enemy)) {
            //all effects of the hits go here,
            //and is applied to the enemy
            traj = action.getKnockTrajectory(getCurrentFrameNum());
            enemy.setAction("knocked");
            magnitude = action.getKnockBack() + enemy.getDmg();
            if (traj == null) {
                traj = atan2(enemy.getY() - getY(), enemy.getX() - getX());
                enemy.setxVel(magnitude * cos(traj));
                enemy.setyVel(magnitude * sin(traj));
            } else {
                enemy.setxVel(getDirection() * (magnitude * cos(action.getKnockTrajectory(getCurrentFrameNum()))));
                enemy.setyVel(magnitude * sin(action.getKnockTrajectory(getCurrentFrameNum())));
            }
            enemy.damage(action.getDmg());
            enemy.stun();

        }
    }

    private double getDmg() {
        return damage;
    }

    private void stun() {
        stunCount = 45;
    }

    private boolean attackHits(inGameEntity enemy) {//checking if the enemy gets hit by the attack
        if (isAttacking()) {
            if(!enemy.isImmune())
            {return action.hitboxOverlapsWith(this, enemy, getCurrentFrameNum());}
        }
        return false;
    }

    private boolean isImmune() {
        return stunCount>20;
    }

    public int getDirection() {
        return direction;
    }

    public int getCurrentFrameNum() {
        return (int) (frameNum / action.getIncrement());
    }

    public void drawHitbox(Graphics g) {
        action.drawHitbox(g, getCurrentFrameNum(), this);
    }

    public void damage(int dmg) {
        damage += dmg;

    }

    public boolean processInputs(boolean up, boolean down, boolean left, boolean right, boolean light, boolean special) {//returns the value for up
        if (!isStunned()) {//entity can only move if not stunned
            if (light) {//light attack
                if (isGrounded()) {//grounded attack
                    if (left || right) {
                        setAction("sLight");
                    } else {
                        setAction("nLight");
                    }
                } else {//aerial attack
                    if (left || right) {
                        setAction("sAir");
                    } else {
                        setAction("nAir");
                    }
                }
            } else if (special) {//heavy attack
                if (isGrounded()) {
                    if (left || right) {
                        setAction("sSpecial");
                    } else {
                        setAction("nSpecial");
                    }
                } else {
                    setAction("recovery");
                }
            }
            if (!isAttacking() || action.toString().endsWith("Air")) {//can only move during aerials or while not attacking
                if (right) {// going right
                    goRight();
                } else if (left) {//going left
                    goLeft();
                } else {//idle if no left or right input (for now)
                    applyDrag();
                    if (isGrounded()) {
                        setAction("idle");
                    }
                }
                if (up) {
                    if (canJump()) {
                        jump();
                        if (!isGrounded()) {
                            reduceJumps();
                        }
                    }
                    return false;
                }
            }
        }
        return up;
    }
    public void respawn(double[] point){
        if(deathTimer>1){
            deathTimer--;
            setX(-100);
            setY(-100);
        }
        else if(deathTimer==1){
            setX(point[0]);
            setY(point[1]);
            setxVel(0);
            setyVel(0);
            damage = 0;
            lives--;
            deathTimer=0;
            stunCount = 0;
        }else {
            deathTimer = 120;
        }

    }

    public int getDeathTimer(){
        return deathTimer;
    }

    public int getLives() {
        return lives;
    }
}