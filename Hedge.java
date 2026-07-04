import javafx.scene.shape.*;
import javafx.scene.*;
import javafx.scene.paint.*;

public class Hedge extends Group {
    private Rectangle mainRect;

    public Hedge(double x, double y, double width, double height) {
        mainRect = new Rectangle(x, y, width, height);
        mainRect.setFill(Color.GREEN);
        this.getChildren().add(mainRect);

        // Random organic visual variations within the hedges
        int count = (int)(Math.random() * Math.max(width, height) / 2);
        for (int i = 0; i < count; i++) {
            double fx = x + Math.random() * width;
            double fy = y + Math.random() * height;
            double size = 1 + Math.random() * 2;
            
            Circle fleck = new Circle(fx, fy, size);
            if (Math.random() < 0.5) {
                fleck.setFill(Color.LIGHTGREEN);
            } else {
                fleck.setFill(Color.DARKGREEN);
            }
            this.getChildren().add(fleck);
        }
    }
    
    public Rectangle getMainRect() {
        return mainRect;
    }
}