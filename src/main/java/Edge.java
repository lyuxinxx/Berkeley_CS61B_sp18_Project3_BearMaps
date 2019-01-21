public class Edge {
    Long v;
    Long w;
    String name;

    Edge(long v, long w, String name) {
        this.v = v;
        this.w = w;
        this.name = name;
    }

    long from() {
        return v;
    }

    long to() {
        return w;
    }
}
