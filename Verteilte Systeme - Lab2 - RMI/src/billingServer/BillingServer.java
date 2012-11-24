/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package billingServer;

import java.rmi.*;

/**
 *
 * @author lisibauernhofer
 */
public interface BillingServer extends Remote{

     BillingServerSecure login(String username, String password) throws RemoteException;

}

