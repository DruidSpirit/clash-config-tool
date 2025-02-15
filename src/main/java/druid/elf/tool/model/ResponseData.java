package druid.elf.tool.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@SuppressWarnings("unused")
@Data
@NoArgsConstructor
public class ResponseData<E> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4872152498902217148L;

    //  响应结果
    private boolean status;

    //  响应状态码
    private Integer statusCode;

    //  响应提示语
    private String message;

    //  响应数据
    private E data;

    private ResponseData(ResponseInterface responseInterface ) {
        this.statusCode = responseInterface.getStatusCode();
        this.message = responseInterface.getName();
    }

    /**
     * 数据响应成功
     * @param  data         响应的具体数据
     * @return responseBody 响应体对象
     */
    public static <E>ResponseData<E> SUCCESS ( E data ) {
        ResponseData<E> responseBody = new ResponseData<>( ResponseDataEnums.RESPONSE_SUCCESS );
        responseBody.setStatus(true);
        responseBody.setData(data);
        return responseBody;
    }

    /**
     * 请求参数响应失败
     * @param data          响应的具体数据
     * @return responseBody 响应体对象
     */
    public static <E>ResponseData<E>  FAILURE ( E data ) {
        ResponseData<E> responseBody = new ResponseData<>( ResponseDataEnums.RESPONSE_FAIL_PARAMS );
        responseBody.setStatus(false);
        responseBody.setData(data);
        return responseBody;
    }

    /**
     * 操作响应成功
     * @param responseInterface 响应状态接口类
     * @return ResponseData     响应体对象
     */
    public static ResponseData<Object> SUCCESS ( ResponseInterface responseInterface ) {
        ResponseData<Object> responseBody = new ResponseData<>(responseInterface);
        responseBody.setStatus(true);
        return responseBody;
    }

    /**
     * 操作响应失败
     * @param responseInterface     响应状态接口类
     * @return ResponseData         响应体对象
     */
    public static ResponseData<Object> FAILURE (  ResponseInterface responseInterface  ) {
        ResponseData<Object> responseBody = new ResponseData<>(responseInterface);
        responseBody.setStatus(false);
        return responseBody;
    }

    /**
     * 数据响应成功(直接返回响应信息)
     * @param  data 响应信息
     * @return responseBody
     */
    public static <E>ResponseData<E> SUCCESSWithResponse ( E data ) {
        ResponseData<E> responseBody = new ResponseData<>( ResponseDataEnums.RESPONSE_SUCCESS );
        responseBody.setStatus(true);
        responseBody.setData(data);
        responseBody.setMessage(data.toString());
        return responseBody;
    }

    /**
     * 请求参数响应失败(直接返回响应信息)
     * @param data  响应信息
     * @return responseBody
     */
    public static <E>ResponseData<E>  FAILUREWithResponse ( E data ) {
        ResponseData<E> responseBody = new ResponseData<>( ResponseDataEnums.RESPONSE_FAIL_PARAMS );
        responseBody.setStatus(false);
        responseBody.setData(data);
        responseBody.setMessage(data.toString());
        return responseBody;
    }
}