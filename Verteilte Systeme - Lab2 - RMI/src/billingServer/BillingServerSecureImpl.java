/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

import billingServer.PriceSteps.PriceStep;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author lisibauernhofer
 */
public class BillingServerSecureImpl implements BillingServerSecure{

    /**
     * @param args the command line arguments
     */
     private static ArrayList<PriceSteps> priceStepsList = new ArrayList<PriceSteps>();
     private static PriceSteps priceSteps = new PriceSteps();
     private static HashMap<String, ArrayList<UserBill>> bill;


    @Override
    public PriceSteps getPriceSteps()throws RemoteException {
        return BillingServerSecureImpl.priceSteps;
    }

    @Override
    public void createPriceStep(double startPrice, double endPrice, 
            double fixedPrice, double variablePricePercent)throws RemoteException{
        System.out.println("createPriceStep!!!");

        priceSteps.addPriceStep(startPrice, endPrice, fixedPrice, variablePricePercent);
        System.out.println("PriceStep: " + getPriceSteps().toString());



    }
    @Override
    public void deletePriceStep(double startPrice, double endPrice) throws RemoteException{
        priceSteps.deleteStep(startPrice, endPrice);
    }

    public void billAuction(String user, long auctionID, double price){

        ArrayList<UserBill> billhelb;

        synchronized (bill) {
            billhelb = bill.get(user);
            if (billhelb == null) {
                billhelb = new ArrayList<UserBill>();
		bill.put(user, billhelb);
            }

        }

        UserBill userBill = new UserBill(auctionID, price, 0.0, 0.0, 0.0);
		synchronized (billhelb) {
			billhelb.add(userBill);
		}
           

    }

    @Override
    public Bill getBill(String user){

        return null;
    }






}

