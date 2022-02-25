package net.bigpoint.assessment.implementation.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import net.bigpoint.assessment.gasstation.implementation.GasStationImpl;

@TestMethodOrder(OrderAnnotation.class)
class GasStationImplTest {

	private GasStationImpl  gasStationImplObj;
   
    @BeforeEach
    public void init() {
       
    	gasStationImplObj = new GasStationImpl();
    	gasStationImplObj.setPrice(GasType.REGULAR, 3);
    	gasStationImplObj.setPrice(GasType.SUPER, 5);
    	gasStationImplObj.setPrice(GasType.DIESEL,2);
       
        GasPump pumpDiesel = new GasPump(GasType.DIESEL, 10);
        GasPump pumpRegular = new GasPump(GasType.REGULAR, 15);
        GasPump pumpSuper = new GasPump(GasType.SUPER, 20);
       
        gasStationImplObj.addGasPump(pumpDiesel);
        gasStationImplObj.addGasPump(pumpRegular);
        gasStationImplObj.addGasPump(pumpSuper);
    }
    
    @Test
    @Order(1)
    public void test_Price() {
        gasStationImplObj.setPrice(GasType.REGULAR, 1);
        gasStationImplObj.setPrice(GasType.SUPER, 2);
        gasStationImplObj.setPrice(GasType.DIESEL, 1.5);
        Assertions.assertTrue(gasStationImplObj.getPrice(GasType.REGULAR) == 1 &&
        		gasStationImplObj.getPrice(GasType.SUPER) == 2 &&
        				gasStationImplObj.getPrice(GasType.DIESEL) == 1.5);
    }
    
    @Test
    @Order(2)
    public void test_BuyGas() throws Exception {
        double pricePerLiter = gasStationImplObj.getPrice(GasType.REGULAR);
        double liters = 2;
        double priceToPay = 0;
        double estimatedPrice = liters * pricePerLiter;
        priceToPay = gasStationImplObj.buyGas(GasType.REGULAR, liters, 4); 
        Assertions.assertEquals(priceToPay, estimatedPrice);
    }
   
    @Test
    @Order(3)
    public void test_GasPumps() {
        GasPump pumpDiesel = new GasPump(GasType.DIESEL, 1000);
        gasStationImplObj.addGasPump(pumpDiesel);
        Assertions.assertEquals(4,gasStationImplObj.getGasPumps().size());
    }
    
    @Test
    @Order(4)
    public void test_TooExpensive_GasException() throws Exception {
    	double amountInLitres = 50;
        Assertions.assertThrows(GasTooExpensiveException.class, () -> gasStationImplObj.buyGas(GasType.DIESEL,amountInLitres, 1));
    }
    
    @Test
    @Order(5)
    public void test_NotEnough_GasException() throws Exception {
    	double amountInLitres = 100;
        Assertions.assertThrows(NotEnoughGasException.class, () -> gasStationImplObj.buyGas(GasType.SUPER,amountInLitres, 6));
    }
}
