package pl.pastebin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.matchers.NotNull;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.pastebin.model.Data;
import pl.pastebin.model.DataResponse;
import pl.pastebin.services.DataService;
import pl.pastebin.services.GoogleCloudStorageService;
import pl.pastebin.services.MetadataService;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@WebMvcTest(controllers = SharingController.class)
@ExtendWith(MockitoExtension.class)
public class SharingControllerTest {
    @MockBean
    private MetadataService metadataService;

    @MockBean
    private GoogleCloudStorageService googleCloudStorageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_POST_ENDPOINT = "/api/post";
    private static final String API_GET_TEXT_BY_HASH_ENDPOINT = "/api/{hash}";
    private static final String time = "2024-01-01 10:11:11";


    @BeforeEach
    void setUp() {

    }

    @Test
    void postMessageTest() throws Exception {
        // given
        String time = "2024-01-01 10:11:11";
        Data data = new Data("FileName", "TEST text longer 3 char", Timestamp.valueOf(time) , 5, UUID.randomUUID().toString());
        DataResponse dataResponse = new DataResponse(data.getText(), Timestamp.valueOf(time), Timestamp.valueOf(time));
        String json = objectMapper.writeValueAsString(data);

        //when
        Mockito.when(googleCloudStorageService.uploadFile(data)).thenReturn("https://storage.googleapis.com/bucket/file_name");

        //then
        mockMvc.perform(post(API_POST_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                    .andExpect(jsonPath("$.text").value(dataResponse.getText()))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.expressionDate").isNotEmpty());
    }

    @Test
    void postMessageWithInvalidDataTest() throws Exception {
        // given
        Data invalidTextData = new Data("FileName", "", Timestamp.valueOf(time) , 5, UUID.randomUUID().toString());

        // when
        String json1 = objectMapper.writeValueAsString(invalidTextData);

        // then
        mockMvc.perform(post(API_POST_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postMessageWithInvalidDateTest() throws Exception {
        // given
        Data invalidDateData = new Data("FileName", "TEST", Timestamp.valueOf(time) , -1, UUID.randomUUID().toString());

        // when
        String json2 = objectMapper.writeValueAsString(invalidDateData);

        // then
        mockMvc.perform(post(API_POST_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTextByHashTest() throws Exception {
        // given
        String createdAtStr = "2024-01-01 10:11:11.111111";
        String expressionDateStr = "2024-01-05 10:11:11.111111";
        String text = "TEST text longer 3 char";
        String hash = UUID.randomUUID().toString();

        // Converting string to instant
        Instant createdAtInstant = Timestamp.valueOf(createdAtStr).toInstant();
        Instant expressionDateInstant = Timestamp.valueOf(expressionDateStr).toInstant();

        // Formating to ISO 8601 UTC
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
        String createdAtDate = dateFormatter.format(createdAtInstant);
        String expressionDateDate = dateFormatter.format(expressionDateInstant);


        // when
        Mockito.when(googleCloudStorageService.getFile(hash)).thenReturn(text);
        Mockito.when(metadataService.getCreatedAt(hash)).thenReturn(Timestamp.from(createdAtInstant));
        Mockito.when(metadataService.getExpressionDate(hash)).thenReturn(Timestamp.from(expressionDateInstant));

        //DataResponse dataResponse = new DataResponse(text, Timestamp.from(createdAtInstant), Timestamp.from(expressionDateInstant));


        // then
        String controllerResponse = mockMvc.perform(get(API_GET_TEXT_BY_HASH_ENDPOINT, hash))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.text").value(text))
                    .andExpect(jsonPath("$.createdAt", Matchers.startsWith(createdAtDate)))
                    .andExpect(jsonPath("$.expressionDate", Matchers.startsWith(expressionDateDate)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        System.out.println(controllerResponse);
    }
}
