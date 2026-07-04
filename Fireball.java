import javafx.scene.paint.*;
import javafx.scene.shape.Circle;

public class Fireball extends Circle {
    private double dx;
    private double dy;
    private double damage;

    public Fireball(double startX, double startY, double dx, double dy, double damage) {
        super(startX, startY, 5, Color.ORANGE);
        this.dx = dx;
        this.dy = dy;
        this.damage = damage;
    }

    public void move() {
        setCenterX(getCenterX() + dx);
        setCenterY(getCenterY() + dy);
    }

    public double getX() { return getCenterX(); }
    public double getY() { return getCenterY(); }
    public double getDamage() { return damage; }
}