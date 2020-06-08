import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.*;
import java.math.*;
public class Camera {
    private static Rectangle camRect,newRect;
    private  static BufferedImage feed;
    private static Graphics feedGraphics;
    private Stage map;
    private ArrayList<inGameEntity> entities;
    private final double aspRatio = 640.0/360.0;
    public Camera(ArrayList<inGameEntity> entitiesIn, Stage mapIn){
        camRect = new Rectangle(0,0,1200,675);
        newRect = new Rectangle(camRect);
        map = mapIn;
        entities = entitiesIn;
        feed = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        feedGraphics = feed.getGraphics();
    }
    public void update(){
        //width and height are slightly bigger than the greatest distance between entities
        // horizontally and vertically
        double width = deltaX()+400;
        double height = deltaY()+200;
        //we resize one dimension based on the other in order to maintain the aspect ratio
        //width will be based on height if
        if(width/height>aspRatio){
            height = width/aspRatio;
        }else if(width/height<aspRatio){
            width = height*aspRatio;
        }
        newRect.setSize((int)width,(int)height);
        newRect.setLocation((int)((getLeftmost()+getRightmost())/2-width/2),(int)((getHighest()+getLowest())/2-height/2));
        fixRect(newRect);
        camRect.grow((int) ((newRect.getWidth()-camRect.getWidth())*0.03),0);
        camRect.setSize((int)camRect.getWidth(),(int)(camRect.getWidth()/aspRatio));
        camRect.translate((int)(newRect.getX()-camRect.getX())/2,(int)(newRect.getY()-camRect.getY())/2);
        fixRect(camRect);
        feed = new BufferedImage((int)camRect.getWidth(),(int)camRect.getHeight(),BufferedImage.TYPE_INT_ARGB);
        feedGraphics = feed.getGraphics();
        map.drawOffset(feedGraphics,(int)-camRect.getX(),(int)-camRect.getY());
        for(inGameEntity entity:entities){
            entity.drawOffset(feedGraphics,(int)-camRect.getX(),(int)-camRect.getY());
        }
    }
    public int getLeftmost(){
        int lowest = 1200;
        for(inGameEntity dude:entities){
            if(dude.getX()<lowest){
                lowest = dude.getX();
            }
        }
        return lowest;
    }
    public int getRightmost(){
        int highest = 0;
        for(inGameEntity dude:entities){
            if(dude.getX()>highest){
                highest = dude.getX();
            }
        }
        return highest;
    }
    public int getHighest(){
        int highest = 675;
        for(inGameEntity dude:entities){
            if(dude.getY()+dude.getCurrentImg().getHeight()<highest){
                highest = dude.getY()+dude.getCurrentImg().getHeight()-30;
            }
        }
        return highest;
    }
    public int getLowest(){
        int lowest = 0;
        for(inGameEntity dude:entities){
            if(dude.getY()+dude.getCurrentImg().getHeight()>lowest){
                lowest = dude.getY()+dude.getCurrentImg().getHeight()-30;
            }
        }
        return lowest;
    }
    public int deltaX(){
        return getRightmost()-getLeftmost();
    }
    public int deltaY(){
        return getLowest()-getHighest();
    }
    public BufferedImage getFeed() {
        return scaleImage(feed,1200/camRect.getWidth());
    }
    public BufferedImage scaleImage(BufferedImage imageIn, double ratio) {
        //code for this found at https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
        BufferedImage before = imageIn;
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage((int) (ratio * w), (int) (h * ratio), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(ratio, ratio);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return scaleOp.filter(before, after);
    }
    public void showFeed(Graphics g){
        g.drawRect((int)camRect.getX(),(int)camRect.getY(),(int)camRect.getWidth(),(int)camRect.getHeight());
        g.drawImage(getFeed(),0,0,null);
    }
    public void fixRect(Rectangle rectIn){
        if(rectIn.getWidth()>1200 || rectIn.getHeight()>675){
            rectIn.setSize(1200, 675);
        }
        if (rectIn.getX()<0){
            rectIn.setLocation(0, (int) rectIn.getY());
        }
        if(rectIn.getY()<0){
            rectIn.setLocation((int) rectIn.getX(), 0);
        }
        if(rectIn.getMaxX()>1200){
            rectIn.setLocation((int) (1200-rectIn.getWidth()), (int) rectIn.getY());
        }
        if(rectIn.getMaxY()>675){
            rectIn.setLocation((int) rectIn.getX(), (int) (675-rectIn.getHeight()));
        }
    }
}
