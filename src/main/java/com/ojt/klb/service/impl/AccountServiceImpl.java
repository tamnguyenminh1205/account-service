package com.ojt.klb.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.klb.dto.*;
import com.ojt.klb.model.Account;
import com.ojt.klb.repository.AccountRepository;
import com.ojt.klb.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;

    private final KafkaTemplate<String, ChangeStatusDto> kafkaTemplate;

    private final RestTemplate restTemplate;

    private static final String TOPIC = "account-status-topics";
    private static final String CUSTOMER_SERVICE_URL = "http://localhost:8082/api/v1/customer/";

    public AccountServiceImpl(AccountRepository accountRepository, KafkaTemplate<String, ChangeStatusDto> kafkaTemplate, RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }


    @Override
    public Optional<AccountDto> getAccountById(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            if (account.getStatus() == Account.Status.suspended) {
                logger.warn("Account is suspended for id: {}", id);
                return Optional.empty();
            }


            String url = CUSTOMER_SERVICE_URL + account.getId();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();
                AccountDto customerData = parseCustomerData(responseBody);

                customerData.setAccountName(account.getAccountName());
                customerData.setAccountNumber(account.getAccountNumber());
                customerData.setBalance(account.getBalance());
                customerData.setStatus(account.getStatus());
                customerData.setOpeningDate(account.getCreatedAt());

                return Optional.of(customerData);
            } else {
                logger.error("Failed to fetch customer data from external service for account id: {}", account.getId());
                return Optional.empty();
            }
        }
        logger.warn("Account not found for id: {}", id);
        return Optional.empty();
    }

    @Override
    public void changeStatusAccount(Long id, ChangeStatusDto changeStatusDto) {
        Account account = accountRepository.findById(id).get();
        account.setStatus(Account.Status.valueOf(changeStatusDto.getStatus()));
        accountRepository.save(account);

        changeStatusDto.setAccountId(account.getId());
        changeStatusDto.setStatus(String.valueOf(account.getStatus()));
        kafkaTemplate.send(TOPIC, changeStatusDto);
        logger.info("Send data {} and {}", changeStatusDto.getAccountId(), changeStatusDto.getStatus());
        logger.info("Sent customer information to Kafka topic: {}", TOPIC);
    }


    @Override
    public Optional<Long> getAccountIdByAccountNumber(Long accountNumber) {
        Optional<Account> accountOptional = accountRepository.findAccountByAccountNumber(accountNumber);
        if (accountOptional.isPresent()) {
            return Optional.of(accountOptional.get().getId());
        } else {
            logger.warn("Account not found for account number: {}", accountNumber);
            return Optional.empty();
        }
    }

    @Override
    public String getFullNameByAccountId(Long accountId) {

        String url = CUSTOMER_SERVICE_URL + accountId;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            FindNameByAccountDto findNameByAccountDto = parseCustomerGetName(responseBody);
            assert findNameByAccountDto != null;
            return findNameByAccountDto.getFullName();
        } else {
            logger.error("Failed to fetch customer data from external service for accountId: {}", accountId);
            return null;
        }
    }


    private AccountDto parseCustomerData(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");

            AccountDto accountDto = new AccountDto();
            accountDto.setFullName(dataNode.path("fullName").asText());
            accountDto.setDateOfBirth(LocalDate.parse(dataNode.path("dateOfBirth").asText()));
            accountDto.setGender(dataNode.path("gender").asText());
            accountDto.setEmail(dataNode.path("email").asText());
            accountDto.setPhoneNumber(dataNode.path("phoneNumber").asText());
            accountDto.setPermanentAddress(dataNode.path("permanentAddress").asText());
            accountDto.setCurrentAddress(dataNode.path("currentAddress").asText());

            return accountDto;
        } catch (Exception e) {
            logger.error("Error parsing customer data: ", e);
            return new AccountDto();
        }
    }

    private FindNameByAccountDto parseCustomerGetName(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");

            FindNameByAccountDto findNameByAccountDto = new FindNameByAccountDto();
            findNameByAccountDto.setFullName(dataNode.path("fullName").asText());

            return findNameByAccountDto;
        }catch (Exception e) {
            logger.error("Error parsing customer data: ", e);
            return null;
        }
    }
}
