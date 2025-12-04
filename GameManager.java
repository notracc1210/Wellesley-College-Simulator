
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class GameManager {
    // A simple class to hold the player's current status
class StudentStats {
    double gpa;
    int happiness;
    int socialConnection;

    public StudentStats() {
        this.gpa = 4.0;
        this.happiness = 100;
        this.socialConnection = 50;
    }
    
    // Use this to update stats easily
    public void update(double gpaChange, int happyChange, int socialChange) {
        this.gpa = Math.max(0.0, Math.min(4.0, this.gpa + gpaChange));
        this.happiness = Math.max(0, Math.min(100, this.happiness + happyChange));
        this.socialConnection = Math.max(0, Math.min(100, this.socialConnection + socialChange));
    }
}

// Represents one button the user can click
class Choice {
    String text;             // What the button says
    String nextEventId;      // The ID of the node this leads to
    double gpaEffect;        // Consequences
    int happinessEffect;
    int socialEffect;

    public Choice(String text, String nextId, double gpa, int happy, int social) {
        this.text = text;
        this.nextEventId = nextId;
        this.gpaEffect = gpa;
        this.happinessEffect = happy;
        this.socialEffect = social;
    }
}


class EventNode {
    String eventId;// Unique ID, e.g., "intro_01"
    String speaker; //For NPC dialogue
    String mainText;     // The story text/question
    List<Choice> options; // The edges of your graph

    public EventNode(String id, String text) {
        this.eventId = id;
        this.mainText = text;
        this.options = new ArrayList<>();
    }
    public EventNode(String id, String speaker, String text) {
        this.eventId = id;
        this.speaker = speaker; // Store who is talking
        this.mainText = text;
        this.options = new ArrayList<>();
    }

    public void addOption(Choice c) {
        options.add(c);
    }
}
    // The Hash Map
    private Map<String, EventNode> storyMap; 
    private EventNode currentEvent;
    private StudentStats currentStats;
    private String[] RAlist;
    private Calendar yearCal;

    public GameManager() {
        storyMap = new HashMap<>();
        currentStats = new StudentStats();
        loadStory(); // Builds the tree/graph
        String[] RAlist = {"Tara", "Ellie", "Paige", "Maya", "Carmen"};
        this.RAlist = RAlist;
        yearCal = new Calendar();
    }

    // This is where we "Build the Tree"
    private void loadStory() {
        // 1. Create Nodes
        EventNode start = new EventNode("root", "It is your first day at Wellesley. Where do you go?");
        EventNode library = new EventNode("library", "You are in Clapp Library. It's quiet.");
        EventNode lulu = new EventNode("lulu", "You are at Lulu. It's loud and smells like food.");
        EventNode KSC = new EventNode("KSC", "You are at the KSC. There's music blasting from the varsity gym.");
        EventNode dorm = new EventNode("dorm", "You are back at your dorm. What do you want to do?");
        // 2. Add Choices (The edges connecting the nodes)
        // Choice: Text, NextNodeID, GPA, Happy, Social
        start.addOption(new Choice("Go to Library", "library", 0.1, -5, -5));
        start.addOption(new Choice("Go to Lulu", "lulu", 0.0, 10, 5));
        start.addOption(new Choice("Go to KSC", "KSC", -0.1, 12, 3));
        start.addOption(new Choice("Go to your dorm", "dorm", 0.0, 5, 0));
        
        library.addOption(new Choice("Study Hard", "root", 0.2, -10, 0)); // Loops back to start
        library.addOption(new Choice("Go back to your dorm", "dorm", 0.0, 5, 0));
        
        lulu.addOption(new Choice("Eat Pizza", "root", -0.1, 15, 10));
        lulu.addOption(new Choice("Eat a salad", "root", 0.1, 0, 10));
        
        KSC.addOption(new Choice("Work out", "KSC", 0.0, 10, 1));
        KSC.addOption(new Choice("Go study instead", "library", 0.2, -10, 0));
        KSC.addOption(new Choice("Go back to your dorm", "root", 0.0, 5, 0));
        
        dorm.addOption(new Choice("Talk to your RA", "RA", 0.1, 5, 10));
        EventNode RA = new EventNode("RA", "The RA on duty is " + chooseRA() + ".");
        
        RA.addOption(new Choice("How do I improve my happiness here?", "Ra_advice1", 0.1, 0, 0));
    RA.addOption(new Choice("Just saying hi", "RA_happy", 0.0, 5, 5));
    RA.addOption(new Choice("How do I improve my health here?", "Ra_advice2", 0.0, 1, 0));
    RA.addOption(new Choice("Leave", "dorm", 0.0, 0, 0)); // Returns to the main map!

    // 3. The Consequences (Sub-nodes)
    
    // Path A: 
    EventNode RAadvice1 = new EventNode("Ra_advice1", "RA", "You could go to the gym, but that might hurt your GPA if you spend too much time.");
    RAadvice1.addOption(new Choice("Thanks.", "RA", 0.0, 0, 0)); // Kicks you back to library

    // Path B: 
    EventNode RA_Happy = new EventNode("RA_happy", "RA", "Oh, well, hello! Always good to see a familiar face.");
    RA_Happy.addOption(new Choice("Yeah, of course!", "RA", 0.0, 5, 0)); // Returns to library
    
    EventNode RAadvice2 = new EventNode("Ra_advice2", "RA", "I recommend eating a salad. They're very nutritious.");
    RAadvice1.addOption(new Choice("Thanks.", "RA", 0.0, 0, 0));     
    
    // 4. Add to Map
    storyMap.put("RA", RA);
    storyMap.put("Ra_advice1", RAadvice1);
    storyMap.put("RA_happy", RA_Happy);
    storyMap.put("Ra_advice2", RAadvice2);
        
    // Example of looping
EventNode answerNode = new EventNode("RA_answer_1", "RA", "Happy to answer more questions.");

// The button here doesn't exit; it goes back to the question list ("prof_intro")
answerNode.addOption(new Choice("I have another question.", "RA", 0, 0, 0));  

        // 3. Put into Hash Map
        storyMap.put("root", start);
        storyMap.put("library", library);
        storyMap.put("lulu", lulu);
        storyMap.put("KSC", KSC);
        storyMap.put("dorm", dorm);

        // Set initial state
        currentEvent = start;
        
    }
    public String chooseRA(){
        int randomIndex = (int) (Math.random() * 5);
        if(randomIndex == 1){
            return RAlist[0];
        }
        if(randomIndex == 2){
            return RAlist[1];
        }
        if(randomIndex == 3){
            return RAlist[2];
        }
        if(randomIndex == 4){
            return RAlist[3];
        }
        if(randomIndex == 5){
            return RAlist[4];
        }
        return "";
    }
    // Method called when user clicks a button
    public void processChoice(int choiceIndex) {
        if (choiceIndex >= currentEvent.options.size()) return;

        Choice selected = currentEvent.options.get(choiceIndex);
        
        // Update stats
        currentStats.update(selected.gpaEffect, selected.happinessEffect, selected.socialEffect);
        
        // Move to next node using the Hash Map
        if (storyMap.containsKey(selected.nextEventId)) {
            currentEvent = storyMap.get(selected.nextEventId);
        }
    }

    // Getters for the GUI to use
    public String getCurrentText() { return currentEvent.mainText; }
    public StudentStats getStats() { return currentStats; }
    public List<Choice> getCurrentOptions() { return currentEvent.options; }
}
