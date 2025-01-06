package ls.tech.modules;

import lombok.Data;

/**
 * @program: coffee_con
 * @ClassName: ResponseDTO
 * @author: skl
 * @create: 2025-01-04 09:45
 */
@Data
public class ResponseDTO<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseDTO<T> success(T data){
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    public static <T> ResponseDTO<T> error(Integer code, String message){
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}