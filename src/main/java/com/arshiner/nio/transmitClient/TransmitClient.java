package com.arshiner.nio.transmitClient;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

//import java.io.File;
//import java.io.FileInputStream;
//import org.apache.commons.codec.digest.DigestUtils;
import com.arshiner.common.ClientDTO;
 
public class TransmitClient {
    public void connect(int port, String host, final ClientDTO fileUploadFile,final String ote) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                		ch.pipeline().addLast(new ObjectEncoder());
                		ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                		ch.pipeline().addLast(new FileUploadClientHandler(fileUploadFile));
                		ch.pipeline().addLast(new StringDecoder());
                		ch.pipeline().addLast(new StringEncoder());
                	
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("客户端启动成功");
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
 
    /**
     * @param args
     * @throws Exception
     */
//    public static void main(String[] args) throws Exception {
//        int port = 9090;
//        if (args != null && args.length > 0) {
//            try {
//                port = Integer.valueOf(args[0]);
//            } catch (NumberFormatException e) {
//                // 采用默认值
//            }
//        }
//        ClientDTO uploadFile = new ClientDTO();
////        File file = new File("D:/1.TXT");
//        File file = new File("E:/1127/新建文本文档.txt");
//        String file_md5=DigestUtils.md5Hex(new FileInputStream("E:/1127/新建文本文档.txt")); 
//        uploadFile.setFile(file);
//        uploadFile.setFile_md5(file_md5);
//        uploadFile.setFileClientName(file.getName());
//        uploadFile.setStarPos(110);// 文件开始位置
//        uploadFile.setLength(file.length());
//        uploadFile.setClientLogo("192.168.8.51");
//        uploadFile.setFilestatus("Modified");
//		new TransmitClient().connect(port, "127.0.0.1",uploadFile,"Modified");
//    }

}

