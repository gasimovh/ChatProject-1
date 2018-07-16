package ChatProject;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, String> {
    Channel findByName(String name);
}
