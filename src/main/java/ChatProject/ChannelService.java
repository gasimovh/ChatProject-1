package ChatProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                                   String content) throws IOException{
        if(cr.findByName(channelName).getStatus().equals(Status.Open)){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateOfCreation = LocalDateTime.now();
            Message message = new Message(accountId, dtf.format(dateOfCreation), content, cr.findByName(channelName));
            cr.findByName(channelName).addMessage(message);
            mr.save(message);

            for (WebSocketSession webSocketSession : sessions.get(channelName)) {
                webSocketSession.sendMessage(
                        new TextMessage(
                                "{\"type\":\"message\", " +
                                        "\"account_id\":\""+ accountId + "\", " +
                                        "\"data\":\"" + content + " \"}"));
            }
        }
        else {
            session.sendMessage(
                    new TextMessage(
                            "{\"type\":\"error\", " +
                                    "\"account_id\":\"" + accountId + "\", " +
                                    "\"data\":\"Channel is " + cr.findByName(channelName).getStatus() + "!\"}"));
        }
    }

    public Response saveAndSendMessage(String channelName,
                                       String accountId,
                                       String content,
                                       ChannelRepository cr,
                                       MessageRepository mr) throws IOException{
        if((sessions.get(channelName) != null) && (cr.findByName(channelName).getStatus().equals(Status.Open))){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateOfCreation = LocalDateTime.now();
            Message message = new Message(accountId, dtf.format(dateOfCreation), content, cr.findByName(channelName));
            cr.findByName(channelName).addMessage(message);
            mr.save(message);
            for (WebSocketSession webSocketSession : sessions.get(channelName)) {
                webSocketSession.sendMessage(
                        new TextMessage(
                                "{\"type\":\"message\", " +
                                        "\"account_id\":\""+ accountId + "\", " +
                                        "\"data\":\"" + content + " \"}"));
            }
            return new Response(Type.Message, accountId, content);
        }
        else if((sessions.get(channelName) == null) && (cr.findByName(channelName).getStatus().equals(Status.Open))){
            return new Response(Type.Error, accountId, "No sessions!");
        }
        else if((sessions.get(channelName) == null) && !(cr.findByName(channelName).getStatus().equals(Status.Open))){
            return new Response(Type.Error, accountId, "No sessions and channel " + channelName + " is " +
                    cr.findByName(channelName).getStatus() + "!");
        }
        else {
            return new Response(Type.Error, accountId,
                    "Channel is " + cr.findByName(channelName).getStatus() + "!");
        }
    }

    public String addSession(WebSocketSession session){
        if(sessions.containsKey(session.getAttributes().get("channel_name"))){
            sessions.get(session.getAttributes().get("channel_name")).add(session);
        }
        else{
            List<WebSocketSession> sessionList = new ArrayList<>();
            sessionList.add(session);
            sessions.put((String) session.getAttributes().get("channel_name"), sessionList);
            return "Session already exists!";
        }
        return "Session saved!";
    }

}
