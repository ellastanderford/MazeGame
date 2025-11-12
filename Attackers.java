//Name: Ella Standerford
//References: TODO

//Class purpose:
  //Instantiable class for my attackers
  //Create instance variables and constructor for my attackers
  //Attack methods, defend methods, health methods
  //toString method
  //To be used to create an array of enemies placed throughout the maze

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;

public class Attackers extends Group {
  public double health;
  private double strength;
  private double xPosition;
  private double yPosition;
  public double dx;
  public double dy;
  private Circle body;
  private Text healthText;
  //directions for wandering
  private double wanderDX = 0;
  private double wanderDY = 0;
  private long lastChangeTime = 0;

  public Attackers(double x, double y) {
    this.health = 10;
    this.strength = Math.random() * 5;
    this.xPosition = x;
    this.yPosition = y;
    this.dx = 4;
    this.dy = 2;
    //creating attacker as a red circle
    body = new Circle(10, Color.RED);
    body.setCenterX(x);
    body.setCenterY(y);
    getChildren().add(body);
    //Creating text object for health
    healthText = new Text(x - 10, y - 15, "HP: " + health);
    healthText.setFill(Color.BLACK);
    getChildren().add(healthText);
  }

  //getter methods
  public Circle getBody() {
    return body;
  }
  public double getX() {
    return body.getCenterX();
  }
  public double getY() {
    return body.getCenterY();
  }
  public double getHealth() {
    return health;
  }
  //strength methods
  public void setStrength(double strength) {
    this.strength = strength;
  }
  public double getStrength() {
    return strength;
  }
  //attacker gets hit and hurt
  public void takeDamage (double amount) {
    this.health = this.health - amount;
  }
  //main movement: either chase or wander (depending on if hedge is in the way) 
  public void update(Navigator player, Hedge[] hedges, Maze maze) {
    double px = player.getX();
    double py = player.getY();
    boolean canSee = maze.canSeePlayer(getX(), getY(), px, py, hedges);
    if (canSee) {
      //Chase the player because the path is clear 
      moveToward(px, py, hedges, maze);
    }
    else {
      //wanders randomly
      wander(hedges, maze);
    }
    //check if the attacker is close enough to hurt the player
    double distance = Math.sqrt(Math.pow(px - getX(), 2) + Math.pow(py - getY(), 2));
    if (distance < 15) {
      player.takeDamage(strength);
    }
    //update health text position and value 
    healthText.setX(getX() - 10);
    healthText.setY(getY() - 15);
    healthText.setText("HP: " + (int)health);
    if (player.getHealth() <= 0) {
      Text gameOver = new Text(250, 300, "GAME OVER!");
      gameOver.setFont(Font.font(40));
      gameOver.setFill(Color.RED);
    }
  } 
  //Moving slightly toward target point(player)
  private void moveToward(double tx, double ty, Hedge[] hedges, Maze maze) {
    //finding directions and length to target
    double dx = tx - getX();
    double dy = ty - getY();
    double length = Math.sqrt(dx * dx + dy * dy);
    //making sure that the item can't move too much
    if (length > 0) {
      dx = dx / length;
      dy = dy / length;
      double step = 1.2;
      double nextX = getX() + dx * step;
      double nextY = getY() + dy * step;
      if (!maze.isAttackerBlocked(nextX, nextY, this, hedges)) {
        body.setCenterX(nextX);
        body.setCenterY(nextY);
      }
    }
  }
  //Wandering around randomly
  private void wander(Hedge[] hedges, Maze maze) {
    long now = System.currentTimeMillis();
    //Every 1 and a half seconds, pick a new random direction
    if (now - lastChangeTime > 1500) {
      double angle = Math.random() * 2 * Math.PI;
      wanderDX = Math.cos(angle);
      wanderDY = Math.sin(angle);
      lastChangeTime = now;
    }
    double step = 0.7;
    double nextX = getX() + wanderDX * step;
    double nextY = getY() + wanderDY * step;
    if (!maze.isAttackerBlocked(nextX, nextY, this, hedges)) {
      body.setCenterX(nextX);
      body.setCenterY(nextY);
    }
  }
} 
