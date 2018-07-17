package ChatProject;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import java.util.Map;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChannelController cc;
    private final ChannelService cs;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(cc, cs), "/chat/*")
                .addInterceptors(new HttpSessionHandshakeInterceptor(){

                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request,
                                                   ServerHttpResponse response,
                                                   WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) throws Exception {

                        String path = request.getURI().getPath();
                        String channelName = path.substring(path.lastIndexOf('/') + 1);

                        attributes.put("channel_name", channelName);
                        attributes.put("authenticated", false);

                        boolean b = super.beforeHandshake(request, response, wsHandler, attributes);
                        return b;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request,
                                               ServerHttpResponse response,
                                               WebSocketHandler wsHandler,
                                               @Nullable Exception ex) {

                        super.afterHandshake(request, response, wsHandler, ex);
                    }
                });
    }
}