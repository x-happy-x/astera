package ru.astera.backend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.astera.backend.entity.Equipment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    /**
     * Находит совместимые пары "котёл+горелка" под требуемую мощность и топливо.
     * Возвращаем проекцию с ценами, DN и ключом совместимости.
     * Сортировка по суммарной цене пары по возрастанию.
     */
    @Query(
            value = """
                    SELECT 
                      b.id           AS boilerId,
                      br.id          AS burnerId,
                      b.price        AS boilerPrice,
                      br.price       AS burnerPrice,
                      b.dn_size      AS dnSize,
                      b.connection_key AS connectionKey,
                      (b.price + br.price) AS pairPrice
                    FROM equipment b
                    JOIN equipment br
                      ON br.category = 'burner' 
                     AND br.active
                     AND br.fuel_type = :fuel
                     AND br.connection_key = b.connection_key
                    WHERE b.category = 'boiler'
                      AND b.active
                      AND :power BETWEEN COALESCE(b.power_min_kw, 0) AND COALESCE(b.power_max_kw, 999999999)
                      AND :power BETWEEN COALESCE(br.power_min_kw, 0) AND COALESCE(br.power_max_kw, 999999999)
                    ORDER BY (b.price + br.price) ASC
                    """,
            nativeQuery = true
    )
    List<BoilerBurnerPair> findBoilerBurnerPairs(@Param("power") BigDecimal power,
                                                 @Param("fuel") String fuel,
                                                 Pageable limit);

    /**
     * Насос: по требуемому расходу (м³/ч)
     */
    @Query(
            value = """
                    SELECT * FROM equipment
                     WHERE category = 'pump' AND active
                       AND :flow BETWEEN COALESCE(flow_min_m3h,0) AND COALESCE(flow_max_m3h, 999999999)
                     ORDER BY price ASC
                     LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Equipment> findCheapestPump(@Param("flow") BigDecimal flow);

    /**
     * Арматура: по DN
     */
    @Query(
            value = """
                    SELECT * FROM equipment
                     WHERE category = 'valve' AND active
                       AND dn_size = :dn
                     ORDER BY price ASC
                     LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Equipment> findCheapestValve(@Param("dn") Integer dn);

    /**
     * Расходомер: по DN
     */
    @Query(
            value = """
                    SELECT * FROM equipment
                     WHERE category = 'flowmeter' AND active
                       AND dn_size = :dn
                     ORDER BY price ASC
                     LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Equipment> findCheapestFlowmeter(@Param("dn") Integer dn);

    /**
     * Автоматика: просто самый дешёвый элемент
     */
    @Query(
            value = """
                    SELECT * FROM equipment
                     WHERE category = 'automation' AND active
                     ORDER BY price ASC
                     LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Equipment> findCheapestAutomation();

    interface BoilerBurnerPair {
        UUID getBoilerId();

        UUID getBurnerId();

        BigDecimal getBoilerPrice();

        BigDecimal getBurnerPrice();

        Integer getDnSize();

        String getConnectionKey();

        BigDecimal getPairPrice();
    }
}