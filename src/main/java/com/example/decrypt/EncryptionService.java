package com.example.decrypt;

import com.corelogic.clp.starters.encryption.client.EncryptionClient;
import com.corelogic.clp.starters.encryption.client.model.BulkDecryptionRequest;
import com.corelogic.clp.starters.encryption.client.model.DecryptionRequest;
import com.corelogic.clp.starters.encryption.client.model.DecryptionResponsePair;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Component
@RequiredArgsConstructor
@Scope(proxyMode = TARGET_CLASS)
public class EncryptionService {
    private final EncryptionServiceConfigurationProperties encryptionServiceConfigurationProperties;
    private final EncryptionClient encryptionClient;


    @SneakyThrows
    public String decrypt(String value) {
        if (StringUtils.isBlank(value)) {
            return EMPTY;
        }

        var decryptionRequest = new DecryptionRequest(value, encryptionServiceConfigurationProperties.getKeyName());
        var decryptionResponse = encryptionClient.decrypt(decryptionRequest);
        return decryptionResponse.getDecryptedValue();
    }

    @SneakyThrows
    public Map<String, String> decryptBulk(String... values) {
        var strings = Arrays.stream(values)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        var isBlank = Arrays.stream(values)
                .filter(StringUtils::isBlank)
                .distinct()
                .collect(Collectors.toList());

        if (strings.isEmpty()) {
            var hashMap = new HashMap<String, String>();
            isBlank.forEach(b -> hashMap.put(b, EMPTY));
            return hashMap;
        }
        var bulkDecryptionRequest = new BulkDecryptionRequest(encryptionServiceConfigurationProperties.getKeyName(), strings);
        var decryptionResponse = encryptionClient.bulkDecrypt(bulkDecryptionRequest);
        var map = decryptionResponse.getDecryptedValues().stream()
                .collect(toMap(DecryptionResponsePair::getEncryptedValue, DecryptionResponsePair::getUnencryptedValue, (o1, o2) -> o1));
        isBlank.forEach(b -> map.put(b, EMPTY));
        return map;
    }
}
