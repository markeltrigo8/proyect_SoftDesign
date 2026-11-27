package com.ecoembes.gateaway;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRecyclingPlantGateaway {
	public Optional<Integer> getAvailableCapacity(LocalDate date);
	public void notifyPlant(LocalDate date, int numDumpsters, int numContainers, List<Integer> listDumpstersID);
}
