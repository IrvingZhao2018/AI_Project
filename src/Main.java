import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
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

        if(edgeMap.containsKey(vertex1)){
            edgeMap.get(vertex1).put(vertex2, distance);
        } else {
            Map<Vertex, Integer> tmpMap = new HashMap<>();
            tmpMap.put(vertex2, distance);
            edgeMap.put(vertex1, tmpMap);
        }

        if(edgeMap.containsKey(vertex2)){
            edgeMap.get(vertex2).put(vertex1, distance);
        } else {
            Map<Vertex, Integer> tmpMap = new HashMap<>();
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
    public Double getHeuristic(Vertex v1, Vertex v2){
        Integer width = Math.abs(v1.squareX - v2.squareX);
        Integer height = Math.abs(v1.squareY - v2.squareY);
        Double result = Math.sqrt(Math.pow(width == 0 ? 0 : width - 1,2)
                                + Math.pow(height == 0 ? 0 : height - 1, 2));
        return result;
    }

    /**
     * Read File into vertexMap and edgeMap
     * @param file
     * @param vertexMap
     * @param edgeMap
     * @throws IOException
     */
    public void readFile(File file, Map<Integer, Vertex> vertexMap, Map<Vertex, Map<Vertex, Integer>> edgeMap) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
    }

    /**
     *  Make sure getHeuristic is never over estimated.
     * @param vertexMap
     * @param edgeMap
     * @return For all vertices : weight >=  edge getHeuristic
     */
    public boolean checkHeuristic(Map<Integer, Vertex> vertexMap, Map<Vertex, Map<Vertex, Integer>> edgeMap){
        for(Vertex it : edgeMap.keySet()){
            Vertex v1 = it;
            Map<Vertex, Integer> targetMap = edgeMap.get(it);
            for ( Vertex v2 : targetMap.keySet()
            ) {
                Integer distance = targetMap.get(v2);
                Double heuritstic = getHeuristic(v1, v2);
                if(distance < heuritstic){
                    System.out.println(v1.index + ", " + v2.index); // The wrong pair of vertices
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check whether the graph is connected
     * @param vertexMap
     * @param edgeMap
     * @return
     */
    public boolean checkConnected(Map<Integer, Vertex> vertexMap, Map<Vertex, Map<Vertex, Integer>> edgeMap){

        return true;
    }

    /**
     *
     * @param start
     * @param end
     * @param vertexMap
     * @param edgeMap
     * @param ucs true: USC / flase : A*
     * @return
     */
    public Path shortestPathSearch(Integer start, Integer end, Map<Integer, Vertex> vertexMap,
                                   Map<Vertex, Map<Vertex, Integer>> edgeMap, boolean ucs){
        Vertex startVertex = vertexMap.get(start);
        Vertex endVertex = vertexMap.get(end);

        Queue<Path> queue = new PriorityQueue<Path>((Path p1, Path p2)->{
            int g1 = p1.length;
            int g2 = p2.length;
            if(ucs)
                return g1 - g2;

            // A*
            Vertex start1 = p1.pathList.get(0);
            Vertex start2 = p2.pathList.get(0);
            double h1 = getHeuristic(start1, endVertex);
            double h2 = getHeuristic(start2, endVertex);
            return (int)((g1 + h1) - (g2 + h2));
        });


        Path p0 = new Path(startVertex, endVertex);
        queue.add(p0);

        while(queue.peek().pathList.get(0).index != end){
            Path origin = queue.poll();
            Set<Vertex> originpathSet = new HashSet<>(origin.pathList);
            Vertex currentVertex = origin.pathList.get(0);
            Map<Vertex, Integer> nextStep = edgeMap.get(currentVertex);
            for (Vertex v : nextStep.keySet()
            ) {
                if(!originpathSet.contains(v)){
                    Path newPath = origin.copy();
                    newPath.add(v, edgeMap);
                    queue.add(newPath);
                }
            }
        }
        return queue.peek();
    }

    /**
     * Static Main Method
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Main function = new Main(); // To call methods which are not static
        Map<Integer, Vertex> vertexMap = new HashMap<>(); // Storing vertices
        Map<Vertex, Map<Vertex, Integer>> edgeMap = new HashMap<>(); // 2-D Map for storing edges
        String graph100_520 = "src/graphs/graph100_520.txt";
        String graph100_942 = "src/graphs/graph100_942.txt";
        String graph100_1472 = "src/graphs/graph100_1472.txt";
        String graph100_1962 = "src/graphs/graph100_1962.txt";
        String graph200_1988 = "src/graphs/graph200_1988.txt";
        String graph200_3942 = "src/graphs/graph200_3942.txt";
        String graph200_5985 = "src/graphs/graph200_5985.txt";
        String graph200_7943 = "src/graphs/graph200_7943.txt";
        String graph500_12340 = "src/graphs/graph500_12340.txt";
        String graph500_25071 = "src/graphs/graph500_25071.txt";
        String graph500_37369 = "src/graphs/graph500_37369.txt";
        String graph500_50052 = "src/graphs/graph500_50052.txt";
        String graph1000_50091 = "src/graphs/graph1000/graph1000_50091.txt";
        String graph1000_100020 = "src/graphs/graph1000/graph1000_100020.txt";
        String graph1000_149851 = "src/graphs/graph1000/graph1000_149851.txt";
        String graph1000_199314 = "src/graphs/graph1000/graph1000_199314.txt";
        String graph2000_199790 = "src/graphs/graph2000/graph2000_199790.txt";
        String graph2000_400096 = "src/graphs/graph2000/graph2000_400096.txt";
        String graph2000_599755 = "src/graphs/graph2000/graph2000_599755.txt";
        String graph2000_799892 = "src/graphs/graph2000/graph2000_799892.txt";

        File file = new File(graph100_520);
        function.readFile(file, vertexMap, edgeMap);


        //print Vertices
//        for (Integer it : vertexMap.keySet()){
//            Vertex tmp = vertexMap.get(it);
//            System.out.println("vertex" + it + ":" + tmp.squareX + "," + tmp.squareY);
//        }
        System.out.println("Number of edges : " + edgeMap.keySet().size());

        System.out.println("Edges contains right number of vertices : " + (edgeMap.keySet().size() == vertexMap.keySet().size()));

        // If edge weight >=  edge getHeuristic, return true
        // , which means that Heuristic is not over estimated.
        System.out.println("checkHeuristic : " +function.checkHeuristic(vertexMap, edgeMap));

        long startTime = System.nanoTime();
        //Test UCS
//        for (int start = 0; start < 99; start++) { //
//            for (int end = 0; end < 99; end++) {
//                if(end == start) continue;
//                Path result = function.shortestPathSearch(start, end, vertexMap, edgeMap, true);
//                //result.print();
//            }
//        }
        function.shortestPathSearch(1, 0, vertexMap, edgeMap, true).print();
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);




    }
}
