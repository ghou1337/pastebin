package pl.pastebin.services;

import com.google.common.hash.Hashing;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pastebin.exe.MetadataSavingException;
import pl.pastebin.exe.NoSuchDateException;
import pl.pastebin.model.Data;
import pl.pastebin.model.Metadata;
import pl.pastebin.repos.MetadataRepos;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class MetadataService {
    private final MetadataRepos metadataRepos;

    @Transactional
    public String saveMetadataFromFile(Data data) {
        Metadata metadata = new Metadata();
        metadata.setHash(data.getUUID());
        metadata.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        metadata.setExpressionDate(Timestamp.valueOf(LocalDateTime.now().plusDays(data.getExpressionDate())));
        System.out.println("Saving data...");
        try {
            metadataRepos.save(metadata);
        }catch (MetadataSavingException e){}
        return metadata.getHash();
    }

    public Metadata getMetadata(String hash) {
        return metadataRepos.findByHash(hash);
    }

    public Timestamp getExpressionDate(String hash) {
        return metadataRepos.findByHash(hash).getExpressionDate();
    }

    public Timestamp getCreatedAt(String hash) {
        try {
            return metadataRepos.findByHash(hash).getCreatedAt();
        } catch (NoSuchElementException e) {
            throw new NoSuchDateException();
        }
    }

    public List<Metadata> getAll() {
        return metadataRepos.findAll();
    }

}
