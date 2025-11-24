package wkallil.card.creditCardManager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.card.creditCardManager.dtos.MonthlyChargeDTO;
import wkallil.card.creditCardManager.dtos.OwnerSummaryDTO;
import wkallil.card.creditCardManager.exceptions.OwnerNotFoundException;
import wkallil.card.creditCardManager.models.EmailNotification;
import wkallil.card.creditCardManager.models.Owner;
import wkallil.card.creditCardManager.models.enums.EmailStatus;
import wkallil.card.creditCardManager.repositories.EmailNotificationRepository;
import wkallil.card.creditCardManager.repositories.OwnerRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import java.sql.Timestamp;
import java.util.Optional;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final OwnerRepository ownerRepository;
    private final EmailNotificationRepository emailNotificationRepository;
    private final MonthlyChargeService monthlyChargeService;
    private final JdbcTemplate jdbcTemplate;

    public EmailService(JavaMailSender mailSender, OwnerRepository ownerRepository,
                        EmailNotificationRepository emailNotificationRepository,
                        MonthlyChargeService monthlyChargeService, JdbcTemplate jdbcTemplate) {
        this.mailSender = mailSender;
        this.ownerRepository = ownerRepository;
        this.emailNotificationRepository = emailNotificationRepository;
        this.monthlyChargeService = monthlyChargeService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void sendMonthlyNotifications(String yearMonth) {
        List<Owner> owners = ownerRepository.findAll();

        for (Owner owner : owners) {
            LocalDate referenceMonthFirstDay = YearMonth.parse(yearMonth).atDay(1);

            OwnerSummaryDTO summaryDTO = monthlyChargeService.getOwnerSummary(owner.getId(), yearMonth);

            if (summaryDTO.getCharges().isEmpty()) {
                logger.info("No charges for owner {} in month {}, skipping email.", owner.getEmail(), yearMonth);
                continue;
            }

            try {
                sendEmail(summaryDTO);

                Optional<EmailNotification> existing = emailNotificationRepository.findByOwnerIdAndReferenceMonth(owner.getId(), referenceMonthFirstDay);
                if (existing.isPresent()) {
                    EmailNotification notification = existing.get();
                    notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                    notification.setTotalAmount(summaryDTO.getTotalAmount());
                    notification.setSentAt(LocalDateTime.now());
                    notification.setStatus(EmailStatus.SENT);
                    saveEmailNotification(notification);
                } else {
                    EmailNotification notification = new EmailNotification();
                    notification.setOwner(owner);
                    notification.setReferenceMonth(referenceMonthFirstDay);
                    notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                    notification.setTotalAmount(summaryDTO.getTotalAmount());
                    notification.setSentAt(LocalDateTime.now());
                    notification.setStatus(EmailStatus.SENT);
                    saveEmailNotification(notification);
                }
            } catch (MailException ex) {
                logger.error("Failed to send email to owner {} for month {}: {}", owner.getEmail(), yearMonth, ex.getMessage());
                Optional<EmailNotification> existing = emailNotificationRepository.findByOwnerIdAndReferenceMonth(owner.getId(), referenceMonthFirstDay);
                if (existing.isPresent()) {
                    EmailNotification notification = existing.get();
                    notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                    notification.setTotalAmount(summaryDTO.getTotalAmount());
                    notification.setSentAt(LocalDateTime.now());
                    notification.setStatus(EmailStatus.FAILED);
                    saveEmailNotification(notification);
                } else {
                    EmailNotification notification = new EmailNotification();
                    notification.setOwner(owner);
                    notification.setReferenceMonth(referenceMonthFirstDay);
                    notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                    notification.setTotalAmount(summaryDTO.getTotalAmount());
                    notification.setSentAt(LocalDateTime.now());
                    notification.setStatus(EmailStatus.FAILED);
                    saveEmailNotification(notification);
                }
            }
        }
    }

    private void sendEmail(OwnerSummaryDTO summaryDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(summaryDTO.getOwnerEmail());
        message.setSubject("Fatura do Cartão " + summaryDTO.getReferenceMonth() + " - Vencimento " + formatDate(summaryDTO.getInvoiceDate()));
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
                        .append("/").append(chargeDTO.getTotalInstallments());
            }
            body.append(": R$ ").append(String.format("%.2f", chargeDTO.getAmount())).append("\n");
        }

        body.append("\n");
        body.append("═══════════════════════════════════════\n");
        body.append("TOTAL DA FATURA: R$ ").append(String.format("%.2f", summaryDTO.getTotalAmount())).append("\n");
        body.append("══════════════════��════════════════════\n\n");
        body.append("Atenciosamente,\nSistema de Gerenciamento de Cartões de Crédito");

        return body.toString();
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    @Transactional
    public void sendMonthlyNotificationForOwner(Long ownerId, String yearMonth) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found"));

        LocalDate referenceMonthFirstDay = YearMonth.parse(yearMonth).atDay(1);

        OwnerSummaryDTO summaryDTO = monthlyChargeService.getOwnerSummary(ownerId, yearMonth);

        if (summaryDTO.getCharges().isEmpty()) {
            logger.info("No charges for owner {} in month {}, skipping email.", owner.getEmail(), yearMonth);
            return;
        }

        try {
            sendEmail(summaryDTO);

            Optional<EmailNotification> existing = emailNotificationRepository.findByOwnerIdAndReferenceMonth(ownerId, referenceMonthFirstDay);
            if (existing.isPresent()) {
                EmailNotification notification = existing.get();
                notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                notification.setTotalAmount(summaryDTO.getTotalAmount());
                notification.setSentAt(LocalDateTime.now());
                notification.setStatus(EmailStatus.SENT);
                saveEmailNotification(notification);
            } else {
                EmailNotification notification = new EmailNotification();
                notification.setOwner(owner);
                notification.setReferenceMonth(referenceMonthFirstDay);
                notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                notification.setTotalAmount(summaryDTO.getTotalAmount());
                notification.setSentAt(LocalDateTime.now());
                notification.setStatus(EmailStatus.SENT);
                saveEmailNotification(notification);
            }
        } catch (MailException ex) {
            logger.error("Failed to send email to owner {} for month {}: {}", owner.getEmail(), yearMonth, ex.getMessage());
            Optional<EmailNotification> existing = emailNotificationRepository.findByOwnerIdAndReferenceMonth(ownerId, referenceMonthFirstDay);
            if (existing.isPresent()) {
                EmailNotification notification = existing.get();
                notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                notification.setTotalAmount(summaryDTO.getTotalAmount());
                notification.setSentAt(LocalDateTime.now());
                notification.setStatus(EmailStatus.FAILED);
                saveEmailNotification(notification);
            } else {
                EmailNotification notification = new EmailNotification();
                notification.setOwner(owner);
                notification.setReferenceMonth(referenceMonthFirstDay);
                notification.setInvoiceDate(summaryDTO.getInvoiceDate());
                notification.setTotalAmount(summaryDTO.getTotalAmount());
                notification.setSentAt(LocalDateTime.now());
                notification.setStatus(EmailStatus.FAILED);
                saveEmailNotification(notification);
            }
        }
    }

    private boolean columnExists(String columnName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = current_schema() AND table_name = 'email_notifications' AND column_name = ?",
                    Integer.class, columnName);
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            logger.warn("Could not query information_schema to check column existence: {}", ex.getMessage());
            return false;
        }
    }

    private void saveEmailNotification(EmailNotification notification) {
        boolean hasSentAt = columnExists("sent_at");
        boolean hasStatus = columnExists("status");

        if (hasSentAt && hasStatus) {
            emailNotificationRepository.save(notification);
            return;
        }

        try {
            if (!hasSentAt) {
                jdbcTemplate.execute("ALTER TABLE email_notifications ADD COLUMN IF NOT EXISTS sent_at TIMESTAMP");
                logger.info("Added missing column sent_at to email_notifications");
            }
            if (!hasStatus) {
                jdbcTemplate.execute("ALTER TABLE email_notifications ADD COLUMN IF NOT EXISTS status VARCHAR(20)");
                logger.info("Added missing column status to email_notifications");
            }
        } catch (DataAccessException ex) {
            logger.warn("Could not alter email_notifications table to add missing columns: {}", ex.getMessage());
        }

        hasSentAt = columnExists("sent_at");
        hasStatus = columnExists("status");

        if (hasSentAt && hasStatus) {
            try {
                emailNotificationRepository.save(notification);
                return;
            } catch (DataAccessException ex) {
                logger.error("Failed to save EmailNotification via repository after adding columns: {}", ex.getMessage());
            }
        }

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("INSERT INTO email_notifications (invoice_date, owner_id, reference_month, total_amount");
        params.add(Date.valueOf(notification.getInvoiceDate()));
        params.add(notification.getOwner().getId());
        params.add(Date.valueOf(notification.getReferenceMonth()));
        params.add(notification.getTotalAmount());

        if (hasSentAt) {
            sql.append(", sent_at");
        }
        if (hasStatus) {
            sql.append(", status");
        }
        sql.append(") VALUES (?,?,?,?");

        if (hasSentAt) {
            sql.append(",?");
            params.add(Timestamp.valueOf(notification.getSentAt()));
        }
        if (hasStatus) {
            sql.append(",?");
            params.add(notification.getStatus() != null ? notification.getStatus().name() : null);
        }
        sql.append(")");

        try {
            jdbcTemplate.update(sql.toString(), params.toArray());
        } catch (DataAccessException ex) {
            logger.error("Failed to persist EmailNotification via fallback insert: {}", ex.getMessage());
        }
    }
}
