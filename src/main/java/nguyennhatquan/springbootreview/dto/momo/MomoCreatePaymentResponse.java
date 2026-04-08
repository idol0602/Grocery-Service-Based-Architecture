package nguyennhatquan.springbootreview.dto.momo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MomoCreatePaymentResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private Long amount;
    private Long responseTime;
    private String message;
    private Integer resultCode;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
}
