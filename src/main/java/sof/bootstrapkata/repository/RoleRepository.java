package sof.bootstrapkata.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sof.bootstrapkata.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

}
