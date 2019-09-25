/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.parse4j.Parse;
import org.parse4j.ParseException;
import org.parse4j.ParseFile;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.callback.FindCallback;
import org.parse4j.callback.GetDataCallback;
import org.parse4j.callback.SaveCallback;


/**
 *
 * @author Greg
 */
public class FXMLDocumentController implements Initializable
{
    
    @FXML
    public ListView<String> yourDir;
    @FXML
    public TableView<DownloadMap> serverTable;
    @FXML
    public TableColumn<DownloadMap, String> mapName;
    @FXML
    public TableColumn<DownloadMap, String> author;
    @FXML
    public TableColumn<DownloadMap, String> date;
    @FXML
    public TextField textDirectory;
    @FXML
    public Circle shapeOnline;
    @FXML
    public TextField textUser;
    
    public ObservableList<String> list = FXCollections.observableArrayList();
    public ObservableList<DownloadMap> maps = FXCollections.observableArrayList();
    
    public String mapDir;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        mapDir = System.getProperty("user.home") + "/AppData/Local/Packages/Microsoft.MSDallas_8wekyb3d8bbwe/LocalState/Games/Age of Empires DE/Game Content/Scenarios";;
        
        yourDir.setItems(list);
        
        mapName.setCellValueFactory(new PropertyValueFactory<>("MapName"));
        author.setCellValueFactory(new PropertyValueFactory<>("Author"));
        date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        serverTable.setItems(maps);
        
        
        
        loadDir();
        loadServer();
        
        serverTable.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>()
        {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) 
            {
                try
                {
                if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
                {
                    if(!serverTable.getSelectionModel().getSelectedItem().getMapName().isEmpty())
                    {
                        DownloadMap tmpMap = maps.get(serverTable.getSelectionModel().getFocusedIndex());
                        DownloadMap(tmpMap.getID());
                    }
                    
                    serverTable.getSelectionModel().clearSelection();
                }
                }
                catch(Exception e)
                {
                    
                }
            }
        }
        );
        
        yourDir.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>()
        {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) 
            {
                try
                {
                if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
                {
                    if(!yourDir.getSelectionModel().getSelectedItem().isEmpty())
                    {
                        uploadMap(yourDir.getSelectionModel().getSelectedItem());
                    }
                    
                    yourDir.getSelectionModel().clearSelection();
                }
                }
                catch(Exception e)
                {
                    
                }
            }
        }
        );
        
        textUser.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>()
        {
            @Override
            public void handle(javafx.scene.input.MouseEvent event)
            {
                textUser.setBlendMode(BlendMode.SRC_OVER);
            }
        });
        
        try
        {
            Parse.initialize("4da94d1a6d30b371e3c3d4daf0deff8b8d0b11ce", "1f5d98946f9aa77dbef13ca31163ec25690325bb", "http://18.191.173.178:80/parse");
            shapeOnline.setFill(Color.GREEN);
        }catch(Exception e)
        {
            
        }
    }
    
    public void chooseDir()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(mapDir));
        chooser.setDialogTitle("Find AOEDE Map Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            mapDir = chooser.getSelectedFile().toString();
            textDirectory.setText(mapDir);
            loadDir();
        }
    }
    
    public void loadDir()
    {
        list.clear();
        
        File folder = new File(mapDir);
        File[] listOfFiles = folder.listFiles();
        
        for(int i = 0; i < listOfFiles.length; i++)
        {
            if(listOfFiles[i].isFile())
            {
                String name = listOfFiles[i].getName();
                String[] parts = name.split("\\.");
                
                if(parts.length == 2)
                    if(parts[1].equals("aoescn"))
                        list.add(parts[0]);
            }
            
            textDirectory.setText(mapDir);
        }
    }
    
    public void loadServer()
    {
        maps.clear();
        ParseQuery<ParseObject> query = new ParseQuery<>("Map");
        query.orderByAscending("createdAt");
        
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException pe) {
                if(pe == null && list.size() > 0)
                {
                    for(ParseObject object: list)
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(object.getCreatedAt());
                        String aDate = (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
                        DownloadMap aMap = new DownloadMap(object.getString("mapName"), object.getString("Player"), aDate, object.getString("md5"), object.getObjectId());
                        maps.add(aMap);
                    }
                }
            }
        });
    }
    
    public void uploadMap(String name)
    {
        if(textUser.getText().equals(""))
        {
            textUser.setBlendMode(BlendMode.RED);
        }
        else
        {            
            try{
                File aMap = new java.io.File(mapDir + "/" + name + ".aoescn");

                InputStream is = Files.newInputStream(aMap.toPath());
                String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
                
                System.out.println(md5);

                Boolean run = true;
                for(DownloadMap tmpMap: maps)
                {
                    if(tmpMap.getMD5().equals(md5))
                        run = false;
                }

                if(run)
                {
                    byte[] fileContent = Files.readAllBytes(aMap.toPath());
                    name = name.replaceAll("\\s+", "_");

                    ParseFile parseFile = new ParseFile(name, fileContent);
                    parseFile.save();

                    ParseObject object = new ParseObject("Map");
                    object.put("mapName", name);
                    object.put("theMap", parseFile);
                    object.put("Player", textUser.getText());
                    object.put("md5", md5);

                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException pe) {
                            if(pe == null)
                            {
                                System.out.println("Uploaded");
                                loadServer();
                            }else
                            {
                                System.out.println(pe.toString());
                            }
                        }
                    });
                }else
                {
                    JOptionPane.showMessageDialog(null, "Map Already Exists On Server!");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void DownloadMap(String id)
    {
        ParseQuery query = new ParseQuery("Map");
        query.whereEqualTo("objectId", id);
        query.limit(1);
        
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException pe) {
                for(ParseObject object : list)
                {
                    ParseFile file = object.getParseFile("theMap");
                    
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException pe)
                        {
                            if(pe == null && bytes != null)
                            {
                                String savePath = mapDir + "/" + object.getString("mapName") + ".aoescn";
                                System.out.println(savePath);

                                try(FileOutputStream fos = new FileOutputStream(savePath))
                                {
                                    fos.write(bytes);
                                    System.out.println(savePath);
                                }
                                catch(Exception e)
                                {
                                    //System.out.println("Couldnt Save");
                                }
                                
                                loadDir();
                            }
                        }
                    });
                }
            }
        });
    }
    
}
