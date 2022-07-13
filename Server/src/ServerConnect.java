import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.Executors;

public class ServerConnect {
    public void connect() throws Exception{
        // 채널 그룹 생성
        Server.channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                    Executors.defaultThreadFactory());

        // 서버소켓 생성
        Server.asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(Server.channelGroup);

        // 포트바인딩
        Server.asynchronousServerSocketChannel.bind(new InetSocketAddress(5001));


        // 연결 수락 코드
        Server.asynchronousServerSocketChannel.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Void>() {
                    @Override
                    public void completed(AsynchronousSocketChannel asynchronousSocketChannel,
                                          Void attachment) {
                        // 연결 수락 후 실행 코드
                        System.out.println("연결 성공");
                        ClientConnection cc = new ClientConnection(asynchronousSocketChannel);
                        Server.connections.add(cc);

                        try {
                            Iterator<ClientConnection> iterator = Server.connections.iterator();
                            while(iterator.hasNext()){  // 객체가 있는지 확인
                                ClientConnection clientConnection = iterator.next();
                                if (clientConnection != cc) {
                                    clientConnection.send("낯선 사람이 입장했습니다");
                                }
                            }
                        } catch (ConcurrentModificationException e) {}

                        Server.asynchronousServerSocketChannel.accept(null, this);
                    }
                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        // 연결 수락 실패시 실행
                        System.out.println("연결 실패");
                        try {
                            Server.asynchronousServerSocketChannel.accept(null, this);
                        } catch (Exception e) {
                            System.out.println(e);
                            if (Server.asynchronousServerSocketChannel.isOpen()){
                                try {
                                    Server.asynchronousServerSocketChannel.close();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    }
                });
    }
}