package nl.trifork.coins.version.query;


import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<VersionEntity, String> {
}