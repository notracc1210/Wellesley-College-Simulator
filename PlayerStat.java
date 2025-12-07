/**
* A class to store the player stats, including the framework of keeping track of the characteristic of the player and modifying them
* @author (Melody Lyu)
* @date (Dec.7, 2025)
**/

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
  social += e.getSocialChange();
  GPA += e.getGPAChange();

  normalize();
}

/**
* Helper function that make sure each variable's value is within range after the change
**/
private void normalize(){
  happiness = Math.max(0, Math.min(100, happiness)); //clamped to [0,100]
  health = Math.max(0, Math.min(100, health));
  social = Math.max(0, Math.min (100, social));
  GPA = Math.max(0.0, Math.min(4.0, GPA)); //clamped to [0.0,4.0]
}

/**
* Check if the student's stats are low, causing potential burnout warning
* @return boolean, return true if one of the value is low
**/
public boolean burnoutWarning(){
  return (happiness < 20 || health < 20 || social < 20);
}

/**
* Check if the stats are valid. 
* If any stat reaches bottom limit, an end of the game will be triggered
* @return boolean, true if the stats are still valid to continue the game
**/
public boolean isValidStat(){
  return (happiness > 0 || health > 0 || social > 0 || GPA > 1.0);
}

/**
* To present the happiness stats into text describing the mood of the player
* @return String, text representation of the happiness stats
**/
public String getMood(){
  if (happiness > 80) return "SHE'S COOKING!!";
  else if (happiness > 50) return "OKAY CHILL GIRL";;
  else if (happiness > 20) return "TRIPPING A BIT BUT THATS OKAY";;
  return "CRYING IN LULU BATHROOM AND THE DOOR GOT LOCKED";
}

//Getters for the instance variables
public int getHappiness() return happiness;
public int getHealth() return health;
public int getSocial() return socialConnection;
public double getGPA() return GPA;
