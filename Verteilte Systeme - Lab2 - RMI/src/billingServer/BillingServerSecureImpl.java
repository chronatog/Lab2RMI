/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

import billingServer.PriceSteps.PriceStep;
import java.rmi.RemoteException;
import java.util.ArrayList;

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



    @Override
    public PriceSteps getPriceSteps()throws RemoteException {
        return BillingServerSecureImpl.priceSteps;
    }

    @Override
    public void createPriceStep(double startPrice, double endPrice, 
            double fixedPrice, double variablePricePercent)throws RemoteException{

        priceSteps.addPriceStep(startPrice, endPrice, fixedPrice, variablePricePercent);



    }
    @Override
    public void deletePriceStep(double startPrice, double endPrice) throws RemoteException{
        priceSteps.deleteStep(startPrice, endPrice);
    }

    public void billAuction(String user, long auctionID, double price){

    }

    public Bill getBill(String user){
        return null;
    }






}

