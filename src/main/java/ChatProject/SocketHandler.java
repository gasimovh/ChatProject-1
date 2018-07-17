package ChatProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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

        Map<String, String> messageJson = new Gson().fromJson(message.getPayload(), Map.class);

        String channelName = (String) session.getAttributes().get("channel_name");

        if(messageJson.get("type").equals("authorization") && !(boolean)session.getAttributes().get("authenticated")){
            String token = messageJson.get("data");
            String url = "";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("authorization", token);
            headers.add("channel_name", channelName);

            HttpEntity entity = new HttpEntity(headers);
            HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if(((ResponseEntity<String>) response).getStatusCode() == HttpStatus.OK){
                session.getAttributes().put("authenticated", true);
                Map<String, String> responseJson = new Gson().fromJson(response.getBody(), Map.class);
                String accountId = responseJson.get("account_id");
                session.getAttributes().put("account_id", accountId);
                session.sendMessage(
                        new TextMessage(new Gson().toJson(new Success("AUTHORIZATION_SUCCESS"))));
            }
            else{
                session.sendMessage(
                        new TextMessage(new Gson().toJson(new Error("AUTHORIZATION_FAILED"))));
            }
        }
        else {
            if((boolean)session.getAttributes().get("authenticated")) {
                channelService.saveAndSendMessage(session,
                        channelName,
                        (String) session.getAttributes().get("account_id"),
                        messageJson.get("data"));
            }
        }
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception, IOException {
        channelService.addSession(session);

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
