/**
* A class to store the current game stats, including the framework of keeping track of the time throughout the game
* @author (Melody Lyu)
* @date (Dec.4, 2025)
**/
public int year; //1-4
public int month; //1-8
public int currentEnergy; //0-3
private final int totalEnergy = 3;
private PlayerStat pStats;

/**
* Constructor for a new game
*/
public gameStat(){
  this.year = 1;
  this.month = 1;
  this.currentEnergy = 3;
}

/**
* Deduct one energy when player performs an action
* Check if the energy is zero, then advance month
**/
public void useEnergy(){
  if (currentEnergy > 0){
    currentEnergy--;
  }
  if (isEnergyZero()){
    advanceMonth();
  }
}

/**
* check if there is more energy to perform action in this month
* @return boolean, true if energy is used up and should increment to the next month
**/
private boolean isEnergyZero(){
  return (currentEnergy == 0);
}

/**
* Reset energy at the start of each new month
**/
private void resetEnergy(){
  currentEnergy = totalEnergy;
}

/**
* If the energy is used up then increment to the next month
* check if the all month in the academic year has passed, then increment year
**/
private void advanceMonth(){
  month++;
  resetEnergy();
  if (month > 8){
    advanceYear();
  }
}

/**
* Helper method to increment to next academic year and reset month count
**/
private void advanceYear(){
  year++;
  month = 1;
}

/** 
* constantly checking if the game reaches the end
* @return boolean, true if the game should end
**/
public boolean isEnding(){
  return (year > 4) || !pStats.isValidStat();
}

/**
* Getters for the instance variables
**/
public int getYear() {return year;}
public int getMonth() {return month;}
public int getCurrentEnergy() {return currentEnergy;}
public PlayerStat getPlayerStats() {return pStats;}
