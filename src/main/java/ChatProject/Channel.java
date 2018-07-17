package ChatProject;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@Table(name = "channel")
public class Channel {

    @Id
    @NotNull
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String channelId;

    @NotNull
    private String name;

    @NotNull
    private Status status;

    @NotNull
    private String dateOfCreation;

    private String dateOfClosing;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="parent", cascade = CascadeType.ALL)
    private List<Message> listOfMessages;

    public enum Status{
        DRAFT, OPEN, CLOSED
    }

    public Channel(){}

    public Channel(String name, Status status, String dateOfCreation){
        this.name = name;
        this.status = status;
        this.dateOfCreation = dateOfCreation;
        this.listOfMessages = new LinkedList<>();
    }

    public void addMessage(Message message){
        listOfMessages.add(message);
    }
}
