package com.heydari.loan;

import com.heydari.loan.exception.LoanInternalException;
import com.heydari.loan.model.BaseLoan;
import com.heydari.loan.model.LoanInterestRate;
import com.heydari.loan.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class InstallmentcalcTest {

    @InjectMocks
    private LoanService loanService;

//==============================================================================
@Test
void Installment_Calc_Happy_Scenario() throws LoanInternalException {
    BaseLoan baseLoan = new BaseLoan(10, null, null,  new BigDecimal(1000000), LoanInterestRate.EIGHTEEN_PERCENT);
    BigDecimal returnVal = loanService.installmentCalc(baseLoan);
    assertEquals(returnVal.equals(new BigDecimal(100075)),true);
}
//==============================================================================
    @Test
    void Installment_Calc_ByNull_Paramet() throws LoanInternalException {
        assertThrows(LoanInternalException.class,()-> loanService.installmentCalc(null));
    }

}
