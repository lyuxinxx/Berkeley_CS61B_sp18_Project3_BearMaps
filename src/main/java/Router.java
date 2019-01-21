import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        long start = g.closest(stlon, stlat);
        long end = g.closest(destlon, destlat);

        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<Long, Long> edgeTo = new TreeMap<>();
        Map<Long, Double> distTo = new TreeMap<>();
        Map<Long, Boolean> marked = new TreeMap<>();

        for (Long i : g.vertices()) {
                distTo.put(i, Double.POSITIVE_INFINITY);
                marked.put(i, false);
        }

        distTo.put(start, 0.0);
        g.getNode(start).setPriority(g.distance(end, start));
        pq.add(g.getNode(start));

        while (!pq.isEmpty()) {
            Node v = pq.poll();
            if (v.id == end) break;
            if (marked.get(v.id)) continue;
            marked.put(v.id, true);
            for (long i : g.adjacent(v.id)) {
                if (g.getNode(i).isWay && distTo.get(i) > distTo.get(v.id) + g.distance(v.id, i)) {
                    Node w = g.getNode(i);
                    distTo.put(w.id, distTo.get(v.id) + g.distance(v.id, w.id));
                    w.setPriority(distTo.get(v.id) + g.distance(v.id, w.id) + g.distance(w.id,
                            end));
                    pq.add(w);
                    edgeTo.put(w.id, v.id);
                }
            }
        }

        Stack<Long> route = new Stack<>();
        long i = end;
        while (i != start) {
            route.add(i);
            i = edgeTo.get(i);
        }
        route.add(start);

        List<Long> ans = new ArrayList<>();
        while (!route.isEmpty()) ans.add(route.pop());
        return ans;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {

        List<NavigationDirection> directions = new ArrayList<>();

        Node thisStart = g.getNode(route.get(0));
        Node lastEnd = thisStart;

        Node currNode = thisStart;
        Node prevNode = thisStart;
        Node nextNode = thisStart;
        for (int i = 0; i < route.size()-2; i++) {
            currNode = g.getNode(route.get(i+1));
            prevNode = g.getNode(route.get(i));
            nextNode = g.getNode(route.get(i+2));
            if (!currNode.way.equals(prevNode.way) && !prevNode.way.equals(nextNode.way)) {
                if (directions.isEmpty()) {
                    NavigationDirection d = new NavigationDirection();
                    d.direction = NavigationDirection.START;
                    d.way = prevNode.way;
                    d.distance = g.distance(thisStart.id, prevNode.id);
                    directions.add(d);
                } else {
                    if (g.bearing(lastEnd.id, thisStart.id) >= -15 && g.bearing(lastEnd.id,
                            thisStart.id) <= 15) {
                        NavigationDirection d = new NavigationDirection();
                        d.direction = NavigationDirection.STRAIGHT;
                        d.way = prevNode.way;
                        d.distance = g.distance(thisStart.id, prevNode.id);
                        directions.add(d);
                    } else if (g.bearing(lastEnd.id, thisStart.id) >= -30 && g.bearing(lastEnd.id,
                            thisStart.id) < -15 ) {
                        NavigationDirection d = new NavigationDirection();
                        d.direction = NavigationDirection.SLIGHT_LEFT;
                        d.way = prevNode.way;
                        d.distance = g.distance(thisStart.id, prevNode.id);
                        directions.add(d);
                    } else if (g.bearing(lastEnd.id, thisStart.id) <= 30 && g.bearing(lastEnd.id,
                            thisStart.id) > 15 ) {
                        NavigationDirection d = new NavigationDirection();
                        d.direction = NavigationDirection.SLIGHT_RIGHT;
                        d.way = prevNode.way;
                        d.distance = g.distance(thisStart.id, prevNode.id);
                        directions.add(d);
                    } else if (g.bearing(lastEnd.id, thisStart.id) >= -100 && g.bearing(lastEnd.id,
                            thisStart.id) < -30 ) {
                        NavigationDirection d = new NavigationDirection();
                        d.direction = NavigationDirection.LEFT;
                        d.way = prevNode.way;
                        d.distance = g.distance(thisStart.id, prevNode.id);
                        directions.add(d);
                    } else if (g.bearing(lastEnd.id, thisStart.id) <= 100 && g.bearing(lastEnd.id,
                            thisStart.id) > 30 ) {
                        NavigationDirection d = new NavigationDirection();
                        d.direction = NavigationDirection.RIGHT;
                        d.way = prevNode.way;
                        d.distance = g.distance(thisStart.id, prevNode.id);
                        directions.add(d);
                    } else if (g.bearing(lastEnd.id, thisStart.id) < -100) {
                        NavigationDirection d = new NavigationDirection();
                        d.direction = NavigationDirection.SHARP_LEFT;
                        d.way = prevNode.way;
                        d.distance = g.distance(thisStart.id, prevNode.id);
                        directions.add(d);
                    } else if (g.bearing(lastEnd.id, thisStart.id) > 100) {
                        NavigationDirection d = new NavigationDirection();
                        d.direction = NavigationDirection.SHARP_RIGHT;
                        d.way = prevNode.way;
                        d.distance = g.distance(thisStart.id, prevNode.id);
                        directions.add(d);
                    }
                }
                lastEnd = prevNode;
                thisStart = currNode;
            }
        }
        NavigationDirection d = new NavigationDirection();
        d.direction = NavigationDirection.SHARP_RIGHT;
        d.way = prevNode.way;
        d.distance = g.distance(thisStart.id, currNode.id);
        directions.add(d);

//        if (lastEnd.id == thisStart.id) {
//            NavigationDirection d = new NavigationDirection();
//            d.direction = NavigationDirection.START;
//            d.way = thisStart.way;
//            d.distance = g.distance(thisStart.id, route.get(route.size()-1));
//            directions.add(d);
//        }
        return directions;
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
