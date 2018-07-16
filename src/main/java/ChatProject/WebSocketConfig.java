package ChatProject;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChannelRepository cr;
    private final MessageRepository mr;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SocketHandler(cr, mr), "/chat/*")
                .addInterceptors(new HttpSessionHandshakeInterceptor(){

                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request,
                                                   ServerHttpResponse response,
                                                   WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) throws Exception {

                        String path = request.getURI().getPath();
                        String channelName = path.substring(path.lastIndexOf('/') + 1);

                        attributes.put("channel_name", channelName);

                        boolean b = super.beforeHandshake(request, response, wsHandler, attributes); //&&
                                //((UsernamePasswordAuthenticationToken) request.getPrincipal()).isAuthenticated();
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
        //registry.addHandler(new SocketHandler(cr, mr), "/channel/*/message")
          /*      .addInterceptors(new HttpSessionHandshakeInterceptor(){

            @Override
            public boolean beforeHandshake(ServerHttpRequest request,
                                           ServerHttpResponse response,
                                           WebSocketHandler wsHandler,
                                           Map<String, Object> attributes) throws Exception {

                String path = request.getURI().getPath();
                String channelName = path.substring(path.lastIndexOf('/') + 1);

                attributes.put("channel_name", channelName);

                System.out.println(request.getMethod());

                boolean b = super.beforeHandshake(request, response, wsHandler, attributes); //&&
                //((UsernamePasswordAuthenticationToken) request.getPrincipal()).isAuthenticated();
                return b;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       @Nullable Exception ex) {

                super.afterHandshake(request, response, wsHandler, ex);
            }
        });*/
    }
}