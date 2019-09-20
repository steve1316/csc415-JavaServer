import java.net.Socket;

public class WorkerThread implements Runnable
{
    protected Socket clientSocket = null;
    protected String serverString = null;

    public WorkerThread(Socket clientSocket, String serverString)
    {
        this.clientSocket = clientSocket;
        this.serverString = serverString;
    }

    public void run()
    {
        try{
            InputStream
        }
    }
}
