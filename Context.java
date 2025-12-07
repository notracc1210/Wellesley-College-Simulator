import java.util.ArrayList;

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