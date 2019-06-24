package studios.hawkeyegame.hackweekbot.chatbot;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class Quad implements Serializable {

    private String id, t1, t2, t3, t4;
    private boolean canStart = false, canEnd = false;
    private List<String> related;

    public Quad() {}

    public Quad(String t1, String t2, String t3, String t4) {
        id = t1 + t2 + t3 + t4;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        related = Lists.newArrayList();
    }

    public String getToken(int index) {
        switch (index) {
            case 0:
                return  t1;
            case  1:
                return t2;
            case 2:
                return t3;
            case 3:
                return t4;
        }
        return null;
    }

    public void addRelated(String id) {
        if (related == null) {
            related = Lists.newArrayList();
        }
        related.add(id);
    }

    public void addAllRelated(List<String> ids) {
        if (related == null) {
            related = Lists.newArrayList();
        }
        related.addAll(ids);
    }

    public void setCanStart(boolean flag) {
        canStart = flag;
    }

    public void setCanEnd(boolean flag) {
        canEnd = flag;
    }

    public boolean canStart() {
        return canStart;
    }

    public boolean canEnd() {
        return canEnd;
    }

    public boolean isValid() {
        return t1 != null && t2 != null && t3 != null && t4 != null;
    }

    public List<String> getRelated() {
        if (related == null) {
            related = Lists.newArrayList();
        }
        return related;
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return t1.hashCode() + t2.hashCode() + t3.hashCode() + t4.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Quad) {
            Quad q = (Quad) o;
            return q.t1.equals(t1) && q.t2.equals(t2) && q.t3.equals(t3) && q.t4.equals(t4);
        }
        return false;
    }

    public boolean nearMatch(Quad q) {
        boolean m1 = q.t1.equalsIgnoreCase(t1);
        boolean m2 = q.t2.equalsIgnoreCase(t2);
        boolean m3 = q.t3.equalsIgnoreCase(t3);
        boolean m4 = q.t4.equalsIgnoreCase(t4);
        return m1 && m2 && m3 || m1 && m2 && m4 || m1 && m3 && m4 || m2 && m3 && m4;
    }
}
