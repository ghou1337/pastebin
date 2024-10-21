package pl.pastebin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pastebin.exe.*;
import pl.pastebin.exe.response.GlobalErrorResponse;
import pl.pastebin.model.Data;
import pl.pastebin.model.DataResponse;
import pl.pastebin.services.MetadataService;
import pl.pastebin.services.GoogleCloudStorageService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SharingController {
    private final GoogleCloudStorageService googleCloudStorageService;
    private final MetadataService metadataService;

    @PostMapping("/post")
    public Object postMessage(@RequestBody Data data) {
        if(data.getExpressionDate() > 60 || data.getExpressionDate() <= 0){
            throw new InvalidDateException();
        }
        if(data.getText().isBlank() || data.getText().length() < 3) {
            throw new InvalidTextException();
        }
        googleCloudStorageService.uploadFile(data);
        return data;
    }

    @GetMapping("/{hash}")
    private DataResponse getTextByHash(@PathVariable String hash) {
        DataResponse response = new DataResponse();
            response.setText(googleCloudStorageService.getFile(hash));
            response.setCreatedAt(metadataService.getCreatedAt(hash));
            response.setExpressionDate(metadataService.getExpressionDate(hash));
        return response;
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(NoSuchElementException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Element wasn't found",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(NoSuchDateException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Date wasn't found",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(MetadataSavingException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Metadata wasn't saved correct",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(InvalidTextException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Your text should be above 3 character",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleException(InvalidDateException e) {
        GlobalErrorResponse response = new GlobalErrorResponse(
                "Your expression date should be above 0 and less than 60 days",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}