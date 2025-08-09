package com.ferreteria.app_web_ferreteria.repository;

import com.ferreteria.app_web_ferreteria.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    @Query(value = "SELECT * FROM producto WHERE nombre LIKE CONCAT('%', :filtro, '%') OR color LIKE CONCAT('%', :filtro, '%')", nativeQuery = true)
    List<Producto> buscarProductoPorFiltro(@Param("filtro") String filtro);
    List<Producto> findByEstaActivoOrderByIdDesc(Boolean estaActivo);

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stockTotal <= p.stockMinimo")
    Long countProductosStockMinimo();

    @Query("SELECT p FROM Producto p WHERE p.stockTotal <= p.stockMinimo")
    Page<Producto> findProductosStockMinimo(Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Producto p WHERE " +
           "LOWER(p.nombre) = LOWER(:nombre) AND " +
           "LOWER(p.color) = LOWER(:color) AND " +
           "p.marca.id = :marcaId AND " +
           "p.tipoPintura.id = :tipoId AND " +
           "(:excludeId IS NULL OR p.id != :excludeId)")
    boolean existsByNombreAndColorAndMarcaAndTipo(
        @Param("nombre") String nombre,
        @Param("color") String color,
        @Param("marcaId") Long marcaId,
        @Param("tipoId") Long tipoId,
        @Param("excludeId") Long excludeId
    );
} 