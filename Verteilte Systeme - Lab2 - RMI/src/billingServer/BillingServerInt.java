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
public interface BillingServerInt extends Remote{

    public BillingServerSecure login(String username, String password) throws RemoteException;

}
