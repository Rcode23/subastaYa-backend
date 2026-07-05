package com.subastaYa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subastas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "precioBase", nullable = false)
    private BigDecimal precioBase;

    @Column(name = "precioActual")
    private BigDecimal precioActual;

    @Column(name = "pujaMinima", nullable = false)
    private BigDecimal pujaMinima;

    @Column(name = "imagenUrl")
    private String imagenUrl;

    @Column(name = "fechaInicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "fechaCreacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "numeroGuia")
    private String numeroGuia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSubasta estado;

    @ManyToOne
    @JoinColumn(name = "vendedorId", nullable = false)
    private Usuario vendedor;

    @ManyToOne
    @JoinColumn(name = "categoriaId", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "compradorGanadorId")
    private Usuario compradorGanador;

    @OneToMany(mappedBy = "subasta")
    @JsonIgnore
    private List<Puja> pujas;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.precioActual == null) {
            this.precioActual = this.precioBase;
        }
        if (this.estado == null) {
            this.estado = EstadoSubasta.ACTIVA;
        }
    }
}
