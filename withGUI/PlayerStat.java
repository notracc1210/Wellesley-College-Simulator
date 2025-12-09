/**
* A class to store the player stats, including the framework of keeping track of the characteristic of the player and modifying them
* @author (Melody Lyu)
* @date (Dec.7, 2025)
**/

public class PlayerStat{
    //instance variables for the characteristics
    private int happiness; //0-100
    private int health; //0-100
    private int socialConnection; //0-100
    private double GPA; //0.0 - 4.0
    
    /**
    * Constructor that set each variable to default value
    **/
    public PlayerStat(){
      this.happiness = 50;
      this.health = 75;
      this.socialConnection = 10;
      this.GPA = 4.0;
    }
    
    /**
    * Perform the consequence of the option from random event by changing the value of varaibles 
    * @param Option e, an object from Option class
    **/
    public void performConsequence(Option e){
      happiness += e.getHappinessChange();
      health += e.getHealthChange();
      socialConnection += e.getSocialChange();
      GPA += e.getGPAChange();
    
      normalize();
    }
    
    /**
    * Helper function that make sure each variable's value is within range after the change
    **/
    private void normalize(){
      happiness = Math.max(0, Math.min(100, happiness)); //clamped to [0,100]
      health = Math.max(0, Math.min(100, health));
      socialConnection = Math.max(0, Math.min (100, socialConnection));
      GPA = Math.max(0.0, Math.min(4.0, GPA)); //clamped to [0.0,4.0]
    }
    
    /**
    * Check if the student's stats are low, causing potential burnout warning
    * @return boolean, return true if one of the value is low
    **/
    public boolean burnoutWarning(){
      return (happiness < 20 || health < 20 || socialConnection < 20);
    }
    
    /**
    * Check if the stats are valid. 
    * If any stat reaches bottom limit, an end of the game will be triggered
    * @return boolean, true if the stats are still valid to continue the game
    **/
    public boolean isValidStat(){
      // Game ends if ANY stat reaches bottom limit
      // GPA < 2.0 triggers academic dismissal (not just <= 1.0)
      return (happiness > 0 && health > 0 && socialConnection > 0 && GPA >= 2.0);
    }
    
    /**
    * Determine which stat failed first (which one is the worst/triggered the ending)
    * Returns a string indicating the failure reason
    * @return String indicating which stat failed: "GPA", "HEALTH", "HAPPINESS", or "SOCIAL"
    **/
    public String getFailureReason(){
      // Check in order of severity - return the first one that failed
      if (GPA < 2.0) {
        return "GPA";
      }
      if (health <= 0) {
        return "HEALTH";
      }
      if (happiness <= 0) {
        return "HAPPINESS";
      }
      if (socialConnection <= 0) {
        return "SOCIAL";
      }
      return "NONE"; // Shouldn't reach here if isValidStat() is checked first
    }
    
    /**
    * Apply isolation penalty when social connection has been < 10 for 3+ months
    * Causes large happiness decrease due to isolation
    **/
    public void applyIsolationPenalty(){
      happiness -= 15; // Large decrease in happiness
      normalize();
    }
    
    /**
    * To present the happiness stats into text describing the mood of the player
    * @return String, text representation of the happiness stats
    **/
    public String getMood(){
      if (happiness > 80) return "SHE'S COOKING!!";
      else if (happiness > 50) return "OKAY CHILL GIRL";
      else if (happiness > 20) return "TRIPPING A BIT BUT THATS OKAY";
      return "CRYING IN LULU BATHROOM AND THE DOOR GOT LOCKED";
    }
    
    //Getters for the instance variables
    public int getHappiness() {return happiness;}
    public int getHealth() {return health;}
    public int getSocial() {return socialConnection;}
    public double getGPA() {return GPA;}
}
