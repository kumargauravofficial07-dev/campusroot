package campus.shuttle;

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

/**
 * Text-based campus shuttle planner demonstrating common graph algorithms.
 * Route weights represent distance in kilometres and must be nonnegative.
 */
public final class CampusShuttlePlanner {
    private final CampusGraph graph = new CampusGraph();
    private final Scanner input = new Scanner(System.in);

    private CampusShuttlePlanner() {
        loadSampleCampus();
    }

    public static void main(String[] args) {
        new CampusShuttlePlanner().run();
    }

    private void run() {
        System.out.println("=== Campus Shuttle Route Planner ===");
        System.out.println("Sample campus map loaded (route weights are in km).\n");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ", 0, 8);
            System.out.println();
            switch (choice) {
                case 1:
                    graph.displayNetwork();
                    break;
                case 2:
                    findShortestRoute();
                    break;
                case 3:
                    findReachableLocations();
                    break;
                case 4:
                    graph.displayConnectedGroups();
                    break;
                case 5:
                    graph.displayMinimumSpanningTree();
                    break;
                case 6:
                    addLocation();
                    break;
                case 7:
                    addRoute();
                    break;
                case 8:
                    displayComplexities();
                    break;
                case 0:
                    running = false;
                    System.out.println("Thanks for using the Campus Shuttle Route Planner.");
                    break;
                default:
                    System.out.println("Please choose an option from 0 to 8.");
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("--------------------------------------");
        System.out.println("1. Display locations and shuttle routes");
        System.out.println("2. Find the shortest route (Dijkstra)");
        System.out.println("3. Find locations within a number of stops (BFS)");
        System.out.println("4. Show connected groups (DFS)");
        System.out.println("5. Show minimum connecting routes (Kruskal)");
        System.out.println("6. Add a location");
        System.out.println("7. Add a shuttle route");
        System.out.println("8. Show algorithm time complexities");
        System.out.println("0. Exit");
    }

    private void findShortestRoute() {
        graph.displayLocations();
        String start = readExistingLocation("Start location: ");
        if (start == null) {
            return;
        }
        String destination = readExistingLocation("Destination: ");
        if (destination != null) {
            graph.displayShortestRoute(start, destination);
        }
    }

    private void findReachableLocations() {
        graph.displayLocations();
        String start = readExistingLocation("Starting location: ");
        if (start != null) {
            int stops = readInt("Maximum number of stops: ", 0, Integer.MAX_VALUE);
            graph.displayReachableWithinStops(start, stops);
        }
    }

    private void addLocation() {
        System.out.print("New location name: ");
        String name = input.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Location name cannot be empty.");
        } else if (graph.addLocation(name)) {
            System.out.println("Added location: " + name);
        } else {
            System.out.println("That location already exists.");
        }
    }

    private void addRoute() {
        graph.displayLocations();
        String from = readExistingLocation("Route from: ");
        if (from == null) {
            return;
        }
        String to = readExistingLocation("Route to: ");
        if (to == null) {
            return;
        }
        if (from.equals(to)) {
            System.out.println("Choose two different locations for a shuttle route.");
            return;
        }
        double distance = readNonnegativeDouble("Distance in km: ");
        graph.addRoute(from, to, distance);
        System.out.println("Added route: " + from + " <-> " + to + " ("
                + format(distance) + " km)");
    }

    private String readExistingLocation(String prompt) {
        System.out.print(prompt);
        String requested = input.nextLine().trim();
        String match = graph.resolveLocation(requested);
        if (match == null) {
            System.out.println("Location not found. Please use a name shown above.");
        }
        return match;
    }

