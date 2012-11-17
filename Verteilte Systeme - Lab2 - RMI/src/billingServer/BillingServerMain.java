/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author lisibauernhofer
 */
public class BillingServerMain {

    /**
     * @param args the command line arguments
     */
    private static String bindingName = "";
    private static String registryHost = "";
    private static int registryPort = 0;

    public static void main(String[] args) {
        if(args.length==1){

            //args 0 = binging Name

            bindingName = args[0];
            readProperties();
            BillingServerImpl bs = new BillingServerImpl();
            try {
                BillingServerImpl bsexport = (BillingServerImpl) UnicastRemoteObject.exportObject(bs, registryPort);
                Registry registry;

                registry = LocateRegistry.createRegistry(registryPort);
                registry.rebind(bindingName, bsexport);

            } catch (RemoteException ex) {
                //Logger.getLogger(BillingServerMain.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error: Binding to the Registry!");
            }
        }else{
            System.out.println("Wrong argument count.");
        }
    }
    
    private static void readProperties(){
        InputStream in = ClassLoader.getSystemResourceAsStream("registry.properties"); 
        if(in!=null){

            Properties props = new Properties();
            try{
                try {
                    props.load(in);
                } catch (IOException ex) {
                    //Logger.getLogger(BillingServerMain.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error:Loding Properties-File");
                }
        
                registryHost = props.getProperty("registry.host");
                registryPort = Integer.parseInt(props.getProperty("registry.port"));
           }finally{     
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(BillingServerMain.class.getName()).log(Level.SEVERE, null, ex);
                }
        
           }
        }else{
            System.out.println("Error: Properties-File not found!");
        }
     }


}
