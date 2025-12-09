
/**
 * 在这里给出对类 AchievementNode 的描述。
 * 
 * @author (你的名字)
 * @version (一个版本号或者一个日期)
 */
public class AchievementNode extends Node
{
    String achievementName;
    String achievementDescripton;
    public AchievementNode(String aN, String aD){
        achievementName = aN;
        achievementDescripton = aD;
    }
    
    String getAchievement(PlayerStat stat){
        return achievementName + "\n" + achievementDescripton;
    }
}
