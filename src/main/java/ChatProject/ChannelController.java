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
    private final MessageRepository messageRepository;
    private final ChannelService channelService;

    @PostMapping("/channel")
    public String createChannel(@RequestParam(value="name") String name,
                                @RequestParam(value="status") Status status){
        if(channelRepository.findByName(name) == null){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime dateOfCreation = LocalDateTime.now();
            channelRepository.save(new Channel(name, status, dtf.format(dateOfCreation)));
            return "Channel " + name + " created successfully!";
        }
        return "Channel already exists!";
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
                           String content) throws IOException {
        return channelService
                .saveAndSendMessage(channel_name,
                                    account_id,
                                    content,
                                    channelRepository,
                                    messageRepository);
    }

    @PatchMapping("/channel/findbyname/{channel_name}")
    public String updateChannelStatus(@PathVariable(value="channel_name") String channel_name, Status status){
        channelRepository.findByName(channel_name).setStatus(status);
        channelRepository.flush();
        return "Channel status changed to " + status + "!";
    }
}
