package wkallil.card.creditCardManager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.card.creditCardManager.dtos.MonthlyChargeDTO;
import wkallil.card.creditCardManager.dtos.OwnerSummaryDTO;
import wkallil.card.creditCardManager.models.Owner;
import wkallil.card.creditCardManager.repositories.EmailNotificationRepository;
import wkallil.card.creditCardManager.repositories.OwnerRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final OwnerRepository ownerRepository;
    private final EmailNotificationRepository emailNotificationRepository;
    private final MonthlyChargeService monthlyChargeService;

    public EmailService(JavaMailSender mailSender, OwnerRepository ownerRepository, EmailNotificationRepository emailNotificationRepository, MonthlyChargeService monthlyChargeService) {
        this.mailSender = mailSender;
        this.ownerRepository = ownerRepository;
        this.emailNotificationRepository = emailNotificationRepository;
        this.monthlyChargeService = monthlyChargeService;
    }

    @Transactional
    public void sendMonthlyNotifications(String yearMonth) {
        List<Owner> owners = ownerRepository.findAll();

        for (Owner owner : owners) {
            if (emailNotificationRepository.existsByOwnerIdAndReferenceMonth(owner.getId(), yearMonth)) {
                logger.info("Notification already sent to owner {} for month {}", owner.getEmail(), yearMonth);
                continue;
            }

            OwnerSummaryDTO summaryDTO = monthlyChargeService.getOwnerSummary(owner.getId(), yearMonth);

            if (summaryDTO.getCharges().isEmpty()) {
                logger.info("No charges for owner {} in month {}, skipping email.", owner.getEmail(), yearMonth);
                continue;
            }

            sendEmail(summaryDTO);
        }
    }

    private void sendEmail(OwnerSummaryDTO summaryDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(summaryDTO.getOwnerEmail());
        message.setSubject("Fatura do Cartão " + summaryDTO.getReferenceMonth() + " - Vencimento" + formatDate(summaryDTO.getInvoiceDate()));
        message.setText(buildEmailBody(summaryDTO));

        mailSender.send(message);
    }

    private String buildEmailBody(OwnerSummaryDTO summaryDTO) {
        StringBuilder body = new StringBuilder();
        body.append("Prezado(a) ").append(summaryDTO.getOwnerName()).append(",\n\n");
        body.append("Segue o resumo da sua fatura do cartão para ").append(summaryDTO.getReferenceMonth()).append(":\n\n");
        body.append("Data de Vencimento: ").append(formatDate(summaryDTO.getInvoiceDate())).append("\n\n");
        body.append("Detalhes dos Gastos:\n\n ");

        for (MonthlyChargeDTO chargeDTO : summaryDTO.getCharges()) {
            body.append("• ").append(chargeDTO.getExpenseName());
            if (chargeDTO.getRecurring()) {
                body.append(" (Recorrente)");
            } else {
                body.append(" (Parcela) ").append(chargeDTO.getInstallmentNumber())
                        .append("/").append(chargeDTO.getTotalInstallments()).append(")");
            }
            body.append(": R$ ").append(String.format("%.2f", chargeDTO.getAmount())).append("\n");
        }

        body.append("\n");
        body.append("═══════════════════════════════════════\n");
        body.append("TOTAL DA FATURA: R$ ").append(String.format("%.2f", summaryDTO.getTotalAmount())).append("\n");
        body.append("═══════════════════════════════════════\n\n");
        body.append("Atenciosamente,\nSistema de Gerenciamento de Cartões de Crédito");

        return body.toString();
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
