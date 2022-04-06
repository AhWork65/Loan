package com.heydari.loan.model.deposit;

import com.heydari.loan.model.Loan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode

public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String title;
    private DepositStatus status;
    private DepositType type;
    private DepositCurrency currency;
    private BigDecimal balance;
    @Temporal(TemporalType.DATE)
    private Date openDate;
    @Temporal(TemporalType.DATE)
    private Date closeDate;
    @OneToMany(mappedBy = "deposit")
    private List<Loan> loan;
}
