package com.example.decrypt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecryptAndSaveService {
    private final EncryptionService encryptionService;

    private static final int SIZE = 100;

    @Value("classpath:data.txt")
    Resource resource;

    private static final String delimiter = ",";
    private static final String DECRYPTION_MARKER = "fXtHGyiCPz";

    @SneakyThrows
    public void saveConsumerRecord() {
        try (var writer = Files.newBufferedWriter(Paths.get("Decrypted.csv"))) {
            CSVPrinter printer = CSVFormat.Builder.create().setDelimiter(",").setHeader().build().print(writer);

            try (BufferedReader br = Files.newBufferedReader(Paths.get("data.txt"))) {
                br.lines().parallel().forEach(
                        rec -> {
                            try {
                                var inputToken = inputTokenEncrypted(rec);
                                if (isValueContainsDecryptionMarker(inputToken)) {
                                    String valueWithoutMarker = inputToken.substring(DECRYPTION_MARKER.length());
                                    var decryptData = encryptionService.decrypt(valueWithoutMarker);
                                    printer.printRecord(inputToken + " = " + decryptData);
                                }else{
                                    var decryptData = encryptionService.decrypt(inputToken);
                                    printer.printRecord(inputToken + " == " + decryptData);
                                }
                            } catch (Exception e) {
                                log.error("error saving data {} due to {}", rec, e);
                            }
                        }
                );
                printer.flush();
            }
        }
    }
    boolean isValueContainsDecryptionMarker(String value) {
        return nonNull(value) && value.startsWith(DECRYPTION_MARKER);
    }

    String inputTokenEncrypted(String rec) {
        String inputToken = "";
        if( rec.contains( delimiter)){
            inputToken = rec.split(delimiter)[0];
        } else {
            inputToken = rec;
        }
        return inputToken;
    }
}