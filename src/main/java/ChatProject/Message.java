package ChatProject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @NotNull
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String messageId;

    @NotNull
    private String dateOfCreation;

    private String content;

    @NotNull
    private String accountId;;

    @JsonIgnore
    @NotNull
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Channel parent;

    public Message(){};

    public Message(String accountId, String dateOfCreation, String content, Channel parent){
        this.accountId = accountId;
        this.dateOfCreation = dateOfCreation;
        this.content = content;
        this.parent = parent;
    }
}
