package managementClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ManagementClient {

	/**
	 * Arg 0: Bindingname for AnalyticsServer
	 * Arg 1: BindingName for BillingServer
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			String analBind = args[0];;
			String billBind = args[1];
			String line = "";
			String userName = "";
			String userPwd = "";
			String startPrice = "";
			String endPrice = "";
			String fixedPrice = "";
			String variablePricePercent = "";
			String userBill = "";
			String filterRegex = "";
			int subscriptionId = 0;
			
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			
			while (true) {
				try {
					System.out.print(userName + "> ");
					line = stdin.readLine();
				} catch (IOException e) {
					// Close ressources?
					System.exit(-1);
				}
				String[] split = line.split(" ");
				
				/*
				* Start of Billing commands
				*/
				
				if (line.startsWith("!login ") && split.length == 3) {
					userName = split[1];
					userPwd = split[2];
					// Login to Billing Server
					
				} else if (line.equals("!steps") && split.length == 1) {
					// Call Pricing Steps from Billing Server
					
				} else if (line.startsWith("!addStep") && split.length == 5) {
					startPrice 			= split[1];
					endPrice   			= split[2];
					fixedPrice 			= split[3];
					variablePricePercent = split[4];
					// Add step to BillingServer
				} else if (line.startsWith("!removeStep") && split.length == 3) {
					startPrice = split[1];
					endPrice = split[2];
					
					// Call RemoveStep from Billing Server
				} else if (line.startsWith("!bill") && split.length == 2) {
					userBill = split[1];
					
					// Call Bill from Billing Server
				} else if (line.equals("!logout") && split.length == 1) {
					
					// Log out user from BillingServer
				/*
				* Start of Analytics commands
				*/
					
				} else if (line.equals("!subscribe") && split.length == 2) {
					filterRegex = split[1];
					// Subscribe to AnalyticsServer
				} else if (line.equals("!unsubscribe") && split.length == 2) {
					subscriptionId = Integer.parseInt(split[1]);
					// Unsubscribe to AnalyticsServer
				} else if (line.equals("!auto") && split.length == 1) {
					
					// Set mode to Auto
				} else if (line.equals("!hide") && split.length == 1) {
					
					// Set mode to Hide
				} else if (line.equals("!print") && split.length == 1) {
					
					// Print all buffered events
				} else {
					System.out.println("Command not recognized.");
				}
			}
		} else {
			System.out.println("Wrong argument count.");
		}

	}

}
