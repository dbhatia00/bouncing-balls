package bouncingballs;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private Pane canvas = new Pane();
    private Scene scene = new Scene(canvas, 1300, 800);
    private ArrayList<Circle> balls = new ArrayList<Circle>(); 
    private ArrayList<Boolean> toFlip = new ArrayList<Boolean>();
    private final int numBalls = 20;
    private double mouseX;
    private double mouseY;

    /* 
     * A function to handle creating the timeline of a single Circle
     * IN:  Circle object
     *      double dx
     *      double dy
     *      int index
     * OUT: void
    */
    public void handleTimeline(Circle ball, double dx, double dy, final int index){
        
        Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            double deltaX = dx;
            double deltaY = dy;
            
            @Override
            public void handle(final ActionEvent t) {
                ball.setLayoutX(ball.getLayoutX() + deltaX);
                ball.setLayoutY(ball.getLayoutY() + deltaY);
                final Bounds bounds = canvas.getBoundsInLocal();
                final boolean atRightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius());
                final boolean atLeftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius());
                final boolean atBottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius());
                final boolean atTopBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius());

                if (atRightBorder || atLeftBorder) {
                    deltaX *= -1;
                }
                if (atBottomBorder || atTopBorder) {
                    deltaY *= -1;
                }
                if (toFlip.get(index)){
                    deltaX *= -1;
                    deltaY *= -1;
                    toFlip.set(index, false);
                }
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    /* 
     * A function to randomly generate a ball's radius
     * IN:  Void
     * OUT: double radius 
    */
    private double generateRadius(){
        Random rand = new Random();
        return rand.nextInt(40 - 10) + 10;
    }

    /* 
     * A function to randomly generate a ball's color
     * IN:  Void
     * OUT: Color object
    */
    private Color generateColor(){
        Random rand = new Random();
        return Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }

    /* 
     * A function to calculate the radius of an object based on its radius
     * IN:  Circle object
     * OUT: double delta 
    */
    public double calculateInitialDelta(Circle ball){
        return (1 / ball.getRadius()) * 10 ;
    }

    /* 
     * A function to initialize all of the variables and kick off the timelines
     * IN:  Stage object
     * OUT: void
    */
    @Override
    public void start(Stage stage) {        
        for(int i = 0; i < numBalls; i++){
            balls.add(new Circle(generateRadius(), generateColor()));
            balls.get(i).relocate(i*30, i*30);
            canvas.getChildren().add(balls.get(i));
            toFlip.add(false);
        }
        
        stage.setTitle("Moving Ball");
        stage.setScene(scene);
        stage.show();
        
        for(int i = 0; i < numBalls; i++){
            handleTimeline(balls.get(i), calculateInitialDelta(balls.get(i)), calculateInitialDelta(balls.get(i)), i);
        }

        scene.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                mouseX = event.getX();
                mouseY = event.getY();
                // Move circles away from the mouse click
                for(int i = 0; i < numBalls; i++){
                    toFlip.set(i, true);
                }
            }
        });
    }
    
    public static void main(String[] args) {
        launch();
    }

}