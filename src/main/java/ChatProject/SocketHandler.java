package ChatProject;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final ChannelRepository channelRepository;

    private final MessageRepository messageRepository;

    private Map<String, List<WebSocketSession>> sessions = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message){

        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);

        String channelName = (String) session.getAttributes().get("channel_name");

        ChannelService.saveAndSendMessage(  session,
                                            channelName,
                                            value.get("account_id"),
                                            value.get("content"),
                                            channelRepository,
                                            messageRepository);
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception, IOException{
        ChannelService.addSession(session);
    }
}
