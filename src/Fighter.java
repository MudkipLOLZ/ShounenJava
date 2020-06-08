import java.awt.*;
import java.io.IOException;

public class Fighter {
    private final Action sLight, nLight, idle, walk, jump, nAir, sAir, nSpecial, sSpecial, recovery, knocked;
    private String name;

    public Fighter(String nameIn) throws IOException {
        name = nameIn;
        idle = new Action(name + "/idle");
        walk = new Action(name + "/walk");
        jump = new Action(name + "/jump");
        nLight = new Action(name + "/nLight");
        sLight = new Action(name + "/sLight");
        nAir = new Action(name + "/nAir");
        sAir = new Action(name + "/sAir");
        nSpecial = new Action(name + "/nSpecial");
        sSpecial = new Action(name + "/sSpecial");
        knocked = new Action(name + "/knocked");
        recovery = new Action(name + "/recovery");
    }

    public Action getsLight() {
        return sLight;
    }

    public Action getnLight() {
        return nLight;
    }

    public Action getnSpecial() {
        return nSpecial;
    }

    public Action getsSpecial() {
        return sSpecial;
    }

    public Action getIdle() {
        return idle;
    }

    public Action getWalk() {
        return walk;
    }

    public Action getJump() {
        return jump;
    }

    public String getName() {
        return name;
    }

    public Action getKnocked() {
        return knocked;
    }

    public Action getRecovery() {
        return recovery;
    }

    public Action getsAir() {
        return sAir;
    }

    public Action getnAir() {
        return nAir;
    }
}
