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

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
}
