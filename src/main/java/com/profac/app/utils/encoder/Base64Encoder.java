package com.profac.app.utils.encoder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Base64Encoder {
    public static Mono<String> encodeFileToBase64Binary(FilePart file) {
        Flux<DataBuffer> content = file.content();
        List<DataBuffer> dataBuffers = content.collectList().block();

        if (dataBuffers == null) {
            throw new RuntimeException("File content could not be retrieved");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataBuffers.forEach(buffer -> {
            try {
                buffer.asInputStream().transferTo(outputStream);
            } catch (IOException e) {
                throw new RuntimeException("Error while processing the file", e);
            }
        });

        byte[] bytes = outputStream.toByteArray();
        return Mono.just(Base64.getEncoder().encodeToString(bytes));
    }
}
