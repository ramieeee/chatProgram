import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Client {
        // 채널 그룹 생성
    static AsynchronousChannelGroup channelGroup;
    static {
        try {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    Executors.defaultThreadFactory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 서버소켓채널을 채널그룹에 연결
    static AsynchronousSocketChannel asynchronousSocketChannel;
    static {
        try {
            asynchronousSocketChannel = AsynchronousSocketChannel.open(channelGroup);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[]args) throws Exception{
        System.out.print("Type your ID: ");
        Scanner sc = new Scanner(System.in);
        String ID = sc.nextLine();
//"141.144.255.103"
        asynchronousSocketChannel.connect(new InetSocketAddress("localhost", 5001), null,
                new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void attachment) {
                        // 연결 성공 후 코드
                        System.out.println("서버 연결 성공");
                        System.out.println("채팅 나가기: iwanttoquit 입력");
                        receive();
                        send(ID);
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        System.out.println("서버 연결 실패");
                    }
                });
    }
    public static void send(String ID) {
        Charset charset = Charset.forName("utf-8");
        Scanner sc = new Scanner(System.in);
        String message = sc.nextLine();
        if (message.equals("iwanttoquit")) {
            System.out.println("exiting the system");
            try {
                asynchronousSocketChannel.close();
                System.exit(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String data = "["+ID+"]" + message;
        ByteBuffer byteBuffer = charset.encode(data);
        asynchronousSocketChannel.write(byteBuffer, null,
                new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                        Charset charset = Charset.forName("utf-8");
                        Scanner sc = new Scanner(System.in);
                        String message = sc.nextLine();
                        if (message.equals("iwanttoquit")) {
                            System.out.println("exiting the system");
                            try {
                                asynchronousSocketChannel.close();
                                System.exit(0);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        String data = "["+ID+"]" + message;
                        ByteBuffer byteBuffer = charset.encode(data);
                        asynchronousSocketChannel.write(byteBuffer, null, this); // 자신 호출
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        throw new RuntimeException();
                    }
                });
    }

    public static void receive(){
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

                            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                            asynchronousSocketChannel.read(byteBuffer, byteBuffer, this); // 자신 호출
                        } catch (Exception e) {throw new RuntimeException(e);}
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        System.out.println("수신 에러. 채팅 종료");
                        System.exit(1);
                    }
                });
    }
}
