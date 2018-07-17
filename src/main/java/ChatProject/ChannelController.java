package ChatProject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChannelController {

    private final ChannelRepository channelRepository;
    private final ChannelService channelService;

    @PostMapping("/channel")
    public Response createChannel(@RequestParam(value="name") String name,
                                @RequestParam(value="status") Channel.Status status){
        if(channelRepository.findByName(name) == null){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateOfCreation = LocalDateTime.now();
            channelRepository.save(new Channel(name, status, dtf.format(dateOfCreation)));
            return new Success(Response.ChannelCreationSuccess);
        }
        return new Error(Response.ChannelCreationFailed);
    }

    @GetMapping("/channel")
    public List<Channel> listOfChannels(){
        return channelRepository.findAll();
    }

    @GetMapping("/channel/findbyname/{channel_name}")
    public Channel findChannelByName(@PathVariable(value="channel_name") String channel_name){
        return channelRepository.findByName(channel_name);
    }

    @GetMapping("/channel/findbyname/{channel_name}/messages")
    public List<Message> listOfMessages(@PathVariable(value="channel_name") String channel_name,
                                        @RequestParam(value="history", defaultValue = "0") Long history){
        if(history != 0){
            List<Message> result = new ArrayList<>();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            for(Message m : channelRepository.findByName(channel_name).getListOfMessages()){
                if(LocalDateTime.parse(m.getDateOfCreation(), dtf).isAfter(LocalDateTime.now().minusMinutes(history))){
                    result.add(m);
                }
            }
            return result;
        }
        return channelRepository.findByName(channel_name).getListOfMessages();
    }

    @PostMapping("/channel/{channel_name}/message")
    public Response addMessage(String channel_name,
                           String account_id,
                           String data) throws IOException {
        return channelService.saveAndSendMessage(channel_name, account_id, data);
    }

    @PatchMapping("/channel/findbyname/{channel_name}")
    public Response updateChannelStatus(@PathVariable(value="channel_name") String channel_name,
                                        Channel.Status status){
        channelRepository.findByName(channel_name).setStatus(status);
        if(status.equals(Channel.Status.CLOSED)){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            channelRepository.findByName(channel_name).setDateOfClosing(dtf.format(LocalDateTime.now()));
        }
        channelRepository.flush();
        return new Success(Response.ChannelStatusChanged);
    }
}
