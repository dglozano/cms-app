package cms;

import cms.cms_case.CasePaymentOrder;

public interface CasesPaymentOrderListener {

    void onPaymentOrderChange(CasePaymentOrder casePaymentOrder);
}
