package com.subastaYa.dto;

import lombok.Data;

@Data
public class PagoDTO {
    private Long subastaId;
    private String metodoPago;
    private String codigoTransaccion;
    private String urlComprobante;
}

