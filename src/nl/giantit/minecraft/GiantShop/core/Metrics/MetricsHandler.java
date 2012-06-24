package nl.giantit.minecraft.GiantShop.core.Metrics;

import nl.giantit.minecraft.GiantShop.GiantShop;
import nl.giantit.minecraft.GiantShop.core.config;
import nl.giantit.minecraft.GiantShop.core.Database.Database;
import nl.giantit.minecraft.GiantShop.core.Metrics.Metrics.Graph;

import java.io.IOException;

public class MetricsHandler {

	public MetricsHandler(GiantShop plugin) {
		config conf = config.Obtain();
	    
		try {
			Metrics metrics = new Metrics(plugin);
			
			if(conf.getBoolean(plugin.getName() + ".metrics.send.database")) {
				Graph graph = metrics.createGraph("Database Engine");
				graph.addPlotter(new Metrics.Plotter(Database.Obtain().getType()) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			metrics.start();
		} catch (IOException e) {
			plugin.getLogger().warning("Failed to load metrics!");
		    if(conf.getBoolean(plugin.getName() + ".global.debug")) {
		    	e.printStackTrace();
		    }
		}
	}
}
