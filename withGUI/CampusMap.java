import java.util.*;

/**
 * Class to store campus map data including location coordinates and connections
 * for graph visualization.
 * 
 * @author GUI Implementation
 * @version 2025.12
 */
public class CampusMap {
    
    /**
     * Represents a location node with its display name and position
     */
    public static class LocationNode {
        public String displayName; // Lowercase display name (e.g., "lulu")
        public String locationKey; // Uppercase key used in game (e.g., "LULU")
        public double x, y; // Coordinates for graph visualization
        
        public LocationNode(String displayName, String locationKey, double x, double y) {
            this.displayName = displayName;
            this.locationKey = locationKey;
            this.x = x;
            this.y = y;
        }
    }
    
    // Map of location keys to their node data
    private static Map<String, LocationNode> locations = new HashMap<>();
    
    // Map of location keys to their connected location keys
    private static Map<String, List<String>> connections = new HashMap<>();
    
    static {
        initializeLocations();
        initializeConnections();
    }
    
    /**
     * Initialize location nodes with coordinates.
     * Coordinates are relative (0-100 scale) and will be scaled to actual pixel size.
     */
    private static void initializeLocations() {
        // Coordinates based on the provided graph image layout
        locations.put("LULU", new LocationNode("lulu", "LULU", 10, 10));      // top-left
        locations.put("JEWETT", new LocationNode("jewett", "JEWETT", 10, 40)); // middle-left
        locations.put("TOWER", new LocationNode("tower", "TOWER", 10, 70));    // bottom-left
        locations.put("CLAPP", new LocationNode("clapp", "CLAPP", 25, 85));    // bottom-center-left
        locations.put("FOUNDERS", new LocationNode("founders", "FOUNDERS", 50, 50)); // middle-center
        locations.put("SCIENCE", new LocationNode("science", "SCIENCE", 90, 10)); // top-right
        locations.put("CHAPEL", new LocationNode("chapel", "CHAPEL", 80, 70));  // bottom-center
        locations.put("CLUB", new LocationNode("club", "CLUB", 90, 85));        // bottom-right
        
        // Pendleton is shown in graph but not a game location - add it for visualization
        locations.put("PENDLETON", new LocationNode("pendleton", "PENDLETON", 50, 20)); // top-center
    }
    
    /**
     * Initialize connections between locations based on the provided graph.
     */
    private static void initializeConnections() {
        // Lulu connections
        addConnection("LULU", "JEWETT");
        
        // Jewett connections
        addConnection("JEWETT", "LULU");
        addConnection("JEWETT", "TOWER");
        addConnection("JEWETT", "PENDLETON");
        
        // Tower connections
        addConnection("TOWER", "JEWETT");
        addConnection("TOWER", "CLAPP");
        addConnection("TOWER", "FOUNDERS");
        
        // Clapp connections
        addConnection("CLAPP", "TOWER");
        addConnection("CLAPP", "CHAPEL");
        
        // Pendleton connections
        addConnection("PENDLETON", "JEWETT");
        addConnection("PENDLETON", "FOUNDERS");
        addConnection("PENDLETON", "SCIENCE");
        
        // Science connections
        addConnection("SCIENCE", "PENDLETON");
        addConnection("SCIENCE", "FOUNDERS");
        addConnection("SCIENCE", "CHAPEL");
        
        // Founders connections
        addConnection("FOUNDERS", "TOWER");
        addConnection("FOUNDERS", "PENDLETON");
        addConnection("FOUNDERS", "SCIENCE");
        addConnection("FOUNDERS", "CHAPEL");
        
        // Chapel connections
        addConnection("CHAPEL", "FOUNDERS");
        addConnection("CHAPEL", "SCIENCE");
        addConnection("CHAPEL", "CLAPP");
        addConnection("CHAPEL", "CLUB");
        
        // Club connections
        addConnection("CLUB", "CHAPEL");
    }
    
    private static void addConnection(String from, String to) {
        connections.putIfAbsent(from, new ArrayList<>());
        connections.get(from).add(to);
    }
    
    /**
     * Get all location nodes.
     * @return Collection of all location nodes
     */
    public static Collection<LocationNode> getAllLocations() {
        return locations.values();
    }
    
    /**
     * Get a location node by its key.
     * @param locationKey The location key (e.g., "LULU")
     * @return LocationNode or null if not found
     */
    public static LocationNode getLocation(String locationKey) {
        return locations.get(locationKey);
    }
    
    /**
     * Get connections for a specific location.
     * @param locationKey The location key
     * @return List of connected location keys, or empty list if none
     */
    public static List<String> getConnections(String locationKey) {
        return connections.getOrDefault(locationKey, new ArrayList<>());
    }
    
    /**
     * Convert game location key to display name.
     * @param locationKey The location key (e.g., "LULU" or "DORM")
     * @return Display name (e.g., "lulu") or the key itself if not found
     */
    public static String getDisplayName(String locationKey) {
        // Map DORM to TOWER
        if ("DORM".equals(locationKey)) {
            locationKey = "TOWER";
        }
        LocationNode node = locations.get(locationKey);
        return node != null ? node.displayName : locationKey.toLowerCase();
    }
    
    /**
     * Convert display name to location key.
     * @param displayName The display name (e.g., "lulu")
     * @return Location key (e.g., "LULU") or null if not found
     */
    public static String getLocationKey(String displayName) {
        for (LocationNode node : locations.values()) {
            if (node.displayName.equalsIgnoreCase(displayName)) {
                return node.locationKey;
            }
        }
        return null;
    }
}