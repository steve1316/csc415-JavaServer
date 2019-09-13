import java.io.*;
import java.util.Date;

public class GET_HEAD extends Request
{
    public void readFileData(File file, int fileLength) throws IOException
    {
        FileInputStream fileInput = null;
        byte[] fileData = new byte[fileLength];

        try
        {
            fileInput = new FileInputStream(file);
            fileInput.read(fileData);
        }
        finally
        {
            if(fileInput != null)
            {
                fileInput.close();
            }
        }

        this.fileData = fileData;
    }

    public void notImplementedConfirmation(PrintWriter output, int fileLength)
    {
        //send HTTP headers
        output.println("HTTP/1.0 501 Not Implemented");
        output.println("Server: Java HTTP Server from Steve Tu : 1.0");
        output.println("Date: " + new Date());
        output.println("Content type: " + contentType);
        output.println("Content length: " + fileLength);
        output.println();
        output.flush();
    }

    public void getConfirmation(PrintWriter output, BufferedOutputStream dataOutput, int fileLength) throws IOException
    {
        //send HTTP headers
        output.println("HTTP/1.0 200 OK");
        output.println("Server: Java HTTP Server from Steve Tu : 1.0");
        output.println("Date: " + new Date());
        output.println("Content type: " + contentType);
        output.println("Content length: " + fileLength);
        output.println();
        output.flush();

        dataOutput.write(fileData, 0, fileLength);
        dataOutput.flush();
    }

    public void fileNotFoundConfirmation(PrintWriter output, BufferedOutputStream dataOutput, int fileLength) throws IOException
    {
        //send HTTP headers
        output.println("HTTP/1.0 404 File Not Found");
        output.println("Server: Java HTTP Server from Steve Tu : 1.0");
        output.println("Date: " + new Date());
        output.println("Content type: " + contentType);
        output.println("Content length: " + fileLength);
        output.println();
        output.flush();

        dataOutput.write(fileData, 0, fileLength);
        dataOutput.flush();
    }
}
