package cms.cms_case;

abstract class CaseState {

    void issuePayment(Case caseContext, Double amountToPay) throws InvalidCaseStateException {
        throw new InvalidCaseStateException("<Issue Payment>",
                caseContext.getId().toString(),
                getStateName());
    }

    void approvePendingPayment(Case caseContext) throws InvalidCaseStateException {
        throw new InvalidCaseStateException("<Approve Pending Payment>",
                caseContext.getId().toString(),
                getStateName());
    }

    void rejectPendingPayment(Case caseContext) throws InvalidCaseStateException {
        throw new InvalidCaseStateException("<Reject Pending Payment>",
                caseContext.getId().toString(),
                getStateName());
    }

    void rejectCase(Case caseContext) throws InvalidCaseStateException {
        throw new InvalidCaseStateException("<Reject Case>",
                caseContext.getId().toString(),
                getStateName());
    }

    abstract String getStateName();
}
