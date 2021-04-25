package com.db.trade;

import com.db.trade.controller.TradeTransmissionController;
import com.db.trade.domain.Trade;
import com.db.trade.service.StoreTradeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TradeTransmissionTest {


    @Autowired
    private TradeTransmissionController tradeController;

    @Autowired
    private StoreTradeService tradeService;

    @Test
    void testSuccessfulTradeValidateAndStore() {
        ResponseEntity responseEntity = tradeController.storeReadings(createTrade("T1",1,"CP-1", "B1",createLocalDate(2021,05,20),false));
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
        Assertions.assertNotNull(tradeService.getDetails("T1"));
    }

    @Test
    void testMaturityValiateFailedStoreTrade() throws Exception{
        Assertions.assertEquals(tradeService.isValid(createTrade("T2", 1,"CP-2","B1",createLocalDate(2021,05,20) , false)),false);

    }

    @Test
    void testVersionValidateFailedStoreTrade() {
        ResponseEntity responseEntity = tradeController.storeReadings(createTrade("T2",2,"CP-2", "B1",createLocalDate(2021,05,20), false));
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
        Trade trade =tradeService.getDetails("T2");
        Assertions.assertEquals(2,trade.getVersion());
        Assertions.assertEquals("B1",trade.getBookId());
        Assertions.assertEquals("CP-2",trade.getCounterPartyId());
        try {
            ResponseEntity responseEntity1 = tradeController.storeReadings(createTrade("T2",1,"CP-2", "B1",createLocalDate(2021,05,20), false));


        }catch (HttpClientErrorException.BadRequest e){
            System.out.println(e.getMessage());
        }
        Trade trade1 =tradeService.getDetails("T2");
        Assertions.assertEquals(2,trade1.getVersion());
        Assertions.assertEquals("B1",trade1.getBookId());
        Assertions.assertEquals("CP-2",trade.getCounterPartyId());
    }


    private Trade createTrade(String tradeId,int version,String counterPartyId, String bookId, LocalDate  maturityDate, Boolean flag){
        Trade trade = new Trade(tradeId,version,counterPartyId,bookId,maturityDate,LocalDate.now(),flag);
        return trade;
    }

    public static LocalDate createLocalDate(int year,int month, int day){
        LocalDate localDate = LocalDate.of(year,month,day);
        return localDate;
    }




}
