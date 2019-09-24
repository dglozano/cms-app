package cms;

import cms.cms_case.Case;
import cms.cms_case.CasePaymentOrder;
import cms.payment.PaymentService;

import java.util.*;

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

    public void createCase(String customerName) {
        Optional<CaseManager> optionalCaseManager = caseWorkerAssignmentStrategy.selectConvinientCaseWorker(caseManagers);
        if (optionalCaseManager.isPresent()) {
            CaseManager caseManager = optionalCaseManager.get();
            Case newCase = new Case(nextId++, customerName, caseManager, this);
            caseManager.assignCase(newCase);
            cases.add(newCase);
            System.out.println(String.format("New case created: %s", newCase.toString()));
        } else {
            System.out.println("ERROR. There are no possible caseworkers to create case.");
        }
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
            try {
                assignCaseToAuditor(casePaymentOrder.getCase());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void assignCaseToAuditor(Case caseToAudit) throws Exception {
        Double amountToPay = caseToAudit.getCasePaymentOrder()
                .orElseThrow(() -> new Exception(String.format("Couldn't select an auditor for case %s" +
                        " because it doens't have a PaymentOrderIssued", caseToAudit.toString())))
                .getAmount();
        caseAuditorSelectionStrategy
                .selectCaseManagerAuditorForCase(caseManagers, caseToAudit.getCaseManager(), amountToPay)
                .orElseThrow(() -> new Exception(String.format("ERROR. There are no possible auditors for case %s", caseToAudit.toString())))
                .addCaseToAudit(caseToAudit);
    }

    public List<Case> getCases() {
        return cases;
    }

    public List<CaseManager> getCaseManagers() {
        return caseManagers;
    }

    public interface CaseWorkerAssignmentStrategy {
        Optional<CaseManager> selectConvinientCaseWorker(List<CaseManager> caseManagers);
    }

    public interface CaseAuditorSelectionStrategy {
        Optional<CaseManager> selectCaseManagerAuditorForCase(List<CaseManager> caseManagers,
                                                              final CaseManager assignedCaseManager,
                                                              final Double amountToPay);
    }

    public class RandomWorkerAssignmentStrategy implements CaseManagementSystem.CaseWorkerAssignmentStrategy {

        private final Random RANDOM = new Random();

        @Override
        public Optional<CaseManager> selectConvinientCaseWorker(List<CaseManager> caseManagers) {

            return caseManagers.isEmpty() ?
                    Optional.empty() :
                    Optional.of(caseManagers.get(RANDOM.nextInt(caseManagers.size())));
        }
    }

    public class LowestPrivilegeAuditorSelectionStrategy implements CaseManagementSystem.CaseAuditorSelectionStrategy {

        @Override
        public Optional<CaseManager> selectCaseManagerAuditorForCase(List<CaseManager> caseManagers,
                                                                     final CaseManager assignedCaseManager,
                                                                     final Double amountToPay) {
            return caseManagers.stream()
                    .filter(caseManager -> {
                        boolean isTheCaseManagerOfTheCase = caseManager.equals(assignedCaseManager);
                        boolean canAuditTheMoneyAmount = caseManager.getPaymentAuditingLimit() >= amountToPay;
                        return !isTheCaseManagerOfTheCase && canAuditTheMoneyAmount;
                    })
                    .min(Comparator.comparing(CaseManager::getPaymentAuditingLimit));
        }
    }
}
