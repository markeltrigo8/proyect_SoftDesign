package com.ecoembes.gateaway;

import java.time.LocalDate;
import com.ecoembes.gateaway.PlantCommunicationException;
import java.util.List;
import java.util.Optional;

public interface IRecyclingPlantGateaway {
	public Optional<Integer> getAvailableCapacity(LocalDate date);
	public void notifyPlant(LocalDate date, int numDumpsters, int numContainers, List<String> listDumpstersID);
}
