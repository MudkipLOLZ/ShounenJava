import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Stage {
    private BufferedImage backround;
    private ArrayList<Platform> plats = new ArrayList<>();
    private Scanner infile;
    private String line;
    private String[] platData;
    public Stage(String name) throws IOException {
        backround = ImageIO.read(new File("maps/"+name+".png"));
        backround = scaleImage(backround, 1200.00 / 640.00);
        infile = new Scanner(new BufferedReader(new FileReader("maps/"+name+".txt")));
        try{
            while (true){
                line = infile.nextLine();
                platData=line.split(" ");
                plats.add(new Platform(Integer.parseInt(platData[0]),Integer.parseInt(platData[1]),Integer.parseInt(platData[2]),Integer.parseInt(platData[3])));
            }
        } catch (Exception e) {
            System.err.println("Error while generating platforms: " + e.getMessage());
        }
    }
    public void updateCollisions(inGameEntity dude){
        dude.setGrounded(false);
        for(Platform plat: plats){
            plat.updateCollision(dude);
        }
    }
    public void draw(Graphics g){
        g.drawImage(backround, 0, 0, null);
    }
    public void drawOffset(Graphics g,int xOff,int yOff){
        g.drawImage(backround, xOff, yOff, null);
        g.setColor(Color.black);
    }
    public BufferedImage scaleImage(BufferedImage imageIn, double ratio) {
        //code for this found at https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
        BufferedImage before = imageIn;
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage((int) (ratio * w), (int) (h * ratio), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(ratio, ratio);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return scaleOp.filter(before, after);
    }
    public double[] getPossibleSpawn(){
        int index = (int) (Math.random()*plats.size());
        Platform plat = plats.get(index);
        return new double[]{plat.getX()+(Math.random()*(plat.getWidth())),plat.getY()-30};
    }
}
