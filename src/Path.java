import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Path {
    public List<Vertex> pathList;
    //public String pathList;
    public Integer length;
    public Vertex currentVertex;
    public Vertex endVertex;

    public Path() {
    }

    public Path(Vertex start, Vertex endVertex) {
        this.pathList = new ArrayList<>();
        this.pathList.add(start);
        //this.pathList = "" + start.index;
        this.length = 0;
        this.currentVertex = start;
        this.endVertex = endVertex;
    }

    public void add(Vertex vertex, Map<Vertex, Map<Vertex, Integer>> edgeMap){
        Integer distance = edgeMap.get(currentVertex).get(vertex);
        this.length += distance;
        this.pathList.add(0, vertex);
        this.currentVertex = vertex;
        //this.pathList = vertex.index + "," + this.pathList;
    }

    public Path copy(){
        List<Vertex> newpath = new ArrayList<>();
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
        System.out.print("Path : ");
        int i = pathList.size() - 1;
        for (; i > 0 ; i--) {
            System.out.print(pathList.get(i).index + ",");
        }
        System.out.print(pathList.get(i).index);
        System.out.println("\tLength :" + length);
    }
}
