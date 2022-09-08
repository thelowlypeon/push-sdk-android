package com.vibes.vibes.tracking;

/**
 * Represents all the possible actions that can be invoked for ecommerce related tracking
 */
public class Actions {

    /**
     * The list of supported product related ecommerce tracking actions.
     */
    public static enum ProductAction {
        CLICK("click"),
        DETAIL("detail"),
        ADD("add"),
        REMOVE("remove"),
        CHECKOUT("checkout"),
        CHECKOUT_OPTION("checkout_option"),
        PROMO_CLICK("promo_click");
        private String actionName;

        ProductAction(String actionName) {
            this.actionName = actionName;
        }

        public String getActionName() {
            return actionName;
        }
    }

    /**
     * The list of supported purchase related ecommerce tracking actions.
     */
    public static enum PurchaseAction {
        PURCHASE("purchase"),
        REFUND("refund");

        private String actionName;

        PurchaseAction(String actionName) {
            this.actionName = actionName;
        }

        public String getActionName() {
            return actionName;
        }
    }
}
