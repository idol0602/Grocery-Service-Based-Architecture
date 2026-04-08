package nguyennhatquan.springbootreview.controller;

import lombok.RequiredArgsConstructor;
import nguyennhatquan.springbootreview.dto.momo.MomoCallbackRequest;
import nguyennhatquan.springbootreview.dto.momo.MomoCreatePaymentResponse;
import nguyennhatquan.springbootreview.service.MomoPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment/momo")
public class MomoPaymentController {
    private final MomoPaymentService momoPaymentService;

    @PostMapping("/create")
    public ResponseEntity<MomoCreatePaymentResponse> creatPayment(@RequestParam Long orderId) {
        try {
            MomoCreatePaymentResponse response = momoPaymentService.createPayment(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody MomoCallbackRequest callbackRequest) {
        try {
            momoPaymentService.handleCallBack(callbackRequest);
        } catch (Exception e) {
            // Log exception, but still return 204 to MoMo so they stop retrying
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/return")
    public ResponseEntity<String> returnUrl(@RequestParam String orderId,
                                            @RequestParam Integer resultCode,
                                            @RequestParam String message) {
        if (resultCode == 0) {
            return ResponseEntity.ok("Payment successful for order: " + orderId);
        } else {
            return ResponseEntity.ok("Payment failed. Message: " + message);
        }
    }
}
