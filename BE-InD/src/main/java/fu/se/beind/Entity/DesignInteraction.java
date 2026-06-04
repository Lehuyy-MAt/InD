package fu.se.beind.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DesignInteractions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesignInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "DesignId")
    private Design design;

    private String actionType;

    private LocalDateTime createdAt;
}