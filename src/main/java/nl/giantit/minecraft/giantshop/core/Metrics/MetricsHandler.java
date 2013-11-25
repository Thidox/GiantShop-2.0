package nl.giantit.minecraft.giantshop.core.Metrics;

import nl.giantit.minecraft.giantshop.GiantShop;
import nl.giantit.minecraft.giantshop.core.config;
import nl.giantit.minecraft.giantshop.core.Metrics.Metrics.Graph;

import java.io.IOException;

public class MetricsHandler {

	public MetricsHandler(GiantShop plugin) {
		config conf = config.Obtain();
	    
		try {
			Metrics metrics = new Metrics(plugin);
			
			if(conf.getBoolean(plugin.getName() + ".metrics.send.database")) {
				Graph graph = metrics.createGraph("Database Engine");
				graph.addPlotter(new Metrics.Plotter(plugin.getDB().getEngine().getType().toString()) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}

			if(conf.getBoolean(plugin.getName() + ".metrics.send.updater")) {
				Graph graph = metrics.createGraph("Update warning");
				graph.addPlotter(new Metrics.Plotter((conf.getBoolean(plugin.getName() + ".Updater.checkForUpdates") ? "Enabled" : "Disabled")) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}

			if(conf.getBoolean(plugin.getName() + ".metrics.send.logging")) {
				Graph graph = metrics.createGraph("Use logging");
				graph.addPlotter(new Metrics.Plotter((conf.getBoolean(plugin.getName() + ".log.useLogging") ? "Yes" : "No")) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}

			if(conf.getBoolean(plugin.getName() + ".metrics.send.permEngine")) {
				Graph graph = metrics.createGraph("Permissions engine");
				graph.addPlotter(new Metrics.Plotter(plugin.getPermHandler().getEngineName()) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}

			if(conf.getBoolean(plugin.getName() + ".metrics.send.ecoEngine")) {
				Graph graph = metrics.createGraph("Economy engine");
				graph.addPlotter(new Metrics.Plotter(plugin.getEcoHandler().getEngineName()) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}

			if(conf.getBoolean(plugin.getName() + ".metrics.send.stock")) {
				Graph graph = metrics.createGraph("Using item stock");
				graph.addPlotter(new Metrics.Plotter((conf.getBoolean(plugin.getName() + ".stock.useStock") ? "Yes" : "No")) {
					
					@Override
					public int getValue() {
						return 1;
					}
				});
			}

			if(conf.getBoolean(plugin.getName() + ".metrics.send.gsl")) {
				Graph graph = metrics.createGraph("Using GiantShop Location");
				graph.addPlotter(new Metrics.Plotter((plugin.useLocation() ? "Yes" : "No")) {
					
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