    private int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Ask again below.
            }
            System.out.println("Enter a whole number from " + minimum + " to " + maximum + ".");
        }
    }

    private double readNonnegativeDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (!Double.isNaN(value) && !Double.isInfinite(value) && value >= 0.0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Ask again below.
            }
            System.out.println("Enter a valid nonnegative distance.");
        }
    }

    private void displayComplexities() {
        System.out.println("Time complexities (V = locations, E = shuttle routes):");
        System.out.println("Adjacency-list display: O(V + E)");
        System.out.println("BFS reachable locations: O(V + E)");
        System.out.println("DFS connected groups: O(V + E)");
        System.out.println("Dijkstra with a priority queue: O((V + E) log V)");
        System.out.println("Kruskal with Union-Find: O(E log E)");
    }

    private void loadSampleCampus() {
        graph.addLocation("North Hostel");
        graph.addLocation("Main Gate");
        graph.addLocation("Library");
        graph.addLocation("Science Block");
        graph.addLocation("Cafeteria");
        graph.addLocation("Student Center");
        graph.addLocation("Sports Complex");

        graph.addRoute("North Hostel", "Main Gate", 4);
        graph.addRoute("North Hostel", "Library", 6);
        graph.addRoute("Main Gate", "Library", 2);
        graph.addRoute("Main Gate", "Science Block", 5);
        graph.addRoute("Library", "Science Block", 3);
        graph.addRoute("Library", "Cafeteria", 5);
        graph.addRoute("Science Block", "Cafeteria", 2);
        graph.addRoute("Cafeteria", "Student Center", 3);
        graph.addRoute("Science Block", "Sports Complex", 6);
        graph.addRoute("Student Center", "Sports Complex", 4);
        graph.addRoute("Main Gate", "Student Center", 8);
    }

    private static String format(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private static final class CampusGraph {
        private final Map<String, List<Edge>> adjacency = new LinkedHashMap<String, List<Edge>>();

        private boolean addLocation(String name) {
            if (resolveLocation(name) != null) {
                return false;
            }
            adjacency.put(name, new ArrayList<Edge>());
            return true;
        }

        private void addRoute(String from, String to, double distance) {
            adjacency.get(from).add(new Edge(from, to, distance));
            adjacency.get(to).add(new Edge(to, from, distance));
        }

        private String resolveLocation(String requested) {
            for (String name : adjacency.keySet()) {
                if (name.equalsIgnoreCase(requested)) {
                    return name;
                }
            }
            return null;
        }

        private void displayLocations() {
            System.out.println("Campus locations:");
            for (String name : adjacency.keySet()) {
                System.out.println("  - " + name);
            }
        }

        private void displayNetwork() {
            System.out.println("Campus network (adjacency list):");
            for (Map.Entry<String, List<Edge>> entry : adjacency.entrySet()) {
                System.out.print("  " + entry.getKey() + " -> ");
                if (entry.getValue().isEmpty()) {
                    System.out.println("(no routes)");
                    continue;
                }
                for (int i = 0; i < entry.getValue().size(); i++) {
                    Edge edge = entry.getValue().get(i);
                    if (i > 0) {
                        System.out.print(", ");
                    }
                    System.out.print(edge.to + " (" + format(edge.distance) + " km)");
                }
                System.out.println();
            }
            System.out.println("Undirected routes: " + getUniqueEdges().size());
        }

        private void displayReachableWithinStops(String start, int maxStops) {
            Map<String, Integer> depth = new LinkedHashMap<String, Integer>();
            Queue<String> queue = new ArrayDeque<String>();
            depth.put(start, 0);
            queue.add(start);
            while (!queue.isEmpty()) {
                String current = queue.remove();
                int currentDepth = depth.get(current);
                if (currentDepth == maxStops) {
                    continue;
                }
                for (Edge edge : adjacency.get(current)) {
                    if (!depth.containsKey(edge.to)) {
                        depth.put(edge.to, currentDepth + 1);
                        queue.add(edge.to);
                    }
                }
            }
            System.out.println("Locations reachable from " + start + " within "
                    + maxStops + " stop(s):");
            for (Map.Entry<String, Integer> entry : depth.entrySet()) {
                System.out.println("  " + entry.getKey() + " (" + entry.getValue() + " stop(s))");
            }
        }

        private void displayConnectedGroups() {
            Set<String> visited = new LinkedHashSet<String>();
            List<List<String>> groups = new ArrayList<List<String>>();
            for (String location : adjacency.keySet()) {
                if (!visited.contains(location)) {
                    List<String> group = new ArrayList<String>();
                    depthFirstVisit(location, visited, group);
                    groups.add(group);
                }
            }
            System.out.println("Connected groups found by DFS: " + groups.size());
            for (int i = 0; i < groups.size(); i++) {
                System.out.println("  Group " + (i + 1) + ": " + String.join(", ", groups.get(i)));
            }
        }

        private void depthFirstVisit(String location, Set<String> visited, List<String> group) {
            visited.add(location);
            group.add(location);
            for (Edge edge : adjacency.get(location)) {
                if (!visited.contains(edge.to)) {
                    depthFirstVisit(edge.to, visited, group);
                }
            }
        }

        private void displayShortestRoute(String start, String destination) {
            Map<String, Double> distance = new HashMap<String, Double>();
            Map<String, String> previous = new HashMap<String, String>();
            for (String location : adjacency.keySet()) {
                distance.put(location, Double.POSITIVE_INFINITY);
            }
            distance.put(start, 0.0);
            PriorityQueue<PathNode> queue = new PriorityQueue<PathNode>(
                    new Comparator<PathNode>() {
                        @Override
                        public int compare(PathNode first, PathNode second) {
                            return Double.compare(first.distance, second.distance);
                        }
                    });
            queue.add(new PathNode(start, 0.0));

            while (!queue.isEmpty()) {
                PathNode current = queue.remove();
                if (current.distance > distance.get(current.location)) {
                    continue;
                }
                if (current.location.equals(destination)) {
                    break;
                }
                for (Edge edge : adjacency.get(current.location)) {
                    double candidate = current.distance + edge.distance;
                    if (candidate < distance.get(edge.to)) {
                        distance.put(edge.to, candidate);
                        previous.put(edge.to, current.location);
                        queue.add(new PathNode(edge.to, candidate));
                    }
                }
            }

            if (Double.isInfinite(distance.get(destination))) {
                System.out.println("No route connects " + start + " and " + destination + ".");
                return;
            }
            List<String> path = new ArrayList<String>();
            for (String at = destination; at != null; at = previous.get(at)) {
                path.add(at);
                if (at.equals(start)) {
                    break;
                }
            }
            Collections.reverse(path);
            System.out.println("Shortest route (Dijkstra): " + String.join(" -> ", path));
            System.out.println("Total distance: " + format(distance.get(destination)) + " km");
        }

        private void displayMinimumSpanningTree() {
            List<Edge> edges = getUniqueEdges();
            Collections.sort(edges, new Comparator<Edge>() {
                @Override
                public int compare(Edge first, Edge second) {
                    return Double.compare(first.distance, second.distance);
                }
            });
            List<String> locations = new ArrayList<String>(adjacency.keySet());
            Map<String, Integer> index = new HashMap<String, Integer>();
            for (int i = 0; i < locations.size(); i++) {
                index.put(locations.get(i), i);
            }
            UnionFind unionFind = new UnionFind(locations.size());
            List<Edge> selected = new ArrayList<Edge>();
            double total = 0.0;
            for (Edge edge : edges) {
                if (unionFind.union(index.get(edge.from), index.get(edge.to))) {
                    selected.add(edge);
                    total += edge.distance;
                    if (selected.size() == locations.size() - 1) {
                        break;
                    }
                }
            }
            System.out.println("Minimum spanning tree (Kruskal):");
            if (selected.isEmpty()) {
                System.out.println("  No routes to select.");
            } else {
                for (Edge edge : selected) {
                    System.out.println("  " + edge.from + " -- " + edge.to + " : "
                            + format(edge.distance) + " km");
                }
            }
            System.out.println("Total selected distance: " + format(total) + " km");
            if (unionFind.components() == 1) {
                System.out.println("All " + locations.size() + " locations are connected.");
            } else {
                System.out.println("Network has " + unionFind.components()
                        + " disconnected groups; these routes form a minimum spanning forest.");
            }
        }

        private List<Edge> getUniqueEdges() {
            List<String> locations = new ArrayList<String>(adjacency.keySet());
            Map<String, Integer> index = new HashMap<String, Integer>();
            for (int i = 0; i < locations.size(); i++) {
                index.put(locations.get(i), i);
            }
            List<Edge> edges = new ArrayList<Edge>();
            for (String location : locations) {
                for (Edge edge : adjacency.get(location)) {
                    if (index.get(location) < index.get(edge.to)) {
                        edges.add(edge);
                    }
                }
            }
            return edges;
        }
    }

    private static final class Edge {
        private final String from;
        private final String to;
        private final double distance;

        private Edge(String from, String to, double distance) {
            this.from = from;
            this.to = to;
            this.distance = distance;
        }
    }

    private static final class PathNode {
        private final String location;
        private final double distance;

        private PathNode(String location, double distance) {
            this.location = location;
            this.distance = distance;
        }
    }

    private static final class UnionFind {
        private final int[] parent;
        private final int[] rank;
        private int components;

        private UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            components = size;
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        private int find(int item) {
            if (parent[item] != item) {
                parent[item] = find(parent[item]);
            }
            return parent[item];
        }

        private boolean union(int first, int second) {
            int firstRoot = find(first);
            int secondRoot = find(second);
            if (firstRoot == secondRoot) {
                return false;
            }
            if (rank[firstRoot] < rank[secondRoot]) {
                parent[firstRoot] = secondRoot;
            } else if (rank[firstRoot] > rank[secondRoot]) {
                parent[secondRoot] = firstRoot;
            } else {
                parent[secondRoot] = firstRoot;
                rank[firstRoot]++;
            }
            components--;
            return true;
        }

        private int components() {
            return components;
        }
    }
}

