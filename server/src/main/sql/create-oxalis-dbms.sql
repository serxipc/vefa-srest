/**
drop table if exists event;
drop table if exists cleansing_job;
drop table if exists billing_period;
drop table if exists billing_scheme;
drop table if exists outbound_message_queue_error;
drop table if exists outbound_message_queue;
drop table if exists message;
drop table if exists account_receiver;
drop table if exists account_role;
drop table if exists account;
drop table if exists customer;
*/

/** To create TEST database : Create a new database called oxalis_test and run this script */
/** To create PROD database : Create a new database called oxalis and run this script */

/** Customer paying for the connection */
CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT 'Name of paying customer',
  `originator_id` int(11) DEFAULT NULL COMMENT 'SR originator ID for billing purposes',
  `created_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `address1` varchar(254) DEFAULT NULL,
  `address2` varchar(254) DEFAULT NULL,
  `zip` varchar(8) DEFAULT NULL,
  `city` varchar(64) DEFAULT NULL,
  `country` varchar(64) DEFAULT NULL,
  `contact_person` varchar(64) DEFAULT NULL COMMENT 'Contact person name',
  `contact_email` varchar(64) DEFAULT NULL,
  `contact_phone` varchar(64) DEFAULT NULL,
  `org_no` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8 COMMENT='Customer paying for accounts';

/** Each customer can have multiple accounts, details here is used for JAAS authentication etc */
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `customer_id` int(11) NOT NULL COMMENT 'FK to customer',
  `name` varchar(128) NOT NULL COMMENT 'Name of account',
  `username` varchar(128) NOT NULL COMMENT 'Username used for logging in',
  `password` varchar(64) DEFAULT NULL COMMENT 'Password used for Basic Authentication',
  `created_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `validate_upload` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Should invoices be validated',
  `send_notification` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Should email notifications be sent',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_account_2_customer` (`customer_id`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8 COMMENT='Client account';

