import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Main GUI application for Wellesley Simulator.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class WellesleySimulatorGUI extends Application {
    
    private GameManager gameManager;
    private HeaderBar headerBar;
    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    private hashForLocation loader;
    private BorderPane root;
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize game
        loader = new hashForLocation();
        loader.importFile("text for locations");
        gameManager = new GameManager(loader);
        
        // Create main layout
        root = new BorderPane();
        
        // Create components
        headerBar = new HeaderBar(gameManager);
        leftPanel = new LeftPanel(gameManager, this::refreshAll);
        rightPanel = new RightPanel(gameManager, this::refreshAll, this::restartGame);
        
        // Create horizontal container for left and right panels to allow resizing
        HBox contentBox = new HBox();
        contentBox.getChildren().addAll(leftPanel.getView(), rightPanel.getView());
        HBox.setHgrow(leftPanel.getView(), Priority.ALWAYS);
        HBox.setHgrow(rightPanel.getView(), Priority.ALWAYS);
        
        // Assemble layout
        root.setTop(headerBar.getView());
        root.setCenter(contentBox);
        
        // Create scene
        Scene scene = new Scene(root, 1400, 900);
        // CSS styles applied inline in components for simplicity
        
        // Configure stage
        primaryStage.setTitle("Wellesley Simulator");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.show();
        
        // Initial refresh
        refreshAll();
    }
    
    /**
     * Refresh all GUI components with current game state.
     */
    private void refreshAll() {
        headerBar.update();
        leftPanel.update();
        rightPanel.update();
    }
    
    /**
     * Restart the game by creating a new GameManager instance.
     */
    private void restartGame() {
        // Reinitialize the game
        gameManager = new GameManager(loader);
        
        // Update all components with the new game manager
        headerBar = new HeaderBar(gameManager);
        leftPanel = new LeftPanel(gameManager, this::refreshAll);
        rightPanel.setGameManager(gameManager);
        
        // Update the layout
        root.setTop(headerBar.getView());
        
        // Update the content box
        HBox contentBox = new HBox();
        contentBox.getChildren().addAll(leftPanel.getView(), rightPanel.getView());
        HBox.setHgrow(leftPanel.getView(), Priority.ALWAYS);
        HBox.setHgrow(rightPanel.getView(), Priority.ALWAYS);
        root.setCenter(contentBox);
        
        // Refresh all components
        refreshAll();
    }
    
    /**
     * Main entry point for the GUI application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}