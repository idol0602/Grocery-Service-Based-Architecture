package nguyennhatquan.springbootreview.dto.momo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MomoCreatePaymentRequest {
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private Long amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String extraData;
    private String requestType;
    private String signature;
    private String lang;
}
