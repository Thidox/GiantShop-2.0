package nl.giantit.minecraft.GiantShop.core.Eco;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.Eco.Engines.*;

/**
 *
 * @author Giant
 */
public class Eco {
	
	private iEco Engine = null;
	private GiantShop plugin;
	
	public enum Engines {
		AEco,
		CraftConomy,
		CurrencyCore,
		EconXP,
		Essentials,
		McMoney,
		MineConomy,
		MultiCurrency,
		BOSE5,
		BOSE6,
		eWallet,
		ic3o,
		iConomy4,
		iConomy5,
		iConomy6
	}
	
	private boolean packageExists(String...Packages) {
		try{
			for(String pckg : Packages) {
				Class.forName(pckg);
			}
			return true;
		}catch(ClassNotFoundException e) {
			return false;
		}
	}
	
	public Eco(GiantShop plugin) {
		this.plugin = plugin;
		
		if(packageExists("org.neocraft.AEco.AEco")) {
			//AEco
			Engine = new AEco_Engine(this.plugin);
		}else if(packageExists("me.greatman.Craftconomy.Craftconomy")) {
			//CraftConomy
			
		}else if(packageExists("is.currency.Currency")) {
			//Currency
			
		}else if(packageExists("ca.agnate.EconXP.EconXP")) {
			//EconXP
			
		}else if(packageExists("com.earth2me.essentials.api.Economy", "com.earth2me.essentials.api.NoLoanPermittedException", "com.earth2me.essentials.api.UserDoesNotExistException")) {
			//Essentials
			Engine = new Essentials_Engine(this.plugin);
		}else if(packageExists("boardinggamer.mcmoney.McMoneyAPI")) {
			//McMoney
			
		}else if(packageExists("me.mjolnir.mineconomy.MineConomy")) {
			//MineConomy
			
		}else if(packageExists("me.ashtheking.currency.Currency", "me.ashtheking.currency.CurrencyList")) {
			//MultiCurrency
			
		}else if(packageExists("cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandManager")) {
			//BOSE6
			Engine = new bose6_Engine(this.plugin);
		}else if(packageExists("cosine.boseconomy.BOSEconomy", "cosine.boseconomy.CommandHandler")) {
			//BOSE7
			Engine = new bose7_Engine(this.plugin);
		}else if(packageExists("me.ethan.eWallet.ECO")) {
			//eWallet
			
		}else if(packageExists("me.ic3d.eco.ECO")) {
			//ic3o
			
		}else if(packageExists("com.nijiko.coelho.iConomy.iConomy", "com.nijiko.coelho.iConomy.system.Account")) {
			//ic4
			Engine = new ic4_Engine(this.plugin);
		}else if(packageExists("com.iConomy.iConomy", "com.iConomy.system.Account", "com.iConomy.system.Holdings")) {
			//ic5
			Engine = new ic5_Engine(this.plugin);
		}else if(packageExists("com.iCo6.iConomy")) {
			//ic6
			Engine = new ic6_Engine(this.plugin);
		}
	}
	
	public boolean isLoaded() {
		return (this.Engine != null && this.Engine.isLoaded());
	}
	
	public iEco getEngine() {
		return this.Engine;
	}
}
