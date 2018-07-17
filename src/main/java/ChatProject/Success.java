package ChatProject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Success implements Response{
    private String successId;
    private Type type;
    private String dateOfCreation;
    private String data;

    public Success(String data){
        this.successId = UUID.randomUUID().toString();
        this.type = Response.Type.SUCCESS;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.dateOfCreation =  dtf.format(LocalDateTime.now());
        this.data = data;
    }
}
