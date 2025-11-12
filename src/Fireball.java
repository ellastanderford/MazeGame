//Name: Ella Standerford
//References: TODO

//The purpose of this class is:
  //Fireball moves in a straight line and damages attackers on collision.

import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;

public class Fireball extends Group {
  private Circle body;
  public double dx; //velocity x 
  public double dy; //velocity y
  public double damage; //how much damage is done by the fireball

  public Fireball(double startX, double startY, double dx, double dy, double damage) {
    this.dx = dx;
    this.dy = dy;
    this.damage = damage;
    body = new Circle(5, Color.ORANGE); //uses the private circle above to create a small orange circle
    body.setCenterX(startX);
    body.setCenterY(startY);
    getChildren().add(body);
  }

  //methods for the fireball
  public void move() {
    body.setCenterX(body.getCenterX() + dx);
    body.setCenterY(body.getCenterY() + dy);
  }
  // method that returns a circle
  public Circle getBody() {
    return body;
  }
  public double getX() {
    return body.getCenterX();
  }
  public double getY() {
    return body.getCenterY();
  }
}
