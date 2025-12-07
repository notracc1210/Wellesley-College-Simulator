import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameManager {

    // --- INNER CLASSES ---

    // 1. Option Class
    public class Option {
        String text;             
        String consequenceText;  // Text shown after picking this option
        
        // Stats consequences
        double gpaEffect;       
        int happinessEffect;
        int socialEffect;
        int healthEffect;        

        public Option(String text, String consequenceText, int happy, int health, int social, double gpa) {
            this.text = text;
            this.consequenceText = consequenceText;
            this.happinessEffect = happy;
            this.healthEffect = health;
            this.socialEffect = social;
            this.gpaEffect = gpa;
        }

        public double getGPAChange() { return gpaEffect; }
        public int getHappinessChange() { return happinessEffect; }
        public int getSocialChange() { return socialEffect; }
        public int getHealthChange() { return healthEffect; }
    }

    // 2. EventNode Class
    public class EventNode {
        String locationTag; // e.g., LULU, TOWER
        String mainText;    
        List<Option> options;

        public EventNode(String location, String text) {
            this.locationTag = location;
            this.mainText = text;
            this.options = new ArrayList<>();
        }

        public void addOption(Option c) {
            options.add(c);
        }
    }

    // --- FIELDS ---

    private Map<String, List<EventNode>> locationEvents; // Stores all loaded events by location
    private EventNode currentEvent;
    private GameStat gameTracker; 
    private Random random;

    // State flags
    private boolean isInNavigationMode; // True = Picking a location, False = In an event
    private boolean isForcedRandomEvent; // True if we are in the end-of-day random event

    // --- CONSTRUCTOR ---

    public GameManager(String filePath) {
        locationEvents = new HashMap<>();
        gameTracker = new GameStat(); 
        random = new Random();
        
        // 1. Load the text file content
        importFile(filePath);
        
        // 2. Start the game in Navigation Mode
        setNavigationState();
    }

    // --- FILE PARSING (Integrated from hashForLocation) ---

    public void importFile(String fileName){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String currentLocation = "";
            EventNode tempNode = null;
            String line = br.readLine();
            
            while(line != null){
                line = line.trim();
                
                if(line.equals("A NEW CONTEXT BEGINS") || line.isEmpty()){
                    // Do nothing, just skip
                }
                else if(line.startsWith("LOCATION:")){
                    currentLocation = line.substring("LOCATION:".length()).trim();
                }
                else if(line.startsWith("CONTEXT:")){
                    String contextText = line.substring("CONTEXT:".length()).trim();
                    // Create a new event node
                    tempNode = new EventNode(currentLocation, contextText);
                }
                else if(line.startsWith("OPTION:")){
                    if (tempNode != null) {
                        String optionLine = line.substring("OPTION:".length()).trim();
                        // Split by pipe | to get: [Description] | [Consequence] | [Stats]
                        String[] parts = optionLine.split("\\|");
                        
                        if (parts.length >= 3) {
                            String optDesc = parts[0].trim();
                            String optCons = parts[1].trim();
                            String[] stats = parts[2].trim().split(",");
                            
                            // Parse stats: Happy, Health, Social, GPA
                            int dHappy = Integer.parseInt(stats[0]);
                            int dHealth = Integer.parseInt(stats[1]);
                            int dSocial = Integer.parseInt(stats[2]);
                            double dGPA = Double.parseDouble(stats[3]);
                            
                            tempNode.addOption(new Option(optDesc, optCons, dHappy, dHealth, dSocial, dGPA));
                        }
                    }
                }
                else if(line.startsWith("END")){
                    // Save the finished node to the map
                    if (tempNode != null && !currentLocation.isEmpty()) {
                        locationEvents.putIfAbsent(currentLocation, new ArrayList<>());
                        locationEvents.get(currentLocation).add(tempNode);
                    }
                }
                
                line = br.readLine();
            }
        } catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // --- GAME LOGIC ---

    /**
     * Sets the game state to the main menu where the user picks a location.
     */
    private void setNavigationState() {
        isInNavigationMode = true;
        isForcedRandomEvent = false;

        // Create a dummy node representing the map
        EventNode navNode = new EventNode("MAP", "It is " + gameTracker.getSeason() + 
            ". You have " + gameTracker.getCurrentEnergy() + " energy left today.\nWhere do you want to go?");
        
        // Add navigation options (these don't change stats, they just move you)
        // 0s for all stats because the cost is applied when the event starts
        navNode.addOption(new Option("Go to Lulu (Dining)", "", 0, 0, 0, 0));
        navNode.addOption(new Option("Go to Clapp (Library)", "", 0, 0, 0, 0));
        navNode.addOption(new Option("Go to Jewett (Arts)", "", 0, 0, 0, 0));
        navNode.addOption(new Option("Go to Science Center", "", 0, 0, 0, 0));
        navNode.addOption(new Option("Go to Founders (Liberal Arts)", "", 0, 0, 0, 0));
        navNode.addOption(new Option("Go to Tower (Dorm)", "", 0, 0, 0, 0));
        navNode.addOption(new Option("Go to Chapel/Shuttle", "", 0, 0, 0, 0));
        navNode.addOption(new Option("Go to Club/Lake", "", 0, 0, 0, 0)); // Mapped to CLUB in text file

        currentEvent = navNode;
    }

    /**
     * Pulls a random event from the loaded file for a specific location.
     */
    private void triggerLocationEvent(String locationKey) {
        if (locationEvents.containsKey(locationKey)) {
            List<EventNode> events = locationEvents.get(locationKey);
            // Pick a random event
            EventNode next = events.get(random.nextInt(events.size()));
            currentEvent = next;
            isInNavigationMode = false;
        } else {
            System.out.println("Error: No events found for " + locationKey);
            setNavigationState(); // Fallback
        }
    }

    /**
     * Triggers the mandatory random event at the end of the day (Energy = 0).
     */
    private void triggerEndOfDayEvent() {
        isForcedRandomEvent = true;
        isInNavigationMode = false;

        // Pull a random event from ANY location to represent chaos/life
        // Or specifically from TOWER/DORM if preferred. Let's do random from all keys.
        List<String> keys = new ArrayList<>(locationEvents.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        
        List<EventNode> events = locationEvents.get(randomKey);
        EventNode next = events.get(random.nextInt(events.size()));
        
        // Override the text slightly to indicate it's the end of the day
        currentEvent = next;
        // We handle the text display in the UI getter
    }

    /**
     * Main method called by the UI when a button is clicked.
     */
    public void processChoice(int choiceIndex) {
        if (gameTracker.isEnding()) return; // Game over check

        // SCENARIO 1: We are in Navigation Mode (Map)
        if (isInNavigationMode) {
            String targetLocation = "";
            switch (choiceIndex) {
                case 0: targetLocation = "LULU"; break;
                case 1: targetLocation = "CLAPP"; break;
                case 2: targetLocation = "JEWETT"; break;
                case 3: targetLocation = "SCIENCE"; break;
                case 4: targetLocation = "FOUNDERS"; break;
                case 5: targetLocation = "TOWER"; break;
                case 6: targetLocation = "CHAPEL"; break;
                case 7: targetLocation = "CLUB"; break;
            }
            // Consume Energy for the action
            gameTracker.useEnergy(); 
            triggerLocationEvent(targetLocation);
            return;
        }

        // SCENARIO 2: We are inside an Event (Resolving a choice)
        if (choiceIndex < currentEvent.options.size()) {
            Option selected = currentEvent.options.get(choiceIndex);
            
            // 1. Apply Stats
            gameTracker.getPlayerStats().performConsequence(selected);

            // 2. Logic for "Next Step"
            
            // If we just finished the Forced Random Event, the day is over.
            if (isForcedRandomEvent) {
                // Since energy was 0, useEnergy() inside GameStat class should have already
                // triggered advanceMonth() when we clicked the Navigation button previously.
                // However, per your prompt: "Day ends after 3 actions AND the random event."
                
                // We need to manually reset energy or allow the calendar to advance here.
                // Assuming gameStat handles the advancement automatically when hitting 0,
                // we just go back to navigation for the new month.
                
                if (gameTracker.isEnding()) {
                   // Logic handled in UI via getEndingText()
                } else {
                   setNavigationState();
                }
            } 
            // If we just finished a normal Action
            else {
                // Check if we are out of energy. 
                // Note: gameTracker.currentEnergy is decremented immediately when choosing location.
                
                if (gameTracker.getCurrentEnergy() == 0) {
                    // Trigger the forced random event
                    triggerEndOfDayEvent();
                } else {
                    // Go back to map for next action
                    setNavigationState();
                }
            }
        }
    }

    // --- ENDINGS ---

    public String getEndingText() {
        if (!gameTracker.isEnding()) return "";

        PlayerStat p = gameTracker.getPlayerStats();
        
        // Logic for different endings based on stats
        if (p.getGPA() > 3.8 && p.getSocial() > 80) {
            return "THE LEGEND ENDING: You graduated Summa Cum Laude while knowing everyone on campus. You are unstoppable.";
        } else if (p.getGPA() > 3.8 && p.getSocial() < 40) {
            return "THE LONE GENIUS ENDING: You have perfect grades, but your only friends are the library squirrels.";
        } else if (p.getGPA() < 2.5 && p.getSocial() > 90) {
            return "THE PARTY ENDING: You barely passed, but you're already famous. Who needs a degree when you have vibes?";
        } else if (p.getHealth() < 20) {
            return "THE BURNOUT ENDING: You survived, but at what cost? You need a 3-month nap.";
        } else {
            return "THE BALANCED ENDING: You did it! A solid degree, good friends, and a bright future.";
        }
    }

    // --- GETTERS FOR UI ---
    
    public String getCurrentText() { 
        if (gameTracker.isEnding()) return getEndingText();
        
        String prefix = "";
        if (isForcedRandomEvent) {
            prefix = "[END OF DAY EVENT] \n";
        }
        return prefix + currentEvent.mainText; 
    }
    
    public List<Option> getCurrentOptions() { 
        if (gameTracker.isEnding()) return new ArrayList<>(); // No options if game over
        return currentEvent.options; 
    }
    
    public String getStatsDisplay() {
        PlayerStat p = gameTracker.getPlayerStats();
        return String.format("Year: %d | Month: %d | Energy: %d/3\nGPA: %.2f | Happy: %d | Social: %d | Health: %d", 
               gameTracker.getYear(), gameTracker.getMonth(), gameTracker.getCurrentEnergy(),
               p.getGPA(), p.getHappiness(), p.getSocial(), p.getHealth());
    }
}
