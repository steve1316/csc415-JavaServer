import java.io.*;
import java.util.Date;

public class GET_HEAD_PUT extends Request
{
    public void readFileData(File file, int fileLength) throws IOException
    {
        FileInputStream fileInput = null;
        byte[] fileData = new byte[fileLength];

        //Read in the file and save it to fileData as a array of bytes. If not, close the FileInputStream.
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

    public void notImplementedConfirmation(PrintWriter output, BufferedOutputStream dataOutput, int fileLength) throws IOException
    {
        //Send HTTP headers.
        output.println();
        output.println("HTTP/1.0 501 Not Implemented");
        output.println("Server: Java HTTP Server from Steve Tu");
        output.println("Date: " + new Date());
        output.println("Content type: " + contentType);
        output.println("Content length: " + fileLength);
        output.println();
        output.flush();

        dataOutput.write(fileData, 0, fileLength);
        dataOutput.flush();
    }

    public void getHEADRequest(PrintWriter output, int fileLength)
    {
        //Send HTTP headers.
        output.println();
        output.println("HTTP/1.0 200 OK");
        output.println("Server: Java HTTP Server from Steve Tu");
        output.println("Date: " + new Date());
        output.println("Content type: " + contentType);
        output.println("Content length: " + fileLength);
        output.println();
        output.flush();
    }

    public void getConfirmation(PrintWriter output, BufferedOutputStream dataOutput, int fileLength) throws IOException
    {
        //Send HTTP headers.
        output.println();
        output.println("HTTP/1.0 200 OK");
        output.println("Server: Java HTTP Server from Steve Tu");
        output.println("Date: " + new Date());
        output.println("Content type: " + contentType);
        output.println("Content length: " + fileLength);
        output.println();
        output.flush();

        dataOutput.write(fileData, 0, fileLength);
        dataOutput.flush();
    }

    public void putConfirmation(PrintWriter output, BufferedOutputStream dataOutput, int fileLength, String fileBody) throws IOException
    {
        //Send HTTP headers.
        output.println();
        output.println("HTTP/1.0 200 OK");
        output.println("Server: Java HTTP Server from Steve Tu");
        output.println("Date: " + new Date());
        output.println("Content type: " + contentType);
        output.println("Content length: " + fileLength);
        output.println("File Body: " + fileBody);
        output.println();
        output.flush();

        //Write to server first.
        dataOutput.write(fileData, 0, fileLength);
        dataOutput.flush();
    }

    public void fileNotFoundConfirmation(PrintWriter output, BufferedOutputStream dataOutput, int fileLength) throws IOException
    {
        //send HTTP headers
        output.println();
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
