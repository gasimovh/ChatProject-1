package ChatProject;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException{

        Map<String, String> json = new Gson().fromJson(message.getPayload(), Map.class);

        String channelName = (String) session.getAttributes().get("channel_name");

        /*String accountID = "";

        if(json.get("type").equals("aut") && !(boolean)session.getAttributes().get("authenticated")){
            session.getAttributes().put("authenticated", true);
            //accountID = api > json.get("id");
        }
        else {*/
            channelService.saveAndSendMessage(session, channelName, /*accountID*/ json.get("account_id"), json.get("data"));
        //}
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception, IOException {
        channelService.addSession(session);

        /*RestTemplate rt = new RestTemplate();

        ResponseEntity<List> re = rt.getForEntity("http://localhost:8080/channel/findbyname/"+ (String) session.getAttributes().get("channel_name") + "/messages?history=60", List.class);

        for(Object m : re.getBody()){
            session.sendMessage(new TextMessage(((LinkedHashMap)m).toString()));
        }*/

        ObjectMapper objectMapper = new ObjectMapper();
        for (Message m : channelController
                .listOfMessages((String) session
                        .getAttributes()
                        .get("channel_name"), 60L)) {
            session.sendMessage(new TextMessage(
                    objectMapper.writeValueAsString(m)));
        }
    }
}
