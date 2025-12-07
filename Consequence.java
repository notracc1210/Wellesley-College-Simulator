/**
 * 在这里给出对类 hashForLocation 的描述。
 * 
 * @author Tracy
 * @version 2025.12.7
 */
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
