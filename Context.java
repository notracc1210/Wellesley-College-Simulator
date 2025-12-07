import java.util.ArrayList;

/**
 * 在这里给出对类 hashForLocation 的描述。
 * 
 * @author Tracy
 * @version 2025.12.7
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
