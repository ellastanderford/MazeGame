import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class Navigator extends Rectangle {
    private double health = 100;
    private double strength = 5;

    public Navigator() {
        super(0, 540, 30, 30);
        this.setFill(Color.BLUE);
    }

    public void takeDamage(double amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public double getHealth() { return health; }
    public void setStrength(double strength) { this.strength = strength; }
    public double getStrength() { return strength; }

    public void moveUp(Hedge[] hedges, Maze maze) {
        double newY = getY() - 5;
        if (!maze.isNavigatorBlocked(getX(), newY, hedges)) {
            setY(newY);
        }
    }
    public void moveDown(Hedge[] hedges, Maze maze) {
        double newY = getY() + 5;
        if (!maze.isNavigatorBlocked(getX(), newY, hedges)) {
            setY(newY);
        }
    }
    public void moveLeft(Hedge[] hedges, Maze maze) {
        double newX = getX() - 5;
        if (!maze.isNavigatorBlocked(newX, getY(), hedges)) {
            setX(newX);
        }
    }
    public void moveRight(Hedge[] hedges, Maze maze) {
        double newX = getX() + 5;
        if (!maze.isNavigatorBlocked(newX, getY(), hedges)) {
            setX(newX);
        }
    }
}