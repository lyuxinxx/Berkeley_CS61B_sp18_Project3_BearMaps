import java.util.LinkedList;

public class Node implements Comparable<Node>{
    public LinkedList<Edge> edges;
    public long id;
    public double lat;
    public double lon;
    public double priority;
    public String way;
    public boolean isWay = false;

    public Node(long id, double lon, double lat) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        edges = new LinkedList<>();
    }

    public void setPriority(double p) {
        priority = p;
    }

    public int compareTo(Node that) {
        if (priority > that.priority) return 1;
        else if (priority == that.priority) return 0;
        else return -1;
    }
}
