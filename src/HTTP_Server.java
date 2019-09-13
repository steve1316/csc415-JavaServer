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

        GET_HEAD methodRequestGETHEAD = null;

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

            methodRequestGETHEAD = new GET_HEAD();

            //if method does not match GET and GET_HEAD methods
            if(!method.equals("GET") && !method.equals("GET_HEAD"))
            {
                if(verbose)
                {
                    System.out.println(method + " is not supported yet.");
                }

                //return the not supported file to client
                File file = new File(webRoot, methodNotSupported);
                int fileLength = (int) file.length();
                methodRequestGETHEAD.setContentType(fileRequested);
                methodRequestGETHEAD.readFileData(file, fileLength);

                methodRequestGETHEAD.notImplementedConfirmation(output, fileLength);
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
                methodRequestGETHEAD.setContentType(fileRequested);

                //GET method to return content
                if(method.equals("GET"))
                {
                    methodRequestGETHEAD.readFileData(file, fileLength);
                    methodRequestGETHEAD.getConfirmation(output, dataOutput, fileLength);
                }

                if(verbose)
                {
                    System.out.println("File " + fileRequested + " of type " + methodRequestGETHEAD.getContentType() + " returned");
                }
            }
        }
        catch(FileNotFoundException e)
        {
            try
            {
                File file = new File(webRoot, fileNotFound);
                int fileLength = (int) file.length();
                methodRequestGETHEAD.readFileData(file, fileLength);
                methodRequestGETHEAD.setContentType(fileRequested);
                methodRequestGETHEAD.fileNotFoundConfirmation(output, dataOutput, fileLength);
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
}
