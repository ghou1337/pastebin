package pl.pastebin.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pastebin.model.Data;

import java.util.NoSuchElementException;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
final public class GoogleCloudStorageService {

    private final Storage storage;
    private final MetadataService metadataService;
    private final String bucketName = "smart-pastebin-bucket";

    public String uploadFile(Data data) {
        data.setUUID(UUID.randomUUID().toString());
        BlobInfo blobInfo = BlobInfo
                .newBuilder(bucketName, data.getUUID())
                .setContentType("text/plain")
                .build();
        try {
            storage.create(blobInfo, data.getText().getBytes(UTF_8));
        } catch (NoSuchElementException e) {}

        metadataService.saveMetadataFromFile(data);
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, data.getUUID());
    }

    public String getFile(String hash) {
        Blob file = storage.get(bucketName, hash);
        if(file == null) {
            throw new NoSuchElementException();
        }
        return new String(file.getContent(), UTF_8);
    }
}
