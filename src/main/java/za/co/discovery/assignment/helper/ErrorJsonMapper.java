package za.co.discovery.assignment.helper;

import java.util.Map;

public class ErrorJsonMapper {

    public Integer status;
    public String error;
    public String message;
    public String timeStamp;
    public String trace;

    public ErrorJsonMapper(int status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.error = (String) errorAttributes.get("error");
        this.message = (String) errorAttributes.get("message");
        this.timeStamp = errorAttributes.get("timestamp").toString();
        this.trace = (String) errorAttributes.get("trace");
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getTrace() {
        return trace;
    }
}
