import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener {
    private boolean[] keys;
    private boolean up, w, enter, left, right, down, esc;
    private BufferedImage menuBack, title, single, two, dM, tmd, lvs, numOfBots;
    private BufferedImage[] blastFrames;
    private ShounenJava mainFrame;
    private inGameEntity player1, player2;
    private Bot testEntity3;
    private Stage currentStage;
    private final Fighter goku, naruto, killua, deku;
    private ArrayList<inGameEntity> entities = new ArrayList<>();
    private Camera actionCam;
    private Rectangle screenRect;
    //different screens
    public final int menu = 0;
    public final int lives = 1;
    public final int deathMatch = 2;
    public final int timed = 3;
    public final int player1Wins = 4;
    public final int player2Wins = 5;
    public final int gameOver = 6;
    //different menus
    public final int playerNumSelect = 0;
    public final int gameModeSelect = 1;
    public final int botNumSelect = 2;
    public int currentScreen = menu;
    public int backOffset, currentMenu, mx, my, selectedButton, numBots, numPlayers, selectedGameMode, alphaVal, screenTime;
    public ArrayList<Rectangle>[] buttonList;
    public final Fighter[] fighterList;
    public ArrayList<Bot> botList;
    public ArrayList<BufferedImage[]> buttonTexts;

    //public final Rectangle singlePlayer, twoPlayer,stock,deathMtch,timd;
    public GamePanel(ShounenJava a) throws IOException {
        setSize(1200, 675);
        addKeyListener(this);
        screenRect = new Rectangle(0, 0, 1200, 675);
        goku = new Fighter("goku");
        killua = new Fighter("killua");
        naruto = new Fighter("naruto");
        deku = new Fighter("midoriya");
        fighterList = new Fighter[]{naruto, deku, goku, killua};
        keys = new boolean[KeyEvent.KEY_LAST + 1];
        buttonTexts = new ArrayList<>();
        player1 = new inGameEntity(naruto, 200, 200);
        player2 = new inGameEntity(naruto, 1000, 200);
        testEntity3 = new Bot(killua, 1000, 200);
        menuBack = ImageIO.read(new File("backs/menuBack.png"));
        single = ImageIO.read(new File("backs/singlePlayer.png"));
        two = ImageIO.read(new File("backs/twoPlayer.png"));
        title = ImageIO.read(new File("backs/title.png"));
        dM = ImageIO.read(new File("backs/deathmatch.png"));
        lvs = ImageIO.read(new File("backs/lives.png"));
        tmd = ImageIO.read(new File("backs/timed.png"));
        numOfBots = ImageIO.read(new File("backs/numBots.png"));
        menuBack = scaleImage(menuBack, (double) (this.getHeight()) / (double) (menuBack.getHeight()));
        currentStage = new Stage("0");
        mainFrame = a;
        entities.add(player1);
        actionCam = new Camera(entities, currentStage);
        backOffset = 0;
        buttonList = new ArrayList[3];
        for (int i = 0; i < buttonList.length; i++) {
            buttonList[i] = new ArrayList<>();
        }
        buttonList[0].add(new Rectangle(getWidth() / 2 - 250, getHeight() / 2 + 25, 500, 100));
        buttonList[0].add(new Rectangle(getWidth() / 2 - 250, getHeight() / 2 + 150, 500, 100));
        for (int i = 0; i < 3; i++) {
            buttonList[1].add(new Rectangle(80 + i * 4 * 80, 100 + i * 3 * 55, 400, 150));
        }
        buttonList[2].add(new Rectangle(getWidth() / 2 - 200, getHeight() / 2 - 100, 400, 200));
        currentMenu = 0;
        selectedButton = playerNumSelect;
        System.out.println("Use the arrow keys and enter to navigate");
        botList = new ArrayList<>();
        buttonTexts.add(new BufferedImage[]{single, two});
        buttonTexts.add(new BufferedImage[]{lvs, tmd, dM});
        buttonTexts.add(new BufferedImage[]{numOfBots});
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

    public int getCurrentScreen() {
        return currentScreen;
    }

    public void film() {
        actionCam.update();
    }

    public void processInputs() {
        w = player1.processInputs(w, keys[KeyEvent.VK_S], keys[KeyEvent.VK_A], keys[KeyEvent.VK_D], keys[KeyEvent.VK_F], keys[KeyEvent.VK_G]);
        up = player2.processInputs(up, keys[KeyEvent.VK_DOWN], keys[KeyEvent.VK_LEFT], keys[KeyEvent.VK_RIGHT], keys[KeyEvent.VK_COMMA], keys[KeyEvent.VK_PERIOD]);
    }

    public void physics() {
        processInputs();
        for (inGameEntity entity : entities) {
            if (!screenRect.contains(entity.getX(), entity.getY())) {//ring out
                if (entity.getLives() > 0) {
                    entity.respawn(currentStage.getPossibleSpawn());
                }
                for (Bot rob : botList) {
                    rob.findTarget(entities);
                }
            }
            if (!entity.isGrounded() && !entity.isStunned() && !entity.isAttacking()) {//if the entity is in the ar not doing anthing
                entity.setAction("jump");
            }
            for (inGameEntity enemy : entities) {
                if (enemy != entity) {
                    entity.applyCollisions(enemy);
                }
            }
            entity.applyDrag();
            entity.applyGravity();
            entity.move();
            currentStage.updateCollisions(entity);
        }
        for (Bot rob : botList) {
            rob.think();
        }
    }

    public BufferedImage flipHoriz(BufferedImage currentImg) {
        AffineTransform tx;
        AffineTransformOp op;
        tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-currentImg.getWidth(null), 0);
        op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(currentImg, null);
    }

    public static BufferedImage scaleImage(BufferedImage imageIn, double ratio) {
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

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        w = e.getKeyChar() == "w".charAt(0);
        up = e.getKeyCode() == 38;
        enter = e.getKeyCode() == KeyEvent.VK_ENTER;
        down = e.getKeyCode() == KeyEvent.VK_DOWN;
        left = e.getKeyCode() == KeyEvent.VK_LEFT;
        right = e.getKeyCode() == KeyEvent.VK_RIGHT;
        esc = e.getKeyCode() == KeyEvent.VK_ESCAPE;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void animate() {
        for (inGameEntity fighter : entities) {
            fighter.animate();
        }
    }

    public Fighter randFighter() {
        int index = fighterList.length;
        index *= Math.random();
        return fighterList[index];
    }

    public void paintComponent(Graphics g) {
//        currentStage.draw(g);
//        for(inGameEntity fighter:entities){
//            //fighter.draw(g);
//            fighter.drawHitbox(g);
//        }
        if (currentScreen == lives) {
            actionCam.showFeed(g);
            judge(g);
        } else if (currentScreen == menu) {
            menu(g);
        } else {
            judge(g);
        }
    }

    public void menu(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(menuBack, -backOffset, 0, null);
        g.drawImage(menuBack, menuBack.getWidth() - backOffset, 0, null);
        backOffset %= menuBack.getWidth();
        backOffset += 5;
        updateButtons();
        drawbuttons(g);
        if (up || left) {
            selectedButton--;
            up = false;
            left = false;
        }
        if (down || right) {
            selectedButton++;
            down = false;
            right = false;
        }
        if (enter) {
            if (currentMenu == playerNumSelect) {
                numPlayers = selectedButton + 1;
                currentMenu = gameModeSelect;
                selectedButton = 0;
            } else if (currentMenu == gameModeSelect) {
                selectedGameMode = selectedButton + 1;
                //fadeTransition(g);
            } else if (currentMenu == botNumSelect) {
                numBots = selectedButton;
                for (int i = 0; i < numBots; i++) {
                    botList.add(new Bot(fighterList[i % (fighterList.length - 1)], (int) currentStage.getPossibleSpawn()[0], 100));
                }
                for (Bot rob : botList) {
                    entities.add(rob);
                }
                for (Bot rob : botList) {
                    rob.findTarget(entities);
                }
                currentScreen = lives;
            }
            selectedButton = 0;
            enter = false;
        }
        if (esc) {
            currentMenu = 0;
            esc = false;
        }
        if (currentMenu == playerNumSelect) {
            g.drawImage(title, getWidth() / 2 - title.getWidth() / 2, 100, null);
        } else if (currentMenu == gameModeSelect) {

        } else if (currentMenu == botNumSelect) {
            g.setFont(new Font("Courier New", Font.PLAIN, 20));
            g.drawString("" + selectedButton, getWidth() / 2, getHeight() / 2 + 50);
        }
        if (selectedButton == botNumSelect) {//cant have a negative number of bots
            currentMenu = Math.abs(currentMenu);
        }
        if (selectedButton < 0) {
            selectedButton = buttonList[currentMenu].size() - 1;
        }
        if (currentMenu != botNumSelect) {
            selectedButton %= buttonList[currentMenu].size();
        }
        if (numPlayers == 1 && selectedGameMode == lives) {
            currentMenu = botNumSelect;
        }else if(numPlayers == 2 && selectedGameMode == lives){
            double[] point = currentStage.getPossibleSpawn();
            currentScreen = lives;
            player2 = new inGameEntity(randFighter(),(int)point[0], (int) point[1]);
            point = currentStage.getPossibleSpawn();
            player1 = new inGameEntity(randFighter(),(int)point[0], (int) point[1]);
            entities.clear();
            entities.add(player1);
            entities.add(player2);
        }
    }

    public void drawbuttons(Graphics g) {
        for (Rectangle button : buttonList[currentMenu]) {
            BufferedImage text = buttonTexts.get(currentMenu)[buttonList[currentMenu].indexOf(button)];
            g.setColor(Color.black);
            g.fillRect((int) button.getX(), (int) button.getY(), (int) button.getWidth(), (int) button.getHeight());//background of button
            g.setColor(Color.white);
            g.drawRect((int) button.getX(), (int) button.getY(), (int) button.getWidth(), (int) button.getHeight());//outline of button
            g.drawImage(text, (int) (button.getCenterX() - text.getWidth() / 2), (int) (button.getCenterY() - text.getHeight() / 2), null);//button text
            g.setColor(new Color(255, 118, 0));
            if (buttonList[currentMenu].indexOf(button) == selectedButton) {//highlighting the selected button
                g.drawRect((int) button.getX(), (int) button.getY(), (int) button.getWidth(), (int) button.getHeight());
            }

        }
    }

    public void updateButtons() {

    }
    public void judge(Graphics g) {
        if (currentScreen == lives) {
            System.out.println("k");
            if (numPlayers == 1) {
                judge(player1);
                System.out.println("k");
            }
        }
        if (currentScreen == gameOver) {
            if (screenTime > 0) {
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setFont(new Font("Courier New", Font.PLAIN, 20));
                g.drawString("Game Over",100,100);
            }
            else{
                currentScreen = playerNumSelect;
            }
        }
        if (currentScreen == player1Wins) {
            if (screenTime > 0) {
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setFont(new Font("Courier New", Font.PLAIN, 20));
                g.drawString("Player 1 wins",100,100);
            }
            else{
                currentScreen = menu;
                currentMenu = playerNumSelect;
            }

        }
        screenTime--;
    }

    public void judge(inGameEntity player1) {
        if (player1.getLives() <= 0) {
            currentScreen = gameOver;
            currentMenu = playerNumSelect;
            screenTime = 180;
        } else if (entities.size() == 1) {
            currentScreen = player1Wins;
            screenTime = 180;
            System.out.println("k");
        }
        screenTime--;
        if (screenTime < 0) {
            screenTime = 0;
        }
    }

    public void updateMousePos() {
        mx = (int) getMousePosition().getX();
        my = (int) getMousePosition().getY();
        System.out.print(mx);
        System.out.print(" ");
        System.out.print(my);
        System.out.println();
    }

    public void fadeTransition(Graphics g) {
        for (int i = 0; i < 256; i++) {
            g.setColor(new Color(0, 0, 0, i));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        for (int i = 255; i < 0; i--) {
            g.setColor(new Color(0, 0, 0, i));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}