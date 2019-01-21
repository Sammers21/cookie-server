package cookie.clicker.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.concurrent.atomic.AtomicLong;

public class AppServerInitializer extends ChannelInitializer<SocketChannel> {

    private final AtomicLong al;

    public AppServerInitializer(AtomicLong al) {
        this.al = al;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new CoockieHandler(al));
    }
}
