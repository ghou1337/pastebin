package pl.pastebin.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pastebin.model.Data;
import pl.pastebin.model.DataResponse;

import java.io.IOException;

@Service
@AllArgsConstructor
public class DataService {
    private final GoogleCloudStorageService googleCloudStorageService;
    private MetadataService metadataService;
}
