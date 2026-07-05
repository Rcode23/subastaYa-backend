package com.subastaYa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pujas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "fechaPuja", nullable = false)
    private LocalDateTime fechaPuja;

    @ManyToOne
    @JoinColumn(name = "subastaId", nullable = false)
    private Subasta subasta;

    @ManyToOne
    @JoinColumn(name = "compradorId", nullable = false)
    private Usuario comprador;

    @PrePersist
    public void prePersist() {
        this.fechaPuja = LocalDateTime.now();
    }
}
