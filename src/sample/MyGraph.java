package sample;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//hash map has a key of node name (from) and each element point to hashSet of pairs <String (to), weight of edge>
public class MyGraph {
    private HashMap<String,ArrayList<Pair<String,Integer>>> nodes;
    public MyGraph(){
        nodes = new HashMap<>();
    }
    public void addPoint(String name){
        if(nodes.containsKey(name)) throw new Error("There is exist a node with the same name");
        nodes.put(name, new ArrayList<>());
    }
    public void addEdge(String from, String to , Integer weight){
        if(!nodes.containsKey(from)) throw new Error("The given node 'from' name is not exist");
        if(!nodes.containsKey(to)) throw new Error("The given node 'to' name is not exist");
        Pair<String, Integer> p = getThisPair(from, to);
        if(p==null) nodes.get(from).add(new Pair<>(to, weight));
        else {
            p = new Pair<>(p.getKey(),p.getValue()+weight);
            removeEdge(from,to);
            addEdge(from,to,p.getValue());
        }
    }
    public void removeEdge(String from, String to){
        if(!nodes.containsKey(from)) throw new Error("The given node 'from' name is not exist");
        if(!nodes.containsKey(to)) throw new Error("The given node 'to' name is not exist");
        Pair<String,Integer> p = getThisPair(from,to);
        if (p==null) throw new Error("There is no edge from "+from+" to "+to);
        nodes.get(from).remove(p);
    }
    Set<String> getNodesNames(){
        return nodes.keySet();
    }
    public HashMap<Pair<String,String>,Integer> getEdges(){ // return edges as hash map of pairs (from, to) pointing to its weight
        Set<String> nodesNames = getNodesNames();
        HashMap<Pair<String,String>,Integer> pairs= new HashMap<>();
        for (String from : nodesNames){
            for (Pair<String, Integer> to : nodes.get(from)){
                pairs.put(new Pair<>(from,to.getKey()),to.getValue());
            }
        }
        return pairs;
    }
    public HashMap<String,ArrayList<String>> getGraphWithoutWeights(){
        HashMap<String,ArrayList<String>> ans = new HashMap<>();
        for(HashMap.Entry<String,ArrayList<Pair<String,Integer>>> entry: nodes.entrySet()){
            ans.put(entry.getKey(),new ArrayList<>());
            for (Pair<String, Integer> pair : entry.getValue()) ans.get(entry.getKey()).add(pair.getKey());
        }
        return ans;
    }
    public Map<String, ArrayList<Pair<String, Integer>>> getGraph(){
        return nodes;
    }
    public void printGraphInConsole(){
        for (Map.Entry<String, ArrayList<Pair<String, Integer>>> entry : nodes.entrySet()){
            System.out.print(entry.getKey());
            for (Pair<String,Integer> p :entry.getValue()) System.out.print(" >> "+p.getKey()+'('+p.getValue()+')');
            System.out.println();
        }
    }
    public void removeNode(String n){
        if(!nodes.containsKey(n)) throw new Error("The given node name is not exist");
        nodes.remove(n);
        Set<String> set = getNodesNames();
        for (String s :set) {
            Pair<String,Integer> p = getThisPair(s,n);
            if (p!=null) nodes.get(s).remove(p);
        }
    }
    public int getNumOfNodes(){return nodes.size();}
    private Pair<String,Integer> getThisPair(String from, String to){
        ArrayList<Pair<String, Integer>> set = nodes.get(from);
        for (Pair<String,Integer> p : set){
            if (p.getKey().equals(to)) return p;
        }
        return null;
    }
}
