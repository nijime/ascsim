import java.util.Random;

public class GameState {
    private static final int seed = 1;

    private Character character;
    private Target target;

    private double time;
    private double gcdEndTime;
    private double castEndTime;

    private Random rGen;

    public GameState(Character character) {
        this.character = character;
        this.time = 0;
        this.gcdEndTime = 0;
        this.castEndTime = 0;

        this.rGen = new Random(seed);

        this.target = new Target();
    }

    public Target getTarget() {
        return target;
    }

    public void waitTillCast() {
        if (castEndTime > time) {
            time = castEndTime;
        }
    }

    public void waitTillGCD() {
        if (gcdEndTime > time) {
            time = gcdEndTime;
        }
    }

    public Character getPlayer() {
        return this.character;
    }

    public int getSpHash() {
        return character.getSpHash();
    }

    public void invokeCast(double dt) {
        castEndTime = time() + dt;
    }

    public void invokeGCD(double gcd) {
        gcdEndTime = time() + gcd;
    }

    public boolean onGCD() {
        return time() < gcdEndTime;
    }

    public boolean casting() {
        return time() < castEndTime;
    }

    public double time() {
        return time;
    }

    public double rand() {
        return rGen.nextDouble();
    }
}
