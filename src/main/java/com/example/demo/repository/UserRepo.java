package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>{

	List<User> findByName(String name);
	
	@Query(value="from user select * where name like 'malicious%' "
			+ "and lowest_trust_level < 2", 
			nativeQuery=true)
	List<User> allMaliciousDetected();
	
	@Query(value="from user select * where name like 'malicious%' "
			+ "and lowest_trust_level = 2 "
			+ "and cancellations > 0", 
			nativeQuery=true)
	List<User> allMaliciousNotDetected();
	
	@Query(value="from user select * where name like 'malicious%' "
			+ "and cancellations > 0", 
			nativeQuery=true)
	List<User> allMaliciousUsers();
	
	@Query(value="from user select * where trust_reparations > 0 "
			+ "and (name not like 'malicious%')", 
			nativeQuery=true)
	List<User> allBenignFalsePositives();
	
	@Query(value="from user select * where lowest_trust_level = 0 "
			+ "and (name not like 'malicious%')", 
			nativeQuery=true)
	List<User> allBenignFalseUntrusted();
	
	@Query(value="from user select * where name not like 'malicious%'", 
			nativeQuery=true)
	List<User> allBenignUsers();
	
	@Query(value="from user select * where lowest_trust_level < 2", 
			nativeQuery=true)
	List<User> allUntrustedUsers();
	
}
