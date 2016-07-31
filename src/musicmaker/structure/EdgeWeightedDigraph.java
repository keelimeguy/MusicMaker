package musicmaker.structure;

import java.util.ArrayList;
import java.util.Random;

public class EdgeWeightedDigraph {
   private final int V;
   private int E;
   private ArrayList<DirectedEdge>[] adj;
   private int[] indegree;

   public EdgeWeightedDigraph(int V) {
      if(V < 0){
         System.err.println("Error: Unexpected value ?<V>=\"" + V + "\" in EdgeWeightedDigraph(<V>)" +
            "<V> must be nonnegative");
         System.exit(-1);
      }
      this.V = V;
      this.E = 0;
      this.indegree = new int[V];
      adj = (ArrayList<DirectedEdge>[]) new ArrayList[V];
      for (int v = 0; v < V; v++)
         adj[v] = new ArrayList<DirectedEdge>();
   }

   // randomized graph
   public EdgeWeightedDigraph(int V, int E) {
      this(V);
      if(E < 0){
         System.err.println("Error: Unexpected value ?<E>=\"" + E + "\" in EdgeWeightedDigraph(?<V>=\"" + V + "\", <E>)" +
            "<E> must be nonnegative");
         System.exit(-1);
      }
      for (int i = 0; i < E; i++) {
         Random rand = new Random();
         int v = rand.nextInt(V);
         int w = rand.nextInt(V);
         double weight = rand.nextInt(101) * 0.01;
         DirectedEdge e = new DirectedEdge(v, w, weight, ("" + v + w));
         addEdge(e);
      }
   }

   public EdgeWeightedDigraph(EdgeWeightedDigraph graph) {
      this(graph.V());
      this.E = graph.E();
      for (int v = 0; v < graph.V(); v++)
         this.indegree[v] = graph.indegree(v);
      for (int v = 0; v < graph.V(); v++) {
         for (DirectedEdge e: graph.adj[v])
            adj[v].add(e);
      }
   }

   private class DirectedEdge {
      private final int v;
      private final int w;
      private String val;
      private final double weight;

      public DirectedEdge(int v, int w, double weight, String val) {
         if (v < 0) {
            System.err.println("Error: Unexpected value ?<v>=\"" + v + "\" in EdgeWeightedDigraph.DirectedEdge(...)" +
            "<v> must be nonnegative");
            System.exit(-1);
         }
         if (w < 0) {
            System.err.println("Error: Unexpected value ?<w>=\"" + w + "\" in EdgeWeightedDigraph.DirectedEdge(...)" +
            "<w> must be nonnegative");
            System.exit(-1);
         }
         if (Double.isNaN(weight)) {
            System.err.println("Error: Unexpected value ?<weight>=\"" + weight + "\" in EdgeWeightedDigraph.DirectedEdge(...)");
            System.exit(-1);
         }
         this.v = v;
         this.w = w;
         this.weight = weight;
         this.val = val;
      }

      public int from() { return v; }

      public int to() { return w; }

      public double weight() { return weight; }

      public String value() { return val; }

      public String toString() {
         String str = v + "->" + w + "\t" + String.format("%5.2f", weight);
         if(val != null)
            str += "\t\"" + val + "\"";
         return str;
      }
   }

   public int V() { return V; }

   public int E() { return E; }

   private void validateVertex(int v) {
      if (v < 0 || v >= V){
         System.err.println("Error: Unexpected value ?<v>=\"" + v + "\" in EdgeWeightedDigraph.validateVertex(<v>)" +
            "<v> must be between 0 and " + (V-1));
         System.exit(-1);
      }
   }

   public void addEdge(int from, int to, double weight, String val) {
      DirectedEdge e = new DirectedEdge(from, to, weight, val);
      int v = e.from();
      int w = e.to();
      validateVertex(v);
      validateVertex(w);
      adj[v].add(e);
      indegree[w]++;
      E++;
   }

   public void addEdge(DirectedEdge e) {
      int v = e.from();
      int w = e.to();
      validateVertex(v);
      validateVertex(w);
      adj[v].add(e);
      indegree[w]++;
      E++;
   }

   public ArrayList<DirectedEdge> adj(int v) {
      validateVertex(v);
      return adj[v];
   }

   public int outdegree(int v) {
      validateVertex(v);
      return adj[v].size();
   }

   public int indegree(int v) {
      validateVertex(v);
      return indegree[v];
   }

   public ArrayList<DirectedEdge> edges() {
      ArrayList<DirectedEdge> list = new ArrayList<DirectedEdge>();
      for (int v = 0; v < V; v++)
         for (DirectedEdge e: adj[v]) {
            list.add(e);
         }
      return list;
   }

   public String toString() {
      String str = V + " " + E + "\n";
      for (int v = 0; v < V; v++) {
         str += v + ": ";
         for (DirectedEdge e: adj[v])
            str += e + "\n   ";
         str += "\n";
      }
      return str;
   }
}