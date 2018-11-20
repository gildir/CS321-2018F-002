
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Thomas
 */
public class NPCList implements Iterable<NPC> {
    private final LinkedList<NPC> npcList;
    
    @Override
    public Iterator<NPC> iterator() {
        Iterator<NPC> iter = this.npcList.iterator();
        return iter;
    }    
    
    public NPCList() {
        this.npcList = new LinkedList<>();
    }
    
    public void addNPC(NPC npc) {
        this.npcList.add(npc);
    }
    
    public void addAllNPC(NPCList list){
      for (NPC npc : list)
      {
      this.npcList.addLast(npc);
      }
    }
    public void removeAllNPC(NPCList list){
      for (NPC npc : list)
      {
      this.npcList.remove(npc);
      }
    }
    public NPC findNPC(String name) {
        for(NPC npc : this.npcList) {
            if(npc.getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }
        return null;
    }
    
    public void removeNPC(String name) {
        NPC npc = findNPC(name);
        if(npc != null) {
            this.npcList.remove(npc);
        }
    }
    public String listOfNPCs()
    {
        String res = "";
        for(NPC npc : this.npcList)
        {
            res += npc.getName() + " ";
        }
        return res;
    }
}
