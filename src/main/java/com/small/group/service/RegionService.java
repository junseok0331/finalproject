package com.small.group.service;

import java.util.List;

import com.small.group.dto.RegionDTO;
import com.small.group.entity.Region;

public interface RegionService {
	Region insertRegion(RegionDTO regionData);
	RegionDTO readRegion(Integer regionNo);
	Region updateRegion(RegionDTO regionData);
    Boolean deleteRegion(Integer regionNo);
    List<RegionDTO> getRegionList();
}
