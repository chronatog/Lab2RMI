package managementClient;

public class ManagementClient {

	/**
	 * Arg 0: Bindingname for AnalyticsServer
	 * Arg 1: BindingName for BillingServer
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			String analBind = "";
			String billBind = "";
			
			analBind = args[0];
			billBind = args[1];
			
		} else {
			System.out.println("Wrong argument count.");
		}

	}

}
