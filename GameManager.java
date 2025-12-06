
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

    // --- INNER CLASSES ---

    // 1. Stats Class
    public class StudentStats {
        double gpa;
        int happiness;
        int socialConnection;

        public StudentStats() {
            this.gpa = 4.0;
            this.happiness = 100;
            this.socialConnection = 50;
        }

        public void update(double gpaChange, int happyChange, int socialChange) {
            this.gpa = Math.max(0.0, Math.min(4.0, this.gpa + gpaChange));
            this.happiness = Math.max(0, Math.min(100, this.happiness + happyChange));
            this.socialConnection = Math.max(0, Math.min(100, this.socialConnection + socialChange));
        }
        
        @Override
        public String toString() {
            return String.format("GPA: %.2f | Happy: %d | Social: %d", gpa, happiness, socialConnection);
        }
    }

    // 2. Choice Class
    public class Choice {
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

    // 3. EventNode Class
    public class EventNode {
        String eventId;
        String speaker; 
        String mainText;    
        List<Choice> options;

        // Constructor for Narrator
        public EventNode(String id, String text) {
            this.eventId = id;
            this.speaker = "Narrator";
            this.mainText = text;
            this.options = new ArrayList<>();
        }

        // Constructor for NPC
        public EventNode(String id, String speaker, String text) {
            this.eventId = id;
            this.speaker = speaker; 
            this.mainText = text;
            this.options = new ArrayList<>();
        }

        public void addOption(Choice c) {
            options.add(c);
        }
    }

    // --- GAME MANAGER FIELDS ---

    private Map<String, EventNode> storyMap; 
    private EventNode currentEvent;
    private StudentStats currentStats;
    private String[] raNames = {"Tara", "Ellie", "Paige", "Maya", "Carmen"};
    private String currentRA; // The RA for this session

    // --- CONSTRUCTOR ---

    public GameManager() {
        storyMap = new HashMap<>();
        currentStats = new StudentStats();
        
        // Pick one RA for the whole game session
        currentRA = pickRandomRA(); 
        
        loadStory(); // Builds the tree/graph
    }

    // --- CORE LOGIC ---

    private String pickRandomRA() {
        int index = (int) (Math.random() * raNames.length); // 0 to 4
        return raNames[index];
    }

    private void loadStory() {
        // --- LEVEL 1: THE ROOT ---
        EventNode start = new EventNode("root", "It is a beautiful day at Wellesley. Where do you go?");
        
        // --- LEVEL 2: MAIN LOCATIONS ---
        EventNode library = new EventNode("library", "You enter Clapp Library. It smells like old books.");
        EventNode lulu = new EventNode("lulu", "You are at Lulu. It's chaotic during lunch rush.");
        EventNode ksc = new EventNode("KSC", "You are at the KSC (Gym). Varsity athletes are training.");
        EventNode dorm = new EventNode("dorm", "You are back in your dorm room.");
        EventNode lake = new EventNode("lake", "You are walking around Lake Waban.");

        // ROOT CONNECTIONS
        start.addOption(new Choice("Go to Library", "library", 0.0, -2, 0));
        start.addOption(new Choice("Go to Lulu", "lulu", 0.0, 5, 5));
        start.addOption(new Choice("Go to KSC", "KSC", 0.0, 5, 0));
        start.addOption(new Choice("Go to Dorm", "dorm", 0.0, 5, -5));
        start.addOption(new Choice("Walk the Lake", "lake", 0.0, 10, 0));

        // --- LEVEL 3: DEEP DIVES ---

        // == LIBRARY BRANCH ==
        EventNode libQuiet = new EventNode("lib_quiet", "You find a desk in the stacks. Absolute silence.");
        EventNode libGroup = new EventNode("lib_group", "You join a study group on the main floor.");

        library.addOption(new Choice("Go to quiet stacks", "lib_quiet", 0.05, -5, -5));
        library.addOption(new Choice("Join a group table", "lib_group", 0.0, 5, 5));
        library.addOption(new Choice("Leave", "root", 0.0, 0, 0));

        // Library Outcomes
        libQuiet.addOption(new Choice("Focus intently", "root", 0.2, -10, 0)); // High GPA, Low Happy
        libQuiet.addOption(new Choice("Scroll on phone", "root", -0.1, 5, 0)); // Low GPA, Med Happy

        libGroup.addOption(new Choice("Review notes together", "root", 0.1, 0, 5));
        libGroup.addOption(new Choice("Just gossip", "root", -0.1, 10, 15)); // Low GPA, High Social

        // == LULU BRANCH ==
        EventNode luluPizza = new EventNode("lulu_pizza", "You wait in the long line for pizza.");
        EventNode luluSalad = new EventNode("lulu_salad", "You grab a salad quickly.");

        lulu.addOption(new Choice("Get Pizza (Long line)", "lulu_pizza", 0.0, 5, 5));
        lulu.addOption(new Choice("Get Salad (Fast)", "lulu_salad", 0.0, 0, 0));

        luluPizza.addOption(new Choice("Chat with person in line", "root", 0.0, 5, 10)); // Social boost
        luluSalad.addOption(new Choice("Eat while studying", "root", 0.1, -5, 0)); // GPA boost

        // == KSC BRANCH ==
        ksc.addOption(new Choice("Heavy lifting", "root", 0.0, 10, 2));
        ksc.addOption(new Choice("Yoga class", "root", 0.0, 15, 5));
        ksc.addOption(new Choice("Too tired, go back", "root", 0.0, -5, 0));

        // == LAKE BRANCH ==
        lake.addOption(new Choice("Listen to a podcast", "root", 0.0, 15, -5)); // Recharge
        lake.addOption(new Choice("Call a friend", "root", 0.0, 10, 10)); // Social

        // == DORM & RA BRANCH (NPC DIALOGUE) ==
        dorm.addOption(new Choice("Take a nap", "root", 0.0, 10, 0));
        dorm.addOption(new Choice("Knock on RA's door", "RA_start", 0.0, 0, 5));
        dorm.addOption(new Choice("Go outside", "root", 0.0, 0, 0));

        // NPC: RA Interaction
        EventNode raStart = new EventNode("RA_start", currentRA, 
            "Hey there! I'm " + currentRA + ". Everything okay?");
        
        EventNode raAcademic = new EventNode("RA_academic", currentRA, 
            "For classes, I really recommend the PLTC tutors. Don't be afraid to ask for help!");
        EventNode raSocial = new EventNode("RA_social", currentRA, 
            "Join a specialized org! Or just leave your door open when you're in.");
        EventNode raFunny = new EventNode("RA_funny", currentRA, 
            "Honestly? I'm just trying to survive finals too.");

        // RA Tree Connections
        raStart.addOption(new Choice("I'm stressed about grades.", "RA_academic", 0.0, -2, 2));
        raStart.addOption(new Choice("I feel lonely.", "RA_social", 0.0, 2, 5));
        raStart.addOption(new Choice("Just saying hi!", "RA_funny", 0.0, 5, 5));
        raStart.addOption(new Choice("Nevermind.", "dorm", 0.0, 0, 0));

        // RA Outcomes (Loop back to RA or Leave)
        raAcademic.addOption(new Choice("Thanks, I'll go study.", "library", 0.1, 0, 0));
        raAcademic.addOption(new Choice("Can I ask something else?", "RA_start", 0.0, 0, 0));

        raSocial.addOption(new Choice("I'll go to Lulu.", "lulu", 0.0, 5, 5));
        raSocial.addOption(new Choice("Can I ask something else?", "RA_start", 0.0, 0, 0));

        raFunny.addOption(new Choice("Good luck!", "dorm", 0.0, 2, 2));

        // --- REGISTER NODES TO MAP ---
        // Root & Locations
        storyMap.put("root", start);
        storyMap.put("library", library);
        storyMap.put("lulu", lulu);
        storyMap.put("KSC", ksc);
        storyMap.put("dorm", dorm);
        storyMap.put("lake", lake);

        // Sub-Nodes
        storyMap.put("lib_quiet", libQuiet);
        storyMap.put("lib_group", libGroup);
        storyMap.put("lulu_pizza", luluPizza);
        storyMap.put("lulu_salad", luluSalad);

        // NPC Nodes
        storyMap.put("RA_start", raStart);
        storyMap.put("RA_academic", raAcademic);
        storyMap.put("RA_social", raSocial);
        storyMap.put("RA_funny", raFunny);

        // Set initial state
        currentEvent = start;
    }

    // --- INTERACTION METHODS ---

    public void processChoice(int choiceIndex) {
        if (choiceIndex >= currentEvent.options.size()) return;

        Choice selected = currentEvent.options.get(choiceIndex);
        
        // 1. Update stats
        currentStats.update(selected.gpaEffect, selected.happinessEffect, selected.socialEffect);
        
        // 2. Navigate
        if (storyMap.containsKey(selected.nextEventId)) {
            currentEvent = storyMap.get(selected.nextEventId);
        } else {
            System.out.println("ERROR: Event ID " + selected.nextEventId + " not found!");
        }
    }

    // --- GETTERS ---
    public String getCurrentText() { return currentEvent.mainText; }
    public String getCurrentSpeaker() { return currentEvent.speaker; }
    public StudentStats getStats() { return currentStats; }
    public List<Choice> getCurrentOptions() { return currentEvent.options; }
}
