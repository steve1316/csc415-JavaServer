import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class HTTP_Server implements Runnable
{
    //Default files to send for certain situations.
    static final File webRoot = new File(".");
    static final String defaultFile = "index.html";
    static final String fileNotFound = "404.html";
    static final String methodNotSupported = "not_supported.html";

    //Port for listening.
    static final int port = 8080;

    //Verbose mode to print server statuses to console.
    static final boolean verbose = true;

    //Client connection via Socket class.
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
            System.out.println("Server started. Listening for connections on port: " + port + "...\n");

            //Listen until user halts server execution.
            while(true)
            {
                HTTP_Server myServer = new HTTP_Server(serverConnection.accept());

                if(verbose)
                {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                //Create thread to manage the server.
                Thread thread = new Thread(myServer);
                thread.start();
            }
        }
        catch(IOException e)
        {
            System.out.println("Server connection error via port: " + port);
        }
    }

    public void run()
    {
        //Grab the portion of the request that contains the file name for GET and HEAD.
        String fileRequested = null;

        //input to grab user request, output for headers, and dataOutput for sending files to user.
        BufferedReader input = null;
        PrintWriter output = null;
        BufferedOutputStream dataOutput = null;

        //Create an instance of GET_HEAD object.
        GET_HEAD methodRequestGETHEAD = null;

        try
        {
            //Setup input and output streams.
            input = new BufferedReader(new InputStreamReader(clientSocketConnection.getInputStream()));
            output = new PrintWriter(clientSocketConnection.getOutputStream());
            dataOutput = new BufferedOutputStream(clientSocketConnection.getOutputStream());

            //Get the request from client.
            String inputString = input.readLine();

            //Parse the request with a string tokenizer.
            StringTokenizer parse = new StringTokenizer(inputString);

            //Grab the HTTP method of the client.
            String method = parse.nextToken().toUpperCase();

            //Get the file requested.
            fileRequested = parse.nextToken().toLowerCase();

            methodRequestGETHEAD = new GET_HEAD();

            //if method does not match GET and GET_HEAD methods.
            if(!method.equals("GET") && !method.equals("HEAD"))
            {
                if(verbose)
                {
                    System.out.println(method + " is not supported yet.");
                }

                //Return the not supported file to client.
                File file = new File(webRoot, methodNotSupported);
                int fileLength = (int) file.length();
                methodRequestGETHEAD.setContentType(fileRequested);
                methodRequestGETHEAD.readFileData(file, fileLength);
                methodRequestGETHEAD.notImplementedConfirmation(output, dataOutput, fileLength);
            }
            else
            {
                //GET and HEAD methods are supported.
                if(fileRequested.endsWith("/"));
                {
                    fileRequested += defaultFile;
                }

                File file = new File(webRoot, fileRequested);
                int fileLength = (int) file.length();
                methodRequestGETHEAD.setContentType(fileRequested);
                methodRequestGETHEAD.readFileData(file, fileLength);

                //GET method to return files.
                if(method.equals("GET"))
                {
                    //methodRequestGETHEAD.readFileData(file, fileLength);
                    methodRequestGETHEAD.getConfirmation(output, dataOutput, fileLength);
                }

                //HEAD method to return only the header of the requested file.
                else if(method.equals("HEAD"))
                {
                    //methodRequestGETHEAD.readFileData(file, fileLength);
                    methodRequestGETHEAD.getHEADRequest(output, fileLength);
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
                //Return the File Not Found html file.
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

                //Close the socket connection.
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
