package com.ecoembes.gateaway;

import java.time.LocalDate;
import com.ecoembes.gateaway.PlantCommunicationException;
import java.util.List;
import java.util.Optional;

public class ContSocketGateaway implements IRecyclingPlantGateaway {
	
	private int port;
	private String host;

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
