package entity;

import java.io.Serializable;

/**
 * @Author: Liuyu
 * @Date: 2018/12/4 0004 20:21
 * @Version 1.0
 * 键盘敲烂,月薪过万
 */
public class Result implements Serializable {
    private Boolean success;
    private String message;

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
