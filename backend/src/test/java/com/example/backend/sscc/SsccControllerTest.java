package com.example.backend.sscc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.example.backend.sscc.dto.SsccRequestDto;
import com.example.backend.sscc.service.SsccStorageService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class SsccControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private SsccStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService.clear();
    }

    @Test
    @DisplayName("POST /sscc is saved and returned 201")
    void postValid() {
        restTestClient.post().uri("/api/v1/sscc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SsccRequestDto("340123450000000017", null))
                .exchange()
                .expectStatus().isCreated();

        List<String> saved = storageService.getAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0)).isEqualTo("340123450000000017");
    }

    @Test
    @DisplayName("Duplicate SSCC returns 409 Conflict")
    void postDuplicate() {
        storageService.add("340123450000000017");

        restTestClient.post().uri("/api/v1/sscc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SsccRequestDto("340123450000000017", null))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        List<String> saved = storageService.getAll();
        assertThat(saved).hasSize(1);
    }

    @Test
    @DisplayName("Invalid SSCC returns 422 and is not saved")
    void postInvalid() {
        restTestClient.post().uri("/api/v1/sscc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SsccRequestDto("340123450000000019", null))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);

        List<String> saved = storageService.getAll();
        assertThat(saved).isEmpty();
    }

    @Test
    @DisplayName("Valid SSCC with GS1 prefix is saved")
    void postValidPrefix() {
        restTestClient.post().uri("/api/v1/sscc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SsccRequestDto("340123450000000017", "4012345"))
                .exchange()
                .expectStatus().isCreated();

        List<String> saved = storageService.getAll();
        assertThat(saved).hasSize(1);
    }

    @Test
    @DisplayName("Wrong GS1 prefix returns 422 and is not saved")
    void postWrongPrefix() {
        restTestClient.post().uri("/api/v1/sscc")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SsccRequestDto("340123450000000017", "9999999"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);

        List<String> saved = storageService.getAll();
        assertThat(saved).isEmpty();
    }

    @Test
    @DisplayName("GET /sscc returns all saved SSCCs")
    void getAll() {
        storageService.add("340123450000000017");
        storageService.add("376130321109103420");

        restTestClient.get().uri("/api/v1/sscc")
                .exchange()
                .expectStatus().isOk();

        List<String> saved = storageService.getAll();
        assertThat(saved).hasSize(2);
    }

}
