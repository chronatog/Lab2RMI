/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

import billingServer.PriceSteps.PriceStep;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;


/**
 *
 * @author lisibauernhofer
 */
public class Bill implements Serializable{

    private String user;
   // private ArrayList<UserBill> userbill;
    private ArrayList<AuctionBill> auctionBill = new ArrayList<AuctionBill>();

    public Bill(String user, ArrayList<UserBill> userbill, PriceSteps pirceStep){
        PriceSteps step = new PriceSteps();
        //PriceStep step = sp.new PriceStep();

        for(int i = 0; i < userbill.size(); i++){

            if(userbill.get(i).user.equals(user)){

               long auctionID = userbill.get(i).auctionID;

               double price = userbill.get(i).price;

               
                   double feeFix = step.getFeeFixByPrice(price);
                   double feeFree = ((price*step.getFeeVariableByPrice(price)/100));
                   double feeTotal = feeFix+feeFree;

                   AuctionBill auctionbillstub = new AuctionBill(auctionID, price, feeFix, feeFree, feeTotal);
                   auctionBill.add(auctionbillstub);
                

            } else System.out.println("No Auctions for this user");
        }
    }



    @Override
    public String toString(){
        String liste = "";
        String header = "auction_ID\tstrike_price\tfee_fixed\tfee_variable\tfee_total";

        for(AuctionBill auction : auctionBill){

            liste += auction.formatAuctionBill() +"\n";
            //System.out.println("Liste: "+ liste);


        }
        return header + "\n" + liste;
    }


    


    public  class AuctionBill implements Serializable{

        private long auctionID;
        private double strikePrice;
        private double feeFix;
        private double feeVariable;
        private double feeTotal;

        public AuctionBill(long auctionID, double strikePrice, double feeFix, double feeVariable, double feeTotal) {
            this.auctionID = auctionID;
            this.strikePrice = strikePrice;
            this.feeFix = feeFix;
            this.feeVariable = feeVariable;
            this.feeTotal = feeTotal;
        }

        public String formatAuctionBill() {
        //%.2f -> Fliesskommerzahl die auf 2 Stellen gerundet wird
        //\t -> Tab
        //%% Prozentzeichen
        return String.format("%d\t\t  %.2f\t\t%.2f\t    %.2f\t\t%.2f", auctionID, strikePrice, feeFix, feeVariable, feeTotal);


        }


    }


}
