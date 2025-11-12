//Name: Ella Standerford
//References: TODO

//Class purpose:
  //Instantiable class for my Navigator item
  //Build in instance variables + constructor
  //Movement (forward, back, left, right) methods
  //Attack methods, defend methods, health methods
  //toString methods

import javafx.scene.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.application.*;
import javafx.animation.AnimationTimer;

public class Navigator extends Group {
  private double health;
  private double strength;
  private double xPosition;
  private double yPosition;
  private double width;
  private double height;
  private Rectangle nav; 

  public Navigator() {
    this.health = 100;
    this.strength = 5; //default as it is set in main
    this.xPosition = 0;
    this.yPosition = 540;
    this.width = 30;
    this.height = 30;

    nav = new Rectangle(xPosition, yPosition, width, height);
    nav.setFill(Color.BLUE);
    getChildren().add(nav);
  }

  //allowing an image fill from Maze.java
  public void setFill(Paint p) {
    nav.setFill(p);
  }
  //dealing with health
  public void takeDamage (double amount) {
    health = health - amount;
    if (health < 0) {
      health = 0;
    }
  }
  public double getHealth() {
    return health;
  }
   //strength methods
  public void setStrength(double strength) {
    this.strength = strength;
  }

  //methods
  public void moveUp(Hedge[] hedges, Maze maze) {
    double newY = yPosition - 5;
    if (!maze.isNavigatorBlocked(xPosition, newY, this, hedges)) {
      yPosition = newY;
      nav.setY(yPosition);
    }
  }
  public void moveDown(Hedge[] hedges, Maze maze) {
    double newY = yPosition + 5;
    if (!maze.isNavigatorBlocked(xPosition, newY, this, hedges)) {
      yPosition = newY;
      nav.setY(yPosition);
    }
  }
  public void moveLeft(Hedge[] hedges, Maze maze) {
    double newX = xPosition - 5;
    if (!maze.isNavigatorBlocked(newX, yPosition, this, hedges)) {
      xPosition = newX;
      nav.setX(xPosition);
    }
  }
  public void moveRight(Hedge[] hedges, Maze maze) {
    double newX = xPosition + 5;
    if (!maze.isNavigatorBlocked(newX, yPosition, this, hedges)) {
      xPosition = newX;
      nav.setX(xPosition);
    }
  }
  //getter methods
  public double getWidth() {
    return width;
  }
  public double getHeight() {
    return height;
  }
  public double getX() {
    return xPosition;
  }
  public double getY() {
    return yPosition;
  }
  public double getStrength() {
    return strength;
  }

  //Shoots a fireball toward the mouse click, damaging enemies on impact.
  public void shootFireballToward(double targetX, double targetY, Group root, Hedge[] hedges, Attackers[] enemies, Maze maze) {
    //finding the center of the player
    double startX = xPosition + width / 2;
    double startY = yPosition + height / 2;
    //find direction from player to click
    double dx = targetX - startX;
    double dy = targetY - startY;
    //finding the length using the pythagorean theorem
    double length = Math.sqrt(dx * dx + dy * dy);
    //normalize (make unit vector) and multiply by speed 
    double speed = 5;
    dx = dx / length * speed;
    dy = dy / length * speed;
    //make the fireball 
    Fireball ball = new Fireball(startX, startY, dx, dy, strength);
    root.getChildren().add(ball);
    //actually animate the fireball
    AnimationTimer fireballTimer = new AnimationTimer() {
      public void handle (long now) {
        ball.move();
        //stop the fireball if it hits the hedge or goes out of bounds
        if (maze.fireballHitsHedge(ball, hedges) || maze.fireballOutOfBounds(ball)) {
          root.getChildren().remove(ball);
          //ends animation
          stop();
        }
        //check if it hit enemies
        for (int i = 0; i < enemies.length; i++) {
          if (enemies[i] != null) {
            if (ball.getBody().getBoundsInParent().intersects(enemies[i].getBody().getBoundsInParent())) {
              //hurt the enemy using navigator strength
              enemies[i].takeDamage(strength);
              //if enemy health is 0 or lower, remove it
              if (enemies[i].getHealth() <= 0) {
                root.getChildren().remove(enemies[i]);
                enemies[i] = null;
              }
              //remove the fireball after it hits
              root.getChildren().remove(ball);
              stop();
              break;
            }
          }
        }
      }
    };
    fireballTimer.start();
  }
}