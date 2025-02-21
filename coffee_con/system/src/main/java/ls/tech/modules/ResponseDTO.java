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

    public static <T> ResponseDTO<T> successNothing(){
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(null);
        return response;
    }
    // 有返回数据的成功情况
    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(00);  // 将成功的code统一为200
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    // 错误返回，失败时code为-1，message为"error"，data包含错误信息
    public static <T> ResponseDTO<T> error(Integer code, String message, T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(code);  // 错误时的code
        response.setMessage("error");  // message为"error"
        response.setData(data);  // 将错误信息放到data中
        return response;
    }
}