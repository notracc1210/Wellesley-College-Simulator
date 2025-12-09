import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Header bar component displaying title, day/energy info, and W logo.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class HeaderBar {
    
    private GameManager gameManager;
    private HBox container;
    private Label titleLabel;
    private TextFlow dayEnergyFlow;
    private Label wLogoLabel;
    
    public HeaderBar(GameManager gameManager) {
        this.gameManager = gameManager;
        createView();
    }
    
    private void createView() {
        container = new HBox(20);
        container.setPadding(new Insets(20, 20, 0, 20));  // Remove bottom padding to stick with panels below
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: #1a3a5c; " +
                          "-fx-border-color: #0d1f2e; " +
                          "-fx-border-width: 0 0 2 0;");
        
        // Left spacer to center content
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Center container for title and day/energy info
        HBox centerContainer = new HBox(20);
        centerContainer.setAlignment(Pos.BASELINE_CENTER); // Use baseline alignment for text
        
        // Title
        titleLabel = new Label("Wellesley Simulator");
        titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Day and Energy info (using TextFlow for colored lightning)
        dayEnergyFlow = new TextFlow();
        dayEnergyFlow.setStyle("-fx-background-color: transparent;");
        dayEnergyFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        // Add top padding to better align with title text baseline
        // Font size difference is 12px (32-20), so we need padding to align baselines
        dayEnergyFlow.setPadding(new Insets(12, 0, 0, 0));
        
        centerContainer.getChildren().addAll(titleLabel, dayEnergyFlow);
        
        // Right spacer to balance layout
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Right: W Logo
        wLogoLabel = new Label("W");
        wLogoLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 42));
        wLogoLabel.setTextFill(Color.WHITE);
        wLogoLabel.setAlignment(Pos.CENTER);
        
        container.getChildren().addAll(leftSpacer, centerContainer, rightSpacer, wLogoLabel);
    }
    
    public HBox getView() {
        return container;
    }
    
    public void update() {
        if (gameManager == null) return;
        
        gameStat tracker = gameManager.getGameTracker();
        String dayLabel = tracker.getDayLabel();
        int energy = tracker.getCurrentEnergy();
        
        // Clear previous text
        dayEnergyFlow.getChildren().clear();
        
        // Create text nodes with different colors
        Text dayText = new Text(dayLabel + " ");
        dayText.setFont(Font.font("Georgia", 20));
        dayText.setFill(Color.WHITE);
        
        Text lightningText = new Text("⚡");
        lightningText.setFont(Font.font("Georgia", 20));
        lightningText.setFill(Color.web("#FFD700")); // Golden color
        
        Text energyText = new Text(" " + energy);
        energyText.setFont(Font.font("Georgia", 20));
        energyText.setFill(Color.WHITE);
        
        dayEnergyFlow.getChildren().addAll(dayText, lightningText, energyText);
    }
}