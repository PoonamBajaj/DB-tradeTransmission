package com.db.trade.controller;


import com.db.trade.domain.Trade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.trade.service.StoreTradeService;


@RestController
@RequestMapping("/trasmitTrade")
public class TradeTransmissionController {

    private final StoreTradeService storeTradeService;

    public TradeTransmissionController (StoreTradeService storeTradeService) {
        this.storeTradeService = storeTradeService;
    }

    @PostMapping("/trade")
    public ResponseEntity storeReadings(@RequestBody Trade trade) {
        if (!storeTradeService.isValid(trade)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        storeTradeService.updateExpiryFlagOfTrade();
        storeTradeService.storeDetails(trade);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/read/{tradeId}")
    public ResponseEntity readTradeInfo(@PathVariable String tradeId) {
        Trade tradeInfo = storeTradeService.getDetails(tradeId);
        return tradeInfo!=null
                ? ResponseEntity.ok(tradeInfo)
                : ResponseEntity.notFound().build();
    }
}
