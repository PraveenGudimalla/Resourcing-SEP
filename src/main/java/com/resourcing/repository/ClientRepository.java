/* ======================
 * Author : Nikhil 
 * ======================*/

package com.resourcing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.resourcing.beans.Client;
import com.resourcing.beans.User;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
	@Query("select u from Client u WHERE lower(u.email)=lower(?1) AND u.password=?2")
	public Client findByUserNameIgnoreCaseAndPassword(String email, String password);

	@Query("select a from Client a WHERE lower(a.email)=lower(?1)")
	public Client findByEmail(String email);
	
	@Query("select a from Client a WHERE a.user=?1")
	List<Client> clientListByUser(User user);

}
