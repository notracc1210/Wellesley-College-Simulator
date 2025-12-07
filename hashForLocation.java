import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;

/**
 * 在这里给出对类 hashForLocation 的描述。
 * 
 * @author Tracy
 * @version 2025.12.6
 */

class Context
{
    String description;
    ArrayList<Option> options;
    
    public Context(String d){
        description = d;
        options = new ArrayList<>();
    }
    
    public void addOption(Option o){
        options.add(o);
    }
    
}

class Option
{
    String description;
    Consequence consequence;
    
    public Option(String d, Consequence c){
        description = d;
        consequence = c;
    }
    
    public int getHappinessChange(){
        return consequence.consequence_happiness;
    }
    
    public int getHealthChange(){
        return consequence.consequence_health;
    }
    
    public int getSocialChange(){
        return consequence.consequence_socialconnection;
    }
    
    public double getGPAChange(){
        return consequence.consequence_gpa;
    }
}

class Consequence
{
    String description;
    int consequence_happiness,consequence_socialconnection,consequence_health;
    double consequence_gpa;
    
    public Consequence(String d, int h1, int h2, int sc, double g){
        description = d;
        consequence_happiness = h1;
        consequence_health = h2;
        consequence_socialconnection = sc;
        consequence_gpa = g;
    }
}

public class hashForLocation
{
    HashMap<String, ArrayList<Context>> map = new HashMap<>();
    
    public void importFile(String fileName){
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String currentLocation = "";
            Context currentContext = null;
            String line = br.readLine();
            
            while(line != null){
                line = line.trim();
                if(line.equals("A NEW CONTEXT BEGINS") || line == null){
                    line = br.readLine();
                }
                else if(line.startsWith("LOCATION:")){
                    currentLocation = line.substring("LOCATION:".length()).trim();
                }
                else if(line.startsWith("CONTEXT:")){
                    currentContext = new Context(line.substring("CONTEXT:".length()).trim());
                }
                else if(line.startsWith("OPTION:")){
                    String optionTxt = line.substring("OPTION:".length()).trim();
                    String parts[] = optionTxt.split("\\|");
                    String optionDescription = parts[0].trim();
                    String optionConsequenceText = parts[1].trim();
                    String[] optionDeltaStats = parts[2].trim().split(",");
                    
                    
                    int deltaHappiness = Integer.parseInt(optionDeltaStats[0]);
                    int deltaHealth = Integer.parseInt(optionDeltaStats[1]);
                    int deltaSocialConnection = Integer.parseInt(optionDeltaStats[2]);
                    double deltaGPA = Double.parseDouble(optionDeltaStats[3]);
                    
                    currentContext.addOption(new Option(optionDescription, new Consequence(optionConsequenceText, deltaHappiness, deltaHealth, deltaSocialConnection, deltaGPA)));
                }
                else if(line.startsWith("END")){
                    if(!map.containsKey(currentLocation)){
                        map.put(currentLocation, new ArrayList<>());
                    }
                    map.get(currentLocation).add(currentContext);
                }
                
                line = br.readLine();
            }
            
            br.close();
        }
        catch(IOException e){
            System.out.println("Fail to read");
        }
        
    }
}