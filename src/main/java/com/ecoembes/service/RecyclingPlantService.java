package com.ecoembes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ecoembes.domain.RecyclingPlant;

@Service
public class RecyclingPlantService {

	private Map<String, RecyclingPlant> recyclingPlantRepository = new ConcurrentHashMap<>();

	public void addRecyclingPlant(RecyclingPlant plant) {
		recyclingPlantRepository.put(plant.getId(), plant);
	}

	public Optional<RecyclingPlant> getRecyclingPlant(String id) {
		return Optional.ofNullable(recyclingPlantRepository.get(id));
	}

	public List<RecyclingPlant> getAllRecyclingPlants() {
		return new ArrayList<>(recyclingPlantRepository.values());
	}
}