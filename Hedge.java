//Name: Ella Standerford
//References: TODO

//Class purpose:
  //to create a hedge constructor that makes creating the maze easier
  //add details to hedge

import javafx.scene.shape.*;
import javafx.scene.*;
import javafx.scene.paint.*;

public class Hedge extends Group {
  private double xPosition;
  private double yPosition;
  private double height;
  private double width;
  private Rectangle mainRect; //stores the main rectangle

  public Hedge(double xPosition, double yPosition, double width, double height) {
    this.xPosition = xPosition;
    this.yPosition = yPosition;
    this.width = width;
    this.height = height;

    //Creating hedge object
    mainRect = new Rectangle(xPosition, yPosition, width, height);
    mainRect.setFill(Color.GREEN);
    this.getChildren().add(mainRect);

    //Random variations within the hedges
    int count = (int)(Math.random() * Math.max(width, height));
    for (int i = 0; i < count; i++) {
      double x = xPosition + Math.random() * width;
      double y = yPosition + Math.random() * height;
      double size = 1 + Math.random() * 2; //this is a size between 1 and 3 pixels
      double fleckColor = Math.random();
      Color color = new Color(0, 0, 0, 0);
      Circle fleck = new Circle(x, y, size, color);
      if (fleckColor < 0.5) {
        fleck.setFill(Color.LIGHTGREEN);
      }
      else {
        fleck.setFill(Color.DARKGREEN);
      }
      this.getChildren().add(fleck);
    }
  }
  
  //getter for mainRect
  public Rectangle getMainRect() {
    return mainRect;
  }
}