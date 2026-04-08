package nguyennhatquan.springbootreview.util;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@Component
public class MomoSignatureUtil {
    public String buildRawSignature(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(java.util.stream.Collectors.joining("&"));
    }

    public String sign(String rawData, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(Map<String, String> params, String signature, String secretKey) {
        String rawData = buildRawSignature(params);
        String expectedSignature = sign(rawData, secretKey);
        return MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8));
    }
}
