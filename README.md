# Technical interview assignment. CMS application.

My proposed solution for the following assignment, which I had to solve for a technical interview when applying for a junior position.


>Imagine a case management system. The system must include, among other things, functionality to pay money to customers.  Payments are made by case managers, with the following criteria:

> * Case managers are authorized to pay up to a certain amount (eg 10,000). Payments that exceed the authority must still be registered by the caseworker, but they should automatically be checked by someone else who either approves or rejects the payment.

> * Some case managers are authorized to approve payments up to a given amount, eg 100,000.

> * It should not be possible for a caseworker to know / guess who will approve a payment.

> * When checking a payment, an auditor must always be selected who has the lowest authorization, but still higher than the amount to be paid.

> You should write the domain logic for this feature in any language you prefer. You can assume that we integrate with a payment system, so this functionality should not be created.
