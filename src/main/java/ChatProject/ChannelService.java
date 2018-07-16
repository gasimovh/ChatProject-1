package ChatProject;

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

    private static Map<String, List<WebSocketSession>> sessions = new HashMap<>();

    public static void saveAndSendMessage(WebSocketSession session,
                                          String channelName,
                                          String accountId,
                                          String content,
                                          ChannelRepository cr,
                                          MessageRepository mr){
        if(cr.findByName(channelName).getStatus().equals(Status.Open)){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateOfCreation = LocalDateTime.now();
            Message message = new Message(accountId, dtf.format(dateOfCreation), content, cr.findByName(channelName));
            cr.findByName(channelName).addMessage(message);
            mr.save(message);

            for (WebSocketSession webSocketSession : sessions.get(channelName)) {
                try {
                    webSocketSession.sendMessage(
                            new TextMessage(
                                    "{ \"type\":\"message\", " +
                                            "\"account_id\":\""+ accountId + "\", " +
                                            "\"data\":\"" + content + " \"}"));
                }
                catch(IOException e){}
            }
        }
        else {
            try {
                session.sendMessage(
                        new TextMessage(
                                "{ \"type\":\"error\", " +
                                        "\"account_id\":\"" + accountId + "\", " +
                                        "\"data\":\"ERROR\"}"));
            }
            catch (IOException e) {}
        }
    }

    public static Response saveAndSendMessage(String channelName,
                                              String accountId,
                                              String content,
                                              ChannelRepository cr,
                                              MessageRepository mr){
        if(cr.findByName(channelName).getStatus().equals(Status.Open)){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateOfCreation = LocalDateTime.now();
            Message message = new Message(accountId, dtf.format(dateOfCreation), content, cr.findByName(channelName));
            cr.findByName(channelName).addMessage(message);
            mr.save(message);

            return new Response(Type.Message, accountId, content);
        }
        return new Response(Type.Error, accountId, "ERROR");
    }

    public static String addSession(WebSocketSession session){
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
