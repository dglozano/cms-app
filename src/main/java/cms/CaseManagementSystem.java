package cms;

import cms.cms_case.Case;
import cms.cms_case.CasePaymentOrder;
import cms.payment.PaymentService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CaseManagementSystem implements CasesPaymentOrderListener {

    private static Integer nextId = 1;
    private PaymentService paymentService;
    private List<Case> cases;
    private List<CaseManager> caseManagers;
    private CaseAuditorSelectionStrategy caseAuditorSelectionStrategy;
    private CaseWorkerAssignmentStrategy caseWorkerAssignmentStrategy;

    public CaseManagementSystem(PaymentService paymentService) {
        this.paymentService = paymentService;
        this.cases = new ArrayList<>();
        this.caseManagers = new ArrayList<>();
        this.caseAuditorSelectionStrategy = new LowestPrivilegeAuditorSelectionStrategy();
        this.caseWorkerAssignmentStrategy = new RandomWorkerAssignmentStrategy();
    }

    public Case createCase(String customerName) {
        CaseManager selectedCaseManager = caseWorkerAssignmentStrategy.selectConvinientCaseWorker(caseManagers);
        Case newCase = new Case(nextId++, customerName, selectedCaseManager, this);
        selectedCaseManager.assignCase(newCase);
        cases.add(newCase);
        System.out.println(String.format("New case created: %s", newCase.toString()));
        return newCase;
    }

    public void enrollNewCaseManager(String firstName, String lastName, Double paymentLimit) {
        CaseManager caseManager = new CaseManager(nextId++, firstName, lastName, paymentLimit);
        caseManagers.add(caseManager);
        System.out.println(String.format("New case manager created: %s", caseManager.toString()));
    }

    public void enrollNewCaseManager(String firstName, String lastName, Double paymentLimit, Double paymentAuditingLimit) {
        CaseManager caseManager = new CaseManager(nextId++, firstName, lastName, paymentLimit, paymentAuditingLimit);
        caseManagers.add(caseManager);
        System.out.println(String.format("New case manager created: %s", caseManager.toString()));
    }

    @Override
    public void onPaymentOrderChange(CasePaymentOrder casePaymentOrder) {
        checkPaymentOrderAuthorization(casePaymentOrder);
    }

    private void checkPaymentOrderAuthorization(CasePaymentOrder casePaymentOrder) {
        if (casePaymentOrder.isAuthorized()) {
            paymentService.makePayment(casePaymentOrder);
        } else {
            assignCaseToAuditor(casePaymentOrder.getCase());
        }
    }

    private void assignCaseToAuditor(Case caseToAudit) {
        CaseManager chosenAuditor = caseAuditorSelectionStrategy.selectCaseManagerAuditorForCase(caseManagers, caseToAudit);
        chosenAuditor.addCaseToAudit(caseToAudit);
    }

    public List<Case> getCases() {
        return cases;
    }

    public List<CaseManager> getCaseManagers() {
        return caseManagers;
    }

    public interface CaseWorkerAssignmentStrategy {
        CaseManager selectConvinientCaseWorker(List<CaseManager> caseManagers);
    }

    public interface CaseAuditorSelectionStrategy {
        CaseManager selectCaseManagerAuditorForCase(List<CaseManager> caseManagers, Case pendingCase);
    }

    public class RandomWorkerAssignmentStrategy implements CaseManagementSystem.CaseWorkerAssignmentStrategy {

        private final Random RANDOM = new Random();

        //TODO: throw Exception if no caseManager matches the criteria
        @Override
        public CaseManager selectConvinientCaseWorker(List<CaseManager> caseManagers) {

            return caseManagers.get(RANDOM.nextInt(caseManagers.size()));
        }
    }

    public class LowestPrivilegeAuditorSelectionStrategy implements CaseManagementSystem.CaseAuditorSelectionStrategy {

        //TODO: throw Exception if no caseManager matches the criteria
        @Override
        public CaseManager selectCaseManagerAuditorForCase(List<CaseManager> caseManagers, final Case pendingCase) {
            return caseManagers.stream()
                    .filter(caseManager -> !caseManager.equals(pendingCase.getCaseManager())
                            && caseManager.getPaymentAuditingLimit() >= pendingCase.getCasePaymentOrder().getAmount())
                    .min(Comparator.comparing(CaseManager::getPaymentAuditingLimit))
                    .get();
        }
    }
}
