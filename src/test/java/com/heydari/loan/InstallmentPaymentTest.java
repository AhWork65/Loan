package com.heydari.loan;
import com.heydari.loan.exception.LoanInternalException;
import com.heydari.loan.model.*;
import com.heydari.loan.model.deposit.Deposit;
import com.heydari.loan.model.deposit.DepositOperationBase;
import com.heydari.loan.model.deposit.NullClass;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class InstallmentPaymentTest {
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
    private WebClient.RequestHeadersSpec requestHeadersSpecMockSec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMockSec;

    //==============================================================================
    @Test
    void installment_Payment_Test_Happy_Scenario() throws LoanInternalException {
        LoanInstallmentPayment loanInstallmentPayment = new LoanInstallmentPayment("1","2");
        Deposit deposit = new Deposit();
        deposit.setBalance(new BigDecimal(100000000));
        deposit.setId(1l);
        deposit.setNumber("1");

        when(loanRepository.findByNumber(Matchers.any())).thenAnswer(t->{
            Loan loan = new Loan();
            loan.setStatus(LoanStatus.OPEN);
            loan.setInstallmentsPrice(new BigDecimal(10000));
            loan.setDeposit(deposit);
            loan.setRemainingInstallments(10);
            return loan;
        });


        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(Matchers.anyString(),Matchers.anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Deposit.class)).thenReturn(Mono.just(deposit));



        NullClass nullClass = new NullClass();
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(Matchers.anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(Matchers.any())).thenReturn(requestHeadersSpecMockSec);
        when(requestHeadersSpecMockSec.retrieve()).thenReturn(responseSpecMockSec);
        when(responseSpecMockSec.bodyToMono( (NullClass.class) )).thenReturn(Mono.just(nullClass));
        Loan loan =  loanService.installmentPayment(loanInstallmentPayment);
        assertNotNull(loan);
    }
    //==============================================================================
    @Test
    void installment_Payment_Test_ByNull_Paramet() throws LoanInternalException {
        assertThrows(LoanInternalException.class,()-> loanService.installmentPayment(null));
    }

}
