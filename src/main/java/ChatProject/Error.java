package ChatProject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Error implements Response{
    private String errorId;
    private Type type;
    private String dateOfCreation;
    private String data;

    public Error(String data){
        this.errorId = UUID.randomUUID().toString();
        this.type = Type.ERROR;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.dateOfCreation =  dtf.format(LocalDateTime.now());
        this.data = data;
    }
}
