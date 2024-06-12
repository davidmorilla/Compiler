package com.grupo22.compiler;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import com.grupo22.compiler.model.Compiler;
import com.grupo22.compiler.util.Token;
/**
 * Unit test for simple App.
 */
public class AppTest 
{
	/**
	 * Rigorous Test :-)
	 */
	List<String> text_to_token(BufferedReader br){
		try {
			List<String> token_list = new ArrayList<String>();
			String str;
			while((str = br.readLine())!=null)
				token_list.add(str.trim());
			return token_list;
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Before
	public void beforeEach() {
	}
	@Test
	public void shouldAnswerWithTrue()
	{
		assertTrue( true );
	}

	@Test
	public void test1() throws FileNotFoundException {
		Compiler.CODE_FILE_NUMBER=1;
		Compiler.main(null);
		List<String> real_result = text_to_token(new BufferedReader(new FileReader(String.format(Compiler.TOKENS_OUTPUT_FORMAT, 1))));
		List<String> expected_result = new ArrayList<String>();
        expected_result.add("<LET,>");
        expected_result.add("<TABLEID,97>");
        expected_result.add("<INT,>");
        expected_result.add("<PYC,>");
        expected_result.add("<LET,>");
        expected_result.add("<TABLEID,98>");
        expected_result.add("<STR,>");
        expected_result.add("<PYC,>");
        expected_result.add("<LET,>");
        expected_result.add("<TABLEID,99>");
        expected_result.add("<BOOL,>");
        expected_result.add("<PYC,>");
        expected_result.add("<LET,>");
        expected_result.add("<TABLEID,-1034364087>");
        expected_result.add("<INT,>");
        expected_result.add("<PYC,>");
        expected_result.add("<PUT,>");
        expected_result.add("<CAD,\"Introduce el primer operando \">");
        expected_result.add("<PYC,>");
        expected_result.add("<PUT,>");
        expected_result.add("<CAD,\"Introduce el segundo operando\">");
        expected_result.add("<PYC,>");
        expected_result.add("<GET,>");
        expected_result.add("<TABLEID,98>");
        expected_result.add("<PYC,>");
        expected_result.add("<TABLEID,97>");
        expected_result.add("<ASIG,0>");
        expected_result.add("<CTE,7>");
        expected_result.add("<PYC,>");
        expected_result.add("<TABLEID,97>");
        expected_result.add("<ASIG,1>");
        expected_result.add("<TABLEID,97>");
        expected_result.add("<MOD,>");
        expected_result.add("<CTE,2>");
        expected_result.add("<PYC,>");
        expected_result.add("<FUNC,>");
        expected_result.add("<TABLEID,1662196504>");
        expected_result.add("<INT,>");
        expected_result.add("<PARENT,0>");
        expected_result.add("<INT,>");
        expected_result.add("<TABLEID,3392875>");
        expected_result.add("<COMA,>");
        expected_result.add("<INT,>");
        expected_result.add("<TABLEID,3392876>");
        expected_result.add("<PARENT,1>");
        expected_result.add("<LLAVE,0>");
        expected_result.add("<IF,>");
        expected_result.add("<PARENT,0>");
        expected_result.add("<TABLEID,99>");
        expected_result.add("<EQ,>");
        expected_result.add("<TRUE,>");
        expected_result.add("<PARENT,1>");
        expected_result.add("<LLAVE,0>");
        expected_result.add("<IF,>");
        expected_result.add("<PARENT,0>");
        expected_result.add("<FALSE,>");
        expected_result.add("<EQ,>");
        expected_result.add("<PARENT,0>");
        expected_result.add("<TABLEID,99>");
        expected_result.add("<AND,>");
        expected_result.add("<TABLEID,99>");
        expected_result.add("<PARENT,1>");
        expected_result.add("<PARENT,1>");
        expected_result.add("<LLAVE,0>");
        expected_result.add("<LLAVE,1>");
        expected_result.add("<ELSE,>");
        expected_result.add("<LLAVE,0>");
        expected_result.add("<PUT,>");
        expected_result.add("<TABLEID,98>");
        expected_result.add("<PYC,>");
        expected_result.add("<LLAVE,1>");
        expected_result.add("<LLAVE,1>");
        expected_result.add("<RET,>");
        expected_result.add("<TABLEID,97>");
        expected_result.add("<MOD,>");
        expected_result.add("<PARENT,0>");
        expected_result.add("<TABLEID,97>");
        expected_result.add("<MOD,>");
        expected_result.add("<CTE,5>");
        expected_result.add("<PARENT,1>");
        expected_result.add("<PYC,>");
        expected_result.add("<LLAVE,1>");
        Object[] realresult = real_result.toArray();
        Object[] expectedresult =  expected_result.toArray();
        org.junit.Assert.assertArrayEquals(expectedresult, realresult);

	}
}
