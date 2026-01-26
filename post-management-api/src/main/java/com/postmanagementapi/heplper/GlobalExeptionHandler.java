package com.postmanagementapi.heplper;

import com.postmanagementapi.heplper.exception.ResourceAlreadyExistsException;
import com.postmanagementapi.heplper.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExeptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception ex){
        System.out.println(ex);
        return ApiResponse.error(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException ex) {
        return  ApiResponse.error(HttpStatus.BAD_REQUEST,ex.getMessage());
    }


    @ExceptionHandler({ResourceNotFoundException.class, ResourceAlreadyExistsException.class})
    public ResponseEntity<?> handleNotFound(Exception ex) {
        return  ApiResponse.error(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    //Xử lý truyền sai dữ liệu --> PathVariable khác kiểu datatype
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
        String errorMessage = String.format("Tham so %s co gia tri %s khong dung dinh dang", ex.getName(),ex.getValue());
        return ApiResponse.error(HttpStatus.BAD_REQUEST,errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        String errors = String.join("; ", errorList);

        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, errors, null, "VALIDATION_ERROR");
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

}
