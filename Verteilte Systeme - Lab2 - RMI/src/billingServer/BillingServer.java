/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

/**
 *
 * @author lisibauernhofer
 */
public class BillingServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here


    }



    public PriceSteps getPriceSteps(){

        return null;
    }

    public void createPriceStep(double startPrice, double endPrice, double fixedPrice, double variablePricePercent){

    }

    public void deletePriceStep(double startPrice, double endPrice){

    }

    public void billAuction(String user, long auctionID, double price){

    }

    public Bill getBill(String user){
        return null;
    }

     private static class Bill {

        public Bill() {
        }
    }

    private static class PriceSteps {

        public PriceSteps() {
        }
    }


}
