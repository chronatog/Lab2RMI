/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.rmi.*;
import java.rmi.server.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lisibauernhofer
 */
public class BillingServerImpl implements BillingServer{
    

    //
   @Override
   public BillingServerSecure login(String username, String password) throws RemoteException{

       String psw = readProperties(username);
       String passwordhash = pwtoMD5(password);

       if(psw.equals(passwordhash)){
           //sollte loggerausgabe sein!!!
           System.out.println("Successfully logged in as "+username);
           BillingServerSecure bss = new BillingServerSecureImpl();

           return bss;
       } else {
           System.out.println("Wrong passwort");
       }

       if(passwordhash == null){
           System.out.println("User does not exist!");
           return null;
       }



       return null;
   }

   private String readProperties(String username){
        InputStream in = ClassLoader.getSystemResourceAsStream("user.properties");
        if(in!=null){

            Properties props = new Properties();
            try{
                try {
                    props.load(in);
                } catch (IOException ex) {
                    //Logger.getLogger(BillingServerMain.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error:Loding Properties-File");
                }

                String psw = props.getProperty(username);
                return psw;
           }finally{
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(BillingServerMain.class.getName()).log(Level.SEVERE, null, ex);
                }

           }
        }else{
            System.out.println("Error: Properties-File not found!");
            return null;
        }
     }

   private String pwtoMD5(String pw){
        try {
            byte[] byteOfPw = pw.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(byteOfPw);
            String pwtohash = String.format("%1$032x", new BigInteger(1, thedigest));
            return pwtohash;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BillingServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(BillingServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; 

   }

   

}
