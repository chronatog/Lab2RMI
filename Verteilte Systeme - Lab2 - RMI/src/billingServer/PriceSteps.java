/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

import billingServer.PriceSteps.PriceStep;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author lisibauernhofer
 */
public class PriceSteps implements Serializable{

    private ArrayList<PriceStep> PriceSteps;

    public PriceSteps() {
        PriceSteps = new ArrayList<PriceStep>();

    }


    

    public void addPriceStep(double startPrice, double endPrice, double fixedPrice, double variablePricePercent) throws RemoteException {
        if(startPrice<0 || endPrice<0 || fixedPrice<0 || variablePricePercent<0){
            throw new RemoteException("Negative values are not allowed");
        }

        PriceStep newPS = new PriceStep(startPrice, endPrice,fixedPrice,variablePricePercent);

        for (PriceStep helpPS : PriceSteps){
            if(newPS.collide(helpPS)== true){
                throw new RemoteException("The price interval collides with an existing price step");
            }
        }
        this.PriceSteps.add(newPS);

    }

    void deleteStep(double startPrice, double endPrice) throws RemoteException {
        for (int i = 0; i< PriceSteps.size(); i++){
            if(PriceSteps.get(i).minPrice == startPrice && PriceSteps.get(i).maxPrice == endPrice){
                PriceSteps.remove(i);
            } else throw new RemoteException("The price step does not exist");

        }
    }

     

     public double getFeeFixByPrice(double price){
        for(PriceStep helpPS: PriceSteps){
            if(price > helpPS.getMinPrice()&&price <= helpPS.getMaxPrice()){
                return helpPS.getFeeFixed();
            }
        }  return 0;
     }

     public double getFeeVariableByPrice(double price){
        for(PriceStep helpPS: PriceSteps){
            if(price > helpPS.getMinPrice()&&price <= helpPS.getMaxPrice()){
                return helpPS.getFeeVariable();
            }
        }  return 0;
     }



    
    @Override
    public String toString(){
        String liste = "";
        String header = "Min_Price\tMax_Price\tFee_Fixed\tFee_variable";

        for(PriceStep pricestep : PriceSteps){

            liste += pricestep.formatPriceStep() +"\n";


        }
        return header + "\n"+ liste;
    }





    public class PriceStep implements Serializable {


        private double minPrice;
        private double maxPrice;
        private double feeFixed;
        private double feeVariable;

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
        return String.format(" %.2f\t    %.2f\t    %.1f\t     %.1f%%",minPrice, maxPrice, feeFixed, feeVariable);


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
        
       




    public double getFeeFixed(){
        return this.feeFixed;
    }

    public double getFeeVariable(){
        return this.feeVariable;
    }

    public double getMaxPrice(){
        return this.maxPrice;
    }

    public double getMinPrice(){
        return this.minPrice;
    }

        
    }



}
