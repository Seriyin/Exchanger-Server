package app;

import com.google.api.client.http.HttpTransport;

/**
 * Exchange server main class, sets up connections and serves HTTP requests.
 */
public class Server
{
    private HttpTransport con;

    public static void main(String args[]) {
        Server s = new Server();
    }
}
