package com.subastaYa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubastaDTO {
    private String titulo;
    private String descripcion;
    private BigDecimal precioBase;
    private BigDecimal pujaMinima;
    private String imagenUrl;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long categoriaId;
}
