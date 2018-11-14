import java.util.ArrayList;


//Class to track gift requests
public class GiftsTracker {
	
	private ArrayList<GiftRequest> requests;
	
	public class GiftRequest{
		
		private Player trader;
		private Player tradee;
		private double amount;
		
		public GiftRequest(Player trader, Player tradee, double amount) {
			this.trader = trader;
			this.tradee = tradee;
			this.amount = amount;
		}
		
		public Player getTrader() {
			return this.trader;
		}
		
		public double getAmount() {
			return this.amount;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof GiftRequest))
				return false;
			GiftRequest g = (GiftRequest) o;
			return g.tradee.equals(this.tradee);
		}
		  
	}
	
	public GiftsTracker() {
		this.requests = new ArrayList<GiftRequest>();
	}
	
	public boolean trackGift(Player trader, Player tradee, double amount) {
		GiftRequest newGift = new GiftRequest(trader, tradee, amount);
		if(!(this.hasOpenRequest(tradee))) {
			requests.add(newGift);
			return true;
		}
		return false;
	}
	
	public boolean hasOpenRequest(Player player) {
		boolean result = false;
		for(GiftRequest gf : this.requests) {
			if(gf.tradee.getName().equals(player.getName()))
				result = true;
		}
		return result;
	}
	
	public GiftRequest getRequest(Player tradee) {
		for(GiftRequest gf : this.requests) {
			if(gf.tradee.getName().equals(tradee.getName()))
				return gf;
		}
		return null;
	}
	
	public void close(GiftRequest request) {
		requests.remove(request);
		requests.trimToSize();
	}
}
