
/**
 * 在这里给出对类 DecisionNode 的描述。
 * 
 * @author Tracy
 * @version 2025.12.7
 */
public class DecisionNode extends Node
{
    private String category;
    private String operator;
    private double condition;
    
    protected Node yesChild;
    protected Node noChild;
    
    public DecisionNode(String c1, String o, double c2, Node yes, Node no){
        category = c1;
        operator = o;
        condition = c2;
        yesChild = yes;
        noChild = no;
    }
    
    private double getValue(PlayerStat stat){
        if("GPA".equals(category)) return stat.getGPA();
        else if("HAPPINESS".equals(category)) return stat.getHappiness();
        else if("HEALTH".equals(category)) return stat.getHealth();
        else if ("SOCIAL".equals(category))return stat.getSocial();
        else return -99;
    }
    
    private boolean compare(double actual, double condition1, String operator1){
        if(">=".equals(operator1)) return actual >= condition1;
        else if("<=".equals(operator1)) return actual <= condition1;
        else return actual == condition1;
    }
    
    String getAchievement(PlayerStat stat){
        double value = getValue(stat);
        boolean compare = compare(value,condition,operator);
        if(compare) return yesChild.getAchievement(stat);
        else return noChild.getAchievement(stat);
    }
    
    public Node getYesChild(){
        return yesChild;
    }
    
    public Node getNoChild(){
        return noChild;
    }
    
}
