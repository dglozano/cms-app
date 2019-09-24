package cms.cms_case;

import cms.payment.PaymentOrder;

import java.time.LocalDateTime;

public class CasePaymentOrder implements PaymentOrder {

    private final Double amountToPay;
    private final LocalDateTime dateIssued;
    private final Case paymentCase;
    private Boolean authorized;

    public CasePaymentOrder(Double amountToPay, Case paymentCase) {
        this.amountToPay = amountToPay;
        this.paymentCase = paymentCase;
        this.dateIssued = LocalDateTime.now();
        this.authorized = false;
    }

    public Case getCase() {
        return paymentCase;
    }

    public Boolean isAuthorized() {
        return authorized;
    }

    public void authorize() {
        this.authorized = true;
    }

    public void reject() {
        this.authorized = false;
    }

    @Override
    public String getPayer() {
        return paymentCase.getCaseManager().getFullName();
    }

    @Override
    public String getReceiver() {
        return paymentCase.getCustomerName();
    }

    @Override
    public Double getAmount() {
        return amountToPay;
    }

    @Override
    public LocalDateTime getDateTimeIssued() {
        return dateIssued;
    }
}
