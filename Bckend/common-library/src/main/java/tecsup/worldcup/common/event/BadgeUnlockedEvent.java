package tecsup.worldcup.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeUnlockedEvent implements Serializable {

    private Long userId;
    private Long badgeId;
    private String badgeName;
    private LocalDateTime earnedAt;
}