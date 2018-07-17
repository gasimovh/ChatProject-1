package ChatProject;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "message")
public class Message implements Response{

    @Id
    @NotNull
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String messageId;

    private Type type;

    @NotNull
    private String dateOfCreation;

    private String data;

    @NotNull
    private String accountId;

    @JsonIgnore
    @NotNull
    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "channel_id")
    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "parent_id", nullable = false)
    private Channel parent;

    public Message(){}

    public Message(String accountId, String data, Channel parent){
        this.accountId = accountId;
        this.type = Response.Type.MESSAGE;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.dateOfCreation =  dtf.format(LocalDateTime.now());
        this.data = data;
        this.parent = parent;
    }
}
