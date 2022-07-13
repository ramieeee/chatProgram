import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Server {
    public static AsynchronousChannelGroup channelGroup;
    public static AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    public static List<ClientConnection> connections = new Vector<ClientConnection>();

    public static void main(String[] args) throws Exception{
        System.out.println("서버 실행");
        ServerConnect serverConnect = new ServerConnect();
        serverConnect.connect();
    }
}