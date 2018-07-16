package ChatProject;

import lombok.Data;

@Data
public class Response {

    private Type type;

    private String account_id;

    private String data;

    public Response(Type type, String account_id, String data){
        this.type = type;
        this.account_id = account_id;
        this.data = data;
    }
}
