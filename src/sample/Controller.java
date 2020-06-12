package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller {
    @FXML private TextField inputSolveFrom;
    @FXML private TextField inputSolveTo;
    @FXML private TextField removeNodeEntry;
    @FXML private Button removeNodeButton;
    @FXML private TextField inputNewNode;
    @FXML private Button enterNewNodeButton;
    @FXML private TextField inputEdgeFrom;
    @FXML private TextField inputEdgeTo;
    @FXML private TextField inputEdgeWeight;
    @FXML private Button enterNewEdgeButton;
    @FXML private TextField removeEdgeFrom;
    @FXML private TextField removeEdgeTo;
    @FXML private Button removeEdgeButton;
    @FXML private Button showGraphButton;
    @FXML private Button solveGraphButton;
    @FXML private TextField answer;
    private MyGraph myGraph;
    private Graph guiG;
    private Alert alert = new Alert(Alert.AlertType.ERROR);

    public Controller(){
        myGraph= new MyGraph();
        guiG = new MultiGraph("Graph");
        guiG.addAttribute("ui.antialias"); //this does something...
        guiG.addAttribute("ui.quality"); //this sounds less important...
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        guiG.addAttribute("ui.stylesheet", "url('GUIUtilies/style.css')");

    }
    public void inputEntered(ActionEvent e) {
        {
            if (e.getSource().equals(enterNewNodeButton)) {
                try {
                    handleNewPoint(inputNewNode.getCharacters().toString());
                    inputNewNode.clear();
                }
                catch (Error error){
                    alert.setContentText(error.getMessage());
                    alert.show();
                }
            }
            if (e.getSource().equals(enterNewEdgeButton)) {
                try {
                    handleNewEdge(inputEdgeFrom.getCharacters().toString(), inputEdgeTo.getCharacters().toString(),
                            Integer.parseInt(inputEdgeWeight.getCharacters().toString()));
                    inputEdgeTo.clear();
                    inputEdgeFrom.clear();
                    inputEdgeWeight.clear();
                }
                catch (Exception ign){
                    alert.setContentText("Weight must be integer");
                    alert.show();
                }
                catch (Error error){
                    alert.setContentText(error.getMessage());
                    alert.show();
                }
            }
            if (e.getSource().equals(removeEdgeButton)) {
                try {
                    handleRemoveEdge(removeEdgeFrom.getCharacters().toString(), removeEdgeTo.getCharacters().toString());
                    removeEdgeFrom.clear();
                    removeEdgeTo.clear();
                }
                catch (Error error){
                    alert.setContentText(error.getMessage());
                    alert.show();
                }
            }
            if (e.getSource().equals(removeNodeButton)) {
                try {
                    handleRemoveNode(removeNodeEntry.getCharacters().toString());
                    removeNodeEntry.clear();
                }
                catch (Error error){
                    alert.setContentText(error.getMessage());
                    alert.show();
                }
            }
            if (e.getSource().equals(showGraphButton)) {
                Viewer viewer = guiG.display();
                viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
            }
            if(e.getSource().equals(solveGraphButton)){
                try {
                    float ans= handleSolveButton(inputSolveFrom.getCharacters().toString(),inputSolveTo.getCharacters().toString());
                    answer.clear();
                    answer.appendText(Float.toString(ans));
                    inputSolveFrom.clear();
                    inputSolveTo.clear();
                }
                catch (Error error){
                    alert.setContentText(error.getMessage());
                    alert.show();
                }
                catch (Exception ex){
                    alert.setContentText("The given graph can't be solved");
                    alert.show();
                }
            }
        }
    }
    private void handleRemoveNode(String s) {
        if (s == null || s.length()==0) throw new Error("Point name can't be empty");
        myGraph.removeNode(s);
        guiG.removeNode(s);
    }
    private void handleNewPoint(String s){
        if (s == null || s.length()==0) throw new Error("Point name can't be empty");
        myGraph.addPoint(s);
        guiG.addNode(s).addAttribute("ui.label",s);


    }
    private void handleNewEdge(String from , String to , Integer weight){
        if(from==null || from.length()==0) throw new Error("from point name can't be empty");
        if(to==null || to.length()==0) throw new Error("to point name can't be empty");
        myGraph.addEdge(from,to,weight);

        Edge edge= guiG.getEdge(from+to);
        if(edge == null ) guiG.addEdge(from+to,from,to,true).addAttribute("ui.label",weight);
        else edge.setAttribute("ui.label",
                Integer.parseInt(edge.getAttribute("ui.label").toString())+weight);
    }
    private void handleRemoveEdge(String from, String to){
        if(from==null || from.length()==0) throw new Error("from point name can't be empty");
        if(to==null || to.length()==0) throw new Error("to point name can't be empty");
        myGraph.removeEdge(from,to);
        guiG.removeEdge(from+to);
    }
    private float handleSolveButton(String from, String to){
        if(from==null || from.length()==0) throw new Error("from point name can't be empty");
        if(to==null || to.length()==0) throw new Error("to point name can't be empty");
        if(!myGraph.getNodesNames().contains(from)) throw new Error("from point is not exist in the graph");
        if(!myGraph.getNodesNames().contains(to)) throw new Error("sink point is not exist in the graph");
        SignalFlowGraphSolver solver = new SignalFlowGraphSolver(myGraph);
        String content = null;
        try {
            content = getContent(solver,from,to);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            throw new Error("This graph can't be solved");
        }
        try {
            URL url = getClass().getResource("Report.txt");
            File file = new File(url.getPath());
            FileWriter writer = new FileWriter(file);
            writer.append(content);
            writer.close();
        }
        catch (Exception ex){
            throw new Error("Error while writing in the file");
        }
        handleReportShowing();
        return solver.getOverAllTransferFunction(from,to);
    }
    private String getContent(SignalFlowGraphSolver solver , String from, String to){
        return getForwardPaths(solver, from, to)+
                getLoops(solver, from, to)+getDelta(solver, from, to)+getTF(solver, from, to);
    }
    private String getForwardPaths(SignalFlowGraphSolver solver , String from, String to){
        StringBuilder ans = new StringBuilder("Forward paths are : \n");
        for( ArrayList<String> p : solver.getForwardPaths(from,to)){
            ans.append(p.toString()).append("\n");
        }
        return ans.toString();
    }
    private String getLoops(SignalFlowGraphSolver solver , String from, String to){
        StringBuilder ans = new StringBuilder("Loops are : \n");
        for( ArrayList<String> p : solver.getSimpleLoops(from)){
            ans.append(p.toString()).append("\n");
        }

        return ans.toString();
    }
    private String getDelta(SignalFlowGraphSolver solver , String from, String to){
        return "Delta is : \n" + solver.getDelta(from) + "\n";

    }
    private String getTF(SignalFlowGraphSolver solver , String from, String to){
        return "Over all TF is : \n" + solver.getOverAllTransferFunction(from, to) + "\n";
    }
    private void handleReportShowing(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("reportShower.fxml"));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load(), 400, 600);
            Stage stage = new Stage();
            stage.setTitle("Report");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
}