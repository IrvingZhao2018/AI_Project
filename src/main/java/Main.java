import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    /**
     * Static Main Method
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        boolean ucs = false; // If true, search function will use UCS; otherwise it will use A*
        Main function = new Main(); // To call methods which are not static
        String filePath = function.getFilePath(0); // You could change file path in this function

        Map<Integer, Vertex> vertexMap = new HashMap<Integer, Vertex>(); // Storing vertices
        Map<Vertex, Map<Vertex, Integer>> edgeMap = new HashMap<Vertex, Map<Vertex, Integer>>(); // 2-D Map for storing edges
        Map<Vertex, Integer> heuristicMap = new HashMap<Vertex, Integer>() ; // Storing heuristic from Key Vertex to an exact End Vertex


        File file = new File(filePath);
        System.out.println("Reading file:\t" + filePath);
        function.readFile(file, vertexMap, edgeMap);

        System.out.println("Number of vertex : " + vertexMap.keySet().size());
        System.out.println("Edges cover all vertices : " + (edgeMap.keySet().size() == vertexMap.keySet().size()));
        System.out.println("Heuristic function is admissible: " +function.checkHeuristic(vertexMap, edgeMap));


        /** Test Search for all pairs of nodes from 0 to @numOfVertex */
        System.out.println((ucs ? "Search using UCS" : "Search using A*"));
        int numOfVertex  = vertexMap.keySet().size();
        // Testing all pairs among more than 500 vertices will take too much time.
        //numOfVertex = 100; // Set test size manually.
        function.testForMultiplePairs(numOfVertex, vertexMap, edgeMap, heuristicMap, ucs);


        /**
         * Test for one pair: the 1st param should be the start index while 2nd one should be the end.
         * If the last param is true, search by UCS, else by A* .
         * function.shortestPathSearch().print() will print the shortest path.
         */
        int start = 0;
        int end = 99;
        System.out.println("\nOne pair test from " + start + " to " + end);
        heuristicMap = new HashMap<Vertex, Integer>();
        function.heuristicMapInitialization(vertexMap, heuristicMap, end);
        long startTime = System.nanoTime();
        Path resultPath = function.shortestPathSearch(start,end,vertexMap,edgeMap, heuristicMap, ucs);
        System.out.println((System.nanoTime() - startTime) +  "\tns");
        resultPath.print();

    }

    /**
     * Read edge from String and put edge into vertexMap
     * @param line line String of one line of file
     * @param vertexMap
     */
    public void readVertices(String line, Map<Integer, Vertex> vertexMap){
        String[] vertexInfo = line.split(",");
        Integer index = Integer.valueOf(vertexInfo[0]);
        Integer squareX = Integer.valueOf(vertexInfo[1]);
        Integer squareY = Integer.valueOf(vertexInfo[2]);
        Vertex vertex = new Vertex(index, squareX, squareY);
        vertexMap.put(index, vertex);
    }

    /**
     * Read edge from String and put edge into edgeMap
     * @param line String of one line of file
     * @param edgeMap
     */
    public void readEdges(String line, Map<Integer, Vertex> vertexMap, Map<Vertex, Map<Vertex, Integer>> edgeMap){
        String[] edgeInfo = line.split(",");
        Vertex vertex1 = vertexMap.get(Integer.valueOf(edgeInfo[0]));
        Vertex vertex2 = vertexMap.get(Integer.valueOf(edgeInfo[1]));
        Integer distance = Integer.valueOf(edgeInfo[2]);
        if(distance <= 0) System.out.println("Exits non-positive edge from " + vertex1.index + " to " + vertex2.index
                        + ", edge weight:\t" + distance);
        if(edgeMap.containsKey(vertex1)){
            edgeMap.get(vertex1).put(vertex2, distance);
        } else {
            Map<Vertex, Integer> tmpMap = new HashMap<Vertex, Integer>();
            tmpMap.put(vertex2, distance);
            edgeMap.put(vertex1, tmpMap);
        }

        if(edgeMap.containsKey(vertex2)){
            edgeMap.get(vertex2).put(vertex1, distance);
        } else {
            Map<Vertex, Integer> tmpMap = new HashMap<Vertex, Integer>();
            tmpMap.put(vertex1, distance);
            edgeMap.put(vertex2, tmpMap);
        }
    }

    /**
     * Calculate getHeuristic between two vertices
     * @param v1 Vertex1
     * @param v2 Vertex2
     * @return getHeuristic
     */
    public Integer getHeuristic(Vertex v1, Vertex v2){ //Euclidean distance
        Integer width = Math.abs(v1.squareX - v2.squareX);
        Integer height = Math.abs(v1.squareY - v2.squareY);
        Double result = Math.sqrt(
                                (width < 2 ? 0 : Math.pow((width - 1), 2))
                                + (height < 2 ? 0 : Math.pow((height - 1), 2))
                                    ) * 100;
        return (int)Math.floor(result); // Make sure no overestimate when datatype converting
    }

    /**
     * Read File into vertexMap and edgeMap
     * @param file
     * @param vertexMap
     * @param edgeMap
     * @throws IOException
     */
    public void readFile(File file, Map<Integer, Vertex> vertexMap, Map<Vertex, Map<Vertex, Integer>> edgeMap) throws IOException{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            //Read Vertices
            while ((line = br.readLine()) != null && !Character.isDigit(line.charAt(0)));
            do{
                readVertices(line, vertexMap);
            } while ((line = br.readLine()) != null && Character.isDigit(line.charAt(0)));

            //Read Edges
            while ((line = br.readLine()) != null && !Character.isDigit(line.charAt(0)));
            do{
                readEdges(line, vertexMap, edgeMap);
            } while ((line = br.readLine()) != null && Character.isDigit(line.charAt(0)));
    }

    /**
     *  Make sure getHeuristic is never over estimated.
     * @param vertexMap
     * @param edgeMap
     * @return For all vertices : weight >=  edge getHeuristic
     */
    public boolean checkHeuristic(Map<Integer, Vertex> vertexMap, Map<Vertex, Map<Vertex, Integer>> edgeMap){
        for(Vertex v1 : edgeMap.keySet()){
            Map<Vertex, Integer> targetMap = edgeMap.get(v1);
            for ( Vertex v2 : targetMap.keySet()
            ) {
                Integer distance = targetMap.get(v2);
                int heuristic = getHeuristic(v1, v2);
                if(distance < heuristic){
                    System.out.println(v1.index + ", " + v2.index); // The wrong heuristic between vertices(v1 & v2)
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Shortest Path Search
     * @param start start point
     * @param end end point
     * @param vertexMap Map for storing vertex info
     * @param edgeMap Map for storing edge info
     * @param heuristicMap Map for storing heuristic from all points to end point
     * @param ucs true : UCS / false : A*
     * @return
     */
    public Path shortestPathSearch(Integer start, Integer end, Map<Integer, Vertex> vertexMap,
                                   Map<Vertex, Map<Vertex, Integer>> edgeMap, final Map<Vertex, Integer> heuristicMap
                                    ,final boolean ucs){
        Vertex startVertex = vertexMap.get(start);
        Vertex endVertex = vertexMap.get(end);
        Comparator<Path> pathComparator = new Comparator<Path>() {
            public int compare(Path p1, Path p2) {
                int g1 = p1.length;
                int g2 = p2.length;

                // UCS
                if(ucs)
                    return g1 - g2;

                // A*
                Vertex start1 = p1.currentVertex;
                Vertex start2 = p2.currentVertex;
                int h1 = heuristicMap.get(start1);
                int h2 = heuristicMap.get(start2);
                return ((g1 + h1) - (g2 + h2));
            }
        };

        MinimumHeap<Path> queue = new MinimumHeap<Path>(pathComparator); //Storing Path

        //Initialization
        Path p0 = new Path(startVertex, endVertex);
        queue.add(p0);
        Map<Vertex, Path> shortestMap = new HashMap<Vertex, Path>(); // Storing the shortest path to Vertex(Key of this Map)
        shortestMap.put(startVertex, p0);

        Search:
        while(queue.size() > 0 && !queue.peek().currentVertex.index.equals(end)){
            Path currentPath = queue.poll();
            Vertex currentVertex = currentPath.currentVertex;
            Integer currentLength = currentPath.length;
            if(shortestMap.containsKey(currentVertex) ){
                /**
                 Compared to this currentPath  to currentVertex,
                 if there exists another path to currentVertex
                 , shorter or equal, and had been explored before currentPath,
                 this currentVertex will not need to be explored.
                 */
                Path shortestPath = shortestMap.get(currentVertex);
                if(currentLength > shortestPath.length
                        || (currentLength == shortestPath.length && !currentPath.equals(shortestPath))){
                    //System.out.println(heap.size() + " skip explore");
                    continue Search;
                }
            }

            Map<Vertex, Integer> nextStep = edgeMap.get(currentVertex);
            Expand:
            for (Vertex v : nextStep.keySet()
            ) {
                if(shortestMap.containsKey(v)){ // If this extend action will not create a shorter path, don't do it.
                    Path shortestPath = shortestMap.get(v);
                    if(currentLength + nextStep.get(v) >= shortestPath.length){
                        continue Expand;
                    }
                }
                    Path newPath = currentPath.copy();
                    newPath.add(v, edgeMap);
                    queue.add(newPath);
                    shortestMap.put(v, newPath);
                    //System.out.println("heap.size: " +heap.size() + " skip expand");
            }
        }
        if(queue.size() == 0){
            System.out.println("No Path from " + start + " to " + end);
            return new Path();
        }
        return queue.peek();
    }

    /**
     * Return File Path
     * @return
     */
    public String getFilePath(int index){
        //File Path
        List<String> paths = new ArrayList<String>();
        String graph100_520 = "src/main/resources/graphs/graph100_520.txt";
        String graph100_942 = "src/main/resources/graphs/graph100_942.txt";
        String graph100_1472 = "src/main/resources/graphs/graph100_1472.txt";
        String graph100_1962 = "src/main/resources/graphs/graph100_1962.txt";
        String graph200_1988 = "src/main/resources/graphs/graph200_1988.txt";
        String graph200_3942 = "src/main/resources/graphs/graph200_3942.txt";
        String graph200_5985 = "src/main/resources/graphs/graph200_5985.txt";
        String graph200_7943 = "src/main/resources/graphs/graph200_7943.txt";
        String graph500_12340 = "src/main/resources/graphs/graph500_12340.txt";
        String graph500_25071 = "src/main/resources/graphs/graph500_25071.txt";
        String graph500_37369 = "src/main/resources/graphs/graph500_37369.txt";
        String graph500_50052 = "src/main/resources/graphs/graph500_50052.txt";
        String graph1000_50091 = "src/main/resources/graphs/graph1000/graph1000_50091.txt";
        String graph1000_100020 = "src/main/resources/graphs/graph1000/graph1000_100020.txt";
        String graph1000_149851 = "src/main/resources/graphs/graph1000/graph1000_149851.txt";
        String graph1000_199314 = "src/main/resources/graphs/graph1000/graph1000_199314.txt";
        String graph2000_199790 = "src/main/resources/graphs/graph2000/graph2000_199790.txt";
        String graph2000_400096 = "src/main/resources/graphs/graph2000/graph2000_400096.txt";
        String graph2000_599755 = "src/main/resources/graphs/graph2000/graph2000_599755.txt";
        String graph2000_799892 = "src/main/resources/graphs/graph2000/graph2000_799892.txt";

        paths.add(graph100_520);
        paths.add(graph100_942);
        paths.add(graph100_1472);
        paths.add(graph100_1962);
        paths.add(graph200_1988);
        paths.add(graph200_3942);
        paths.add(graph200_5985);
        paths.add(graph200_7943);
        paths.add(graph500_12340);
        paths.add(graph500_25071);
        paths.add(graph500_37369);
        paths.add(graph500_50052);
        paths.add(graph1000_50091);
        paths.add(graph1000_100020);
        paths.add(graph1000_149851);
        paths.add(graph1000_199314);
        paths.add(graph2000_199790);
        paths.add(graph2000_400096);
        paths.add(graph2000_599755);
        paths.add(graph2000_799892);

        return paths.get(index);
        //return  graph2000_199790; // Return file path more clearly.
    }

    /**
     * Test Search for all pairs of nodes from 0 to @numOfVertex
     * @param numOfVertex
     * @param vertexMap Map for storing vertex info
     * @param edgeMap Map for storing edge info
     * @param heuristicMap Map for storing heuristic from all points to end point
     * @param ucs true : UCS / false : A*
     */
    private void testForMultiplePairs(int numOfVertex, Map<Integer, Vertex> vertexMap,
                                      Map<Vertex, Map<Vertex, Integer>> edgeMap,  Map<Vertex, Integer> heuristicMap
                                    ,final boolean ucs) {
        int deepestLength = 0;
        long totalRuntime = 0; //Runtime recording
        int pairsCount = 0; //Record numbers of pairs that are tested
        for (int start = 0; start < numOfVertex; start++) { //
            for (int end = 0; end < numOfVertex; end++) {
                if(end == start) continue; // From one point to itself, the path contains only this point
                heuristicMap = new HashMap<Vertex, Integer>();
                heuristicMapInitialization(vertexMap, heuristicMap, end);
                long startTime = System.nanoTime();
                Path result = shortestPathSearch(start, end, vertexMap, edgeMap, heuristicMap, ucs);
                totalRuntime += (System.nanoTime() - startTime);
                //result.print(); // Print Shortest Path from start to end, if you want.
                deepestLength = Math.max(deepestLength, result.pathList.size());
                pairsCount++;
            }
            System.out.println("Test for StartVertex: " + start + " finished");
        }
        System.out.println("Runtime:\t" + totalRuntime + "\tns");
        System.out.println("Number of Pairs Compared:\t" + pairsCount);
        System.out.println("Deepest length among tested pairs:\t" + deepestLength);
    }

    private void heuristicMapInitialization(Map<Integer,Vertex> vertexMap, Map<Vertex, Integer> heuristicMap, int end) {
        Vertex endVertex = vertexMap.get(end);
        for (Vertex v : vertexMap.values()){
            int heuristic = getHeuristic(v, endVertex);
            heuristicMap.put(v, heuristic);
        }
    }
}
