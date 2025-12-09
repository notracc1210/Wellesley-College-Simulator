import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Panel displaying student statistics with progress bars.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class StudentStatsPanel {
    
    private GameManager gameManager;
    private VBox container;
    private Label titleLabel;
    private ProgressBar gpaBar;
    private Label gpaLabel;
    private ProgressBar happinessBar;
    private Label happinessLabel;
    private ProgressBar socialBar;
    private Label socialLabel;
    private ProgressBar healthBar;
    private Label healthLabel;
    
    public StudentStatsPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        createView();
    }
    
    private void createView() {
        container = new VBox(15);
        container.setPadding(new Insets(0, 30, 0, 30));  // Keep top/bottom 0 for tight fit, increase left/right for text
        container.setStyle("-fx-background-color: white; " +
                          "-fx-border-color: #cccccc; " +
                          "-fx-border-width: 1px;");
        
        // Title
        titleLabel = new Label("Student Stats");
        titleLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(25, 0, 15, 0));  // Add top and bottom padding to title
        
        // GPA row
        HBox gpaBox = createStatRow("GPA: ", gpaBar = new ProgressBar(), gpaLabel = new Label());
        gpaBar.setMinWidth(150);
        gpaBar.setPrefWidth(200);
        gpaBar.setMaxWidth(Double.MAX_VALUE);
        gpaBar.setPrefHeight(20);
        HBox.setHgrow(gpaBar, javafx.scene.layout.Priority.ALWAYS);
        
        // Happiness row
        HBox happinessBox = createStatRow("Happiness: ", happinessBar = new ProgressBar(), happinessLabel = new Label());
        happinessBar.setMinWidth(150);
        happinessBar.setPrefWidth(200);
        happinessBar.setMaxWidth(Double.MAX_VALUE);
        happinessBar.setPrefHeight(20);
        HBox.setHgrow(happinessBar, javafx.scene.layout.Priority.ALWAYS);
        
        // Social Connection row
        HBox socialBox = createStatRow("Social Connection: ", socialBar = new ProgressBar(), socialLabel = new Label());
        socialBar.setMinWidth(150);
        socialBar.setPrefWidth(200);
        socialBar.setMaxWidth(Double.MAX_VALUE);
        socialBar.setPrefHeight(20);
        HBox.setHgrow(socialBar, javafx.scene.layout.Priority.ALWAYS);
        
        // Health row
        HBox healthBox = createStatRow("Health: ", healthBar = new ProgressBar(), healthLabel = new Label());
        healthBar.setMinWidth(150);
        healthBar.setPrefWidth(200);
        healthBar.setMaxWidth(Double.MAX_VALUE);
        healthBar.setPrefHeight(20);
        HBox.setHgrow(healthBar, javafx.scene.layout.Priority.ALWAYS);
        // Increase bottom padding for health row
        healthBox.setPadding(new Insets(10, 0, 30, 0));  // Increased bottom padding from 10 to 30
        
        container.getChildren().addAll(titleLabel, gpaBox, happinessBox, socialBox, healthBox);
    }
    
    private HBox createStatRow(String prefix, ProgressBar bar, Label valueLabel) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));  // Add vertical padding to each row
        
        Label prefixLabel = new Label(prefix);
        prefixLabel.setFont(Font.font("Georgia", 18));
        prefixLabel.setPadding(new Insets(0, 10, 0, 0));  // Add right padding to prefix label
        
        valueLabel.setFont(Font.font("Georgia", 18));
        valueLabel.setMinWidth(60);
        valueLabel.setPadding(new Insets(0, 0, 0, 10));  // Add left padding to value label
        
        // Style progress bars
        bar.setStyle("-fx-accent: #4a90e2;");
        
        row.getChildren().addAll(prefixLabel, bar, valueLabel);
        return row;
    }
    
    public VBox getView() {
        return container;
    }
    
    public void update() {
        if (gameManager == null) return;
        
        PlayerStat stats = gameManager.getGameTracker().getPlayerStats();
        
        // GPA (0.0 - 4.0)
        double gpa = stats.getGPA();
        gpaBar.setProgress(gpa / 4.0);
        gpaLabel.setText(String.format("%.2f", gpa));
        
        // Happiness (0-100)
        int happiness = stats.getHappiness();
        happinessBar.setProgress(happiness / 100.0);
        happinessLabel.setText(happiness + "%");
        
        // Social Connection (0-100)
        int social = stats.getSocial();
        socialBar.setProgress(social / 100.0);
        socialLabel.setText(social + "%");
        
        // Health (0-100)
        int health = stats.getHealth();
        healthBar.setProgress(health / 100.0);
        healthLabel.setText(health + "%");
    }
}