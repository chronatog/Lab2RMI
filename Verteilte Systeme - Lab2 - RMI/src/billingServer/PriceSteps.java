/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author lisibauernhofer
 */
public class PriceSteps {

    private ArrayList<PriceStep> PriceSteps;

    public PriceSteps() {
        PriceSteps = new ArrayList<PriceStep>();
    }

    PriceSteps(ArrayList<PriceSteps> priceSteps){

    }

    public void addPriceStep(double startPrice, double endPrice, double fixedPrice, double variablePricePercent) throws RemoteException {
        if(startPrice<0 || endPrice<0 || fixedPrice<0 || variablePricePercent<0){
            throw new RemoteException("Negativ values are not allowed");
        }

        PriceStep newPS = new PriceStep(startPrice, endPrice,fixedPrice,variablePricePercent);

        for (PriceStep helpPS : PriceSteps){
            if(newPS.collide(helpPS)== true){
                throw new RemoteException("The price interval collides with an existing price step");
            }
        }
        this.PriceSteps.add(newPS);
    }

    void deleteStep(double startPrice, double endPrice) {
        for (int i = 0; i< PriceSteps.size(); i++){
            if(PriceSteps.get(i).minPrice == startPrice && PriceSteps.get(i).maxPrice == endPrice){
                PriceSteps.remove(i);
            }
        }
    }



    public  String showHeader(){

        String header = "Min_Price\tMax_Price\tFee_Fixed\tFee_variable";

        //for()
        return header;// +"/n" +""+ String.format("%.2f\t%.2f\t%.1f\t%.1%%", minPrice, maxPrice, feeFixed, feeVariable);
        }



    public class PriceStep{


        private double minPrice = 0.0;
        private double maxPrice = 0.0;
        private double feeFixed = 0.0;
        private double feeVariable = 0.0;

        PriceStep(double minPrice, double maxPrice, double feeFixed, double feeVariable){
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.feeFixed = feeFixed;
        this.feeVariable = feeVariable;
        }

        PriceStep(){
            
        }

        public String formatPriceStep() {
        //%.2f -> Fliesskommerzahl die auf 2 Stellen gerundet wird
        //\t -> Tab
        //%% Prozentzeichen
        String format = String.format("%.2f\t%.2f\t%.1f\t%.1%%", minPrice, maxPrice, feeFixed, feeVariable);
        return format;

        }

        public boolean collide(PriceStep step){

            if((this.maxPrice != 0 && step.maxPrice!= 0)==true){
                if(this.minPrice>=step.maxPrice){
                    return false;
                }
                if(this.maxPrice<=step.minPrice){
                    return false;
                }
                return true;
            }

            if(this.maxPrice == 0 && step.maxPrice == 0) return true;
            else
                return false;
        }
    }

}
