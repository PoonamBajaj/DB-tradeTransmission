package com.db.trade.service;

import com.db.trade.domain.Trade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class StoreTradeService {

    private final Map<String, Trade> tradeDetailsByTradeId;

    public StoreTradeService(Map<String, Trade> tradeDetailsByTradeId) {
        this.tradeDetailsByTradeId = tradeDetailsByTradeId;
    }

    public Trade getDetails(String tradeId) {
        return tradeDetailsByTradeId.get(tradeId);
    }

    public void storeDetails(Trade trade) {

        String tradeId = trade.getTradeId();

        if (!tradeDetailsByTradeId.containsKey(tradeId)) {
            trade.setCreatedDate(LocalDate.now());
            tradeDetailsByTradeId.put(tradeId, trade);
        }
//        else{
//            trade.setVersion(trade.getVersion()+1);
//            tradeDetailsByTradeId.put(tradeId, trade);
//        }
    }



    public boolean isValid(Trade trade){
        if(validateMaturityDate(trade)) {
            Optional<Trade> existingTrade = Optional.ofNullable(getDetails(trade.getTradeId()));
            if (existingTrade.isPresent()) {
                return validateVersion(trade, existingTrade.get());
            }else{
                return true;
            }
        }
        return false;
    }

    private boolean validateVersion(Trade trade,Trade oldTrade) {
        if(trade.getVersion() >= oldTrade.getVersion()){
            return true;
        }
        return false;
    }

    private boolean validateMaturityDate(Trade trade){
        return trade.getMaturityDate().isBefore(LocalDate.now())  ? false:true;
    }

    public void updateExpiryFlagOfTrade() {
        tradeDetailsByTradeId.keySet().forEach(tradeId -> {
            Trade trade = getDetails(tradeId);
            if (!validateMaturityDate(trade)) {
                trade.setExpiredFlag(true);
            }
        });
    }
}
