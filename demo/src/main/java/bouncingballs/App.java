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
    Pane canvas = new Pane();

    private int numBalls = 20;

    public void handleTimeline(Circle ball, double dx, double dy){
        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {

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
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    private double generateRadius(){
        Random rand = new Random();
        return rand.nextInt(40 - 10) + 10;
    }

    private Color generateColor(){
        Random rand = new Random();
        return Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }

    public double calculateDelta(Circle ball){
        return (1 / ball.getRadius()) * 10 ;
    }

    @Override
    public void start(Stage stage) {
    	Scene scene = new Scene(canvas, 1300, 800);
        ArrayList<Circle> balls = new ArrayList<Circle>(); 
        
        Circle temp;
        for(int i = 0; i < numBalls; i++){
            balls.add(new Circle(generateRadius(), generateColor()));
            balls.get(i).relocate(i*20, i*20);
            canvas.getChildren().add(balls.get(i));
        }
        
        stage.setTitle("Moving Ball");
        stage.setScene(scene);
        stage.show();
        
        for(int i = 0; i < numBalls; i++){
            handleTimeline(balls.get(i), calculateDelta(balls.get(i)), calculateDelta(balls.get(i)));
        }

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {         
            @Override         
            public void handle(MouseEvent event) {  
                if (event.getButton() == MouseButton.PRIMARY) {
                    for(int i = 0; i < numBalls; i++){
                        handleTimeline(balls.get(i), -calculateDelta(balls.get(i)), calculateDelta(balls.get(i)));
                    }
                }
            }     
        }); 
    }
    
    public static void main(String[] args) {
        launch();
    }

}