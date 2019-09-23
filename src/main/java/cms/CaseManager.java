package cms;

import cms.cms_case.Case;
import cms.cms_case.InvalidCaseStateException;

import java.util.ArrayList;
import java.util.List;

public class CaseManager {

    private final Integer id;
    private String firstName;
    private String lastName;
    private Double paymentAuditingLimit;
    private Double paymentLimit;
    private List<Case> assignedCases;
    private List<Case> casesToAudit;

    CaseManager(Integer id, String firstName, String lastName, Double paymentLimit) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.paymentLimit = paymentLimit;
        this.paymentAuditingLimit = 0d;
        assignedCases = new ArrayList<>();
        casesToAudit = new ArrayList<>();
    }

    CaseManager(Integer id, String firstName, String lastName, Double paymentLimit, Double paymentAuditingLimit) {
        this(id, firstName, lastName, paymentLimit);
        this.paymentAuditingLimit = paymentAuditingLimit;
    }

    public void issuePaymentOrderForCase(Case aCase, Double amount) {
        if (assignedCases.contains(aCase)) {
            try {
                aCase.issuePayment(amount);
            } catch (InvalidCaseStateException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(("The case is not assigned to this CaseManager"));
        }
    }

    public void authorizePendingCase(Case aCase) {
        if (casesToAudit.contains(aCase)) {
            try {
                aCase.approvePendingPayment();
            } catch (InvalidCaseStateException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(("The case is not being audited by this CaseManager "));
        }
    }

    public void rejectPendingCase(Case aCase) {
        if (casesToAudit.contains(aCase)) {
            try {
                aCase.rejectPendingPayment();
            } catch (InvalidCaseStateException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(("The case is not being audited by this CaseManager "));
        }
    }

    public void rejectCase(Case aCase) {
        if (assignedCases.contains(aCase)) {
            try {
                aCase.rejectCase();
            } catch (InvalidCaseStateException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(("The case is not assigned to this CaseManager"));
        }
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return lastName.toUpperCase() + ", " + firstName;
    }

    public Double getPaymentAuditingLimit() {
        return paymentAuditingLimit;
    }

    public Double getPaymentLimit() {
        return paymentLimit;
    }

    public List<Case> getAssignedCases() {
        return assignedCases;
    }

    public List<Case> getCasesToAudit() {
        return casesToAudit;
    }

    void addCaseToAudit(Case caseToAudit) {
        this.casesToAudit.add(caseToAudit);
    }

    public void assignCase(Case newCase) {
        this.assignedCases.add(newCase);
    }

    @Override
    public String toString() {
        return String.format("<%d, %s, Pay Limit %.2f, Audit Limit %.2f>", id, getFullName(), paymentLimit, paymentAuditingLimit);
    }
}
