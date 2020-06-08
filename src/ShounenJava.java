import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShounenJava extends JFrame {
    GamePanel Game;
    java.util.Timer myTimer;
    TimerTask tt;
    int count = 0;
    long before;
    public final int menu = 0;
    public final int lives = 1;
    public final int deathMatch = 2;
    public final int timed =3;
    public ShounenJava() throws IOException {
        setTitle("Shounen Java");
        setSize(1200, 675);
        setVisible(true);
        setResizable(false);
        setIconImage(ImageIO.read(new File("backs/4star.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myTimer = new java.util.Timer();

        tt = new TimerTask() {
            @Override
            public void run() {
                //Game.updateMousePos();
                decideScreen();
                Game.repaint();
                count += 1;
//                long aft = System.currentTimeMillis();
//                System.out.println(count * 1000 / (aft - before));
            }
        };
        Game = new GamePanel(this);
        add(Game);

    }

    public void start() {
        before = System.currentTimeMillis();
        myTimer.scheduleAtFixedRate(tt, 0, 16);
    }

    public static void main(String[] args) throws IOException {
        new ShounenJava();
    }
    public void Arena(){
        Game.animate();
        Game.film();
        Game.physics();
    }
    public void Menu(){
    }
    public void decideScreen(){
        if(Game.getCurrentScreen()==menu){
            Menu();
        }else if(Game.getCurrentScreen()==lives){
            Arena();
        }
    }
}
