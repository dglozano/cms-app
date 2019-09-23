package cms.cms_case;

public class InvalidCaseStateException extends Exception {

    private static final String MESSAGE_TEMPLATE = "ERROR. Couldn't execute the action %s on the Case Nº %s because it is in %s";

    public InvalidCaseStateException(String illegalAction, String caseId, String caseState) {
        super(String.format(MESSAGE_TEMPLATE, illegalAction, caseId, caseState));
    }
}
