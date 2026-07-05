package com.subastaYa.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PujaDTO {
    private Long subastaId;
    private BigDecimal monto;
}
