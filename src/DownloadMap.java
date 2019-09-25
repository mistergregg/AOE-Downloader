
import javafx.beans.property.SimpleStringProperty;


public class DownloadMap 
{
    private SimpleStringProperty mapName;
    private SimpleStringProperty author;
    private SimpleStringProperty date;
    private SimpleStringProperty md5;
    private SimpleStringProperty id;
    
    public DownloadMap(String mapName, String author, String date, String md5, String id)
    {
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.mapName = new SimpleStringProperty(mapName);
        this.md5 = new SimpleStringProperty(md5);
        this.id = new SimpleStringProperty(id);
    }
    
    public String getMapName()
    {
        return mapName.get();
    }
    
    public String getAuthor()
    {
        return author.get();
    }
    
    public String getDate()
    {
        return date.get();
    }
    
    public String getMD5()
    {
        return md5.get();
    }
    
    public String getID()
    {
        return id.get();
    }

}
