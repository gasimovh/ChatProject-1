package ChatProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ChannelService")
public class ChannelService {

    @Autowired
    private ChannelRepository cr;
    @Autowired
    private MessageRepository mr;

    private Map<String, List<WebSocketSession>> sessions = new HashMap<>();

    public void saveAndSendMessage(WebSocketSession session,
                                   String channelName,
                                   String accountId,
                                   String data) throws IOException{
        if(cr.findByName(channelName).getStatus().equals(Status.OPEN)){
            ObjectMapper objectMapper = new ObjectMapper();
            Message message = new Message(accountId, data, cr.findByName(channelName));
            cr.findByName(channelName).addMessage(message);
            mr.save(message);

            for (WebSocketSession webSocketSession : sessions.get(channelName)) {
                webSocketSession.sendMessage(
                        new TextMessage(objectMapper.writeValueAsString(message)));
            }
        }
        else {
            session.sendMessage(
                    new TextMessage( new Gson()
                            .toJson(new Error("CHANNEL_" +cr.findByName(channelName).getStatus()))));
        }
    }

    public Response saveAndSendMessage(String channelName,
                                       String accountId,
                                       String data) throws IOException{
        if((sessions.get(channelName) != null) && (cr.findByName(channelName).getStatus().equals(Status.OPEN))){
            ObjectMapper objectMapper = new ObjectMapper();
            Message message = new Message(accountId, data, cr.findByName(channelName));
            cr.findByName(channelName).addMessage(message);
            mr.save(message);
            for (WebSocketSession webSocketSession : sessions.get(channelName)) {
                webSocketSession.sendMessage(
                        new TextMessage(objectMapper.writeValueAsString(message)));
            }
            return message;
        }
        else if((sessions.get(channelName) == null) && (cr.findByName(channelName).getStatus().equals(Status.OPEN))){
            return new Error("NO_SESSION");
        }
        else if((sessions.get(channelName) == null) && !(cr.findByName(channelName).getStatus().equals(Status.OPEN))){
            return new Error("NO_SESSION_CHANNEL_" + cr.findByName(channelName).getStatus());
        }
        else {
            return new Error("CHANNEL_" + cr.findByName(channelName).getStatus());
        }
    }

    public void addSession(WebSocketSession session){
        if(sessions.containsKey(session.getAttributes().get("channel_name"))){
            sessions.get(session.getAttributes().get("channel_name")).add(session);
        }
        else{
            List<WebSocketSession> sessionList = new ArrayList<>();
            sessionList.add(session);
            sessions.put((String) session.getAttributes().get("channel_name"), sessionList);
        }
    }

}
