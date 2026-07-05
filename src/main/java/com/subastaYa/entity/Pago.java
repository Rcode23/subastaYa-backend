package com.subastaYa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subastaId", nullable = false)
    private Subasta subasta;

    @ManyToOne
    @JoinColumn(name = "compradorId", nullable = false)
    private Usuario comprador;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private BigDecimal comision; // 5% de la plataforma

    @Column(nullable = false)
    private BigDecimal montoVendedor; // monto - comision

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @Column(name = "metodoPago")
    private String metodoPago; // YAPE, PLIN, TARJETA

    @Column(name = "codigoTransaccion")
    private String codigoTransaccion;

    @Column(name = "fechaPago")
    private LocalDateTime fechaPago;

    @Column(name = "fechaCreacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "urlComprobante")
    private String urlComprobante;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
        this.comision = this.monto.multiply(new java.math.BigDecimal("0.05"));
        this.montoVendedor = this.monto.subtract(this.comision);
    }
}