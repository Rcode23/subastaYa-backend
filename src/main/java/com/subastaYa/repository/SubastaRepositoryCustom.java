package com.subastaYa.repository;

import com.subastaYa.entity.Subasta;
import java.util.List;

public interface SubastaRepositoryCustom {
    List<Subasta> buscarConFiltros(String keyword, Long categoriaId, String estado);
    List<Subasta> findTopSubastasPorPujas(int limit);
}