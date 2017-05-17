package vn.com.tma.idlesmart;

import org.junit.Test;

import vn.com.tma.idlesmart.Utils.IntegerParser;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ntmhanh on 5/8/2017.
 */

public class IntegerParserUnitTest {
    @Test
    public void toInteger_InputStringOfNumber_ReturnThisNumber(){
        //Arrange
        IntegerParser integerParser = new IntegerParser();
        String textNum = "20";
        //Action
        int num = integerParser.toInteger(textNum);
        //Assert
        assertEquals(20, num);
    }
    @Test
    public void toInteger_InputStringIsNotNumber_Return0(){
        //Arrange
        IntegerParser integerParser = new IntegerParser();
        String textNum = "A20";
        //Action
        int num = integerParser.toInteger(textNum);
        //Assert
        assertEquals(0, num);
    }

}
