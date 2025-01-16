package com.openclassrooms.safetynet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Europe/Paris")
    private Date timeStamp;
    private int httpStatusCode;
    private HttpStatus httpStatus;
    //private String reason;
    private String message;
    private String details;

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message, String details) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        //this.reason = reason;
        this.message = message;
        this.details = details;
    }
}
