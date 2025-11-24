-- V6: Ensure sent_at TIMESTAMP and status column exist on email_notifications
-- Use ALTER TABLE IF NOT EXISTS so this migration is idempotent if run multiple times.

ALTER TABLE email_notifications
  ADD COLUMN IF NOT EXISTS sent_at TIMESTAMP;

ALTER TABLE email_notifications
  ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

