package cookie.clicker.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class CoockieHandler extends SimpleChannelInboundHandler<Object> {

    private final AtomicLong al;
    private ScheduledFuture<?> f;

    public CoockieHandler(AtomicLong al) {
        this.al = al;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (f != null) {
            f.cancel(true);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof DefaultHttpRequest) {
            DefaultHttpRequest request = (DefaultHttpRequest) msg;
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            String path = queryStringDecoder.path();
            if (path.equals("/increment")) {
                end("OK " + al.incrementAndGet(), ctx);
            } else if (path.equals("/stream")) {
                DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, OK);
                response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
                response.headers().add(HttpHeaderNames.TRANSFER_ENCODING, "chunked");
                ctx.writeAndFlush(response);
                EventExecutor executor = ctx.executor();
                this.f = executor.scheduleAtFixedRate(() -> {
                    if (ctx.channel().isActive()) {
                        ctx.writeAndFlush(new DefaultHttpContent(Unpooled.copiedBuffer(al.get() + "\r\n", CharsetUtil.UTF_8)));
                    }
                }, 0, 100, TimeUnit.MILLISECONDS);

            }
        }
    }

    private void end(String responseBody, ChannelHandlerContext ctx) {
        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        OK,
                        Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8)
                );
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, responseBody.length());
        ctx.writeAndFlush(response);
    }
}
