package sample;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SignalFlowGraphSolver {
    private MyGraph myGraph;
    private ArrayList<ArrayList<String>> forwardPaths= new ArrayList<>();
    private ArrayList<ArrayList<String>> loops= new ArrayList<>();
    public SignalFlowGraphSolver(MyGraph myGraph){
        if(myGraph==null) throw new Error("Given graph can't be null");
        this.myGraph=myGraph;
    }
    public float getOverAllTransferFunction(String from , String to){
        int sum = getSumOfMsProductMiniDeltas(from,to);
        int delta = getDelta(from);
        return (float)sum/(float)delta;
    }
    public int getDelta(String node){
        ArrayList<ArrayList<String>> loops = getSimpleLoops(node);
        ArrayList<ArrayList<ArrayList<String>>> nonTouchedLoops = getNonTouchedLoops(node);
        int pathsGain= calculateAllPathGain(loops);
        int unTouchedLoopsGain = calculateNonTouchedPathGain(nonTouchedLoops);
        return 1-pathsGain+unTouchedLoopsGain;
    }
    private int getSumOfMsProductMiniDeltas(String from, String to){
        int ans=0;
        for(ArrayList<String>forwardPath: getForwardPaths(from,to)){
            ans+= calculatePathGain(forwardPath) * calculateDeltaI(forwardPath);
        }
        return ans;
    }
    private int calculateDeltaI(ArrayList<String> forwardPath){
        return 1- getUntouchedLoopsWithThisForwardPath(forwardPath);
    }
    private int getUntouchedLoopsWithThisForwardPath(ArrayList<String> forwardPath){
        ArrayList<ArrayList<String>> ans = new ArrayList<>();
        ArrayList<ArrayList<String>> loops = getSimpleLoops(forwardPath.get(0));
        for (ArrayList<String> loop: loops){
            if(nonTouched(loop,forwardPath)) ans.add(loop);
        }
        return calculateAllPathGain(ans);
    }
    public ArrayList<ArrayList<String>> getForwardPaths(String from, String to){
        forwardPaths.clear();
        fillForwardPaths(from,to);
        return forwardPaths;
    }
    private void fillForwardPaths(String from, String to) {
        HashMap<String,Boolean> isVisited = new HashMap<>();
        for(String n : myGraph.getNodesNames()) isVisited.put(n,false);
        ArrayList<String> pathList = new ArrayList<>();
        pathList.add(from);
        fillForwardPathsUtil(from, to, isVisited, pathList);
    }
    private void fillForwardPathsUtil(String u, String d, HashMap<String,Boolean> isVisited, ArrayList<String> localPathList) {
        HashMap<String, ArrayList<String>> adjList = myGraph.getGraphWithoutWeights();
        isVisited.put(u, true);
        if (u.equals(d)) {
            forwardPaths.add((ArrayList<String>) localPathList.clone());
            isVisited.put(u, false);
            return;
        }
        for (String i : adjList.get(u)) {
            if (!isVisited.get(i)) {
                localPathList.add(i);
                fillForwardPathsUtil(i, d, isVisited, localPathList);
                localPathList.remove(i);
                isVisited.put(i,false);
            }
        }
    }
    private ArrayList<ArrayList<String>> getLoops(String node){
        HashMap<String,Boolean> isVisited = new HashMap<>();
        for(String n : myGraph.getNodesNames()) isVisited.put(n,false);
        loops.clear();
        dfs(myGraph.getGraphWithoutWeights(),node,isVisited,new ArrayList<>());
        return loops;
    }
    private void dfs(HashMap<String,ArrayList<String>> adj, String node, HashMap<String,Boolean> visited, ArrayList<String> loop){
        if(visited.get(node)){
            if(!loop.get(0).equals(node))loop.add(0,node);
            loops.add((ArrayList<String>) loop.clone());
            return;
        }
        visited.put(node,true);

        for (String nei : adj.get(node)) {
            loop.add(nei);
            dfs(adj,nei,visited,loop);
            loop.remove(loop.size()-1);
        }
        visited.put(node,false);
    }
    public ArrayList<ArrayList<String>> getSimpleLoops(String node){
        ArrayList<ArrayList<String>> longLoops = getLoops(node);
        ArrayList<ArrayList<String>> set =new ArrayList<>();
        for (ArrayList<String> arrayList : longLoops) set.add(getTheLoop(arrayList));
        removeDoublicates(set);
        return set;
    }
    private ArrayList<String> getTheLoop(ArrayList<String> longLoop){
        String last = longLoop.get(longLoop.size()-1);
        ArrayList<String> ans = new ArrayList<>();
        ans.add(last);
        for(int i=longLoop.size()-2;i>=0;i--){
            ans.add(longLoop.get(i));
            if (longLoop.get(i).equals(last)) break;
        }
        return reverseArray(ans);
    }
    private ArrayList<String> reverseArray(ArrayList<String> loop){
        ArrayList<String> ans = new ArrayList<>();
        for(int i=loop.size()-1;i>=0;i--){
            ans.add(loop.get(i));
        }
        return ans;
    }
    private void removeDoublicates (ArrayList<ArrayList<String>> loops){
        for(int i=0;i<loops.size();i++){
            for (int j=i+1;j<loops.size();j++){
                if (distinctEquals(loops.get(i), loops.get(j))) loops.remove(j);
            }
        }
    }
    private boolean distinctEquals(ArrayList<String> loop1,ArrayList<String> loop2){
        HashSet<String> a,b;
        a = new HashSet<>(loop1);
        b = new HashSet<>(loop2);
        return a.equals(b);
    }
    public ArrayList<ArrayList<ArrayList<String>> > getNonTouchedLoops(String node){
        ArrayList<ArrayList<String>> simpleLoops = getSimpleLoops(node);
        ArrayList<ArrayList<ArrayList<String>> > nonTouched = new ArrayList<>();
        for(int i=0;i<simpleLoops.size();i++){
            for (int j=i+1;j<simpleLoops.size();j++){
                if (nonTouched(simpleLoops.get(i), simpleLoops.get(j))){
                    ArrayList<ArrayList<String>> temp = new ArrayList<>();
                    temp.add(simpleLoops.get(i));
                    temp.add(simpleLoops.get(j));
                    nonTouched.add(temp);
                }
            }
        }
        return nonTouched;
    }
    private boolean nonTouched(ArrayList<String> loop1,ArrayList<String> loop2){
        for(String s : loop1) {
            if (loop2.contains(s)) return false;
        }
        return true;
    }
    private int calculateAllPathGain(ArrayList<ArrayList<String>> loops){
        int ans =0;
        for(ArrayList<String> path: loops) ans+=calculatePathGain(path);
        return ans;
    }
    private int calculatePathGain(ArrayList<String> path) {
        int ans=1;
        for(int i=0;i<path.size()-1;i++) ans*=calculateEdgeGain(path.get(i),path.get(i+1));
        return ans;
    }
    private int calculateEdgeGain(String from , String to){
        return myGraph.getEdges().get(new Pair<>(from,to));
    }
    private int calculateNonTouchedPathGain(ArrayList<ArrayList<ArrayList<String>>> loops) {
        int ans=0;
        for (ArrayList<ArrayList<String>> dual : loops){
            ans += calculatePathGain(dual.get(0)) * calculatePathGain(dual.get(1));
        }
        return ans;
    }
}
