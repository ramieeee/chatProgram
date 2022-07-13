import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class ServerReceive {
    AsynchronousSocketChannel asynchronousSocketChannel;
    ServerReceive(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    public void receive() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        asynchronousSocketChannel.read(byteBuffer, byteBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        // 받은 데이터 처리
                        try {
                            attachment.flip();
                            Charset charset = Charset.forName("utf-8");
                            String data = charset.decode(attachment).toString();
                            System.out.println(data);

                            for (ClientConnection cc : Server.connections) {
                                cc.send(data);
                            }

                            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                            asynchronousSocketChannel.read(byteBuffer, byteBuffer, this); // 자신 호출
                        } catch (Exception e) {throw new RuntimeException(e);}
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                    }
                });
    }
}
