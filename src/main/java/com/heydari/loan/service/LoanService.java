package com.heydari.loan.service;

import com.heydari.loan.controller.LoanController;
import com.heydari.loan.exception.LoanInternalException;
import com.heydari.loan.model.*;
import com.heydari.loan.model.deposit.Deposit;
import com.heydari.loan.model.deposit.DepositOperationBase;
import com.heydari.loan.model.deposit.NullClass;
import com.heydari.loan.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LoanController.class);

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private WebClient webClient;



    @Autowired
    private WebClient.Builder webClientBuilder;

//==============================================================================

    private boolean validBaseLoan(BaseLoan baseLoan) {

        if (baseLoan == null)
            return false;

        if (baseLoan.getLoanPrice() == null)
            return false;

        if (baseLoan.getTotalInstallments() == null)
            return false;

        if (baseLoan.getLoanInterestRate() == null)
            return false;

        if (baseLoan.getLoanPrice().compareTo(new BigDecimal(1)) == -1)
            return false;

        if (baseLoan.getTotalInstallments() < 1)
            return false;

        return true;
    }

    //==============================================================================
    private boolean validLoan(Loan loan) {
        if (!validBaseLoan((BaseLoan) loan))
            return false;

        if (loan.getType() == null)
            return false;

        if (loan.getDeposit() == null)
            return false;

        return true;
    }

    //==============================================================================
    public BigDecimal installmentCalc(BaseLoan installment) throws LoanInternalException {
        LOGGER.debug("Installmentcalc InPut Paramet is:{}",(installment == null) ? " null ":installment.toString());

        if (!validBaseLoan(installment)) {
            LOGGER.error("Installmentcalc Not Valid Parameters...");
            throw new LoanInternalException("Bad Parameters...");
        }

        Float interestRate = 0f;

        if (installment.getLoanInterestRate().equals(LoanInterestRate.FOUR_PERCENT))
            interestRate = 0.04f;

        if (installment.getLoanInterestRate().equals(LoanInterestRate.EIGHTEEN_PERCENT))
            interestRate = 0.18f;

        if (installment.getLoanInterestRate().equals(LoanInterestRate.TWENTY_FOUR_PERCENT))
            interestRate = 0.24f;

        Float interest = (installment.getLoanPrice().floatValue()) * interestRate * (installment.getTotalInstallments());
        interest = interest / 2400f;

        interest = (interest + (installment.getLoanPrice().floatValue())) / (installment.getTotalInstallments());

        LOGGER.debug("Installmentcalc return value is:{}",interest);
        return new BigDecimal(interest);
    }

    //==============================================================================
    @Transactional(rollbackFor = {Exception.class})

    public Loan createLoan(LoanOperation loanOperation) throws LoanInternalException {

        LOGGER.debug("createLoan InPut Paramet is:{}",(loanOperation == null) ? " null ":loanOperation.toString());

        if (!validLoan(loanOperation)) {
            LOGGER.error("createLoan Not Valid Parameters...");
            throw new LoanInternalException("Bad Parameters...");
        }

        Deposit deposit = getDepositByNumber(loanOperation.getDepositNumber());

        if (deposit == null) {
            LOGGER.error("createLoan Deposit Not Found...");
            throw new LoanInternalException("Deposit Not Found...");
        }

        Calendar calendar = Calendar.getInstance();
        loanOperation.setOpeningDate(calendar.getTime());
        loanOperation.setStatus(LoanStatus.OPEN);
        loanOperation.setDeposit(deposit);
        loanOperation.setInstallmentsPrice(installmentCalc(loanOperation));

        LOGGER.debug("createLoan : loan return value is:{}",loanOperation.toString());
        return loanRepository.save((Loan) loanOperation);
    }
    //==============================================================================
    public Deposit getDepositByNumber(String number) {
        LOGGER.debug("getDepositByNumber Service  InPut Paramet is:{}",number);
        Deposit depositList = webClient.
                get()
                .uri("http://127.0.0.1:8091/depositservice/getdepositbynumber/{depositNumber}", number)
                .retrieve()
                .bodyToMono(Deposit.class)
                .block();

        LOGGER.debug("getDepositByNumber return is:{}",depositList.toString());

        return depositList;
    }

    //==============================================================================
    public Loan installmentPayment(LoanInstallmentPayment loanInstallmentPayment) throws LoanInternalException {
        LOGGER.debug("installmentPayment Service  InPut Paramet is:{}",(loanInstallmentPayment == null) ? " null ":loanInstallmentPayment.toString());

        if (loanInstallmentPayment == null) {
            LOGGER.error("installmentPayment  Is Null");
            throw new LoanInternalException("Bad Parameters...");
        }

        if (loanInstallmentPayment.getLoanNumber() == null) {
            LOGGER.error("installmentPayment Loan number  Is Null");
            throw new LoanInternalException("Bad Parameters...");
        }

        if (loanInstallmentPayment.getDepositNumber() == null) {
            LOGGER.error("installmentPayment Deposit number  Is Null");
            throw new LoanInternalException("Bad Parameters...");
        }

        Loan loan = loanRepository.findByNumber(loanInstallmentPayment.getLoanNumber());

        if (loan == null){
            LOGGER.error("Loan Not Found...");
            throw new LoanInternalException("Loan Not Found...");
        }

        Deposit deposit = getDepositByNumber(loanInstallmentPayment.getDepositNumber());

        if (deposit == null) {
            LOGGER.error("Deposit Not Found...");
            throw new LoanInternalException("Deposit Not Found...");
        }

        if (! loan.getDeposit().equals(deposit)){
            LOGGER.error("Deposit paramet Not equals Loans deposit");
            throw new LoanInternalException("Deposit paramet Not equals Loans deposit...");
        }


        if (loan.getStatus().equals(LoanStatus.CLOSE)) {
            LOGGER.error("Loan is Closed");
            throw new LoanInternalException("Loan is Closed...");
        }

        if (deposit.getBalance().compareTo(loan.getInstallmentsPrice()) < 0){
            LOGGER.error("No Deposit has balance");
            throw new LoanInternalException("No Deposit has balance...");
        }


        DepositOperationBase depositOperationBase = new DepositOperationBase();
        depositOperationBase.setDeposit(deposit);
        depositOperationBase.setPrice(loan.getInstallmentsPrice());

        try {
            withdrawDeposit(depositOperationBase);
        } catch (WebClientException e) {
            LOGGER.error("withdraw Deposit has Exception is : {}", e.getMessage());
            throw new LoanInternalException("withdraw Deposit has Exception ...", e);
        }

        loan.setRemainingInstallments(loan.getRemainingInstallments() - 1);
        if (loan.getRemainingInstallments() == 0)
            loan.setStatus(LoanStatus.CLOSE);
        else
            loan.setStatus(LoanStatus.PAYING);

        loanRepository.save(loan);
        LOGGER.debug("installmentPayment   Save Loan is:{}", loan.toString());
        return loan;
    }

    //======================================================================================
    public Loan getLoanById(Long id) throws LoanInternalException {
        LOGGER.debug("getLoanById paramet is :{}", id.toString());
        Optional<Loan> loanOptional;
        loanOptional = loanRepository.findById(id);

        loanOptional.orElseThrow(() ->{LOGGER.debug("Loan Not Found");
            return new LoanInternalException("Loan Not Found...");});

        LOGGER.debug("getLoanById Service  Save Loan is:{}", loanOptional.toString());
        return loanOptional.orElse(null);
    }

    //======================================================================================
    public void withdrawDeposit(DepositOperationBase depositOperationBase) {
        LOGGER.debug("withdrawDeposit Service  InPut Paramet is:{}",(depositOperationBase == null) ? " null ":depositOperationBase.toString());
        webClient.
                post()
                .uri("http://127.0.0.1:8091/depositservice/withdraw")
                .bodyValue(depositOperationBase)
                .retrieve()
                .bodyToMono(NullClass.class)
                .block();
    }

    //======================================================================================
    public List<Loan> getAllLoan() {
       return loanRepository.findAll();
    }

    //======================================================================================
    public List<Loan> getAllLoanByDeposit(Deposit deposit) {
        LOGGER.debug("getAllLoanByDeposit  InPut Paramet is:{}",(deposit == null) ? " null ":deposit.toString());
        return loanRepository.findAllByDeposit(deposit);
    }
}
