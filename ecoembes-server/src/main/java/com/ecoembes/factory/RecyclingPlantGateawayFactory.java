package com.ecoembes.factory;

import java.util.HashMap;
import java.util.Map;

import com.ecoembes.domain.RecyclingPlant;
import com.ecoembes.gateaway.IRecyclingPlantGateaway;
import com.ecoembes.gateaway.PlasSBGateaway;

public class RecyclingPlantGateawayFactory {

	private static final RecyclingPlantGateawayFactory recyclingPlantGateawayFactory = new RecyclingPlantGateawayFactory();

	// Gateways ya creados, por id de planta
	private final Map<Integer, IRecyclingPlantGateaway> gateways = new HashMap<>();

	private RecyclingPlantGateawayFactory() {
	}

	// Devuelve el gateway de la planta (lo crea la primera vez)
	public static IRecyclingPlantGateaway createGateway(RecyclingPlant plant) {
		return recyclingPlantGateawayFactory.getOrCreate(plant);
	}

	private synchronized IRecyclingPlantGateaway getOrCreate(RecyclingPlant plant) {
		IRecyclingPlantGateaway gateway = gateways.get(plant.getId());
		if (gateway == null) {
			gateway = build(plant);
			gateways.put(plant.getId(), gateway);
		}
		return gateway;
	}

	// Aquí se decide qué canal usa cada planta. Ajusta la regla a tu caso.
	private IRecyclingPlantGateaway build(RecyclingPlant plant) {
		switch (plant.getId()) {
		case 1:
			// Planta PlasSB: por API REST
			return new PlasSBGateaway();

		// case 2:
		//     // Planta por sockets
		//     return new TuGatewayDeSockets();

		default:
			throw new IllegalStateException("No gateway configured for plant " + plant.getId());
		}
	}
}
