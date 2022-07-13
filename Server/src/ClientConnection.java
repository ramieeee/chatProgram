import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ClientConnection {
    AsynchronousSocketChannel asynchronousSocketChannel;
    ClientConnection(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;

        ServerReceive serverReceive = new ServerReceive(asynchronousSocketChannel);
        try {
            serverReceive.receive();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String message) {
        Charset charset = Charset.forName("utf-8");
        ByteBuffer byteBuffer = charset.encode(message);
        asynchronousSocketChannel.write(byteBuffer, null,
                new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        System.out.println("발신 실패 객체 삭제");
                        Server.connections.remove(ClientConnection.this);
                    }
                });
    }
}
