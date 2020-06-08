import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Hitbox {
    // A hitbox takes in a set of pictures for the animation, and all opaque pixels are added to the corresponding
    // frame index in the list
    private final ArrayList<ArrayList<int[]>> hitboxFramePixels = new ArrayList<>();
    private final ArrayList<int[]> attackEpicenters = new ArrayList<>();
    private final ArrayList<Integer> widths = new ArrayList<Integer>();
    public Hitbox(String filePath) throws IOException {
        for (int i = 0; i < new File(filePath + "/hitSpr/").list().length; i++) {//add every image to the animation
            BufferedImage hitboxFrame = ImageIO.read(new File(filePath + "/hitSpr/" + i + ".png"));
            hitboxFramePixels.add(getOpaquePixels(hitboxFrame));
            attackEpicenters.add(findAttackEpicenter(hitboxFrame));
            widths.add(hitboxFrame.getWidth());
        }
    }

    private ArrayList getOpaquePixels(BufferedImage image) {
        ArrayList<int[]> opaquePixels = new ArrayList<>();
        int alpha;
        Color pixelCol;
        //loop through every pixel in the image, and check if its alpha value is 255,
        // that way we know what pixels that the hitbox takes up.
        for (int y = 0; y < image.getHeight(); y++) {// help from https://stackoverflow.com/questions/10726594/bufferedimage-getrgbx-y-does-not-yield-alpha
            for (int x = 0; x < image.getWidth(); x++) {
                pixelCol = new Color(image.getRGB(x, y), true);
                if (pixelCol.getAlpha() == 255) {
                    opaquePixels.add(new int[]{x, y});
                }
            }
        }
        return opaquePixels;
    }

    public boolean overlapsWith(inGameEntity player, inGameEntity enemy, int i) {
        Rectangle enemyRect = new Rectangle(enemy.getX()-15, enemy.getY(), 30, 50);


        for (int[] pixel : hitboxFramePixels.get(i)) {
            if (enemyRect.contains((pixel[0]-widths.get(i)/2)*player.getDirection() + player.getX(), pixel[1] + player.getY())) {
                return true;
            }
        }
        return false;
    }
    public int[] findAttackEpicenter(BufferedImage image){
        Color pixelCol;
        //loop through every pixel in the image, and check if its alpha value is 255,
        // that way we know what pixels that the hitbox takes up.
        for (int y = 0; y < image.getHeight(); y++) {// help from https://stackoverflow.com/questions/10726594/bufferedimage-getrgbx-y-does-not-yield-alpha
            for (int x = 0; x < image.getWidth(); x++) {
                pixelCol = new Color(image.getRGB(x, y), true);
                if (pixelCol.getRed() == 255&&pixelCol.getBlue()==0&&pixelCol.getGreen()==0) {
                    return new int[]{x-image.getWidth()/2,-(y-image.getHeight())};
                }
            }
        }
        return new int[]{};
    }
    public Double getCurrentTrajectory(int frameNum) {//gets the trajectory of the attack based on the epicenter quoordinates.
                                                        // returns null if none is found (trajectory will be directly away from the attacker)
        double angle;
//        for(int[] point:attackEpicenters){
//            try {
//                System.out.println("("+point[0]+","+point[1]+")");
//            }catch (Exception e){
//
//            }
//        }
        int firstPoint = 0;//the first given attack epicenter quoordinate
        for(int[] epicenter:attackEpicenters){
            if(epicenter.length>0){
                firstPoint = attackEpicenters.indexOf(epicenter);
            }
        }
        try {
            if(frameNum==firstPoint){
                angle = Math.atan2(attackEpicenters.get(frameNum+1)[1]- attackEpicenters.get(frameNum)[1],attackEpicenters.get(frameNum+1)[0]- attackEpicenters.get(frameNum)[0]);

            }else {
                angle = Math.atan2(attackEpicenters.get(frameNum)[1]-attackEpicenters.get(frameNum-1)[1],attackEpicenters.get(frameNum)[0]-attackEpicenters.get(frameNum-1)[0]);
            }
        } catch (Exception e) {
            return null;
        }
        return angle;
    }

    public void drawHitbox(Graphics g,int frameNum,inGameEntity player) {
        g.setColor(Color.black);
        for (int[] pixel : hitboxFramePixels.get(frameNum)) {
            g.drawOval((pixel[0]-widths.get(frameNum)/2)*player.getDirection() + player.getX(), pixel[1] + player.getY(),1,1);
        }
    }
}
