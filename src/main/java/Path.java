import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Path {
    public List<Vertex> pathList;
    public Integer length;
    public Vertex currentVertex;
    public Vertex endVertex;

    public Path() {
        this.length = -1; // Error result
    }

    public Path(Vertex start, Vertex endVertex) {
        this.pathList = new ArrayList<Vertex>();
        this.pathList.add(start);
        this.length = 0;
        this.currentVertex = start;
        this.endVertex = endVertex;
    }

    public void add(Vertex vertex, Map<Vertex, Map<Vertex, Integer>> edgeMap){
        Integer distance = edgeMap.get(currentVertex).get(vertex);
        this.length += distance;
        this.pathList.add(vertex);
        this.currentVertex = vertex;
    }

    public Path copy(){
        List<Vertex> newpath = new ArrayList<Vertex>();
        for (Vertex v : this.pathList
             ) {
            newpath.add(v);
        }
        Path newPath = new Path();
        newPath.pathList = newpath;
        newPath.length = this.length;
        newPath.currentVertex = this.currentVertex;
        newPath.endVertex = this.endVertex;
        return newPath;
    }

    public void print(){
        if(this.length < 0){
            System.out.println("No result");
            return;
        }
        System.out.print("Path : ");
        int i = 0;
        for (; i < pathList.size() - 1; i++) {
            System.out.print(pathList.get(i).index + ",");
        }
        System.out.print(pathList.get(i).index);
        System.out.println("\tLength :" + length);
    }
}
