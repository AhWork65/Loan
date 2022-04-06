package com.heydari.loan.controller;

import com.heydari.loan.exception.LoanBadRequestException;
import com.heydari.loan.exception.LoanInternalException;
import com.heydari.loan.model.BaseLoan;
import com.heydari.loan.model.Loan;
import com.heydari.loan.model.LoanInstallmentPayment;
import com.heydari.loan.model.LoanOperation;
import com.heydari.loan.model.deposit.Deposit;
import com.heydari.loan.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/loanservice")
public class LoanController {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoanController.class);

    @Autowired
    private  LoanService loanService;

//======================================================================================

    @PostMapping("/installmentcalc")
    public BigDecimal Installmentcalc (@RequestBody BaseLoan baseLoan){
        LOGGER.info("Installmentcalc InPut paramet is :{}", baseLoan.toString());
        BigDecimal retInstallment = null;

        try {
            retInstallment = loanService.installmentCalc(baseLoan);
        } catch (LoanInternalException e) {
            LOGGER.error("Installmentcalc Exception is :{}",e.getMessage());
            throw new LoanBadRequestException(e.getMessage());
        }

        LOGGER.info("Installmentcalc return is :{}", retInstallment.toString());
        return retInstallment;
    }
//======================================================================================

    @PostMapping("/create")
    public Loan createLoan (@RequestBody LoanOperation loanOperation){
        LOGGER.info("createLoan InPut paramet is :{}", loanOperation.toString());
        Loan loan = null;

        try {
            loan = loanService.createLoan(loanOperation);
        } catch (LoanInternalException e) {
            LOGGER.error("createLoan Exception is :{}",e.getMessage());
            throw new LoanBadRequestException(e.getMessage());
        }

        LOGGER.info("createLoan return is :{}", loan.toString());
        return loan;
    }
//======================================================================================

    @PostMapping("/payment")
    public void installmentPayment (@RequestBody LoanInstallmentPayment loanInstallmentPayment){
        LOGGER.info("installmentPayment InPut paramet is :{}", loanInstallmentPayment.toString());

        try {
         loanService.installmentPayment(loanInstallmentPayment);
    } catch (LoanInternalException e) {
        LOGGER.error("installmentPayment Exception is :{}",e.getMessage());
        throw new LoanBadRequestException(e.getMessage());
    }
}
//======================================================================================
    @GetMapping("/getallLoan")
    public List<Loan> getAllLoan (){
        List<Loan> loanList = loanService.getAllLoan();
        LOGGER.info("getAllLoan return is :{}", loanList.size());
        return loanList;
    }
//======================================================================================
    @PostMapping("/getallloanbydeposit")
    public List<Loan> getAllLoanByDeposit (@RequestBody Deposit deposit){
        List<Loan> loanList = loanService.getAllLoanByDeposit(deposit);
        LOGGER.info("getAllLoanByDeposit return is :{}", loanList.size());
        return  loanList;
}

}

