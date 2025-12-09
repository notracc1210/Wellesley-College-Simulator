import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Campus map graph visualization component.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class CampusMapView {
    
    private GameManager gameManager;
    private VBox container;
    private StackPane mapContainer;
    private Canvas canvas;
    private GraphicsContext gc;
    private Pane buttonOverlay;
    private Runnable refreshCallback;
    private static final double CANVAS_MIN_WIDTH = 400;  // Minimum width
    private static final double CANVAS_PREF_WIDTH = 550;  // Preferred width
    private static final double CANVAS_MIN_HEIGHT = 300;  // Minimum height
    private static final double CANVAS_PREF_HEIGHT = 400;  // Preferred height
    private static final double NODE_WIDTH = 60;
    private static final double NODE_HEIGHT = 30;
    
    // Store node bounds and buttons for click detection
    private Map<String, NodeBounds> nodeBoundsMap;
    private Map<String, Button> locationButtons;
    
    /**
     * Helper class to store node bounds for click detection
     */
    private static class NodeBounds {
        double x, y, width, height;
        String locationKey;
        
        NodeBounds(double x, double y, double width, double height, String locationKey) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.locationKey = locationKey;
        }
        
        boolean contains(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }
    
    public CampusMapView(GameManager gameManager) {
        this(gameManager, null);
    }
    
    public CampusMapView(GameManager gameManager, Runnable refreshCallback) {
        this.gameManager = gameManager;
        this.refreshCallback = refreshCallback;
        this.nodeBoundsMap = new HashMap<>();
        this.locationButtons = new HashMap<>();
        createView();
    }
    
    private void createView() {
        container = new VBox(10);
        container.setPadding(new Insets(0, 20, 0, 20));  // Remove top and bottom padding to stick with adjacent panels
        container.setStyle("-fx-background-color: white; " +
                          "-fx-border-color: #cccccc; " +
                          "-fx-border-width: 1px;");
        
        // Create stack pane to overlay buttons on canvas
        mapContainer = new StackPane();
        mapContainer.setMinSize(CANVAS_MIN_WIDTH, CANVAS_MIN_HEIGHT);
        mapContainer.setPrefSize(CANVAS_PREF_WIDTH, CANVAS_PREF_HEIGHT);
        mapContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        // Canvas for drawing - bind size to container
        canvas = new Canvas(CANVAS_PREF_WIDTH, CANVAS_PREF_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        // Make canvas mouse-transparent so clicks pass through to buttons
        canvas.setMouseTransparent(true);
        
        // Bind canvas size to mapContainer size
        canvas.widthProperty().bind(mapContainer.widthProperty());
        canvas.heightProperty().bind(mapContainer.heightProperty());
        
        // Redraw when size changes
        mapContainer.widthProperty().addListener((obs, oldVal, newVal) -> drawGraph());
        mapContainer.heightProperty().addListener((obs, oldVal, newVal) -> drawGraph());
        
        // Overlay pane for invisible buttons - bind size to container
        buttonOverlay = new Pane();
        buttonOverlay.prefWidthProperty().bind(mapContainer.widthProperty());
        buttonOverlay.prefHeightProperty().bind(mapContainer.heightProperty());
        buttonOverlay.setMouseTransparent(false);
        
        mapContainer.getChildren().addAll(canvas, buttonOverlay);
        
        // Allow map container to grow with parent
        VBox.setVgrow(mapContainer, javafx.scene.layout.Priority.ALWAYS);
        
        container.getChildren().add(mapContainer);
    }
    
    public VBox getView() {
        return container;
    }
    
    public void update() {
        drawGraph();
    }
    
    private void drawGraph() {
        // Get current canvas dimensions
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        
        // Skip drawing if canvas is not yet sized
        if (canvasWidth <= 0 || canvasHeight <= 0) {
            return;
        }
        
        // Clear canvas
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        
        // Set background
        gc.setFill(Color.web("#f5f5f5"));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
        
        // Get current location (map DORM to TOWER for display)
        String currentLocation = gameManager != null ? gameManager.getCurrentLocation() : "DORM";
        if ("DORM".equals(currentLocation)) {
            currentLocation = "TOWER";
        }
        
        // Get all locations
        Map<String, CampusMap.LocationNode> nodes = new HashMap<>();
        for (CampusMap.LocationNode node : CampusMap.getAllLocations()) {
            nodes.put(node.locationKey, node);
        }
        
        // Scale coordinates to canvas size (with padding)
        double padding = 40;
        double scaleX = (canvasWidth - 2 * padding) / 100.0;
        double scaleY = (canvasHeight - 2 * padding) / 100.0;
        
        // Draw edges first (so they appear behind nodes)
        gc.setStroke(Color.web("#4CAF50"));
        gc.setLineWidth(4);
        
        for (CampusMap.LocationNode node : CampusMap.getAllLocations()) {
            double x1 = padding + node.x * scaleX;
            double y1 = padding + node.y * scaleY;
            
            List<String> connections = CampusMap.getConnections(node.locationKey);
            for (String connectedKey : connections) {
                CampusMap.LocationNode connectedNode = nodes.get(connectedKey);
                if (connectedNode != null) {
                    double x2 = padding + connectedNode.x * scaleX;
                    double y2 = padding + connectedNode.y * scaleY;
                    gc.strokeLine(x1, y1, x2, y2);
                }
            }
        }
        
        // Clear node bounds map and remove old buttons
        nodeBoundsMap.clear();
        buttonOverlay.getChildren().clear();
        locationButtons.clear();
        
        // Check if in navigation mode
        boolean inNavigationMode = gameManager != null && gameManager.isInNavigationMode();
        
        // Draw nodes and create buttons
        for (CampusMap.LocationNode node : CampusMap.getAllLocations()) {
            // Skip PENDLETON as it's not a playable location
            if ("PENDLETON".equals(node.locationKey)) {
                continue;
            }
            
            double x = padding + node.x * scaleX;
            double y = padding + node.y * scaleY;
            
            // Check if this is the current location
            boolean isCurrent = node.locationKey.equals(currentLocation);
            
            // Draw node rectangle
            double nodeX = x - NODE_WIDTH / 2;
            double nodeY = y - NODE_HEIGHT / 2;
            
            // Store bounds for reference
            nodeBoundsMap.put(node.locationKey, new NodeBounds(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT, node.locationKey));
            
            // Create invisible button overlay for click detection
            Button locationButton = new Button();
            locationButton.setLayoutX(nodeX);
            locationButton.setLayoutY(nodeY);
            locationButton.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
            locationButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand; " +
                "-fx-opacity: 1.0;"
            );
            
            // Always enable button (we'll check navigation mode in handler)
            locationButton.setDisable(false);
            locationButton.setVisible(true);
            locationButton.setMouseTransparent(false);
            
            // Add hover effect to show it's clickable (only when in navigation mode)
            locationButton.setOnMouseEntered(e -> {
                if (gameManager != null && gameManager.isInNavigationMode()) {
                    locationButton.setStyle(
                        "-fx-background-color: rgba(33, 150, 243, 0.2); " +
                        "-fx-border-color: rgba(33, 150, 243, 0.5); " +
                        "-fx-border-width: 2px; " +
                        "-fx-cursor: hand; " +
                        "-fx-opacity: 1.0;"
                    );
                } else {
                    locationButton.setCursor(javafx.scene.Cursor.DEFAULT);
                }
            });
            locationButton.setOnMouseExited(e -> {
                locationButton.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-border-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-opacity: 1.0;"
                );
            });
            
            // Set click handler
            final String locationKey = node.locationKey;
            locationButton.setOnAction(e -> {
                System.out.println("Button clicked for location: " + locationKey);
                System.out.println("Navigation mode: " + (gameManager != null && gameManager.isInNavigationMode()));
                if (gameManager != null && gameManager.isInNavigationMode()) {
                    int choiceIndex = getChoiceIndexForLocation(locationKey);
                    System.out.println("Choice index: " + choiceIndex);
                    if (choiceIndex >= 0) {
                        gameManager.processChoice(choiceIndex);
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                    }
                } else {
                    System.out.println("Click ignored - not in navigation mode");
                }
            });
            
            locationButtons.put(node.locationKey, locationButton);
            buttonOverlay.getChildren().add(locationButton);
            
            // Fill - different color if clickable
            if (isCurrent) {
                gc.setFill(Color.web("#FFD700")); // Gold for current
            } else if (inNavigationMode) {
                gc.setFill(Color.web("#2196F3")); // Blue for clickable locations
            } else {
                gc.setFill(Color.web("#9E9E9E")); // Gray for non-clickable
            }
            gc.fillRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT);
            
            // Border - thicker if clickable
            if (inNavigationMode && !isCurrent) {
                gc.setStroke(Color.web("#1976D2"));
                gc.setLineWidth(3);
            } else {
                gc.setStroke(Color.web("#1976D2"));
                gc.setLineWidth(2);
            }
            gc.strokeRect(nodeX, nodeY, NODE_WIDTH, NODE_HEIGHT);
            
            // Text
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Georgia", 16));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(node.displayName, x, y + 5);
            
            // Red wavy underline for current location (simplified as red line)
            if (isCurrent) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                double underlineY = nodeY + NODE_HEIGHT + 3;
                gc.strokeLine(nodeX + 5, underlineY, nodeX + NODE_WIDTH - 5, underlineY);
            }
        }
    }
    
    
    /**
     * Map location key to choice index based on GameManager's processChoice mapping.
     * @param locationKey The location key (e.g., "LULU", "CLAPP")
     * @return Choice index (0-7) or -1 if not found
     */
    private int getChoiceIndexForLocation(String locationKey) {
        switch (locationKey) {
            case "LULU": return 0;
            case "CLAPP": return 1;
            case "JEWETT": return 2;
            case "SCIENCE": return 3;
            case "FOUNDERS": return 4;
            case "TOWER": return 5;
            case "CHAPEL": return 6;
            case "CLUB": return 7;
            default: return -1;
        }
    }
    
    /**
     * Add current location label below the graph.
     */
    public void addCurrentLocationLabel(Label label) {
        container.getChildren().add(label);
    }
    
    /**
     * Set the refresh callback to be called after processing a choice.
     */
    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }
}