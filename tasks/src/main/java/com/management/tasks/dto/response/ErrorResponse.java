package com.management.tasks.dto.response;


import java.time.LocalDateTime;

public record ErrorResponse (
	 Integer httpErrorCode,
	 String errorName,
	 String errorMessage,
	 String requestPath,
	 LocalDateTime timeStamp
){
}
