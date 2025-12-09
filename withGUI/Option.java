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