package com.ecoembes.gateaway;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PlasSBGateaway implements IRecyclingPlantGateaway {

	private  String baseUrl;

	
	@Override
	public Optional<Integer> getAvailableCapacity(LocalDate date) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void notifyPlant(LocalDate date, int numDumpsters, int numContainers, List<String> listDumpstersID) {
		// TODO Auto-generated method stub

	}

}
