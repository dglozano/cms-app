package cms.payment;

import java.time.LocalDateTime;

public interface PaymentOrder {

    String getPayer();
    String getReceiver();
    Double getAmount();
    LocalDateTime getDateTimeIssued();
}
