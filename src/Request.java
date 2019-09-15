import java.io.File;
import java.io.IOException;

abstract public class Request
{
    String contentType;
    byte[] fileData;

    public abstract void readFileData(File file, int fileLength) throws IOException;

    public byte[] getFileData()
    {
        return fileData;
    }

    public void setFileData(byte[] fileData)
    {
        this.fileData = fileData;
    }

    public String getContentType()
    {
        return contentType;
    }

    //Sets contentType to the appropriate file type based on the given file name string.
    public void setContentType(String contentType)
    {
        if(contentType.endsWith(".htm") || contentType.endsWith(".html"))
        {
            this.contentType = "text/html";
        }
        else
        {
            this.contentType = "text/plain";
        }
    }
}
