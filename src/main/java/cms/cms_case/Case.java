package cms.cms_case;

import cms.CaseManager;
import cms.CasesPaymentOrderListener;

import java.util.Objects;

public class Case {

    static OpenCaseState openState = new OpenCaseState();
    static PendingAuthorizationCaseState pendingAuthorizationState = new PendingAuthorizationCaseState();
    static RejectedCaseState rejectedState = new RejectedCaseState();
    static ResolvedCaseState resolvedState = new ResolvedCaseState();

    private final Integer id;
    private final String customerName;
    private CaseManager caseManager;
    private CaseState currentState;
    private CasePaymentOrder casePaymentOrder;
    private CasesPaymentOrderListener paymentOrderListener;

    public Case(Integer id, String customerName, CaseManager caseManager, CasesPaymentOrderListener paymentOrderListener) {
        this.id = id;
        this.customerName = customerName;
        this.caseManager = caseManager;
        this.paymentOrderListener = paymentOrderListener;
        setState(openState);
    }

    public void issuePayment(Double amountToPay) throws InvalidCaseStateException {
        currentState.issuePayment(this, amountToPay);
    }

    public void approvePendingPayment() throws InvalidCaseStateException {
        currentState.approvePendingPayment(this);
    }

    public void rejectPendingPayment() throws InvalidCaseStateException {
        currentState.rejectPendingPayment(this);
    }

    public void rejectCase() throws InvalidCaseStateException {
        currentState.rejectCase(this);
    }

    void setState(CaseState newState) {
        System.out.println(String.format("Case Nº %s changing state. Previous: %s. New: %s",
                id, currentState == null ? "null" : currentState.getStateName(), newState.getStateName()));
        this.currentState = newState;
    }

    void setCasePaymentOrder(CasePaymentOrder casePaymentOrder) {
        this.casePaymentOrder = casePaymentOrder;
    }

    void notifyPaymentListener() {
        paymentOrderListener.onPaymentOrderChange(getCasePaymentOrder());
    }

    public Integer getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public CaseManager getCaseManager() {
        return caseManager;
    }

    public CasePaymentOrder getCasePaymentOrder() {
        return casePaymentOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Case aCase = (Case) o;
        return Objects.equals(getId(), aCase.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return String.format("<%d, Customer %s, CaseManager %s>", id, getCustomerName(), caseManager.getFullName());
    }
}
