package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.TransactionsMonthlySummaryDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalByTransactionTypeAndUser(Long userId, int transactionTypeId, int month, int year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionRepository.findTotalByUserAndTransactionType(userId, transactionTypeId, month, year)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactionsByUser(Long userId, int month, int year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionRepository.findTotalNoOfTransactionsByUser(userId, month, year)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalExpenseByCategoryAndUser(String email, int categoryId, int month, int year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionRepository.findTotalByUserAndCategory(email, categoryId, month, year)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(String email) {
        List<Object[]> result = transactionRepository.findMonthlySummaryByUser(email);

        List<TransactionsMonthlySummaryDto> transactionsMonthlySummary = result.stream()
                .map(data -> {
                    int month = data[0] != null ? ((Number) data[0]).intValue() : 0;
                    double income = data[1] != null ? ((Number) data[1]).doubleValue() : 0.0;
                    double expense = data[2] != null ? ((Number) data[2]).doubleValue() : 0.0;

                    return new TransactionsMonthlySummaryDto(month, income, expense);
                })
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionsMonthlySummary
                )
        );
    }
}