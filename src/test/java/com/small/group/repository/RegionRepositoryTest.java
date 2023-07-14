package com.small.group.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.Commit;

import com.small.group.entity.Region;

@SpringBootTest
@EnableJpaAuditing
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RegionRepositoryTest {
	
	@Autowired
	private RegionRepository regionRepository;
	
	//@Test
	//@Commit
	public void test() {
		String regionList[] = {"수원시", "성남시","고양시","용인시","부천시","안산시","안양시","남양주시","화성시","평택시","의정부시","시흥시","파주시","김포시","광명시","군포시","이천시","양주시",
							   "오산시","구리시","안성시","포천시","의왕시","여주시","동두천시","과천시","광주시","여주시","파주시"};
		for(String regionStr : regionList) {
			Region region = Region.builder()
					.regionName(regionStr)
					.build();
			regionRepository.save(region);
		}
		
	}

}
