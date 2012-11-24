/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package loadTesting;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lisibauernhofer
 */
public class TestClient extends Thread{
    private String host;
    private int port;
    private int clientNr;
    private int auctionPerMin;
    private int auctionDuration;
    private int updateIntervalSec;
    private int bidsPerMin;
    private Socket socket = null;
    PrintWriter out = null;


    TestClient(String host, int port, int client, int auctionPerMin, int auctionDuration, int updateIntervalSec, 
            int bidsPerMin) {

        this.host = host;
        this.port = port;
        this.clientNr = client;
        this.auctionPerMin = auctionPerMin;
        this.auctionDuration = auctionDuration;
        this.updateIntervalSec = updateIntervalSec;
        this.bidsPerMin = bidsPerMin;



    }

    public void run(){
        try {
            socket = new Socket(host, port); //Socket wird erstellt fuer die Verbindung mir dem Server
            out = new PrintWriter(socket.getOutputStream(), true); //damit werden Daten an den Server uebergeben

        } catch (UnknownHostException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        login();

    }

    private void login() {
        String login = "!login client"+clientNr;
        out.println(login);
    }

}