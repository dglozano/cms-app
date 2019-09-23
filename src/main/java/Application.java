import cms.CaseManagementSystem;
import cms.CaseManager;
import cms.cms_case.Case;
import cms.payment.PaymentOrder;
import cms.payment.PaymentService;

import java.util.Random;

public class Application {

    public static void main(String[] args) {
        CaseManagementSystem caseManagementSystem = new CaseManagementSystem(new ConsolePaymentService());

        caseManagementSystem.enrollNewCaseManager("Diego", "Garcia Lozano", 10000d);
        caseManagementSystem.enrollNewCaseManager("Juan", "Perez", 5000d);
        caseManagementSystem.enrollNewCaseManager("Maria", "Gonzalez", 2000d);
        caseManagementSystem.enrollNewCaseManager("Auditor", "Primero", 1000d, 400000d);
        caseManagementSystem.enrollNewCaseManager("Auditor", "Segundo", 1000d, 600000d);
        caseManagementSystem.enrollNewCaseManager("Auditor", "Tercero", 1000d, 800000d);

        Case firstCase = caseManagementSystem.createCase("One Customer");
        Case secondCase = caseManagementSystem.createCase("Second Customer");
        Case thirdCase = caseManagementSystem.createCase("Third Customer");
        Case fourthCase = caseManagementSystem.createCase("Fourth Customer");
        Case fifthCase = caseManagementSystem.createCase("Fifth Customer");
        Case sixthCase = caseManagementSystem.createCase("Sixth Customer");

        System.out.println();

        CaseManager firstCaseManager = firstCase.getCaseManager();
        firstCaseManager.rejectCase(firstCase);
        firstCaseManager.authorizePendingCase(firstCase);
        firstCaseManager.issuePaymentOrderForCase(firstCase, 100d);

        System.out.println();

        CaseManager secondCaseManager = secondCase.getCaseManager();
        secondCaseManager.issuePaymentOrderForCase(secondCase, 200d); // will be approved immediately
        secondCaseManager.rejectCase(secondCase);

        System.out.println();

        CaseManager thirdCaseManager = thirdCase.getCaseManager();
        thirdCaseManager.issuePaymentOrderForCase(thirdCase, 30000d); // will be put on hold.

        System.out.println();

        CaseManager fourthCaseManager = fourthCase.getCaseManager();
        fourthCaseManager.issuePaymentOrderForCase(fourthCase, 44000d); // will be put on hold

        System.out.println();

        CaseManager fifthCaseCaseManager = fifthCase.getCaseManager();
        fifthCaseCaseManager.issuePaymentOrderForCase(fifthCase, 550000d); // will be put on hold

        System.out.println();

        CaseManager sixthCaseManager = sixthCase.getCaseManager();
        sixthCaseManager.issuePaymentOrderForCase(sixthCase, 660000d); // will be put on hold


        System.out.println();
        System.out.println("Starting to audit...");

        caseManagementSystem.getCaseManagers().stream()
                .filter(caseManager -> !caseManager.getCasesToAudit().isEmpty())
                .forEach(auditor -> {
                    System.out.println(String.format("Auditor %s is resolving his cases...", auditor.getFullName()));
                    auditor.getCasesToAudit().forEach(caseToAudit -> {
                        System.out.println(String.format("Thinking what to do about case Nº %s...", caseToAudit.getId()));
                        boolean decidedToApprove = new Random().nextBoolean();
                        if (decidedToApprove) {
                            System.out.println("... APPROVED!");
                            auditor.authorizePendingCase(caseToAudit);
                        } else {
                            System.out.println("... REJECTED!");
                            auditor.rejectPendingCase(caseToAudit);
                        }
                    });
                    System.out.println();
                });
    }

    private static class ConsolePaymentService implements PaymentService {
        @Override
        public void makePayment(PaymentOrder paymentOrder) {
            System.out.println(String.format("Payment succeeded: %,.2f transferred from %s to %s.",
                    paymentOrder.getAmount(),
                    paymentOrder.getPayer(),
                    paymentOrder.getReceiver()));
        }
    }
}
