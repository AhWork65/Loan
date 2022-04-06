package com.heydari.loan;

import com.heydari.loan.exception.LoanInternalException;
import com.heydari.loan.model.*;
import com.heydari.loan.model.deposit.Deposit;
import com.heydari.loan.repository.LoanRepository;
import com.heydari.loan.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class CreateLoanTest {
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;


    @Mock
    private WebClient webClientMock;


    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    //==============================================================================
    @Test
    void create_Loan_Test_Happy_Scenario() throws LoanInternalException {
        LoanOperation loanOperation = new LoanOperation();
        loanOperation.setNumber("1");
        loanOperation.setType(LoanType.HOUSE_LOAN);
        loanOperation.setDepositNumber("12");
        loanOperation.setDeposit(new Deposit());
        loanOperation.setLoanPrice(new BigDecimal(10000000));
        loanOperation.setTotalInstallments(10);
        loanOperation.setLoanInterestRate(LoanInterestRate.FOUR_PERCENT);


        Deposit deposit = new Deposit();

        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(Matchers.anyString(),Matchers.anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Deposit.class)).thenReturn(Mono.just(deposit));

        when(loanRepository.save(Matchers.any())).thenReturn(loanOperation);
        Loan loan= loanService.createLoan(loanOperation);
        assertNotNull(loan.getNumber());
    }
    //==============================================================================
    @Test
    void create_Loan_Test_ByNull_Paramet() throws LoanInternalException {
        assertThrows(LoanInternalException.class,()-> loanService.createLoan(null));
    }

}
