import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

public class Attackers extends Group {
    private double health = 10;
    private double strength;
    private Circle body;
    private Text healthText;
    
    private double wanderDX = 0;
    private double wanderDY = 0;
    private long lastChangeTime = 0;

    public Attackers(double x, double y) {
        this.strength = Math.random() * 5;
        
        body = new Circle(x, y, 10, Color.RED);
        getChildren().add(body);
        
        healthText = new Text(x - 10, y - 15, "HP: " + (int)health);
        healthText.setFill(Color.BLACK);
        getChildren().add(healthText);
    }

    public Circle getBody() { return body; }
    public double getX() { return body.getCenterX(); }
    public double getY() { return body.getCenterY(); }
    public double getHealth() { return health; }
    
    public void setStrength(double strength) { this.strength = strength; }
    public double getStrength() { return strength; }
    
    public void takeDamage(double amount) {
        this.health -= amount;
    }

    public void update(Navigator player, Hedge[] hedges, Maze maze) {
      if (this.getParent() == null || this.getScene() == null) {
        return; 
      }

      double px = player.getX() + player.getWidth() / 2;
      double py = player.getY() + player.getHeight() / 2;
    
      boolean canSee = maze.canSeePlayer(getX(), getY(), px, py, hedges);
      if (canSee) {
        moveToward(px, py, hedges, maze);
      } else {
        wander(hedges, maze);
      }

      // Check melee collision with player
      double distance = Math.sqrt(Math.pow(px - getX(), 2) + Math.pow(py - getY(), 2));
      if (distance < 20) {
        player.takeDamage(strength * 0.1); // Scaled down so you don't instantly vaporize
      }

      // Keep label attached to head
      healthText.setX(getX() - 10);
      healthText.setY(getY() - 15);
      healthText.setText("HP: " + (int)health);
    }

    private void moveToward(double tx, double ty, Hedge[] hedges, Maze maze) {
        double dx = tx - getX();
        double dy = ty - getY();
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
            double step = 1.2;
            double nextX = getX() + dx * step;
            double nextY = getY() + dy * step;
            if (!maze.isAttackerBlocked(nextX, nextY, hedges)) {
                body.setCenterX(nextX);
                body.setCenterY(nextY);
            }
        }
    }

    private void wander(Hedge[] hedges, Maze maze) {
        long now = System.currentTimeMillis();
        if (now - lastChangeTime > 1500) {
            double angle = Math.random() * 2 * Math.PI;
            wanderDX = Math.cos(angle);
            wanderDY = Math.sin(angle);
            lastChangeTime = now;
        }
        double step = 0.7;
        double nextX = getX() + wanderDX * step;
        double nextY = getY() + wanderDY * step;
        if (!maze.isAttackerBlocked(nextX, nextY, hedges)) {
            body.setCenterX(nextX);
            body.setCenterY(nextY);
        }
    }
}