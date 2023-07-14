package com.small.group.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.small.group.dto.RegionDTO;
import com.small.group.entity.Region;
import com.small.group.repository.RegionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

	private final RegionRepository regionRepository;
	
	/**
	 *  DTO TO ENTITY
	 */
	private Region dtoToEntity(RegionDTO dto) {
		Region entity = Region.builder()
				.regionName(dto.getRegionName())
				.build();
		return entity;
	}
	
	/**
	 *  ENTITY TO DTO
	 */
	private RegionDTO entityToDto(Region entity) {
		RegionDTO dto = RegionDTO.builder()
				.regionNo(entity.getRegionNo())
				.regionName(entity.getRegionName())
				.build();
		return dto;
	}
	

	/**
	 * ----------------------------------
	 * 			C / R / U / D
	 * ----------------------------------
	 */
	
	/**
	 *	지역 저장하는 함수
	 */
	@Override
	public Region insertRegion(RegionDTO regionData) {
		Region region = dtoToEntity(regionData);
		return regionRepository.save(region);
	}
	
	/**
	 *	지역 한 개 가져오는 함수
	 */
	@Override
	public RegionDTO readRegion(Integer regionNo) {
		Optional<Region> region = regionRepository.findById(regionNo);
		RegionDTO regionDTO = null;
		if(region.isPresent()) {
			regionDTO = entityToDto(region.get());
		}
		return regionDTO;
	}

	/**
	 *	지역 수정하는 함수
	 */
	@Override
	public Region updateRegion(RegionDTO regionData) {
		Optional<Region> data = regionRepository.findById(regionData.getRegionNo());
		if(data.isPresent()) {
			Region targetEntity = data.get();
			targetEntity.setRegionName(regionData.getRegionName());
			
			return regionRepository.save(targetEntity);
		}
		return null;
	}

	/**
	 *	지역 삭제하는 함수
	 */
	@Override
	public Boolean deleteRegion(Integer regionNo) {
		Optional<Region> data = regionRepository.findById(regionNo);
		if(data.isPresent()) {
			regionRepository.delete(data.get());
			return true;
		}
		return false;
	}

	/**
	 *	지역 리스트를 가져오는 함수
	 */
	@Override
	public List<RegionDTO> getRegionList() {
		List<Region> regionList = regionRepository.findAll();
		List<RegionDTO> regionDTOList = regionList
				.stream().map(entity -> entityToDto(entity)).collect(Collectors.toList());
		return regionDTOList;
	}
}
