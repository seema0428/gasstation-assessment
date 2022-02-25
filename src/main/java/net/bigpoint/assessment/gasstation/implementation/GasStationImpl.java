package net.bigpoint.assessment.gasstation.implementation;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationImpl implements GasStation {

	private CopyOnWriteArrayList<GasPump> pumpList = new CopyOnWriteArrayList<GasPump>();
    
    private ConcurrentMap<GasType, Double> pricesMap = new ConcurrentHashMap<>();
       
    private AtomicLong revenue = new AtomicLong(0);
    
    private AtomicInteger numberOfSales = new AtomicInteger(0);
    
    private AtomicInteger numberOfCancellationsForNoGas = new AtomicInteger(0);
    
    private AtomicInteger numberOfCancellationsForTooExpensiveGas = new AtomicInteger(0);
    
	@Override
	public void addGasPump(GasPump pump) {
		this.pumpList.add(pump);
		
	}

	@Override
	public Collection<GasPump> getGasPumps() {
		return this.pumpList;
	}

	@Override
	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
			throws NotEnoughGasException, GasTooExpensiveException {
		double currentPricePerLiter = pricesMap.get(type);
		
		var price = new Object(){ double priceToPay = 0; };
			
        if (currentPricePerLiter > maxPricePerLiter) {
        	numberOfCancellationsForTooExpensiveGas.incrementAndGet();
            throw new GasTooExpensiveException();
        }
        
        pumpList.forEach(pump->{
        	if (pump.getGasType().equals(type)) {
        		//lock the gas pump so that one thread can access at a time.
        		synchronized(pump) {
                    if (pump.getRemainingAmount() >= amountInLiters) {
                            pump.pumpGas(amountInLiters);
                            price.priceToPay = amountInLiters * currentPricePerLiter;
                            revenue.addAndGet(Double.valueOf(price.priceToPay).longValue());
                            numberOfSales.incrementAndGet();
                            return;
                    	}
        		}
        	}
        	
        });
        if (price.priceToPay == 0 && amountInLiters > 0){
        	numberOfCancellationsForNoGas.incrementAndGet();
            throw new NotEnoughGasException();        
        }
        return price.priceToPay;
	}

	@Override
	public double getRevenue() {
		return  this.revenue.get();
	}

	@Override
	public int getNumberOfSales() {
		return  this.numberOfSales.get();
	}

	@Override
	public int getNumberOfCancellationsNoGas() {
		return this.numberOfCancellationsForNoGas.get();
	}

	@Override
	public int getNumberOfCancellationsTooExpensive() {
		return this.numberOfCancellationsForTooExpensiveGas.get();
	}

	@Override
	public double getPrice(GasType type) {
		return this.pricesMap.get(type);
	}

	@Override
	public void setPrice(GasType type, double price) {
		this.pricesMap.put(type, price);
		
	}

}
