package cms.cms_case;

class PendingAuthorizationCaseState extends CaseState {

    @Override
    void approvePendingPayment(Case caseContext) throws InvalidCaseStateException {
        caseContext.getCasePaymentOrder()
                .orElseThrow(() -> new InvalidCaseStateException("<Approve Pending Payment>",
                        caseContext.getId().toString(),
                        getStateName()))
                .authorize();
        caseContext.notifyPaymentListener();
        caseContext.setState(Case.resolvedState);
    }

    @Override
    void rejectPendingPayment(Case caseContext) {
        caseContext.setState(Case.rejectedState);
    }

    @Override
    String getStateName() {
        return "PENDING_AUTHORIZATION_STATE";
    }
}
