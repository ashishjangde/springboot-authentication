package com.ashish.authandsessionmanagment.advices;

import com.ashish.authandsessionmanagment.exceptions.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {


   public ResponseEntity<ApiResponse<ApiError>> handelReturnStatement(ApiError apiError) {
      return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
   }

   @ExceptionHandler(ResourceNotFoundException.class)
   public ResponseEntity<ApiResponse<ApiError>> handleResourceNotFoundException(ResourceNotFoundException e) {
      ApiError apiError = ApiError.builder()
              .status(HttpStatus.NOT_FOUND)
              .message(e.getMessage())
              .build();
      return handelReturnStatement(apiError);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ApiResponse<ApiError>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
      List<Map<String, String>> errors = e.getBindingResult()
              .getFieldErrors()
              .stream()
              .map(fieldError -> {
                 Map<String, String> errorMap = new HashMap<>();
                 errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                 return errorMap;
              })
              .collect(Collectors.toList());

      ApiError apiError = ApiError.builder()
              .status(HttpStatus.BAD_REQUEST)
              .message("Input Validation Failed")
              .subError(errors)
              .build();

      return handelReturnStatement(apiError);
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<ApiResponse<ApiError>> handelIllegalArgumentException(IllegalArgumentException e){
       ApiError apiError = ApiError.builder()
               .status(HttpStatus.NOT_ACCEPTABLE)
               .message(e.getMessage())
               .build();
       return handelReturnStatement(apiError);
   }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<ApiError>> handelBadCredentialsException(BadCredentialsException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_ACCEPTABLE)
                .message(e.getMessage())
                .build();
        return handelReturnStatement(apiError);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ApiError>> handelRuntimeException(RuntimeException e){
       ApiError apiError = ApiError.builder()
               .status(HttpStatus.BAD_REQUEST)
               .message(e.getMessage())
               .build();
       return handelReturnStatement(apiError);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<ApiError>> handelRequestMethodNotSupported(HttpRequestMethodNotSupportedException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .message(e.getMessage())
                .build();
        return handelReturnStatement(apiError);
    }


   @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleNoResourceFoundException(NoResourceFoundException e) {
       String route = e.getMessage().replace("No static resource", "").trim();

       ApiError apiError = ApiError.builder()
               .status(HttpStatus.NOT_FOUND)
               .message(String.format("The requested route '%s' does not exist.", route))
               .subError(null) // Add sub-error details if needed
               .build();

        return handelReturnStatement(apiError);
    }
}
