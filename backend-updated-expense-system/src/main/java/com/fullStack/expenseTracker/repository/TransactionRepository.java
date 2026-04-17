package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(
            value = """
                    SELECT t.*
                    FROM "transaction" t
                    JOIN category c ON t.category_id = c.category_id
                    JOIN users u ON t.user_id = u.id
                    JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                    WHERE u.email = :email
                      AND tt.transaction_type_name ILIKE CONCAT('%', :transactionType, '%')
                      AND (
                            t.description ILIKE CONCAT('%', :searchKey, '%')
                            OR c.category_name ILIKE CONCAT('%', :searchKey, '%')
                          )
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM "transaction" t
                    JOIN category c ON t.category_id = c.category_id
                    JOIN users u ON t.user_id = u.id
                    JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                    WHERE u.email = :email
                      AND tt.transaction_type_name ILIKE CONCAT('%', :transactionType, '%')
                      AND (
                            t.description ILIKE CONCAT('%', :searchKey, '%')
                            OR c.category_name ILIKE CONCAT('%', :searchKey, '%')
                          )
                    """,
            nativeQuery = true
    )
    Page<Transaction> findByUser(@Param("email") String email,
                                 Pageable pageable,
                                 @Param("searchKey") String searchKey,
                                 @Param("transactionType") String transactionType);

    @Query(
            value = """
                    SELECT t.*
                    FROM "transaction" t
                    JOIN category c ON t.category_id = c.category_id
                    JOIN users u ON t.user_id = u.id
                    JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                    WHERE t.description ILIKE CONCAT('%', :searchKey, '%')
                       OR c.category_name ILIKE CONCAT('%', :searchKey, '%')
                       OR tt.transaction_type_name ILIKE CONCAT('%', :searchKey, '%')
                       OR u.email ILIKE CONCAT('%', :searchKey, '%')
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM "transaction" t
                    JOIN category c ON t.category_id = c.category_id
                    JOIN users u ON t.user_id = u.id
                    JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                    WHERE t.description ILIKE CONCAT('%', :searchKey, '%')
                       OR c.category_name ILIKE CONCAT('%', :searchKey, '%')
                       OR tt.transaction_type_name ILIKE CONCAT('%', :searchKey, '%')
                       OR u.email ILIKE CONCAT('%', :searchKey, '%')
                    """,
            nativeQuery = true
    )
    Page<Transaction> findAll(Pageable pageable, @Param("searchKey") String searchKey);

    @Query(value = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM "transaction" t
            JOIN users u ON t.user_id = u.id
            JOIN category c ON t.category_id = c.category_id
            JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
            WHERE u.id = :userId
              AND tt.transaction_type_id = :transactionTypeId
              AND EXTRACT(MONTH FROM t.date) = :month
              AND EXTRACT(YEAR FROM t.date) = :year
            """, nativeQuery = true)
    Double findTotalByUserAndTransactionType(@Param("userId") long userId,
                                             @Param("transactionTypeId") Integer transactionTypeId,
                                             @Param("month") int month,
                                             @Param("year") int year);

    @Query(value = """
            SELECT COUNT(*)
            FROM "transaction" t
            JOIN users u ON t.user_id = u.id
            WHERE u.id = :userId
              AND EXTRACT(MONTH FROM t.date) = :month
              AND EXTRACT(YEAR FROM t.date) = :year
            """, nativeQuery = true)
    Integer findTotalNoOfTransactionsByUser(@Param("userId") long userId,
                                            @Param("month") int month,
                                            @Param("year") int year);

    @Query(value = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM "transaction" t
            JOIN users u ON t.user_id = u.id
            JOIN category c ON t.category_id = c.category_id
            WHERE u.email = :email
              AND c.category_id = :categoryId
              AND EXTRACT(MONTH FROM t.date) = :month
              AND EXTRACT(YEAR FROM t.date) = :year
            """, nativeQuery = true)
    Double findTotalByUserAndCategory(@Param("email") String email,
                                      @Param("categoryId") int categoryId,
                                      @Param("month") int month,
                                      @Param("year") int year);

    @Query(value = """
            SELECT
                EXTRACT(MONTH FROM t.date) AS month,
                SUM(CASE WHEN tt.transaction_type_id = 1 THEN t.amount ELSE 0 END) AS income,
                SUM(CASE WHEN tt.transaction_type_id = 2 THEN t.amount ELSE 0 END) AS expense
            FROM "transaction" t
            JOIN users u ON t.user_id = u.id
            JOIN category c ON t.category_id = c.category_id
            JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
            WHERE u.email = :email
              AND t.date >= (CURRENT_DATE - INTERVAL '5 months')
            GROUP BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)
            ORDER BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)
            """, nativeQuery = true)
    List<Object[]> findMonthlySummaryByUser(@Param("email") String email);
}