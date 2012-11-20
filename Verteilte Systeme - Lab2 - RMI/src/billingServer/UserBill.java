/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

/**
 *
 * @author lisibauernhofer
 */
 public  class UserBill {

        private long auctionID;
        private double strikePrice;
        private double feeFix;
        private double feeVariable;
        private double feeTotal;

        public UserBill(long auctionID, double strikePrice, double feeFix, double feeVariable, double feeTotal) {
            this.auctionID = auctionID;
            this.strikePrice = strikePrice;
            this.feeFix = feeFix;
            this.feeVariable = feeVariable;
            this.feeTotal = feeTotal;
        }

   
    }
