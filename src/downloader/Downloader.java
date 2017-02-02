package downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javafx.concurrent.Task;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Jamie
 */
public class Downloader {
    
    private boolean downloading;
    
    private FileOutputStream fos;
    
    private URLConnection connection;

    public boolean isDownloading() {
        return downloading;
    }

    public FileOutputStream getFos() {
        return fos;
    }

    public URLConnection getConnection() {
        return connection;
    }
    
    public Task downloadTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    URL dlUrl = new URL("https://dl.dropboxusercontent.com/s/6jgin4fanee08ye/Main.java");
                    URL dlUrl2 = new URL("https://doc-10-9g-docs.googleusercontent.com/docs/securesc/d0frbt6rqcieqhqv1t6ngv6qga0sg1uf/sqhsafibsc2i8f58jqp8s6jqrqe3kv4a/1486044000000/11639743524017494893/11639743524017494893/0BzZn-IVgGfoiOHlWM28xcGQ1SzQ?e=download&nonce=9tknt4uta9klg&user=11639743524017494893&hash=a08rnmc01grfmi5akb84vlbp8hjeq0bj");
                    //URL dlUrl = new URL("https://dl.dropboxusercontent.com/s/oyg9fpcs4tku8x7/protobuf-java-3.2.0rc2.zip");
                    connection = dlUrl.openConnection();
                    URLConnection con2 = dlUrl2.openConnection();
                    System.out.println(connection.getHeaderFields());
                    System.out.println(con2.getHeaderFields());
                    String field = connection.getHeaderField("Content-Disposition");
                    if (field != null && field.contains("filename=")) {
                        String fileName = field.substring(field.indexOf("filename=\"") + 10, field.lastIndexOf("\""));
                        updateMessage(fileName);
                        File file = new File(FileSystemView.getFileSystemView().getHomeDirectory() +"/"+ fileName);
                        ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
                        fos = new FileOutputStream(file);
                        
                        System.out.println("Downloading: "+ fileName);
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                        System.out.println("Download Complete.");
                        
                        downloading = false;
                        fos.close();
                        rbc.close();
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
                downloading = false;
                return true;
            };
        };
    }
        
    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }
    
}
