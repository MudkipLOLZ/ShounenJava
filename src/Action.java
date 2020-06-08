import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Action {
    private double dmg, knockBack, increment,xMo,yMo;
    private String[] data;
    private Hitbox hitBox;
    private int numOfFiles;
    private String name;
    private File datFile;
    private ArrayList<BufferedImage> animation = new ArrayList<>();
    public Action(String filePath) throws IOException {
        datFile = new File(filePath+"/"+"data.txt");
        try {
            data = new Scanner(new BufferedReader(new FileReader(datFile))).nextLine().split(" ");
        }catch (Exception e){

        }
        dmg = Double.parseDouble(data[0]);
        knockBack = Double.parseDouble(data[1]);
        increment = Double.parseDouble(data[2]);
        name = filePath.substring(filePath.indexOf("/")+1);
        numOfFiles = new File(filePath + "/spr/").list().length;
        for(int i=0;i<numOfFiles;i++){//add every image to the animation
            animation.add(ImageIO.read(new File(filePath + "/spr/" + i + ".png")));
//            try {
//                animation.add(ImageIO.read(new File(filePath + "/spr/" + i + ".png")));
//                hitBox = new Hitbox(filePath);
//            }
//            catch (IOException e){
//                System.err.println("Error while generating sprites for " +filePath.substring(0,filePath.indexOf("/"))+"'s "+name+": " + e.getMessage());
//            }
        }
        hitBox = new Hitbox(filePath);
        System.out.println(filePath.substring(0,filePath.indexOf("/"))+"'s "+name+" loaded.");
    }

    public int getDmg() {
        return (int) dmg;
    }

    public double getKnockBack() {
        return knockBack;
    }

    public double getIncrement() {
        return increment;
    }

    public ArrayList<BufferedImage> getAnimation() {
        return animation;
    }
    public String toString(){
        return name;
    }

    public boolean hitboxOverlapsWith(inGameEntity player,inGameEntity enemy, int i) {
        return hitBox.overlapsWith(player,enemy, i);
    }

    public Double getKnockTrajectory(int frameNum) {
        return hitBox.getCurrentTrajectory(frameNum);
    }

    public void drawHitbox(Graphics g, int frameNum, inGameEntity entity) {
        hitBox.drawHitbox(g,frameNum,entity);
    }
}