/** The JAAS security roles for the accounts */
CREATE TABLE `account_role` (
  `username` varchar(128) NOT NULL,
  `role_name` enum('client','admin') NOT NULL DEFAULT 'client',
  PRIMARY KEY (`username`,`role_name`),
  CONSTRAINT `account_role_ibfk_1` FOREIGN KEY (`username`) REFERENCES `account` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Account roles';

/** Which PEPPOL participantid belong to which account */
CREATE TABLE `account_receiver` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `participant_id` varchar(32) DEFAULT NULL COMMENT 'PEPPOL prefix and orgno',
  `account_id` int(11) NOT NULL COMMENT 'FK to account',
  PRIMARY KEY (`id`),
  UNIQUE KEY `participant_id` (`participant_id`),
  KEY `fk_acc_rec_2_account` (`account_id`),
  CONSTRAINT `account_receiver_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8 COMMENT='Account receivers';


/** Holds the message metadata and payloads of all messages sent or received by the access point */
CREATE TABLE `message` (
  `msg_no` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL COMMENT 'account sending or receiving a xmlMessage',
  `direction` enum('IN','OUT') NOT NULL,
  `received` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'TS when received by AP',
  `delivered` datetime DEFAULT NULL COMMENT 'TS when delivered to destination',
  `sender` varchar(32) NOT NULL COMMENT 'PPID of sender',
  `receiver` varchar(32) NOT NULL COMMENT 'PPID of receiver',
  `channel` varchar(128) NOT NULL,
  `message_uuid` varchar(64) DEFAULT NULL COMMENT 'UUID assigned by AP when transmitted',
  `document_id` varchar(256) NOT NULL COMMENT 'document type id',
  `process_id` varchar(128) DEFAULT NULL COMMENT 'Process type id',
  `remote_host` varchar(128) DEFAULT NULL COMMENT 'Remote IP address',
  `ap_name` varchar(128) DEFAULT NULL,
  `xml_message` longtext,
  `invoice_no` varchar(255) DEFAULT NULL COMMENT 'Invoice no (cbc:id',
  PRIMARY KEY (`msg_no`),
  KEY `fk_message_2_account` (`account_id`),
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11930 DEFAULT CHARSET=utf8 COMMENT='In and outbound PEPPOL messages';

/** The oubound queue implementation */
CREATE TABLE `outbound_message_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `msg_no` int(11) DEFAULT NULL COMMENT 'FK to message table',
  `state` enum('QUEUED','IN_PROGRESS','EXTERNAL','OK','AOD','CBU','CBO') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_omk_2_msg` (`msg_no`),
  CONSTRAINT `outbound_message_queue_ibfk_1` FOREIGN KEY (`msg_no`) REFERENCES `message` (`msg_no`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4978 DEFAULT CHARSET=utf8 COMMENT='Outbound message queue';

/** The oubound error queue implementation */
CREATE TABLE `outbound_message_queue_error` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `queue_id` int(11) NOT NULL COMMENT 'FK to queue table',
  `message` varchar(256) DEFAULT NULL,
  `details` text,
  `stacktrace` text,
  `create_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_omq_2_omq` (`queue_id`),
  CONSTRAINT `outbound_message_queue_error_ibfk_1` FOREIGN KEY (`queue_id`) REFERENCES `outbound_message_queue` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8 COMMENT='Outbound message queue error';


/** The different billing schemes in the system */
CREATE TABLE `billing_scheme` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `code` varchar(10) DEFAULT NULL COMMENT 'billing scheme code',
  `price_invoice_send` decimal(12,2) DEFAULT NULL COMMENT 'price for sending one invoice',
  `price_invoice_receive` decimal(12,2) DEFAULT NULL COMMENT 'price for receiving one invoice',
  `price_billing_cycle` decimal(12,2) DEFAULT NULL,
  `start_price` decimal(12,2) DEFAULT NULL,
  `billing_cycle` enum('YEARLY','QUARTERLY','MONTHLY') NOT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='Billing scheme';

/** Links a customer and a period to a specific billing scheme */
CREATE TABLE `billing_period` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `customer_id` int(11) NOT NULL COMMENT 'FK to customer',
  `billing_scheme_id` int(11) NOT NULL COMMENT 'FK to billing scheme',
  `from_date` date NOT NULL COMMENT 'Billing period start date',
  `to_date` date DEFAULT NULL COMMENT 'Billing period end date',
  PRIMARY KEY (`id`),
  KEY `fk_pb_2_cust` (`customer_id`),
  KEY `fk_pb_2_bs` (`billing_scheme_id`),
  CONSTRAINT `billing_period_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `billing_period_ibfk_2` FOREIGN KEY (`billing_scheme_id`) REFERENCES `billing_scheme` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='Billing scheme period';

/** Create the cleansing table */
CREATE TABLE `cleansing_job` (
  `id` varchar(48) NOT NULL COMMENT 'Primary Key',
  `account_id` int(11) NOT NULL COMMENT 'Account who owns job',
  `progress` int(11) NOT NULL COMMENT 'Progress in percent',
  `ts_added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when job added',
  `ts_started` timestamp NULL DEFAULT NULL COMMENT 'Timestamp when job started',
  `ts_finished` timestamp NULL DEFAULT NULL COMMENT 'Timestamp when job finished',
  `xml_request` mediumtext COMMENT 'Request max 16 MiB',
  `xml_result` mediumtext COMMENT 'Result max 16 MiB',
  PRIMARY KEY (`id`),
  KEY `fk_cleansing_job_2_account` (`account_id`),
  CONSTRAINT `cleansing_job_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cleansing Job';

/** Create the event table */
CREATE TABLE `event` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `account_id` int(11) NOT NULL COMMENT 'Account who receives this event',
  `msg_no` int(11) DEFAULT NULL COMMENT 'Reference to a message in our system',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when event was added to this table',
  `delivered` timestamp NULL DEFAULT NULL COMMENT 'Timestamp when event was marked as read',
  `system` varchar(256) NOT NULL COMMENT 'Name of the system producing the event',
  `message` varchar(2048) NOT NULL COMMENT 'Short message explaining what happened',
  `type` enum('INFO','WARNING','ERROR','DELIVERED') NOT NULL COMMENT 'What type of event is this',
  `source_name` varchar(256) NOT NULL COMMENT 'Name of the source where event was received from',
  `source_timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'When the actual event occurred in external system',
  `source_message` mediumtext COMMENT 'Detailed message about what happened, max 16 MiB',
  `source_payload` mediumtext COMMENT 'Complete event payload when appropriate max 16 MiB',
  `source_reference` varchar(2048) DEFAULT NULL COMMENT 'Any reference mentioned in the event',
  PRIMARY KEY (`id`),
  KEY `fk_event_2_message` (`msg_no`),
  KEY `fk_event_2_account` (`account_id`),
  CONSTRAINT `event_ibfk_1` FOREIGN KEY (`msg_no`) REFERENCES `message` (`msg_no`) ON DELETE CASCADE,
  CONSTRAINT `event_ibfk_2` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5317 DEFAULT CHARSET=utf8 COMMENT='Event Storage';


/* ============= INSERT ONE DEFAULT CUSTOMER WITH A SINGE ACCOUNT  =============== */

insert into customer (id, name, originator_id, org_no) values (1, 'SendRegning AS', 279, "976098897");
insert into account (id, customer_id, name, username, password) values (1, 1, 'SendRegning User', 'sr', 'ringo');
insert into account_role values ('sr', 'client');
insert into account_receiver (participant_id, account_id) values ('9908:976098897', 1);
insert into billing_scheme (id, code, price_invoice_send, price_invoice_receive, price_billing_cycle, start_price, billing_cycle, description) values (1, 'DEFAULT', 0.00, 0.00, 9100.00, 3000.00, 'YEARLY', 'Default pricing model');
