package cms.cms_case;

class OpenCaseState extends CaseState {

    @Override
    void issuePayment(Case caseContext, Double amountToPay) {
        CasePaymentOrder casePaymentOrder = new CasePaymentOrder(amountToPay, caseContext);
        if (amountToPay <= caseContext.getCaseManager().getPaymentLimit()) {
            casePaymentOrder.authorize();
            caseContext.setCasePaymentOrder(casePaymentOrder);
            caseContext.notifyPaymentListener();
            caseContext.setState(Case.resolvedState);
        } else {
            caseContext.setCasePaymentOrder(casePaymentOrder);
            caseContext.notifyPaymentListener();
            caseContext.setState(Case.pendingAuthorizationState);
        }
    }

    @Override
    void rejectCase(Case caseContext) {
        caseContext.setState(Case.rejectedState);
    }

    @Override
    String getStateName() {
        return "OPEN_STATE";
    }
}
