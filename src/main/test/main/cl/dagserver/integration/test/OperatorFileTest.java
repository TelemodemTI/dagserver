package main.cl.dagserver.integration.test;

import org.testng.annotations.Test;

import main.cl.dagserver.integration.test.core.BaseOperatorTest;

public class OperatorFileTest extends BaseOperatorTest {

    @Test(priority = 1)
    public void readFileWTitleWoRDelimiterOperator() {
        //lectura de archivo existe, con titulos, sin delimitador
    }
    @Test(priority = 2)
    public void readFileWoTitleWoRDelimiterOperator() {
        //lectura de archivo existe, sin titulos, sin delimitador
    }
    @Test(priority = 3)
    public void readFileWTitleWRDelimiterOperator() {
        //lectura de archivo existe, con titulos, con delimitador
    }
    @Test(priority = 4)
    public void readFileWoTitleWRDelimiterOperator() {
        //lectura de archivo existe, sin titulos, con delimitador
    }
    @Test(priority = 5)
    public void readFileWoFileOperator() {
        //lectura de archivo no existe
    }
    @Test(priority = 6)
    public void writeFileWTitleWoRDelimiterOperator() {
        //escritura de archivo existe, con titulos, sin delimitador
    }
    @Test(priority = 7)
    public void writeFileWoTitleWoRDelimiterOperator() {
        //escritura de archivo existe, sin titulos, sin delimitador
    }
    @Test(priority = 8)
    public void writeFileWTitleWRDelimiterOperator() {
        //escritura de archivo existe, con titulos, con delimitador
    }
    @Test(priority = 9)
    public void writeFileWoTitleWRDelimiterOperator() {
        //escritura de archivo existe, sin titulos, con delimitador
    }
    
}
