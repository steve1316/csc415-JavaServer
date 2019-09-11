import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class HTTP_Server implements Runnable
{
    static final File webRoot = new File(".");
    static final String defaultFile = "index.html";
    static final String fileNotFound = "404.html";
    static final String methodNotSupported = "not_supported.html";

    //port to listen connection
    static final int port = 8080;

    //verbose mode
    static final boolean verbose = true;

    //Client connection via Socket class
    private Socket clientSocketConnection;

    public HTTP_Server(Socket myConnection)
    {
        clientSocketConnection = myConnection;
    }

    public static void main(String[] args)
    {
        try
        {
            ServerSocket serverConnection = new ServerSocket(port);
            System.out.println("Server started.\nListening for connections on port: " + port + "...\n");

            //listen until user halts server execution
            while(true)
            {
                HTTP_Server myServer = new HTTP_Server(serverConnection.accept());

                if(verbose)
                {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                //create dedicated thread to manage client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }
        }
        catch(IOException e)
        {
            System.out.println("Server Connection error via port: " + e.getMessage());
        }
    }

    public void run()
    {
        //manage client connection
        BufferedReader input = null;
        PrintWriter output = null;
        BufferedOutputStream dataOutput = null;
        String fileRequested = null;

        try
        {
            //read in string from client
            input = new BufferedReader(new InputStreamReader(clientSocketConnection.getInputStream()));

            //get character output stream to client (for headers)
            output = new PrintWriter(clientSocketConnection.getOutputStream());

            //get binary output stream to client (for requested data)
            dataOutput = new BufferedOutputStream(clientSocketConnection.getOutputStream());

            //get first line of the request from client
            String inputString = input.readLine();

            //we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(inputString);

            //we get the HTTP method of the client
            String method = parse.nextToken().toUpperCase();

            //we get the file requested
            fileRequested = parse.nextToken().toLowerCase();

            //if method does not match GET and HEAD methods
            if(!method.equals("GET") && !method.equals("HEAD"))
            {
                if(verbose)
                {
                    System.out.println(method + " is not supported yet.");
                }

                //return the not supported file to client
                File file = new File(webRoot, methodNotSupported);
                int fileLength = (int) file.length();
                String contentType = "text/html";

                //read content to return to client
                byte[] fileData = readFileData(file, fileLength);

                //we send HTTP headers with data to client
                output.println("HTTP/1.1 501 Not Implemented");
                output.println("Server: Java HTTP Server from Steve Tu : 1.0");
                output.println("Date: " + new Date());
                output.println("Content type: " + contentType);
                output.println("Content length: " + fileLength);
                output.println();
                output.flush();
            }
            else
            {
                //GET and HEAD methods are supported
                if(fileRequested.endsWith("/"));
                {
                    fileRequested += defaultFile;
                }

                File file = new File(webRoot, fileRequested);
                int fileLength = (int) file.length();
                String content = getContentType(fileRequested);

                //GET method to return content
                if(method.equals("GET"))
                {
                    byte[] fileData = readFileData(file, fileLength);

                    //send HTTP headers
                    output.println("HTTP/1.1 200 OK");
                    output.println("Server: Java HTTP Server from Steve Tu : 1.0");
                    output.println("Date: " + new Date());
                    output.println("Content type: " + content);
                    output.println("Content length: " + fileLength);
                    output.println();
                    output.flush();

                    dataOutput.write(fileData, 0, fileLength);
                    dataOutput.flush();
                }

                if(verbose)
                {
                    System.out.println("File " + fileRequested + " of type " + content + " returned");
                }
            }


        }
        catch(FileNotFoundException e)
        {
            try{
                fileNotFound(output, dataOutput, fileRequested);
            }
            catch(IOException ioe)
            {
                System.out.println("Error with file not found exception: " + ioe.getMessage());
            }
        }
        catch(IOException e)
        {
            System.out.println("Server error: " + e.getMessage());
        }
        finally
        {
            try
            {
                input.close();
                output.close();
                dataOutput.close();

                //Close socket connection
                clientSocketConnection.close();
            }
            catch(Exception e)
            {
                System.out.println("Error closing stream: " + e.getMessage());
            }

            if(verbose)
            {
                System.out.println("Connection closed.\n");
            }
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException
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

        return fileData;
    }

    private String getContentType(String fileRequested)
    {
        if(fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
        {
            return "text/html";
        }
        else
        {
            return "text/plain";
        }
    }

    private void fileNotFound(PrintWriter output, OutputStream dataOutput, String fileRequested) throws IOException
    {
        File file = new File(webRoot, fileNotFound);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);

        //send HTTP headers
        output.println("HTTP/1.1 404 File Not Found");
        output.println("Server: Java HTTP Server from Steve Tu : 1.0");
        output.println("Date: " + new Date());
        output.println("Content type: " + content);
        output.println("Content length: " + fileLength);
        output.println();
        output.flush();

        dataOutput.write(fileData, 0, fileLength);
        dataOutput.flush();

        if(verbose)
        {
            System.out.println("File " + fileRequested + " not found");
        }
    }
}
