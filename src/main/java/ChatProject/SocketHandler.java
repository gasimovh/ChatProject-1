package ChatProject;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Map;

@Component
@Configurable
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final ChannelController channelController;
    private final ChannelService channelService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message){

        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);

        String channelName = (String) session.getAttributes().get("channel_name");

        channelService.saveAndSendMessage(  session,
                                            channelName,
                                            value.get("account_id"),
                                            value.get("content"));
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception, IOException{
        channelService.addSession(session);

        /*RestTemplate rt = new RestTemplate();

        ResponseEntity<List> re = rt.getForEntity("http://localhost:8080/channel/findbyname/"+ (String) session.getAttributes().get("channel_name") + "/messages?history=60", List.class);

        for(Object m : re.getBody()){
            session.sendMessage(new TextMessage(((LinkedHashMap)m).toString()));
        }*/

        for(Message m : channelController.listOfMessages((String)session.getAttributes().get("channel_name"), 60L)){
            session.sendMessage(new TextMessage("{ \"type\":\"message\", " +
                                                        "\"account_id\":\""+ m.getAccountId() + "\", " +
                                                        "\"data\":\"" + m.getContent() + " \"}"));
        }
    }
}
